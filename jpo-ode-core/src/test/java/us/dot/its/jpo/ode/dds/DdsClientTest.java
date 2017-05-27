package us.dot.its.jpo.ode.dds;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.dds.CASClient.CASException;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.wrapper.SSLBuilder;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageDecoder;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;

@RunWith(JMockit.class)
public class DdsClientTest {
   @Mocked private WebSocketMessageHandler<String> mockMessageHandler;
   @Mocked private CASClient mockCasClient;
   
   String ddsCasUrl = "ddsCasUrl";
	String ddsCasUsername = "ddsCasUsername";
	String ddsCasPassword = "ddsCasPassword";
	String websocketURL = "ws://websocket.org";
	String keystoreFile = "keystoreFile";
	String keystorePass = "keystorePass";

	@Ignore
	@Test
	public void testConstructor() {
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

   @Ignore
	@Test(expected = DdsClientException.class)
	public void testConstructorException()
			throws DdsClientException, CASException {

		new Expectations() {
			{
				CASClient.configure((SSLContext) any, anyString, anyString, anyString);
				result = new Exception();
			}
		};

		new DdsClient<String>(ddsCasUrl, ddsCasUsername, ddsCasPassword, websocketURL, keystoreFile, keystorePass);
	}

   @Ignore
	@Test
	public void testLogin() {
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
	public void testLoginException()
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
