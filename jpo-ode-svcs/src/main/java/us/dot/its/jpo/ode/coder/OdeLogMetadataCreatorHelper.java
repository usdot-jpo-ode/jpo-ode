package us.dot.its.jpo.ode.coder;

import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;

public class OdeLogMetadataCreatorHelper {

   private OdeLogMetadataCreatorHelper() {
   }

   public static void updateLogMetadata(OdeLogMetadata metadata, LogFileParser logFileParser) {

      if (logFileParser != null) {
         metadata.setLogFileName(logFileParser.getFilename());
         metadata.setRecordType(logFileParser.getRecordType().name());
         metadata.setRecordGeneratedAt(logFileParser.getGeneratedAt().toString());
         metadata.setValidSignature(logFileParser.isValidSignature());
      } else {
         /*
          * TODO Temporarily put in place for testing CV PEP. Should be removed
          * after testing is complete.
          */
         metadata.setRecordGeneratedAt(metadata.getReceivedAt());
      }

      metadata.setRecordGeneratedBy(GeneratedBy.OBU);
      metadata.getSerialId().addRecordId(1);
   }

}
