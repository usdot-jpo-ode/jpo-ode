package us.dot.its.jpo.ode.plugin.builders.timstorage;

import us.dot.its.jpo.ode.model.OdeObject;

public class Xy extends OdeObject {
   
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
