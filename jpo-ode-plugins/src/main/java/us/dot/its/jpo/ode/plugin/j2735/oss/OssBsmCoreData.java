package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.BSMcoreData;
import us.dot.its.jpo.ode.j2735.dsrc.SteeringWheelAngle;
import us.dot.its.jpo.ode.plugin.j2735.J2735BsmCoreData;
import us.dot.its.jpo.ode.plugin.j2735.J2735Position3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735TransmissionState;
import us.dot.its.jpo.ode.util.CodecUtils;

public class OssBsmCoreData {

	public static J2735BsmCoreData genericBsmCoreData(BSMcoreData coreData) {
		J2735BsmCoreData genericBsmCoreData = new J2735BsmCoreData();

		genericBsmCoreData.msgCnt = coreData.msgCnt.intValue();

		genericBsmCoreData.id = CodecUtils.toHex(coreData.id.byteArrayValue());

		genericBsmCoreData.secMark = coreData.secMark.intValue();

		genericBsmCoreData.position = new J2735Position3D(coreData.lat.longValue(), coreData._long.longValue(),
				coreData.elev.longValue());

		genericBsmCoreData.accelSet = OssAccelerationSet4Way
				.genericAccelerationSet4Way(coreData.getAccelSet());

		genericBsmCoreData.accuracy = 
				OssPositionalAccuracy.genericPositionalAccuracy(coreData.accuracy);

		if (coreData.transmission != null) {
			if (coreData.transmission.indexOf() != J2735TransmissionState.unavailable.ordinal()) {
				genericBsmCoreData.transmission = J2735TransmissionState.values()[coreData.transmission.indexOf()];
			}
		}

		// speed is received in units of 0.02 m/s
		genericBsmCoreData.speed = OssSpeedOrVelocity.genericSpeed(coreData.speed);

		if (coreData.heading != null) {
			// Heading ::= INTEGER (0..28800)
			// -- LSB of 0.0125 degrees
			// -- A range of 0 to 359.9875 degrees
			genericBsmCoreData.heading = OssHeading.genericHeading(coreData.heading);
		}

		genericBsmCoreData.angle = steeringAngle(coreData.angle);
		genericBsmCoreData.brakes = 
				OssBrakeSystemStatus.genericBrakeSystemStatus(coreData.brakes);
		genericBsmCoreData.size = 
				new OssVehicleSize(coreData.size).getGenericVehicleSize();
		
		return genericBsmCoreData;
	}

	private static BigDecimal steeringAngle(SteeringWheelAngle steeringWhealAngle) {
		// SteeringWheelAngle ::= OCTET STRING (SIZE(1))
		// -- LSB units of 1.5 degrees.
		// -- a range of -189 to +189 degrees
		// -- 0x01 = 00 = +1.5 deg
		// -- 0x81 = -126 = -189 deg and beyond
		// -- 0x7E = +126 = +189 deg and beyond
		// -- 0x7F = +127 to be used for unavailable
		BigDecimal angle = null;
		if (steeringWhealAngle != null && 
				steeringWhealAngle.intValue() != 0x7F) {
				angle = BigDecimal.valueOf(
						steeringWhealAngle.intValue() * 15, 1);
		}
		return angle;
	}

}
