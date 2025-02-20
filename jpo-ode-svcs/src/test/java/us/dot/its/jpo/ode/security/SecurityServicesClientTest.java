package us.dot.its.jpo.ode.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import us.dot.its.jpo.ode.config.SerializationConfig;
import us.dot.its.jpo.ode.http.WebClientConfig;
import us.dot.its.jpo.ode.security.models.SignatureRequestModel;
import us.dot.its.jpo.ode.security.models.SignatureResultModel;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {
        SerializationConfig.class,
        SecurityServicesClient.class,
        SecurityServicesProperties.class,
        WebClientConfig.class,
    }
)
@EnableConfigurationProperties
class SecurityServicesClientTest {

  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private SecurityServicesClient securityServicesClient;
  @Autowired
  private SecurityServicesProperties securityServicesProperties;

  private MockRestServiceServer mockServer;
  private final Clock clock = Clock.fixed(Instant.parse("2024-12-26T23:53:21.120Z"), ZoneId.of("UTC"));
  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void beforeEach() {
    mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  void testSignMessage_WithMockServerSuccessfulResponse() throws JsonProcessingException {
    // Arrange
    String message = "TestMessage";
    SignatureResultModel expectedResult = new SignatureResultModel();
    expectedResult.setMessageSigned("signed message<%s>".formatted(message));
    expectedResult.setMessageExpiry(clock.instant().getEpochSecond());

    SignatureRequestModel signatureRequestModel = new SignatureRequestModel();
    signatureRequestModel.setMessage(message);
    var expiryTimeInSeconds = (int) clock.instant().plusSeconds(3600).getEpochSecond();
    signatureRequestModel.setSigValidityOverride(expiryTimeInSeconds);

    mockServer.expect(ExpectedCount.once(), requestTo(securityServicesProperties.getSignatureEndpoint()))
        .andRespond(withSuccess(objectMapper.writeValueAsString(expectedResult), MediaType.APPLICATION_JSON));

    SignatureResultModel result = securityServicesClient.signMessage(message, expiryTimeInSeconds);
    assertEquals(expectedResult, result);
  }

  @Test
  void testSignMessage_WithNullResponse() {
    // Arrange
    String message = "NullResponseTest";
    var expiryTimeInSeconds = (int) clock.instant().plusSeconds(3600).getEpochSecond();

    mockServer.expect(ExpectedCount.once(), requestTo(securityServicesProperties.getSignatureEndpoint()))
        .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

    // Act
    SignatureResultModel result = securityServicesClient.signMessage(message, expiryTimeInSeconds);

    // Assert
    assertNull(result);
  }

  @Test
  void testSignMessage_WithErrorResponse() {
    String message = "ErrorResponseTest";
    var expiryTimeInSeconds = (int) clock.instant().plusSeconds(3600).getEpochSecond();

    mockServer.expect(ExpectedCount.once(), requestTo(securityServicesProperties.getSignatureEndpoint()))
        .andRespond(withServerError());

    assertThrows(RestClientException.class, () -> securityServicesClient.signMessage(message, expiryTimeInSeconds));
  }
}