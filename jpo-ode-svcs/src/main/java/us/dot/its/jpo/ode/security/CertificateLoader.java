/*******************************************************************************
 * Copyright 2018 572682
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package us.dot.its.jpo.ode.security;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO open-ode
//import gov.usdot.cv.security.cert.CertificateException;
//import gov.usdot.cv.security.cert.CertificateManager;
//import gov.usdot.cv.security.cert.CertificateWrapper;
//import gov.usdot.cv.security.cert.FileCertificateStore;
//import gov.usdot.cv.security.crypto.CryptoException;
//import gov.usdot.cv.security.crypto.CryptoProvider;
//import gov.usdot.cv.security.msg.IEEE1609p2Message;
import us.dot.its.jpo.ode.OdeProperties;

public class CertificateLoader implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(CertificateLoader.class);

    private OdeProperties odeProperties;

    
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
                        // TODO open-ode
//                            loadCert(new CryptoProvider(), certFileName, certFile);
                        } catch (Exception e) {
                            logger.error("Error loading certificate: " + certFile, e);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error loading certificates in: " + subdir, e);
                }
                count++;
            }
            logger.debug("Loaded {} certificates from location: {}", count, odeProperties.getCaCertPath());
        } catch (Exception e) {
            logger.error("Error loading certifcate files.", e);
        }
    }

 // TODO open-ode
//    public boolean loadCert(CryptoProvider cryptoProvider, 
//                                   String name, 
//                                   Path certFilePath)
//                                           throws CertificateException, 
//                                           IOException, 
//                                           DecoderException, 
//                                           CryptoException, 
//                                           DecodeFailedException, 
//                                           DecodeNotSupportedException, 
//                                           EncodeFailedException, 
//                                           EncodeNotSupportedException {
//        return loadCert(cryptoProvider, name, certFilePath, null, null);
//    }
//    
//    public boolean loadCert(CryptoProvider cryptoProvider, 
//                                   String name, 
//                                   Path certFilePath,
//                                   Path privateKeyReconFilePath, 
//                                   Path seedPrivateKeyFilePath)
//                                           throws CertificateException, 
//                                           IOException, 
//                                           DecoderException, 
//                                           CryptoException, 
//                                           DecodeFailedException, 
//                                           DecodeNotSupportedException, 
//                                           EncodeFailedException, 
//                                           EncodeNotSupportedException {
//        
//        //load public cert
//        return FileCertificateStore.load(new CryptoProvider(), name, certFilePath, privateKeyReconFilePath, seedPrivateKeyFilePath);
//   }
//    
//
//    private boolean loadSelfFullCerts() throws DecodeFailedException, EncodeFailedException, CertificateException, IOException, DecoderException, CryptoException, DecodeNotSupportedException, EncodeNotSupportedException {
//        return loadCert(
//                new CryptoProvider(), 
//                IEEE1609p2Message.getSelfCertificateFriendlyName(), 
//                Paths.get(odeProperties.getSelfCertPath()), 
//                Paths.get(odeProperties.getSelfPrivateKeyReconstructionFilePath()), 
//                Paths.get(odeProperties.getSelfSigningPrivateKeyFilePath()));
//    }

    @Override
    public void run() {

        logger.info(this.getClass().getSimpleName() + " initiated.");

        
     // TODO open-ode
//        // 0. Begin with an empty store
//        CertificateManager.clear();
//
//        if (StringUtils.isNotEmpty(odeProperties.getCaCertPath()) &&
//            StringUtils.isNotEmpty(odeProperties.getSelfCertPath())) {
//            // 1. Begin by loading CA cert to the store
//            try {
//                loadCert(
//                        new CryptoProvider(), 
//                        CertificateWrapper.getRootPublicCertificateFriendlyName(), 
//                        Paths.get(odeProperties.getCaCertPath()));
//            } catch (Exception e) {
//                logger.error("Error loading CA certificate", e);
//            }
//    
//            // 2. Load full certificates of self to the store
//            try {
//                loadSelfFullCerts();
//            } catch (Exception e) {
//                logger.error("Error loading full certificate of self", e);
//            }
//        }
    }

}
