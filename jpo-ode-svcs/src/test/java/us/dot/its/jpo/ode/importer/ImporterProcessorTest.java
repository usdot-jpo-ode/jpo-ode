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

package us.dot.its.jpo.ode.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType.bsmTx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher;
import us.dot.its.jpo.ode.coder.stream.LogFileToAsn1CodecPublisher.LogFileToAsn1CodecPublisherException;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParserFactory.LogFileParserFactoryException;

@ExtendWith(MockitoExtension.class)
class ImporterProcessorTest {

  Path dirToProcess;
  Path backupDir;
  Path failureDir;

  @BeforeEach
  void setup() throws IOException {
    dirToProcess = new File(System.getProperty("java.io.tmpdir") + "/filesToProcess").toPath();
    backupDir = new File(System.getProperty("java.io.tmpdir") + "/backup").toPath();
    failureDir = new File(System.getProperty("java.io.tmpdir") + "/failure").toPath();
    OdeFileUtils.createDirectoryRecursively(dirToProcess);
    OdeFileUtils.createDirectoryRecursively(backupDir);
    OdeFileUtils.createDirectoryRecursively(failureDir);
  }

  @Test
  void testProcessDirectoryGZIP(@Mock LogFileToAsn1CodecPublisher publisher)
      throws IOException, LogFileParserFactoryException, LogFileToAsn1CodecPublisherException {
    var testCaseDir = new File(dirToProcess + "/gzips");
    testCaseDir.mkdirs();
    // Write some dummy data to the test gzip file so the input stream can be opened
    createGZIP(testCaseDir.getAbsolutePath());

    ImporterProcessor importerProcessor = new ImporterProcessor(publisher, ImporterFileType.LOG_FILE, 1024);
    int result = importerProcessor.processDirectory(dirToProcess, backupDir, failureDir);
    assertEquals(1, result);
    verify(publisher, times(1)).publish(any(BufferedInputStream.class), anyString(), eq(ImporterFileType.LOG_FILE), any(LogFileParser.class));
  }


  @Test
  void testProcessDirectoryPlainText(@Mock LogFileToAsn1CodecPublisher publisher)
      throws IOException, LogFileParserFactoryException, LogFileToAsn1CodecPublisherException {
    var testCaseDir = new File(dirToProcess + "/plaintexts");
    testCaseDir.mkdirs();
    createPlaintext(testCaseDir.getAbsolutePath());

    ImporterProcessor importerProcessor = new ImporterProcessor(publisher, ImporterFileType.LOG_FILE, 1024);
    int result = importerProcessor.processDirectory(testCaseDir.toPath(), backupDir, failureDir);
    assertEquals(1, result);
    verify(publisher, times(1)).publish(any(BufferedInputStream.class), anyString(), eq(ImporterFileType.LOG_FILE), any(LogFileParser.class));
  }

  @Test
  void testProcessDirectoryZip(@Mock LogFileToAsn1CodecPublisher publisher)
      throws IOException, LogFileParserFactoryException, LogFileToAsn1CodecPublisherException {
    var testCaseDir = new File(dirToProcess + "/zips");
    testCaseDir.mkdirs();
    createZip(testCaseDir.getAbsolutePath());

    ImporterProcessor importerProcessor = new ImporterProcessor(publisher, ImporterFileType.LOG_FILE, 1024);
    int result = importerProcessor.processDirectory(testCaseDir.toPath(), backupDir, failureDir);
    assertEquals(1, result);
    // verify publisher.publish called for each ZipEntry in the zip file
    verify(publisher, times(2)).publish(any(BufferedInputStream.class), anyString(), eq(ImporterFileType.LOG_FILE), any(LogFileParser.class));
  }


  @Test
  void testProcessWithAllFileTypes(@Mock LogFileToAsn1CodecPublisher publisher)
      throws IOException, LogFileParserFactoryException, LogFileToAsn1CodecPublisherException {
    var testCaseDir = new File(dirToProcess + "/allFileTypes");
    testCaseDir.mkdirs();
    createGZIP(testCaseDir.getAbsolutePath());
    createPlaintext(testCaseDir.getAbsolutePath());
    createZip(testCaseDir.getAbsolutePath());

    ImporterProcessor importerProcessor = new ImporterProcessor(publisher, ImporterFileType.LOG_FILE, 1024);
    int result = importerProcessor.processDirectory(testCaseDir.toPath(), backupDir, failureDir);
    assertEquals(3, result);
    // verify we call publish for each file - 1 plaintext, 1 gzip, 2 zip (once for each zip entry)
    verify(publisher, times(4)).publish(any(BufferedInputStream.class), anyString(), eq(ImporterFileType.LOG_FILE), any(LogFileParser.class));
  }

  @Test
  void testProcessDirectoryWithNestedDirectories(@Mock LogFileToAsn1CodecPublisher publisher)
      throws IOException, LogFileParserFactoryException, LogFileToAsn1CodecPublisherException {
    var testCaseDir = new File(dirToProcess + "/nestedDirectory");
    testCaseDir.mkdirs();
    createGZIP(testCaseDir.getAbsolutePath());

    ImporterProcessor importerProcessor = new ImporterProcessor(publisher, ImporterFileType.LOG_FILE, 1024);
    int result = importerProcessor.processDirectory(testCaseDir.toPath(), backupDir, failureDir);
    assertEquals(1, result);
    verify(publisher, times(1)).publish(any(BufferedInputStream.class), anyString(), eq(ImporterFileType.LOG_FILE), any(LogFileParser.class));
  }

  @Test
  void testProcessDirectoryWithEmptyDirectory(@Mock LogFileToAsn1CodecPublisher publisher) {
    var emptyDir = new File(dirToProcess + "/empty");
    emptyDir.mkdirs();
    ImporterProcessor importerProcessor = new ImporterProcessor(publisher, ImporterFileType.LOG_FILE, 1024);

    int result = importerProcessor.processDirectory(emptyDir.toPath(), backupDir, failureDir);

    assertEquals(0, result);
    verifyNoInteractions(publisher);
  }

  private void createZip(String parentDir) throws IOException {
    var firstFileForProcessing = new File("%s/%s-%s.uper".formatted(parentDir, bsmTx.name(), UUID.randomUUID().toString()));
    var os = new FileOutputStream(firstFileForProcessing);
    os.write("test".getBytes());
    os.flush();
    os.close();

    var secondFileForProcessing = new File("%s/%s-%s.uper".formatted(parentDir, bsmTx.name(), UUID.randomUUID().toString()));
    os = new FileOutputStream(secondFileForProcessing);
    os.write("test2".getBytes());
    os.flush();
    os.close();

    var zipFileForProcessing = new File("%s/%s-%s.zip".formatted(parentDir, bsmTx.name(), UUID.randomUUID().toString()));
    assertTrue(zipFileForProcessing.createNewFile());
    zipFileForProcessing.deleteOnExit();
    var zipOutputStream = new java.util.zip.ZipOutputStream(new FileOutputStream(zipFileForProcessing));
    zipOutputStream.putNextEntry(new java.util.zip.ZipEntry("first-bsmTx.uper"));
    zipOutputStream.write("test".getBytes());
    zipOutputStream.closeEntry();
    zipOutputStream.putNextEntry(new java.util.zip.ZipEntry("second-bsmTx.uper"));
    zipOutputStream.write("test2".getBytes());
    zipOutputStream.closeEntry();
    zipOutputStream.finish();
    zipOutputStream.close();

    // now that we have the zip file, delete the unzipped files
    firstFileForProcessing.delete();
    secondFileForProcessing.delete();
  }

  private void createGZIP(String parentDir) throws IOException {
    var fileForProcessing = new File("%s/%s-%s.gz".formatted(parentDir, bsmTx.name(), UUID.randomUUID().toString()));
    assertTrue(fileForProcessing.createNewFile());
    fileForProcessing.deleteOnExit();
    GZIPOutputStream os = new GZIPOutputStream(new FileOutputStream(fileForProcessing));
    os.write("test".getBytes());
    os.finish();
    os.close();
  }

  private void createPlaintext(String parentDir) throws IOException {
    var fileForProcessing = new File("%s/%s-%s.txt".formatted(parentDir, bsmTx.name(), UUID.randomUUID().toString()));
    assertTrue(fileForProcessing.createNewFile());
    fileForProcessing.deleteOnExit();
    var os = new FileOutputStream(fileForProcessing);
    os.write("test".getBytes());
    os.flush();
    os.close();
  }
}
