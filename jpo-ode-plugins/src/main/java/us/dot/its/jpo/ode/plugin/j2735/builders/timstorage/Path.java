package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;

public class Path extends OdeObject
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
}
