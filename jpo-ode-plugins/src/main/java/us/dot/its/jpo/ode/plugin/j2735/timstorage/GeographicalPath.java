/*******************************************************************************
 * Copyright 2018 572682
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.plugin.j2735.timstorage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.EqualsAndHashCode;
import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

@JsonPropertyOrder({"name", "id", "anchor", "laneWidth", "directionality", "closedPath",
    "direction", "description"})
@EqualsAndHashCode(callSuper = false)
public class GeographicalPath extends Asn1Object {
  private static final long serialVersionUID = 1L;

  @JsonProperty("name")
  private String name;

  @JsonProperty("id")
  private Id id;

  @JsonProperty("anchor")
  private Anchor anchor;

  @JsonProperty("laneWidth")
  private String laneWidth;

  @JsonProperty("directionality")
  private DirectionOfUse directionality;

  @JsonProperty("closedPath")
  private String closedPath;

  @JsonProperty("direction")
  private String direction;

  @JsonProperty("description")
  private Description description;

  public Id getId() {
    return id;
  }

  public void setId(Id id) {
    this.id = id;
  }

  public String getClosedPath() {
    return closedPath;
  }

  public void setClosedPath(String closedPath) {
    this.closedPath = closedPath;
  }


  public DirectionOfUse getDirectionality() {
    return directionality;
  }

  public void setDirectionality(DirectionOfUse directionality) {
    this.directionality = directionality;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public Description getDescription() {
    return description;
  }

  public void setDescription(Description description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLaneWidth() {
    return laneWidth;
  }

  public void setLaneWidth(String laneWidth) {
    this.laneWidth = laneWidth;
  }

  public Anchor getAnchor() {
    return anchor;
  }

  public void setAnchor(Anchor anchor) {
    this.anchor = anchor;
  }
}
