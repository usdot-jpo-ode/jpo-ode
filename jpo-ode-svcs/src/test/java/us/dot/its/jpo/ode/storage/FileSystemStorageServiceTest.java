package us.dot.its.jpo.ode.storage;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import us.dot.its.jpo.ode.OdeProperties;

public class FileSystemStorageServiceTest {
    
    @Mocked
    OdeProperties mockOdeProperties;

    @Before
    public void setupOdePropertiesExpectations() {
        new Expectations() {
            {
                mockOdeProperties.getUploadLocationRoot();
                result = anyString;
                minTimes = 0;
                mockOdeProperties.getUploadLocationBsm();
                result = anyString;
                minTimes = 0;
                mockOdeProperties.getUploadLocationMessageFrame();
                result = anyString;
                minTimes = 0;
            }
        };
    }

    @Test
    public void shouldConstruct() {
        
        FileSystemStorageService testFileSystemStorageService = new FileSystemStorageService(mockOdeProperties);

        assertNotNull(testFileSystemStorageService.getRootLocation());
        assertNotNull(testFileSystemStorageService.getBsmLocation());
        assertNotNull(testFileSystemStorageService.getMessageFrameLocation());
        

    }

}
