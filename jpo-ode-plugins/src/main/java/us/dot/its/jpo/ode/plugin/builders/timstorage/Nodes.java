package us.dot.its.jpo.ode.plugin.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Nodes extends Asn1Object {

   private static final long serialVersionUID = 1L;
   @JsonProperty("NodeLL")
   private NodeLL[] nodeLL;
   
   @JsonProperty("NodeXY")
   private NodeXY[] nodeXY;

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
