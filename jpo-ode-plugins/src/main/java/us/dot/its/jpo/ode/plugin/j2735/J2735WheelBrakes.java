package us.dot.its.jpo.ode.plugin.j2735;

public class J2735WheelBrakes {

   private static final long serialVersionUID = 1L;

   private boolean leftFront;
   private boolean leftRear;
   private boolean rightFront;
   private boolean rightRear;
   private boolean unvailable;

   public boolean isLeftFront() {
      return leftFront;
   }

   public void setLeftFront(boolean leftFront) {
      this.leftFront = leftFront;
   }

   public boolean isLeftRear() {
      return leftRear;
   }

   public void setLeftRear(boolean leftRear) {
      this.leftRear = leftRear;
   }

   public boolean isRightFront() {
      return rightFront;
   }

   public void setRightFront(boolean rightFront) {
      this.rightFront = rightFront;
   }

   public boolean isRightRear() {
      return rightRear;
   }

   public void setRightRear(boolean rightRear) {
      this.rightRear = rightRear;
   }

   public boolean isUnvailable() {
      return unvailable;
   }

   public void setUnvailable(boolean unvailable) {
      this.unvailable = unvailable;
   }

}
