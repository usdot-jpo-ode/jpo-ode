package us.dot.its.jpo.ode.plugin.j2735;

import java.util.ArrayList;

public class J2735GeographicalPath {
   public String name;
   public J2735RoadSegmentReferenceID id;
   public J2735Position3D anchor;
   public Integer laneWidth;
   public J2735DirectionOfUse directionality;
   public Boolean closedPath;
   public J2735HeadingSlice direction;
   public J2735GeoPathDescription description;
   public ArrayList<J2735RegionalContent> regional = 
         new ArrayList<J2735RegionalContent>();

}
