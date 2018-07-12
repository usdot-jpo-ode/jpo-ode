package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class Delta extends Asn1Object {

   private static final long serialVersionUID = 1L;
   
   @JsonProperty("node-XY")
   private Node_XY node_XY;
   
   @JsonProperty("node-XY1")
   private Node_XY node_XY1;

   @JsonProperty("node-XY2")
   private Node_XY node_XY2;

   @JsonProperty("node-XY3")
   private Node_XY node_XY3;

   @JsonProperty("node-XY4")
   private Node_XY node_XY4;

   @JsonProperty("node-XY5")
   private Node_XY node_XY5;

   @JsonProperty("node-XY6")
   private Node_XY node_XY6;

   @JsonProperty("node-LL1")
   private Node_LatLon node_LL1;

   @JsonProperty("node-LL2")
   private Node_LatLon node_LL2;

   @JsonProperty("node-LL3")
   private Node_LatLon node_LL3;

   @JsonProperty("node-LL4")
   private Node_LatLon node_LL4;

   @JsonProperty("node-LL5")
   private Node_LatLon node_LL5;

   @JsonProperty("node-LL6")
   private Node_LatLon node_LL6;

   @JsonProperty("node-LatLon")
   private Node_LatLon node_LatLon;

   public Node_XY getNode_XY() {
      return node_XY;
   }

   public void setNode_XY(Node_XY node_XY) {
      this.node_XY = node_XY;
   }

   public Node_XY getNode_XY1() {
      return node_XY1;
   }

   public void setNode_XY1(Node_XY node_XY1) {
      this.node_XY1 = node_XY1;
   }

   public Node_XY getNode_XY2() {
      return node_XY2;
   }

   public void setNode_XY2(Node_XY node_XY2) {
      this.node_XY2 = node_XY2;
   }

   public Node_XY getNode_XY3() {
      return node_XY3;
   }

   public void setNode_XY3(Node_XY node_XY3) {
      this.node_XY3 = node_XY3;
   }

   public Node_XY getNode_XY4() {
      return node_XY4;
   }

   public void setNode_XY4(Node_XY node_XY4) {
      this.node_XY4 = node_XY4;
   }

   public Node_XY getNode_XY5() {
      return node_XY5;
   }

   public void setNode_XY5(Node_XY node_XY5) {
      this.node_XY5 = node_XY5;
   }

   public Node_XY getNode_XY6() {
      return node_XY6;
   }

   public void setNode_XY6(Node_XY node_XY6) {
      this.node_XY6 = node_XY6;
   }

   public Node_LatLon getNode_LL1() {
      return node_LL1;
   }

   public void setNode_LL1(Node_LatLon node_LL1) {
      this.node_LL1 = node_LL1;
   }

   public Node_LatLon getNode_LL2() {
      return node_LL2;
   }

   public void setNode_LL2(Node_LatLon node_LL2) {
      this.node_LL2 = node_LL2;
   }

   public Node_LatLon getNode_LL3() {
      return node_LL3;
   }

   public void setNode_LL3(Node_LatLon node_LL3) {
      this.node_LL3 = node_LL3;
   }

   public Node_LatLon getNode_LL4() {
      return node_LL4;
   }

   public void setNode_LL4(Node_LatLon node_LL4) {
      this.node_LL4 = node_LL4;
   }

   public Node_LatLon getNode_LL5() {
      return node_LL5;
   }

   public void setNode_LL5(Node_LatLon node_LL5) {
      this.node_LL5 = node_LL5;
   }

   public Node_LatLon getNode_LL6() {
      return node_LL6;
   }

   public void setNode_LL6(Node_LatLon node_LL6) {
      this.node_LL6 = node_LL6;
   }

   public Node_LatLon getNode_LatLon() {
      return node_LatLon;
   }

   public void setNode_LatLon(Node_LatLon node_LatLon) {
      this.node_LatLon = node_LatLon;
   }

}
