package us.dot.its.jpo.ode.dds;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.junit.Test;
import org.slf4j.Logger;

import mockit.*;
import us.dot.its.jpo.ode.dds.CASClient.CASException;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.wrapper.SSLBuilder;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageDecoder;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;

public class DdsClientTest {
	String ddsCasUrl = "ddsCasUrl";
	String ddsCasUsername = "ddsCasUsername";
	String ddsCasPassword = "ddsCasPassword";
	String websocketURL = "websocketURL";
	String keystoreFile = "keystoreFile";
	String keystorePass = "keystorePass";

	@Test
	public void testConstructor(@Mocked CASClient mockCasClient, @Mocked final Logger mockLogger, @Mocked URI mockURI,
			@Mocked SSLBuilder mockSSLBuilder) {
		try {
			new Expectations() {
				{
					CASClient.configure((SSLContext) any, anyString, anyString, anyString);
				}
			};
		} catch (CASException e1) {
			e1.printStackTrace();
		}

		try {
			new DdsClient<String>(ddsCasUrl, ddsCasUsername, ddsCasPassword, websocketURL, keystoreFile, keystorePass);
		} catch (DdsClientException e) {
			e.printStackTrace();
		}

		try {
			new Verifications() {
				{
					new URI("websocketURL");
					minTimes = 1;
					CASClient.class.getClassLoader().getResourceAsStream("keystoreFile");
					SSLBuilder.buildSSLContext((InputStream) any, "keystorePass");
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(expected = DdsClientException.class)
	public void testConstructorException(@Mocked CASClient mockCasClient, @Mocked final Logger mockLogger)
			throws DdsClientException, CASException {

		new Expectations() {
			{
				CASClient.configure((SSLContext) any, anyString, anyString, anyString);
				result = new Exception();
			}
		};

		new DdsClient<String>(ddsCasUrl, ddsCasUsername, ddsCasPassword, websocketURL, keystoreFile, keystorePass);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLogin(@Mocked WebSocketMessageHandler<String> mockMessageHandler, @Mocked CASClient mockCasClient,
			@Mocked URI mockURI, @Mocked SSLContext mockSSLContext) {
		try {
			DdsClient<String> ddsClient = new DdsClient<String>(ddsCasUrl, ddsCasUsername, ddsCasPassword, websocketURL,
					keystoreFile, keystorePass);
			ddsClient.login(null, mockMessageHandler);

			new Verifications() {
				{
					mockCasClient.login(anyString);
					new WebSocketEndpoint<String>((URI) any, (SSLContext) any, null,
							(Map<String, Map<String, String>>) any, (WebSocketMessageHandler<String>) any,
							(List<Class<? extends WebSocketMessageDecoder<?>>>) any);
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Test(expected = DdsClientException.class)
	public void testLoginException(@Mocked WebSocketMessageHandler<String> mockMessageHandler,
			@Mocked CASClient mockCasClient, @Mocked URI mockURI, @Mocked SSLContext mockSSLContext)
			throws DdsClientException, CASException {

		new Expectations() {
			{
				mockCasClient.login(anyString);
				new WebSocketEndpoint<String>((URI) any, (SSLContext) any, null, (Map<String, Map<String, String>>) any,
						(WebSocketMessageHandler<String>) any, (List<Class<? extends WebSocketMessageDecoder<?>>>) any);
				result = new DdsClientException(null);
			}
		};

		DdsClient<String> ddsClient = new DdsClient<String>(ddsCasUrl, ddsCasUsername, ddsCasPassword, websocketURL,
				keystoreFile, keystorePass);
		ddsClient.login(null, mockMessageHandler);
	}
}
