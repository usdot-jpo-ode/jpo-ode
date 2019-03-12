package us.dot.its.jpo.ode.importer.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import org.junit.Test;

import us.dot.its.jpo.ode.importer.parser.FileParser.FileParserException;
import us.dot.its.jpo.ode.importer.parser.FileParser.ParserStatus;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeLogMetadata.RecordType;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.RxSource;

public class LogFileParserTest {

  private static final String GZ = ".gz";

  @Test
  public void testFactory_bsmTx() {
    RecordType recordType = RecordType.bsmTx;
    String filename = recordType.name();
    LogFileParser parser = LogFileParser.factory(filename);

    assertTrue(parser instanceof BsmLogFileParser);
    assertEquals(recordType, parser.getRecordType());
  }

  @Test
  public void testFactory_bsmLogDuringEvent() {
    RecordType recordType = RecordType.bsmLogDuringEvent;
    String filename = recordType.name();
    LogFileParser parser = LogFileParser.factory(filename);

    assertTrue(parser instanceof BsmLogFileParser);
    assertEquals(recordType, parser.getRecordType());
  }

  @Test
  public void testFactory_rxMsg() {
    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name();
    LogFileParser parser = LogFileParser.factory(filename);

    assertTrue(parser instanceof RxMsgFileParser);
    assertEquals(recordType, parser.getRecordType());
  }

  @Test
  public void testFactory_dnMsg() {
    RecordType recordType = RecordType.dnMsg;
    String filename = recordType.name();
    LogFileParser parser = LogFileParser.factory(filename);

    assertTrue(parser instanceof DistressMsgFileParser);
    assertEquals(recordType, parser.getRecordType());
  }

  @Test
  public void testFactory_driverAlert() {
    RecordType recordType = RecordType.driverAlert;
    String filename = recordType.name();
    LogFileParser parser = LogFileParser.factory(filename);

    assertTrue(parser instanceof DriverAlertFileParser);
    assertEquals(recordType, parser.getRecordType());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFactoryThrowsException() {
    LogFileParser.factory("invalidFileName");
    fail("Expected IllegalArgumentException");
  }

  @Test
  public void testUpdateMetadata_bsmLogDuringEvent() throws FileParserException {
    byte[] buf = new byte[] { 
        (byte)0x00,                                     //1. direction 
        (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //2.0 latitude
        (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //2.1 longitude
        (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2.3 elevation
        (byte)0x04, (byte)0x00,                         //2.3 speed
        (byte)0x09, (byte)0x27,                         //2.4 heading
        (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //3. utcTimeInSec
        (byte)0x8f, (byte)0x01,                         //4. mSec
        (byte)0x00,                                     //5. securityResultCode
        (byte)0x06, (byte)0x00,                         //6.0 payloadLength
                                                        //6.1 payload
        (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
        };
    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.bsmLogDuringEvent;
    String filename = recordType.name() + GZ;
    BsmLogFileParser parser = (BsmLogFileParser) LogFileParser.factory(filename);
    ParserStatus status = parser.parseFile(bis, filename);
    
    OdeBsmMetadata metadata = new OdeBsmMetadata();
    parser.updateMetadata(metadata);
    
    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(BsmSource.EV, metadata.getBsmSource());
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.NA, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  public void testUpdateMetadata_bsmTx() throws FileParserException {
    byte[] buf = new byte[] { 
        (byte)0x00,                                     //1. direction 
        (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //2.0 latitude
        (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //2.1 longitude
        (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2.3 elevation
        (byte)0x04, (byte)0x00,                         //2.3 speed
        (byte)0x09, (byte)0x27,                         //2.4 heading
        (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //3. utcTimeInSec
        (byte)0x8f, (byte)0x01,                         //4. mSec
        (byte)0x00,                                     //5. securityResultCode
        (byte)0x06, (byte)0x00,                         //6.0 payloadLength
                                                        //6.1 payload
        (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
        };
    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.bsmTx;
    String filename = recordType.name() + GZ;
    BsmLogFileParser parser = (BsmLogFileParser) LogFileParser.factory(filename);
    ParserStatus status = parser.parseFile(bis, filename);
    
    OdeBsmMetadata metadata = new OdeBsmMetadata();
    parser.updateMetadata(metadata);
    
    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(BsmSource.EV, metadata.getBsmSource());
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.NA, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  public void testUpdateMetadata_dnMsg() throws FileParserException {
    byte[] buf = new byte[] { 
        (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.1 latitude
        (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.2 longitude
        (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.3 elevation
        (byte)0x04, (byte)0x00,                         //1.4 speed
        (byte)0x09, (byte)0x27,                         //1.5 heading
        (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
        (byte)0x8f, (byte)0x01,                         //3. mSec
        (byte)0x00,                                     //4. securityResultCode
        (byte)0x06, (byte)0x00,                         //5.1 payloadLength
                                                        //5.2 payload
        (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
        };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.dnMsg;
    String filename = recordType.name() + GZ;
    DistressMsgFileParser parser = (DistressMsgFileParser) LogFileParser.factory(filename);
    ParserStatus status = parser.parseFile(bis, filename);
    
    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);
    
    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.NA, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  public void testUpdateMetadata_driverAlert() throws FileParserException {
    byte[] buf = new byte[] { 
        (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //1.0 latitude
        (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //1.1 longitude
        (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //1.2 elevation
        (byte)0x04, (byte)0x00,                         //1.3 speed
        (byte)0x09, (byte)0x27,                         //1.4 heading
        (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //2. utcTimeInSec
        (byte)0x8f, (byte)0x01,                         //3. mSec
        (byte)0x11, (byte)0x00,                         //4.0 payloadLength
                                                        //4.1 payload
        'T', 'e', 's', 't', ' ', 'D', 'r', 'i', 'v', 'e', 'r', ' ', 'A', 'l', 'e', 'r', 't' 
        };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.driverAlert;
    String filename = recordType.name() + GZ;
    DriverAlertFileParser parser = (DriverAlertFileParser) LogFileParser.factory(filename);
    ParserStatus status = parser.parseFile(bis, filename);
    
    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);
    
    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.NA, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  public void testUpdateMetadata_rxMsgBsm() throws FileParserException {
    byte[] buf = new byte[] { 
        (byte)0x02,                                     //1. RxSource = RV 
        (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //2.0 latitude
        (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //2.1 longitude
        (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2.3 elevation
        (byte)0x04, (byte)0x00,                         //2.3 speed
        (byte)0x09, (byte)0x27,                         //2.4 heading
        (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //3. utcTimeInSec
        (byte)0x8f, (byte)0x01,                         //4. mSec
        (byte)0x00,                                     //5. securityResultCode
        (byte)0x06, (byte)0x00,                         //6.0 payloadLength
                                                        //6.1 payload
        (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
        };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParser.factory(filename);
    ParserStatus status = parser.parseFile(bis, filename);
    
    OdeBsmMetadata metadata = new OdeBsmMetadata();
    parser.updateMetadata(metadata);
    
    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.RV, metadata.getReceivedMessageDetails().getRxSource());
    assertEquals(BsmSource.RV, metadata.getBsmSource());
  }
  
  @Test
  public void testUpdateMetadata_rxMsgTimSAT() throws FileParserException {
    byte[] buf = new byte[] { 
        (byte)0x01,                                     //1. RxSource = SAT
        (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //2.0 latitude
        (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //2.1 longitude
        (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2.3 elevation
        (byte)0x04, (byte)0x00,                         //2.3 speed
        (byte)0x09, (byte)0x27,                         //2.4 heading
        (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //3. utcTimeInSec
        (byte)0x8f, (byte)0x01,                         //4. mSec
        (byte)0x00,                                     //5. securityResultCode
        (byte)0x06, (byte)0x00,                         //6.0 payloadLength
                                                        //6.1 payload
        (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
        };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParser.factory(filename);
    ParserStatus status = parser.parseFile(bis, filename);
    
    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);
    
    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.TMC_VIA_SAT, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.SAT, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  public void testUpdateMetadata_rxMsgTimSNMP() throws FileParserException {
    byte[] buf = new byte[] { 
        (byte)0x03,                                     //1. RxSource = SNMP
        (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //2.0 latitude
        (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //2.1 longitude
        (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2.3 elevation
        (byte)0x04, (byte)0x00,                         //2.3 speed
        (byte)0x09, (byte)0x27,                         //2.4 heading
        (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //3. utcTimeInSec
        (byte)0x8f, (byte)0x01,                         //4. mSec
        (byte)0x00,                                     //5. securityResultCode
        (byte)0x06, (byte)0x00,                         //6.0 payloadLength
                                                        //6.1 payload
        (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
        };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParser.factory(filename);
    ParserStatus status = parser.parseFile(bis, filename);
    
    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);
    
    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.TMC_VIA_SNMP, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.SNMP, metadata.getReceivedMessageDetails().getRxSource());
  }

  @Test
  public void testUpdateMetadata_rxMsgTimUnknownRxSource() throws FileParserException {
    byte[] buf = new byte[] { 
        (byte)0x04,                                     //1. RxSource = unknown value
        (byte)0x6f, (byte)0x75, (byte)0x4d, (byte)0x19, //2.0 latitude
        (byte)0xa4, (byte)0xa1, (byte)0x5c, (byte)0xce, //2.1 longitude
        (byte)0x67, (byte)0x06, (byte)0x00, (byte)0x00, //2.3 elevation
        (byte)0x04, (byte)0x00,                         //2.3 speed
        (byte)0x09, (byte)0x27,                         //2.4 heading
        (byte)0xa9, (byte)0x2c, (byte)0xe2, (byte)0x5a, //3. utcTimeInSec
        (byte)0x8f, (byte)0x01,                         //4. mSec
        (byte)0x00,                                     //5. securityResultCode
        (byte)0x06, (byte)0x00,                         //6.0 payloadLength
                                                        //6.1 payload
        (byte)0x03, (byte)0x81, (byte)0x00, (byte)0x40, (byte)0x03, (byte)0x80 
        };

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(buf));

    RecordType recordType = RecordType.rxMsg;
    String filename = recordType.name() + GZ;
    RxMsgFileParser parser = (RxMsgFileParser) LogFileParser.factory(filename);
    ParserStatus status = parser.parseFile(bis, filename);
    
    OdeLogMetadata metadata = new OdeLogMetadata();
    parser.updateMetadata(metadata);
    
    assertEquals(ParserStatus.COMPLETE, status);
    assertEquals(GeneratedBy.OBU, metadata.getRecordGeneratedBy());
    assertEquals(RxSource.NA, metadata.getReceivedMessageDetails().getRxSource());
  }

}
