package us.dot.its.jpo.ode.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import us.dot.its.jpo.ode.asn.OdeGeoRegion;
import us.dot.its.jpo.ode.asn.OdePosition3D;

public class GeoUtils {

   public static class QELLIPSOID {
      public double dEqRadius = 6378206.4;
      public double dPolRadius = 6356583.8;
      public double dEccentricity = 0.08227185423947;

      public QELLIPSOID() {
         super();
      }

      public QELLIPSOID(double dEqRadius, double dPolRadius,
            double dEccentricity) {
         this();
         this.dEqRadius = dEqRadius;
         this.dPolRadius = dPolRadius;
         this.dEccentricity = dEccentricity;
      }

   }

   public static class PROJ_PARAMS {
      public double dCentralMeridian;

      public PROJ_PARAMS() {
         super();
      }

      public PROJ_PARAMS(double dCentralMeridian) {
         this();
         this.dCentralMeridian = dCentralMeridian;
      }

   }

   public static final double ERROR_MARGIN = 0.0001;
   public static final QELLIPSOID ellipsoid = new QELLIPSOID(6378206.4,
         6356583.8, 0.08227185423947);

   public static final double EARTH_RADIUS_M = 6370997.0;
   public static final double EARTH_RADIUS_MI = 3963.2263272;
   
   public static final PROJ_PARAMS proj = new PROJ_PARAMS(0.0);

   // private void setEllipsoid(double dEqRadius, double dPolRadius, double
   // dEccentricity)
   // {
   // ellipsoid.dEqRadius = dEqRadius;
   // ellipsoid.dPolRadius = dPolRadius;
   // ellipsoid.dEccentricity = dEccentricity;
   // }
   //
   public static Point2D nearestPointOnLine(Line2D l, Point2D p,
         boolean clampToSegment, Point2D dest) {
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
   // Calculate the distance between the point (nX, nY) and the line through the
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

         // Now calulate the distance between the point and the intesection of
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
   public static Point2D pointOffset(Point2D a, Point2D b, double k,
         Point2D dest) {
      if (dest == null) {
         dest = new Point2D.Double();
      }

      // if (b.getX() == a.getX()) {
      // if (b.getY() > a.getY())
      // dest.setLocation(a.getX() - k, a.getY());
      // else
      // dest.setLocation(a.getX() + k, a.getY());
      // } else if (b.getY() == a.getY()) {
      // if (b.getX() > a.getX())
      // dest.setLocation(a.getX(), a.getY() + k);
      // else
      // dest.setLocation(a.getX(), a.getY() - k);
      // } else {
      // double l = Math.sqrt(
      // (k*k*Math.pow((b.getX()-a.getX()), 2))
      // /
      // (Math.pow((b.getY()-a.getY()), 2) + Math.pow((b.getX() - a.getX()),
      // 2)));
      //
      // double m = (l * (b.getY() - a.getY())/(b.getX() - a.getX()));
      //
      // dest.setLocation(a.getX() - m, a.getY() + l);
      // }

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

      divPowerE = Math.pow((oneMinusESine / onePlusESine),
            (ellipsoid.dEccentricity / 2.0));

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

         divPowerE = Math.pow((oneMinusESine / onePlusESine),
               (ellipsoid.dEccentricity / 2.0));
         arcTangent = RAD2DEG(Math.atan(t * divPowerE));
         double lat = 90 - (2 * arcTangent);
         error = Math.abs(lat - lat0);
         lat0 = lat;
         i++; // to avoid infinite loop;
      } while (error > ERROR_MARGIN && i < 5);

      // Calculate the longitude
      double lng = RAD2DEG(d.getY() / ellipsoid.dEqRadius)
            + proj.dCentralMeridian;
      p.setLocation(lat0, lng);

      return p;
   }

   public static double distance (Point2D a, Point2D b) {
      return a.distance(b);
   }
   
   public static double distanceLatLng(double dLat1, double dLng1,
         double dLat2, double dLng2, LengthUtils.UnitOfMeasure u) {
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
               + Math.cos(DEG2RAD(dLat1)) * Math.cos(DEG2RAD(dLat2))
               * Math.cos(DEG2RAD(180.0));

         cd = EARTH_RADIUS_M * Math.acos(cd); // This is in miles
         dDistance = nSegs * cd;
         dDiff = Math.abs(dDiff) - 180.0 * nSegs;
      }

      cd = Math.sin(DEG2RAD(dLat1)) * Math.sin(DEG2RAD(dLat2))
            + Math.cos(DEG2RAD(dLat1)) * Math.cos(DEG2RAD(dLat2))
            * Math.cos(DEG2RAD(dDiff));

      cd = EARTH_RADIUS_M * Math.acos(cd); // This is in meters

      cd += dDistance;

      switch(u)
      {
      case KM:
         cd = LengthUtils.MILES2KM(cd);
         break;
      case YARD:
         cd = (LengthUtils.MILES2KM(cd) * 1000.0)/ 0.9; // 90cm = 1 yard 
         break;
      case MILE:
         cd = LengthUtils.METERS2MILES(cd) * 1000.0;      
         break;
      case NAUTICAL_MILE:
         cd = LengthUtils.MILES2KNOTS(cd);
         break;
      case M :
      default:
         // already cd is in meters. we don't have to convert.
         break;
      }
      
      return cd;
   }

   public static double distanceXY(double aLat, double aLng,
         double bLat, double bLng) {

      Point2D a = GeoUtils.latLngToMap(aLat, aLng);
      Point2D b = GeoUtils.latLngToMap(bLat, bLng);
      
      return a.distance(b);
   }

   public static boolean isPointWithinBounds(Point2D p, Line2D l, double tolerance) {
      // Make sure the point p is inside the bounding rectangle of the segment
      // a-b
      return ( (p.getX() >= (Math.min(l.getX1(), l.getX2()) - tolerance))
            && (p.getX() <= (Math.max(l.getX1(), l.getX2()) + tolerance))
            && (p.getY() >= (Math.min(l.getY1(), l.getY2()) - tolerance)) 
            && (p.getY() <= (Math.max(l.getY1(), l.getY2()) + tolerance)));
   }

   public static boolean isPositionWithinRegion(OdePosition3D pos, OdeGeoRegion region) {
      if (pos == null || region == null)
         return false;
      
      OdePosition3D nw = region.getNwCorner();
      OdePosition3D se = region.getSeCorner();
      
      if (nw == null || nw.getLatitude() == null || pos == null || pos.getLatitude().doubleValue() > nw.getLatitude().doubleValue())
         return false;
      if (nw.getLongitude() == null || pos.getLongitude().doubleValue() < nw.getLongitude().doubleValue())
         return false;
      if (se == null || se.getLatitude() == null || pos.getLatitude().doubleValue() < se.getLatitude().doubleValue())
         return false;
      if (se.getLongitude() == null || pos.getLongitude().doubleValue() > se.getLongitude().doubleValue())
         return false;
      
      return true;
   }

   // public static void snapToPathSegment(Path2D path, Point2D p) {
   // PathIterator pathIter = path.getPathIterator(null);
   //
   // double dClosestDist = EDITROUTE_HIT_TOLERANCE;
   //
   // while (!pathIter.isDone()) {
   // if (isPointOnLine(p, a, b, EDITROUTE_HIT_TOLERANCE)) {
   //
   // }
   // }
   // }
   //
   // public static Rectangle2D getLineSurround(PathIterator pathIter) {
   // Rectangle2D head = null;
   // double[] coords = new double[6];
   // int segType = pathIter.currentSegment(coords);
   // if (segType != PathIterator.SEG_CLOSE) {
   // // We assume that the segment is not SEG_CLOSE
   // double headX = coords[0];
   // double headY = coords[1];
   //
   // head = new Point2D.Double(headX, headY);
   // }
   // return head;
   // }
   //
   // public static String selectRouteSegment(Path2D segments, Point2D ptHitScr)
   // {
   // int nRetVal;
   //
   // PathIterator pathIter = segments.getPathIterator(null);
   //
   // if (pathIter.isDone())
   // // No points in route
   // return null;
   //
   // // Loop through the points of the route and find the closest point within
   // the tolerance
   // int nIndex = 0;
   // double dClosestDist = EDITROUTE_HIT_TOLERANCE;
   // int nRouteCount = getSegmentCount(segments);
   // Point2D head = getCurrentPoint(pathIter);
   //
   // //Go through all the routes
   // for (int nSelected=0; nSelected<nRouteCount; nSelected++) {
   // double f_dTotalRouteDistance = 0.0; // for the next route
   // pathIter.next();
   // Point2D pos = getCurrentPoint(pathIter);
   //
   // if (pos != null) {
   // while (pos != null) {
   // DISTPOINT *pPoint = (DISTPOINT*) pPointsList->GetNext (pos);
   // if (pPoint != NULL) {
   // int nScrX, nScrY;
   //
   // f_pGeoTransform->MapToScreen ((pPoint->x), (pPoint->y), &nScrX, &nScrY);
   //
   // double dDist;
   // dDist = sqrt (pow (ptHitScr.x - nScrX, 2) + pow (ptHitScr.y - nScrY, 2));
   // if ((dDist < EDITROUTE_HIT_TOLERANCE) && (dDist < dClosestDist))
   // {
   // SelectPointFromIndex (nIndex);
   // dClosestDist = dDist;
   // }
   // }
   // nIndex++;
   // }
   //
   // if (f_nSelectedRtePartType != RTEPART_TYPE_NONE) // Got a point
   // return 1;
   //
   // // Read the first point
   // pos = pPointsList->GetHeadPosition ();
   // if (pos == NULL)
   // return 0;
   //
   // //the first point ALWAYS contains the color and the category.
   // if (pPointsList != NULL) {
   // DISTPOINT *pd = (DISTPOINT*)pPointsList->GetNext(pos);
   // }
   //
   // // Loop through the segments of the route and find the closest one within
   // the tolerance
   // DISTPOINT *pPoint1 = NULL, *pPoint2 = NULL;
   // int nCurrIndex = 1; // 1 because already one of the point is read
   // pPoint1 = (DISTPOINT*) pPointsList->GetNext (pos);
   // if (pPoint1 == NULL)
   // return 0;
   //
   // dClosestDist = EDITROUTE_HIT_TOLERANCE;
   //
   // while (pos != NULL) {
   // pPoint2 = (DISTPOINT*) pPointsList->GetNext (pos);
   // if (pPoint2 != NULL) {
   // // Now we have a segment (two points) which we can test against.
   // // First convert the points from Map to Screen coords.
   // int nScrX1, nScrY1, nScrX2, nScrY2;
   // f_pGeoTransform->MapToScreen ((pPoint1->x), (pPoint1->y), &nScrX1,
   // &nScrY1);
   // f_pGeoTransform->MapToScreen ((pPoint2->x), (pPoint2->y), &nScrX2,
   // &nScrY2);
   //
   // double dRteDist;
   // f_pGeoTransform->GetDistanceScreen(nScrX1, nScrY1, nScrX2, nScrY2,
   // &dRteDist, *f_pnDistUnit);
   // f_dTotalRouteDistance += dRteDist;
   //
   // // Make sure the hit point is inside the bounding rectangle of the segment
   // if ((ptHitScr.x >= (min (nScrX1, nScrX2) - EDITROUTE_HIT_TOLERANCE)) &&
   // (ptHitScr.x <= (max (nScrX1, nScrX2) + EDITROUTE_HIT_TOLERANCE)) &&
   // (ptHitScr.y >= (min (nScrY1, nScrY2) - EDITROUTE_HIT_TOLERANCE)) &&
   // (ptHitScr.y <= (max (nScrY1, nScrY2) + EDITROUTE_HIT_TOLERANCE))) {
   // // Calculate the distance between the hit point and the segment
   // double dDist;
   // dDist = DistanceBetweenPointAndLine (ptHitScr.x, ptHitScr.y, nScrX1,
   // nScrY1, nScrX2, nScrY2);
   //
   // if ((dDist < EDITROUTE_HIT_TOLERANCE) && (dDist < dClosestDist)) {
   // f_pGeoTransform->GetDistanceScreen(nScrX1, nScrY1, nScrX2, nScrY2, &dDist,
   // *f_pnDistUnit);
   // // if shift key is down
   // if (nFlags & MK_SHIFT) {
   // if (f_nFirstSelRtePartIndex == -1) { // there are no already selected
   // segments
   // // set first and last indices to crnt index
   // f_nLastSelRtePartIndex = f_nFirstSelRtePartIndex = nCurrIndex;
   // f_nSelectedRtePartSegIndex = nCurrIndex;
   // nRetVal = 1;
   // }
   // else {
   // // clicked on already selected segment
   // if ( (f_nFirstSelRtePartIndex <= nCurrIndex) && (f_nLastSelRtePartIndex >=
   // nCurrIndex) ) {
   // f_nSelectedRtePartSegIndex = nCurrIndex;
   // nRetVal = 2;
   // }
   // else if (f_nFirstSelRtePartIndex < nCurrIndex) {
   // if (f_nLastSelRtePartIndex == nCurrIndex - 1) {
   // f_nLastSelRtePartIndex = nCurrIndex;
   // f_nSelectedRtePartSegIndex = nCurrIndex;
   // nRetVal = 1;
   // }
   // else // non continuous
   // return -1;
   // }
   // else if (f_nFirstSelRtePartIndex > nCurrIndex) {
   // if (f_nFirstSelRtePartIndex == nCurrIndex + 1) {
   // f_nSelectedRtePartSegIndex = nCurrIndex;
   // f_nFirstSelRtePartIndex = nCurrIndex;
   // nRetVal = 1;
   // }
   // else // non continuous
   // return -1;
   // }
   // else // if segments are not continuous
   // return -1;
   // }
   // // add all the selected route distance
   // if (nRetVal != 2)
   // f_dSelectedSegmentDistance = f_dSelectedSegmentDistance + dDist;
   // }
   // else {
   // f_nFirstSelRtePartIndex = f_nLastSelRtePartIndex = -1;
   // f_dSelectedSegmentDistance = dDist;
   // f_nSelectedRtePartSegIndex = nCurrIndex;
   // nRetVal = 1;
   // }
   // f_nSelectedRtePartType = RTEPART_TYPE_SEGMENT;
   // f_nSelectedRteIndex = nSelected + 1; // Index of the route in
   // f_pListOfRoutes
   // dClosestDist = dDist;
   // }
   // }
   // }
   //
   // pPoint1 = pPoint2;
   // pPoint2 = NULL;
   // nCurrIndex++;
   // }
   //
   // // go through the loop to deduct the dist
   // if (nRetVal == 2) {
   // int nDistIndex = 0;
   // int nTmpX1, nTmpY1, nTmpX2, nTmpY2;
   //
   // DISTPOINT *pTmpPoint1 = NULL, *pTmpPoint2 = NULL;
   // POSITION posTmp = pPointsList->GetHeadPosition ();
   // pTmpPoint1 = (DISTPOINT*) pPointsList->GetNext (posTmp);
   // if (posTmp == NULL)
   // return 0;
   // while (nDistIndex != f_nSelectedRtePartSegIndex) {
   // pTmpPoint1 = (DISTPOINT*) pPointsList->GetNext (posTmp);
   // if (pTmpPoint1 == NULL)
   // return 0;
   // //pTmpPoint2 = (DISTPOINT*) pPointsList->GetNext (posTmp);
   // nDistIndex++;
   // }
   //
   // double dTmpDist;
   // pTmpPoint1 = (DISTPOINT*) pPointsList->GetNext (posTmp);
   // if (pTmpPoint1 == NULL)
   // return 0;
   // while (nDistIndex != f_nLastSelRtePartIndex) {
   // pTmpPoint2 = (DISTPOINT*) pPointsList->GetNext (posTmp);
   // if (pTmpPoint2 == NULL)
   // return 0;
   //
   // f_pGeoTransform->MapToScreen ((pTmpPoint1->x), (pTmpPoint1->y), &nTmpX1,
   // &nTmpY1);
   // f_pGeoTransform->MapToScreen ((pTmpPoint2->x), (pTmpPoint2->y), &nTmpX2,
   // &nTmpY2);
   // f_pGeoTransform->GetDistanceScreen(nTmpX1, nTmpY1, nTmpX2, nTmpY2,
   // &dTmpDist, *f_pnDistUnit);
   // f_dSelectedSegmentDistance = f_dSelectedSegmentDistance - dTmpDist;
   //
   // pTmpPoint1 = pTmpPoint2;
   // nDistIndex++;
   // }
   //
   // f_nLastSelRtePartIndex = f_nSelectedRtePartSegIndex;
   // }
   // if (f_nSelectedRtePartType != RTEPART_TYPE_NONE) // Got a point
   // return nRetVal;
   //
   // }
   // f_nFirstSelRtePartIndex = f_nLastSelRtePartIndex = -1;
   // return 0;
   // }
   //
   // private static int getSegmentCount(Path2D segments) {
   // int count = 0;
   // PathIterator iter = segments.getPathIterator(null);
   //
   // while (!iter.isDone()) count++;
   //
   // return count;
   // }
}