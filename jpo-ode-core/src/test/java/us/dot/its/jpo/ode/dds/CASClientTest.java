package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.net.ssl.SSLContext;

import us.dot.its.jpo.ode.dds.CASClient.CASException;
import us.dot.its.jpo.ode.wrapper.HttpClientFactory;

@RunWith(JMockit.class)
public class CASClientTest {	
	@Mocked(stubOutClassInitialization = true) 
	final HttpClientFactory unused = null;
	
	@Mocked 
	SSLContext sslContext;
	
	@Mocked
	String webSocketURL;
	
	String casUser = "testUser";
	String casPass = "testPass";
	String casUrl = "testUrl";
	
	@Test
	public void testConfigure(){
		CASClient casClient = null;
		try {
			 casClient = CASClient.configure(sslContext, casUrl, casUser, casPass);
		} catch (CASException e) {
			fail("Unexpected exception: " + e.toString());
		}
		assertEquals(casClient.getDdsCasUrl(), casUrl);
		assertEquals(casClient.getDdsCasUsername() ,casUser);
	}
}
