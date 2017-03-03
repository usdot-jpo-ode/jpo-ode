package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.OdeDepRequest;
import us.dot.its.jpo.ode.model.OdeRequest;

public class DdsDepositRequestManagerTest {

    /**
     * Verify the constructor populates odeProperties and ddsClient
     */
    @Test
    public void shouldConstruct(@Mocked OdeProperties mockOdeProperties) {

        new Expectations() {
            {
                mockOdeProperties.getDdsCasUrl();
                result = anyString;
                mockOdeProperties.getDdsCasUsername();
                result = anyString;
                mockOdeProperties.getDdsCasPassword();
                result = anyString;
                mockOdeProperties.getDdsWebsocketUrl();
                result = anyString;
            }
        };

        try {
            DdsDepositRequestManager testDdsDepositRequestManager = new DdsDepositRequestManager(mockOdeProperties);

            assertNotNull(testDdsDepositRequestManager.getOdeProperties());
            assertNotNull(testDdsDepositRequestManager.getDdsClient());
        } catch (DdsRequestManagerException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * Verify DdsRequestManagerException thrown when !(odeRequest instanceof
     * OdeDepRequest)
     */
    @Test
    public void shouldFailToBuildRequestWrongRequestType(@Mocked OdeProperties mockOdeProperties,
            @Mocked OdeRequest mockOdeRequest) {

        new Expectations() {
            {
                mockOdeProperties.getDdsCasUrl();
                result = anyString;
                mockOdeProperties.getDdsCasUsername();
                result = anyString;
                mockOdeProperties.getDdsCasPassword();
                result = anyString;
                mockOdeProperties.getDdsWebsocketUrl();
                result = anyString;

            }
        };

        new Expectations() {
            {
                mockOdeRequest.toJson(false);
                result = "test123";
            }
        };

        DdsDepositRequestManager testDdsDepositRequestManager = null;
        try {
            testDdsDepositRequestManager = new DdsDepositRequestManager(mockOdeProperties);
        } catch (DdsRequestManagerException e) {
            fail("Unexpected exception: " + e);
        }

        try {
            testDdsDepositRequestManager.buildDdsRequest(mockOdeRequest);
            fail("Expected DdsRequestManagerException");
        } catch (Exception e) {
            assertEquals(DdsRequestManagerException.class, e.getClass());
            assertEquals("Invalid Request: test123", e.getMessage());
        }
    }

    /**
     * Verify DdsRequestManagerException thrown when .getRequestType() returns null
     */
    @Test
    public void shouldFailToBuildEncodeTypeNull(@Mocked OdeProperties mockOdeProperties,
            @Mocked OdeDepRequest mockOdeDepRequest) {
        
        new Expectations() {
            {
                mockOdeProperties.getDdsCasUrl();
                result = anyString;
                mockOdeProperties.getDdsCasUsername();
                result = anyString;
                mockOdeProperties.getDdsCasPassword();
                result = anyString;
                mockOdeProperties.getDdsWebsocketUrl();
                result = anyString;

            }
        };

        new Expectations() {{
            mockOdeDepRequest.getEncodeType();
            result = null;
        }};
        
        DdsDepositRequestManager testDdsDepositRequestManager = null;
        try {
            testDdsDepositRequestManager = new DdsDepositRequestManager(mockOdeProperties);
        } catch (DdsRequestManagerException e) {
            fail("Unexpected exception: " + e);
        }

        try {
            testDdsDepositRequestManager.buildDdsRequest(mockOdeDepRequest);
            fail("Expected DdsRequestManagerException");
        } catch (Exception e) {
            assertEquals(DdsRequestManagerException.class, e.getClass());
            assertTrue(e.getMessage().startsWith("Invalid or unsupported EncodeType Deposit:"));
        } 
    }
    
    public void shouldBuildSuccessfully(@Mocked OdeProperties mockOdeProperties,
            @Mocked OdeDepRequest mockOdeDepRequest) {
        
        new Expectations() {
            {
                mockOdeProperties.getDdsCasUrl();
                result = anyString;
                mockOdeProperties.getDdsCasUsername();
                result = anyString;
                mockOdeProperties.getDdsCasPassword();
                result = anyString;
                mockOdeProperties.getDdsWebsocketUrl();
                result = anyString;
            }
        };

        new Expectations() {{
            mockOdeDepRequest.getEncodeType();
            result = "base64";
        }};
        
        DdsDepositRequestManager testDdsDepositRequestManager = null;
        try {
            testDdsDepositRequestManager = new DdsDepositRequestManager(mockOdeProperties);
        } catch (DdsRequestManagerException e) {
            fail("Unexpected exception: " + e);
        }

        DdsRequest actualDdsRequest = null;
        try {
            actualDdsRequest = testDdsDepositRequestManager.buildDdsRequest(mockOdeDepRequest);
            assertNotNull("Failed to build actualDdsRequest", actualDdsRequest);
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        } 
        
        assertEquals("base64", actualDdsRequest.getResultEncoding());
    }
    
    
}
