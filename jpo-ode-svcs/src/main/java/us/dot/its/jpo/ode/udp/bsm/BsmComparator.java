package us.dot.its.jpo.ode.udp.bsm;

import java.util.Comparator;

import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;

/**
 * Comparator for the priority queue to keep the chronological order of bsms
 */
public class BsmComparator implements Comparator<J2735Bsm> {

   @Override
   public int compare(J2735Bsm x, J2735Bsm y) {
      // TODO - determine message arrival time
      // for now we are using the BSM's time offset property

      int xt = x.getCoreData().getSecMark();
      int yt = y.getCoreData().getSecMark();

      return Integer.compare(xt, yt);
   }

}
