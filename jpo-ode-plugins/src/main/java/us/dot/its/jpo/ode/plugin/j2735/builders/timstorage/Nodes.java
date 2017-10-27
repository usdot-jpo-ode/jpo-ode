package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.model.OdeObject;

public class Nodes extends OdeObject {

   private static final long serialVersionUID = 1L;
   @JsonProperty("NodeLL")
   private NodeLL[] nodeLL;

   public NodeLL[] getNodeLL() {
      return nodeLL;
   }

   public void setNodeLL(NodeLL[] NodeLL) {
      this.nodeLL = NodeLL;
   }

   @Override
   public String toString() {
      return "ClassPojo [NodeLL = " + nodeLL + "]";
   }
}
