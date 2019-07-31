/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class GeoUtils {
	public static final double ERROR_MARGIN = 0.0001;
	public static final QELLIPSOID ellipsoid = new QELLIPSOID(6378206.4, 6356583.8, 0.08227185423947);

	public static final double EARTH_RADIUS_M = 6370997.0;
	public static final double EARTH_RADIUS_MI = 3963.2263272;

	public static final ProjParams proj = new ProjParams(0.0);

	private GeoUtils() {
	}

	public static class QELLIPSOID {
		private double dEqRadius = 6378206.4;
		private double dPolRadius = 6356583.8;
		private double dEccentricity = 0.08227185423947;

		public QELLIPSOID() {
			super();
		}

		public QELLIPSOID(double dEqRadius, double dPolRadius, double dEccentricity) {
			this();
			this.dEqRadius = dEqRadius;
			this.dPolRadius = dPolRadius;
			this.dEccentricity = dEccentricity;
		}

    public double getdEqRadius() {
      return dEqRadius;
    }

    public void setdEqRadius(double dEqRadius) {
      this.dEqRadius = dEqRadius;
    }

    public double getdPolRadius() {
      return dPolRadius;
    }

    public void setdPolRadius(double dPolRadius) {
      this.dPolRadius = dPolRadius;
    }

    public double getdEccentricity() {
      return dEccentricity;
    }

    public void setdEccentricity(double dEccentricity) {
      this.dEccentricity = dEccentricity;
    }

	}

	public static class ProjParams {
		private double dCentralMeridian;

		public ProjParams() {
			super();
		}

		public ProjParams(double dCentralMeridian) {
			this();
			this.dCentralMeridian = dCentralMeridian;
		}

	}

	public static Point2D nearestPointOnLine(Line2D l, Point2D p, boolean clampToSegment, Point2D dest) {
		if (dest == null) {
			dest = new Point2D.Double();
		}

		double apx = p.getX() - l.getX1();
		double apy = p.getY() - l.getY1();
		double abx = l.getX2() - l.getX1();
		double aby = l.getY2() - l.getY1();

		double ab2 = abx * abx + aby * aby;
		double ap_ab = apx * abx + apy * aby;
		double t = ap_ab / ab2;
		if (clampToSegment) {
			if (t < 0) {
				t = 0;
			} else if (t > 1) {
				t = 1;
			}
		}
		dest.setLocation(l.getX1() + abx * t, l.getY1() + aby * t);
		return dest;
	}

	public static double lineLength(Point2D a, Point2D b) {
		double abx = Math.abs(b.getX() - a.getX());
		double aby = Math.abs(b.getY() - a.getY());
		return Math.sqrt(abx * abx + aby * aby);
	}

	public static double distanceToLine(Line2D l, Point2D p) {

		return l.ptLineDist(p);
	}

	public static double distanceToLine2(Line2D l, Point2D p) {

		Point2D p2 = nearestPointOnLine(l, p, true, null);
		return p.distance(p2);
	}

	public static double distanceToLine3(Line2D l, Point2D p)
	// Calculate the distance between the point (nX, nY) and the line through
	// the
	// points (nP1X, nP1Y), (nP2X, nP2Y).
	{
		double dDist = 0;

		if (l.getX1() == l.getX2())
			// Vertical line
			dDist = p.getX() - l.getX1();
		else if (l.getY1() == l.getY2())
			// Horizontal line
			dDist = p.getY() - l.getY1();
		else {
			// Figure out the slope and Y intercept of the line
			double dM1 = ((double) l.getY2() - l.getY1()) / ((double) l.getX2() - l.getX1());
			double dB1 = l.getY1() - (dM1 * l.getX1());
			// Figure out the slope and Y intercept of the perpendicular line
			// through the third point
			double dM2 = -(1 / dM1);
			double dB2 = p.getY() - (dM2 * p.getX());

			// Find the intersection of the two lines
			double dXInt, dYInt;
			dXInt = (dB2 - dB1) / (dM1 - dM2);
			dYInt = (dM2 * dXInt) + dB2;

			// Now calulate the distance between the point and the intesection
			// of
			// the two lines.
			dDist = Math.sqrt(Math.pow(dXInt - p.getX(), 2) + Math.pow(dYInt - p.getY(), 2));
		}

		return Math.abs(dDist);
	}

	/**
	 * This function returns a Point2D that is offset to a point 'a' by distance
	 * 'k' and perpendicular to the line 'a-b'
	 * 
	 * b / dest / *--- m ----- / | / l k / | a
	 * 
	 * @param a
	 * @param b
	 * @param k
	 * @param dest
	 * @return
	 */
	public static Point2D pointOffset(Point2D a, Point2D b, double k, Point2D dest) {
		if (dest == null) {
			dest = new Point2D.Double();
		}

		double tanAlpha = (b.getY() - a.getY()) / (b.getX() - a.getX());
		double alpha = Math.atan(tanAlpha);
		double sinAlpha = Math.sin(alpha);
		double cosAlpha = Math.cos(alpha);

		double destX;
		double destY;
		if (b.getX() < a.getX()) {
			destX = (a.getX() + k * sinAlpha);
			destY = (a.getY() - k * cosAlpha);
		} else {
			destX = (a.getX() - k * sinAlpha);
			destY = (a.getY() + k * cosAlpha);
		}

		dest.setLocation(destX, destY);
		return dest;
	}

	public static double DEG2RAD(double deg) {
		return ((double) deg * (Math.PI / 180.0));
	}

	public static double RAD2DEG(double rad) {
		return ((double) rad * (180.0 / Math.PI));
	}

	public static Point2D latLngToMap(double dLat, double dLng) {
		Point2D p = new Point2D.Double();

		// Calculate map x
		double px = ellipsoid.dEqRadius * DEG2RAD(dLng - proj.dCentralMeridian);

		double onePlusESine, oneMinusESine, tangent;
		double eSine;
		double divPowerE;
		double lnValue;

		eSine = ellipsoid.dEccentricity * Math.sin(DEG2RAD(dLat));

		onePlusESine = 1 + eSine;
		oneMinusESine = 1 - eSine;

		divPowerE = Math.pow((oneMinusESine / onePlusESine), (ellipsoid.dEccentricity / 2.0));

		tangent = Math.tan(DEG2RAD((45.0 + dLat / 2.0)));

		lnValue = tangent * divPowerE;
		double py = ellipsoid.dEqRadius * Math.log(lnValue);

		p.setLocation(px, py);
		return p;
	}

	public static Point2D mapToLatLng(Point2D d) {
		// Calculate the lat
		double t;
		double onePlusESine, oneMinusESine;
		double arcTangent;
		double divPowerE;
		double error = 0.0;
		Point2D p = new Point2D.Double();

		t = Math.exp(-(d.getY() / ellipsoid.dEqRadius));
		// Initial value far the lat

		double lat0 = 90 - 2 * RAD2DEG(Math.atan(t));

		int i = 0;
		do {
			onePlusESine = 1 + (ellipsoid.dEccentricity * Math.sin(DEG2RAD(lat0)));
			oneMinusESine = 1 - (ellipsoid.dEccentricity * Math.sin(DEG2RAD(lat0)));

			divPowerE = Math.pow((oneMinusESine / onePlusESine), (ellipsoid.dEccentricity / 2.0));
			arcTangent = RAD2DEG(Math.atan(t * divPowerE));
			double lat = 90 - (2 * arcTangent);
			error = Math.abs(lat - lat0);
			lat0 = lat;
			i++; // to avoid infinite loop;
		} while (error > ERROR_MARGIN && i < 5);

		// Calculate the longitude
		double lng = RAD2DEG(d.getY() / ellipsoid.dEqRadius) + proj.dCentralMeridian;
		p.setLocation(lat0, lng);

		return p;
	}

	public static double distance(Point2D a, Point2D b) {
		return a.distance(b);
	}

	public static double distanceLatLng(double dLat1, double dLng1, double dLat2, double dLng2,
			LengthUtils.UnitOfMeasure u) {
		double cd;

		// special case
		if (dLat1 == dLat2 && dLng1 == dLng2)
			return 0.0;

		double dDiff = dLng1 - dLng2;
		int nSegs = (int) (Math.abs(dDiff) / 180.0);
		double dDistance = 0;

		if (nSegs > 0 && dLat1 == dLat2) {
			// if the span is bigger than 180 degrees, adjust the distance
			cd = Math.sin(DEG2RAD(dLat1)) * Math.sin(DEG2RAD(dLat2))
					+ Math.cos(DEG2RAD(dLat1)) * Math.cos(DEG2RAD(dLat2)) * Math.cos(DEG2RAD(180.0));

			cd = EARTH_RADIUS_M * Math.acos(cd); // This is in miles
			dDistance = nSegs * cd;
			dDiff = Math.abs(dDiff) - 180.0 * nSegs;
		}

		cd = Math.sin(DEG2RAD(dLat1)) * Math.sin(DEG2RAD(dLat2))
				+ Math.cos(DEG2RAD(dLat1)) * Math.cos(DEG2RAD(dLat2)) * Math.cos(DEG2RAD(dDiff));

		cd = EARTH_RADIUS_M * Math.acos(cd); // This is in meters

		cd += dDistance;

		switch (u) {
		case KM:
			cd = LengthUtils.MILES2KM(cd);
			break;
		case YARD:
			cd = (LengthUtils.MILES2KM(cd) * 1000.0) / 0.9; // 90cm = 1 yard
			break;
		case MILE:
			cd = LengthUtils.METERS2MILES(cd) * 1000.0;
			break;
		case NAUTICAL_MILE:
			cd = LengthUtils.MILES2KNOTS(cd);
			break;
		case M:
		default:
			// already cd is in meters. we don't have to convert.
			break;
		}

		return cd;
	}

	public static double distanceXY(double aLat, double aLng, double bLat, double bLng) {

		Point2D a = GeoUtils.latLngToMap(aLat, aLng);
		Point2D b = GeoUtils.latLngToMap(bLat, bLng);

		return a.distance(b);
	}

	public static boolean isPointWithinBounds(Point2D p, Line2D l, double tolerance) {
		// Make sure the point p is inside the bounding rectangle of the segment
		// a-b
		return ((p.getX() >= (Math.min(l.getX1(), l.getX2()) - tolerance))
				&& (p.getX() <= (Math.max(l.getX1(), l.getX2()) + tolerance))
				&& (p.getY() >= (Math.min(l.getY1(), l.getY2()) - tolerance))
				&& (p.getY() <= (Math.max(l.getY1(), l.getY2()) + tolerance)));
	}
}
