package us.dot.its.jpo.ode.coder;

import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.TimLogFileParser;
import us.dot.its.jpo.ode.model.OdeAsn1Metadata;
import us.dot.its.jpo.ode.model.OdeAsn1WithBsmMetadata;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;

public class OdeLogMetadataCreatorHelper {

   private OdeLogMetadataCreatorHelper() {
   }

   public static void updateLogMetadata(OdeLogMetadata metadata, LogFileParser logFileParser) {

      if (logFileParser != null) {
         metadata.setLogFileName(logFileParser.getFilename());
         metadata.setRecordType(logFileParser.getRecordType());
         metadata.setRecordGeneratedAt(logFileParser.getGeneratedAt().toString());
         metadata.setSecurityResultCode(logFileParser.getSecurityResultCode());
         if (logFileParser instanceof BsmLogFileParser &&
               metadata instanceof OdeAsn1WithBsmMetadata) {
            BsmLogFileParser bsmLogFileParser = (BsmLogFileParser) logFileParser;
            OdeAsn1WithBsmMetadata odeAsn1WithBsmMetadata = (OdeAsn1WithBsmMetadata) metadata;
            odeAsn1WithBsmMetadata.setBsmSource(bsmLogFileParser.getBsmSource());
         } else if (logFileParser instanceof TimLogFileParser &&
               metadata instanceof OdeAsn1Metadata) {
            ReceivedMessageDetails receivedMsgDetails = 
                  TimDecoderHelper.buildReceivedMessageDetails((TimLogFileParser) logFileParser);
            OdeAsn1Metadata odeAsn1Metadata = (OdeAsn1Metadata) metadata;
            odeAsn1Metadata.setReceivedMessageDetails(receivedMsgDetails);
         }
      }

      metadata.setRecordGeneratedBy(GeneratedBy.OBU);
      metadata.getSerialId().addRecordId(1);
   }

}
