package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class MsgId
{
    private RoadSignID roadSignID;

    public RoadSignID getRoadSignID ()
    {
        return roadSignID;
    }

    public void setRoadSignID (RoadSignID roadSignID)
    {
        this.roadSignID = roadSignID;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [roadSignID = "+roadSignID+"]";
    }
}
