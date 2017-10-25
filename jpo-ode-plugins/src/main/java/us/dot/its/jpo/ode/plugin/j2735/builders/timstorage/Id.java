package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class Id
{
    private String region;

    private String id;

    public String getRegion ()
    {
        return region;
    }

    public void setRegion (String region)
    {
        this.region = region;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [region = "+region+", id = "+id+"]";
    }
}
