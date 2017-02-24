package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.util.Iterator;

import us.dot.its.jpo.ode.j2735.dsrc.TrailerHistoryPoint;
import us.dot.its.jpo.ode.j2735.dsrc.TrailerUnitDescription;
import us.dot.its.jpo.ode.plugin.j2735.J2735TrailerUnitDescription;

public class OssTrailerUnitDescription {

    private OssTrailerUnitDescription() {
    }

    public static J2735TrailerUnitDescription genericTrailerUnitDescription(TrailerUnitDescription tud) {
        J2735TrailerUnitDescription gtud = new J2735TrailerUnitDescription();

        // Required elements
        gtud.isDolly = tud.isDolly.booleanValue();

        if (tud.width.intValue() < 0 || tud.width.intValue() > 1023) {
            throw new IllegalArgumentException("Trailer width value out of bounds [0..1023]");
        } else if (tud.width.intValue() == 0) {
            gtud.width = null;
        } else {
            gtud.width = tud.width.intValue();
        }

        if (tud.length.intValue() < 0 || tud.length.intValue() > 4095) {
            throw new IllegalArgumentException("Trailer length value out of bounds [0..4095]");
        } else if (tud.length.intValue() == 0) {
            gtud.length = null;
        } else {
            gtud.length = tud.length.intValue();
        }

        gtud.frontPivot = OssPivotPointDescription.genericPivotPointDescription(tud.frontPivot);
        gtud.positionOffset = OssNode_XY.genericNode_XY(tud.positionOffset);

        // Optional elements
        if (tud.hasHeight()) {
            gtud.height = OssHeight.genericHeight(tud.height);
        }
        if (tud.hasMass()) {
            gtud.mass = OssMassOrWeight.genericMass(tud.mass);
        }
        if (tud.hasBumperHeights()) {
            gtud.bumperHeights = OssBumperHeights.genericBumperHeights(tud.bumperHeights);
        }
        if (tud.hasCenterOfGravity()) {
            gtud.centerOfGravity = OssHeight.genericHeight(tud.centerOfGravity);
        }
        if (tud.hasRearPivot()) {
            gtud.rearPivot = OssPivotPointDescription.genericPivotPointDescription(tud.rearPivot);
        }
        if (tud.hasRearWheelOffset()) {
            gtud.rearWheelOffset = OssOffset.genericOffset(tud.rearWheelOffset);
        }
        if (tud.hasElevationOffset()) {
            gtud.elevationOffset = OssOffset.genericOffset(tud.elevationOffset);
        }
        if (tud.hasCrumbData()) {
            Iterator<TrailerHistoryPoint> iter = tud.crumbData.elements.iterator();
            while (iter.hasNext()) {
                gtud.crumbData.add(OssTrailerHistoryPoint.genericTrailerHistoryPoint(iter.next()));
            }
        }

        return gtud;
    }

}
