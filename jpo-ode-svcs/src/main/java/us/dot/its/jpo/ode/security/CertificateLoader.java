package us.dot.its.jpo.ode.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import com.oss.asn1.EncodeFailedException;
import com.oss.asn1.EncodeNotSupportedException;

import gov.usdot.cv.security.cert.CertificateException;
import gov.usdot.cv.security.cert.CertificateManager;
import gov.usdot.cv.security.cert.CertificateWrapper;
import gov.usdot.cv.security.cert.FileCertificateStore;
import gov.usdot.cv.security.cert.SecureECPrivateKey;
import gov.usdot.cv.security.crypto.CryptoException;
import gov.usdot.cv.security.crypto.CryptoProvider;
import us.dot.its.jpo.ode.OdeProperties;

public class CertificateLoader implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(CertificateLoader.class);

    private OdeProperties odeProperties;
    private SecureECPrivateKey seedPrivateKey;
    

    public CertificateLoader(OdeProperties odeProperties) {
      super();
      this.odeProperties = odeProperties;
   }

   public void loadAllCerts(String certsDir) {
       int count = 0;
       // Process certs
       logger.info("Loading certificates from location: {}", certsDir);
       try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(certsDir))) {

           if (stream == null) {
               throw new IOException("Failed to create directory stream for location " + certsDir);
           }

           for (Path subdir : stream) {
               Path filename = subdir.getFileName();
               logger.debug("Processing {}", filename);
               try (DirectoryStream<Path> tcstream = Files.newDirectoryStream(subdir)) {
                   for (Path certFile : tcstream) {
                       String certFileName = certFile.getFileName().toString();
                       logger.info("Loading trsuted certificate: {}", certFileName);
                       try {
                           loadCert(certFileName, certFile);
                       } catch (Exception e) {
                           logger.error("Error loading certificate: " + certFile, e);
                       }
                   }
               } catch (Exception e) {
                   logger.error("Error loading certificates in: " + subdir, e);
               }
               count++;
           }
           logger.debug("Loaded {} certificates from location: {}", count, certsDir);
       } catch (Exception e) {
           logger.error("Error loading certifcate files.", e);
       }
   }

    public int loadAllCerts(Path zipFilePath) {
       int count = 0;
       // Process certs
       logger.info("Loading certificates from: {}", zipFilePath);
       try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
          // get the zipped file list entry
          ZipEntry entry = zis.getNextEntry();

          while (entry != null) {
               logger.debug("Processing {}", entry);
               if (entry.getName().endsWith("root.oer")) {
                  loadCert(zis, entry, CertificateWrapper.getRootPublicCertificateFriendlyName());
               } else if (entry.getName().endsWith("ECA.oer")) {
                  loadCert(zis, entry, CertificateWrapper.getEcaPublicCertificateFriendlyName());
               } else if (entry.getName().endsWith("enrollment.oer")) {
                  loadCert(zis, entry, CertificateWrapper.getEnrollmentPublicCertificateFriendlyName());
               } else if (entry.getName().endsWith("enrollment.s")) {
                  loadS(zis, entry);
               } else if (entry.getName().endsWith("RA.oer")) {
                  loadCert(zis, entry, CertificateWrapper.getRaPublicCertificateFriendlyName());
               }
               count++;
               entry = zis.getNextEntry();
           }
           logger.debug("Loaded {} certificates from: {}", count, zipFilePath);
       } catch (Exception e) {
           logger.error("Error loading some certifcates from " + zipFilePath, e);
       }
      return count;
   }

    private void loadS(ZipInputStream zis, ZipEntry entry) {
      // TODO Auto-generated method stub
      
   }

   private boolean loadCert(ZipInputStream zis, ZipEntry entry, String name) 
         throws CertificateException {
      logger.info("Loading trsuted certificate: {}", entry.getName());
      return FileCertificateStore.load(new CryptoProvider(), name, entry, zis);
   }

   public boolean loadCert(String name, Path certFilePath)
                                           throws CertificateException, 
                                           IOException, 
                                           DecoderException, 
                                           CryptoException, 
                                           DecodeFailedException, 
                                           DecodeNotSupportedException, 
                                           EncodeFailedException, 
                                           EncodeNotSupportedException {
        return loadCert(name, certFilePath, null, null);
    }
    
    public boolean loadCert(String name, Path certFilePath, Path privateKeyReconFilePath, 
       SecureECPrivateKey seedPrivateKey)
                                           throws CertificateException, 
                                           IOException, 
                                           DecoderException, 
                                           CryptoException, 
                                           DecodeFailedException, 
                                           DecodeNotSupportedException, 
                                           EncodeFailedException, 
                                           EncodeNotSupportedException {
        
        //load public cert
        return FileCertificateStore.load(new CryptoProvider(), name, certFilePath, 
           privateKeyReconFilePath, seedPrivateKey);
   }
    

    @Override
    public void run() {

        logger.info(this.getClass().getSimpleName() + " initiated.");

        loadAllCerts();
    }

   private void loadAllCerts() {
      // Initialize ODE properties to set all SCMS file paths
      initScmsProperties();

      // 0. Begin with an empty store
      CertificateManager.clear();

      // 1. Load Enrollment certificates
      try {
         loadEnrollmentCerts();
      } catch (Exception e) {
          logger.error("Error loading some Enrollment certificates", e);
      }
 
      // TODO
      // 2. Provision Application Certificate (https://wiki.campllc.org/display/SCP/Use+Case+13%3A+RSE+Application+Certificate+Provisioning)
      /*
       * At a high level, two steps are relevant:
       *    1. Request Application Certificate
       *    2. Download Application Certificate
       */
      try {
         provisionApplicationCerts();
      } catch (Exception e) {
          logger.error("Error provisioning application certificate", e);
      }
      
      // 4. Load Application certificate
      try {
         loadApplicationCert();
      } catch (Exception e) {
          logger.error("Error loading certificate of self", e);
      }
   }

    public void initScmsProperties() {
       if (this.odeProperties.getScmsCertsDir() != null) {
          Path certsDirPath = Paths.get(this.odeProperties.getScmsCertsDir());
          File certsDir = certsDirPath.toFile();
          if (!certsDir.exists()) {
             certsDir.mkdirs();
          }

          Path enrollmentDirPath;
          if (this.odeProperties.getScmsEnrollmentDir() == null) {
             enrollmentDirPath = certsDirPath.resolve("enrollment");
             this.odeProperties.setScmsEnrollmentDir(enrollmentDirPath.toString());
          } else {
             enrollmentDirPath = Paths.get(this.odeProperties.getScmsEnrollmentDir());
          }
          File enrollmentDir = enrollmentDirPath.toFile();
          if (!enrollmentDir.exists()) {
             enrollmentDir.mkdirs();
          }

          if (this.odeProperties.getScmsCertRevocationListFile() == null) {
             this.odeProperties.setScmsCertRevocationListFile(enrollmentDirPath.resolve("CRL.oer").toString());
          }

          if (this.odeProperties.getScmsLocalCertChainFile() == null) {
             this.odeProperties.setScmsLocalCertChainFile(enrollmentDirPath.resolve("LCCF.oer").toString());
          }

          if (this.odeProperties.getScmsLocalPolicyFile() == null) {
             this.odeProperties.setScmsLocalPolicyFile(enrollmentDirPath.resolve("LPF.oer").toString());
          }

          if (this.odeProperties.getScmsRootCertFile() == null) {
             this.odeProperties.setScmsRootCertFile(enrollmentDirPath.resolve("root.oer").toString());
          }

          if (this.odeProperties.getScmsRootTlsFile() == null) {
             this.odeProperties.setScmsRootTlsFile(enrollmentDirPath.resolve("root.tls.pem").toString());
          }

          //Get the device directory name from enrollment directory
          String deviceDirName = getEnrollmentDeviceDirName(odeProperties.getScmsEnrollmentDir());

          if (deviceDirName != null) {
             Path deviceDir = enrollmentDirPath.resolve(deviceDirName);

             if (this.odeProperties.getScmsEcaCertFile() == null) {
                this.odeProperties.setScmsEcaCertFile(deviceDir.resolve("ECA.oer").toString());
             }
      
             if (this.odeProperties.getScmsEnrollmentCertFile() == null) {
                this.odeProperties.setScmsEnrollmentCertFile(deviceDir.resolve("enrollment.oer").toString());
             }
      
             if (this.odeProperties.getScmsPriKeyReconValueFile() == null) {
                this.odeProperties.setScmsPriKeyReconValueFile(deviceDir.resolve("enrollment.s").toString());
             }
      
             if (this.odeProperties.getScmsRaCertFile() == null) {
                this.odeProperties.setScmsRaCertFile(deviceDir.resolve("RA.oer").toString());
             }
          }
       } else {
          logger.error("ode.scmsCertsDir property not defined");
       }
    }

    private void provisionApplicationCerts() {
      // TODO Auto-generated method stub
      
   }

   
   private String getEnrollmentDeviceDirName(String enrollmentDirName) {
      File enrollmentDir = new File(enrollmentDirName);
      File[] filesList = enrollmentDir.listFiles();
      String deviceDir = null;
      if (filesList != null) {
         for (File file : filesList) {
            if (file.isDirectory()) {
               deviceDir = file.getName();
               break;
            }
         }
      }
      return deviceDir;
   }

   public void loadEnrollmentCerts() throws CertificateException {
      boolean success = true;
      // Load explicit SCMS certificates
      if (odeProperties.getScmsRootCertFile() != null) {
         if (Paths.get(odeProperties.getScmsRootCertFile()).toFile().exists()) {
            if (!FileCertificateStore.load(new CryptoProvider(), CertificateWrapper.getRootPublicCertificateFriendlyName(),
               Paths.get(odeProperties.getScmsRootCertFile()))) {
               success = false;
            }
         } else {
            success = false;
            logger.error("Root CA certificate not found: {}", odeProperties.getScmsRootCertFile());
         }
      } else {
         success = false;
         logger.error("ode.scmsRootCertFile property not defined");
      }

      if (odeProperties.getScmsRaCertFile() != null) {
         if (Paths.get(odeProperties.getScmsRaCertFile()).toFile().exists()) {
            if (!FileCertificateStore.load(new CryptoProvider(), CertificateWrapper.getRaPublicCertificateFriendlyName(),
               Paths.get(odeProperties.getScmsRaCertFile()))) {
               success = false;
            }
         } else {
            success = false;
            logger.error("RA certificate not found: {}", odeProperties.getScmsRaCertFile());
         }
      } else {
         success = false;
         logger.error("ode.scmsRaCertFile property not defined");
      }

      if (odeProperties.getScmsEcaCertFile() != null) {
         if (Paths.get(odeProperties.getScmsEcaCertFile()).toFile().exists()) {
            if (!FileCertificateStore.load(new CryptoProvider(), CertificateWrapper.getEcaPublicCertificateFriendlyName(),
               Paths.get(odeProperties.getScmsEcaCertFile()))) {
               success = false;
            }
         } else {
            success = false;
            logger.error("ECA certificate not found: {}", odeProperties.getScmsEcaCertFile());
         }
      } else {
         success = false;
         logger.error("ode.scmsEcaCertFile property not defined");
      }

      // Load implicit Enrollment certificate
      if (odeProperties.getScmsEnrollmentCertFile() != null && odeProperties.getScmsPriKeyReconValueFile() != null) {
         if (Paths.get(odeProperties.getScmsEcaCertFile()).toFile().exists() &&
               Paths.get(odeProperties.getScmsPriKeyReconValueFile()).toFile().exists()) {
            if (!FileCertificateStore.load(new CryptoProvider(),
               CertificateWrapper.getEnrollmentPublicCertificateFriendlyName(),
               Paths.get(odeProperties.getScmsEnrollmentCertFile()),
               Paths.get(odeProperties.getScmsPriKeyReconValueFile()), 
               seedPrivateKey)) {
               success = false;
            }
         } else {
            success = false;
            logger.error("Enrollment certificate not found: {}", odeProperties.getScmsEnrollmentCertFile());
         }
      } else {
         success = false;
         logger.error("ode.scmsEnrollmentCertFile property not defined");
      }

      if (!success) {
         throw new CertificateException("Error loading all enrollment certificates!");
      }
   }

   public void loadApplicationCert() throws CertificateException {
      boolean success = true;
      // Load implicit Application certificate
      if (odeProperties.getScmsApplicationCertFile() != null) {
         if (Paths.get(odeProperties.getScmsApplicationCertFile()).toFile().exists()) {
            if (!FileCertificateStore.load(new CryptoProvider(), CertificateWrapper.getSelfCertificateFriendlyName(),
               Paths.get(odeProperties.getScmsApplicationCertFile()), Paths.get(odeProperties.getScmsPriKeyReconValueFile()), 
               seedPrivateKey)) {
               success = false;
            }
         } else {
            success = false;
            logger.error("Application certificate not found: ()", odeProperties.getScmsApplicationCertFile());
         }
      } else {
         success = false;
         logger.error("ode.scmsApplicationCertFile property not defined");
      }

      if (!success) {
         throw new CertificateException("Error loading Application certificate!");
      }
   }

   public SecureECPrivateKey getSeedPrivateKey() {
      return seedPrivateKey;
   }

   public void setSeedPrivateKey(SecureECPrivateKey seedPrivateKey) {
      this.seedPrivateKey = seedPrivateKey;
   }

}
