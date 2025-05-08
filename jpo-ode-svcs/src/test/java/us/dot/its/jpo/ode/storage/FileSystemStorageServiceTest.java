/*=============================================================================
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

package us.dot.its.jpo.ode.storage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import us.dot.its.jpo.ode.coder.stream.FileImporterProperties;

@ExtendWith(value = {SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class, classes = FileImporterProperties.class)
@EnableConfigurationProperties
class FileSystemStorageServiceTest {

  @Autowired
  private FileImporterProperties fileImporterProperties;

  @Test
  void storeShouldThrowExceptionUnknownType(@Mock MultipartFile mockMultipartFile) {
    var storageService = new FileSystemStorageService(fileImporterProperties);
    var storageException = assertThrows(StorageException.class, () -> storageService.store(mockMultipartFile, LogFileType.UNKNOWN));
    assertTrue(storageException.getMessage().startsWith("File type unknown:"), "Incorrect message received");

  }

  @Test
  void storeShouldThrowExceptionOnEmptyFile(@Mock MultipartFile mockMultipartFile) {

    Mockito.doReturn("filename").when(mockMultipartFile).getOriginalFilename();
    Mockito.doReturn(true).when(mockMultipartFile).isEmpty();
    var storageService = new FileSystemStorageService(fileImporterProperties);
    var storageException = assertThrows(StorageException.class, () -> storageService.store(mockMultipartFile, LogFileType.OBU));
    assertTrue(storageException.getMessage().startsWith("File is empty:"), "Incorrect message received");
  }

  @Test
  void storeShouldRethrowDeleteException(@Mock MultipartFile mockMultipartFile) {
    Mockito.doReturn("filename").when(mockMultipartFile).getOriginalFilename();
    Mockito.doReturn(false).when(mockMultipartFile).isEmpty();

    var storageService = new FileSystemStorageService(fileImporterProperties);
    var storageException = assertThrows(StorageException.class, () -> storageService.store(mockMultipartFile, LogFileType.OBU));
    assertTrue(storageException.getMessage().startsWith("Failed to store file in shared directory"), "Incorrect message received");
  }

  @Test
  void storeShouldRethrowCopyException(@Mock MultipartFile mockMultipartFile) throws IOException {
    Mockito.doReturn("filename").when(mockMultipartFile).getOriginalFilename();
    Mockito.doReturn(false).when(mockMultipartFile).isEmpty();
    Mockito.doReturn(null).when(mockMultipartFile).getInputStream();

    var storageService = new FileSystemStorageService(fileImporterProperties);
    var storageException = assertThrows(StorageException.class, () -> storageService.store(mockMultipartFile, LogFileType.OBU));
    assertTrue(storageException.getMessage().startsWith("Failed to store file in shared directory"), "Incorrect message received");
  }
}
