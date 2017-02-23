package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.AccelerationSet4Way;
import us.dot.its.jpo.ode.plugin.j2735.J2735AccelerationSet4Way;

public class OssAccelerationSet4Way {
	public static J2735AccelerationSet4Way genericAccelerationSet4Way(
			AccelerationSet4Way accelSet) {
		J2735AccelerationSet4Way genericAccelerationSet4Way = 
				new J2735AccelerationSet4Way();
		
		if (accelSet.yaw.intValue() < -32767 || accelSet.yaw.intValue() > 32767) {
		    throw new IllegalArgumentException("Yaw rate out of bounds");
		}
		
        //Acceleration ::= INTEGER (-2000..2001) 
        //    -- LSB units are 0.01 m/s^2
        //   -- the value 2000 shall be used for values greater than 2000     
        //   -- the value -2000 shall be used for values less than -2000  
        //   -- a value of 2001 shall be used for Unavailable
		if (accelSet._long.intValue() == 2001) {
			genericAccelerationSet4Way.setAccelLong(null);
		} else if (accelSet._long.intValue() < -2000) {
			genericAccelerationSet4Way.setAccelLong(BigDecimal.valueOf(-20.00));
		} else if (accelSet._long.intValue() > 2001) {
			genericAccelerationSet4Way.setAccelLong(BigDecimal.valueOf(20.00));
		} else {
			genericAccelerationSet4Way.setAccelLong(BigDecimal.valueOf(accelSet._long.longValue(), 2));
		}
		
		if (accelSet.lat.intValue() == 2001) {
			genericAccelerationSet4Way.setAccelLat(null);
		} else if (accelSet.lat.intValue() < -2000) {
			genericAccelerationSet4Way.setAccelLat(BigDecimal.valueOf(-20.00));
		} else if (accelSet.lat.intValue() > 2001) {
			genericAccelerationSet4Way.setAccelLat(BigDecimal.valueOf(20.00));
		} else {
			genericAccelerationSet4Way.setAccelLat(BigDecimal.valueOf(accelSet.lat.intValue(), 2));
		}

		// VerticalAcceleration ::= INTEGER (-127..127)
		//		-- LSB units of 0.02 G steps over -2.52 to +2.54 G 
		//		-- The value +127 shall be used for ranges >= 2.54 G 
		//		-- The value -126 shall be used for ranges <= -2.52 G 
		//		-- The value -127 shall be used for unavailable
		if (accelSet.vert.intValue() == -127) {
			genericAccelerationSet4Way.setAccelVert(null);
		} else if (accelSet.vert.intValue() < -127) {
			genericAccelerationSet4Way.setAccelVert(BigDecimal.valueOf(-2.52));
		} else if (accelSet.vert.intValue() > 127) {
			genericAccelerationSet4Way.setAccelVert(BigDecimal.valueOf(2.54));
		} else {
			genericAccelerationSet4Way.setAccelVert(BigDecimal.valueOf(accelSet.vert.intValue() * (long)2, 2));
			
		}

		// YawRate ::= INTEGER (-32767..32767)
		// -- LSB units of 0.01 degrees per second (signed)
		if (accelSet.yaw.intValue() <= 32767 && accelSet.yaw.intValue() >= -32767)
			genericAccelerationSet4Way.setAccelYaw(BigDecimal.valueOf(accelSet.yaw.intValue(), 2));
		
		return genericAccelerationSet4Way;
	}

}
