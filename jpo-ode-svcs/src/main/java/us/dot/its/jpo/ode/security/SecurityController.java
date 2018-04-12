package us.dot.its.jpo.ode.security;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;
import org.thymeleaf.util.StringUtils;

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
      LunaProvider, BC, SAFENET
   };
   
   private enum KeystoreTypes {
      Luna, JKS, BKS, CRYPTOKI
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

   private String odeKeystore;

   private char[] keystorePassword;

   @Autowired
   protected SecurityController(OdeProperties odeProps) throws SecurityException {
      super();
      this.odeProperties = odeProps;
      String odeCryptoProvider = odeProperties.getCryptoProvider();
      if (odeCryptoProvider != null) {
         try {
            this.cryptoProvider = Providers.valueOf(odeCryptoProvider );
         } catch (IllegalArgumentException e) {
            throw new SecurityException("Invalid value for enum " + Providers.class.getSimpleName() + ": " + odeCryptoProvider);
         }
      }
      
      String odeKeystoreType = odeProperties.getKeystoreType();
      if (odeKeystoreType != null) {
         try {
            this.keystoreType = KeystoreTypes.valueOf(odeKeystoreType);
         } catch (IllegalArgumentException e) {
            throw new SecurityException("Invalid value for enum " + KeystoreTypes.class.getSimpleName() + ": " + odeKeystoreType);
         }
      }
      
      certificateLoader = new CertificateLoader(odeProps);
      Executors.newSingleThreadExecutor().submit(certificateLoader);
   }

   @Bean
   @DependsOn("provider")
   KeyStore keyStore() throws SecurityException {

      try {
         /*
          * Note: could also use a keystore file, which contains the token label
          * or slot no. to use. Load that via "new FileInputStream(ksFileName)"
          * instead of ByteArrayInputStream. Save objects to the keystore via a
          * FileOutputStream.
          */
         if (this.keystoreType != null) {
            try {
               this.keyStore = KeyStore.getInstance(this.keystoreType.name(), provider);
            } catch (IllegalArgumentException e) {
               throw new SecurityException("Invalid value for enum " + KeystoreTypes.class.getSimpleName() + ": " + this.keystoreType);
            }
            
            this.odeKeystore = odeProperties.getKeystore();
            byte[] keystoreBytes = null;
            if (odeKeystore != null) {
               keystoreBytes = odeKeystore.getBytes();
            }

            String keystorePasswordStr = odeProperties.getKeystorePassword();
            if (keystorePasswordStr != null) {
               this.keystorePassword  = keystorePasswordStr.toCharArray();
            }
            
            InputStream is1 = null;
            switch (this.keystoreType) {
            case CRYPTOKI:
               if (keystoreBytes != null) {
                  is1 = new ByteArrayInputStream(keystoreBytes);
                  keyStore.load(is1, this.keystorePassword);
                  is1.close();
               } else {
                  keyStore.load(null, this.keystorePassword);
               }
               break;
               
            case Luna:
               if (keystoreBytes != null) {
                  is1 = new ByteArrayInputStream(keystoreBytes);
                  keyStore.load(is1, this.keystorePassword);
                  is1.close();
               } else {
                  keyStore.load(null, this.keystorePassword);
               }
               break;
               
            default:
               if (Paths.get(odeKeystore).toFile().exists()) {// if keystore file exists, load it
                  is1 = new FileInputStream(odeKeystore);
                  keyStore.load(is1, this.keystorePassword);
                  is1.close();
               } else {// if no keystore file, create a new one
                  FileOutputStream keystoreStream = new FileOutputStream(odeKeystore);
                  keyStore.load(null, null);
                  keyStore.store(keystoreStream, this.keystorePassword);
                  keystoreStream.close();
               }
               break;
            }
         }
         

         if (keyStore != null) {
            Enumeration<String> aliases = keyStore.aliases();
   
            while (aliases.hasMoreElements()) {
               String alias = aliases.nextElement();
               logger.info("KeyStore entryalias: {}", alias);
               Certificate cert = keyStore.getCertificate(alias);
               if (null != cert) {
                  byte[] certBytes = cert.getEncoded();
                  logger.debug("Certificate {}: {}", alias, CodecUtils.toHex(certBytes));
               }
            }
         }
      } catch (KeyStoreException kse) {
         throw new SecurityException("Unable to create keystore object", kse);
      } catch (NoSuchAlgorithmException nsae) {
         throw new SecurityException("Unexpected NoSuchAlgorithmException while loading keystore", nsae);
      } catch (CertificateException e) {
         logger.error("Unexpected CertificateException while loading keystore", e);
      } catch (IOException e) {
         // this should never happen
         throw new SecurityException("Unexpected IOException while loading keystore.", e);
      } catch (Exception e) {
         throw new SecurityException("Error loading Keystore", e);
      }
      return keyStore;

   }
   
   @Bean
   @DependsOn("keyStore")
   KeyStore keyStoreCached() {
      return this.keyStore;
   }

   @Bean
   @DependsOn("encryptionCipher")
   Cipher decryptionCipher(KeyPair keyPair) throws SecurityException {
      Cipher cipher = null;
      try {
         if (provider != null) {
            cipher = Cipher.getInstance(CryptoProvider.ENCRYPTION_ALGORITHM, provider);
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(), parameters);
         }
      } catch (Exception e) {
         throw new SecurityException("Error initializing decryption cipher!", e);
      }
      return cipher;
   }

   @Bean
   @DependsOn("provider")
   Cipher encryptionCipher(KeyPair keyPair) throws SecurityException {
      Cipher cipher = null;
      try {
         if (provider != null) {
            cipher = Cipher.getInstance(CryptoProvider.ENCRYPTION_ALGORITHM, provider);
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            parameters = cipher.getParameters();
         }
      } catch (Exception e) {
         throw new SecurityException("Error initializing encryption cipher!", e);
      }
      return cipher;
   }

   @Bean
   @DependsOn("keyStore")
   KeyPair keyPair() throws SecurityException {
      try {
         KeyStore.ProtectionParameter param = null;
         if (keystorePassword != null) {
            // The keystore password is required to retrieve the entry
            param = new KeyStore.PasswordProtection(this.keystorePassword);
         }
         
         String odeKeyPairAlias = odeProperties.getKeyPairAlias();
         if (odeKeyPairAlias != null) {
            PrivateKeyEntry prKE = (PrivateKeyEntry) keyStore.getEntry(odeKeyPairAlias , param);
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
               if (this.cryptoProvider.equals(Providers.BC)) {
                  keystoreStream = new FileOutputStream(odeKeystore);
               } else {
                  keystoreStream = null;
               }
               
               try {
                  // Save the Certificate to the KeyStore
                  logger.info("Storing Certificate {} to KeyStore...", odeKeyPairAlias);
                  keyStore.setKeyEntry(odeKeyPairAlias, this.keyPair.getPrivate(), this.keystorePassword, certChain);
                  keyStore.store(keystoreStream, this.keystorePassword);
               } catch (Exception e) {
                  logger.error("Exception while storing Certificate", e);
               }
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
      } catch (Exception e) {
         throw new SecurityException("Error key pair!", e);
      }
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
   private Certificate certificate(KeyPair keyPair) throws SecurityException {

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
         throw new SecurityException("Unexpected InvalidKeyException while generating cert.", ike);
      } catch (CertificateEncodingException cee) {
         throw new SecurityException("Unexpected CertificateEncodingException while generating cert.", cee);
      } catch (Exception e) {
         throw new SecurityException("Error creating certificate!", e);
      }
      return cert;
   }

   @Bean
   @DependsOn("keyPair")
   Certificate enrollmentCert() {
      return this.enrollmentCert;
   }

   @Bean
   Signature signingSignature(KeyPair keyPair) throws SecurityException {
      Signature signature = null;
      try {
         if (provider != null) {
            signature = Signature.getInstance(ECDSAProvider.SIGNATURE_ALGORITHM, provider);
            signature.initSign(keyPair.getPrivate(), secureRandom());
         }
      } catch (Exception e) {
         throw new SecurityException("Error initializing signer!", e);
      }
      return signature;
   }

   @Bean(destroyMethod = "logout")
   @DependsOn("secureRandom")
   LunaSlotManager slotManager() {
      
      LunaSlotManager slotManager = null;
      if (!StringUtils.isEmptyOrWhitespace(odeProperties.getHsmTokenLabel()) && 
          !StringUtils.isEmptyOrWhitespace(odeProperties.getKeystorePassword())) {
         switch (this.cryptoProvider) {
         case LunaProvider:
            slotManager = LunaSlotManager.getInstance();
            slotManager.login(odeProperties.getHsmTokenLabel(), odeProperties.getKeystorePassword());
            com.safenetinc.luna.LunaSlotManager.getInstance().logout();

         case SAFENET:
            break;
            
         default:
            break;
         }
      }
      
      return slotManager;
   }

   @Bean
   @DependsOn("keyPair")
   private String pubKeyHexBytes() {
      return pubKeyHexBytes;
   }

   @Bean
   @DependsOn("slotManager")
   Provider provider () throws SecurityException {
      
      try {
         if (this.cryptoProvider != null) {
            switch (this.cryptoProvider) {
            case SAFENET:
               this.provider = new au.com.safenet.crypto.provider.SAFENETProvider();
               addProvider(provider);
               addProvider(new BouncyCastleProvider()); // always add BC because we are using it for some things that Luna doesn't provide
               break;
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
         }         
      } catch (Exception e) {
         throw new SecurityException("Exception caught during crypto provider loading", e);
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
         throw new SecurityException("Exception caught during loading of the providers.", e);
      }
   }

   @Bean
   SecureRandom secureRandom() {
      CryptoProvider.initialize();
      return CryptoProvider.getSecureRandom();
   }

   @Bean
   Signature verificationSignature(KeyPair keyPair) throws GeneralSecurityException {
      Signature signature = null;
      if (provider != null) {
         signature = Signature.getInstance(ECDSAProvider.SIGNATURE_ALGORITHM, provider);
         signature.initVerify(keyPair.getPublic());
      }
      return signature;
   }

}
