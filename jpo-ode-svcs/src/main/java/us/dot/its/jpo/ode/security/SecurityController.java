package us.dot.its.jpo.ode.security;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Paths;
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
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;
import org.thymeleaf.util.StringUtils;

import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.safenetinc.luna.LunaSlotManager;
import com.safenetinc.luna.provider.LunaCertificateX509;

import gov.usdot.cv.security.cert.SecureECPrivateKey;
import gov.usdot.cv.security.crypto.CryptoProvider;
import gov.usdot.cv.security.crypto.ECDSAProvider;
import us.dot.its.jpo.ode.OdeProperties;
import us.dot.its.jpo.ode.util.CodecUtils;

@Controller
public class SecurityController {

   private enum Providers {
      LunaProvider, BC
   };
   
   private enum KeystoreTypes {
      Luna, JKS, BKS
   };
   
   private Logger logger = LoggerFactory.getLogger(this.getClass());

   private OdeProperties odeProperties;

   private Providers cryptoProvider;
   private KeystoreTypes keystoreType;
   private KeyStore keyStore;
   private KeyPair keyPair;
   private AlgorithmParameters parameters;
   private Certificate enrollmentCert;
   private Provider provider;

   private String pubKeyHexBytes;

   private CertificateLoader certificateLoader;

   @Autowired
   protected SecurityController(OdeProperties odeProps) {
      super();
      this.odeProperties = odeProps;
      certificateLoader = new CertificateLoader(odeProps);
      Executors.newSingleThreadExecutor().submit(certificateLoader);
   }

   @Bean
   @DependsOn("provider")
   KeyStore keyStore(
      @Value("${ode.keystoreType}") String odeKeystoreType,
      @Value("${ode.keystore}") String odeKeystore,
      @Value("${ode.keystorePassword}") String password) throws EncodeFailedException, gov.usdot.cv.security.cert.CertificateException, EncodeNotSupportedException {

      try {
         /*
          * Note: could also use a keystore file, which contains the token label
          * or slot no. to use. Load that via "new FileInputStream(ksFileName)"
          * instead of ByteArrayInputStream. Save objects to the keystore via a
          * FileOutputStream.
          */
         try {
            this.keystoreType = KeystoreTypes.valueOf(odeKeystoreType);
            this.keyStore = KeyStore.getInstance(
                  this.keystoreType.name(), providerCached());
         } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid value for enum " + KeystoreTypes.class.getSimpleName() + ": " + odeKeystoreType);
         }

         InputStream is1 = null;
         switch (this.keystoreType) {
         case Luna:
            is1 = new ByteArrayInputStream(odeKeystore.getBytes());
            keyStore.load(is1, password.toCharArray());
            is1.close();
            break;
            
         default:
            if (Paths.get(odeKeystore).toFile().exists()) {// if keystore file exists, load it
               is1 = new FileInputStream(odeKeystore);
               keyStore.load(is1, password.toCharArray());
               is1.close();
            } else {// if no keystore file, create a new one
               FileOutputStream keystoreStream = new FileOutputStream(odeKeystore);
               keyStore.load(null, null);
               keyStore.store(keystoreStream, password.toCharArray());
               keystoreStream.close();
            }
            this.keyStore = KeyStore.getInstance(
                  this.keystoreType.name(), 
                  providerCached());
            break;
         }

         Enumeration<String> aliases = keyStore.aliases();

         while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            logger.info("KeyStore entryalias: {}", alias);
            Certificate cert = keyStore.getCertificate(alias);
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
         logger.error("Error loading Keystore", e);
      }
      return keyStore;

   }
   
   @Bean
   @DependsOn("keyStore")
   KeyStore keyStoreCached() {
      return this.keyStore;
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
         convertX509CertToIEEE1609Dot2Cert(Certificate cert) throws Exception {
      // TODO Auto-generated method stub
      throw new Exception("convertX509CertToIEEE1609Dot2Cert not implemented");
   }

   @Bean
   @DependsOn("encryptionCipher")
   Cipher decryptionCipher(KeyPair keyPair) throws GeneralSecurityException {
      Cipher cipher = Cipher.getInstance(CryptoProvider.ENCRYPTION_ALGORITHM, this.cryptoProvider.name());
      cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(), parameters);
      return cipher;
   }

   @Bean
   @DependsOn("provider")
   Cipher encryptionCipher(KeyPair keyPair) throws GeneralSecurityException {
      Cipher cipher = Cipher.getInstance(CryptoProvider.ENCRYPTION_ALGORITHM, provider);
      cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
      parameters = cipher.getParameters();
      return cipher;
   }

   @Bean
   @DependsOn("keyStore")
   KeyPair keyPair(
      @Value("${ode.keyPairAlias}") String odeKeyPairAlias,
      @Value("${ode.keystorePassword}") String odekeystorePassword,
      @Value("${ode.keystore}") String odeKeystore) throws GeneralSecurityException, FileNotFoundException {

      // The keystore password is required to retrieve the entry
      KeyStore.ProtectionParameter param = new KeyStore.PasswordProtection(odekeystorePassword.toCharArray());
      PrivateKeyEntry prKE = (PrivateKeyEntry) keyStore.getEntry(odeKeyPairAlias, param);
      if (prKE != null) {
         logger.info("Entry with alias {} found", odeKeyPairAlias);
         enrollmentCert = prKE.getCertificate();
   
         PublicKey pubKey = enrollmentCert.getPublicKey();
         this.keyPair = new KeyPair(pubKey, prKE.getPrivateKey());
      } else {
         logger.info("Entry with alias {} NOT found. Generating a new key pair...", odeKeyPairAlias);
         
         KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
               ECDSAProvider.KEYPAIR_GENERATION_ALGORITHM);
         ECGenParameterSpec ecSpec = new ECGenParameterSpec(
               ECDSAProvider.KEYPAIR_GENERATION_ALGORTHM_SPECS);
         keyPairGenerator.initialize(ecSpec, secureRandom());
         this.keyPair = keyPairGenerator.generateKeyPair();

         enrollmentCert = certificate(this.keyPair);
         Certificate[] certChain =  new Certificate[1];
         certChain[0] = enrollmentCert;
         
         // store the certificate and key in the KeyStore.
         OutputStream keystoreStream;
         if (this.cryptoProvider.equals(Providers.LunaProvider)) {
            keystoreStream = null;
         } else {
            keystoreStream = new FileOutputStream(odeKeystore);
         }
         
         try {
            // Save the Certificate to the KeyStore
            logger.info("Storing Certificate {} to KeyStore...", odeKeyPairAlias);
            keyStore.setKeyEntry(odeKeyPairAlias, this.keyPair.getPrivate(), odekeystorePassword.toCharArray(), certChain);
            keyStore.store(keystoreStream, odekeystorePassword.toCharArray());
         } catch (Exception e) {
            logger.error("Exception while storing Certificate", e);
         }
      }

      if (this.keyPair != null) {
         
         certificateLoader.setSeedPrivateKey(new SecureECPrivateKey(keyStore, this.keyPair.getPrivate()));
         // Note: cannot display private key from HSM
//         ECPrivateKey ecPriKey = (ECPrivateKey) pair.getPrivate();
//         logger.info("Enrollment Private Key [{}], [{}]: {}", 
//            pair.getPrivate().getFormat(),
//            pair.getPrivate().getAlgorithm(),
//            CodecUtils.toHex(ecPriKey.getS().toByteArray()));
         
         ECPublicKey ecPubKey = (ECPublicKey) this.keyPair.getPublic();
         this.pubKeyHexBytes = CodecUtils.toHex(ecPubKey.getW().getAffineX().toByteArray());
         logger.info("Enrollment Public Key [{}], [{}]: {}", 
               this.keyPair.getPublic().getFormat(),
               this.keyPair.getPublic().getAlgorithm(),
               pubKeyHexBytes);
      }

      return this.keyPair;
   }

   @Bean
   @DependsOn("keyPair")
   KeyPair keyPairCached() {
      return this.keyPair;
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
   @SuppressWarnings("deprecation")
   private Certificate certificate(KeyPair keyPair) throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException {

      //generate a self-signed ECDSA certificate.
      Calendar expiry = Calendar.getInstance();
      Date notBefore = expiry.getTime();              // time from which certificate is valid
      expiry.add(Calendar.DAY_OF_YEAR, odeProperties.getSelfCertExpPeriodDays());
      Date notAfter = expiry.getTime();
      BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());     // serial number for certificate
      Certificate cert = null;
      try {
         switch (this.cryptoProvider) {
         case LunaProvider:
            cert = LunaCertificateX509.SelfSign(keyPair, "CN=SCMS Enrollment, L=DC, C=US", serialNumber, notBefore, notAfter);
            break;
         case BC:
            X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
            X500Principal              dnName = new X500Principal("CN=SCMS Enrollment, L=DC, C=US");
            certGen.setSerialNumber(serialNumber);
            certGen.setIssuerDN(dnName);
            certGen.setNotBefore(notBefore);
            certGen.setNotAfter(notAfter);
            certGen.setSubjectDN(dnName);                       // note: same as issuer
            certGen.setPublicKey(keyPair.getPublic());
            certGen.setSignatureAlgorithm(ECDSAProvider.SIGNATURE_ALGORITHM);
            cert = certGen.generate(keyPair.getPrivate(), this.cryptoProvider.name());
            break;
         }
      } catch (InvalidKeyException ike) {
         logger.error("Unexpected InvalidKeyException while generating cert.", ike);
      } catch (CertificateEncodingException cee) {
         logger.error("Unexpected CertificateEncodingException while generating cert.", cee);
      }
      return cert;
   }

   @Bean
   @DependsOn("keyPair")
   Certificate enrollmentCertCached() {
     return this.enrollmentCert;
   }

   @Bean
   Signature signingSignature(KeyPair keyPair) throws GeneralSecurityException {
      Signature signature = Signature.getInstance(ECDSAProvider.SIGNATURE_ALGORITHM, provider.getName());
      signature.initSign(keyPair.getPrivate(), secureRandom());
      return signature;
   }

   @Bean(destroyMethod = "logout")
   @DependsOn("secureRandom")
   LunaSlotManager slotManager() {
      
      LunaSlotManager slotManager = null;
      if (!StringUtils.isEmptyOrWhitespace(odeProperties.getHsmTokenLabel()) && 
          !StringUtils.isEmptyOrWhitespace(odeProperties.getKeystorePassword())) {
         if (this.cryptoProvider == Providers.LunaProvider) {
            slotManager = LunaSlotManager.getInstance();
            slotManager.login(odeProperties.getHsmTokenLabel(), odeProperties.getKeystorePassword());
            com.safenetinc.luna.LunaSlotManager.getInstance().logout();
         }
      }
      
      return slotManager;
   }

   @DependsOn("keyPair")
   @Bean
   private String pubKeyHexBytes() {
      return pubKeyHexBytes;
   }

   @Bean
   Provider provider (@Value("${ode.cryptoProvider}") String odeCryptoProvider) {
      
      try {
         try {
            this.cryptoProvider = Providers.valueOf(odeCryptoProvider);
         } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid value for enum " + Providers.class.getSimpleName() + ": " + odeCryptoProvider);
         }
         
         switch (this.cryptoProvider) {
         case LunaProvider:
            this.provider = new com.safenetinc.luna.provider.LunaProvider();
            addProvider(provider);
            addProvider(new BouncyCastleProvider()); // always add BC because we are using it for some things that Luna doesn't provide
            break;
         case BC:
            this.provider = new BouncyCastleProvider();
            addProvider(provider);
            break;
         default:
            throw new SecurityException("ode.cryptoProvider not defined.");
         }
         
      } catch (Exception e) {
         logger.error("Exception caught during crypto provider loading", e);
      }

      return this.provider;
   }

   @Bean
   @DependsOn("provider")
   Provider providerCached() {
      return this.provider;
   }
   
   private void addProvider(Provider provider) throws Exception {
      try {
         if (java.security.Security.getProvider(provider.getName()) == null) {
            // removing the provider is only necessary if it is already
            // registered
            // and you want to change its position
            // java.security.Security.removeProvider(provider.getName());
            java.security.Security.addProvider(provider);
         }
      } catch (Exception e) {
         logger.error("Exception caught during loading of the providers.", e);
         throw e;
      }
   }

   @Bean
   @DependsOn("provider")
   SecureRandom secureRandom() {
      CryptoProvider.initialize();
      return CryptoProvider.getSecureRandom();
   }

   @Bean
   Signature verificationSignature(KeyPair keyPair) throws GeneralSecurityException {
      Signature signature = Signature.getInstance(ECDSAProvider.SIGNATURE_ALGORITHM, provider.getName());
      signature.initVerify(keyPair.getPublic());
      return signature;
   }

//   private void initFilePaths() {
//      if (this.odeProperties.getScmsCertsDir() != null) {
//         Path enrollmentDir;
//         if (this.odeProperties.getScmsEnrollmentDir() == null) {
//            enrollmentDir = Paths.get(this.odeProperties.getScmsCertsDir(), "enrollment");
//            this.odeProperties.setScmsEnrollmentDir(enrollmentDir.toString());
//         } else {
//            enrollmentDir = Paths.get(this.odeProperties.getScmsEnrollmentDir());
//         }
//
//         if (this.odeProperties.getScmsCertRevocationListFile() == null) {
//            this.odeProperties.setScmsCertRevocationListFile(enrollmentDir.resolve("CRL.oer").toString());
//         }
//
//         if (this.odeProperties.getScmsLocalCertChainFile() == null) {
//            this.odeProperties.setScmsLocalCertChainFile(enrollmentDir.resolve("LCCF.oer").toString());
//         }
//
//         if (this.odeProperties.getScmsLocalPolicyFile() == null) {
//            this.odeProperties.setScmsLocalPolicyFile(enrollmentDir.resolve("LPF.oer").toString());
//         }
//
//         if (this.odeProperties.getScmsRootCertFile() == null) {
//            this.odeProperties.setScmsRootCertFile(enrollmentDir.resolve("root.oer").toString());
//         }
//
//         if (this.odeProperties.getScmsRootTlsFile() == null) {
//            this.odeProperties.setScmsRootTlsFile(enrollmentDir.resolve("root.tls.pem").toString());
//         }
//
//         Path deviceDir = enrollmentDir.resolve(pubKeyHexBytes);
//         File deviceDirFile = deviceDir.toFile();
//         if (!deviceDirFile.exists()) {
//            deviceDirFile.mkdirs();
//         }
//         if (this.odeProperties.getScmsEcaCertFile() == null) {
//            this.odeProperties.setScmsEcaCertFile(deviceDir.resolve("ECA.oer").toString());
//         }
//  
//         if (this.odeProperties.getScmsEnrollmentCertFile() == null) {
//            this.odeProperties.setScmsEnrollmentCertFile(deviceDir.resolve("enrollment.oer").toString());
//         }
//  
//         if (this.odeProperties.getScmsPriKeyReconValueFile() == null) {
//            this.odeProperties.setScmsPriKeyReconValueFile(deviceDir.resolve("enrollment.s").toString());
//         }
//  
//         if (this.odeProperties.getScmsRaCertFile() == null) {
//            this.odeProperties.setScmsRaCertFile(deviceDir.resolve("RA.oer").toString());
//         }
//      }
//   }


}
