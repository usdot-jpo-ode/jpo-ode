package us.dot.its.jpo.ode.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Cipher;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.oss.asn1.COERCoder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.OctetString;

import gov.usdot.asn1.generated.ieee1609dot2.Ieee1609dot2;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.EccP256CurvePoint;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Time32;
import gov.usdot.cv.security.clock.ClockHelper;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.crypto.CryptoProvider;
import gov.usdot.cv.security.crypto.ECDSAProvider;
import gov.usdot.cv.security.crypto.EcdsaP256SignatureWrapper;
import gov.usdot.cv.security.util.Time32Helper;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

@RestController
final class CryptoController {

   public class EcR_Params extends OdeObject {

      private static final long serialVersionUID = 1L;
      
      private String type;
      private byte[] value;
      
      public String getType() {
         return type;
      }
      public void setType(String type) {
         this.type = type;
      }
      public byte[] getValue() {
         return value;
      }
      public void setValue(byte[] value) {
         this.value = value;
      }
    
      public String toHex() {
         return JsonUtils.newObjectNode("type", type).put("value", CodecUtils.toHex(value)).toString();
      }
   }

   public class EcParams extends OdeObject {
      private static final long serialVersionUID = 1L;

      private EcR_Params r;
      private byte[] s;
      
      public EcR_Params getR() {
         return r;
      }
      public void setR(EcR_Params r) {
         this.r = r;
      }
      public byte[] getS() {
         return s;
      }
      public void setS(byte[] s) {
         this.s = s;
      }
      
      public String toHex() {
         String rStr = r.toHex();
         String sStr = CodecUtils.toHex(s);
         return "{r:" + rStr + ",s:" + sStr + "}";
      }
      
   }
   private static final String X_ONLY = "x-only";

   private static final String COMPRESSED_Y_1 = "compressed-y-1";

   private static final String COMPRESSED_Y_0 = "compressed-y-0";

   private static final String VALIDITY_PERIOD_STOP = "validityPeriodStop";

   private static final String VALIDITY_PERIOD_START = "validityPeriodStart";

   private static final String NAME = "name";

   private static final String SIGNATURE = "signature";

   private static final String MESSAGE = "message";

   private static final String PAYLOAD_MUST_CONTAIN = "Payload must contain ";

   private static final String CIPHER_TEXT = "cipher-text";

   private static final String DIGEST = "digest";

   private final Logger logger = LoggerFactory.getLogger(this.getClass());

   private final Base64.Decoder decoder = Base64.getDecoder();

   private final Cipher decryptionCipher;

   private final Base64.Encoder encoder = Base64.getEncoder();
   
   private final COERCoder coerCoder = Ieee1609dot2.getCOERCoder();

   private final KeyPair keyPair;
   
   private final Cipher encryptionCipher;

   private final Signature signingSignature;

   private final Signature verificationSignature;

   private final Certificate enrollmentCert;

   @Autowired
   CryptoController(
      @Qualifier("keyPair") KeyPair keyPair,
      @Qualifier("enrollmentCert") Certificate enrollmentCert,
      @Qualifier("decryptionCipher") Cipher decryptionCipher,
      @Qualifier("encryptionCipher") Cipher encryptionCipher,
      @Qualifier("signingSignature") Signature signingSignature,
      @Qualifier("verificationSignature") Signature verificationSignature) {
      this.keyPair = keyPair;
      this.enrollmentCert = enrollmentCert;
      this.decryptionCipher = decryptionCipher;
      this.encryptionCipher = encryptionCipher;
      this.signingSignature = signingSignature;
      this.verificationSignature = verificationSignature;
   }

   @RequestMapping(method = RequestMethod.POST, value = "/decrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, String> decrypt(@RequestBody Map<String, String> payload) throws GeneralSecurityException {
      String cipherText = Optional.of(payload.get(CIPHER_TEXT))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + CIPHER_TEXT));

      this.logger.info("Decrypting Cipher Text '{}'", cipherText);

      this.decryptionCipher.update(this.decoder.decode(cipherText));
      String message = new String(this.decryptionCipher.doFinal(), Charset.defaultCharset()).trim();

      return Util.zip(new String[] { CIPHER_TEXT, MESSAGE }, new String[] { cipherText, message });
   }

   @RequestMapping(method = RequestMethod.POST, value = "/encrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, String> encrypt(@RequestBody Map<String, String> payload) throws GeneralSecurityException {
      String message = Optional.of(payload.get(MESSAGE))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + MESSAGE));

      this.logger.info("Encrypting Message '{}'", message);

      this.encryptionCipher.update(message.getBytes(Charset.defaultCharset()));
      String cipherText = this.encoder.encodeToString(this.encryptionCipher.doFinal());

      return Util.zip(new String[] { MESSAGE, CIPHER_TEXT }, new String[] { message, cipherText });
   }

   @RequestMapping(method = RequestMethod.POST, value = "/sign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, String> sign(@RequestBody Map<String, String> payload) throws GeneralSecurityException, DecodeFailedException, DecodeNotSupportedException {
      String message = Optional.of(payload.get(MESSAGE))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + MESSAGE));

      this.logger.info("Signing Message '{}'", message);

//      byte[] digest = digest(message.getBytes());
//      String digestString = this.encoder.encodeToString(digest);

      byte[] sig = sign(message.getBytes());
      
//      EcdsaP256Signature asnSig = new EcdsaP256Signature();
//      coerCoder.decode(ByteBuffer.wrap(sig), asnSig);
//      EccP256CurvePoint r = asnSig.getR();
//      OctetString xOnly = r.getX_only();
//      OctetString s = asnSig.getS();
      
//      String signature = this.encoder.encodeToString(sig);
      String signature = CodecUtils.toHex(sig);
//      String xOnlyString = CodecUtils.toHex(xOnly.byteArrayValue());
//      String sString = CodecUtils.toHex(s.byteArrayValue());

//      return Util.zip(new String[] { MESSAGE, DIGEST, SIGNATURE, "x-only", "s" }, new String[] { message, digestString, signature, xOnlyString, sString});
//      return Util.zip(new String[] { MESSAGE, SIGNATURE, "x-only", "s" }, new String[] { message, signature, xOnlyString, sString});
      return Util.zip(new String[] { MESSAGE, SIGNATURE }, new String[] { message, signature});
   }

   byte[] sign(byte[] data) throws GeneralSecurityException {
      this.signingSignature.update(data);
      return this.signingSignature.sign();
   }

   private byte[] digest(byte[] data) throws NoSuchAlgorithmException, NoSuchProviderException {
      MessageDigest hash = MessageDigest.getInstance("SHA256", "LunaProvider");
      hash.update(data);
      byte[] digest = hash.digest();
      return digest;
   }
   
   @RequestMapping(method = RequestMethod.POST, value = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> verify(@RequestBody Map<String, String> payload) throws GeneralSecurityException {
      String message = Optional.of(payload.get(MESSAGE))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + MESSAGE));
      String signature = Optional.of(payload.get(SIGNATURE))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + SIGNATURE));

      this.logger.info("Verifying Message '{}' and Signature '{}'", message, signature);

      this.verificationSignature.update(message.getBytes());
//      boolean verified = this.verificationSignature.verify(this.decoder.decode(signature));
      boolean verified = this.verificationSignature.verify(CodecUtils.fromHex(signature));

      return Util.zip(new String[] { MESSAGE, SIGNATURE, "verified" }, new Object[] { message, signature, verified });
   }

   @RequestMapping(method = RequestMethod.POST, value = "/savecsr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> savecsr(@RequestBody Map<String, String> payload) throws GeneralSecurityException, ParseException, EncodeFailedException, EncodeNotSupportedException, IOException {
      Date nowDate = ClockHelper.nowDate();
      ZonedDateTime nowZdt = DateTimeUtils.isoDateTime(nowDate);
      Time32 currentTime = Time32Helper.dateToTime32(nowDate);
      String name = Optional.of(payload.get(NAME))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + NAME));
      String validityPeriodStart = Optional.of(payload.get(VALIDITY_PERIOD_START))
            .orElse(DateTimeUtils.isoDateTime(nowZdt));
      String validityPeriodStop = Optional.of(payload.get(VALIDITY_PERIOD_STOP))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + VALIDITY_PERIOD_STOP));
      Long validityPeriodDurationHours = 
            DateTimeUtils.difference(DateTimeUtils.isoDateTime(validityPeriodStart), 
               DateTimeUtils.isoDateTime(validityPeriodStop))/1000/60/60;
      
      this.logger.info("Genrating CSR for '{}':  '{}'", NAME, name);
      this.logger.info("Genrating CSR for '{}':  '{}'", VALIDITY_PERIOD_START, validityPeriodStart);
      this.logger.info("Genrating CSR for '{}':  '{}'", VALIDITY_PERIOD_STOP, validityPeriodStop);
      this.logger.info("validityPeriodDurationHours:  '{}'", validityPeriodDurationHours);

      PublicKey pubKey = this.keyPair.getPublic();
      this.logger.info("public ley:  '{}'", CodecUtils.toHex(pubKey.getEncoded()));

//      //TODO
//      CertificateId id;
//      HashedId3 cracaId;
//      CrlSeries crlSeries;
//      ValidityPeriod validityPeriod;
//      GeographicRegion region;
//      SequenceOfPsidGroupPermissions certRequestPermissions;
//      VerificationKeyIndicator verifyKeyIndicator;
//      ToBeSignedCertificate tbsData = new ToBeSignedCertificate(
//         id, cracaId, crlSeries, validityPeriod, region, null, null, null, 
//         certRequestPermissions, null, null, verifyKeyIndicator);
//
//      EeEcaCertRequest eeEcaCertRequest = new EeEcaCertRequest(
//         new Uint8(1), new org.campllc.scms.protocols.ieee1609dot2basetypes.Time32(currentTime.intValue()),
//         tbsData);
//
//      org.campllc.scms.protocols.ieee1609dot2scmsprotocol.ScmsPDU.Content scmsPduContent;
//      scmsPduContent.setChosenFlag(org.campllc.scms.protocols.ieee1609dot2scmsprotocol.ScmsPDU.Content.eca_ee_chosen);
//      scmsPduContent.setChosenValue(eeEcaCertRequest);
//      
//      ScopedCertificateRequest tbsRequest = 
//            new ScopedCertificateRequest(new Uint8(1), scmsPduContent);
//      
//      SignerIdentifier signer = new SignerIdentifier();
//      signer.setSelf(Null.VALUE);
//      
//      //TODO
//      EccP256CurvePoint r;
//      OctetString s;
//      EcdsaP256Signature ecdsaP256Signature = new EcdsaP256Signature(r, s);
//
//      org.campllc.scms.protocols.ieee1609dot2basetypes.Signature signature =
//            new org.campllc.scms.protocols.ieee1609dot2basetypes.Signature();
//      signature.setChosenFlag(org.campllc.scms.protocols.ieee1609dot2basetypes.Signature.ecdsaNistP256Signature_chosen);
//      signature.setChosenValue(ecdsaP256Signature);
//      
//      SignedCertificateRequest signedCertificateRequest = 
//            new SignedCertificateRequest(HashAlgorithm.sha256, tbsRequest, signer, signature);
//
//      org.campllc.scms.protocols.ieee1609dot2scmsprotocol.SignedEeEnrollmentCertRequest.Content seecrContent = 
//            new org.campllc.scms.protocols.ieee1609dot2scmsprotocol.SignedEeEnrollmentCertRequest.Content();
//      seecrContent.setChosenValue(signedCertificateRequest);
//      seecrContent.setChosenFlag(Content.signedCertificateRequest_chosen);
//      
//      SignedEeEnrollmentCertRequest seecr = 
//            new SignedEeEnrollmentCertRequest(new Uint8(3), seecrContent);
//
//      byte[] seecrEncode = this.coerCoder.encode(seecr).array();
      
      String csrFileName = CodecUtils.toHex(pubKey.getEncoded()).substring(0, 64)  + ".oer";
      FileOutputStream keyfos = new FileOutputStream(csrFileName);
      
//      keyfos.write(seecrEncode);
      keyfos.close();
      
      return Util.zip(new String[] { "csrName", "csrFileName", },
         new Object[] { name, csrFileName});
   }

   @RequestMapping(method = RequestMethod.GET, value = "/pkSigParams", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> pkSigParams() throws CryptoException, KeyStoreException, CertificateEncodingException, IOException {
      ECDSAProvider provider = new CryptoProvider().getSigner();
//      AsymmetricCipherKeyPair keyPair = provider.generateKeyPair();
//      logger.info("Generated keypair: {}", keyPair);
//      
//      ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters)keyPair.getPrivate();
//      ECPublicKeyParameters  publicKey  = (ECPublicKeyParameters)keyPair.getPublic();

      ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
      ECPublicKeyParameters  publicKey  = (ECPublicKeyParameters) PublicKeyFactory.createKey(keyPair.getPublic().getEncoded());

      final int maxByteBuffer = (1 << 16) - 1;
      ByteBuffer privateByteBuffer = ByteBuffer.allocate(maxByteBuffer);
      provider.encodePrivateKey(privateByteBuffer, privateKey);
      byte[] privateKeyBytes = (privateByteBuffer != null) ? (Arrays.copyOfRange(privateByteBuffer.array(), 0, privateByteBuffer.position())) : null;
      logger.debug("Private key size: {}", privateKeyBytes.length);
      
      EccP256CurvePoint encodedPublicKey = provider.encodePublicKey(publicKey);
      logger.debug("Public Key encoded");
      
      EcR_Params encodedPubKeyParams = getEcParams(encodedPublicKey);
      
      byte[] signerCertBytes = enrollmentCert.getEncoded();
      EcdsaP256SignatureWrapper pubKeySignature = provider.computeSignature(
         encodedPubKeyParams.getValue(), signerCertBytes, privateKey);
      logger.info("Signed Public Key");
      
      gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Signature encodedPubKeySig = 
            pubKeySignature.encode();
      
      EccP256CurvePoint encodedPubKeySigR = encodedPubKeySig.getEcdsaNistP256Signature().getR();
      OctetString encodedPubKeySigS = encodedPubKeySig.getEcdsaNistP256Signature().getS();
      
      EcR_Params encodedPubKeySigRParams = getEcParams(encodedPubKeySigR);

      EcParams encodedPubKeySigParams = new EcParams();
      encodedPubKeySigParams.setR(encodedPubKeySigRParams);
      encodedPubKeySigParams.setS(encodedPubKeySigS.byteArrayValue());
      
      return Util.zip(new String[] { "publicKeyCurvePoint", "privateKeySignature" },
         new Object[] {encodedPubKeyParams.toHex(), encodedPubKeySigParams.toHex()});
   }
   
   EcR_Params getEcParams(EccP256CurvePoint eccP256CurvePoint) {
      EcR_Params ecrParams = new EcR_Params();
      
      if(eccP256CurvePoint.hasCompressed_y_0()) {
         ecrParams.setValue(eccP256CurvePoint.getCompressed_y_0().byteArrayValue());
         ecrParams.setType(COMPRESSED_Y_0);
      } else if (eccP256CurvePoint.hasCompressed_y_1()) {
         ecrParams.setValue(eccP256CurvePoint.getCompressed_y_1().byteArrayValue());
         ecrParams.setType(COMPRESSED_Y_1);
      } else if (eccP256CurvePoint.hasX_only()) {
         ecrParams.setValue(eccP256CurvePoint.getX_only().byteArrayValue());
         ecrParams.setType(X_ONLY);
      }

      return ecrParams;
   }
}
