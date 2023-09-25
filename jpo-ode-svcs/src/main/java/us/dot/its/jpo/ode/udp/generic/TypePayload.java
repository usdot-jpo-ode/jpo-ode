package us.dot.its.jpo.ode.udp.generic;

public class TypePayload {
    
    private MessageType type;
    private byte[] payload;

    public TypePayload(MessageType type, byte[] payload){
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public byte[] getPayload() {
        return payload;
    }


    public void setPayload(byte[] payload) {
        this.payload = payload;
    }


    public String toString(){
        return "{ \"type\":" + type + "\"payload\":" + payload + "}"; 
    }

}