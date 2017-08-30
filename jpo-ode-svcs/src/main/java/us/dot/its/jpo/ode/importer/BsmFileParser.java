package us.dot.its.jpo.ode.importer;

import java.io.BufferedInputStream;

public class BsmFileParser implements LogFileParser {
    public final int MAX_PAYLOAD_SIZE = 2048;
    
    private String filename;
    private BsmSource   direction; //0 for EV(Tx), 1 for RV(Rx)
    private long  utctimeInSec;
    private short  mSec;
    private boolean verificationStatus;
    private short  length;
    private byte[]   payload;
    
    public String getFilename() {
        return filename;
    }
    public BsmFileParser setFilename(String filename) {
        this.filename = filename;
        return this;
    }
    public BsmSource getDirection() {
        return direction;
    }
    public BsmFileParser setDirection(BsmSource direction) {
        this.direction = direction;
        return this;
    }
    public long getUtctimeInSec() {
        return utctimeInSec;
    }
    public BsmFileParser setUtctimeInSec(long utctimeInSec) {
        this.utctimeInSec = utctimeInSec;
        return this;
    }
    public short getmSec() {
        return mSec;
    }
    public BsmFileParser setmSec(short mSec) {
        this.mSec = mSec;
        return this;
    }
    public boolean isVerificationStatus() {
        return verificationStatus;
    }
    public BsmFileParser setVerificationStatus(boolean verificationStatus) {
        this.verificationStatus = verificationStatus;
        return this;
    }
    public short getLength() {
        return length;
    }
    public BsmFileParser setLength(short length) {
        this.length = length;
        return this;
    }
    public byte[] getPayload() {
        return payload;
    }
    public BsmFileParser setPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }
    
    public BsmFileParser parse (BufferedInputStream is, String fileName) {
        // TODO Auto-generated method stub
        return this;
    }
}
