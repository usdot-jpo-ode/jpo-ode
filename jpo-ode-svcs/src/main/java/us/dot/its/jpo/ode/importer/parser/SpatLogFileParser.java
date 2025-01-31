package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeSpatMetadata.SpatSource;

/**
 * SpatLogFileParser is responsible for parsing specific SPaT (Signal Phase and Timing)
 * log files. This class extends the generic functionality of LogFileParser and adds
 * additional parsing mechanisms for SPaT-related attributes, including SPaT sources
 * and certificate presence indicators.
 *
 * <p>The parsing process involves multiple steps, each handled sequentially to extract
 * critical information from the file stream. The extracted data is processed and made
 * available through defined methods in this class.
 *
 * <p>Key parsing steps include:
 * <ul>
 *  <li>Extracting the SPaT source.</li>
 *  <li>Parsing intersection data using the IntersectionParser.</li>
 *  <li>Parsing time information using the TimeParser.</li>
 *  <li>Parsing security result codes using the SecurityResultCodeParser.</li>
 *  <li>Determining certificate presence indicators.</li>
 *  <li>Parsing payload data using the PayloadParser.</li>
 * </ul>
 */
public class SpatLogFileParser extends LogFileParser {
  private static final Logger logger = LoggerFactory.getLogger(SpatLogFileParser.class.getName());
  private static final int RX_FROM_LENGTH = 1;
  /*ieee 1609 (acceptable values 0 = no,1 =yes by default the Cert shall be present)*/
  private static final int IS_CERT_PRESENT_LENGTH = 1;

  private SpatSource spatSource;
  private boolean isCertPresent;

  /**
   * Constructs a SpatLogFileParser object, which is responsible for parsing SPaT
   * (Signal Phase and Timing) log files.
   */
  public SpatLogFileParser(OdeLogMetadata.RecordType recordType, String filename) {
    super(recordType, filename);
    setIntersectionParser(new IntersectionParser(recordType, filename));
    setTimeParser(new TimeParser(recordType, filename));
    setSecResCodeParser(new SecurityResultCodeParser(recordType, filename));
    setPayloadParser(new PayloadParser(recordType, filename));
  }

  @Override
  public ParserStatus parseFile(BufferedInputStream bis) throws FileParserException {
    ParserStatus status;
    try {
      status = super.parseFile(bis);
      if (status != ParserStatus.COMPLETE) {
        return status;
      }

      if (getStep() == 1) {
        status = parseStep(bis, RX_FROM_LENGTH);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
        setSpatSource(readBuffer);
      }

      if (getStep() == 2) {
        status = nextStep(bis, intersectionParser);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
      }

      if (getStep() == 3) {
        status = nextStep(bis, timeParser);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
      }

      if (getStep() == 4) {
        status = nextStep(bis, secResCodeParser);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
      }

      if (getStep() == 5) {
        status = parseStep(bis, IS_CERT_PRESENT_LENGTH);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
        setCertPresent(readBuffer);
      }

      if (getStep() == 6) {
        status = nextStep(bis, payloadParser);
        if (status != ParserStatus.COMPLETE) {
          return status;
        }
      }

      resetStep();
      status = ParserStatus.COMPLETE;

    } catch (Exception e) {
      throw new FileParserException("Error parsing " + getFilename(), e);
    }

    return status;
  }


  public SpatSource getSpatSource() {
    return spatSource;
  }

  public void setSpatSource(SpatSource spatSource) {
    this.spatSource = spatSource;
  }

  protected void setSpatSource(byte[] code) {
    try {
      setSpatSource(SpatSource.values()[code[0]]);
    } catch (Exception e) {
      logger.error("Invalid SpatSource: {}. Valid values are {}-{} inclusive", code, 0, SpatSource.values());
      setSpatSource(SpatSource.unknown);
    }
  }

  public boolean isCertPresent() {
    return isCertPresent;
  }

  private void setCertPresent(boolean isCertPresent) {
    this.isCertPresent = isCertPresent;
  }

  private void setCertPresent(byte[] code) {
    try {
      setCertPresent(code[0] != 0);
    } catch (Exception e) {
      logger.error("Invalid Certificate Presence indicator: {}. Valid values are {}-{} inclusive", code, 0, SpatSource.values());
      setSpatSource(SpatSource.unknown);
    }
  }

  @Override
  public void writeTo(OutputStream os) throws IOException {
    os.write((byte) spatSource.ordinal());
    super.writeTo(os);
  }
}
