package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class Content
{
    private Advisory advisory;

    public Advisory getAdvisory ()
    {
        return advisory;
    }

    public void setAdvisory (Advisory advisory)
    {
        this.advisory = advisory;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [advisory = "+advisory+"]";
    }
}
