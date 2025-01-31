package us.dot.its.jpo.ode.importer.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.model.OdeLogMetadata;

class LogFileParserFactoryTest {

  @Test
  void shouldReturnBsmTxLogFileParserWhenFileNameStartsWithBsmTx() throws LogFileParserFactory.LogFileParserFactoryException {
    // Arrange
    String fileName = "bsmTx_someFileName.log";

    // Act
    LogFileParser result = LogFileParserFactory.getLogFileParser(fileName);

    // Assert
    assertNotNull(result);
    assertInstanceOf(BsmLogFileParser.class, result);
    assertEquals(OdeLogMetadata.RecordType.bsmTx, result.getRecordType());
  }

  @Test
  void shouldReturnBsmLogDuringEventParserWhenFileNameStartsWithBsmLogDuringEvent() throws LogFileParserFactory.LogFileParserFactoryException {
    // Arrange
    String fileName = "bsmLogDuringEvent_someFileName.log";

    // Act
    LogFileParser result = LogFileParserFactory.getLogFileParser(fileName);

    // Assert
    assertNotNull(result);
    assertInstanceOf(BsmLogFileParser.class, result);
    assertEquals(OdeLogMetadata.RecordType.bsmLogDuringEvent, result.getRecordType());
  }

  @Test
  void shouldReturnRxMsgFileParserWhenFileNameStartsWithRxMsg() throws LogFileParserFactory.LogFileParserFactoryException {
    // Arrange
    String fileName = "rxMsg_someFileName.log";

    // Act
    LogFileParser result = LogFileParserFactory.getLogFileParser(fileName);

    // Assert
    assertNotNull(result);
    assertInstanceOf(RxMsgFileParser.class, result);
    assertEquals(OdeLogMetadata.RecordType.rxMsg, result.getRecordType());
  }

  @Test
  void shouldReturnDistressMsgFileParserWhenFileNameStartsWithDnMsg() throws LogFileParserFactory.LogFileParserFactoryException {
    // Arrange
    String fileName = "dnMsg_someFileName.log";

    // Act
    LogFileParser result = LogFileParserFactory.getLogFileParser(fileName);

    // Assert
    assertNotNull(result);
    assertInstanceOf(DistressMsgFileParser.class, result);
    assertEquals(OdeLogMetadata.RecordType.dnMsg, result.getRecordType());
  }

  @Test
  void shouldReturnDriverAlertFileParserWhenFileNameStartsWithDriverAlert() throws LogFileParserFactory.LogFileParserFactoryException {
    // Arrange
    String fileName = "driverAlert_someFileName.log";

    // Act
    LogFileParser result = LogFileParserFactory.getLogFileParser(fileName);

    // Assert
    assertNotNull(result);
    assertInstanceOf(DriverAlertFileParser.class, result);
    assertEquals(OdeLogMetadata.RecordType.driverAlert, result.getRecordType());
  }

  @Test
  void shouldReturnSpatLogFileParserWhenFileNameStartsWithSpatTx() throws LogFileParserFactory.LogFileParserFactoryException {
    // Arrange
    String fileName = "spatTx_someFileName.log";

    // Act
    LogFileParser result = LogFileParserFactory.getLogFileParser(fileName);

    // Assert
    assertNotNull(result);
    assertInstanceOf(SpatLogFileParser.class, result);
    assertEquals(OdeLogMetadata.RecordType.spatTx, result.getRecordType());
  }

  @Test
  void shouldThrowExceptionWhenFileNameDoesNotMatchAnyRecordType() {
    // Arrange
    String fileName = "unknown_someFileName.log";

    // Act & Assert
    LogFileParserFactory.LogFileParserFactoryException exception = assertThrows(
        LogFileParserFactory.LogFileParserFactoryException.class,
        () -> LogFileParserFactory.getLogFileParser(fileName)
    );
    assertEquals("Unknown log file prefix: " + fileName, exception.getMessage());
  }
}