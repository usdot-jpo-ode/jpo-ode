package us.dot.its.jpo.ode.plugin.j2735;

import java.math.BigDecimal;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735Radius extends Asn1Object {

   private static final long serialVersionUID = -1615313015072982197L;

   public Integer km_chosen;
   public Integer miles_chosen;
   public BigDecimal cm_chosen;
   
   //TODO Move to us.dot.its.jpo.ode.plugin.j2735.oss.OssRaduis class
//   public OdeRadius(Raduis radius) {
//      super();
//
//      int flag = radius.getChosenFlag();
//      switch (flag) {
//      case Raduis.km_chosen:
//         if (radius.hasKm())
//            setChosenField("km_chosen", Integer.valueOf(radius.getKm().intValue()));
//         break;
//      case Raduis.miles_chosen:
//         if (radius.hasMiles())
//            setChosenField("miles_chosen", Integer.valueOf(radius.getMiles().intValue()));
//         break;
//      case Raduis.radiusSteps_chosen:
//         if (radius.hasRadiusSteps())
//            setChosenField("cm_chosen", BigDecimal.valueOf(radius.getRadiusSteps().intValue() * 2.5));
//         break;
//      }
//   }

  
}