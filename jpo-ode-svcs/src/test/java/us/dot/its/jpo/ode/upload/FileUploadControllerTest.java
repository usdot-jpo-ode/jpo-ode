/*******************************************************************************
 * Copyright 2018 572682
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.upload;

import mockit.Capturing;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.multipart.MultipartFile;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.coder.stream.FileImporterProperties;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher;
import us.dot.its.jpo.ode.kafka.FileTopics;
import us.dot.its.jpo.ode.kafka.JsonTopics;
import us.dot.its.jpo.ode.kafka.OdeKafkaProperties;
import us.dot.its.jpo.ode.kafka.RawEncodedJsonTopics;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

class FileUploadControllerTest {

    FileUploadController testFileUploadController;

    @Mocked
    StorageService mockStorageService;

    @Injectable
    OdeProperties injectableOdeProperties;

    @Injectable
    OdeKafkaProperties injectableOdeKafkaProperties;

    @Injectable
    SimpMessagingTemplate injectableSimpMessagingTemplate;

    @Injectable
    FileImporterProperties fileImporterProps;

    @Injectable
    FileTopics fileTopics;

    @Injectable
    JsonTopics jsonTopics;

    @Injectable
    RawEncodedJsonTopics rawEncodedJsonTopics;

    @Capturing
    Executors capturingExecutors;
    @Capturing
    ImporterDirectoryWatcher capturingImporterDirectoryWatcher;
    @Mocked
    ExecutorService mockExecutorService;

    @Mocked
    OdeProperties mockOdeProperties;

    @Mocked
    MultipartFile mockMultipartFile;

    @BeforeEach
    public void constructorShouldLaunchSevenThreads() {
        new Expectations() {
            {
                Executors.newCachedThreadPool();
                result = mockExecutorService;

                mockExecutorService.submit((Runnable) any);
                times = 11;
            }
        };

        testFileUploadController = new FileUploadController(mockStorageService,
                injectableSimpMessagingTemplate, fileImporterProps, fileTopics, jsonTopics, rawEncodedJsonTopics, injectableOdeKafkaProperties);
    }

    @Test
    void handleFileUploadReturnsErrorOnStorageException() {
        new Expectations() {
            {
                mockStorageService.store((MultipartFile) any, anyString);
                result = new StorageFileNotFoundException("testException123");
            }
        };

        assertEquals(HttpStatus.BAD_REQUEST,
                testFileUploadController.handleFileUpload(mockMultipartFile, "type").getStatusCode());
    }

    @Test
    void successfulUploadReturnsHttpOk() {
        new Expectations() {
            {
                mockStorageService.store((MultipartFile) any, anyString);
                times = 1;
            }
        };

        assertEquals(HttpStatus.OK, testFileUploadController.handleFileUpload(mockMultipartFile, "type").getStatusCode());
    }

    @Test
    void testStorageFileNotFoundException() {
        assertEquals(HttpStatus.NOT_FOUND, testFileUploadController
                .handleStorageFileNotFound(new StorageFileNotFoundException("testException123")).getStatusCode());
    }
}
