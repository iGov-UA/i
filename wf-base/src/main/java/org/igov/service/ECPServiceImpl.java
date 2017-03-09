package org.igov.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import ua.privatbank.cryptonite.CryptoniteException;
import ua.privatbank.cryptonite.CryptoniteX;
import ua.privatbank.cryptonite.KeyStore;

@Service
public class ECPServiceImpl implements ECPService {

    private static final String TSP_URL = "http://acsk.privatbank.ua/services/tsp";

	private final static Logger LOG = LoggerFactory.getLogger(ECPServiceImpl.class);

    @Autowired
    GeneralConfig generalConfig;
    
    @Autowired
    private Environment environment;
    
	@Override
	public byte[] signFile(byte[] content) throws Exception {
		LOG.info("Creating KeyStore with parameters. FileName:" + (environment.getProperty("catalina.home") + "/conf/" + generalConfig.getsECPKeystoreFilename()) + " Passwd:"
				+ generalConfig.getsECPKeystorePasswd());

		File keyFile = new File(environment.getProperty("catalina.home") + "/conf/" + generalConfig.getsECPKeystoreFilename());
		File certFile = new File(environment.getProperty("catalina.home") + "/conf/" + generalConfig.getsECPSelfCertPathFile());


		if (keyFile.exists() && certFile.exists()) {
			LOG.info("Creating KeyStore with parameters. keystore path:"
					+ keyFile.getPath());
			final KeyStore keyStore = new KeyStore(keyFile.getPath(), generalConfig.getsECPKeystorePasswd());
			
			final Path pathCert = Paths.get(certFile.getPath());
			final byte[] dataCert = Files.readAllBytes(pathCert);
			
			LOG.info("Signing the document. Size of original document:"
					+ (content != null ? content.length : "0"));
			final byte[] signedDoc = CryptoniteX.cmsSignHash(keyStore, content, dataCert, true, null, true);

			LOG.info("Signed the document. Size of signed document:"
					+ (signedDoc != null ? signedDoc.length : "0"));

			return signedDoc;
		} else {
			LOG.info("Either KeyStore file or certificate sign file are not found in classpath. Paths:" + keyFile.getAbsolutePath() + " " + certFile.getAbsolutePath());
		}
		return null;
	}

}
