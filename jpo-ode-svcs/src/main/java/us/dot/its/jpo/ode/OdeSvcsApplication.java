package us.dot.its.jpo.ode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.util.Enumeration;

import javax.annotation.PreDestroy;
import javax.crypto.Cipher;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import com.safenetinc.luna.LunaSlotManager;

@SpringBootApplication
@EnableConfigurationProperties(OdeProperties.class)
public class OdeSvcsApplication {

   private Logger logger = LoggerFactory.getLogger(this.getClass());
   
   private static final String LUNA_PROVIDER = "LunaProvider";
   static final int DEFAULT_NO_THREADS = 10;
   static final String DEFAULT_SCHEMA = "default";

   public static void main(String[] args) throws MalformedObjectNameException, InterruptedException,
         InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
      SpringApplication.run(OdeSvcsApplication.class, args);
      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      SystemConfig mBean = new SystemConfig(DEFAULT_NO_THREADS, DEFAULT_SCHEMA);
      ObjectName name = new ObjectName("us.dot.its.jpo.ode:type=SystemConfig");
      mbs.registerMBean(mBean, name);
   }

   @Bean
   CommandLineRunner init(OdeProperties odeProperties) {
      return args -> {
      };
   }

   @PreDestroy
   public void cleanup() {
      // Unused
   }

   @Bean
   @DependsOn("cryptoProvider")
   KeyStore keyStore(
      @Value("${ode.keyStoreProvider}") String keystoreProvider, 
      @Value("${ode.keyStoreProviderSlot}") String slot, 
      @Value("${ode.hsmTokenPassword}") String password) {
      KeyStore myStore = null;
      try
      {

        /*
          Note: could also use a keystore file, which contains the token label or slot no. to use.
          Load that via "new FileInputStream(ksFileName)" instead of ByteArrayInputStream. Save
          objects to the keystore via a FileOutputStream.
         */

        ByteArrayInputStream is1 = new ByteArrayInputStream(("slot:" + slot).getBytes());
        myStore = KeyStore.getInstance(keystoreProvider);
        myStore.load(is1, password.toCharArray());
        
        Enumeration<String> aliases = myStore.aliases();
        
        while (aliases.hasMoreElements()) {
           logger.info("Key alias: {}", aliases.nextElement());
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
      }
      return myStore;

   }
   
   @Bean
   Cipher decryptionCipher(KeyPair keyPair) throws GeneralSecurityException {
      Cipher cipher = Cipher.getInstance("ECIES/NONE/NoPadding", LUNA_PROVIDER);
      // cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
      return cipher;
   }

   @Bean
   Cipher encryptionCipher(KeyPair keyPair) throws GeneralSecurityException {
      Cipher cipher = Cipher.getInstance("ECIES/NONE/NoPadding", LUNA_PROVIDER);
      // cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
      return cipher;
   }

   @Bean
   @DependsOn("slotManager")
   KeyPair keyPair() throws GeneralSecurityException {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", LUNA_PROVIDER);
      ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime256v1");
      keyPairGenerator.initialize(ecSpec);
      return keyPairGenerator.generateKeyPair();
   }

   @Bean
   Signature signingSignature(KeyPair keyPair) throws GeneralSecurityException {
      Signature signature = Signature.getInstance("SHA256withECDSA");
      signature.initSign(keyPair.getPrivate());
      return signature;
   }

   @Bean(destroyMethod = "logout")
   @DependsOn("keyStore")
   LunaSlotManager slotManager(
      @Value("${ode.hsmTokenLabel}") String tokenLabel,
      @Value("${ode.hsmTokenPassword}") String password,
      String cryptoProvider) {
      LunaSlotManager slotManager;
      if (cryptoProvider.equals(LUNA_PROVIDER)) {
         slotManager  = LunaSlotManager.getInstance();
         slotManager.login(tokenLabel, password);
      } else {
         throw new IllegalArgumentException("ode.cryptoProvider property not defined");
      }
      return slotManager;
   }

   @Bean
   String cryptoProvider(@Value("${ode.cryptoProvider}") String cryptoProvider) {
      return cryptoProvider;
   }

   @Bean
   Signature verificationSignature(KeyPair keyPair) throws GeneralSecurityException {
      Signature signature = Signature.getInstance("SHA256withECDSA");
      signature.initVerify(keyPair.getPublic());
      return signature;
   }

}
