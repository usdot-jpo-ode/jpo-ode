package us.dot.its.jpo.ode.util;

import java.io.*;
import java.security.*;

class GenSig {

    public static void main(String[] args) {

        /* Generate a DSA signature */

        if (args.length != 3) {
            System.out.println("Usage: GenSig nameOfFileToSign");
        }
        else try {

           KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "LunaProvider");
           
           SecureRandom random = SecureRandom.getInstanceStrong();
           keyGen.initialize(2048, random);
           
           KeyPair pair = keyGen.generateKeyPair();
           PrivateKey priv = pair.getPrivate();
           PublicKey pub = pair.getPublic();
           
//           System.out.println("priv: " + priv.toString());
//           System.out.println("pub: " + pub.toString());
           
           Signature dsa = Signature.getInstance("SHA256withRSA", "LunaProvider"); 
           dsa.initSign(priv);
           
           FileInputStream fis = new FileInputStream(args[0]);
           
           BufferedInputStream bufin = new BufferedInputStream(fis);
           byte[] buffer = new byte[1024];
           int len;
           while ((len = bufin.read(buffer)) >= 0) {
               dsa.update(buffer, 0, len);
           };
           bufin.close();
           
           byte[] realSig = dsa.sign();
           
           /* save the signature in a file */
           FileOutputStream sigfos = new FileOutputStream(args[1]);
           sigfos.write(realSig);
           sigfos.close();
           
           /* save the public key in a file */
           byte[] key = pub.getEncoded();
           FileOutputStream keyfos = new FileOutputStream(args[2]);
           keyfos.write(key);
           keyfos.close();
        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
            e.printStackTrace();
        }
    }
}
