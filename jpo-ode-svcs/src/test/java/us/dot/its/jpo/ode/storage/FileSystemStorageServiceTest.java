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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import us.dot.its.jpo.ode.archive.ArchiveImporterProperties;
import us.dot.its.jpo.ode.coder.stream.FileImporterProperties;

@ExtendWith(MockitoExtension.class)
class FileSystemStorageServiceTest {

  @TempDir
  Path uploadRoot;

  private FileSystemStorageService storageService;

  @BeforeEach
  void setUp() {
    FileImporterProperties fileImporterProperties = new FileImporterProperties();
    fileImporterProperties.setUploadLocationRoot(uploadRoot.toString());
    fileImporterProperties.setObuLogUploadLocation("bsmlog");
    storageService =
        new FileSystemStorageService(fileImporterProperties, new ArchiveImporterProperties());
  }

  @Test
  void storeShouldThrowExceptionUnknownType(@Mock MultipartFile mockMultipartFile) {
    var storageException = assertThrows(StorageException.class,
        () -> storageService.store(mockMultipartFile, LogFileType.UNKNOWN));
    assertTrue(storageException.getMessage().startsWith("File type unknown:"),
        "Incorrect message received");
  }

  @Test
  void storeShouldThrowExceptionOnEmptyFile(@Mock MultipartFile mockMultipartFile) {
    Mockito.doReturn("filename").when(mockMultipartFile).getOriginalFilename();
    Mockito.doReturn(true).when(mockMultipartFile).isEmpty();
    var storageException = assertThrows(StorageException.class,
        () -> storageService.store(mockMultipartFile, LogFileType.OBU));
    assertTrue(storageException.getMessage().startsWith("File is empty:"),
        "Incorrect message received");
  }

  @Test
  void storeShouldRethrowCopyException(@Mock MultipartFile mockMultipartFile) throws IOException {
    Mockito.doReturn("filename").when(mockMultipartFile).getOriginalFilename();
    Mockito.doReturn(false).when(mockMultipartFile).isEmpty();
    Mockito.doThrow(new IOException("read failed")).when(mockMultipartFile).getInputStream();

    var storageException = assertThrows(StorageException.class,
        () -> storageService.store(mockMultipartFile, LogFileType.OBU));
    assertTrue(storageException.getMessage().startsWith("Failed to store file in shared directory"),
        "Incorrect message received");
    assertFalse(Files.exists(uploadRoot.resolve("bsmlog").resolve("filename")));
    assertNoPartialFiles();
  }

  @Test
  void storeShouldWriteObuLogToLogDirectory() throws IOException {
    storageService.store(file("rxMsg_1.bin", "log"), LogFileType.OBU);

    assertEquals("log", Files.readString(uploadRoot.resolve("bsmlog").resolve("rxMsg_1.bin")));
    assertNoPartialFiles();
  }

  @Test
  void storeShouldWriteArchiveToArchiveInbox() throws IOException {
    String name = "topic.OdeBsmJson+0+0000000000.json.gz";
    storageService.store(file(name, "archive"), LogFileType.ARCHIVE);

    assertEquals("archive", Files.readString(uploadRoot.resolve("archive").resolve(name)));
    assertNoPartialFiles();
  }

  @Test
  void storeShouldReplaceExistingFile() throws IOException {
    String name = "topic.OdeBsmJson+0+0000000000.json.gz";
    storageService.store(file(name, "first"), LogFileType.ARCHIVE);
    storageService.store(file(name, "second"), LogFileType.ARCHIVE);

    assertEquals("second", Files.readString(uploadRoot.resolve("archive").resolve(name)));
  }

  @Test
  void storeShouldKeepOnlyTheLastElementOfAClientPath() throws IOException {
    storageService.store(file("C:\\Users\\me\\data\\topic.OdeMapJson+0+0000000005.json.gz", "map"),
        LogFileType.ARCHIVE);

    assertEquals("map", Files.readString(
        uploadRoot.resolve("archive").resolve("topic.OdeMapJson+0+0000000005.json.gz")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"..", ".hidden", "../", " "})
  void storeShouldRejectInvalidFileNames(String name) {
    MultipartFile upload = file(name, "x");
    assertThrows(StorageException.class, () -> storageService.store(upload, LogFileType.ARCHIVE));
  }

  @Test
  void storeShouldNotWriteOutsideTheUploadDirectory() throws IOException {
    storageService.store(file("../../escaped.json.gz", "x"), LogFileType.ARCHIVE);

    assertFalse(Files.exists(uploadRoot.resolve("escaped.json.gz")));
    assertFalse(Files.exists(uploadRoot.getParent().resolve("escaped.json.gz")));
    assertTrue(Files.exists(uploadRoot.resolve("archive").resolve("escaped.json.gz")));
  }

  private static MockMultipartFile file(String name, String content) {
    return new MockMultipartFile("file", name, "application/octet-stream",
        content.getBytes(StandardCharsets.UTF_8));
  }

  private void assertNoPartialFiles() throws IOException {
    try (Stream<Path> files = Files.walk(uploadRoot)) {
      assertTrue(files.noneMatch(p -> p.toString().endsWith(".part")),
          "A partial upload file was left behind");
    }
  }
}
