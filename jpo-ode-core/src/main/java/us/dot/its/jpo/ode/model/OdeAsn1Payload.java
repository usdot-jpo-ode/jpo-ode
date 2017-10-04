package us.dot.its.jpo.ode.model;

public class OdeAsn1Payload extends OdeMsgPayload {
   private static final long serialVersionUID = -7540671640610460741L;

    public OdeAsn1Payload() {
      super();
   }

   public OdeAsn1Payload(OdeObject data) {
      super(data);
   }

   public OdeAsn1Payload(String dataType, OdeObject data) {
      super(dataType, data);
   }

   public OdeAsn1Payload(OdeHexByteArray bytes) {
        super(bytes);
        this.setData(bytes);
    }

    public OdeAsn1Payload(byte[] bytes) {
       this(new OdeHexByteArray(bytes));
   }

}
