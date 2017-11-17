package us.dot.its.jpo.ode.coder;

import us.dot.its.jpo.ode.importer.parser.RxMsgFileParser;
import us.dot.its.jpo.ode.importer.parser.TimLogFileParser;
import us.dot.its.jpo.ode.importer.parser.TimLogLocation;
import us.dot.its.jpo.ode.model.OdeLogMsgMetadataLocation;
import us.dot.its.jpo.ode.model.ReceivedMessageDetails;
import us.dot.its.jpo.ode.plugin.j2735.builders.ElevationBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.HeadingBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.LatitudeBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.LongitudeBuilder;
import us.dot.its.jpo.ode.plugin.j2735.builders.SpeedOrVelocityBuilder;

public class TimDecoderHelper {
   public TimDecoderHelper() {
   }

   public static ReceivedMessageDetails buildReceivedMessageDetails(TimLogFileParser fileParser) {
      TimLogLocation locationDetails = ((TimLogFileParser) fileParser).getLocation();
      ReceivedMessageDetails timSpecificMetadata = new ReceivedMessageDetails(
            new OdeLogMsgMetadataLocation(
               LatitudeBuilder.genericLatitude(locationDetails.getLatitude()).toString(),
               LongitudeBuilder.genericLongitude(locationDetails.getLongitude()).toString(),
               ElevationBuilder.genericElevation(locationDetails.getElevation()).toString(),
               SpeedOrVelocityBuilder.genericSpeedOrVelocity(locationDetails.getSpeed()).toString(),
               HeadingBuilder.genericHeading(locationDetails.getHeading()).toString()
                  ), null);
      
      if (fileParser instanceof RxMsgFileParser) {
         timSpecificMetadata.setRxSource( ((RxMsgFileParser) fileParser).getRxSource());
      }
      return timSpecificMetadata; 
    }
}
