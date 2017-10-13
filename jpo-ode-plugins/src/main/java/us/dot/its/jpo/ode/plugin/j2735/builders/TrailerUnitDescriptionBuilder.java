package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.Iterator;

import us.dot.its.jpo.ode.j2735.dsrc.TrailerHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerUnitDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerUnitDescription;

public class OssTrailerUnitDescription {

    private OssTrailerUnitDescription() {
       throw new UnsupportedOperationException();
    }

    public static J2735TrailerUnitDescription genericTrailerUnitDescription(TrailerUnitDescription tud) {
        J2735TrailerUnitDescription gtud = new J2735TrailerUnitDescription();

        // Required elements
        gtud.setIsDolly(tud.isDolly.booleanValue());

        if (tud.width.intValue() < 0 || tud.width.intValue() > 1023) {
            throw new IllegalArgumentException("Trailer width value out of bounds [0..1023]");
        } else if (tud.width.intValue() == 0) {
            gtud.setWidth(null);
        } else {
            gtud.setWidth(tud.width.intValue());
        }

        if (tud.length.intValue() < 0 || tud.length.intValue() > 4095) {
            throw new IllegalArgumentException("Trailer length value out of bounds [0..4095]");
        } else if (tud.length.intValue() == 0) {
            gtud.setLength(null);
        } else {
            gtud.setLength(tud.length.intValue());
        }

        gtud.setFrontPivot(OssPivotPointDescription.genericPivotPointDescription(tud.frontPivot));
        gtud.setPositionOffset(OssNode_XY.genericNode_XY(tud.positionOffset));

        // Optional elements
        if (tud.hasHeight()) {
            gtud.setHeight(OssHeight.genericHeight(tud.height));
        }
        if (tud.hasMass()) {
            gtud.setMass(OssMassOrWeight.genericMass(tud.mass));
        }
        if (tud.hasBumperHeights()) {
            gtud.setBumperHeights(OssBumperHeights.genericBumperHeights(tud.bumperHeights));
        }
        if (tud.hasCenterOfGravity()) {
            gtud.setCenterOfGravity(OssHeight.genericHeight(tud.centerOfGravity));
        }
        if (tud.hasRearPivot()) {
            gtud.setRearPivot(OssPivotPointDescription.genericPivotPointDescription(tud.rearPivot));
        }
        if (tud.hasRearWheelOffset()) {
            gtud.setRearWheelOffset(OssOffset.genericOffset(tud.rearWheelOffset));
        }
        if (tud.hasElevationOffset()) {
            gtud.setElevationOffset(OssOffset.genericOffset(tud.elevationOffset));
        }
        if (tud.hasCrumbData()) {
            Iterator<TrailerHistoryPoint> iter = tud.crumbData.elements.iterator();
            while (iter.hasNext()) {
                gtud.getCrumbData().add(OssTrailerHistoryPoint.genericTrailerHistoryPoint(iter.next()));
            }
        }

        return gtud;
    }

}
