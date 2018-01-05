package us.dot.its.jpo.ode.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.safenetinc.luna.LunaSlotManager;
import com.safenetinc.luna.provider.LunaCertificateX509;

import gov.usdot.cv.security.cert.CertificateManager;
import gov.usdot.cv.security.cert.CertificateWrapper;
import gov.usdot.cv.security.crypto.CryptoProvider;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.util.CodecUtils;

@Controller
public class SecurityController {

   private Logger logger = LoggerFactory.getLogger(this.getClass());

   private String cryptoProvider;
   private KeyStore keyStore;

   private AlgorithmParameters parameters;

   private Certificate enrollmentCert;

   private static final String LUNA_PROVIDER = "LunaProvider";
   private static final String BOUNCYCASTLE_PROVIDER = "BC";

   @Autowired
   protected SecurityController(OdeProperties odeProps) {
      super();

      Executors.newSingleThreadExecutor().submit(new CertificateLoader(odeProps));
   }

   @Bean
   @DependsOn("slotManager")
   KeyStore keyStore(
      @Value("${ode.keyStoreProvider}") String keystoreProvider,
      @Value("${ode.hsmSlotNumber}") String slot,
      @Value("${ode.hsmTokenPassword}") String password) throws EncodeFailedException, gov.usdot.cv.security.cert.CertificateException, EncodeNotSupportedException {

      try {
         /*
          * Note: could also use a keystore file, which contains the token label
          * or slot no. to use. Load that via "new FileInputStream(ksFileName)"
          * instead of ByteArrayInputStream. Save objects to the keystore via a
          * FileOutputStream.
          */

         ByteArrayInputStream is1 = new ByteArrayInputStream(("slot:" + slot).getBytes());
         keyStore = KeyStore.getInstance(keystoreProvider);
         keyStore.load(is1, password.toCharArray());

         Enumeration<String> aliases = keyStore.aliases();

         while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            logger.info("KeyStore entryalias: {}", alias);
            LunaCertificateX509 cert = (LunaCertificateX509) keyStore.getCertificate(alias);
            if (null != cert) {
               byte[] certBytes = cert.getEncoded();
               logger.debug("Certificate {}: {}", alias, CodecUtils.toHex(certBytes));
// TODO Uncomment
//               gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Certificate certificate;
//               try {
//                  certificate = convertX509CertToIEEE1609Dot2Cert(cert);
//                  CertificateWrapper certificateWrapper = CertificateWrapper.fromCertificate(new CryptoProvider(), certificate);
//                  CertificateManager.put(alias, certificateWrapper);
//               } catch (Exception e) {
//                  logger.error("Error converting cert from X.509 to IEEE 1609.2", e);
//               }
            }
         }
      } catch (KeyStoreException kse) {
         logger.error("Unable to create keystore object", kse);
      } catch (NoSuchAlgorithmException nsae) {
         logger.error("Unexpected NoSuchAlgorithmException while loading keystore", nsae);
      } catch (CertificateException e) {
         logger.error("Unexpected CertificateException while loading keystore", e);
      } catch (IOException e) {
         // this should never happen
         logger.error("Unexpected IOException while loading keystore.", e);
      } catch (Exception e) {
         logger.error("Unknown Exception", e);
      }
      return keyStore;

   }

   /**
    * Method to convert a X.509 certificate to IEEE 1609.2 certificate
    * TODO This method should be added to CertificateWrapper class in jpo-security
    * 
    * @param cert X509 certificate
    * @return equivalent IEEE 1609.2 certificate 
    * @throws Exception
    */
   private gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.Certificate
         convertX509CertToIEEE1609Dot2Cert(LunaCertificateX509 cert) throws Exception {
      // TODO Auto-generated method stub
      throw new Exception("convertX509CertToIEEE1609Dot2Cert not implemented");
   }

   @Bean
   @DependsOn("encryptionCipher")
   Cipher decryptionCipher(KeyPair keyPair) throws GeneralSecurityException {
      Cipher cipher = Cipher.getInstance("ECIES", this.cryptoProvider);
      cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(), parameters);
      return cipher;
   }

   @Bean
   Cipher encryptionCipher(KeyPair keyPair) throws GeneralSecurityException {
      Cipher cipher = Cipher.getInstance("ECIES", this.cryptoProvider);
      cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
      parameters = cipher.getParameters();
      return cipher;
   }

   @Bean
   @DependsOn("keyStore")
   KeyPair keyPair(
      @Value("${ode.hsmKeyPairAlias}") String hsmKeyPairAlias,
      @Value("${ode.hsmTokenPassword}") String password) throws GeneralSecurityException {

      // password can be a dummy char array as it will be ignored but, we'll use
      // the available password config property nonetheless
      KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(password.toCharArray());
      PrivateKeyEntry prKE = (PrivateKeyEntry) keyStore.getEntry(hsmKeyPairAlias, param);
      KeyPair pair;
      if (prKE != null) {
         logger.info("Entry with alias {} found", hsmKeyPairAlias);
         enrollmentCert = prKE.getCertificate();
   
         PublicKey pubKey = enrollmentCert.getPublicKey();
         if (pubKey != null) {
            logger.info("Public Key: {}", CodecUtils.toHex(pubKey.getEncoded()));
         } else {
            logger.info("Public Key: {}", pubKey);
         }
         
         pair = new KeyPair(pubKey, prKE.getPrivateKey());
      } else {
//      ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime256v1");
//      KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", this.cryptoProvider);
//      g.initialize(ecSpec, new SecureRandom());
//      KeyPair pair = g.generateKeyPair();
      
         logger.info("Entry with alias {} NOT found. Generating a new key pair...", hsmKeyPairAlias);
         
         KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", this.cryptoProvider);
         ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime256v1");
         keyPairGenerator.initialize(ecSpec);
         pair = keyPairGenerator.generateKeyPair();

         enrollmentCert = createCertificate(pair);
         LunaCertificateX509[] certChain =  new LunaCertificateX509[1];
         certChain[0] = (LunaCertificateX509) enrollmentCert;
         
         // store the certificate and key in the KeyStore.
         try {
             // Save the Certificate to the Luna KeyStore
             logger.info("Storing Certificate {} to KeyStore...", hsmKeyPairAlias);
             keyStore.setKeyEntry(hsmKeyPairAlias, pair.getPrivate(), null, certChain);
             keyStore.store(null, null);
         } catch (Exception e) {
             logger.error("Exception while storing Certificate", e);
         }
      }
      return pair;
   }

   /**
    * @param keyPair EC public/private key pair
    * @return
    * @throws SignatureException 
    * @throws NoSuchAlgorithmException 
    * @throws NoSuchProviderException 
    * @throws IllegalStateException 
    * @throws InvalidKeyException 
    * @throws CertificateEncodingException 
    */
   private Certificate createCertificate(KeyPair keyPair) throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException {
//      Calendar expiry = Calendar.getInstance();
//      Date notBefore = expiry.getTime();              // time from which certificate is valid
//      expiry.add(Calendar.DAY_OF_YEAR, 180);
//      Date notAfter = expiry.getTime();
//      BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());     // serial number for certificate
//      X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
//      X500Principal              dnName = new X500Principal("CN=Test CA Certificate");
//      certGen.setSerialNumber(serialNumber);
//      certGen.setIssuerDN(dnName);
//      certGen.setNotBefore(notBefore);
//      certGen.setNotAfter(notAfter);
//      certGen.setSubjectDN(dnName);                       // note: same as issuer
//      certGen.setPublicKey(keyPair.getPublic());
//      certGen.setSignatureAlgorithm("SHA256withECDSA");
//      X509Certificate cert = certGen.generate(keyPair.getPrivate(), this.cryptoProvider);
//      return cert;

      //generate a self-signed ECDSA certificate.
      Calendar expiry = Calendar.getInstance();
      Date notBefore = expiry.getTime();              // time from which certificate is valid
      expiry.add(Calendar.DAY_OF_YEAR, 180);
      Date notAfter = expiry.getTime();
      BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());     // serial number for certificate
      LunaCertificateX509 cert = null;
      try {
         cert = LunaCertificateX509.SelfSign(keyPair, "CN=ODE ECDSA Enrollment, L=MD, C=US", serialNumber, notBefore, notAfter);
      } catch (InvalidKeyException ike) {
         logger.error("Unexpected InvalidKeyException while generating cert.", ike);
      } catch (CertificateEncodingException cee) {
         logger.error("Unexpected CertificateEncodingException while generating cert.", cee);
      }
      return cert;
   }

   @Bean
   @DependsOn("keyPair")
   Certificate enrollmentCert() {
      return this.enrollmentCert;
   }

   @Bean
   Signature signingSignature(KeyPair keyPair) throws GeneralSecurityException {
      Signature signature = Signature.getInstance("SHA256withECDSA");
      signature.initSign(keyPair.getPrivate());
      return signature;
   }

   @Bean(destroyMethod = "logout")
   LunaSlotManager slotManager(
      @Value("${ode.hsmTokenLabel}") String tokenLabel,
      @Value("${ode.hsmTokenPassword}") String password,
      @Value("${ode.cryptoProvider}") String cryptoProvider) {
      this.cryptoProvider = cryptoProvider;
      LunaSlotManager slotManager = null;
//      if (cryptoProvider.equals(LUNA_PROVIDER)) {
         try {
            slotManager = LunaSlotManager.getInstance();
            slotManager.login(tokenLabel, password);
            java.security.Provider provider = new com.safenetinc.luna.provider.LunaProvider();
            if (java.security.Security.getProvider(provider.getName()) == null) {
               // removing the provider is only necessary if it is already
               // registered
               // and you want to change its position
               // java.security.Security.removeProvider(provider.getName());
               java.security.Security.addProvider(provider);
               com.safenetinc.luna.LunaSlotManager.getInstance().logout();
            }
         } catch (Exception e) {
            logger.error("Exception caught during loading of the providers.", e);
            throw e;
         }
//      } else if (cryptoProvider.equals(BOUNCYCASTLE_PROVIDER)) { 
         java.security.Security.addProvider(new BouncyCastleProvider());
//      } else {
//         throw new IllegalArgumentException("ode.cryptoProvider property not defined");
//      }
      
      
      return slotManager;
   }

   @Bean
   Signature verificationSignature(KeyPair keyPair) throws GeneralSecurityException {
      Signature signature = Signature.getInstance("SHA256withECDSA");
      signature.initVerify(keyPair.getPublic());
      return signature;
   }
}
