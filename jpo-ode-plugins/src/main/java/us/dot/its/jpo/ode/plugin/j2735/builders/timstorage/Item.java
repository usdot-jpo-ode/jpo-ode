package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class Item
{
    private String itis;

    public String getItis ()
    {
        return itis;
    }

    public void setItis (String itis)
    {
        this.itis = itis;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [itis = "+itis+"]";
    }
}