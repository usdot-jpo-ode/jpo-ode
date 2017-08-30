package us.dot.its.jpo.ode.importer;

import java.io.BufferedInputStream;

public class BsmFileParser implements LogFileParser {
    public final int MAX_PAYLOAD_SIZE = 2048;
    
    private BsmSource   direction; //0 for EV(Tx), 1 for RV(Rx)
    private long  utctimeInSec;
    private short  mSec;
    private boolean verificationStatus;
    private short  length;
    private byte[]   payload;
    
    public BsmSource getDirection() {
        return direction;
    }
    public void setDirection(BsmSource direction) {
        this.direction = direction;
    }
    public long getUtctimeInSec() {
        return utctimeInSec;
    }
    public void setUtctimeInSec(long utctimeInSec) {
        this.utctimeInSec = utctimeInSec;
    }
    public short getmSec() {
        return mSec;
    }
    public void setmSec(short mSec) {
        this.mSec = mSec;
    }
    public boolean isVerificationStatus() {
        return verificationStatus;
    }
    public void setVerificationStatus(boolean verificationStatus) {
        this.verificationStatus = verificationStatus;
    }
    public short getLength() {
        return length;
    }
    public void setLength(short length) {
        this.length = length;
    }
    public byte[] getPayload() {
        return payload;
    }
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
    
    public void parse (BufferedInputStream is) {
        // TODO Auto-generated method stub
    }
}
