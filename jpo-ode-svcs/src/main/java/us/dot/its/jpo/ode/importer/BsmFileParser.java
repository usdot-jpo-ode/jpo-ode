package us.dot.its.jpo.ode.importer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;

import us.dot.its.jpo.ode.util.CodecUtils;

public class BsmFileParser implements LogFileParser {
    public static final int MAX_PAYLOAD_SIZE = 2048;
    
    private static final int DIRECTION_LENGTH = 1;
    private static final int UTC_TIME_IN_SEC_LENGTH = 4;
    private static final int MSEC_LENGTH = 2;
    private static final int VERIFICATION_STATUS_LENGTH = 1;
    private static final int LENGTH_LENGTH = 2;
    public static final int MAX_INPUT_BUFFER_SIZE = MAX_PAYLOAD_SIZE 
            + DIRECTION_LENGTH 
            + UTC_TIME_IN_SEC_LENGTH 
            + MSEC_LENGTH
            + VERIFICATION_STATUS_LENGTH
            + LENGTH_LENGTH;

    private byte[] readBuffer = new byte[MAX_INPUT_BUFFER_SIZE];
    private int step = 0;

    private String filename;
    private BsmSource   direction; //0 for EV(Tx), 1 for RV(Rx)
    private long  utctimeInSec;
    private short  mSec;
    private boolean verificationStatus;
    private short  length;
    private byte[] payload;
    
    public ParserStatus parse (BufferedInputStream bis, String fileName) throws IOException {
        ParserStatus status = ParserStatus.INIT;
        
        if (step == 0) {
            setFilename(fileName);
            step++;
        }
        
        int numBytes;
        
        //Step 1
        if (step == 1) {
            numBytes = bis.read(readBuffer, 0, DIRECTION_LENGTH);
            if (numBytes < 0) {
                return ParserStatus.EOF;
            } else if (numBytes < DIRECTION_LENGTH) {
                return ParserStatus.PARTIAL;
            } else {
                setDirection(BsmSource.values()[readBuffer[0]]);
                step++;
            }
        }        
        //Step 2
        if (step == 2) {
            numBytes = bis.read(readBuffer, 0, UTC_TIME_IN_SEC_LENGTH);
            if (numBytes < 0) {
                return ParserStatus.EOF;
            } else if (numBytes < UTC_TIME_IN_SEC_LENGTH) {
                return ParserStatus.PARTIAL;
            } else {
                setUtctimeInSec(CodecUtils.bytesToInt(readBuffer, 0, UTC_TIME_IN_SEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
                step++;
            }
        }        
        //Step 3
        if (step == 3) {
            numBytes = bis.read(readBuffer, 0, MSEC_LENGTH);
            if (numBytes < 0) {
                return ParserStatus.EOF;
            } else if (numBytes < MSEC_LENGTH) {
                return ParserStatus.PARTIAL;
            } else {
                setmSec(CodecUtils.bytesToShort(readBuffer, 0, MSEC_LENGTH, ByteOrder.LITTLE_ENDIAN));
                step++;
            }
        }
        //Step 4
        if (step == 4) {
            if (getDirection() == BsmSource.EV_TX) {
                setVerificationStatus(true);
                step++;
            } else {
                numBytes = bis.read(readBuffer, 0, VERIFICATION_STATUS_LENGTH);
                if (numBytes < 0) {
                    return ParserStatus.EOF;
                } else if (numBytes < VERIFICATION_STATUS_LENGTH) {
                    return ParserStatus.PARTIAL;
                } else {
                    setVerificationStatus(readBuffer[0] == 0 ? false : true);
                    step++;
                }
            }
        }
        //Step 5
        if (step == 5) {
            numBytes = bis.read(readBuffer, 0, LENGTH_LENGTH);
            if (numBytes < 0) {
                return ParserStatus.EOF;
            } else if (numBytes < LENGTH_LENGTH) {
                return ParserStatus.PARTIAL;
            } else {
                setLength(CodecUtils.bytesToShort(readBuffer, 0, LENGTH_LENGTH, ByteOrder.LITTLE_ENDIAN));
                step++;
            }
        }
        //Step 6
        if (step == 6) {
            numBytes = bis.read(readBuffer, 0, getLength());
            if (numBytes < 0) {
                return ParserStatus.EOF;
            } else if (numBytes < getLength()) {
                return ParserStatus.PARTIAL;
            } else {
                setPayload(Arrays.copyOf(readBuffer, numBytes));
                step++;
            }
        }
        
        step = 0;
        status = ParserStatus.COMPLETE;
        
        return status;
    }

    public int getStep() {
        return step;
    }
    public void setStep(int step) {
        this.step = step;
    }
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
    
//    public ParserStatus parse (BufferedInputStream bis, String fileName) throws IOException {
//        ParserStatus status = ParserStatus.UNKNOWN;
//        
//        setFilename(fileName);
//        byte[] readBuffer = new byte[MAX_INPUT_BUFFER_SIZE];
//        int numBytes = bis.read(readBuffer, 0, MAX_INPUT_BUFFER_SIZE);
//        if (numBytes < 0) {
//            return ParserStatus.EOF;
//        }
//
//        ByteBuffer bb = ByteBuffer.wrap(readBuffer).order(ByteOrder.BIG_ENDIAN);
//
//        try {
//            setDirection(BsmSource.values()[bb.get()]);
//            setUtctimeInSec(bb.getInt());
//            setmSec(bb.getShort());
//            setLength(bb.getShort());
//            bb.get(payload, 0, getLength());
//            if (getDirection() == BsmSource.EV_TX) {
//                setVerificationStatus(true);
//            } else {
//                setVerificationStatus(bb.get() == 0 ? false : true);
//            }
//        } catch (BufferUnderflowException e) {
//            return ParserStatus.PARTIAL;
//        }
//
//        status = ParserStatus.COMPLETE;
//        
//        return status;
//    }

}
