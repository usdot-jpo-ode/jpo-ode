package us.dot.its.jpo.ode.storage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import us.dot.its.jpo.ode.OdeProperties;

public class FileSystemStorageServiceTest {
    
    /**
     * Basic constructor test
     */
    @Ignore
    @Test
    public void shouldConstructFileSystemStorageService() {
        
        String testuploadLocationRoot = "uploads";
        String testUploadLocationBsm = "uploadsbsm";
        String testUploadLocationMessageFrame = "uploads/messageframe";
        
        OdeProperties testInputProperties = null;
        try {
            testInputProperties = new OdeProperties();
            testInputProperties.setUploadLocationRoot(testuploadLocationRoot);
            testInputProperties.setUploadLocationBsm(testUploadLocationBsm);
            testInputProperties.setUploadLocationMessageFrame(testUploadLocationMessageFrame);
        } catch (Exception e) {
            fail("Unexpected exception creating OdeProperties object: " + e);
        }
        
        if (testInputProperties == null) {
            fail("OdeProperties object failed to populate");
        }
        
        FileSystemStorageService testService = new FileSystemStorageService(testInputProperties);
        
        assertNotNull(testService);
    }
    
    /**
     * Check that directories can be created and then deleted
     */
    @Ignore
    @Test
    public void shouldCreateThenDeleteTestDirectories() {
        
        String testUploadLocationRoot = "testRoot";
        String testUploadLocationBsm = "testRoot/testBsm";
        String testUploadLocationMessageFrame = "testRoot/testMessageframe";
        
        OdeProperties testInputProperties = null;
        try {
            testInputProperties = new OdeProperties();
            testInputProperties.setUploadLocationRoot(testUploadLocationRoot);
            testInputProperties.setUploadLocationBsm(testUploadLocationBsm);
            testInputProperties.setUploadLocationMessageFrame(testUploadLocationMessageFrame);
        } catch (Exception e) {
            fail("Unexpected exception creating OdeProperties object: " + e);
        }
        
        if (testInputProperties == null) {
            fail("OdeProperties object failed to populate");
        }
        
        FileSystemStorageService testService = new FileSystemStorageService(testInputProperties);
        
       // Test 1 - check that the directories have been created by .init()
        testService.init();
        assertTrue("Expected directory to be created: " + testUploadLocationRoot, 
                Files.exists(Paths.get(testUploadLocationRoot)));
        assertTrue("Expected directory to be created: " + testUploadLocationBsm, 
                Files.exists(Paths.get(testUploadLocationBsm)));
        assertTrue("Expected directory to be created: " + testUploadLocationMessageFrame, 
                Files.exists(Paths.get(testUploadLocationMessageFrame)));
        
        // Test 2 - check that the directories have been deleted by .deleteAll();
        testService.deleteAll();
        assertFalse("Expected directory to be deleted: " + testUploadLocationRoot, 
                Files.exists(Paths.get(testUploadLocationRoot)));
        assertFalse("Expected directory to be deleted: " + testUploadLocationBsm, 
                Files.exists(Paths.get(testUploadLocationBsm)));
        assertFalse("Expected directory to be deleted: " + testUploadLocationMessageFrame, 
                Files.exists(Paths.get(testUploadLocationMessageFrame)));
    }

    /**
     * Try storing, loading, and then deleting a bsm file
     */
    @Ignore
    @Test
    public void shouldStoreAndLoadBsmFile() {
        
        String testType = "bsm";
        String expectedLocalFilePath = "bsm/testBsm.hex";
        String expectedFilePath = "uploads/bsm/testBsm.hex";
        MultipartFile testFile = new MockMultipartFile("testBsm", "testBsm.hex", "text/plain", "001122".getBytes());
        
        OdeProperties testOdeProperties = null;
        try {
            testOdeProperties = new OdeProperties();
        } catch (Exception e) {
            fail("Unexpected exception creating OdeProperties object: " + e);
        }
        
        if (testOdeProperties == null) {
            fail("OdeProperties object failed to populate");
        }
        
        FileSystemStorageService testService = new FileSystemStorageService(testOdeProperties);
        testService.deleteAll();
        assertFalse("Expected root directory not to exist, unable to initialize FileSystemStorageService", 
                Files.exists(Paths.get(testOdeProperties.getUploadLocationRoot()))); 
        testService.init();
        
        // Test 1 - Try storing the file
        testService.store(testFile, testType);
        assertTrue(Files.exists(Paths.get(expectedFilePath)));
        
        // Test 2 - Try loading the file
        Resource loadedFile = testService.loadAsResource(expectedLocalFilePath);
        try {
            assertTrue(loadedFile.getInputStream().available() == 6);
        } catch (IOException e) {
            fail("Unexpected exception loading file: " + e);
        }
        
        // Test 3 - Try deleting the file
        testService.deleteAll();
        assertFalse(Files.exists(Paths.get(expectedFilePath))); 
    }
    
    /**
     * Try storing, loading, and then deleting a message frame file
     */
    @Ignore
    @Test
    public void shouldStoreAndLoadMessageFrameFile() {
        
        String testType = "messageFrame";
        String expectedLocalFilePath = "messageframe/testMf.hex";
        String expectedFilePath = "uploads/messageframe/testMf.hex";
        MockMultipartFile testFile = new MockMultipartFile("testMf", "testMf.hex", "text/plain", "001122".getBytes());
        
        OdeProperties testOdeProperties = null;
        try {
            testOdeProperties = new OdeProperties();
        } catch (Exception e) {
            fail("Unexpected exception creating OdeProperties object: " + e);
        }
        
        if (testOdeProperties == null) {
            fail("OdeProperties object failed to populate");
        }
        
        FileSystemStorageService testService = new FileSystemStorageService(testOdeProperties);
        testService.deleteAll();
        assertFalse("Expected root directory not to exist, unable to initialize FileSystemStorageService", 
                Files.exists(Paths.get(testOdeProperties.getUploadLocationRoot()))); 
        testService.init();
        
        // Test 1 - Try storing the file
        testService.store(testFile, testType);
        assertTrue(Files.exists(Paths.get(expectedFilePath)));
        
        // Test 2 - Try loading the file
        Resource loadedFile = testService.loadAsResource(expectedLocalFilePath);
        try {
            assertTrue(loadedFile.getInputStream().available() == 6);
        } catch (IOException e) {
            fail("Unexpected exception loading file: " + e);
        }
        
        // Test 3 - Try deleting the file
        testService.deleteAll();
        assertFalse(Files.exists(Paths.get(expectedFilePath)));
    }
}
