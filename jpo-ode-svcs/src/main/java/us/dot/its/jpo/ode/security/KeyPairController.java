package us.dot.its.jpo.ode.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.util.Base64;
import java.util.Map;

@RestController
final class KeyPairController {

    private final KeyPair keyPair;

    @Autowired
    KeyPairController(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/key-pair")
    Map<String, String> keyPair() {
        String privateKey = Base64.getEncoder().encodeToString(this.keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(this.keyPair.getPublic().getEncoded());

        return Util.zip(new String[]{"private", "public"}, new String[]{privateKey, publicKey});
    }

}
