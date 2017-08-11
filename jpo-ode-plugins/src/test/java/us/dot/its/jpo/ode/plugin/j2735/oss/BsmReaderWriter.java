package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.PERUnalignedCoder;

import gov.usdot.cv.security.cert.CertificateException;
import gov.usdot.cv.security.cert.CertificateManager;
import gov.usdot.cv.security.cert.CertificateWrapper;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.crypto.CryptoProvider;
import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.j2735.dsrc.MessageFrame;
import us.dot.its.jpo.ode.plugin.OdePlugin;
import us.dot.its.jpo.ode.plugin.j2735.J2735DSRCmsgID;
import us.dot.its.jpo.ode.plugin.j2735.oss.OssMessageFrame.OssMessageFrameException;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BsmReaderWriter {

    private static final String IN_ENCODING = "in_encoding";
    private static final String OUT_ENCODING = "out_encoding";
    private static final String HEX = "hex";
    private static final String JSON = "json";
    private static final String UPER = "uper";
    private static final String IN_FILENAME = "in_filename";
    private static final String OUT_SIGNED = "out_signed";
    private static final int PSID_BSM = 32;
    
    private static String inFilename = "bsm.uper";
    private static String inEncoding = UPER;
    private static String outEncoding = UPER;
    private static boolean outSigned = true;

    private static Logger logger = LoggerFactory.getLogger(BsmReaderWriter.class);
    private static File inputFile;
    private static String outFilename;
    private static PERUnalignedCoder uperCoder;
    private static PrintWriter stringOut;
    private static FileOutputStream binOut;
    private static String selfCert = "self.cert";
    private static String selfCertPrivateKeyReconstructionValue = "self.s";
    private static String signingPrivateKey = "sign.prv";
    private static String caCert = "ca.cert";

    public static void main(String args[]) throws Exception  {
        // Process command line arguments
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-" + IN_FILENAME)) {
                    inFilename = new String(args[++i]);
                } else if (args[i].equals("-" + IN_ENCODING)) {
                    inEncoding = new String(args[++i]);
                } else if (args[i].equals("-" + OUT_ENCODING)) {
                    outEncoding = new String(args[++i]);
                } else if (args[i].equals("-" + OUT_SIGNED)) {
                    outSigned = Boolean.valueOf(new String(args[++i]));
                } else {
                    System.out.println("usage: BsmReaderWriter reads UPER binary, UPER HEX or JSON from " + IN_FILENAME
                            + " and outputs Signed COER binary, Signed COER HEX or JSON to " + IN_FILENAME + ".out");
                    System.out.println("   -" + IN_FILENAME + " <in_filename>  ");
                    System.out.println("   -" + IN_ENCODING + " <" + UPER + "|" + HEX + "|" + JSON + ">");
                    System.out.println("   -" + OUT_ENCODING + " <" + UPER + "|" + HEX + "|" + JSON + ">");
                    System.out.println("   -" + OUT_SIGNED + " <true|false>");
                    System.exit(1);
                }
            }
        }

        if (outSigned) {
            loadCertificates();
        }
        
        inputFile = new File(inFilename);

        uperCoder = J2735.getPERUnalignedCoder();

        stringOut  = null;
        binOut  = null;
        switch (outEncoding ) {
            case HEX:
                outFilename = inFilename + "." + HEX;
                stringOut = new PrintWriter(new PrintStream(outFilename)); 
                break;
            case JSON:
                outFilename = inFilename + "." + JSON;
                stringOut = new PrintWriter(new PrintStream(outFilename)); 
                break;
            case UPER:
                outFilename = inFilename + "." + UPER;
                binOut = new FileOutputStream(outFilename); 
                break;
            default:
                logger.error("Invalid output encoding: {}", outEncoding);
                return;
        }

        try {
            System.setProperty("log.name", inFilename + ".log");

            logger.info("\n*** DECODING BEGIN ***\n");
            long startTime = System.currentTimeMillis();

            int numBSMs = 0;
            if (inEncoding.equalsIgnoreCase(HEX)) {
                numBSMs = readHexFile();
            } else if (inFilename.endsWith(JSON)) {
                numBSMs = readEncoding(uperCoder);
            } else {
                numBSMs = readEncoding(uperCoder);
            }
            
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Number of BSMs: " + numBSMs);
            logger.info("Decode Time: " + duration + " ms");
            logger.info("Decode Rate: "
                  + (int) (numBSMs / ((double) duration / 1000)) + " PDUs/sec");
            
        } catch (Exception e) {
            logger.error("Unable to open or process file: " + inFilename, e);
        }
    }

    private static void loadCertificates() 
            throws IOException, DecodeFailedException, EncodeFailedException, CertificateException, DecoderException, CryptoException, DecodeNotSupportedException, EncodeNotSupportedException {
        CertificateManager.clear();

        caCert = Hex.encodeHexString(Files.readAllBytes(Paths.get(".", caCert)));

        signingPrivateKey  = Hex.encodeHexString(Files.readAllBytes(Paths.get(".", signingPrivateKey)));
        
        selfCert = Hex.encodeHexString(Files.readAllBytes(Paths.get(".", selfCert)));
        
        selfCertPrivateKeyReconstructionValue = Hex.encodeHexString(Files.readAllBytes(Paths.get(".", selfCertPrivateKeyReconstructionValue)));
        
        CryptoProvider.initialize();
        
        CryptoProvider cryptoProvider = new CryptoProvider();
        
        if ( !load(cryptoProvider, CertificateWrapper.getRootPublicCertificateFriendlyName(), caCert, null, null) )
            throw new CertificateException("Couldn't load CA certificate.");

        if ( !load(cryptoProvider, IEEE1609p2Message.getSelfCertificateFriendlyName(), selfCert, selfCertPrivateKeyReconstructionValue, signingPrivateKey) )
            throw new CertificateException("Couldn't load self certificate.");
    }

    public static boolean load(
        CryptoProvider cryptoProvider,
        String name,
        String hexCert,
        String hexPrivateKeyReconstructionValue,
        String hexSigningPrivateKey) throws CertificateException, IOException, DecoderException, CryptoException,
            DecodeFailedException, DecodeNotSupportedException, EncodeFailedException, EncodeNotSupportedException {
        byte[] certBytes = Hex.decodeHex(hexCert.toCharArray());
        CertificateWrapper cert;
        if (hexPrivateKeyReconstructionValue == null && hexSigningPrivateKey == null) {
            cert = CertificateWrapper.fromBytes(cryptoProvider, certBytes);
        } else {
            byte[] privateKeyReconstructionValueBytes = Hex.decodeHex(hexPrivateKeyReconstructionValue.toCharArray());
            byte[] signingPrivateKeyBytes = Hex.decodeHex(hexSigningPrivateKey.toCharArray());
            cert = CertificateWrapper.fromBytes(cryptoProvider, certBytes, privateKeyReconstructionValueBytes,
                signingPrivateKeyBytes);
        }
        if (cert != null) {
            boolean isValid = cert.isValid();
            logger.debug("Certificate is valid: " + isValid);
            if (isValid)
                CertificateManager.put(name, cert);
            return isValid;
        }
        return false;
    }

    private static int readEncoding(Coder coder) throws IOException {
        int numBSMs = 0;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile))) {
            while (bis.available() > 0) {
                try {
                    if (bis.markSupported()) {
                        bis.mark(OdePlugin.INPUT_STREAM_BUFFER_SIZE);
                    }
                    BasicSafetyMessage bsm = getBasicSafetyMessage(coder, bis);
                    output(bsm);
                    numBSMs++;

                } catch (Exception e) {
                    if (bis.markSupported()) {
                        try {
                            bis.reset();
                        } catch (IOException ioe) {
                            logger.error("Error reseting Input Stream to marked position", ioe);
                        }
                    }
                    int errRec = numBSMs + 1;
                    System.out.println("Decode Error on BSM # " + errRec + "\n" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        return numBSMs;

    }

    private static BasicSafetyMessage getBasicSafetyMessage(Coder coder, InputStream ins)
            throws DecodeNotSupportedException, DecodeFailedException {
        BasicSafetyMessage bsm = null;
        try {
            MessageFrame mf = new MessageFrame();
            coder.decode(ins, mf);
            if (J2735DSRCmsgID.BASICSAFETYMESSAGE.getMsgID() == mf.getMessageId().intValue()) {

                // If basicSafetyMessage
                if (mf.value.getDecodedValue() != null) {
                    bsm = (BasicSafetyMessage) mf.value.getDecodedValue();
                } else if (mf.value.getEncodedValueAsStream() != null) {
                    bsm = new BasicSafetyMessage();
                    try {
                        coder.decode(mf.value.getEncodedValueAsStream(), bsm);
                    } catch (DecodeFailedException | DecodeNotSupportedException e) {
                        throw new OssMessageFrameException("Error decoding OpenType value", e);
                    }
                } else {
                    throw new OssMessageFrameException("No OpenType value");
                }

            } else {
                throw new OssMessageFrameException("Unknown message type: " + mf.getMessageId().intValue());
            }
        } catch (Exception e) {
            bsm = new BasicSafetyMessage();
            coder.decode(ins, bsm);
        }
        return bsm;
    }

    private static int readHexFile() throws FileNotFoundException {
        int numBSMs = 0;

        try (Scanner scanner = new Scanner(inputFile)) {
            while (scanner.hasNext()) {
                try {
                    String line = scanner.nextLine();
                    InputStream ins = new ByteArrayInputStream(CodecUtils.fromHex(line));

                    BasicSafetyMessage bsm = getBasicSafetyMessage(uperCoder, ins);
                    output(bsm);
                    numBSMs++;

                } catch (Exception e) {
                    int errRec = numBSMs + 1;
                    System.out.println("Decode Error on BSM # " + errRec + "\n" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return numBSMs;

    }

    private static void output(BasicSafetyMessage bsm) 
            throws EncodeFailedException, EncodeNotSupportedException, IOException, CertificateException, CryptoException {
        if (outSigned) {
            IEEE1609p2Message signer = new IEEE1609p2Message();
            byte[] signedBsm = signMessage(bsm, signer);
            
            switch (outEncoding ) {
            case HEX:
                stringOut.println(CodecUtils.toHex(signedBsm));
                break;
            case JSON:
          //      stringOut.println(JsonUtils.toJson(signer.getIeee1609Dot2Data(), true));
                break;
            case UPER:
                binOut.write(signedBsm);
                break;
            default:
                logger.error("Invalid output encoding: {}", outEncoding);
            }
        } else {
            switch (outEncoding ) {
            case HEX:
                stringOut.println(CodecUtils.toHex(uperCoder.encode(bsm).array()));
                break;
            case JSON:
                stringOut.println(JsonUtils.toJson(bsm, true));
                break;
            case UPER:
                binOut.write(uperCoder.encode(bsm).array());
                break;
            default:
                logger.error("Invalid output encoding: {}", outEncoding);
            }
        }
        
        

    }

    private static byte[] signMessage(BasicSafetyMessage bsm, IEEE1609p2Message signer) 
            throws EncodeFailedException, EncodeNotSupportedException, CertificateException, CryptoException {
        byte[] signedMsg;
        signer.setPSID(PSID_BSM);
        // send signed (with certificate) ServiceRequest in 1609.2 envelope
        signedMsg = signer.sign(uperCoder.encode(bsm).array());
        return signedMsg;
    }
}
