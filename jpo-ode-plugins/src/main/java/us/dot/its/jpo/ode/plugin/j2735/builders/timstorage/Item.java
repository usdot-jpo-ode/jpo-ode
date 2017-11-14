package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Item extends Asn1Object {
   private static final long serialVersionUID = 1L;
   
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