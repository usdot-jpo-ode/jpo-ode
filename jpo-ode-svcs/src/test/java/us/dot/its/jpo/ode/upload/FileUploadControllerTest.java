package us.dot.its.jpo.ode.upload;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.http.ResponseEntity.HeadersBuilder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.multipart.MultipartFile;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.SerializableMessageProducerPool;
import us.dot.its.jpo.ode.importer.ImporterWatchService;
import us.dot.its.jpo.ode.plugin.PluginFactory;
import us.dot.its.jpo.ode.plugin.asn1.J2735Plugin;
import us.dot.its.jpo.ode.storage.StorageException;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;
import us.dot.its.jpo.ode.wrapper.MessageProducer;

@RunWith(JMockit.class)
@Ignore // TODO - this test is incorrect
public class FileUploadControllerTest {

    @Tested
    FileUploadController testFileUploadController;

    @Injectable
    StorageService mockStorageService;
    @Injectable
    OdeProperties mockOdeProperties;
    @Injectable
    SimpMessagingTemplate mockTemplate;

    @Before
    public void setUp() {
        new Expectations() {
            {
                mockOdeProperties.getUploadLocationRoot();
                result = anyString;
                mockOdeProperties.getUploadLocationBsm();
                result = anyString;
                mockOdeProperties.getUploadLocationMessageFrame();
                result = anyString;
            }
        };
    }

    @Test
    public void handleUploadShouldReturnFalseWithUnknownType(@Mocked MultipartFile mockFile,
            @Injectable final Executors unused, @Injectable ExecutorService mockExecutorService,
            @Mocked final PluginFactory unused2, @Mocked J2735Plugin mockAsn1Plugin,
            @Mocked MessageProducer<String, String> mockMessageProducer,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked final LoggerFactory unusedLoggerFactory,
            @Mocked final ImporterWatchService mockImporterWatchService
            ) {

        String testType = "unknownTestType";

        assertEquals("{\"success\": false}", testFileUploadController.handleFileUpload(mockFile, testType));

    }
    
    @Test
    public void handleUploadShouldReturnFalseWhenStoreThrowsException(@Mocked MultipartFile mockFile,
            @Injectable final Executors unused, @Injectable ExecutorService mockExecutorService,
            @Mocked final PluginFactory unused2, @Mocked J2735Plugin mockAsn1Plugin,
            @Mocked MessageProducer<String, String> mockMessageProducer,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked final LoggerFactory unusedLoggerFactory,
            @Mocked final ImporterWatchService mockImporterWatchService
            ) {

        String testType = "bsm";

        try {
            new Expectations() {
                {
                    mockStorageService.store(mockFile, anyString);
                    result = new StorageException("testException123");
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        
        assertEquals("{\"success\": false}", testFileUploadController.handleFileUpload(mockFile, testType));

    }
    
    @Test
    public void handleUploadShouldReturnTrueForBsm(
          @Mocked MultipartFile mockFile,
          @Injectable final Executors unused, 
          @Injectable ExecutorService mockExecutorService,
          @Mocked final PluginFactory unused2,
          @Mocked J2735Plugin mockAsn1Plugin,
          @Mocked MessageProducer<String, String> mockMessageProducer,
          @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
          @Mocked final LoggerFactory unusedLoggerFactory,
          @Mocked final ImporterWatchService mockImporterWatchService
          ) {

        String testType = "bsm";

        try {
            new Expectations() {
                {
                    mockStorageService.store(mockFile, anyString);
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        assertEquals("{\"success\": true}", testFileUploadController.handleFileUpload(mockFile, testType));

    }
    
    @Test
    public void handleUploadShouldReturnTrueForMessageFrame(@Mocked MultipartFile mockFile,
            @Injectable final Executors unused, @Injectable ExecutorService mockExecutorService,
            @Mocked final PluginFactory unused2, @Mocked J2735Plugin mockAsn1Plugin,
            @Mocked MessageProducer<String, String> mockMessageProducer,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked final LoggerFactory unusedLoggerFactory,
            @Mocked final ImporterWatchService mockImporterWatchService
            ) {

        String testType = "messageFrame";

        try {
            new Expectations() {
                {
                    mockStorageService.store(mockFile, anyString);
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        
        assertEquals("{\"success\": true}", testFileUploadController.handleFileUpload(mockFile, testType));

    }
    
    @Test
    public void serveFileShouldReturnSuccessfully(@Mocked MultipartFile mockFile,
            @Injectable final Executors unused, @Injectable ExecutorService mockExecutorService,
            @Mocked final PluginFactory unused2, @Mocked J2735Plugin mockAsn1Plugin,
            @Mocked MessageProducer<String, String> mockMessageProducer,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked final LoggerFactory unusedLoggerFactory,
            @Mocked final ImporterWatchService mockImporterWatchService,
            @Mocked final ResponseEntity<?> mockResponseEntity,
            @Mocked BodyBuilder mockBodyBuilder
            ) {

        try {
            new Expectations() {
                {
                    ResponseEntity.ok();
                    result = mockBodyBuilder;
                    
                    mockBodyBuilder.header(anyString, (String[]) any);
                    result = mockBodyBuilder;
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        
        testFileUploadController.serveFile("someTestFile");

    }
    
    @Test
    public void testHandleStorageFileNotFound(@Mocked MultipartFile mockFile,
            @Injectable final Executors unused, @Injectable ExecutorService mockExecutorService,
            @Mocked final PluginFactory unused2, @Mocked J2735Plugin mockAsn1Plugin,
            @Mocked MessageProducer<String, String> mockMessageProducer,
            @Mocked SerializableMessageProducerPool<String, byte[]> mockSerializableMessagePool,
            @Mocked final LoggerFactory unusedLoggerFactory,
            @Mocked final ImporterWatchService mockImporterWatchService,
            @Mocked final ResponseEntity<?> mockResponseEntity,
            @Mocked BodyBuilder mockBodyBuilder,
            @Mocked HeadersBuilder<?> mockHeadersBuilder
            ) {

        try {
            new Expectations() {
                {
                    ResponseEntity.notFound();
                    result = mockHeadersBuilder;
                    
                    mockHeadersBuilder.build();
                }
            };
        } catch (Exception e) {
            fail("Unexpected exception in expectations block: " + e);
        }

        
        testFileUploadController.handleStorageFileNotFound(new StorageFileNotFoundException("testString123"));
    }
}
