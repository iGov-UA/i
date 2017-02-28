package org.igov.service;

import java.io.File;

import org.igov.io.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import ua.privatbank.cryptonite.CryptoniteException;
import ua.privatbank.cryptonite.CryptoniteX;

@Service
public class ECPServiceImpl implements ECPService {

    private final static Logger LOG = LoggerFactory.getLogger(ECPServiceImpl.class);

    @Autowired
    GeneralConfig generalConfig;
    
    @Autowired
    private Environment environment;
    
	@Override
	public byte[] signFile(byte[] content) throws CryptoniteException {
		LOG.info("Creating KeyStore with parameters. FileName:" + (environment.getProperty("catalina.home") + "/conf/" + generalConfig.getsECPKeystoreFilename()) + " Passwd:"
				+ generalConfig.getsECPKeystorePasswd());

		File storePath = new File(environment.getProperty("catalina.home") + "/conf/" + generalConfig.getsECPKeystoreFilename());

		if (storePath.exists()) {
			LOG.info("Creating KeyStore with parameters. keystore path:"
					+ storePath.getPath());
			ua.privatbank.cryptonite.KeyStore ks = new ua.privatbank.cryptonite.KeyStore(
					storePath.getPath(),
					generalConfig.getsECPKeystorePasswd());
			LOG.info("Created KeyStore");

			LOG.info("Signing the document. Size of original document:"
					+ (content != null ? content.length : "0"));
			byte[] signedDoc = CryptoniteX.signHash(ks, content);
			LOG.info("Signed the document. Size of signed document:"
					+ (signedDoc != null ? signedDoc.length : "0"));

			return signedDoc;
		} else {
			LOG.info("KeyStore file has not found in classpath");
		}
		return null;
	}

}
