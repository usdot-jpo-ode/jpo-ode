package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Delta extends Asn1Object {

   private static final long serialVersionUID = 1L;
   
   @JsonProperty("node-XY")
   private Node_XY node_XY;
   
   @JsonProperty("node-LatLon")
   private Node_LatLon node_LatLon;

   public Node_LatLon getNode_LatLon() {
      return node_LatLon;
   }

   public void setNode_LatLon(Node_LatLon node_LatLon) {
      this.node_LatLon = node_LatLon;
   }

}
