package us.dot.its.jpo.ode.plugin.j2735;

public enum J2735FuelType {
	UNKNOWNFUEL, // FuelType::= 0 -- Gasoline Powered
	GASOLINE, // FuelType::= 1
	ETHANOL, // FuelType::= 2 -- Including blends
	DIESEL, // FuelType::= 3 -- All types
	ELECTRIC, // FuelType::= 4
	HYBRID, // FuelType::= 5 -- All types
	HYDROGEN, // FuelType::= 6
	NATGASLIQUID, // FuelType::= 7 -- Liquefied
	NATGASCOMP, // FuelType::= 8 -- Compressed
	PROPANE // FuelType::= 9
}
