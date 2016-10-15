package org.igov.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;

public final class Tool {

    private final static Logger LOG = LoggerFactory.getLogger(Tool.class);

    public static final String DEFAULT_ENCODING = "UTF-8";
    private static final String sa = new String("����������������������������������");
    private static final String[] as = { "a", "b", "v", "g", "d", "e", "yo", "g", "z", "i", "y", "i",
            "k", "l", "m", "n", "o", "p", "r", "s", "t", "u",
            "f", "h", "tz", "ch", "sh", "sh", "'", "e", "yu", "ya", "ji", "i", "e" };
    
    public static String sCut(int nSize, String s){
        if(s!=null){
            if(s.length()>nSize){
                return new StringBuilder(s.substring(0, nSize)).append("...(+").append(s.length()-nSize).append("").append(")").toString();
            }else{
                return s;
            }
        }else{
            return null;
        }
    }
    
    /**
     * @param oS обьект со строкой
     * @return Сьрока
     */
    public static String sO(Object oS) {
        return (oS == null) ? "" : oS.toString();
    }

    
    public static String sData(byte[] a) {
        String s = "Not convertable!";
        try {
            s = new String(a, DEFAULT_ENCODING);
        } catch (Exception oException) {
            LOG.error("Error: {}", oException.getMessage());
            LOG.trace("FAIL:", oException);
        }
        return s;
    }

    public static byte[] aData(String s) {
        //log.info("[aData]:s=" + s);
        byte[] a = s.getBytes(Charset.forName(DEFAULT_ENCODING));
        //log.info("[aData]:a.length=" + a.length + ",Arrays.toString(a)=" + Arrays.toString(a));
        return a;
    }

    
    /**
     * Converts an object to a serialized byte array.
     *
     * @param obj Object to be converted.
     * @return byte[] Serialized array representing the object.
     */
    public static byte[] getByteArrayFromObject(Object obj) {
        byte[] result = null;

        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ObjectOutputStream oos = new JBossObjectOutputStream(baos)) {
            oos.writeObject(obj);
            oos.flush();
            result = baos.toByteArray();
        } catch (IOException ioEx) {
            LOG.error("Error: {}, during converting object to byteArray", ioEx.getMessage());
            LOG.trace("FAIL:", ioEx);
        }

        return result;
    }

    /**
     * Utility method to un-serialize objects from byte arrays.
     *
     * @param bytes The input byte array.
     * @return The output object.
     */
    public static Object getObjectFromByteArray(byte[] bytes) {
        Object result = null;

        try (final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                final ObjectInputStream ois = new JBossObjectInputStream(bais)) {
            result = ois.readObject();
        } catch (IOException ioEx) {
            LOG.error("Error: {}. Unable to deserialize object from byte array.", ioEx.getMessage());
            LOG.trace("FAIL:", ioEx);
        } catch (ClassNotFoundException cnfEx) {
            LOG.error("Error: {}. No corresponding class for byte array.", cnfEx.getMessage());
            LOG.trace("FAIL:", cnfEx);
        }

        return result;
    }    
    

    //return true if all args are null
    public static boolean bNullArgsAll(Object... args) {
        boolean bReturn = true;
        for (Object o : args) {
            if (o != null) {
                bReturn = false;
                break;
            }
        }
        return bReturn;
    }

    public static boolean bFoundText(String sWhere, String sFind) {
        return sWhere.toLowerCase().contains(sFind.toLowerCase());
    }

    
    public static String getGeneratedToken() {
        return RandomStringUtils.random(20, true, true);
    }

    /**
     * @return random 4xDigits answer code
     */
    public static String getGeneratedPIN() {
        return String.format("%04d", new Random().nextInt(10000));
    }
 


    public static String sTextTranslit(String sOld) {
        StringBuilder sNew = new StringBuilder();
        char[] aChar = sOld.toLowerCase().toCharArray();
        for (int i = 0; i < aChar.length; i++) {
            int nAt = sa.indexOf(aChar[i]);
            sNew.append(nAt != -1 ? as[nAt] : aChar[i]);
        }
        return sNew.toString();
    }    
    
}
