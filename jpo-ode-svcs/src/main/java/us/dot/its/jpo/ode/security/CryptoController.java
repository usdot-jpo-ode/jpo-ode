package us.dot.its.jpo.ode.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Cipher;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;
import com.oss.asn1.Null;
import com.oss.asn1.OctetString;

import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.CertificateId;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.PsidGroupPermissions;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.SequenceOfPsidGroupPermissions;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.SignerIdentifier;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.SubjectPermissions;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.ToBeSignedCertificate;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2.VerificationKeyIndicator;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.CrlSeries;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Duration;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.EccP256CurvePoint;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.GeographicRegion;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.HashAlgorithm;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.HashedId3;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Hostname;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.IdentifiedRegion;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Psid;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.PsidSspRange;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.PublicVerificationKey;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.SequenceOfIdentifiedRegion;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.SequenceOfOctetString;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.SequenceOfPsidSspRange;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.SspRange;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Time32;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Uint8;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.ValidityPeriod;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2ecaendentityinterface.EcaEndEntityInterfacePDU;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2ecaendentityinterface.EeEcaCertRequest;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2scmsprotocol.ScmsPDU;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2scmsprotocol.ScopedCertificateRequest;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2scmsprotocol.SignedCertificateRequest;
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2scmsprotocol.SignedEeEnrollmentCertRequest;
import gov.usdot.cv.security.clock.ClockHelper;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.crypto.CryptoProvider;
import gov.usdot.cv.security.crypto.ECDSAProvider;
import gov.usdot.cv.security.crypto.EcdsaP256SignatureWrapper;
import gov.usdot.cv.security.util.Ieee1609dot2Helper;
import gov.usdot.cv.security.util.Time32Helper;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.CodecUtils;
import us.dot.its.jpo.ode.util.JsonUtils;

@RestController
final class CryptoController {

   private static final int CRL_SERIES = 0;

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

   private static final String VALIDITY_DURATION_HOURS = "validityDurationHours";

   private static final String NAME = "name";

   private static final String SIGNATURE = "signature";

   private static final String MESSAGE = "message";

   private static final String PAYLOAD_MUST_CONTAIN = "Payload must contain ";

   private static final String CIPHER_TEXT = "cipher-text";

   private static final Object REGIONS = "regions";

   private static final Object PSIDS = "psids";

   private final Logger logger = LoggerFactory.getLogger(this.getClass());

   private final Base64.Decoder decoder = Base64.getDecoder();

   private final Cipher decryptionCipher;

   private final Base64.Encoder encoder = Base64.getEncoder();
   
   private final KeyPair keyPair;
   
   private final Cipher encryptionCipher;

   private final Signature signingSignature;

   private final Signature verificationSignature;

   private final Certificate enrollmentCert;

   private static ECDSAProvider provider = new CryptoProvider().getSigner();

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

      this.signingSignature.update(message.getBytes());
      byte[] sig= this.signingSignature.sign();
      
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

   public byte[] digest(byte[] data) throws NoSuchAlgorithmException, NoSuchProviderException {
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

   @RequestMapping(method = RequestMethod.POST, value = "/csr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> csr(@RequestBody Map<String, String> payload) throws GeneralSecurityException, ParseException, EncodeFailedException, EncodeNotSupportedException, IOException, CryptoException {
      Date nowDate = ClockHelper.nowDate();
      Time32 currentTime = Time32Helper.dateToTime32(nowDate);
      String name = Optional.of(payload.get(NAME))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + NAME));
      Integer validityPeriodDurationHours = Optional.of(Integer.valueOf(payload.get(VALIDITY_DURATION_HOURS)))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + VALIDITY_DURATION_HOURS));
      String[] regionsArray = Strings.split(Optional.of(payload.get(REGIONS))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + REGIONS)), ' ');
      String[] psidsArray = Strings.split(Optional.of(payload.get(PSIDS))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + PSIDS)),' ');
      
      this.logger.info("Genrating CSR for '{}':  '{}'", NAME, name);
      this.logger.info("Genrating CSR for '{}':  '{}'", VALIDITY_DURATION_HOURS, validityPeriodDurationHours);
      this.logger.info("Genrating CSR for '{}':  '{}'", REGIONS, payload.get(REGIONS));
      this.logger.info("Genrating CSR for '{}':  '{}'", PSIDS, payload.get(PSIDS));

      CertificateId id = new CertificateId();
      id.setName(new Hostname(name));
      HashedId3 cracaId = new HashedId3();
      cracaId.setValue(new byte[]{0, 0, 0});
      CrlSeries crlSeries = new CrlSeries(CRL_SERIES);
      Duration duration = new Duration();
      duration.setHours(validityPeriodDurationHours);
      ValidityPeriod validityPeriod = new ValidityPeriod(currentTime, duration);
      GeographicRegion region = new GeographicRegion();
      SequenceOfIdentifiedRegion identifiedRegion = new SequenceOfIdentifiedRegion();
      for (String regionStr : regionsArray)  {
         IdentifiedRegion ir = new IdentifiedRegion();
         ir.setCountryOnly(Integer.valueOf(regionStr));
         identifiedRegion.add(ir);
      }
      region.setIdentifiedRegion(identifiedRegion);
      SequenceOfPsidGroupPermissions certRequestPermissions = new SequenceOfPsidGroupPermissions();
      PsidGroupPermissions pgp = new PsidGroupPermissions();
      SubjectPermissions subjectPermissions = new SubjectPermissions();
      SequenceOfPsidSspRange explicit = new SequenceOfPsidSspRange();
      for (String psid : psidsArray) {
         PsidSspRange element = new PsidSspRange(new Psid(Integer.valueOf(psid)));
         SequenceOfOctetString opaque = new SequenceOfOctetString();
         opaque.add(new OctetString(new byte[]{0}));
         SspRange sspRange = new SspRange();
         sspRange.setOpaque(opaque);
         element.setSspRange(sspRange );
         explicit.add(element );
      }
      subjectPermissions.setExplicit(explicit );
      pgp.setSubjectPermissions(subjectPermissions);
      certRequestPermissions.add(pgp);
      VerificationKeyIndicator verifyKeyIndicator = new VerificationKeyIndicator();
      PublicVerificationKey verificationKey = new PublicVerificationKey();
      ECPublicKeyParameters  publicKey  = (ECPublicKeyParameters) PublicKeyFactory.createKey(keyPair.getPublic().getEncoded());
      EccP256CurvePoint encodedPublicKey = provider.encodePublicKey(publicKey);
      
      verificationKey.setEcdsaNistP256(encodedPublicKey );
      verifyKeyIndicator.setVerificationKey(verificationKey );
      ToBeSignedCertificate tbsData = new ToBeSignedCertificate(
         id, cracaId, crlSeries, validityPeriod, region, null, null, null, 
         certRequestPermissions, null, null, verifyKeyIndicator);

      EeEcaCertRequest eeEcaCertRequest = new EeEcaCertRequest(
         new Uint8(1), new Time32(currentTime.intValue()),
         tbsData);

      ScmsPDU.Content scmsPduContent = new ScmsPDU.Content();
      EcaEndEntityInterfacePDU eca_ee = new EcaEndEntityInterfacePDU();
      eca_ee.setEeEcaCertRequest(eeEcaCertRequest);
      scmsPduContent.setEca_ee(eca_ee );
      
      ScopedCertificateRequest tbsRequest = 
            new ScopedCertificateRequest(new Uint8(1), scmsPduContent);
      
      SignerIdentifier signer = new SignerIdentifier();
      signer.setSelf(Null.VALUE);
      
      ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
      EcdsaP256SignatureWrapper tbsRequestSignature = provider.computeSignature(
         Ieee1609dot2Helper.encodeCOER(tbsRequest), enrollmentCert.getEncoded(), privateKey);
      gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Signature signature = 
            tbsRequestSignature.encode();
      
      SignedCertificateRequest decodedSSR = new SignedCertificateRequest(HashAlgorithm.sha256, tbsRequest, signer, signature);
      SignedEeEnrollmentCertRequest.Content.SignedCertificateRequest signedCertificateRequest = 
            new SignedEeEnrollmentCertRequest.Content.SignedCertificateRequest(decodedSSR);
      signedCertificateRequest.setValue(Ieee1609dot2Helper.encodeCOER(decodedSSR));
      SignedEeEnrollmentCertRequest.Content seecrContent = new SignedEeEnrollmentCertRequest.Content();
      seecrContent.setSignedCertificateRequest(signedCertificateRequest);
      
      SignedEeEnrollmentCertRequest seecr =  new SignedEeEnrollmentCertRequest(new Uint8(3), seecrContent );

      byte[] seecrEncode = Ieee1609dot2Helper.encodeCOER(seecr);
      
      String csrFileName = CodecUtils.toHex(getEcParams(encodedPublicKey).getValue())  + ".oer";
      FileOutputStream keyfos = new FileOutputStream(csrFileName);
      
      keyfos.write(seecrEncode);
      keyfos.close();
      
      return Util.zip(new String[] { "csrName", "csrFileName", },
         new Object[] { name, csrFileName});
   }

   @RequestMapping(method = RequestMethod.GET, value = "/csrdemo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> csrDemo() throws CryptoException, KeyStoreException, CertificateEncodingException, IOException {
//      AsymmetricCipherKeyPair keyPair = provider.generateKeyPair();
//      logger.info("Generated keypair: {}", keyPair);
//      
//      ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters)keyPair.getPrivate();
//      ECPublicKeyParameters  publicKey  = (ECPublicKeyParameters)keyPair.getPublic();

      ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
      ECPublicKeyParameters  publicKey  = (ECPublicKeyParameters) PublicKeyFactory.createKey(keyPair.getPublic().getEncoded());

      EccP256CurvePoint encodedPublicKey = provider.encodePublicKey(publicKey);
      
      EcdsaP256SignatureWrapper pubKeySignature = provider.computeSignature(
         getEcParams(encodedPublicKey).getValue(), enrollmentCert.getEncoded(), privateKey);
      
      gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Signature encodedPubKeySig = 
            pubKeySignature.encode();
      
      EccP256CurvePoint encodedPubKeySigR = encodedPubKeySig.getEcdsaNistP256Signature().getR();
      OctetString encodedPubKeySigS = encodedPubKeySig.getEcdsaNistP256Signature().getS();
      
      EcR_Params encodedPubKeySigRParams = getEcParams(encodedPubKeySigR);

      EcParams encodedPubKeySigParams = new EcParams();
      encodedPubKeySigParams.setR(encodedPubKeySigRParams);
      encodedPubKeySigParams.setS(encodedPubKeySigS.byteArrayValue());
      
      EcR_Params encodedPubKeyParams = getEcParams(encodedPublicKey);
      
      return Util.zip(new String[] { "publicKeyCurvePoint", "privateKeySignature" },
         new Object[] {encodedPubKeyParams.toHex(), encodedPubKeySigParams.toHex()});
   }

//   private EcdsaP256SignatureWrapper createEcdsaP256Signature(
//      ECPrivateKeyParameters privateKey,
//      EccP256CurvePoint encodedPublicKey) throws CertificateEncodingException {
//      EcdsaP256SignatureWrapper pubKeySignature = provider.computeSignature(
//         getEcParams(encodedPublicKey).getValue(), enrollmentCert.getEncoded(), privateKey);
//      logger.info("Signed Public Key");
//      return pubKeySignature;
//   }

   private EcR_Params getEcParams(EccP256CurvePoint eccP256CurvePoint) {
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
