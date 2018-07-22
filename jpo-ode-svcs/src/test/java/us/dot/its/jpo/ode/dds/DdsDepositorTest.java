package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.qos.logback.classic.Logger;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.OdeDataType;
import us.dot.its.jpo.ode.model.OdeDepRequest;
import us.dot.its.jpo.ode.model.OdeMessage;
import us.dot.its.jpo.ode.model.OdeRequest;
import us.dot.its.jpo.ode.model.OdeRequest.DataSource;
import us.dot.its.jpo.ode.model.OdeRequestType;
import us.dot.its.jpo.ode.plugin.j2735.DdsAdvisorySituationData;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;

@RunWith(JMockit.class)
public class DdsDepositorTest {

    /**
     * Basic test to verify constructor works as expected
     */
    @Test
    public void shouldConstruct(@Mocked OdeProperties mockOdeProperties, @Mocked Logger mockLogger) {

        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);

        testDdsDepositor.setLogger(mockLogger);

        OdeDepRequest actualOdeDepRequest = testDdsDepositor.getDepRequest();

        assertEquals(mockOdeProperties, testDdsDepositor.getOdeProperties());
        assertEquals(DataSource.SDW, actualOdeDepRequest.getDataSource());
        assertEquals(OdeDataType.AsnHex, actualOdeDepRequest.getDataType());
        assertEquals("hex", actualOdeDepRequest.getEncodeType());
        assertEquals(OdeRequestType.Deposit, actualOdeDepRequest.getRequestType());
    }

    /**
     * When the DdsRequestManager.isConnected() returns false, should try
     * connecting (Verify that DdsRequestManager.connect() is called)
     */
    @Test
    public void shouldTryConnectingWhenConnectedFalse(@Mocked OdeProperties mockOdeProperties,
            @Mocked DdsRequestManager<Object> mockRequestManager, @Mocked Logger mockLogger,
            @Mocked DdsAdvisorySituationData mockMessage) {

        new Expectations() {
            {
                mockRequestManager.isConnected();
                result = false;
            }
        };

        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        testDdsDepositor.setRequestManager(mockRequestManager);

        testDdsDepositor.setLogger(mockLogger);

        try {
            testDdsDepositor.deposit(mockMessage);

            new Verifications() {
                {
                    mockRequestManager.connect((WebSocketMessageHandler<Object>) any,
                            withAny(DepositResponseDecoder.class));
                    times = 1;
                    mockRequestManager.sendRequest((OdeRequest) any);
                    times = 1;
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * When the DdsRequestManager.isConnected() returns true, should not try
     * connecting (Verify that DdsRequestManager.connect() is NOT called)
     */
    @Test
    public void shouldNotTryConnectingWhenConnectedTrue(@Mocked OdeProperties mockOdeProperties,
            @Mocked DdsRequestManager<Object> mockRequestManager, @Mocked Logger mockLogger,
            @Mocked DdsAdvisorySituationData mockAsdMessage) {

        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);

        new Expectations() {
            {
                mockRequestManager.isConnected();
                result = true;
            }
        };

        testDdsDepositor.setRequestManager(mockRequestManager);

        testDdsDepositor.setLogger(mockLogger);

        try {
            testDdsDepositor.deposit(mockAsdMessage);

            new Verifications() {
                {
                    mockRequestManager.connect((WebSocketMessageHandler<Object>) any,
                            withAny(DepositResponseDecoder.class));
                    times = 0;
                    mockRequestManager.sendRequest((OdeRequest) any);
                    times = 1;
                }
            };

        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void shouldCloseWithoutErrorMessage(@Mocked OdeProperties mockOdeProperties,
            @Mocked DdsRequestManager<Object> mockRequestManager, @Mocked Logger mockLogger,
            @Mocked CloseReason mockCloseReason) throws DdsRequestManagerException {

        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);

        testDdsDepositor.setRequestManager(mockRequestManager);

        testDdsDepositor.setLogger(mockLogger);

        testDdsDepositor.onClose(mockCloseReason);

        try {
            new Verifications() {
                {
                    mockRequestManager.close();
                    times = 1;
                }
            };
        } catch (DdsRequestManagerException e) {
            fail("Unexpected exception: " + e);
        }
    }

    /**
     * Throw a control exception WITHOUT exception argument
     */
    @Test
    public void shouldCatchThrownExceptionWhenErrorOnCloseWithoutExceptionParam(@Mocked OdeProperties mockOdeProperties,
            @Mocked DdsRequestManager<Object> mockRequestManager, @Mocked Logger mockLogger,
            @Mocked CloseReason mockCloseReason) throws DdsRequestManagerException {

        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);

        DdsRequestManagerException testException = new DdsRequestManagerException("test");

        new Expectations() {
            {
                mockRequestManager.close();
                result = testException;
            }
        };

        testDdsDepositor.setRequestManager(mockRequestManager);
        testDdsDepositor.setLogger(mockLogger);

        testDdsDepositor.onClose(mockCloseReason);

        new Verifications() {
            {
                mockLogger.error(anyString, withAny(testException));
            }
        };
    }
    
    /**
     * Throw a control exception WITH exception argument
     */
    @Test
    public void shouldCatchThrownExceptionWhenErrorOnCloseWithExceptionParam(@Mocked OdeProperties mockOdeProperties,
            @Mocked DdsRequestManager<Object> mockRequestManager, @Mocked Logger mockLogger,
            @Mocked CloseReason mockCloseReason) throws DdsRequestManagerException {

        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);

        DdsRequestManagerException testException = new DdsRequestManagerException("test", new Exception("test"));

        new Expectations() {
            {
                mockRequestManager.close();
                result = testException;
            }
        };

        testDdsDepositor.setRequestManager(mockRequestManager);
        testDdsDepositor.setLogger(mockLogger);

        testDdsDepositor.onClose(mockCloseReason);

        new Verifications() {
            {
                mockLogger.error(anyString, withAny(testException));
            }
        };
    }

    @Test
    public void shouldLogOnMessage(@Mocked OdeProperties mockOdeProperties, @Mocked Logger mockLogger,
            @Mocked OdeMessage mockOdeMessage) {

        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);

        testDdsDepositor.setLogger(mockLogger);

        testDdsDepositor.onMessage(mockOdeMessage);

        new Verifications() {
            {
                mockLogger.info(anyString, withAny(mockOdeMessage));
            }
        };
    }

    @Test
    public void shouldLogOnOpen(@Mocked OdeProperties mockOdeProperties, @Mocked Logger mockLogger,
            @Mocked Session mockSession) {

        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);

        testDdsDepositor.setLogger(mockLogger);

        testDdsDepositor.onOpen(mockSession);

        new Verifications() {
            {
                mockLogger.info(anyString, anyString);
            }
        };
    }

    @Test
    public void shouldLogOnError(@Mocked OdeProperties mockOdeProperties, @Mocked Logger mockLogger) {

        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);

        testDdsDepositor.setLogger(mockLogger);

        Exception e = new Exception();
        testDdsDepositor.onError(e);

        new Verifications() {
            {
                mockLogger.error(anyString, e);
            }
        };
    }

}
