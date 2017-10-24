package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class Node_LL3
{
    private String lon;

    private String lat;

    public String getLon ()
    {
        return lon;
    }

    public void setLon (String lon)
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
        return "ClassPojo [lon = "+lon+", lat = "+lat+"]";
    }
}

