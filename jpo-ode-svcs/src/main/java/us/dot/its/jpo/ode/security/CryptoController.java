package us.dot.its.jpo.ode.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Cipher;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
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
import com.safenetinc.luna.provider.key.LunaPrivateKeyECDsa;
import com.safenetinc.luna.provider.keyfactory.LunaKeyFactory;
import com.safenetinc.luna.provider.keyfactory.LunaKeyFactoryEC;
import com.safenetinc.luna.provider.param.LunaECUtils;
import com.safenetinc.luna.provider.param.LunaParametersEC;
import com.safenetinc.luna.provider.signature.LunaSignatureSHA256withECDSA;

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
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.EcdsaP256Signature;
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
import gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2scmsprotocol.SignedEeEnrollmentCertRequest.Content;
import gov.usdot.cv.security.clock.ClockHelper;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.crypto.CryptoProvider;
import gov.usdot.cv.security.crypto.ECDSAProvider;
import gov.usdot.cv.security.crypto.EcdsaP256SignatureWrapper;
import gov.usdot.cv.security.util.Ieee1609dot2Helper;
import gov.usdot.cv.security.util.Time32Helper;
import us.dot.its.jpo.ode.model.OdeObject;
import us.dot.its.jpo.ode.util.CodecUtils;

@RestController
final class CryptoController {

   private final Logger logger = LoggerFactory.getLogger(this.getClass());

   private static final int CRL_SERIES = 0;

   public class CsrParams extends OdeObject {
      private static final long serialVersionUID = 1L;
      private String name; // Name associated with the CSR
      private Integer validityPeriodDurationHours; // validity period duration of the CSR in hours
      private String[] regionsArray; // space separated list of region codes in decimal integer
      private String[] psidsArray; // space separated list of PSID codes in decimal integer
      private Time32 currentTime;
      
      
      public CsrParams(Map<String, String> payload) {
         name = Optional.of(payload.get(NAME))
               .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + NAME));
         validityPeriodDurationHours = Optional.of(Integer.valueOf(payload.get(VALIDITY_DURATION_HOURS)))
               .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + VALIDITY_DURATION_HOURS));
         regionsArray = Strings.split(Optional.of(payload.get(REGIONS))
               .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + REGIONS)),
            ' ');
         psidsArray = Strings.split(Optional.of(payload.get(PSIDS))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + PSIDS)),
         ' ');
         
         String strCurTime = payload.get("currentTime");
         if (strCurTime == null) {
            currentTime = Time32Helper.dateToTime32(ClockHelper.nowDate());
         } else {
            currentTime = new Time32(Integer.valueOf(strCurTime));
         }
         
         logger.info("Genrating CSR for '{}':  '{}'", NAME, name);
         logger.info("Genrating CSR for '{}':  '{}'", VALIDITY_DURATION_HOURS, validityPeriodDurationHours);
         logger.info("Genrating CSR for '{}':  '{}'", REGIONS, payload.get(REGIONS));
         logger.info("Genrating CSR for '{}':  '{}'", PSIDS, payload.get(PSIDS));
         logger.info("Genrating CSR for '{}':  '{}'", "currentTime", payload.get("currentTime"));
      }

      public String getName() {
         return name;
      }
      public void setName(String name) {
         this.name = name;
      }
      public Integer getValidityPeriodDurationHours() {
         return validityPeriodDurationHours;
      }
      public void setValidityPeriodDurationHours(Integer validityPeriodDurationHours) {
         this.validityPeriodDurationHours = validityPeriodDurationHours;
      }
      public String[] getRegionsArray() {
         return regionsArray;
      }
      public void setRegionsArray(String[] regionsArray) {
         this.regionsArray = regionsArray;
      }
      public String[] getPsidsArray() {
         return psidsArray;
      }
      public void setPsidsArray(String[] psidsArray) {
         this.psidsArray = psidsArray;
      }

      public Time32 getCurrentTime() {
         return currentTime;
      }

      public void setCurrentTime(Time32 currentTime) {
         this.currentTime = currentTime;
      }


      
   }

   private static final String VALIDITY_DURATION_HOURS = "validityDurationHours";

   private static final String NAME = "name";

   private static final String SIGNATURE = "signature";

   private static final String MESSAGE = "message";

   private static final String PAYLOAD_MUST_CONTAIN = "Payload must contain ";

   private static final String CIPHER_TEXT = "cipher-text";

   private static final Object REGIONS = "regions";

   private static final Object PSIDS = "psids";

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

      this.signingSignature.update(message.getBytes());
      byte[] sig= this.signingSignature.sign();
      
      String signature = CodecUtils.toHex(sig);

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
   Map<String, Object> csr(@RequestBody Map<String, String> payload) throws GeneralSecurityException, ParseException, EncodeFailedException, EncodeNotSupportedException, IOException, CryptoException, DecodeFailedException, DecodeNotSupportedException {

      CsrParams csrParams = new CsrParams(payload);
      SignedEeEnrollmentCertRequest seecr = buildCsr(csrParams);
      
      byte[] seecrEncode = Ieee1609dot2Helper.encodeCOER(seecr);
      
      Content content = seecr.getContent();
      SignedEeEnrollmentCertRequest.Content.SignedCertificateRequest signedCertReq = 
            content.getSignedCertificateRequest();
      byte[] encodedSCR = signedCertReq.byteArrayValue();
      SignedCertificateRequest decodedSCR = new SignedCertificateRequest();
      Ieee1609dot2Helper.decodeCOER(encodedSCR, decodedSCR);
      ScopedCertificateRequest tbsReq = decodedSCR.getTbsRequest();
      
      EccP256CurvePoint eccP256CurvePoint = 
            tbsReq.getContent().getEca_ee().getEeEcaCertRequest().getTbsData().getVerifyKeyIndicator().getVerificationKey().getEcdsaNistP256();
      
      String csrFileName = CodecUtils.toHex(getCurvePointValue(eccP256CurvePoint))  + ".oer";
      FileOutputStream keyfos = new FileOutputStream(csrFileName);

      keyfos.write(seecrEncode);
      keyfos.close();

      return Util.zip(new String[] { "csrName", "csrFileName", },
         new Object[] { csrParams.getName(), csrFileName});
   }

   @RequestMapping(method = RequestMethod.POST, value = "/csrdemo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> csrDemo(@RequestBody Map<String, String> payload) throws CryptoException, KeyStoreException, CertificateEncodingException, IOException, EncodeFailedException, EncodeNotSupportedException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

      CsrParams csrParams = new CsrParams(payload);
      logger.info("Generating CSR with parms:  {}", csrParams);

      SignedEeEnrollmentCertRequest seecr = buildCsr(csrParams);
      
      byte[] signedEeEnrollmentCertRequest = Ieee1609dot2Helper.encodeCOER(seecr);

      return Util.zip(new String[] {"signedEeEnrollmentCertRequest"},
         new Object[] {CodecUtils.toHex(signedEeEnrollmentCertRequest)});
   }

   @RequestMapping(method = RequestMethod.POST, value = "/csrdemoverify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> csrDemoVerify(@RequestBody Map<String, String> payload) throws IOException, DecodeFailedException, DecodeNotSupportedException, CertificateEncodingException, EncodeFailedException, EncodeNotSupportedException {
      String signedEeEnrollmentCertRequest = Optional.of(payload.get("signedEeEnrollmentCertRequest"))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + "signedEeEnrollmentCertRequest"));

      logger.info("Veriying CSR for '{}':  '{}'", "signedEeEnrollmentCertRequest", signedEeEnrollmentCertRequest);

      ECDSAProvider ecdsaProvider = new ECDSAProvider();
      
      SignedEeEnrollmentCertRequest seecr = new SignedEeEnrollmentCertRequest();
      Ieee1609dot2Helper.decodeCOER(CodecUtils.fromHex(signedEeEnrollmentCertRequest), seecr);
      Content content = seecr.getContent();
      SignedEeEnrollmentCertRequest.Content.SignedCertificateRequest signedCertReq = 
            content.getSignedCertificateRequest();
      byte[] encodedSCR = signedCertReq.byteArrayValue();
      SignedCertificateRequest decodedSCR = new SignedCertificateRequest();
      Ieee1609dot2Helper.decodeCOER(encodedSCR, decodedSCR);
      gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Signature signature = decodedSCR.getSignature();
      ScopedCertificateRequest tbsReq = decodedSCR.getTbsRequest();
      byte[] toBeVerified = Ieee1609dot2Helper.encodeCOER(tbsReq);
      
      EcdsaP256SignatureWrapper signtureWrapper = EcdsaP256SignatureWrapper.decode(signature, ecdsaProvider);
      
      EccP256CurvePoint eccP256CurvePoint = 
            tbsReq.getContent().getEca_ee().getEeEcaCertRequest().getTbsData().getVerifyKeyIndicator().getVerificationKey().getEcdsaNistP256();
      
      ECPublicKeyParameters publicKeyParams = provider.decodePublicKey(eccP256CurvePoint);

      boolean verified = ecdsaProvider.verifySignature(toBeVerified, 
         enrollmentCert.getEncoded(), publicKeyParams, signtureWrapper);

      return Util.zip(new String[] { "signedEeEnrollmentCertRequest", "verified" }, new Object[] { signedEeEnrollmentCertRequest, verified });
   }

   /**
    * Builds a SignedEeEnrollmentCertRequest containing a Certificate Signing Request (CSR)
    * containing a public key, and the signature of the CSR using the corresponding private key. 
    * 
    * Here's a sample SignedEeEnrollmentCertRequest
    * 
    * value SignedEeEnrollmentCertRequest ::= 
{
  protocolVersion 3,
  content signedCertificateRequest : 
      CONTAINING
      {
        hashId sha256,
        tbsRequest 
        {
          version 1,
          content eca-ee : eeEcaCertRequest : 
              {
                version 1,
                currentTime 431026272,
                tbsData 
                {
                  id name : "obeenr",
                  cracaId '000000'H,
                  crlSeries 4,
                  validityPeriod 
                  {
                    start 431026272,
                    duration hours : 4320
                  },
                  region identifiedRegion : 
                    {
                      countryOnly : 124,
                      countryOnly : 484,
                      countryOnly : 840
                    },
                  certRequestPermissions 
                  {
                    {
                      subjectPermissions explicit : 
                        {
                          {
                            psid 32,
                            sspRange opaque : 
                              {
                              }
                          },
                          {
                            psid 38,
                            sspRange opaque : 
                              {
                              }
                          }
                        },
                      minChainDepth 0
                    }
                  },
                  verifyKeyIndicator verificationKey : ecdsaNistP256 : compressed-y-1 : '8751D2FDC5D7BF8CCE4A7FACE5E5AD7B92 ...'H
                }
              }
        },
        signer self : NULL,
        signature ecdsaNistP256Signature : 
          {
            r x-only : '301D57F8D01E98C685428C49328BE8164B ...'H,
            s '3121B89C7919FD75D7AB411CFB254A4466 ...'H
          }
      }
}
    * @param csrParams parameters for buiding the CSR
    * @return a SignedEeEnrollmentCertRequest
    * 
    * @throws IOException
    * @throws CryptoException
    * @throws EncodeFailedException
    * @throws EncodeNotSupportedException
    * @throws CertificateEncodingException
    * @throws SignatureException 
    * @throws InvalidKeyException 
    */
   private SignedEeEnrollmentCertRequest buildCsr(CsrParams csrParams) throws IOException, CryptoException, EncodeFailedException, EncodeNotSupportedException, CertificateEncodingException, SignatureException, InvalidKeyException {
      Date nowDate = ClockHelper.nowDate();
      Time32 currentTime = Time32Helper.dateToTime32(nowDate);

      CertificateId id = new CertificateId();
      id.setName(new Hostname(csrParams.getName()));
      HashedId3 cracaId = new HashedId3();
      cracaId.setValue(new byte[]{0, 0, 0});
      CrlSeries crlSeries = new CrlSeries(CRL_SERIES);
      Duration duration = new Duration();
      duration.setHours(csrParams.getValidityPeriodDurationHours());
      ValidityPeriod validityPeriod = new ValidityPeriod(currentTime, duration);
      GeographicRegion region = new GeographicRegion();
      SequenceOfIdentifiedRegion identifiedRegion = new SequenceOfIdentifiedRegion();
      for (String regionStr : csrParams.getRegionsArray())  {
         IdentifiedRegion ir = new IdentifiedRegion();
         ir.setCountryOnly(Integer.valueOf(regionStr));
         identifiedRegion.add(ir);
      }
      region.setIdentifiedRegion(identifiedRegion);
      SequenceOfPsidGroupPermissions certRequestPermissions = new SequenceOfPsidGroupPermissions();
      PsidGroupPermissions pgp = new PsidGroupPermissions();
      SubjectPermissions subjectPermissions = new SubjectPermissions();
      SequenceOfPsidSspRange explicit = new SequenceOfPsidSspRange();
      for (String psid : csrParams.getPsidsArray()) {
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
      EccP256CurvePoint encodedPublicKey = buildPublicKeyCurvePoint();
      
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
      
      ECPrivateKeyParameters privateKeyParams = 
            (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(keyPair.getPrivate());
      
//      ECPrivateKeyParameters privateKeyParams = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(keyPair.getPrivate().getEncoded());
      byte[] encodedTbsRequest = Ieee1609dot2Helper.encodeCOER(tbsRequest);
      EcdsaP256SignatureWrapper tbsRequestSignature = provider.computeSignature(
         encodedTbsRequest, enrollmentCert.getEncoded(), privateKeyParams );
      
//      byte[] digest = provider.computeDigest(encodedTbsRequest, enrollmentCert.getEncoded());
//      this.signingSignature.update(digest);
//      byte[] sig= this.signingSignature.sign();
//      logger.debug("sig: {}", CodecUtils.toHex(sig));
//      ECParameterSpec sigParams = LunaParametersEC.decodeParameters(sig);
//      EllipticCurve curve = sigParams.getCurve();
//      int cofactor = sigParams.getCofactor();
//      ECPoint generator = sigParams.getGenerator();
//      BigInteger order = sigParams.getOrder();
//
//      EccP256CurvePoint r = new EccP256CurvePoint();
//      byte[] s = new byte[]{0};
//      
//      EcdsaP256Signature signature = new EcdsaP256Signature(r, new OctetString(s));
      
      gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Signature signature = 
            tbsRequestSignature.encode();
      
      SignedCertificateRequest decodedSCR = new SignedCertificateRequest(HashAlgorithm.sha256, tbsRequest, signer, signature);
      byte[] encodedSCR = Ieee1609dot2Helper.encodeCOER(decodedSCR);
      SignedEeEnrollmentCertRequest.Content.SignedCertificateRequest signedCertificateRequest = 
            new SignedEeEnrollmentCertRequest.Content.SignedCertificateRequest(encodedSCR);
      SignedEeEnrollmentCertRequest.Content seecrContent = 
            SignedEeEnrollmentCertRequest.Content.createContentWithSignedCertificateRequest(signedCertificateRequest);
      
      SignedEeEnrollmentCertRequest seecr =  new SignedEeEnrollmentCertRequest(new Uint8(3), seecrContent );
      return seecr;
   }

   
   private EccP256CurvePoint buildPublicKeyCurvePoint() throws IOException, CryptoException {
      ECPublicKeyParameters  publicKey  = (ECPublicKeyParameters) PublicKeyFactory.createKey(keyPair.getPublic().getEncoded());
      EccP256CurvePoint encodedPublicKey = provider.encodePublicKey(publicKey);
      return encodedPublicKey;
   }

   private byte[] getCurvePointValue(EccP256CurvePoint eccP256CurvePoint) {
      if(eccP256CurvePoint.hasCompressed_y_0()) {
         return eccP256CurvePoint.getCompressed_y_0().byteArrayValue();
      } else if (eccP256CurvePoint.hasCompressed_y_1()) {
         return eccP256CurvePoint.getCompressed_y_1().byteArrayValue();
      } else if (eccP256CurvePoint.hasX_only()) {
         return eccP256CurvePoint.getX_only().byteArrayValue();
      }

      return null;
   }

}
