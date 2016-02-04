/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.io.fs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.igov.util.ToolFS;
import static org.igov.util.ToolFS.getCheckedPathFileOnReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Belyavtsev Vladimir Vladimirovich (BW)
 */

public class FileSystemData {
    
    private final static Logger LOG = LoggerFactory.getLogger(FileSystemData.class);

    //private static final String BASE_INFO_PATTERN_FILE_PATH = "patterns/services/Info";
    //private static final String BASE_FAQ_PATTERN_FILE_PATH = "patterns/services/FAQ";
    //private static final String BASE_LAW_PATTERN_FILE_PATH = "patterns/services/Law";
    
     //public static final String PATTERN_FILE_PATH_BEGIN = "../webapps/wf/WEB-INF/classes/pattern/";
    //public static final String PATTERN_PRINT_FILE_PATH_BEGIN = "../webapps/wf/WEB-INF/classes/pattern/print";
     //public static final String MARKERS_MOTION_FILE_PATH_BEGIN = "../webapps/wf/WEB-INF/classes/bpmn/markers/motion/";

    private static final String SUB_PATH_PATTERN_FILES = "pattern/";
    private static final String SUB_PATH_PATTERNS_FILES = "patterns/";
    private static final String SUB_PATH_PATTERN_PRINT_FILES = SUB_PATH_PATTERN_FILES + "print/";
    private static final String SUB_PATH_PATTERNS_SERVICE_FILES = SUB_PATH_PATTERNS_FILES + "services/";
    private static final String SUB_PATH_PATTERNS_DICTONARY = SUB_PATH_PATTERNS_FILES + "dictionary/";
    private static final String SUB_PATH_MARKERS_MOTION_FILES = "bpmn/markers/motion/";

    public static BufferedReader getBufferedReader_PatternDictonary(String sSubPathFile) throws UnsupportedEncodingException{
        try{
            BufferedReader oBufferedReader = new BufferedReader(new InputStreamReader(ToolFS.getInputStream(SUB_PATH_PATTERNS_DICTONARY, sSubPathFile), "UTF-8"));
            LOG.debug("Got oBufferedReader from stream (sSubPathFile={})", sSubPathFile);
            return oBufferedReader;
        }catch(Exception oException){
            LOG.error("FAIL: {} (sSubPathFile={})", oException.getMessage(), sSubPathFile);
            throw oException;
        }
    }
    
    public static Collection<File> getFiles_PatternPrint() {
        //File directory = new File(PATTERN_PRINT_FILE_PATH_BEGIN);
        try{
            Path oPath = Paths.get(SUB_PATH_PATTERN_PRINT_FILES);
            File oFileDir = oPath.toFile();
            return FileUtils.listFiles(oFileDir, null, true);
        }catch(Exception oException){
            LOG.warn("FAIL: {})", oException.getMessage());
            throw oException;
        }
    }    
    
    public static byte[] getFileData_Pattern(String sPathFile) throws IOException, URISyntaxException {
        //return getResourcesFile(PATTERN_FILE_PATH_BEGIN, sPathFile);
        try{
            return ToolFS.aFileByte(SUB_PATH_PATTERN_FILES, getCheckedPathFileOnReturn(sPathFile));
        }catch(IOException | URISyntaxException oException){
            LOG.warn("FAIL: {} (sPathFile={})", oException.getMessage(), sPathFile);
            throw oException;
        }
    }
    
    public static byte[] getFileData_MarkersMotionJson(String sPathFile) throws IOException, URISyntaxException {
        //return getResourcesFile(MARKERS_MOTION_FILE_PATH_BEGIN, sPathFile);
        try{
            return ToolFS.aFileByte(SUB_PATH_MARKERS_MOTION_FILES, getCheckedPathFileOnReturn(sPathFile));
        }catch(IOException | URISyntaxException oException){
            LOG.warn("FAIL: {} (sPathFile={})", oException.getMessage(), sPathFile);
            throw oException;
        }
    }
    
    public static String getSmartPathFileContent_ActionItem(String sSubPathFileSmart, String sSubPath, Long nID) {
        //return FileSystemData.getSmartFieldValue(info, BASE_INFO_PATTERN_FILE_PATH, new StringBuilder().append(getId()).append(".html").toString());
        String sContent = null;
        try{
            sContent = ToolFS.getSmartPathFileContent(sSubPathFileSmart
                    , new StringBuilder(SUB_PATH_PATTERNS_SERVICE_FILES).append(getCheckedPathFileOnReturn(sSubPath)).toString()
                    , new StringBuilder().append(nID).append(".html").toString());
        }catch(IOException | URISyntaxException oException){
            LOG.error("Refused: {} (sSubPathFileSmart={},sSubPath={},nID={})", oException.getMessage(), sSubPathFileSmart, sSubPath, nID);
            LOG.trace("FAIL:", oException);
            //throw oException;
        }
        return sContent != null ? sContent : sSubPathFileSmart;
    }

    /*public static byte[] getResourcesFile(String sRootFolder, String sPathFile) throws IOException {
        if (sPathFile.contains("..")) {
            throw new IllegalArgumentException("incorrect sPathFile!");
        }
        String sFullFileName = sRootFolder + sPathFile;
        File file = new File(sFullFileName);
        LOG.info("Loading pattern file: '{}'", sFullFileName);
        return Files.toByteArray(file);
    }*/
    
    /*public static String getSmartFieldValue(String sDefaultValue, String sBasePath, String sFileName) {
        String content = getSmartPathFileContent(sDefaultValue, sBasePath, sFileName);
        return content != null ? content : sDefaultValue;
    }*/

    /*try {
        //osFullPathFile = Paths.get(oURL.toURI());
        return new String(Files.toByteArray(osFullPathFile.toFile()), DEFAULT_ENCODING);
    } catch (IOException e) {
        LOG.error("Cannot read the file: {} (osFullPathFile={})", e.getMessage(), osFullPathFile.toString());
        return null;
    }*/

    /*
    Path osSubPathFile = sSubPathFileSmart.equals("*")
            ? Paths.get(sSubPathBase, sPathFileDefault)
            : Paths.get(sSubPathBase, sSubPathFileSmart);

    String sSubPathFile = osSubPathFile.toString();
    URL oURL = Tool.class.getClassLoader().getResource(sSubPathFile);
    if (oURL == null) {
        LOG.error("Cannot find the file '(sSubPathFile={})'", sSubPathFile);
        return null;
    }

    Path osFullPathFile = null;
    try {
        osFullPathFile = Paths.get(oURL.toURI());
        return new String(Files.toByteArray(osFullPathFile.toFile()), DEFAULT_ENCODING);
    } catch (URISyntaxException | IOException e) {
        LOG.error("Cannot read the file: {} (osFullPathFile={})", e.getMessage(), osFullPathFile);
        return null;
    }*/
    
}
