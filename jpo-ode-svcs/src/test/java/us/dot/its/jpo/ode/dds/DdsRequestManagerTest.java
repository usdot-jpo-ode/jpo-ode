package us.dot.its.jpo.ode.dds;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;

public class DdsRequestManagerTest {
    
    public final class MockDdsRequestManager extends MockUp<DdsRequestManager> {
        
    }
    
    @Test
    public void shouldConstruct() {
        MockDdsRequestManager testMockDdsRequestManager = new MockDdsRequestManager();
    }

//    @Test
//    public void shouldConstruct(@Mocked DdsRequestManager mockDdsRequestManager,
//            @Mocked OdeProperties mockOdeProperties) {
//
//        //mockDdsRequestManager = new MockUp<DdsRequestManager>(mockOdeProperties);
//        
//        new MockUp<DdsRequestManager>() {
//            @Mock(invocations = 1)
//            void $init(mockOdeProperties) {
//                assertNotNull
//            }
//            
//        };
//    }

}
