/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.io.fs;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.igov.util.Util;
import static org.igov.util.Util.DEFAULT_ENCODING;
import static org.igov.util.Util.getResourcesFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */

public class FileSystemData {
    
    private final static Logger LOG = LoggerFactory.getLogger(FileSystemData.class);
    
    public static Collection<File> getPatternFiles() {
        File directory = new File("../webapps/wf/WEB-INF/classes/pattern/print");
        return FileUtils.listFiles(directory, null, true);
    }    
    
    public static final String PATTERN_FILE_PATH_BEGIN = "../webapps/wf/WEB-INF/classes/pattern/";
    public static final String MARKERS_MOTION_FILE_PATH_BEGIN = "../webapps/wf/WEB-INF/classes/bpmn/markers/motion/";
    
    public static byte[] getPatternFileData(String sPathFile) throws IOException {
        return getResourcesFile(PATTERN_FILE_PATH_BEGIN, sPathFile);
    }
    
    public static byte[] getMarkersMotionJson(String sPathFile) throws IOException {
        return getResourcesFile(MARKERS_MOTION_FILE_PATH_BEGIN, sPathFile);
    }
    
    public static String getSmartFieldValue(String sDefaultValue, String sBasePath, String sFileName) {
        String content = getSmartPathFileContent(sDefaultValue, sBasePath, sFileName);
        return content != null ? content : sDefaultValue;
    }
    
    /**
     * Resolves file content based on specified smart file path string and base file path.
     * Examples of the smart paths: "[/custom.html]", "[*]"
     *
     * @param smartPath       A possible smart path string starting from [
     * @param sBasePath        Base path to be prepended
     * @param defaultFilePath If the string equals to "[*]" than this value will be used
     * @return File content. If a passed string was not a smart file path
     * (e.g. it does not start and end with "[" and "]"), then "null" is returned
     */
    public static String getSmartPathFileContent(String smartPath, String sBasePath, String defaultFilePath) {
        if (smartPath == null || smartPath.isEmpty() || !smartPath.startsWith("[") || !smartPath.endsWith("]")) {
            return null;
        }

        smartPath = new StringBuilder(smartPath)
                .deleteCharAt(smartPath.length() - 1)
                .deleteCharAt(0)
                .toString();

        Path sPath = smartPath.equals("*")
                ? Paths.get(sBasePath, defaultFilePath)
                : Paths.get(sBasePath, smartPath);

        String pathString = sPath.toString();
        URL resource = Util.class.getClassLoader().getResource(pathString);
        if (resource == null) {
            LOG.error("Cannot find the file '{}'", sPath);
            return null;
        }

        try {
            sPath = Paths.get(resource.toURI());
            return new String(Files.toByteArray(sPath.toFile()), DEFAULT_ENCODING);
        } catch (URISyntaxException | IOException e) {
            LOG.error("Cannot read the file: {} (sPath={})", e.getMessage(), sPath);
            return null;
        }
    }    
    
}
