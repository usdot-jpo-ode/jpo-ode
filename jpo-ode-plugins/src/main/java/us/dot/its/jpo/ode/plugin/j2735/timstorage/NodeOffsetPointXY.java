/*******************************************************************************
 * Copyright 2018 572682.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at</p>
 *
 *   <p>http://www.apache.org/licenses/LICENSE-2.0</p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.</p>
 ******************************************************************************/

package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * Node offset point in XY plane.
 */
@EqualsAndHashCode(callSuper = false)
public class NodeOffsetPointXY extends Asn1Object {

  private static final long serialVersionUID = 1L;

  @JsonProperty("node-XY")
  private Node_XY nodeXY;

  @JsonProperty("node-XY1")
  private Node_XY nodeXY1;

  @JsonProperty("node-XY2")
  private Node_XY nodeXY2;

  @JsonProperty("node-XY3")
  private Node_XY nodeXY3;

  @JsonProperty("node-XY4")
  private Node_XY nodeXY4;

  @JsonProperty("node-XY5")
  private Node_XY nodeXY5;

  @JsonProperty("node-XY6")
  private Node_XY nodeXY6;

  @JsonProperty("node-LL1")
  private Node_LatLon nodeLL1;

  @JsonProperty("node-LL2")
  private Node_LatLon nodeLL2;

  @JsonProperty("node-LL3")
  private Node_LatLon nodeLL3;

  @JsonProperty("node-LL4")
  private Node_LatLon nodeLL4;

  @JsonProperty("node-LL5")
  private Node_LatLon nodeLL5;

  @JsonProperty("node-LL6")
  private Node_LatLon nodeLL6;

  @JsonProperty("node-LatLon")
  private Node_LatLon nodeLatLon;

  public Node_XY getNodeXY() {
    return nodeXY;
  }

  public void setNodeXY(Node_XY nodeXY) {
    this.nodeXY = nodeXY;
  }

  public Node_XY getNodeXY1() {
    return nodeXY1;
  }

  public void setNodeXY1(Node_XY nodeXY1) {
    this.nodeXY1 = nodeXY1;
  }

  public Node_XY getNodeXY2() {
    return nodeXY2;
  }

  public void setNodeXY2(Node_XY nodeXY2) {
    this.nodeXY2 = nodeXY2;
  }

  public Node_XY getNodeXY3() {
    return nodeXY3;
  }

  public void setNodeXY3(Node_XY nodeXY3) {
    this.nodeXY3 = nodeXY3;
  }

  public Node_XY getNodeXY4() {
    return nodeXY4;
  }

  public void setNodeXY4(Node_XY nodeXY4) {
    this.nodeXY4 = nodeXY4;
  }

  public Node_XY getNodeXY5() {
    return nodeXY5;
  }

  public void setNodeXY5(Node_XY nodeXY5) {
    this.nodeXY5 = nodeXY5;
  }

  public Node_XY getNodeXY6() {
    return nodeXY6;
  }

  public void setNodeXY6(Node_XY nodeXY6) {
    this.nodeXY6 = nodeXY6;
  }

  public Node_LatLon getNodeLL1() {
    return nodeLL1;
  }

  public void setNodeLL1(Node_LatLon nodeLL1) {
    this.nodeLL1 = nodeLL1;
  }

  public Node_LatLon getNodeLL2() {
    return nodeLL2;
  }

  public void setNodeLL2(Node_LatLon nodeLL2) {
    this.nodeLL2 = nodeLL2;
  }

  public Node_LatLon getNodeLL3() {
    return nodeLL3;
  }

  public void setNodeLL3(Node_LatLon nodeLL3) {
    this.nodeLL3 = nodeLL3;
  }

  public Node_LatLon getNodeLL4() {
    return nodeLL4;
  }

  public void setNodeLL4(Node_LatLon nodeLL4) {
    this.nodeLL4 = nodeLL4;
  }

  public Node_LatLon getNodeLL5() {
    return nodeLL5;
  }

  public void setNodeLL5(Node_LatLon nodeLL5) {
    this.nodeLL5 = nodeLL5;
  }

  public Node_LatLon getNodeLL6() {
    return nodeLL6;
  }

  public void setNodeLL6(Node_LatLon nodeLL6) {
    this.nodeLL6 = nodeLL6;
  }

  public Node_LatLon getNodeLatLon() {
    return nodeLatLon;
  }

  public void setNodeLatLon(Node_LatLon nodeLatLon) {
    this.nodeLatLon = nodeLatLon;
  }

}
