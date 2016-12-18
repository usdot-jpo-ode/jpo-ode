package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.TrailerMass;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerWeight;
import us.dot.its.jpo.ode.j2735.dsrc.VehicleMass;

public class OssMassOrWeight {

	public static Integer genericMass(TrailerMass mass) {
	    
	    if (mass.intValue() < 0 || mass.intValue() > 255) {
	        throw new IllegalArgumentException("Trailer mass out of bounds");
	    }
		
		Integer result = null;
		
		if (mass.intValue() != 0) {
			result = mass.intValue() * 500;
		}
		
		return result;
	}

	//A data element re-used from the SAE J1939 standard and encoded as: 
	// 2kg/bit, 0 deg offset, Range: 0 to +128,510kg. See SPN 180, PGN reference 65258
	public static Integer genericWeight(TrailerWeight weight) {
	    
	    if (weight.intValue() < 0 || weight.intValue() > 64255) {
	        throw new IllegalArgumentException("Trailer weight out of bounds");
	    }
	    
		return weight.intValue() * 2;
	}

	
	/**
	 * ASN.1 Representation:
		VehicleMass ::= INTEGER (0..255)
		-- Values 000 to 080 in steps of 50kg
		-- Values 081 to 200 in steps of 500kg
		-- Values 201 to 253 in steps of 2000kg
		-- The Value 254 shall be used for weights above 170000 kg
		-- The Value 255 shall be used when the value is unknown or unavailable
		-- Encoded such that the values:
		-- 81 represents 4500 kg
		-- 181 represents 54500 kg
		-- 253 represents 170000 kg
	 * @param mass
	 * @return
	 */
	public static Integer genericMass(VehicleMass mass) {
	    
	    if (mass.intValue() < 0 || mass.intValue() > 255) {
	        throw new IllegalArgumentException("Vehicle mass out of bounds");
	    }
	    
		Integer gmass = null;
		int imass = mass.intValue();
		
		if (0 <= imass && imass <= 80) {
			gmass = imass * 50;
		} else if (81 <= imass && imass <= 200) {
			gmass = 80*50 + (imass - 80) * 500;
		} else if (201 <= imass && imass <= 253) {
			gmass = 80*50 + (200-80)*500 + (imass - 200)*2000;
		} else if (254 == imass) {
			gmass = 170001;
		}

		return gmass ;
	}
}
