package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Delta extends Asn1Object {

   private static final long serialVersionUID = 1L;
   @JsonProperty("node-LL3")
   private Node_LL3 node_LL3;
   
   @JsonProperty("node-LatLon")
   private Node_LatLon node_LatLon;

   public Node_LL3 getNode_LL3() {
      return node_LL3;
   }

   public void setNode_LL3(Node_LL3 node_LL3) {
      this.node_LL3 = node_LL3;
   }
}
