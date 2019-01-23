/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.websocket.Session;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.OdeDepRequest;
import us.dot.its.jpo.ode.model.OdeRequest;
import us.dot.its.jpo.ode.model.OdeRequest.DataSource;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageDecoder;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;


public class DdsDepositRequestManagerTest {

    DdsDepositRequestManager testDdsDepositRequestManager;

    @Mocked OdeProperties mockOdeProperties;
    @Mocked Logger mockLogger;
    
    @Before
    public void setup() {
        new Expectations() {
            {
                mockOdeProperties.getDdsCasUrl();
                result = anyString;
                minTimes = 0;
                mockOdeProperties.getDdsCasUsername();
                result = anyString;
                minTimes = 0;
                mockOdeProperties.getDdsCasPassword();
                result = anyString;
                minTimes = 0;
                mockOdeProperties.getDdsWebsocketUrl();
                result = anyString;
                minTimes = 0;
            }
        };
        
        try {
            testDdsDepositRequestManager = new DdsDepositRequestManager(mockOdeProperties);
            testDdsDepositRequestManager.setLogger(mockLogger);
        } catch (DdsRequestManagerException e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e);
        }

    }

    /**
     * Verify the constructor populates odeProperties and ddsClient
     */
    @Test
    public void shouldConstruct() {

        assertNotNull(testDdsDepositRequestManager.getOdeProperties());
        assertNotNull(testDdsDepositRequestManager.getDdsClient());
    }

    /**
     * Verify DdsRequestManagerException thrown when .getRequestType() returns
     * null
     */
    @Test
    public void shouldFailToBuildEncodeTypeNull(@Mocked OdeDepRequest mockOdeDepRequest) {

        new Expectations() {
            {
                mockOdeDepRequest.getEncodeType();
                result = null;
            }
        };

        try {
            OdeDepRequest odeDepRequest = new OdeDepRequest();
            odeDepRequest.setEncodeType(null);
            testDdsDepositRequestManager.buildDdsRequest(odeDepRequest );
            fail("Expected DdsRequestManagerException");
        } catch (Exception e) {
            assertEquals(DdsRequestManagerException.class, e.getClass());
            assertTrue(e.getMessage().startsWith("Invalid or unsupported EncodeType Deposit:"));
        }
    }

    @Test
    public void shouldBuildSuccessfully(@Mocked OdeDepRequest mockOdeDepRequest) {

        new Expectations() {
            {
                mockOdeDepRequest.getEncodeType();
                result = "BASE64";
            }
        };
        
        DdsDepRequest actualDdsRequest = null;
        try {
            actualDdsRequest = (DdsDepRequest) testDdsDepositRequestManager.buildDdsRequest(mockOdeDepRequest);
            assertNotNull("Failed to build actualDdsRequest", actualDdsRequest);
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }

        assertEquals("BASE64", actualDdsRequest.getEncodeType());
    }

    @Test
    public void connectShouldTryToLoginAndConnect(@Mocked DdsClient<DdsStatusMessage> mockDdsClient,
            @Mocked WebSocketEndpoint<DdsStatusMessage> mockWsClient,
            @Mocked WebSocketMessageHandler<DdsStatusMessage> mockWebSocketMessageHandler,
            @Mocked WebSocketMessageDecoder<?> mockWebSocketMessageDecoder, @Mocked Session mockSession) {

        try {

            new Expectations() {
                {
                    mockDdsClient.login(withAny(DdsStatusMessageDecoder.class), (StatusMessageHandler) any);

                    mockWsClient.connect();
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception mocking expectations: " + e);
        }

        testDdsDepositRequestManager.setDdsClient(mockDdsClient);
        testDdsDepositRequestManager.setWsClient(mockWsClient);

        Session actualSession = null;
        try {
            actualSession = testDdsDepositRequestManager.connect(mockWebSocketMessageHandler,
                    mockWebSocketMessageDecoder.getClass());
        } catch (DdsRequestManagerException e) {
            fail("Unexpected exception attempting to connect: " + e);
        }

        assertEquals("Sessions do not match", mockSession, actualSession);
        assertTrue("Expected isConnected() to return true", testDdsDepositRequestManager.isConnected());
    }

    @Test
    public void connectShouldRethrowLoginException(@Mocked DdsClient<DdsStatusMessage> mockDdsClient,
            @Mocked WebSocketEndpoint<DdsStatusMessage> mockWsClient,
            @Mocked WebSocketMessageHandler<DdsStatusMessage> mockWebSocketMessageHandler,
            @Mocked WebSocketMessageDecoder<?> mockWebSocketMessageDecoder, @Mocked Session mockSession) {

        try {

            new Expectations() {
                {
                    mockDdsClient.login(withAny(DdsStatusMessageDecoder.class), (StatusMessageHandler) any);

                    mockWsClient.connect();
                    result = new DdsRequestManagerException("test exception");
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception mocking expectations: " + e);
        }

        testDdsDepositRequestManager.setDdsClient(mockDdsClient);
        testDdsDepositRequestManager.setWsClient(mockWsClient);

        try {
            testDdsDepositRequestManager.connect(mockWebSocketMessageHandler,
                    mockWebSocketMessageDecoder.getClass());
            fail("Expected DdsRequestManagerException");
        } catch (DdsRequestManagerException e) {
            assertEquals(DdsRequestManagerException.class, e.getClass());
            assertTrue(e.getMessage().startsWith("Error connecting to DDS"));
        }
    }

    @Test
    public void connectShouldSetConnectedFalseWhenSessionNull(@Mocked DdsClient<DdsStatusMessage> mockDdsClient,
            @Mocked WebSocketEndpoint<DdsStatusMessage> mockWsClient,
            @Mocked WebSocketMessageHandler<DdsStatusMessage> mockWebSocketMessageHandler,
            @Mocked WebSocketMessageDecoder<?> mockWebSocketMessageDecoder, @Mocked Session mockSession) {

        try {

            new Expectations() {
                {
                    mockDdsClient.login(withAny(DdsStatusMessageDecoder.class), (StatusMessageHandler) any);

                    mockWsClient.connect(); result = null;
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception mocking expectations: " + e);
        }

        testDdsDepositRequestManager.setDdsClient(mockDdsClient);
        testDdsDepositRequestManager.setWsClient(mockWsClient);

        try {
            testDdsDepositRequestManager.connect(mockWebSocketMessageHandler,
                    mockWebSocketMessageDecoder.getClass());
        } catch (DdsRequestManagerException e) {
            fail("Unexpected exception attempting to connect: " + e);
        }

        assertFalse(testDdsDepositRequestManager.isConnected());
    }

    @Test
    public void sendRequestshouldThrowExceptionWhenSessionNull(@Mocked DdsClient<DdsStatusMessage> mockDdsClient,
            @Mocked WebSocketEndpoint<DdsStatusMessage> mockWsClient,
            @Mocked WebSocketMessageHandler<DdsStatusMessage> mockWebSocketMessageHandler,
            @Mocked WebSocketMessageDecoder<?> mockWebSocketMessageDecoder, @Mocked Session mockSession,
            @Mocked OdeRequest mockOdeRequest) {

        try {

            new Expectations() {
                {
                    // mockDdsClient.login(withAny(DdsStatusMessageDecoder.class),
                    // (StatusMessageHandler) any);
                    // result = mockWsClient;

                    mockWsClient.connect();
                    result = null;
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception mocking expectations: " + e);
        }

        // Verify the session is null or the next part will be skipped
        assertNull(testDdsDepositRequestManager.getSession());

        testDdsDepositRequestManager.setDdsClient(mockDdsClient);
        testDdsDepositRequestManager.setWsClient(mockWsClient);

        try {
            testDdsDepositRequestManager.sendRequest(mockOdeRequest);
            fail("Expected DdsRequestManagerException");
        } catch (DdsRequestManagerException e) {
            assertEquals("Incorrect exception type", DdsRequestManagerException.class, e.getClass());
            assertTrue("Unexpected exception message" + e.getMessage(),
                    e.getMessage().startsWith("Error sending Data Request"));
            assertFalse("Expected connected to be false", testDdsDepositRequestManager.isConnected());
        }
    }

    @Test
    public void sendRequestShouldSendWithoutError(@Mocked DdsClient<DdsStatusMessage> mockDdsClient,
            @Mocked WebSocketEndpoint<DdsStatusMessage> mockWsClient,
            @Mocked WebSocketMessageHandler<DdsStatusMessage> mockWebSocketMessageHandler,
            @Mocked WebSocketMessageDecoder<?> mockWebSocketMessageDecoder, @Mocked Session mockSession,
            @Mocked OdeDepRequest mockOdeDepRequest) {

        try {

            new Expectations() {
                {
                    mockWsClient.connect();

                    mockWsClient.send(anyString);
                }
            };

            new Expectations() {
                {
                    mockOdeDepRequest.getDataSource();
                    result = DataSource.SDC;
                    mockOdeDepRequest.getEncodeType();
                    result = "BASE64";
                }
            };

        } catch (Exception e) {
            fail("Unexpected exception mocking expectations: " + e);
        }

        // Verify the session is null or the next part will be skipped
        assertNull(testDdsDepositRequestManager.getSession());

        testDdsDepositRequestManager.setDdsClient(mockDdsClient);
        testDdsDepositRequestManager.setWsClient(mockWsClient);

        try {
            testDdsDepositRequestManager.sendRequest(mockOdeDepRequest);
        } catch (DdsRequestManagerException e) {
            fail("Unexpected exception in send request: " + e);
        }

    }

    @Test
    public void sendRequestShouldLogWhenErrorClosingClient(@Mocked DdsClient<DdsStatusMessage> mockDdsClient,
            @Mocked WebSocketEndpoint<DdsStatusMessage> mockWsClient,
            @Mocked WebSocketMessageHandler<DdsStatusMessage> mockWebSocketMessageHandler,
            @Mocked WebSocketMessageDecoder<?> mockWebSocketMessageDecoder, @Mocked Session mockSession,
            @Mocked OdeRequest mockOdeRequest) {

        try {

            new Expectations() {
                {
                    mockWsClient.connect();

                    mockWsClient.close();
                    result = new WebSocketException("test exception on .close()");
                }
            };

            new Expectations() {
                {
                    mockLogger.error(anyString, (WebSocketException) any);
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception mocking expectations: " + e);
        }

        // Verify the session is null or the next part will be skipped
        assertNull(testDdsDepositRequestManager.getSession());

        testDdsDepositRequestManager.setDdsClient(mockDdsClient);
        testDdsDepositRequestManager.setWsClient(mockWsClient);

        try {
            testDdsDepositRequestManager.sendRequest(mockOdeRequest);
            fail("Expected DdsRequestManagerException");
        } catch (Exception e) {
            assertEquals(DdsRequestManagerException.class, e.getClass());
        }
    }

    @Test
    public void closeShouldCloseSuccessfully(@Mocked WebSocketEndpoint<DdsStatusMessage> mockWsClient) {

        try {
            new Expectations() {
                {
                    mockWsClient.close();
                }
            };
        } catch (WebSocketException e1) {
            fail("Unexpected exception calling close on mock websocket client: " + e1);
        }

        new Expectations() {
            {
                mockLogger.info(anyString);
            }
        };

        testDdsDepositRequestManager.setWsClient(mockWsClient);

        try {
            testDdsDepositRequestManager.close();
        } catch (DdsRequestManagerException e) {
            fail("Unexpected exception calling close: " + e);
        }

        assertFalse("Expected connected to be false.", testDdsDepositRequestManager.isConnected());
        assertNull("Expected wsClient to be null.", testDdsDepositRequestManager.getWsClient());
        assertNull("Expected session to be null.", testDdsDepositRequestManager.getSession());

    }

    @Test
    public void closeShouldThrowException(@Mocked WebSocketEndpoint<DdsStatusMessage> mockWsClient) {

        try {
            new Expectations() {
                {
                    mockWsClient.close();
                    result = new WebSocketException("test WebSocketException on close method");
                }
            };
        } catch (WebSocketException e1) {
            fail("Unexpected exception websocket client expectations: " + e1);
        }

        new Expectations() {
            {
                mockLogger.info(anyString);
            }
        };

        testDdsDepositRequestManager.setWsClient(mockWsClient);

        try {
            testDdsDepositRequestManager.close();
            fail("Expected DdsRequestManagerException to be thrown in close.");
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown", DdsRequestManagerException.class, e.getClass());
            assertTrue("Incorrect error message: " + e.getMessage(),
                    e.getMessage().startsWith("Error closing DDS Client"));
        }

    }
}
