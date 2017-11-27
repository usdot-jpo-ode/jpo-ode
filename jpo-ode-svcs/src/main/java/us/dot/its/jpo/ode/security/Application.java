package us.dot.its.jpo.ode.security;

import com.safenetinc.luna.LunaSlotManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    Cipher decryptionCipher(KeyPair keyPair) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding", "LunaProvider");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        return cipher;
    }

    @Bean
    Cipher encryptionCipher(KeyPair keyPair) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding", "LunaProvider");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        return cipher;
    }

    @Bean
    @DependsOn("slotManager")
    KeyPair keyPair() throws GeneralSecurityException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "LunaProvider");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    @Bean
    Signature signingSignature(KeyPair keyPair) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("RSA");
        signature.initSign(keyPair.getPrivate());
        return signature;
    }

    @Bean(destroyMethod = "logout")
    LunaSlotManager slotManager(@Value("${ode.hsmLoginId}") String tokenLabel,
                                @Value("${ode.hsmLoginPassword}") String password) {
        LunaSlotManager slotManager = LunaSlotManager.getInstance();
        slotManager.login(tokenLabel, password);
        return slotManager;
    }

    @Bean
    Signature verificationSignature(KeyPair keyPair) throws GeneralSecurityException {
        Signature signature = Signature.getInstance("RSA");
        signature.initVerify(keyPair.getPublic());
        return signature;
    }

}
