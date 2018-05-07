package us.dot.its.jpo.ode.coder;

import us.dot.its.jpo.ode.importer.parser.BsmLogFileParser;
import us.dot.its.jpo.ode.importer.parser.LocationParser;
import us.dot.its.jpo.ode.importer.parser.LogFileParser;
import us.dot.its.jpo.ode.importer.parser.LogLocation;
import us.dot.its.jpo.ode.importer.parser.RxMsgFileParser;
import us.dot.its.jpo.ode.model.OdeBsmMetadata;
import us.dot.its.jpo.ode.model.OdeLogMetadata;
import us.dot.its.jpo.ode.model.OdeLogMsgMetadataLocation;
import us.dot.its.jpo.ode.model.OdeMsgMetadata.GeneratedBy;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.model.RxSource;
import us.dot.its.jpo.ode.model.OdeBsmMetadata.BsmSource;
import us.dot.its.jpo.ode.plugin.j2735.builders.ElevationBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.HeadingBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.LatitudeBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.LongitudeBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.SpeedOrVelocityBuilder;

public class OdeLogMetadataCreatorHelper {

   private OdeLogMetadataCreatorHelper() {
   }

   public static void updateLogMetadata(OdeLogMetadata metadata, LogFileParser logFileParser) {

      if (logFileParser != null) {
         metadata.setLogFileName(logFileParser.getFilename());
         metadata.setRecordType(logFileParser.getRecordType());
         metadata.setRecordGeneratedAt(logFileParser.getTimeParser().getGeneratedAt().toString());
         
         if (logFileParser.getSecResCodeParser() != null) {
            metadata.setSecurityResultCode(logFileParser.getSecResCodeParser().getSecurityResultCode());
         }
         
         metadata.setReceivedMessageDetails(buildReceivedMessageDetails(logFileParser));
         
         if (metadata instanceof OdeBsmMetadata) {
            OdeBsmMetadata odeBsmMetadata = (OdeBsmMetadata) metadata;
            BsmSource bsmSource = BsmSource.unknown; 
            if (logFileParser instanceof BsmLogFileParser) {
               BsmLogFileParser bsmLogFileParser = (BsmLogFileParser) logFileParser;
               bsmSource = bsmLogFileParser.getBsmSource();
            } else if (logFileParser instanceof RxMsgFileParser) {
               RxMsgFileParser rxMsgFileParser = (RxMsgFileParser) logFileParser;
               if (rxMsgFileParser.getRxSource() == RxSource.RV) {
                  bsmSource = BsmSource.RV;
               }
            }
            odeBsmMetadata.setBsmSource(bsmSource);
         }
      }

      metadata.setRecordGeneratedBy(GeneratedBy.OBU);
      metadata.getSerialId().addRecordId(1);
   }

   public static ReceivedMessageDetails buildReceivedMessageDetails(LogFileParser parser) {
      LocationParser locationParser = parser.getLocationParser();
      ReceivedMessageDetails rxMsgDetails = null;
      if (locationParser != null) {
         LogLocation locationDetails = locationParser.getLocation();
         rxMsgDetails = new ReceivedMessageDetails(
               new OdeLogMsgMetadataLocation(
                  LatitudeBuilder.genericLatitude(locationDetails.getLatitude()).stripTrailingZeros().toPlainString(),
                  LongitudeBuilder.genericLongitude(locationDetails.getLongitude()).stripTrailingZeros().toPlainString(),
                  ElevationBuilder.genericElevation(locationDetails.getElevation()).stripTrailingZeros().toPlainString(),
                  SpeedOrVelocityBuilder.genericSpeedOrVelocity(locationDetails.getSpeed()).stripTrailingZeros().toPlainString(),
                  HeadingBuilder.genericHeading(locationDetails.getHeading()).stripTrailingZeros().toPlainString()
                     ), null);
      }
      
      if (parser instanceof RxMsgFileParser && rxMsgDetails != null) {
         RxMsgFileParser rxMsgFileParser = (RxMsgFileParser) parser;
         rxMsgDetails.setRxSource(rxMsgFileParser.getRxSource());
      }
      return rxMsgDetails; 
    }

}
