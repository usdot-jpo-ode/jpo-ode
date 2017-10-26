package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;

public class Geometry extends OdeObject
{
   private static final long serialVersionUID = 1L;

   private String extent;

    private String direction;

    private Circle circle;

    private String laneWidth;

    public String getExtent ()
    {
        return extent;
    }

    public void setExtent (String extent)
    {
        this.extent = extent;
    }

    public String getDirection ()
    {
        return direction;
    }

    public void setDirection (String direction)
    {
        this.direction = direction;
    }

    public Circle getCircle ()
    {
        return circle;
    }

    public void setCircle (Circle circle)
    {
        this.circle = circle;
    }

    public String getLaneWidth ()
    {
        return laneWidth;
    }

    public void setLaneWidth (String laneWidth)
    {
        this.laneWidth = laneWidth;
    }
}
