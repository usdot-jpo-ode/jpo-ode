package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Xy extends Asn1Object {
   
   private static final long serialVersionUID = 1L;
   private Nodes nodes;

   public Nodes getNodes ()
   {
       return nodes;
   }

   public void setNodes (Nodes nodes)
   {
       this.nodes = nodes;
   }

}
