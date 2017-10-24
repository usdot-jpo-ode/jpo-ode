package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

public class Path
{
    private String scale;

    private Offset offset;

    public String getScale ()
    {
        return scale;
    }

    public void setScale (String scale)
    {
        this.scale = scale;
    }

    public Offset getOffset ()
    {
        return offset;
    }

    public void setOffset (Offset offset)
    {
        this.offset = offset;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [scale = "+scale+", offset = "+offset+"]";
    }
}
