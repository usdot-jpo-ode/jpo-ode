//package us.dot.its.jpo.ode.plugin;
//
//import static org.junit.Assert.fail;
//
//import java.io.File;
//import java.security.Policy;
//import java.util.Properties;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import mockit.Expectations;
//import mockit.Injectable;
//import mockit.Mocked;
//import mockit.Tested;
//import mockit.integration.junit4.JMockit;
//import us.dot.its.jpo.ode.plugin.OdePluginImpl.OdePluginException;
//
//@RunWith(JMockit.class)
//public class OdePluginImplTest {
//
////    @Tested
////    OdePluginImpl testOdePluginImpl;
////
////    @Injectable
////    Properties mockProperties;
////
////    @Test
////    public void test_load(@Mocked final Policy mockPolicy, @Mocked final System mockSystem, @Mocked final File mockFile) {
////        
////        new Expectations() {{
////            new File("plugins");
////            mockFile.exists();
////            result = true;
////            mockFile.isDirectory();
////            result = true;
////        }};
////        
////        try {
////            testOdePluginImpl.load(mockProperties);
////        } catch (OdePluginException e) {
////            fail("Unexpected exception: " + e);
////        }
////    }
//
//}
