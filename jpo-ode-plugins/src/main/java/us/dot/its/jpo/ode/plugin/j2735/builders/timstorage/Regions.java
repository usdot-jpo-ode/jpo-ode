package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class Regions
{
    private GeographicalPath GeographicalPath;

    public GeographicalPath getGeographicalPath ()
    {
        return GeographicalPath;
    }

    public void setGeographicalPath (GeographicalPath GeographicalPath)
    {
        this.GeographicalPath = GeographicalPath;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [GeographicalPath = "+GeographicalPath+"]";
    }
}