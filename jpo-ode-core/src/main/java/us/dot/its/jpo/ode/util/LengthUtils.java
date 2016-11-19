package us.dot.its.jpo.ode.util;

public class LengthUtils {
   public enum UnitOfMeasure {
      MM, CM, M, KM, INCH, FOOT, YARD, MILE, NAUTICAL_MILE
   }

   public static double MILES2KM(double m) {return ((double)m * 1.609347);}
   
   public static double KM2MILES(double m) {return ((double)m / 1.609347);}

   public static double MILES2METERS(double m) {return ((double)m * 1609.347);}
   public static double METERS2MILES(double m) {return ((double)m / 1609.347);}

   public static double MILES2KNOTS(double m) {return ((double)m / 1.150568);}
   public static double KNOTS2MILES(double m) {return ((double)m * 1.150568);}

   public static double MILES2YARDS(double m) {return ((double)m * 1760.0);}
   public static double YARDS2MILES(double m) {return ((double)m / 1760.0);}
}
