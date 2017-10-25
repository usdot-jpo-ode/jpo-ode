package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class Position
{
    private String elevation;

    private String lon; // TODO needs to be "long"

    private String lat;

    public String getElevation ()
    {
        return elevation;
    }

    public void setElevation (String elevation)
    {
        this.elevation = elevation;
    }

    public String getlon ()
    {
        return lon;
    }

    public void setlon (String lon)
    {
        this.lon = lon;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [elevation = "+elevation+", lon = "+lon+", lat = "+lat+"]";
    }
}
