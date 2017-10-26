package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class DataFrames
{
    private TravelerDataFrame TravelerDataFrame;

    public TravelerDataFrame getTravelerDataFrame ()
    {
        return TravelerDataFrame;
    }

    public void setTravelerDataFrame (TravelerDataFrame TravelerDataFrame)
    {
        this.TravelerDataFrame = TravelerDataFrame;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [TravelerDataFrame = "+TravelerDataFrame+"]";
    }
}

