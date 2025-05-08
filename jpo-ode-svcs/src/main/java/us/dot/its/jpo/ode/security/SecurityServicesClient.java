package us.dot.its.jpo.ode.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import us.dot.its.jpo.ode.security.models.SignatureRequestModel;
import us.dot.its.jpo.ode.security.models.SignatureResultModel;


/**
 * A client service that interacts with the security services module to handle
 * cryptographic operations requiring signed messages. This service facilitates
 * communication with the security services module using REST APIs.
 */
@Slf4j
@Service
public class SecurityServicesClient {

  private final String signatureUri;
  private final RestTemplate restTemplate;

  public SecurityServicesClient(RestTemplate restTemplate, SecurityServicesProperties securityServicesProperties) {
    this.restTemplate = restTemplate;
    this.signatureUri = securityServicesProperties.getSignatureEndpoint();
  }

  /**
   * Signs a given message using the security services module.
   *
   * @param message             the message to be signed
   * @param sigValidityOverride an optional integer value to override the default validity period of the signature
   *
   * @return the result of the signing operation including the signed message and its expiration details
   *
   * @throws RestClientException if there is an error during communication with the security services module
   */
  public SignatureResultModel signMessage(String message, int sigValidityOverride) throws RestClientException {
    log.info("Sending data to security services module at {} with validity override {} to be signed", signatureUri, sigValidityOverride);

    var requestBody = new SignatureRequestModel();
    requestBody.setMessage(message);
    requestBody.setSigValidityOverride(sigValidityOverride);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<SignatureRequestModel> entity = new HttpEntity<>(requestBody, headers);

    log.debug("Data to be signed: {}", entity);
    try {
      var respEntity = restTemplate.postForEntity(signatureUri, entity, SignatureResultModel.class);
      log.debug("Security services module response: {}", respEntity);

      return respEntity.getBody();
    } catch (RestClientException e) {
      log.error("Error sending data to security services module: {}", e.getMessage());
      throw e;
    }
  }
}
