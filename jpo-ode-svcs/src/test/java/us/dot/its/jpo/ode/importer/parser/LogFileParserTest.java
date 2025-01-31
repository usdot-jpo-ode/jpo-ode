package us.dot.its.jpo.ode.importer.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import us.dot.its.jpo.ode.importer.parser.FileParser.FileParserException;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.RxSource;

class LogFileParserTest {

  private static final String GZ = ".gz";

  @Test
  void testFactory_bsmTx() throws LogFileParserFactory.LogFileParserFactoryException {
    RecordType recordType = RecordType.bsmTx;
    String filename = recordType.name();
    LogFileParser parser = LogFileParserFactory.getLogFileParser(filename);

    assertInstanceOf(BsmLogFileParser.class, parser);
    assertEquals(recordType, parser.getRecordType());
  }

  @Test
  void testFactory_bsmLogDuringEvent() throws LogFileParserFactory.LogFileParserFactoryException {
    RecordType recordType = RecordType.bsmLogDuringEvent;
    String filename = recordType.name();
    LogFileParser parser = LogFileParserFactory.getLogFileParser(filename);

    assertInstanceOf(BsmLogFileParser.class, parser);
    assertEquals(recordType, parser.getRecordType());
  }

  @Test
  void testFactory_rxMsg() throws LogFileParserFactory.LogFileParserFactoryException {
    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name();
    LogFileParser parser = LogFileParserFactory.getLogFileParser(filename);

    assertInstanceOf(RxMsgFileParser.class, parser);
    assertEquals(recordType, parser.getRecordType());
  }

  @Test
  void testFactory_dnMsg() throws LogFileParserFactory.LogFileParserFactoryException {
    RecordType recordType = RecordType.dnMsg;
    String filename = recordType.name();
    LogFileParser parser = LogFileParserFactory.getLogFileParser(filename);

    assertInstanceOf(DistressMsgFileParser.class, parser);
    assertEquals(recordType, parser.getRecordType());
  }

  @Test
  void testFactory_driverAlert() throws LogFileParserFactory.LogFileParserFactoryException {
    RecordType recordType = RecordType.driverAlert;
    String filename = recordType.name();
    LogFileParser parser = LogFileParserFactory.getLogFileParser(filename);

    assertInstanceOf(DriverAlertFileParser.class, parser);
    assertEquals(recordType, parser.getRecordType());
  }

  @Test
  void testFactoryThrowsException() {
    assertThrows(LogFileParserFactory.LogFileParserFactoryException.class, () -> {
      LogFileParserFactory.getLogFileParser("invalidFileName");
      fail("Expected IllegalArgumentException");
    });
  }

  @Test
  void testUpdateMetadata_bsmLogDuringEvent() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x00,                                     //1. direction
        (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, //2.0 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, //2.1 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, //2.3 elevation
        (byte) 0x04, (byte) 0x00,                         //2.3 speed
        (byte) 0x09, (byte) 0x27,                         //2.4 heading
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //3. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //4. mSec
        (byte) 0x00,                                     //5. securityResultCode
        (byte) 0x06, (byte) 0x00,                         //6.0 payloadLength
        //6.1 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
    };
    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.bsmLogDuringEvent;
    String filename = recordType.name() + GZ;
    BsmLogFileParser parser = (BsmLogFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);

    OdeBsmMetadata metadata = new OdeBsmMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(BsmSource.EV, metadata.getBsmSource());
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.NA, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  void testUpdateMetadata_bsmTx() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x00,                                     //1. direction
        (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, //2.0 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, //2.1 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, //2.3 elevation
        (byte) 0x04, (byte) 0x00,                         //2.3 speed
        (byte) 0x09, (byte) 0x27,                         //2.4 heading
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //3. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //4. mSec
        (byte) 0x00,                                     //5. securityResultCode
        (byte) 0x06, (byte) 0x00,                         //6.0 payloadLength
        //6.1 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
    };
    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.bsmTx;
    String filename = recordType.name() + GZ;
    BsmLogFileParser parser = (BsmLogFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);

    OdeBsmMetadata metadata = new OdeBsmMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(BsmSource.EV, metadata.getBsmSource());
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.NA, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  void testUpdateMetadata_dnMsg() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, //1.1 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, //1.2 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, //1.3 elevation
        (byte) 0x04, (byte) 0x00,                         //1.4 speed
        (byte) 0x09, (byte) 0x27,                         //1.5 heading
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //2. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //3. mSec
        (byte) 0x00,                                     //4. securityResultCode
        (byte) 0x06, (byte) 0x00,                         //5.1 payloadLength
        //5.2 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
    };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.dnMsg;
    String filename = recordType.name() + GZ;
    DistressMsgFileParser parser = (DistressMsgFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);

    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.NA, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  void testUpdateMetadata_driverAlert() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, //1.0 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, //1.1 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, //1.2 elevation
        (byte) 0x04, (byte) 0x00,                         //1.3 speed
        (byte) 0x09, (byte) 0x27,                         //1.4 heading
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //2. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //3. mSec
        (byte) 0x11, (byte) 0x00,                         //4.0 payloadLength
        //4.1 payload
        'T', 'e', 's', 't', ' ', 'D', 'r', 'i', 'v', 'e', 'r', ' ', 'A', 'l', 'e', 'r', 't'
    };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.driverAlert;
    String filename = recordType.name() + GZ;
    DriverAlertFileParser parser = (DriverAlertFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);

    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.NA, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  void testUpdateMetadata_rxMsgBsm() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x02,                                     //1. RxSource = RV
        (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, //2.0 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, //2.1 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, //2.3 elevation
        (byte) 0x04, (byte) 0x00,                         //2.3 speed
        (byte) 0x09, (byte) 0x27,                         //2.4 heading
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //3. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //4. mSec
        (byte) 0x00,                                     //5. securityResultCode
        (byte) 0x06, (byte) 0x00,                         //6.0 payloadLength
        //6.1 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
    };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);

    OdeBsmMetadata metadata = new OdeBsmMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.RV, metadata.getReceivedMessageDetails().getRxSource());
    assertEquals(BsmSource.RV, metadata.getBsmSource());
  }

  @Test
  void testUpdateMetadata_rxMsgTimRSU() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x00,                                     //1. RxSource = RSU
        (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, //2.0 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, //2.1 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, //2.3 elevation
        (byte) 0x04, (byte) 0x00,                         //2.3 speed
        (byte) 0x09, (byte) 0x27,                         //2.4 heading
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //3. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //4. mSec
        (byte) 0x00,                                     //5. securityResultCode
        (byte) 0x06, (byte) 0x00,                         //6.0 payloadLength
        //6.1 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
    };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);

    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.RSU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.RSU, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  void testUpdateMetadata_rxMsgTimSAT() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x01,                                     //1. RxSource = SAT
        (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, //2.0 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, //2.1 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, //2.3 elevation
        (byte) 0x04, (byte) 0x00,                         //2.3 speed
        (byte) 0x09, (byte) 0x27,                         //2.4 heading
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //3. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //4. mSec
        (byte) 0x00,                                     //5. securityResultCode
        (byte) 0x06, (byte) 0x00,                         //6.0 payloadLength
        //6.1 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
    };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);

    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.TMC_VIA_SAT, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.SAT, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  void testUpdateMetadata_rxMsgTimSNMP() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x03,                                     //1. RxSource = SNMP
        (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, //2.0 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, //2.1 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, //2.3 elevation
        (byte) 0x04, (byte) 0x00,                         //2.3 speed
        (byte) 0x09, (byte) 0x27,                         //2.4 heading
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //3. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //4. mSec
        (byte) 0x00,                                     //5. securityResultCode
        (byte) 0x06, (byte) 0x00,                         //6.0 payloadLength
        //6.1 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
    };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);

    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.TMC_VIA_SNMP, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.SNMP, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  void testUpdateMetadata_rxMsgTimUnknownRxSource() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x09,                                     //1. RxSource = unknown value
        (byte) 0x6f, (byte) 0x75, (byte) 0x4d, (byte) 0x19, //2.0 latitude
        (byte) 0xa4, (byte) 0xa1, (byte) 0x5c, (byte) 0xce, //2.1 longitude
        (byte) 0x67, (byte) 0x06, (byte) 0x00, (byte) 0x00, //2.3 elevation
        (byte) 0x04, (byte) 0x00,                         //2.3 speed
        (byte) 0x09, (byte) 0x27,                         //2.4 heading
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //3. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //4. mSec
        (byte) 0x00,                                     //5. securityResultCode
        (byte) 0x06, (byte) 0x00,                         //6.0 payloadLength
        //6.1 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
    };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);

    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.UNKNOWN, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.UNKNOWN, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  void testUpdateMetadata_rxMsgTimNullLocations() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x09,                                     //1. RxSource = unknown value
        (byte) 0x01, (byte) 0xE9, (byte) 0xA4, (byte) 0x35, //2.0 latitude unavailable
        (byte) 0x01, (byte) 0xD2, (byte) 0x49, (byte) 0x6B, //2.1 longitude unavailable
        (byte) 0x00, (byte) 0xF0, (byte) 0xFF, (byte) 0xFF, //2.3 elevation unavailable
        (byte) 0xFF, (byte) 0x1F,                         //2.3 speed unavailable
        (byte) 0x80, (byte) 0x70,                         //2.4 heading unavailable
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //3. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //4. mSec
        (byte) 0x00,                                     //5. securityResultCode
        (byte) 0x06, (byte) 0x00,                         //6.0 payloadLength
        //6.1 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
    };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);

    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.UNKNOWN, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.UNKNOWN, metadata.getReceivedMessageDetails().getRxSource());
    assertNull(metadata.getReceivedMessageDetails().getLocationData().getElevation());
    assertNull(metadata.getReceivedMessageDetails().getLocationData().getHeading());
    assertNull(metadata.getReceivedMessageDetails().getLocationData().getLatitude());
    assertNull(metadata.getReceivedMessageDetails().getLocationData().getLongitude());
    assertNull(metadata.getReceivedMessageDetails().getLocationData().getSpeed());
  }

  @Test
  void testUpdateMetadata_rxMsgNullReceivedMessageDetails() throws FileParserException, LogFileParserFactory.LogFileParserFactoryException {
    byte[] buf = new byte[] {
        (byte) 0x09,                                     //1. RxSource = unknown value
        (byte) 0x01, (byte) 0xE9, (byte) 0xA4, (byte) 0x35, //2.0 latitude unavailable
        (byte) 0x01, (byte) 0xD2, (byte) 0x49, (byte) 0x6B, //2.1 longitude unavailable
        (byte) 0x00, (byte) 0xF0, (byte) 0xFF, (byte) 0xFF, //2.3 elevation unavailable
        (byte) 0xFF, (byte) 0x1F,                         //2.3 speed unavailable
        (byte) 0x80, (byte) 0x70,                         //2.4 heading unavailable
        (byte) 0xa9, (byte) 0x2c, (byte) 0xe2, (byte) 0x5a, //3. utcTimeInSec
        (byte) 0x8f, (byte) 0x01,                         //4. mSec
        (byte) 0x00,                                     //5. securityResultCode
        (byte) 0x06, (byte) 0x00,                         //6.0 payloadLength
        //6.1 payload
        (byte) 0x03, (byte) 0x81, (byte) 0x00, (byte) 0x40, (byte) 0x03, (byte) 0x80
    };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParserFactory.getLogFileParser(filename);
    ParserStatus status = parser.parseFile(bis);
    parser.setLocationParser(null);

    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);

    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertNull(metadata.getReceivedMessageDetails());
  }
}
