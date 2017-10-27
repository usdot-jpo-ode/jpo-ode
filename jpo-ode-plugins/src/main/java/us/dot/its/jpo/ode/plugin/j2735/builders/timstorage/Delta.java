package us.dot.its.jpo.ode.plugin.j2735.builders.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;

import us.dot.its.jpo.ode.model.OdeObject;

public class Delta extends OdeObject {

   private static final long serialVersionUID = 1L;
   @JsonProperty("node-LL3")
   private Node_LL3 node_LL3;

   public Node_LL3 getNode_LL3() {
      return node_LL3;
   }

   public void setNode_LL3(Node_LL3 node_LL3) {
      this.node_LL3 = node_LL3;
   }
}
