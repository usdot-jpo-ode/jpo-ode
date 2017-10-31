package us.dot.its.jpo.ode.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.eventlog.EventLogger;

public class FileSystemStorageServiceTest {

    @Mocked
    OdeProperties mockOdeProperties;

    @Before
    public void setupOdePropertiesExpectations() {
        new Expectations() {
            {
                mockOdeProperties.getUploadLocationRoot();
                result = anyString;
                mockOdeProperties.getUploadLocationObuLog();
                result = anyString;
            }
        };
    }

    @Test
    public void shouldConstruct(@Mocked final Logger mockLogger, @Mocked LoggerFactory unused) {

        FileSystemStorageService testFileSystemStorageService = new FileSystemStorageService(mockOdeProperties);

        assertNotNull(testFileSystemStorageService.getRootLocation());

    }

    @Test
    public void storeShouldThrowExceptionUnknownType(@Mocked MultipartFile mockMultipartFile) {

        String unknownType = "test123";

        try {
            new FileSystemStorageService(mockOdeProperties).store(mockMultipartFile, unknownType);
            fail("Expected StorageException");
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown", StorageException.class, e.getClass());
            assertTrue("Incorrect message received", e.getMessage().startsWith("File type unknown:"));
        }

        new Verifications() {
            {
                EventLogger.logger.info(anyString, any, any);
            }
        };

    }

    @Test
    public void storeShouldTryToResolveBsmFilename(@Mocked MultipartFile mockMultipartFile) {

        String testType = "obulog";

        new Expectations() {
            {
                mockMultipartFile.getOriginalFilename();
                result = anyString;
                mockMultipartFile.isEmpty();
                result = true;
            }
        };

        try {
            new FileSystemStorageService(mockOdeProperties).store(mockMultipartFile, testType);
            fail("Expected StorageException");
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown", StorageException.class, e.getClass());
            assertTrue("Incorrect message received", e.getMessage().startsWith("File is empty:"));
        }

        new Verifications() {
            {
                EventLogger.logger.info(anyString);
            }
        };
    }

    @Test
    public void storeShouldThrowAnErrorEmptyFile(@Mocked MultipartFile mockMultipartFile) {

        String testType = "obulog";

        new Expectations() {
            {
                mockMultipartFile.getOriginalFilename();
                result = anyString;
                mockMultipartFile.isEmpty();
                result = true;
            }
        };

        try {
            new FileSystemStorageService(mockOdeProperties).store(mockMultipartFile, testType);
            fail("Expected StorageException");
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown", StorageException.class, e.getClass());
            assertTrue("Incorrect message received", e.getMessage().startsWith("File is empty:"));
        }

        new Verifications() {
            {
                EventLogger.logger.info(anyString);
            }
        };
    }

    @Test
    public void storeShouldRethrowDeleteException(@Mocked MultipartFile mockMultipartFile, @Mocked Files unused) {

        String testType = "obulog";

        new Expectations() {
            {
                mockMultipartFile.getOriginalFilename();
                result = anyString;
                mockMultipartFile.isEmpty();
                result = false;
            }
        };

        try {
            new Expectations() {
                {
                    Files.deleteIfExists((Path) any);
                    result = new IOException("testException123");
                }
            };
        } catch (IOException e1) {
            fail("Unexpected exception on Files.deleteIfExists() expectation creation");
        }

        try {
            new FileSystemStorageService(mockOdeProperties).store(mockMultipartFile, testType);
            fail("Expected StorageException");
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown", StorageException.class, e.getClass());
            assertTrue("Incorrect message received", e.getMessage().startsWith("Failed to delete existing file:"));
        }

        new Verifications() {
            {
                EventLogger.logger.info("Deleting existing file: {}", any);
                EventLogger.logger.info("Failed to delete existing file: {} ", any);
            }
        };
    }

    @Test
    public void storeShouldRethrowCopyException(@Mocked MultipartFile mockMultipartFile, @Mocked Files unusedFiles,
            @Mocked final Logger mockLogger, @Mocked LoggerFactory unusedLogger, @Mocked InputStream mockInputStream) {

        String testType = "obulog";

        try {
            new Expectations() {
                {
                    mockMultipartFile.getOriginalFilename();
                    result = anyString;

                    mockMultipartFile.isEmpty();
                    result = false;

                    mockMultipartFile.getInputStream();
                    result = mockInputStream;

                    Files.deleteIfExists((Path) any);

                    Files.copy((InputStream) any, (Path) any);
                    result = new IOException("testException123");
                }
            };
        } catch (IOException e1) {
            fail("Unexpected exception creating test Expectations: " + e1);
        }

        try {
            new FileSystemStorageService(mockOdeProperties).store(mockMultipartFile, testType);
            fail("Expected StorageException");
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown", StorageException.class, e.getClass());
            assertTrue("Incorrect message received",
                    e.getMessage().startsWith("Failed to store file in shared directory"));
        }

        new Verifications() {
            {
                EventLogger.logger.info("Copying file {} to {}", anyString, (Path) any);
                EventLogger.logger.info("Failed to store file in shared directory {}", (Path) any);
            }
        };
    }

    @Test
    public void loadAllShouldRethrowException(@Mocked Files unused) {
        try {
            new Expectations() {
                {
                    Files.walk((Path) any, anyInt);// .filter(null).map(null);
                    result = new IOException("testException123");
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception creating Expectations: " + e);
        }

        try {
            new FileSystemStorageService(mockOdeProperties).loadAll();
            fail("Expected StorageException");
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown", StorageException.class, e.getClass());
            assertTrue("Incorrect message received", e.getMessage().startsWith("Failed to read files stored in"));
        }

        new Verifications() {
            {
                EventLogger.logger.info("Failed to read files stored in {}", (Path) any);
            }
        };
    }

    @Test
    public void loadAsResourceShouldThrowExceptionWhenFileNotExists(
          @Mocked Path mockRootPath,
          @Mocked Path mockResolvedPath,
          @Mocked UrlResource mockUrlResource) {

        try {
            new Expectations() {
                {
                    mockRootPath.resolve(anyString);
                    result = mockResolvedPath;

                    mockResolvedPath.toUri();

                    new UrlResource((URI) any); 
                    result = mockUrlResource;
                    
                    mockUrlResource.exists();
                    result = false;
                }
            };
        } catch (MalformedURLException e) {
            fail("Unexpected exception creating Expectations");
        }

        try {
            FileSystemStorageService testFileSystemStorageService = new FileSystemStorageService(mockOdeProperties);
            testFileSystemStorageService.setRootLocation(mockRootPath);
            testFileSystemStorageService.loadAsResource("testFile");
            fail("Expected StorageFileNotFoundException");
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown.", StorageFileNotFoundException.class, e.getClass());
            assertTrue("Incorrect exception message", e.getMessage().startsWith("Could not read file:"));
        }
    }

    @Test
    public void loadAsResourceShouldThrowExceptionWhenFileNotReadable(@Mocked Path mockRootPath,
            @Mocked Path mockResolvedPath, @Mocked UrlResource mockUrlResource) {

        try {
            new Expectations() {
                {
                    mockRootPath.resolve(anyString);
                    result = mockResolvedPath;

                    mockResolvedPath.toUri();

                    new UrlResource((URI) any);
                    result = mockUrlResource;

                    mockUrlResource.exists();
                    result = true;
                    
                    mockUrlResource.isReadable();
                    result = false;
                }
            };
        } catch (MalformedURLException e) {
            fail("Unexpected exception creating Expectations");
        }

        try {
            FileSystemStorageService testFileSystemStorageService = new FileSystemStorageService(mockOdeProperties);
            testFileSystemStorageService.setRootLocation(mockRootPath);
            testFileSystemStorageService.loadAsResource("testFile");
            fail("Expected StorageFileNotFoundException");
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown.", StorageFileNotFoundException.class, e.getClass());
            assertTrue("Incorrect exception message", e.getMessage().startsWith("Could not read file:"));
        }
    }

    @Test
    public void loadAsResourceShouldRethrowMalformedURLException(@Mocked Path mockRootPath,
            @Mocked Path mockResolvedPath, @Mocked UrlResource mockUrlResource) {

        try {
            new Expectations() {
                {
                    mockRootPath.resolve(anyString);
                    result = mockResolvedPath;

                    mockResolvedPath.toUri();

                    new UrlResource((URI) any);
                    result = new MalformedURLException("testException123");
                }
            };
        } catch (MalformedURLException e) {
            fail("Unexpected exception creating Expectations");
        }

        try {
            FileSystemStorageService testFileSystemStorageService = new FileSystemStorageService(mockOdeProperties);
            testFileSystemStorageService.setRootLocation(mockRootPath);
            testFileSystemStorageService.loadAsResource("testFile");
            fail("Expected StorageFileNotFoundException");
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown.", StorageFileNotFoundException.class, e.getClass());
            assertTrue("Incorrect exception message", e.getMessage().startsWith("Could not read file:"));
        }
    }

    @Test
    public void loadAsResourceShouldReturnResource(@Mocked Path mockRootPath, @Mocked Path mockResolvedPath,
            @Mocked UrlResource mockUrlResource) {

        try {
            new Expectations() {
                {
                    mockRootPath.resolve(anyString);
                    result = mockResolvedPath;

                    mockResolvedPath.toUri();

                    new UrlResource((URI) any);
                    result = mockUrlResource;

                    mockUrlResource.exists();
                    result = true;
                    mockUrlResource.isReadable();
                    result = true;
                }
            };
        } catch (MalformedURLException e) {
            fail("Unexpected exception creating Expectations");
        }

        try {
            FileSystemStorageService testFileSystemStorageService = new FileSystemStorageService(mockOdeProperties);
            testFileSystemStorageService.setRootLocation(mockRootPath);
            assertEquals(UrlResource.class, testFileSystemStorageService.loadAsResource("testFile").getClass());
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void initShouldCreateDirectories(@Mocked final Files unused) {

        new FileSystemStorageService(mockOdeProperties).init();

        try {
            new Verifications() {
                {
                    Files.createDirectory((Path) any);
                    times = 1;
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in verifications block: " + e);
        }
    }

    @Test
    public void initShouldRethrowAndLogException(@Mocked final Files unused) {

        try {
            new Expectations() {
                {
                    Files.createDirectory((Path) any);
                    result = new IOException("testException123");
                }
            };
        } catch (IOException e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        try {
            new FileSystemStorageService(mockOdeProperties).init();
        } catch (Exception e) {
            assertEquals("Incorrect exception thrown.", StorageException.class, e.getClass());
            assertTrue("Incorrect exception message",
                    e.getMessage().startsWith("Failed to initialize storage service"));
        }

        new Verifications() {
            {
                EventLogger.logger.info("Failed to initialize storage service {}", (Path) any);
            }
        };
    }

    @Test
    public void deleteAllShouldDeleteRecursivelyAndLog(@Mocked final FileSystemUtils unused) {

        new FileSystemStorageService(mockOdeProperties).deleteAll();

        new Verifications() {
            {
                FileSystemUtils.deleteRecursively((File) any);
                EventLogger.logger.info("Deleting {}", (Path) any);
            }
        };

    }

}
