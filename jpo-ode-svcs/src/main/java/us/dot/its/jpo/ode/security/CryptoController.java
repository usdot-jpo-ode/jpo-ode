package us.dot.its.jpo.ode.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Cipher;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
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

   private final static Logger logger = LoggerFactory.getLogger(CryptoController.class);

   private static final int CRL_SERIES = 4;

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

   private static CryptoProvider provider = new CryptoProvider();

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

      logger.info("Decrypting Cipher Text '{}'", cipherText);

      this.decryptionCipher.update(this.decoder.decode(cipherText));
      String message = new String(this.decryptionCipher.doFinal(), Charset.defaultCharset()).trim();

      return Util.zip(new String[] { CIPHER_TEXT, MESSAGE }, new String[] { cipherText, message });
   }

   @RequestMapping(method = RequestMethod.POST, value = "/encrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, String> encrypt(@RequestBody Map<String, String> payload) throws GeneralSecurityException {
      String message = Optional.of(payload.get(MESSAGE))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + MESSAGE));

      logger.info("Encrypting Message '{}'", message);

      this.encryptionCipher.update(message.getBytes(Charset.defaultCharset()));
      String cipherText = this.encoder.encodeToString(this.encryptionCipher.doFinal());

      return Util.zip(new String[] { MESSAGE, CIPHER_TEXT }, new String[] { message, cipherText });
   }

   @RequestMapping(method = RequestMethod.POST, value = "/sign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, String> sign(@RequestBody Map<String, String> payload) throws GeneralSecurityException, DecodeFailedException, DecodeNotSupportedException {
      String message = Optional.of(payload.get(MESSAGE))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + MESSAGE));

      logger.info("Signing Message '{}'", message);

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

      logger.info("Verifying Message '{}' and Signature '{}'", message, signature);

      this.verificationSignature.update(message.getBytes());
      boolean verified = this.verificationSignature.verify(CodecUtils.fromHex(signature));

      return Util.zip(new String[] { MESSAGE, SIGNATURE, "verified" }, new Object[] { message, signature, verified });
   }

   @RequestMapping(method = RequestMethod.POST, value = "/csr", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> csr(@RequestBody Map<String, String> payload) throws Exception {

      CsrParams csrParams = new CsrParams(payload);
      SignedEeEnrollmentCertRequest seecr = buildCsr(csrParams);
      
      byte[] seecrEncode = Ieee1609dot2Helper.encodeCOER(seecr);
      
      Content content = seecr.getContent();
//      SignedEeEnrollmentCertRequest.Content.SignedCertificateRequest signedCertReq = 
//            content.getSignedCertificateRequest();
//      byte[] encodedSCR = signedCertReq.byteArrayValue();
//      SignedCertificateRequest decodedSCR = new SignedCertificateRequest();
//      Ieee1609dot2Helper.decodeCOER(encodedSCR, decodedSCR);
      SignedCertificateRequest signedCertReq = content.getSignedCertificateRequest().getContainedValue();
      ScopedCertificateRequest tbsReq = signedCertReq.getTbsRequest();
      
      EccP256CurvePoint eccP256CurvePoint = 
            tbsReq.getContent().getEca_ee().getEeEcaCertRequest().getTbsData().getVerifyKeyIndicator().getVerificationKey().getEcdsaNistP256();
      
      
      String csrFileName = CodecUtils.toHex(ECDSAProvider.getPublicKeyBytes(eccP256CurvePoint))  + ".oer";
      FileOutputStream keyfos = new FileOutputStream(csrFileName);

      keyfos.write(seecrEncode);
      keyfos.close();

      return Util.zip(new String[] { "csrName", "csrFileName", },
         new Object[] { csrParams.getName(), csrFileName});
   }

   @RequestMapping(method = RequestMethod.POST, value = "/csrdemo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> csrDemo(@RequestBody Map<String, String> payload) throws Exception {

      CsrParams csrParams = new CsrParams(payload);
      logger.info("Generating CSR with parms:  {}", csrParams);

      SignedEeEnrollmentCertRequest seecr = buildCsr(csrParams);
      
      byte[] signedEeEnrollmentCertRequest = Ieee1609dot2Helper.encodeCOER(seecr);

      return Util.zip(new String[] {"signedEeEnrollmentCertRequest"},
         new Object[] {CodecUtils.toHex(signedEeEnrollmentCertRequest)});
   }

   @RequestMapping(method = RequestMethod.POST, value = "/csrdemoverify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
   Map<String, Object> csrDemoVerify(@RequestBody Map<String, String> payload) throws Exception {
      String signedEeEnrollmentCertRequest = Optional.of(payload.get("signedEeEnrollmentCertRequest"))
            .orElseThrow(() -> new IllegalArgumentException(PAYLOAD_MUST_CONTAIN + "signedEeEnrollmentCertRequest"));

      logger.info("Veriying CSR for '{}':  '{}'", "signedEeEnrollmentCertRequest", signedEeEnrollmentCertRequest);

      SignedEeEnrollmentCertRequest seecr = new SignedEeEnrollmentCertRequest();
      Ieee1609dot2Helper.decodeCOER(CodecUtils.fromHex(signedEeEnrollmentCertRequest), seecr);
      Content content = seecr.getContent();

      SignedCertificateRequest signedCertReq = content.getSignedCertificateRequest().getContainedValue();
      gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Signature signature = signedCertReq.getSignature();
      ScopedCertificateRequest tbsReq = signedCertReq.getTbsRequest();
      byte[] toBeVerified = Ieee1609dot2Helper.encodeCOER(tbsReq);
      logger.debug("Extracted tbsRequest = {}", CodecUtils.toHex(toBeVerified));
      
      EcdsaP256SignatureWrapper signatureWrapper = EcdsaP256SignatureWrapper.decode(signature, provider.getSigner());
      logger.debug("Extracted signature.r = {}", CodecUtils.toHex(signatureWrapper.getR().toByteArray()));
      logger.debug("Extracted signature.s = {}", CodecUtils.toHex(signatureWrapper.getS().toByteArray()));

      byte[] digest = provider.getSigner().computeDigest(toBeVerified, "".getBytes());
      logger.debug("Calculated digest = {}", CodecUtils.toHex(digest));
      
      // let's extract the public key from the CSR
      EccP256CurvePoint recon = tbsReq.getContent().getEca_ee().getEeEcaCertRequest().getTbsData()
            .getVerifyKeyIndicator().getVerificationKey().getEcdsaNistP256();
      
      ECPublicKey enrollPubKey = (ECPublicKey) this.keyPair.getPublic();
      
      logger.debug("Enrollment Public Key [{}], [{}]: {}", 
         enrollPubKey.getFormat(),
         enrollPubKey.getAlgorithm(),
         CodecUtils.toHex(enrollPubKey.getW().getAffineX().toByteArray()));
      
      ECPublicKey extractedPubKey = (ECPublicKey) extractPublicKey(recon);
      
      logger.info("Extracted Public Key [{}], [{}]: {}", 
         extractedPubKey.getFormat(),
         extractedPubKey.getAlgorithm(),
         CodecUtils.toHex(extractedPubKey.getW().getAffineX().toByteArray()));

      Signature myVerificationSignature = Signature.getInstance(
         verificationSignature.getAlgorithm(),
         verificationSignature.getProvider());
      myVerificationSignature.initVerify(extractedPubKey);
      myVerificationSignature.update(digest);
      
      byte[] encodedSig = encodeECDSASignature(new BigInteger[]{signatureWrapper.getR(), signatureWrapper.getS()});
      logger.debug("encodedECDSASignature = {}", CodecUtils.toHex(encodedSig));
      boolean verified = myVerificationSignature.verify(encodedSig);
      
//      boolean verified = provider.getSigner().verifySignature(
//            toBeVerified,
//            "".getBytes(),
//            provider.getSigner().decodePublicKey(recon), 
//            signatureWrapper);
      
      return Util.zip(new String[] { "signedEeEnrollmentCertRequest", "verified" }, new Object[] { signedEeEnrollmentCertRequest, verified });
   }

   private PublicKey extractPublicKey(EccP256CurvePoint pubKeyCurvePoint)
         throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
      ECPublicKeyParameters pubKeyParams = provider.getSigner().decodePublicKey(pubKeyCurvePoint);
      ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("prime256v1");
      ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(pubKeyParams.getQ(), spec);
      KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
      PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
      return pubKey;
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
    * @throws Exception 
    */
   private SignedEeEnrollmentCertRequest buildCsr(CsrParams csrParams) throws Exception {
      CertificateId id = new CertificateId();
      id.setName(new Hostname(csrParams.getName()));
      HashedId3 cracaId = new HashedId3();
      cracaId.setValue(new byte[]{0, 0, 0});
      CrlSeries crlSeries = new CrlSeries(CRL_SERIES);
      Duration duration = new Duration();
      duration.setHours(csrParams.getValidityPeriodDurationHours());
      ValidityPeriod validityPeriod = new ValidityPeriod(csrParams.currentTime, duration);
      SequenceOfIdentifiedRegion identifiedRegion = new SequenceOfIdentifiedRegion();
      for (String regionStr : csrParams.getRegionsArray())  {
         identifiedRegion.add(IdentifiedRegion.createIdentifiedRegionWithCountryOnly(Integer.valueOf(regionStr)));
      }
      GeographicRegion region = GeographicRegion.createGeographicRegionWithIdentifiedRegion(identifiedRegion);
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
         id, cracaId, crlSeries, validityPeriod, verifyKeyIndicator);
      tbsData.setRegion(region);
      tbsData.setCertRequestPermissions(certRequestPermissions);
      
      EeEcaCertRequest eeEcaCertRequest = new EeEcaCertRequest(
         new Uint8(1), new Time32(csrParams.currentTime.intValue()),
         tbsData);

      EcaEndEntityInterfacePDU endEntityInterfacePDU = 
            EcaEndEntityInterfacePDU.createEcaEndEntityInterfacePDUWithEeEcaCertRequest(eeEcaCertRequest);
      ScopedCertificateRequest scopedCertificateRequest = new ScopedCertificateRequest(new Uint8(1),
         ScmsPDU.Content.createContentWithEca_ee(endEntityInterfacePDU));
      
      byte[] tbsRequest = Ieee1609dot2Helper.encodeCOER(scopedCertificateRequest);
      logger.debug("tbsRequest = {}", CodecUtils.toHex(tbsRequest));

      byte[] digest = provider.getSigner().computeDigest(tbsRequest, "".getBytes());
      this.signingSignature.update(digest);
      byte[] sig= this.signingSignature.sign();
      logger.debug("signature: {}", CodecUtils.toHex(sig));
      
      BigInteger[] sigParts = decodeECDSASignature(sig);
      EcdsaP256SignatureWrapper tbsRequestSignature = new EcdsaP256SignatureWrapper(sigParts[0], sigParts[1]);

//      ECPrivateKey ecPrivateKey = (ECPrivateKey) this.keyPair.getPrivate();
//      EcdsaP256SignatureWrapper tbsRequestSignature = 
//            provider.getSigner().computeSignature(tbsRequest, 
//                  "".getBytes(), 
//                  provider.getSigner().decodePrivateKey(ecPrivateKey));

      gov.usdot.asn1.generated.ieee1609dot2.ieee1609dot2basetypes.Signature signature = 
            tbsRequestSignature.encode();
      
      SignerIdentifier signer = SignerIdentifier.createSignerIdentifierWithSelf(new Null());
      
      SignedCertificateRequest signedCertificateRequest = new SignedCertificateRequest(HashAlgorithm.sha256, scopedCertificateRequest, signer, signature);

      SignedEeEnrollmentCertRequest seecr = new SignedEeEnrollmentCertRequest(
         new Uint8(3), SignedEeEnrollmentCertRequest.Content.createContentWithSignedCertificateRequest(
               new SignedEeEnrollmentCertRequest.Content.SignedCertificateRequest(signedCertificateRequest)));

      return seecr;
   }

   
   private EccP256CurvePoint buildPublicKeyCurvePoint() throws InvalidKeyException, CryptoException {
      ECPublicKeyParameters  publicKey = (ECPublicKeyParameters) ECUtil.generatePublicKeyParameter(keyPair.getPublic());
      EccP256CurvePoint encodedPublicKey = provider.getSigner().encodePublicKey(publicKey);
      return encodedPublicKey;
   }

   private static BigInteger[] decodeECDSASignature(byte[] signature) throws Exception {
      BigInteger[] sigs = new BigInteger[2];
      ByteArrayInputStream inStream = new ByteArrayInputStream(signature);
      try (ASN1InputStream asnInputStream = new ASN1InputStream(inStream)){
         ASN1Primitive asn1 = asnInputStream.readObject();

         int count = 0;
         if (asn1 instanceof ASN1Sequence) {
             ASN1Sequence asn1Sequence = (ASN1Sequence) asn1;
             ASN1Encodable[] asn1Encodables = asn1Sequence.toArray();
             for (ASN1Encodable asn1Encodable : asn1Encodables) {
                 ASN1Primitive asn1Primitive = asn1Encodable.toASN1Primitive();
                 if (asn1Primitive instanceof ASN1Integer) {
                     ASN1Integer asn1Integer = (ASN1Integer) asn1Primitive;
                     BigInteger integer = asn1Integer.getValue();
                     if (count  < 2) {
                         sigs[count] = integer;
                     }
                     count++;
                 }
             }
         }
         if (count != 2) {
             throw new CryptoException(String.format("Invalid ECDSA signature. Expected count of 2 but got: %d. Signature is: %s", count,
                     CodecUtils.toHex(signature)));
         }
      } catch (Exception e) {
         logger.error("Error decoding encoded signature", e);
      }
      return sigs;
   }

   private static byte[] encodeECDSASignature(BigInteger[] sigs) throws Exception {
      ByteArrayOutputStream s = new ByteArrayOutputStream();
      
      DERSequenceGenerator seq = new DERSequenceGenerator(s);
      seq.addObject(new ASN1Integer(sigs[0]));
      seq.addObject(new ASN1Integer(sigs[1]));
      seq.close();
      return s.toByteArray(); 
   }
}
