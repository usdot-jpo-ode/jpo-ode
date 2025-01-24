/*============================================================================
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import us.dot.its.jpo.ode.importer.ImporterDirectoryWatcher;
import us.dot.its.jpo.ode.storage.StorageFileNotFoundException;
import us.dot.its.jpo.ode.storage.StorageService;

@ExtendWith(MockitoExtension.class)
class FileUploadControllerTest {

  FileUploadController testFileUploadController;

  @Mock
  StorageService mockStorageService;

  @Mock
  ImporterDirectoryWatcher mockImporterDirectoryWatcher;

  @Mock
  MultipartFile mockMultipartFile;

  @BeforeEach
  public void setup() {
    testFileUploadController = new FileUploadController(mockStorageService, mockImporterDirectoryWatcher);
  }

  @Test
  void handleFileUploadReturnsErrorOnStorageException() {
    doThrow(new RuntimeException()).when(mockStorageService).store(any(), anyString());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, testFileUploadController.handleFileUpload(mockMultipartFile, "type").getStatusCode());
  }

  @Test
  void successfulUploadReturnsHttpOk() {
    Assertions.assertEquals(HttpStatus.OK, testFileUploadController.handleFileUpload(mockMultipartFile, "type").getStatusCode());
  }

  @Test
  void testStorageFileNotFoundException() {
    Assertions.assertEquals(HttpStatus.NOT_FOUND, testFileUploadController
        .handleStorageFileNotFound(new StorageFileNotFoundException("testException123")).getStatusCode());
  }
}
