package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.COERCoder;
import com.oss.asn1.Coder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.JSONCoder;
import com.oss.asn1.PERUnalignedCoder;

import gov.usdot.asn1.generated.ieee1609dot2.Ieee1609dot2;
import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.util.CodecUtils;

public class BsmReaderWriter {

    private static final String IN_ENCODING = "in_encoding";
    private static final String OUT_ENCODING = "out_encoding";
    private static final String HEX = "hex";
    private static final String JSON = "json";
    private static final String UPER = "uper";
    private static final String IN_FILENAME = "in_filename";
    private static String inFilename = "bsm.uper";
    private static String inEncoding = UPER;
    private static String outEncoding = UPER;

    private static Logger logger = LoggerFactory.getLogger(BsmReaderWriter.class);
    private static File inputFile;
    private static String outFilename;
    private static PERUnalignedCoder uperCoder;
    private static JSONCoder jsonCoder;
    private static COERCoder coerCoder;
    private static PrintWriter stringOut;
    private static FileOutputStream binOut;
    
    public static void main(String args[]) throws FileNotFoundException {
        // Process command line arguments
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-" + IN_FILENAME)) {
                    inFilename = new String(args[++i]);
                } else if (args[i].equals("-" + IN_ENCODING)) {
                    inEncoding = new String(args[++i]);
                } else if (args[i].equals("-" + OUT_ENCODING)) {
                    outEncoding = new String(args[++i]);
                } else {
                    System.out.println("usage: BsmReaderWriter reads UPER binary, UPER HEX or JSON from " + IN_FILENAME
                            + " and outputs Signed COER binary, Signed COER HEX or JSON to " + IN_FILENAME + ".out");
                    System.out.println("   -" + IN_FILENAME + " <in_filename>  ");
                    System.out.println("   -" + IN_ENCODING + " <" + UPER + "|" + HEX + "|" + JSON + ">");
                    System.out.println("   -" + OUT_ENCODING + " <" + UPER + "|" + HEX + "|" + JSON + ">");
                    System.exit(1);
                }
            }
        }

        inputFile = new File(inFilename);

        uperCoder = J2735.getPERUnalignedCoder();
        jsonCoder = J2735.getJSONCoder();
        coerCoder = Ieee1609dot2.getCOERCoder();

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
                numBSMs = readEncoding(jsonCoder);
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

    private static int readEncoding(Coder coder) throws IOException {
        int numBSMs = 0;

        try (InputStream ins = new FileInputStream(inputFile)) {
            while (ins.available() > 0) {
                try {
                    BasicSafetyMessage bsm = new BasicSafetyMessage();
                    coder.decode(ins, bsm);
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

    private static int readHexFile() throws FileNotFoundException {
        int numBSMs = 0;

        try (Scanner scanner = new Scanner(inputFile)) {
            while (scanner.hasNext()) {
                try {
                    String line = scanner.nextLine();
                    InputStream ins = new ByteArrayInputStream(CodecUtils.fromHex(line));

                    BasicSafetyMessage bsm = new BasicSafetyMessage();
                    uperCoder.decode(ins, bsm);
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
            throws EncodeFailedException, EncodeNotSupportedException, IOException {
        switch (outEncoding ) {
            case HEX:
                stringOut.println(CodecUtils.toHex(uperCoder.encode(bsm).array()));
                break;
            case JSON:
                stringOut.println(jsonCoder.encode(bsm));
                break;
            case UPER:
                binOut.write(uperCoder.encode(bsm).array());
                break;
            default:
                logger.error("Invalid output encoding: {}", outEncoding);
        }
        

    }
}
