package us.dot.its.jpo.ode.plugin.j2735.oss;

import us.dot.its.jpo.ode.j2735.dsrc.HeadingSlice;
import us.dot.its.jpo.ode.plugin.j2735.J2735HeadingSlice;

public class OssHeadingSlice {

   private OssHeadingSlice() {
   }

   public static J2735HeadingSlice genericHeadingSlice(HeadingSlice direction) {
      J2735HeadingSlice headingSlice = new J2735HeadingSlice();
      
      for (int i = 0; i < direction.getSize(); i++) {

          String headingBitName = direction.getNamedBits().getMemberName(i);
          Boolean headingBitStatus = direction.getBit(i);

          if (headingBitName != null) {
              headingSlice.put(headingBitName, headingBitStatus);
          }
      }
      return headingSlice;
      
   }

}
