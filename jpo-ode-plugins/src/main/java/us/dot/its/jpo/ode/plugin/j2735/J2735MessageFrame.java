package us.dot.its.jpo.ode.plugin.j2735;

import us.dot.its.jpo.ode.plugin.asn1.Asn1Object;

public class J2735MessageFrame extends Asn1Object {

    private static final long serialVersionUID = 1L;
    
    private J2735DSRCmsgID messageId;
    private Asn1Object value;
    
    public J2735DSRCmsgID getMessageId() {
        return messageId;
    }
    public void setMessageId(J2735DSRCmsgID messageId) {
        this.messageId = messageId;
    }
    public Asn1Object getValue() {
        return value;
    }
    public void setValue(Asn1Object value) {
        this.value = value;
    }
    
}
