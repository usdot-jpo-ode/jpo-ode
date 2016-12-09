package us.dot.its.jpo.ode.plugin.j2735;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735HeadingSlice extends Asn1Object {
	private static final long serialVersionUID = 1L;
	
   public enum SliceMask {
      noHeading(0x0000),
      from000_0to022_5degrees(0x0001),
      from022_5to045_0degrees(0x0002),
      from045_0to067_5degrees(0x0004),
      from067_5to090_0degrees(0x0008),
      from090_0to112_5degrees(0x0010),
      from112_5to135_0degrees(0x0020),
      from135_0to157_5degrees(0x0040),
      from157_5to180_0degrees(0x0080),
      from180_0to202_5degrees(0x0100),
      from202_5to225_0degrees(0x0200),
      from225_0to247_5degrees(0x0400),
      from247_5to270_0degrees(0x0800),
      from270_0to292_5degrees(0x1000),
      from292_5to315_0degrees(0x2000),
      from315_0to337_5degrees(0x4000),
      from337_5to360_0degrees(0x8000),
      allHeadings(0xFFFF);
      
      
      private int sliceMask;
   
      private SliceMask(int sliceMask) {
         this.sliceMask = sliceMask;
      }
      
      
      public int getSliceMask() {
         return sliceMask;
      }
      public void setSliceMask(int sliceMask) {
         this.sliceMask = sliceMask;
      }
      
      public static List<SliceMask> getHeadingSlices(byte[] headingSlice) {
         ArrayList<SliceMask> result = new ArrayList<SliceMask>();
         short hs = ByteBuffer.wrap(headingSlice).order(ByteOrder.BIG_ENDIAN).getShort();
         
         if (hs == allHeadings.getSliceMask()) {
            result.add(allHeadings);
         } else if (hs == noHeading.getSliceMask()) {
            result.add(noHeading);
         } else {
            for (SliceMask value : SliceMask.values()) {
               if (value != noHeading && value != allHeadings &&
                     (hs&value.getSliceMask()) == value.getSliceMask()) {
                  result.add(value);
               }
            }
         }
         return result;
      }
   }
   
   private short headingSlice;

   public J2735HeadingSlice(byte[] headingSlice) {
      this.headingSlice = ByteBuffer.wrap(headingSlice).order(ByteOrder.BIG_ENDIAN).getShort();
   }

   public int getHeadingSlice() {
      return headingSlice;
   }

   public void setHeadingSlice(short headingSlice) {
      this.headingSlice = headingSlice;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + headingSlice;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      J2735HeadingSlice other = (J2735HeadingSlice) obj;
      if (headingSlice != other.headingSlice)
         return false;
      return true;
   }

   
}
