package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.J2735Connection;
import us.dot.its.jpo.ode.plugin.j2735.J2735ConnectingLane;
import us.dot.its.jpo.ode.plugin.j2735.J2735BitString;
import us.dot.its.jpo.ode.plugin.j2735.J2735AllowedManeuvers;

public class ConnectionBuilder {
    private ConnectionBuilder() {
		throw new UnsupportedOperationException();
	}
	
    public static J2735Connection genericConnection(JsonNode connectionNode) {
		J2735Connection connection = new J2735Connection();

        JsonNode connectingLaneNode = connectionNode.get("connectingLane");
		if (connectingLaneNode != null) {
            J2735ConnectingLane connectingLaneObj = new J2735ConnectingLane();

            JsonNode lane = connectingLaneNode.get("lane");
		    if (lane != null) {
                connectingLaneObj.setLane(lane.asInt());
            }

            JsonNode maneuver = connectingLaneNode.get("maneuver");
		    if (maneuver != null) {
                J2735BitString maneuverObj = BitStringBuilder.genericBitString(maneuver, J2735AllowedManeuvers.values());
                connectingLaneObj.setManeuver(maneuverObj);
            }

            connection.setConnectingLane(connectingLaneObj);
		}
 
        return connection;
    }
}
