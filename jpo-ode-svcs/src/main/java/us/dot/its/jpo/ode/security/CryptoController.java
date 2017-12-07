package us.dot.its.jpo.ode.security;

import org.campllc.scms.protocols.Protocols;
import org.campllc.scms.protocols.ieee1609dot2.CertificateId;
import org.campllc.scms.protocols.ieee1609dot2.SequenceOfPsidGroupPermissions;
import org.campllc.scms.protocols.ieee1609dot2.SignerIdentifier;
import org.campllc.scms.protocols.ieee1609dot2.ToBeSignedCertificate;
import org.campllc.scms.protocols.ieee1609dot2.ToBeSignedData;
import org.campllc.scms.protocols.ieee1609dot2.VerificationKeyIndicator;
import org.campllc.scms.protocols.ieee1609dot2basetypes.CrlSeries;
import org.campllc.scms.protocols.ieee1609dot2basetypes.GeographicRegion;
import org.campllc.scms.protocols.ieee1609dot2basetypes.HashAlgorithm;
import org.campllc.scms.protocols.ieee1609dot2basetypes.HashedId3;
import org.campllc.scms.protocols.ieee1609dot2basetypes.Uint8;
import org.campllc.scms.protocols.ieee1609dot2basetypes.ValidityPeriod;
import org.campllc.scms.protocols.ieee1609dot2ecaendentityinterface.EeEcaCertRequest;
import org.campllc.scms.protocols.ieee1609dot2scmsprotocol.ScopedCertificateRequest;
import org.campllc.scms.protocols.ieee1609dot2scmsprotocol.SignedCertificateRequest;
import org.campllc.scms.protocols.ieee1609dot2scmsprotocol.SignedEeEnrollmentCertRequest;
import org.campllc.scms.protocols.ieee1609dot2scmsprotocol.SignedEeEnrollmentCertRequest.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.oss.asn1.AbstractData;
import com.oss.asn1.COERCoder;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.Null;
import com.oss.asn1.OctetString;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.EccP256CurvePoint;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.EcdsaP256Signature;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Time32;
import gov.usdot.cv.security.clock.ClockHelper;
import gov.usdot.cv.security.util.Time32Helper;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.DateTimeUtils;

import javax.crypto.Cipher;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
final class CryptoController {

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
   
   private final COERCoder coerCoder = Protocols.getCOERCoder();

   private final KeyPair keyPair;
   
   private final Cipher encryptionCipher;

   private final Signature signingSignature;

   private final Signature verificationSignature;

   @Autowired
   CryptoController(
      @Qualifier("keyPair") KeyPair keyPair,
      @Qualifier("decryptionCipher") Cipher decryptionCipher,
      @Qualifier("encryptionCipher") Cipher encryptionCipher,
      @Qualifier("signingSignature") Signature signingSignature,
      @Qualifier("verificationSignature") Signature verificationSignature) {
      this.keyPair = keyPair;
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
   Map<String, String> sign(@RequestBody Map<String, String> payload) throws GeneralSecurityException {
      String message = Optional.of(payload.get(MESSAGE))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + MESSAGE));

      this.logger.info("Signing Message '{}'", message);

      byte[] digest = digest(message.getBytes());
      String digestString = this.encoder.encodeToString(digest);

      String signature = this.encoder.encodeToString(sign(digest));

      return Util.zip(new String[] { MESSAGE, DIGEST, SIGNATURE }, new String[] { message, digestString, signature });
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
      String digest = Optional.of(payload.get(DIGEST))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + DIGEST));
      String signature = Optional.of(payload.get(SIGNATURE))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + SIGNATURE));

      this.logger.info("Verifying Message '{}' and Signature '{}'", digest, signature);

      this.verificationSignature.update(this.decoder.decode(digest));
      boolean verified = this.verificationSignature.verify(this.decoder.decode(signature));

      return Util.zip(new String[] { DIGEST, SIGNATURE, "verified" }, new Object[] { digest, signature, verified });
   }

   @RequestMapping(method = RequestMethod.POST, value = "/csr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> csr(@RequestBody Map<String, String> payload) throws GeneralSecurityException, ParseException, EncodeFailedException, EncodeNotSupportedException, IOException {
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

}
