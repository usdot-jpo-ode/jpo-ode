package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class MsgId extends Asn1Object
{
   private static final long serialVersionUID = 1L;
   private RoadSignID roadSignID;

    public RoadSignID getRoadSignID ()
    {
        return roadSignID;
    }

    public void setRoadSignID (RoadSignID roadSignID)
    {
        this.roadSignID = roadSignID;
    }
}
