package org.igov.util;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ToolWeb {
    
    private final static Logger LOG = LoggerFactory.getLogger(ToolWeb.class);

    public static String getSignature(String sBase64EncodedData, String sPrivateKey) {
        return base64_encode(aByte_SHA1(sPrivateKey + sBase64EncodedData + sPrivateKey));
    }
    
    public static byte[] aByte_SHA1(String param) {
        try {
            MessageDigest SHA = MessageDigest.getInstance("SHA-1");
            SHA.reset();
            SHA.update(param.getBytes("UTF-8"));
            return SHA.digest();
        } catch (Exception e) {
            throw new RuntimeException("Can't calc SHA-1 hash", e);
        }
    }

    public static String base64_encode(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    public static String base64_encode(String data) {
        return base64_encode(data.getBytes());
    }

    public static byte[] contentStringToByte(String contentString) {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] contentByte;
        try {
            contentByte = decoder.decodeBuffer(contentString);
        } catch (Exception oException) {
            LOG.error("{}", oException.getMessage(), oException);
            LOG.trace("FAIL: ", oException);
            contentByte = new byte[1];
        }
        return contentByte;
    }

    public static String deleteContextFromURL(String sURL) {
        String temp = sURL.substring(sURL.indexOf("//") + 2);
        return temp.substring(temp.indexOf("/"));
    }

    public static String contentByteToString(byte[] contentByte) {
        BASE64Encoder encoder = new BASE64Encoder();
        String contentString = encoder.encode(contentByte);
        return contentString;
    }
}
