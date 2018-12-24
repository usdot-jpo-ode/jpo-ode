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
package us.dot.its.jpo.ode.udp.vsd;

//TODO open-ode
//import java.util.ArrayList;
//import java.util.List;
//
//public class VsdToBsmConverter {
//
//	private VsdToBsmConverter() {
//		// hidden
//	}
//
//	public static List<BasicSafetyMessage> convert(VehSitDataMessage vsdm) {
//		
//		// If the bundle element is null, the data is likely corrupted
//		if (null == vsdm.bundle) {
//			throw new IllegalArgumentException("VehSitDataMessage bundle null");
//		}
//		
//		// If the bundle exists but is empty, return an empty list
//		if (vsdm.bundle.getSize() == 0) {
//			return new ArrayList<>();
//		}
//
//		List<BasicSafetyMessage> bsmList = new ArrayList<>();
//
//		for (VehSitRecord vsr : vsdm.bundle.elements) {
//			BasicSafetyMessage bsm = new BasicSafetyMessage();
//			bsm.coreData = createCoreData(vsr);
//			bsmList.add(bsm);
//		}
//
//		return bsmList;
//	}
//
//	private static BSMcoreData createCoreData(VehSitRecord vsr) {
//
//		BSMcoreData cd = new BSMcoreData();
//		cd.lat = vsr.pos.lat;
//		cd._long = vsr.pos._long;
//		cd.accelSet = vsr.fundamental.accelSet;
//		cd.accuracy = new PositionalAccuracy(new SemiMajorAxisAccuracy(255), new SemiMinorAxisAccuracy(255),
//				new SemiMajorAxisOrientation(65535));
//		cd.angle = vsr.fundamental.steeringAngle;
//		cd.brakes = vsr.fundamental.brakes;
//		cd.elev = vsr.pos.elevation;
//		cd.heading = vsr.fundamental.heading;
//		cd.id = vsr.tempID;
//		cd.msgCnt = new MsgCount(0);
//		cd.secMark = vsr.time.second;
//		cd.size = vsr.fundamental.vehSize;
//		cd.speed = new Speed(vsr.fundamental.speed.speed.intValue());
//
//		return cd;
//	}
//
//}
