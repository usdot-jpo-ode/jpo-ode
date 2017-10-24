package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class GeographicalPath
{
    private Id id;

    private boolean closedPath;

    private String direction;

    private Description description;

    private String name;

    private Directionality directionality;

    private String laneWidth;

    private Anchor anchor;

    public Id getId ()
    {
        return id;
    }

    public void setId (Id id)
    {
        this.id = id;
    }

    public boolean isClosedPath ()
    {
        return closedPath;
    }

    public void setClosedPath (boolean closedPath)
    {
        this.closedPath = closedPath;
    }

    public String getDirection ()
    {
        return direction;
    }

    public void setDirection (String direction)
    {
        this.direction = direction;
    }

    public Description getDescription ()
    {
        return description;
    }

    public void setDescription (Description description)
    {
        this.description = description;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Directionality getDirectionality ()
    {
        return directionality;
    }

    public void setDirectionality (Directionality directionality)
    {
        this.directionality = directionality;
    }

    public String getLaneWidth ()
    {
        return laneWidth;
    }

    public void setLaneWidth (String laneWidth)
    {
        this.laneWidth = laneWidth;
    }

    public Anchor getAnchor ()
    {
        return anchor;
    }

    public void setAnchor (Anchor anchor)
    {
        this.anchor = anchor;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", closedPath = "+closedPath+", direction = "+direction+", description = "+description+", name = "+name+", directionality = "+directionality+", laneWidth = "+laneWidth+", anchor = "+anchor+"]";
    }
}
