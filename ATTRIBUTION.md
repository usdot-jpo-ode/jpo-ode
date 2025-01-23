The key contributors to the development of the USDOT ITS JPO ODE and related files are:
* Dwayne Henclewood - Project Manager (Booz Allen)
* Tamer Morsi - Agile coach and Scrum master (Booz Allen)
* Hamid Musavi - Solution Architect and Technical Lead (Booz Allen)
* Mathew Schwartz - Software Engineer (Booz Allen)

The developers also wish to acknowledge the following leaders and contributors:
* US DOT: Ariel Gold
* Volpe: Kristin Tufte
* Booz Allen: Ram Kandarpa and Brien Miller
* TriHydro: Tony English, Shane Zumpf and Kim Perry
* Oakridge National Laboratories: Jason Carter and Aaron Ferber

Thanks to the ITS Joint Program Office for their support of the effort.

The J2735 Traveler Information classes were generated using asn1jvm, which is a new ASN.1 compiler targeting Java. The asn1jvm tool is currently capable of compiling the 2016 version of the J2735 ASN.1 specification to Java classes which are capable of serializing and deserializing XER and JER.  The output of the tool includes classes with Jackson annotations for each type in the specification, and a runtime library containing base classes for ASN.1 types and custom Jackson serializers and deserialers. The raw output of the tool and associated runtime library are here: https://github.com/iyourshaw/j2735-2016-java
 
The generated classes were edited for compatibility with the 2020 version of the specification as follows:
 
* TimDatFrame.java, fields renamed:
	* sspTimRights -> notUsed
	* sspLocationRights -> notUsed1
	* sspMsgRights1 -> notUsed2
	* sspMsgRights2 -> notUsed3
	* duratonTime -> durationTime
* Classes were moved to Java packages to be consistent with the module organization scheme in the 2020+ versions of J2735, and with Java package naming conventions (lowercase), and existing ODE package naming. Specifically, instead being in a `DSRC` package, the TIM-related classes were moved to `us.dot.its.jpo.ode.plugin.j2735.travelerinformation`, and `us.dot.its.jpo.ode.plugin.j2735.common` packages.
 
The top level TravelerInformation class was also edited, by changing its base class to `us.dot.its.jpo.ode.plugin.asn1.Asn1Object` to enable it to plug directily into the existing `OdeData`/`OdeMsgPayload` data structure.