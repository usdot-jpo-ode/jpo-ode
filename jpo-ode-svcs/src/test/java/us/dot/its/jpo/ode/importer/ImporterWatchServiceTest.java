package us.dot.its.jpo.ode.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchService;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.AbstractStreamDecoderPublisher;
import us.dot.its.jpo.ode.coder.BsmStreamDecoderPublisher;
public class ImporterWatchServiceTest {

    @Tested // (fullyInitialized=false)
    ImporterWatchService testImporterWatchService;
    @Injectable
    OdeProperties odeProperties;
    @Injectable
    Path dir;
    @Injectable
    Path backupDir;
    @Injectable
    BsmStreamDecoderPublisher decoderPublisher;
    @Injectable
    Logger logger;
    @Injectable
    String kafkaTopic = "foo";

    @Test
    public void testRun() {
        testImporterWatchService.run();
    }

    @Test
    public void createDirectoryRecursivelyShouldThrowExceptionDirDoesNotExist() {
        try {
            testImporterWatchService.createDirectoryRecursively(dir);
            fail("Expected IOException directory does not exist");
        } catch (Exception e) {
            assertEquals(IOException.class, e.getClass());
            assertEquals("Failed to verify directory creation - directory does not exist.", e.getMessage());
        }
    }

    @Test
    public void createDirectoryRecursivelyShouldThrowExceptionUnableToCreateDirectory(@Mocked final Files unused) {

        try {
            new Expectations() {
                {
                    Files.createDirectories((Path) any);
                    result = new IOException("testException123");
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        try {
            testImporterWatchService.createDirectoryRecursively(dir);
            fail("Expected IOException while trying to create directory:");
        } catch (Exception e) {
            assertEquals(IOException.class, e.getClass());
            assertTrue(e.getMessage().startsWith("Exception while trying to create directory:"));
        }
    }

    @Test
    public void testCreateDirectoryRecursively() {
        new Expectations() {
            {
                dir.toFile().exists();
                result = true;
            }
        };

        try {
            testImporterWatchService.createDirectoryRecursively(dir);
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void backupFileShouldThrowExceptionBackupDirDoesNotExist(@Mocked Path mockFile) {

        new Expectations() {
            {
                backupDir.toFile().exists();
                result = false;
            }
        };

        try {
            testImporterWatchService.backupFile(mockFile, backupDir);
            fail("Expected IOException while trying to backup file:");
        } catch (Exception e) {
            assertEquals(IOException.class, e.getClass());
            assertTrue(e.getMessage().startsWith("Backup directory does not exist:"));
        }
    }

    @Test
    public void backupFileShouldThrowExceptionUnableToMoveFile(@Mocked final Files unused, @Injectable Path mockFile) {

        try {
            new Expectations() {
                {
                    backupDir.toFile().exists();
                    result = true;

                    Files.move((Path) any, (Path) any, (CopyOption) any);
                    result = new IOException("testException123");
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        try {
            testImporterWatchService.backupFile(mockFile, backupDir);
            fail("Expected IOException while trying to move file:");
        } catch (Exception e) {
            assertEquals(IOException.class, e.getClass());
            assertTrue(e.getMessage().startsWith("Unable to move file to backup:"));
        }
    }

    @Test
    public void testBackupFile(@Mocked final Files unused, @Injectable Path mockFile) {

        new Expectations() {
            {
                backupDir.toFile().exists();
                result = true;
            }
        };

        try {
            testImporterWatchService.backupFile(mockFile, backupDir);
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }
    
    @Test
    public void processExistingFilesShouldCatchExceptionFailedToCreateStream(@Mocked final Files unused) {
        
        try {
            new Expectations() {{
                Files.newDirectoryStream((Path) any);
                result = null;
            }};
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        
        testImporterWatchService.processExistingFiles();
    }
    
    @Test
    public void processFileShouldCatchExceptionUnableToOpenFile(@Injectable Path mockFile) {
        
        new Expectations() {{
            mockFile.toFile();
            result = new IOException("testException123");
        }};
        testImporterWatchService.processFile(mockFile);
    }
    
    @Test
    public void processFileShouldDetectHex(@Mocked AbstractStreamDecoderPublisher decoderPublisher) {
        
        TemporaryFolder tmpFolder = new TemporaryFolder();
        
        File tmpFile = null;
        try {
            tmpFolder.create();
            tmpFile = tmpFolder.newFile("tmpHexFile.hex");
        } catch (IOException e) {
            fail("Failed to create temp hex file: " + e);
        }
        
        try {
            new Expectations() {{
                decoderPublisher.decodeHexAndPublish((InputStream)any);
            }};
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        
        testImporterWatchService.processFile(tmpFile.toPath());
    }
    
    @Test
    public void processFileShouldDetectTxt(@Mocked AbstractStreamDecoderPublisher decoderPublisher) {
        
        TemporaryFolder tmpFolder = new TemporaryFolder();
        
        File tmpFile = null;
        try {
            tmpFolder.create();
            tmpFile = tmpFolder.newFile("tmpTxtFile.txt");
        } catch (IOException e) {
            fail("Failed to create temp txt file: " + e);
        }
        
        try {
            new Expectations() {{
                decoderPublisher.decodeHexAndPublish((InputStream)any);
            }};
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        
        testImporterWatchService.processFile(tmpFile.toPath());
    }
    
    @Test
    public void processFileShouldDecodeMiscFilesAsBinary(@Mocked AbstractStreamDecoderPublisher decoderPublisher) {
        
        TemporaryFolder tmpFolder = new TemporaryFolder();
        
        File tmpFile = null;
        try {
            tmpFolder.create();
            tmpFile = tmpFolder.newFile("tmpBinFile.bin");
        } catch (IOException e) {
            fail("Failed to create temp bin file: " + e);
        }
        
        try {
            new Expectations() {{
                decoderPublisher.decodeBinaryAndPublish((InputStream)any);
            }};
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        
        testImporterWatchService.processFile(tmpFile.toPath());
    }
    
    @Test
    public void runShouldCatchExceptionNullWatchService() {
        
        try {
            new Expectations() {{
                dir.getFileSystem().newWatchService();
                result = null;
            }};
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        
        testImporterWatchService.run();
    }
    
    @Test
    public void runShouldCatchExceptionNullWatchKey(@Mocked WatchService mockWatchService) {
        try {
            new Expectations() {{
                dir.getFileSystem().newWatchService();
                result = mockWatchService;
                
                dir.register((WatchService) any, (Kind<?>) any);
                result = null;
            }};
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }
        
        testImporterWatchService.run();
    }
    

}
