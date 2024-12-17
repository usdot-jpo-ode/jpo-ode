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
import lombok.Data;
import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

/**
 * Node offset point in XY plane.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class NodeOffsetPointXY extends Asn1Object {

  private static final long serialVersionUID = 1L;

  @JsonProperty("node-XY")
  private NodeXYAsn1Object nodeXY;

  @JsonProperty("node-XY1")
  private NodeXYAsn1Object nodeXY1;

  @JsonProperty("node-XY2")
  private NodeXYAsn1Object nodeXY2;

  @JsonProperty("node-XY3")
  private NodeXYAsn1Object nodeXY3;

  @JsonProperty("node-XY4")
  private NodeXYAsn1Object nodeXY4;

  @JsonProperty("node-XY5")
  private NodeXYAsn1Object nodeXY5;

  @JsonProperty("node-XY6")
  private NodeXYAsn1Object nodeXY6;

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
}
