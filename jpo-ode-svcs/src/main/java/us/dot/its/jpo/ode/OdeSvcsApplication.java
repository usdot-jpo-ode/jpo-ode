package us.dot.its.jpo.ode;

import java.lang.management.ManagementFactory;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;

import javax.annotation.PreDestroy;
import javax.crypto.Cipher;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

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
   Cipher decryptionCipher(KeyPair keyPair) throws GeneralSecurityException {
       Cipher cipher = Cipher.getInstance("ECIES/NONE/NoPadding", "LunaProvider");
       cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
       return cipher;
   }

   @Bean
   Cipher encryptionCipher(KeyPair keyPair) throws GeneralSecurityException {
       Cipher cipher = Cipher.getInstance("ECIES/NONE/NoPadding", "LunaProvider");
       cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
       return cipher;
   }

   @Bean
   @DependsOn("slotManager")
   KeyPair keyPair() throws GeneralSecurityException {
       KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "LunaProvider");
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
   LunaSlotManager slotManager(@Value("${ode.hsmTokenLabel}") String tokenLabel,
                               @Value("${ode.hsmTokenPassword}") String password) {
       LunaSlotManager slotManager = LunaSlotManager.getInstance();
       slotManager.login(tokenLabel, password);
       return slotManager;
   }

   @Bean
   Signature verificationSignature(KeyPair keyPair) throws GeneralSecurityException {
       Signature signature = Signature.getInstance("SHA256withECDSA");
       signature.initVerify(keyPair.getPublic());
       return signature;
   }
   
}
