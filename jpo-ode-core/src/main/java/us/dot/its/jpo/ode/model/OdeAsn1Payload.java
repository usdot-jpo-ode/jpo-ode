package us.dot.its.jpo.ode.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OdeAsn1Payload extends OdeMsgPayload {
   private static final long serialVersionUID = -7540671640610460741L;

    public OdeAsn1Payload(OdeHexByteArray bytes) {
        super(bytes);
        this.setData(bytes);
    }

    public OdeAsn1Payload(byte[] bytes) {
       this(new OdeHexByteArray(bytes));
   }

    public OdeHexByteArray getOdeByteArray() {
        return (OdeHexByteArray) getData();
    }

    public void setOdeByteArray(OdeHexByteArray bytes) {
        setData(bytes);
    }

}
