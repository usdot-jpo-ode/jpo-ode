package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;


public class J2735RegionalContent implements Asn1Object{
	public enum RegionId {
				noRegion(J2735RegionalExtension.class), //Use default supplied stubs
				addGrpA(J2735RegionalExtension.class),  //RegionId ::= 1 -- USA
				addGrpB(J2735RegionalExtension.class),  //RegionId ::= 2 -- Japan
				addGrpC(J2735RegionalExtension.class)   //RegionId ::= 3 -- EU
				; 
		
		Class<?> type;

		private RegionId(Class<?> type) {
			this.type = type;
		}
		
		
	}

	public Integer id;
	public J2735RegionalExtension value;
}
