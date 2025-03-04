package us.dot.its.jpo.ode.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileFormatTest {

  @Test
  void detectFileFormat_GZIP(@Mock Path filePath) throws IOException {
    doReturn("mockfile.gz").when(filePath).toString();
    var format = FileFormat.detectFileFormat(filePath);
    assertEquals(FileFormat.GZIP, format);
  }

  @Test
  void detectFileFormat_ZIP(@Mock Path filePath) throws IOException {
    doReturn("mockfile.zip").when(filePath).toString();
    var format = FileFormat.detectFileFormat(filePath);
    assertEquals(FileFormat.ZIP, format);
  }

  @Test
  void detectFileFormat_UNKNOWN(@Mock Path filePath) throws IOException {
    doReturn("mockfile.json").when(filePath).toString();
    var format = FileFormat.detectFileFormat(filePath);
    assertEquals(FileFormat.UNKNOWN, format);
  }
}