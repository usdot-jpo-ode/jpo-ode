/*******************************************************************************
 * Copyright 2018 572682
 * 
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   <p>http://www.apache.org/licenses/LICENSE-2.0
 * 
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package us.dot.its.jpo.ode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

/**
 * Base ODE Metadata class.
 */
@JsonPropertyOrder({ "logFileName", "recordType", "receivedMessageDetails", "payloadType", "serialId",
    "odeReceivedAt", "schemaVersion", "maxDurationTime", "recordGeneratedAt", "recordGeneratedBy", "sanitized",
    "asn1" })
public class OdeMsgMetadata extends OdeObject {

  /**
   * Enum for conveying where a message was generated from.
   */
  public enum GeneratedBy {
    TMC, OBU, RSU, TMC_VIA_SAT, TMC_VIA_SNMP, UNKNOWN
  }

  private static final long serialVersionUID = 3979762143291085955L;
  private static final int staticSchemaVersion = 8;

  private String payloadType;
  private SerialId serialId;
  private String odeReceivedAt;
  private int schemaVersion;
  private int maxDurationTime;
  private String odePacketID;
  private String odeTimStartDateTime;
  private String recordGeneratedAt;
  private GeneratedBy recordGeneratedBy;
  private boolean sanitized = false;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String asn1 = null;

  public OdeMsgMetadata() {
    this(OdeMsgPayload.class.getName(), new SerialId(), DateTimeUtils.now());
  }

  /**
   * Constructs an OdeMsgMetadata object with the specified payload.
   *
   * @param payload the payload to be set
   */
  public OdeMsgMetadata(OdeMsgPayload payload) {
    this(payload, new SerialId(), DateTimeUtils.now());
  }

  /**
   * Constructs an OdeMsgMetadata object with the specified payload, serial ID,
   * and received time.
   *
   * @param payload    the payload to be set
   * @param serialId   the serial ID to be set
   * @param receivedAt the time the message was received
   */
  private OdeMsgMetadata(OdeMsgPayload payload, SerialId serialId, String receivedAt) {
    this(payload.getClass().getName(), serialId, receivedAt);
    setAsn1(payload);
  }

  /**
   * Constructs an OdeMsgMetadata object with the specified payload type, serial
   * ID, and received time.
   *
   * @param payloadType the type of the payload
   * @param serialId    the serial ID to be set
   * @param receivedAt  the time the message was received
   */
  public OdeMsgMetadata(String payloadType, SerialId serialId, String receivedAt) {
    super();
    this.schemaVersion = staticSchemaVersion;
    this.payloadType = payloadType;
    this.serialId = serialId;
    this.odeReceivedAt = receivedAt;
  }

  public String getPayloadType() {
    return payloadType;
  }

  public OdeMsgMetadata setPayloadType(OdeDataType payloadType) {
    this.payloadType = payloadType.getShortName();
    return this;
  }

  public OdeMsgMetadata setPayloadType(String payloadType) {
    this.payloadType = payloadType;
    return this;
  }

  public SerialId getSerialId() {
    return serialId;
  }

  public void setSerialId(SerialId serialId) {
    this.serialId = serialId;
  }

  public String getOdeReceivedAt() {
    return odeReceivedAt;
  }

  public void setOdeReceivedAt(String receivedAt) {
    this.odeReceivedAt = receivedAt;
  }

  public int getSchemaVersion() {
    return schemaVersion;
  }

  public void setSchemaVersion(int schemaVersion) {
    this.schemaVersion = schemaVersion;
  }

  public int getMaxDurationTime() {
    return maxDurationTime;
  }

  public void setMaxDurationTime(int maxDurationTime) {
    this.maxDurationTime = maxDurationTime;
  }

  public String getOdePacketID() {
    return odePacketID;
  }

  public void setOdePacketID(String odePacketID) {
    this.odePacketID = odePacketID;
  }

  public String getOdeTimStartDateTime() {
    return odeTimStartDateTime;
  }

  public void setOdeTimStartDateTime(String odeTimStartDateTime) {
    this.odeTimStartDateTime = odeTimStartDateTime;
  }

  public String getRecordGeneratedAt() {
    return recordGeneratedAt;
  }

  public void setRecordGeneratedAt(String recordGeneratedAt) {
    this.recordGeneratedAt = recordGeneratedAt;
  }

  public GeneratedBy getRecordGeneratedBy() {
    return recordGeneratedBy;
  }

  public void setRecordGeneratedBy(GeneratedBy recordGeneratedBy) {
    this.recordGeneratedBy = recordGeneratedBy;
  }

  public boolean isSanitized() {
    return sanitized;
  }

  public void setSanitized(boolean sanitized) {
    this.sanitized = sanitized;
  }

  public static int getStaticSchemaVersion() {
    return staticSchemaVersion;
  }

  public String getAsn1() {
    return asn1;
  }

  public void setAsn1(String asn1) {
    this.asn1 = asn1;
  }

  /**
   * Sets the ASN1 value for the metadata object.
   *
   * @param payload the ASN1 payload hex string
   */
  public void setAsn1(OdeMsgPayload payload) {
    if (payload != null && payload.getData() != null) {
      if (JsonUtils.getJsonNode(payload.getData().toString(), "bytes") != null) {
        this.asn1 = JsonUtils.getJsonNode(payload.getData().toString(), "bytes").asText();
      }
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((odeReceivedAt == null) ? 0 : odeReceivedAt.hashCode());
    result = prime * result + ((payloadType == null) ? 0 : payloadType.hashCode());
    result = prime * result + ((recordGeneratedAt == null) ? 0 : recordGeneratedAt.hashCode());
    result = prime * result + ((recordGeneratedBy == null) ? 0 : recordGeneratedBy.hashCode());
    result = prime * result + (sanitized ? 1231 : 1237);
    result = prime * result + schemaVersion;
    result = prime * result + ((serialId == null) ? 0 : serialId.hashCode());
    result = prime * result + ((asn1 == null) ? 0 : asn1.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    OdeMsgMetadata other = (OdeMsgMetadata) obj;
    if (odeReceivedAt == null) {
      if (other.odeReceivedAt != null) {
        return false;
      }
    } else if (!odeReceivedAt.equals(other.odeReceivedAt)) {
      return false;
    }

    if (payloadType == null) {
      if (other.payloadType != null) {
        return false;
      }
    } else if (!payloadType.equals(other.payloadType)) {
      return false;
    }

    if (recordGeneratedAt == null) {
      if (other.recordGeneratedAt != null) {
        return false;
      }
    } else if (!recordGeneratedAt.equals(other.recordGeneratedAt)) {
      return false;
    }

    if (recordGeneratedBy != other.recordGeneratedBy) {
      return false;
    }

    if (sanitized != other.sanitized) {
      return false;
    }

    if (schemaVersion != other.schemaVersion) {
      return false;
    }

    if (serialId == null) {
      if (other.serialId != null) {
        return false;
      }
    } else if (!serialId.equals(other.serialId)) {
      return false;
    }

    if (asn1 == null) {
      if (other.asn1 != null) {
        return false;
      }
    } else if (!asn1.equals(other.asn1)) {
      return false;
    }

    return true;
  }

}
