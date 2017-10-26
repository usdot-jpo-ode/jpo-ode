package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;

public class Content extends OdeObject
{
   private static final long serialVersionUID = 1L;
   private Advisory advisory;

    public Advisory getAdvisory ()
    {
        return advisory;
    }

    public void setAdvisory (Advisory advisory)
    {
        this.advisory = advisory;
    }
}
