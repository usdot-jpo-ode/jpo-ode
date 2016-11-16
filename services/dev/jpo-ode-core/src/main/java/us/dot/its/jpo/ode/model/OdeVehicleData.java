/*******************************************************************************
 * Copyright (c) 2015 US DOT - Joint Program Office
 *
 * The Government has unlimited rights to all documents/material produced under 
 * this task order. All documents and materials, to include the source code of 
 * any software produced under this contract, shall be Government owned and the 
 * property of the Government with all rights and privileges of ownership/copyright 
 * belonging exclusively to the Government. These documents and materials may 
 * not be used or sold by the Contractor without written permission from the CO.
 * All materials supplied to the Government shall be the sole property of the 
 * Government and may not be used for any other purpose. This right does not 
 * abrogate any other Government rights.
 *
 * Contributors:
 *     Booz | Allen | Hamilton - initial API and implementation
 *******************************************************************************/
package us.dot.its.jpo.ode.model;

import com.bah.ode.asn.oss.dsrc.AccelerationSet4Way;
import com.bah.ode.asn.oss.dsrc.BrakeSystemStatus;
import com.bah.ode.asn.oss.dsrc.Heading;
import com.bah.ode.asn.oss.dsrc.SteeringWheelAngle;
import com.bah.ode.asn.oss.dsrc.TransmissionAndSpeed;
import com.bah.ode.asn.oss.semi.GroupID;
import com.bah.ode.asn.oss.semi.VsmEventFlag;

import us.dot.its.jpo.ode.asn.OdeDateTime;
import us.dot.its.jpo.ode.asn.OdePosition3D;
import us.dot.its.jpo.ode.asn.OdeVehicleSize;
import us.dot.its.jpo.ode.util.CodecUtils;

public final class OdeVehicleData extends OdeData {
   private static final long serialVersionUID = 33889808649252185L;

   private String groupId;
   private OdeDateTime dateTime;
   private OdePosition3D location;
   private String speed;
   private Integer heading;
   private String steeringAngle;
   private String accelSet;
   private String brakes;
   private OdeVehicleSize size;
   private String vsmEventFlag;

   public OdeVehicleData() {
      super();
   }

   public OdeVehicleData(String streamId, long bundleId, long recordId) {
      super(streamId, bundleId, recordId);
   }

   public OdeVehicleData(String serialId) {
      super(serialId);
   }

   public OdeVehicleData(String serialId, GroupID groupId, OdeDateTime dateTime,
         OdePosition3D location, TransmissionAndSpeed speed, Heading heading,
         SteeringWheelAngle steeringAngle, AccelerationSet4Way accelSet,
         BrakeSystemStatus brakes, OdeVehicleSize size,
         VsmEventFlag vsmEventFlag) {
      super(serialId);
      this.groupId = CodecUtils
            .toHex(groupId != null ? groupId.byteArrayValue() : "".getBytes());
      this.dateTime = dateTime;
      this.location = location;
      this.speed = CodecUtils
            .toHex(speed != null ? speed.byteArrayValue() : "".getBytes());
      this.heading = heading != null ? heading.intValue() : 0;
      this.steeringAngle = CodecUtils.toHex(steeringAngle != null
            ? steeringAngle.byteArrayValue() : "".getBytes());
      this.accelSet = CodecUtils.toHex(
            accelSet != null ? accelSet.byteArrayValue() : "".getBytes());
      this.brakes = CodecUtils
            .toHex(brakes != null ? brakes.byteArrayValue() : "".getBytes());
      this.size = size;
      this.vsmEventFlag = CodecUtils.toHex(vsmEventFlag != null
            ? vsmEventFlag.byteArrayValue() : "".getBytes());
   }

   public String getGroupId() {
      return groupId;
   }

   public void setGroupId(String groupId) {
      this.groupId = groupId;
   }

   public OdeDateTime getDateTime() {
      return dateTime;
   }

   public void setDateTime(OdeDateTime dateTime) {
      this.dateTime = dateTime;
   }

   public OdePosition3D getLocation() {
      return location;
   }

   public void setLocation(OdePosition3D location) {
      this.location = location;
   }

   public String getSpeed() {
      return speed;
   }

   public void setSpeed(String speed) {
      this.speed = speed;
   }

   public Integer getHeading() {
      return heading;
   }

   public void setHeading(Integer heading) {
      this.heading = heading;
   }

   public String getSteeringAngle() {
      return steeringAngle;
   }

   public void setSteeringAngle(String steeringAngle) {
      this.steeringAngle = steeringAngle;
   }

   public String getAccelSet() {
      return accelSet;
   }

   public void setAccelSet(String accelSet) {
      this.accelSet = accelSet;
   }

   public String getBrakes() {
      return brakes;
   }

   public void setBrakes(String brakes) {
      this.brakes = brakes;
   }

   public OdeVehicleSize getSize() {
      return size;
   }

   public void setSize(OdeVehicleSize size) {
      this.size = size;
   }

   public String getVsmEventFlag() {
      return vsmEventFlag;
   }

   public void setVsmEventFlag(String vsmEventFlag) {
      this.vsmEventFlag = vsmEventFlag;
   }


}
