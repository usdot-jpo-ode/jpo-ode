package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.OdeDataType;
import us.dot.its.jpo.ode.model.OdeDepRequest;
import us.dot.its.jpo.ode.model.OdeMessage;
import us.dot.its.jpo.ode.model.OdeRequest.DataSource;
import us.dot.its.jpo.ode.model.OdeRequestType;
import us.dot.its.jpo.ode.traveler.AsdMessage;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DdsDepositor.class, Logger.class, LoggerFactory.class})
public class DdsDepositorTest {
    
    private Logger mockLogger;
    
    @Before
    public void setup() {
        if (mockLogger != null) {
            reset(mockLogger);
        }
        mockStatic(LoggerFactory.class);
        mockLogger = mock(Logger.class);
        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(mockLogger);
        
    }

    
    /**
     * Basic test to verify constructor works as expected
     */
    @Test
    public void shouldConstruct() {
        
        OdeProperties mockOdeProperties = mock(OdeProperties.class);
        
        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        
        OdeDepRequest actualOdeDepRequest = testDdsDepositor.getDepRequest();
        
        assertEquals(mockOdeProperties, testDdsDepositor.getOdeProperties());
        assertEquals(DataSource.SDW, actualOdeDepRequest.getDataSource());
        assertEquals(OdeDataType.AsnHex, actualOdeDepRequest.getDataType());
        assertEquals("hex", actualOdeDepRequest.getEncodeType());
        assertEquals(OdeRequestType.Deposit, actualOdeDepRequest.getRequestType());
    }
    
    /**
     * When the DdsRequestManager.isConnected() returns false, should try connecting
     * (Verify that DdsRequestManager.connect() is called)
     */
    @Test
    public void shouldTryConnectingWhenConnectedFalse() {
        
        // Setup
        OdeProperties mockOdeProperties = mock(OdeProperties.class);
        
        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        
        DdsRequestManager<Object> mockRequestManager = mock(DdsRequestManager.class);
        when(mockRequestManager.isConnected()).thenReturn(false);
        
        testDdsDepositor.setRequestManager(mockRequestManager);
        
        AsdMessage mockMessage = mock(AsdMessage.class);
        
        try {
            testDdsDepositor.deposit(mockMessage);
            
            verify(mockRequestManager, times(1)).connect(any(), any());
            verify(mockRequestManager, times(1)).sendRequest(any());
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }
    
    /**
     * When the DdsRequestManager.isConnected() returns true, should not try connecting
     * (Verify that DdsRequestManager.connect() is NOT called)
     */
    @Test
    public void shouldNotTryConnectingWhenConnectedTrue() {
        
        // Setup
        OdeProperties mockOdeProperties = mock(OdeProperties.class);
        
        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        
        DdsRequestManager<Object> mockRequestManager = mock(DdsRequestManager.class);
        when(mockRequestManager.isConnected()).thenReturn(true);
        
        testDdsDepositor.setRequestManager(mockRequestManager);
        
        AsdMessage mockMessage = mock(AsdMessage.class);
        
        try {
            testDdsDepositor.deposit(mockMessage);
            
            verify(mockRequestManager, times(0)).connect(any(), any());
            verify(mockRequestManager, times(1)).sendRequest(any());
            
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }
    
    @Test
    public void shouldCloseWithoutErrorMessage() {
        
        OdeProperties mockOdeProperties = mock(OdeProperties.class);
        
        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        
        DdsRequestManager<Object> mockRequestManager = mock(DdsRequestManager.class);
        
        testDdsDepositor.setRequestManager(mockRequestManager);
        
        testDdsDepositor.onClose(mock(CloseReason.class));
        
        try {
            verify(mockRequestManager).close();
        } catch (DdsRequestManagerException e) {
            fail("Unexpected exception: " + e);
        }
    }
    
    @Test
    public void shouldCloseWithErrorMessageWhenFail() throws DdsRequestManagerException {
        
        OdeProperties mockOdeProperties = mock(OdeProperties.class);
        
        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        
        DdsRequestManager<Object> mockRequestManager = mock(DdsRequestManager.class);
        
        DdsRequestManagerException testException = new DdsRequestManagerException("test");
        
        doThrow(testException).when(mockRequestManager).close();
        
        testDdsDepositor.setRequestManager(mockRequestManager);
        Logger mockLogger = mock(Logger.class);
        testDdsDepositor.setLogger(mockLogger);
        
        testDdsDepositor.onClose(mock(CloseReason.class));
        
        verify(mockLogger).error("Error closing DDS Request Manager", testException);
        
    }
    
    @Test
    public void shouldLogOnMessage() {
        
        OdeProperties mockOdeProperties = mock(OdeProperties.class);
        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        
        Logger mockLogger = mock(Logger.class);
        testDdsDepositor.setLogger(mockLogger);
        
        OdeMessage mockMessage = mock(OdeMessage.class);
        
        testDdsDepositor.onMessage(mockMessage);
        
        verify(mockLogger).info("Deposit Response: {}", mockMessage);
    }
    
    @Test
    public void shouldLogOnOpen() {
        
        OdeProperties mockOdeProperties = mock(OdeProperties.class);
        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        
        Logger mockLogger = mock(Logger.class);
        testDdsDepositor.setLogger(mockLogger);
        
        Session mockSession = mock(Session.class);
        
        testDdsDepositor.onOpen(mockSession);
        
        verify(mockLogger).info("DDS Message Handler Opened Session {} ", mockSession.getId());
    }
    
    @Test
    public void shouldLogOnError() {
        
        OdeProperties mockOdeProperties = mock(OdeProperties.class);
        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        
        testDdsDepositor.setLogger(mockLogger);
        
        Throwable mockThrowable = mock(Throwable.class);
        
        testDdsDepositor.onError(mockThrowable);
        
        verify(mockLogger).error("Error reported by DDS Message Handler", mockThrowable);
    }
    
}
