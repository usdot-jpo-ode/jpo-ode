package us.dot.its.jpo.ode.util;

import java.math.BigDecimal;

import com.bah.ode.asn.oss.dsrc.Heading;

public class ASN1Utils {
   
   public static BigDecimal convert(Heading heading) {
      if (heading != null)
         return BigDecimal.valueOf(heading.intValue() * 125, 4);
      else
         return null; 
   }

}
