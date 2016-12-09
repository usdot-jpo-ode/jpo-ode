package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public enum J2735FuelType {
	unknownFuel, // FuelType::= 0 -- Gasoline Powered
	gasoline, // FuelType::= 1
	ethanol, // FuelType::= 2 -- Including blends
	diesel, // FuelType::= 3 -- All types
	electric, // FuelType::= 4
	hybrid, // FuelType::= 5 -- All types
	hydrogen, // FuelType::= 6
	natGasLiquid, // FuelType::= 7 -- Liquefied
	natGasComp, // FuelType::= 8 -- Compressed
	propane // FuelType::= 9
}
