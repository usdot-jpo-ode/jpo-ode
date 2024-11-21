package us.dot.its.jpo.ode.plugin.j2735.builders;

import com.fasterxml.jackson.databind.JsonNode;

import us.dot.its.jpo.ode.plugin.j2735.DsrcPosition3D;
import us.dot.its.jpo.ode.plugin.j2735.J2735Description;
import us.dot.its.jpo.ode.plugin.j2735.J2735DirectionOfUse;
import us.dot.its.jpo.ode.plugin.j2735.J2735GeographicalPath;
import us.dot.its.jpo.ode.plugin.j2735.J2735RoadSegmentReferenceID;

public class GeographicalPathBuilder {
    private GeographicalPathBuilder() {
        throw new UnsupportedOperationException();
    }

    public static J2735GeographicalPath genericGeographicalPath(JsonNode geographicalPath) {
        J2735GeographicalPath genericGeographicalPath = new J2735GeographicalPath();

        JsonNode name = geographicalPath.get("name");
        if (name != null) {
            genericGeographicalPath.setName(name.asText());
        }

        JsonNode id = geographicalPath.get("id");
        if (id != null) {
            J2735RoadSegmentReferenceID idObj = new J2735RoadSegmentReferenceID();

            JsonNode idRegion = id.get("region");
            if (idRegion != null) {
                idObj.setRegion(idRegion.asInt());
            }

            JsonNode idId = id.get("id");
            if (idId != null) {
                idObj.setId(idId.asInt());
            }

            genericGeographicalPath.setId(idObj);
        }

        JsonNode anchor = geographicalPath.get("anchor");
        if (anchor != null) {
            DsrcPosition3D dsrcPosition3d = Position3DBuilder.dsrcPosition3D(anchor);
            genericGeographicalPath.setAnchor(Position3DBuilder.odePosition3D(dsrcPosition3d));
        }

        JsonNode laneWidth = geographicalPath.get("laneWidth");
        if (laneWidth != null) {
            genericGeographicalPath.setLaneWidth(laneWidth.asInt());
        }

        JsonNode directionality = geographicalPath.get("directionality");
        if (directionality != null) {
            String directionalityValue = directionality.fields().next().getKey();
            genericGeographicalPath.setDirectionality(J2735DirectionOfUse.valueOf(directionalityValue));
        }

        JsonNode closedPath = geographicalPath.get("closedPath");
        if (closedPath != null) {
            boolean closedPathValue = "true".equals(closedPath.fields().next().getKey());
            genericGeographicalPath.setClosedPath(closedPathValue);
        }

        JsonNode description = geographicalPath.get("description");
        if (description != null) {
            J2735Description descriptionObj = new J2735Description();

            JsonNode path = description.get("path");
            if (path != null) {
                descriptionObj.setPath(OffsetSystemBuilder.genericOffsetSystem(path));
            }

            JsonNode geometry = description.get("geometry");
            if (geometry != null) {
                // RoadSignIdBuilder.genericRoadSignId(roadSignID)
                descriptionObj.setGeometry(null);
            }

            JsonNode oldRegion = description.get("oldRegion");
            if (oldRegion != null) {
                descriptionObj.setOldRegion(null);
            }

            genericGeographicalPath.setDescription(descriptionObj);
        }

        return genericGeographicalPath;
    }
}
