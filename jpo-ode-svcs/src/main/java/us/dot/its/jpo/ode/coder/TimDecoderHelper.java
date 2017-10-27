package us.dot.its.jpo.ode.coder;

import us.dot.its.jpo.ode.importer.parser.RxMsgFileParser;
import us.dot.its.jpo.ode.importer.parser.TimLogFileParser;
import us.dot.its.jpo.ode.model.OdeLogMsgMetadataLocation;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.builders.AngleBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.SpeedOrVelocityBuilder;

public class TimDecoderHelper {
   public TimDecoderHelper() {
   }

   public static ReceivedMessageDetails buildReceivedMessageDetails(TimLogFileParser fileParser) {
      ReceivedMessageDetails timSpecificMetadata = new ReceivedMessageDetails(
            new OdeLogMsgMetadataLocation(new DsrcPosition3D(
                     Long.valueOf(fileParser.getLocation().getLatitude()),
                     Long.valueOf(fileParser.getLocation().getLongitude()),
                     Long.valueOf(fileParser.getLocation().getElevation())
                  ),
                  SpeedOrVelocityBuilder.genericSpeedOrVelocity(fileParser.getLocation().getSpeed()).toString(),
                  AngleBuilder.longToDecimal(fileParser.getLocation().getHeading()).toString()), null);
      
      if (fileParser instanceof RxMsgFileParser) {
         timSpecificMetadata.setRxSource( ((RxMsgFileParser) fileParser).getRxSource());
      }
      return timSpecificMetadata; 
    }
}
