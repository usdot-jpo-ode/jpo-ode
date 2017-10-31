package us.dot.its.jpo.ode.plugin.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;

public class MsgId extends OdeObject
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
