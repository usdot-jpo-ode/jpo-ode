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
