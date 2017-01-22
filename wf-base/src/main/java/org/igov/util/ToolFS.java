/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.util;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.URLClassPath;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */
public class ToolFS {
    
    private final static Logger LOG = LoggerFactory.getLogger(ToolFS.class);

    public static String getFileString(File file, String sCodepage) throws IOException {
        byte[] aByte = getFileBytes(file);
        if (aByte == null) {
            return null;
        }
        return new String(aByte, sCodepage == null ? Tool.DEFAULT_ENCODING : sCodepage);
    }

    public static byte[] getFileBytes(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    public static String getCheckedPathFileOnReturn(String sPathFile) throws IOException {
        if (sPathFile.contains("..")) {
            throw new IllegalArgumentException("Incorrect sPathFile!");
        }
        return sPathFile;
    }

    public static InputStream getInputStream(String sSubPath, String sSubPathFile) {
        try {
            InputStream oInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(new StringBuilder(sSubPath).append(sSubPathFile).toString());
            return oInputStream;
        } catch (Exception e) {
            throw e;
        }
    }

    public static Path oFullPathFile(String sSubPath, String... asSubPathFile) throws IOException, URISyntaxException {
        LOG.info("sSubPath: " + sSubPath + " asSubPathFile: " + asSubPathFile);
        Path osSubPathFile = Paths.get(sSubPath, asSubPathFile);
        return oFullPathFile(osSubPathFile);
    }

    public static Path oFullPathFile(Path osSubPathFile) throws IOException, URISyntaxException {
        String sSubPathFile = osSubPathFile.toString();
        LOG.info("sSubPathFile: " + sSubPathFile);
        URL oURL = Tool.class.getClassLoader().getResource(sSubPathFile);
        if (oURL == null) {
            LOG.error("\"oURL == null sSubPathFile: " + sSubPathFile);
            throw new IOException("oURL == null");
        }
        Path osFullPathFile = null;
        try {
            osFullPathFile = Paths.get(oURL.toURI());
            return osFullPathFile;
        } catch (URISyntaxException e) {
            throw e;
        }
    }

    /**
     * Resolves file content based on specified smart file path string and base file path.
     * Examples of the smart paths: "[/custom.html]", "[*]"
     *
     * @param sSubPathFileSmart       A possible smart path string starting from [
     * @param sSubPathBase        Base path to be prepended
     * @param sPathFileDefault If the string equals to "[*]" than this value will be used
     * @return File content. If a passed string was not a smart file path
     * (e.g. it does not start and end with "[" and "]"), then "null" is returned
     * @throws java.io.IOException
     */
    public static String getSmartPathFileContent(String sSubPathFileSmart, String sSubPathBase, String sPathFileDefault) throws IOException, URISyntaxException {
        try {
            if (sSubPathFileSmart == null || sSubPathFileSmart.isEmpty() || !sSubPathFileSmart.startsWith("[") || !sSubPathFileSmart.endsWith("]")) {
                return null;
            }
            sSubPathFileSmart = new StringBuilder(sSubPathFileSmart).deleteCharAt(sSubPathFileSmart.length() - 1).deleteCharAt(0).toString();
            Path osFullPathFile = sSubPathFileSmart.equals("*") ? oFullPathFile(sSubPathBase, sPathFileDefault) : oFullPathFile(sSubPathBase, sSubPathFileSmart);
            return new String(aFileByte(osFullPathFile));
        } catch (IOException oException) {
            throw oException;
        }
    }

    /**
     * возращает расширение файла
     * @param nameFile
     * @return
     */
    public static String getFileExp(String nameFile) {
        final Pattern oPattern = Pattern.compile("^[-a-zA-Z0-9+&#/%?=~:.;\"_*]+$");
        if (nameFile == null || nameFile.trim().isEmpty()) {
            return null;
        }
        Matcher m = oPattern.matcher(nameFile);
        if (m.find()) {
            String exp = null;
            for (String part : m.group(m.groupCount()).split("\\.")) {
                exp = part;
            }
            return exp;
        }
        return null;
    }

    public static byte[] aFileByte(String sSubPath, String... asSubPathFile) throws IOException, URISyntaxException {
        Path osFullPathFile = oFullPathFile(sSubPath, asSubPathFile);
        return aFileByte(osFullPathFile);
    }
    
    public static File getFile(String sSubPath, String... asSubPathFile) throws IOException, URISyntaxException {
        Path osFullPathFile = oFullPathFile(sSubPath, asSubPathFile);
        return aFile(osFullPathFile);
    }

    public static byte[] aFileByte(Path osFullPathFile) throws IOException {
        try {
            return Files.toByteArray(osFullPathFile.toFile());
        } catch (IOException e) {
            throw e;
        }
    }
    
    public static File aFile(Path osFullPathFile) throws IOException {
            return osFullPathFile.toFile();
    }
    
}
