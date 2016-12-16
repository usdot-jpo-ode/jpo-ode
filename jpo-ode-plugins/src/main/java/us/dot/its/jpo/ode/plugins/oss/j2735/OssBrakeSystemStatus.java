package us.dot.its.jpo.ode.plugins.oss.j2735;

import us.dot.its.jpo.ode.j2735.dsrc.BrakeSystemStatus;
import us.dot.its.jpo.ode.plugin.j2735.J2735BrakeSystemStatus;

public class OssBrakeSystemStatus {

	public static J2735BrakeSystemStatus genericBrakeSystemStatus(
			BrakeSystemStatus brakesStatus) {
		J2735BrakeSystemStatus genericBrakesStatus = new J2735BrakeSystemStatus();
		   
		//      BrakeSystemStatus ::= OCTET STRING (SIZE(2))
		//            -- Encoded with the packed content of: 
		//            -- SEQUENCE {
		//            --   wheelBrakes        BrakeAppliedStatus,
		//            --                      -x- 4 bits
		//            --   wheelBrakesUnavailable  BOOL
		//            --                      -x- 1 bit (1=true)
		//            --   spareBit
		//            --                      -x- 1 bit, set to zero
		//            --   traction           TractionControlState,
		//            --                      -x- 2 bits
		//            --   abs                AntiLockBrakeStatus, 
		//            --                      -x- 2 bits
		//            --   scs                StabilityControlStatus,
		//            --                      -x- 2 bits
		//            --   brakeBoost         BrakeBoostApplied, 
		//            --                      -x- 2 bits
		//            --   auxBrakes          AuxiliaryBrakeStatus,
		//            --                      -x- 2 bits
		//            --   }
		
		//       BrakeAppliedStatus ::= BIT STRING {
		//          allOff      (0), -- B'0000  The condition All Off 
		//          leftFront   (1), -- B'0001  Left Front Active
		//          leftRear    (2), -- B'0010  Left Rear Active
		//          rightFront  (4), -- B'0100  Right Front Active
		//          rightRear   (8)  -- B'1000  Right Rear Active
		//      } -- to fit in 4 bits
		
		genericBrakesStatus.wheelBrakes = 
				OssBitString.genericBitString(brakesStatus.wheelBrakes);
		
		//      TractionControlState ::= ENUMERATED {
		//         unavailable (0), -- B'00  Not Equipped with tracton control 
		//                          -- or tracton control status is unavailable
		//         off         (1), -- B'01  tracton control is Off
		//         on          (2), -- B'10  tracton control is On (but not Engaged)
		//         engaged     (3)  -- B'11  tracton control is Engaged
		genericBrakesStatus.traction = 
				brakesStatus.traction.name();
		 
		//      AntiLockBrakeStatus ::= ENUMERATED {
		//         unavailable (0), -- B'00  Vehicle Not Equipped with ABS 
		//                          -- or ABS status is unavailable
		//         off         (1), -- B'01  Vehicle's ABS is Off
		//         on          (2), -- B'10  Vehicle's ABS is On (but not engaged)
		//         engaged     (3)  -- B'11  Vehicle's ABS is Engaged
		//         } 
		//         -- Encoded as a 2 bit value
		genericBrakesStatus.abs = 
				brakesStatus.abs.name();
		 
		//      StabilityControlStatus ::= ENUMERATED {
		//         unavailable (0), -- B'00  Not Equipped with SC
		//                          -- or SC status is unavailable
		//         off         (1), -- B'01  Off
		//         on          (2)  -- B'10  On or active (engaged)
		//         } 
		//         -- Encoded as a 2 bit value
		genericBrakesStatus.scs = 
				brakesStatus.scs.name();

		//      BrakeBoostApplied ::= ENUMERATED {
		//         unavailable   (0), -- Vehicle not equipped with brake boost
		//                            -- or brake boost data is unavailable
		//         off           (1), -- Vehicle's brake boost is off
		//         on            (2)  -- Vehicle's brake boost is on (applied)
		//         }
		//         -- Encoded as a 2 bit value
		genericBrakesStatus.brakeBoost = 
				brakesStatus.brakeBoost.name();
		 
		//      AuxiliaryBrakeStatus ::= ENUMERATED {
		//         unavailable (0), -- B'00  Vehicle Not Equipped with Aux Brakes 
		//                          -- or Aux Brakes status is unavailable
		//         off         (1), -- B'01  Vehicle's Aux Brakes are Off
		//         on          (2), -- B'10  Vehicle's Aux Brakes are On ( Engaged )
		//         reserved    (3)  -- B'11 
		//         } 
		//         -- Encoded as a 2 bit value
		genericBrakesStatus.auxBrakes = 
				brakesStatus.auxBrakes.name();
		
		return genericBrakesStatus;
	}

}
