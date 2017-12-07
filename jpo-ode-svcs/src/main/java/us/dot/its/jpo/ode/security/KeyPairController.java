package us.dot.its.jpo.ode.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import us.dot.its.jpo.ode.util.CodecUtils;

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

   @RequestMapping(method = RequestMethod.GET, value = "/key-pair", produces = "application/json")
   Map<String, String> keyPair(@RequestParam(value = "encoding", required = false) String encoding) {
      byte[] privKey = this.keyPair.getPrivate().getEncoded();
      byte[] pubKey = this.keyPair.getPublic().getEncoded();
      String privateKey;
      String publicKey;
      if (encoding == null || encoding.equals("base64")) {
         privateKey = Base64.getEncoder().encodeToString(privKey);
         publicKey = Base64.getEncoder().encodeToString(pubKey);
      } else {
         privateKey = CodecUtils.toHex(privKey);
         publicKey = CodecUtils.toHex(pubKey);
      }

      return Util.zip(new String[] { "private", "public" }, new String[] { privateKey, publicKey });
   }

}
