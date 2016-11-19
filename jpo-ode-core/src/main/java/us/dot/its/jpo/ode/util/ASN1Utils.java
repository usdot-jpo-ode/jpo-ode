package us.dot.its.jpo.ode.util;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.j2735.dsrc.Heading;

public class ASN1Utils {
   
   public static BigDecimal convert(Heading heading) {
      if (heading != null)
         return BigDecimal.valueOf(heading.intValue() * 125, 4);
      else
         return null; 
   }

}
