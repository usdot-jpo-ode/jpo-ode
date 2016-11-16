package us.dot.its.jpo.ode.model;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bah.ode.asn.oss.semi.AdvisorySituationData;
import com.bah.ode.asn.oss.semi.GeoRegion;
import com.bah.ode.asn.oss.semi.GroupID;
import com.bah.ode.asn.oss.semi.TimeToLive;

import us.dot.its.jpo.ode.asn.OdeAdvisoryDetails;
import us.dot.its.jpo.ode.util.DateTimeUtils;

public final class OdeAdvisoryData extends DotWarehouseData
      implements HasTimestamp {

   private static final long serialVersionUID = -8570818421787304109L;

   private static Logger logger = LoggerFactory
         .getLogger(OdeAdvisoryData.class);

   // bitwise constants for setting distType by ORing
   public static final byte DIST_TYPE_NONE = 0;
   public static final byte DIST_TYPE_RSU = 1;
   public static final byte DIST_TYPE_IP = 2;

   private OdeAdvisoryDetails advisoryMessage;

   public OdeAdvisoryData() {
      super();
   }

   public OdeAdvisoryData(String serialId, GroupID groupID,
         TimeToLive timeToLive, GeoRegion serviceRegion) {
      super(serialId, groupID, timeToLive, serviceRegion);
   }

   public OdeAdvisoryData(String streamId, long bundleId, long recordId) {
      super(streamId, bundleId, recordId);
   }

   public OdeAdvisoryData(String serialId) {
      super(serialId);
   }

   public OdeAdvisoryData(String serialId, AdvisorySituationData asd)
         throws UnsupportedEncodingException {

      super(serialId, asd.getGroupID(), asd.getTimeToLive(), asd.getServiceRegion());
      
      setAdvisoryMessage(new OdeAdvisoryDetails(asd.getAsdmDetails()));
   }

   public OdeAdvisoryDetails getAdvisoryMessage() {
      return advisoryMessage;
   }

   public void setAdvisoryMessage(OdeAdvisoryDetails advisoryMessage) {
      this.advisoryMessage = advisoryMessage;
   }

   @Override
   public ZonedDateTime getTimestamp() {
      try {
         if (advisoryMessage.getStartTime() == null ||
               advisoryMessage.getStopTime() == null) {
            return null;
         } else {
            ZonedDateTime startTime = DateTimeUtils.isoDateTime(advisoryMessage.getStartTime());
            ZonedDateTime endTime = DateTimeUtils.isoDateTime(advisoryMessage.getStopTime());
            return startTime.plusSeconds(endTime.toEpochSecond() - startTime.toEpochSecond());
         }
      } catch (ParseException e) {
         logger.error("Error getting timestamp: ", e);
      }

      return null;
   }

   @Override
   public boolean isOnTime(ZonedDateTime start, ZonedDateTime end) {
      return DateTimeUtils.isBetweenTimesInclusive(getTimestamp(),
            start, end);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result
            + ((advisoryMessage == null) ? 0 : advisoryMessage.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      OdeAdvisoryData other = (OdeAdvisoryData) obj;
      if (advisoryMessage == null) {
         if (other.advisoryMessage != null)
            return false;
      } else if (!advisoryMessage.equals(other.advisoryMessage))
         return false;
      return true;
   }


}
