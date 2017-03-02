package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import us.dot.its.jpo.ode.OdeProperties;

public class DdsRequestManagerTest {
    
    DdsRequestManager mockedDdsRequestManager;

    @Before
    public void shouldDoSomething() {
        mockedDdsRequestManager = Mockito.mock(DdsRequestManager.class, Mockito.CALLS_REAL_METHODS);
    }
    
    @Test
    public void shouldConstruct() {
        OdeProperties mockProps = Mockito.mock(OdeProperties.class);
        
        
        
    }

}
