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

package us.dot.its.jpo.ode.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OdeFileUtilsTest {

  @Test
  void createDirectoryRecursivelyShouldThrowExceptionDirDoesNotExist(@Mock Path dir, @Mock File mockFile) {
    try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
      filesMock.when(() -> Files.exists(any(Path.class))).thenReturn(false);
      filesMock.when(() -> Files.createDirectories(any(Path.class))).thenReturn(dir);
      when(mockFile.exists()).thenReturn(false);
      when(dir.toFile()).thenReturn(mockFile);

      var ioException = assertThrows(IOException.class, () -> OdeFileUtils.createDirectoryRecursively(dir));
      assertEquals("Failed to verify directory creation - directory does not exist.", ioException.getMessage());
    }
  }

  @Test
  void createDirectoryRecursivelyShouldThrowExceptionUnableToCreateDirectory() {
    try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
      filesMock.when(() -> Files.createDirectories(any(Path.class)))
          .thenThrow(new IOException("testException123"));

      // Testing the actual method and asserting the exception
      Path mockPath = mock(Path.class);
      var ioException = assertThrows(IOException.class, () -> OdeFileUtils.createDirectoryRecursively(mockPath));
      assertTrue(ioException.getMessage().startsWith("Exception while trying to create directory:"));
    }

  }

  @Test
  void testCreateDirectoryRecursively() {
    var tempDir = new File(System.getProperty("java.io.tmpdir") + "/OdeFileUtilsTest-createDirectoryRecursively");
    try {
      OdeFileUtils.createDirectoryRecursively(tempDir.toPath());
    } catch (Exception e) {
      fail("Unexpected exception: " + e);
    }
  }


  @Test
  void backupFileShouldThrowExceptionBackupDirDoesNotExist() {
    // Mock the behavior of the backupDir.toFile().exists() method
    Path backupDir = mock(Path.class);
    Path mockFile = mock(Path.class);

    File mockBackupDirFile = mock(File.class);
    when(backupDir.toFile()).thenReturn(mockBackupDirFile);
    when(mockBackupDirFile.exists()).thenReturn(false);

    // Execute and assert
    Exception exception = assertThrows(IOException.class, () -> OdeFileUtils.backupFile(mockFile, backupDir));
    assertTrue(exception.getMessage().startsWith("Backup directory does not exist:"));
  }

  @Test
  void backupFileShouldThrowExceptionUnableToMoveFile() {
    // Mock Path and Files behavior
    Path mockFile = mock(Path.class);
    Path backupDir = mock(Path.class);
    File mockBackupDirFile = mock(File.class);

    when(mockFile.getFileName()).thenReturn(Path.of("testfile.uper"));
    when(backupDir.toFile()).thenReturn(mockBackupDirFile);
    when(backupDir.toString()).thenReturn("/tmp/OdeFileUtilsTest-backupDir");
    when(mockBackupDirFile.exists()).thenReturn(true);

    // Mock Files.move to throw an IOException
    try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
      filesMock.when(() -> Files.move(any(Path.class), any(Path.class), any(CopyOption.class)))
          .thenThrow(new IOException("testException123"));

      // Execute and assert
      Exception exception = assertThrows(IOException.class, () -> OdeFileUtils.backupFile(mockFile, backupDir));
      assertTrue(exception.getMessage().startsWith("Unable to move file to backup:"));
    }
  }

  @Test
  void testBackupFile() {
    var tempDir = new File(System.getProperty("java.io.tmpdir"));
    var tempBackupDir = new File(System.getProperty("java.io.tmpdir") + "/OdeFileUtilsTest-backups");
    tempBackupDir.mkdirs();
    tempBackupDir.deleteOnExit();
    var tempFile = new File(tempDir, "testfile.uper");
    tempFile.deleteOnExit();

    try {
      assertTrue(tempFile.createNewFile());
      OdeFileUtils.backupFile(tempFile.toPath(), tempBackupDir.toPath());
      assertTrue(Arrays.stream(Objects.requireNonNull(tempBackupDir.listFiles())).anyMatch(f -> f.getName().contains("testfile.pbo")));
    } catch (Exception e) {
      fail("Unexpected exception: " + e);
    }
  }

}
