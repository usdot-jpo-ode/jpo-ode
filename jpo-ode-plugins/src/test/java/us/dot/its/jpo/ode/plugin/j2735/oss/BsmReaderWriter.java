package us.dot.its.jpo.ode.plugin.j2735.oss;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.oss.asn1.Coder;

import us.dot.its.jpo.ode.j2735.J2735;
import us.dot.its.jpo.ode.j2735.dsrc.BasicSafetyMessage;
import us.dot.its.jpo.ode.plugin.j2735.J2735Bsm;
import us.dot.its.jpo.ode.util.JsonUtils;

public class BsmReaderWriter {

   private static Logger logger = Logger.getLogger(BsmReaderWriter.class
         .getName());

   public static void main(String args[]) {
      String filename = "bsm.json";
      String encoding = "json";
      // Process command line arguments
      if (args.length > 0) {
         for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-i")) {
               filename = new String(args[++i]);
            } else if (args[i].equals("-encoding")) {
               encoding = new String(args[++i]);
            } else {
               System.out.println("usage: BsmReaderWriter reads UPER encoded HEX or binary data from filename"
                       + "and outputs JSON to filename.json."
                       + "If the input file is already in json format as specified by -encloding json option"
                       + "the data is encoded to J2735Bsm POJO and back to JSON before writing to the output file."
                       + "If the input file is binary, DO NOT specify -encoding option. -encoding option is "
                       + "for hex input file and json input file only. [");
               System.out.println("   -i <filename>  ");
               System.out.println("   -encoding <hex|json>  ");
               System.exit(1);
            }
         }
      }

      Coder coder = J2735.getPERUnalignedCoder();
      try {
         FileHandler handler = new FileHandler(filename + ".log", true);
         SimpleFormatter formatter = new SimpleFormatter();
         handler.setFormatter(formatter);
         logger.addHandler(handler);

         logger.info("\n*** BER DECODING BEGIN ***\n");
         long decodeTime = System.currentTimeMillis();
         File inputFile = new File(filename);
         Scanner scanner = new Scanner(inputFile);
         
         PrintWriter bsmOut = null;
         
         int numBSMs = 0;
         OssJ2735Coder ossCoder = new OssJ2735Coder();
         while (scanner.hasNext()) {
            try {
               if (encoding.equalsIgnoreCase("hex")) {
                   bsmOut = new PrintWriter(new PrintStream(filename + ".json"));
                   J2735Bsm bsm = (J2735Bsm) ossCoder.decodeUPERBsmHex(scanner.nextLine());
                   bsmOut.println(bsm.toJson(true));
               } else if (encoding.equalsIgnoreCase("json")) {
                   //encode from JSON to binary UPER
                   bsmOut = new PrintWriter(new PrintStream(filename + ".hex"));
                   J2735Bsm gbsm = (J2735Bsm) JsonUtils.fromJson(
                           scanner.nextLine(), J2735Bsm.class);
                   
                   bsmOut.println(gbsm.toJson(true));
                   
               } else { // assuming binary UPER
                   bsmOut = new PrintWriter(new PrintStream(filename + ".json"));
                   InputStream ins = new FileInputStream(inputFile);

                   if (ins.available() > 0) {
                       BasicSafetyMessage bsm = (BasicSafetyMessage) coder.decode(
                               ins, new BasicSafetyMessage());
                       J2735Bsm gbsm = OssBsm.genericBsm(bsm);
                       
                       bsmOut.println(gbsm.toJson(true));
                   }

                   ins.close();
                   
               }
               numBSMs++;
               
            } catch (Exception e) {
               int errRec = numBSMs + 1;
               System.out.println("Decode Error on BSM # " + errRec + "\n" + e.getMessage());
               e.printStackTrace();
            }
         }
         scanner.close();
         
         if (bsmOut != null)
             bsmOut.close();
         
         decodeTime = System.currentTimeMillis() - decodeTime;

         logger.info("Number of BSMs: " + numBSMs);
         logger.info("Decode Time: " + decodeTime + " ms");
         logger.info("Decode Rate: "
               + (int) (numBSMs / ((double) decodeTime / 1000)) + " PDUs/sec");

      } catch (Exception e) {
         System.out.println(e.getMessage());
         e.printStackTrace();
         System.exit(1);
      } finally {
      }
   }
}
