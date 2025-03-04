package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.util.CodecUtils;

/**
 * The IntersectionParser class extends the LogFileParser and provides
 * specific functionalities for parsing intersection-related information
 * from log files. It parses and extracts the intersection ID and intersection
 * status, encapsulating the data into a LogIntersection object.
 */
public class IntersectionParser extends LogFileParser {
  public static final int INTERSECTION_ID_LENGTH = 2;
  public static final int INTERSECTION_STATUS_LENGTH = 1;

  protected LogIntersection intersection;

  public IntersectionParser(OdeLogMetadata.RecordType recordType, String filename) {
    super(recordType, filename);
  }

  @Override
  public ParserStatus parseFile(BufferedInputStream bis) throws FileParserException {

    ParserStatus status = ParserStatus.INIT;

    try {
      this.intersection = new LogIntersection();

      // Step 1 - parse intersection.intersectionStatus
      if (getStep() == 0) {
        status = parseStep(bis, INTERSECTION_ID_LENGTH);
        if (status != ParserStatus.ENTRY_PARSING_COMPLETE) {
          return status;
        }
        intersection.setIntersectionId(
            CodecUtils.bytesToShort(readBuffer, 0, INTERSECTION_ID_LENGTH, ByteOrder.LITTLE_ENDIAN));
      }

      // Step 2 - parse intersection.intersectionId
      if (getStep() == 1) {
        status = parseStep(bis, INTERSECTION_STATUS_LENGTH);
        if (status != ParserStatus.ENTRY_PARSING_COMPLETE) {
          return status;
        }
        intersection.setIntersectionStatus(readBuffer[0]);
      }

      resetStep();
      status = ParserStatus.ENTRY_PARSING_COMPLETE;
    } catch (Exception e) {
      throw new FileParserException(String.format("Error parsing %s on step %d", getFilename(), getStep()), e);
    }

    return status;
  }

  @Override
  public void writeTo(OutputStream os) throws IOException {
    os.write(CodecUtils.intToBytes(intersection.getIntersectionId(), ByteOrder.LITTLE_ENDIAN));
    os.write(CodecUtils.intToBytes(intersection.getIntersectionStatus(), ByteOrder.LITTLE_ENDIAN));
  }
}
