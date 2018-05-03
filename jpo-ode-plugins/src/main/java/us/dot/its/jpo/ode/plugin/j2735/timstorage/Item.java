package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Item extends Asn1Object {
   private static final long serialVersionUID = 1L;
   
   private String itis;
   private String text;

    public String getItis ()
    {
        return itis;
    }

    public void setItis (String itis)
    {
        this.itis = itis;
    }

    public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }
}