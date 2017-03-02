package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.dds.DdsClient.DdsClientException;
import us.dot.its.jpo.ode.dds.DdsRequestManager.DdsRequestManagerException;
import us.dot.its.jpo.ode.model.OdeDataType;
import us.dot.its.jpo.ode.model.OdeDepRequest;
import us.dot.its.jpo.ode.model.OdeRequest.DataSource;
import us.dot.its.jpo.ode.traveler.AsdMessage;
import us.dot.its.jpo.ode.model.OdeRequestType;
import us.dot.its.jpo.ode.wrapper.WebSocketMessageHandler;
import us.dot.its.jpo.ode.wrapper.WebSocketEndpoint.WebSocketException;

public class DdsDepositorTest {

    
    /**
     * Basic test to verify constructor works as expected
     */
    @Test
    public void shouldConstruct() {
        
        OdeProperties mockOdeProperties = Mockito.mock(OdeProperties.class);
        
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
        OdeProperties mockOdeProperties = Mockito.mock(OdeProperties.class);
        
        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        
        DdsRequestManager<Object> mockRequestManager = Mockito.mock(DdsRequestManager.class);
        Mockito.when(mockRequestManager.isConnected()).thenReturn(false);
        
        testDdsDepositor.setRequestManager(mockRequestManager);
        
        AsdMessage mockMessage = Mockito.mock(AsdMessage.class);
        
        try {
            testDdsDepositor.deposit(mockMessage);
            
            Mockito.verify(mockRequestManager, Mockito.times(1)).connect(Mockito.any(), Mockito.any());
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
        OdeProperties mockOdeProperties = Mockito.mock(OdeProperties.class);
        
        DdsDepositor<Object> testDdsDepositor = new DdsDepositor<Object>(mockOdeProperties);
        
        DdsRequestManager<Object> mockRequestManager = Mockito.mock(DdsRequestManager.class);
        Mockito.when(mockRequestManager.isConnected()).thenReturn(true);
        
        testDdsDepositor.setRequestManager(mockRequestManager);
        
        AsdMessage mockMessage = Mockito.mock(AsdMessage.class);
        
        try {
            testDdsDepositor.deposit(mockMessage);
            
            Mockito.verify(mockRequestManager, Mockito.times(0)).connect(Mockito.any(), Mockito.any());
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }
    
    

}
