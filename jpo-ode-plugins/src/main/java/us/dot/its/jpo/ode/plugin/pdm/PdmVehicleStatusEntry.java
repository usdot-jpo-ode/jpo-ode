package us.dot.its.jpo.ode.plugin.pdm;

public class PdmVehicleStatusEntry {
   private int rsuPDMVSReqTag;   // 15628.4.1.200.16.1.1    INTEGER (0..28)
   private int rsuPDMVSReqSubTag; // 15628.4.1.200.16.1.2   INTEGER (1..15)
   private int rsuPDMVSReqLessThenValue; // 15628.4.1.200.16.1.3  INTEGER (-32767..32767)
   private int rsuPDMVSReqMoreThenValue; // 15628.4.1.200.16.1.4  INTEGER (-32767..32767)
   private int rsuPDMVSReqSendAll; // 15628.4.1.200.16.1.5  INTEGER (0|1)
   private int rsuPDMVSReqStatus; // 15628.4.1.200.16.1.6   INTEGER (0|1)

   public PdmVehicleStatusEntry() {
       // empty constructor
   }

   public int getRsuPDMVSReqTag() {
      return rsuPDMVSReqTag;
   }

   public void setRsuPDMVSReqTag(int rsuPDMVSReqTag) {
      this.rsuPDMVSReqTag = rsuPDMVSReqTag;
   }

   public int getRsuPDMVSReqSubTag() {
      return rsuPDMVSReqSubTag;
   }

   public void setRsuPDMVSReqSubTag(int rsuPDMVSReqSubTag) {
      this.rsuPDMVSReqSubTag = rsuPDMVSReqSubTag;
   }

   public int getRsuPDMVSReqLessThenValue() {
      return rsuPDMVSReqLessThenValue;
   }

   public void setRsuPDMVSReqLessThenValue(int rsuPDMVSReqLessThenValue) {
      this.rsuPDMVSReqLessThenValue = rsuPDMVSReqLessThenValue;
   }

   public int getRsuPDMVSReqMoreThenValue() {
      return rsuPDMVSReqMoreThenValue;
   }

   public void setRsuPDMVSReqMoreThenValue(int rsuPDMVSReqMoreThenValue) {
      this.rsuPDMVSReqMoreThenValue = rsuPDMVSReqMoreThenValue;
   }

   public int getRsuPDMVSReqSendAll() {
      return rsuPDMVSReqSendAll;
   }

   public void setRsuPDMVSReqSendAll(int rsuPDMVSReqSendAll) {
      this.rsuPDMVSReqSendAll = rsuPDMVSReqSendAll;
   }

   public int getRsuPDMVSReqStatus() {
      return rsuPDMVSReqStatus;
   }

   public void setRsuPDMVSReqStatus(int rsuPDMVSReqStatus) {
      this.rsuPDMVSReqStatus = rsuPDMVSReqStatus;
   }
}
