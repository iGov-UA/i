package org.igov.io.fs;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import static org.igov.io.fs.FileSystemData.getBufferedReader_PatternDictonary;

public class FileSystemDictonary {

    public static final Map<String, Map<String, String>> VALUES = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemDictonary.class);
    
    protected static void loadDictionary(String sSubPathFile) throws ExceptionInInitializerError {
        Map<String, String> values = new HashMap<>();
        try (BufferedReader oBufferedReader = getBufferedReader_PatternDictonary(sSubPathFile)) {

            //LOG.info("Reading dictionary from stream:" + DEFAULT_ROOT_PATH + sSubPathFile);
            String sLine;

            while ((sLine = oBufferedReader.readLine()) != null) {
                String key = StringUtils.substringBefore(sLine, ";");

                values.put(key, sLine);
            }

            VALUES.put(sSubPathFile, values);
            //Close the input stream
            oBufferedReader.close();
        } catch (Exception e) {
            LOG.warn("Error during loading csv file: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public String replaceMVSTagWithValue(String sText) throws Exception {
        try {
            String res = sText;
            int n = 0;
            while (sText.contains("[pattern_dictonary:") && n < 20) {
                n++;
                String pattern = StringUtils.substringBetween(sText, "[pattern_dictonary:", "]");
                LOG.info("Found pattern in the (text={}) ", pattern);
                String[] params = pattern.split(":");
                if (params.length > 2) {
                    LOG.debug("Have to replace pattern with ID:" + params[1] + " and column:" + params[2]);
                    Map<String, String> patternValues = VALUES.get(params[0]);
                    if (patternValues == null) {
                        synchronized (VALUES) {
                            loadDictionary(params[0]);
                            patternValues = VALUES.get(params[0]);
                        }
                    }
                    if (patternValues == null) {
                        LOG.debug("Unable to find dictionary value from the (path={}) ", params[0]);
                        return res;
                    }
                    LOG.debug("Pattern value for the specified (ID={}) ",  patternValues);
                    if (!patternValues.isEmpty()) {
                        String patternValue = patternValues.get(params[1]);
                        String[] patternColumns = patternValue.split(";");
                        String valueToReplace = patternColumns[Integer.valueOf(params[2])
                                - 1];// value in the map starts from second column in csv file
                        LOG.debug("Replacing pattern with the (value={}) ", valueToReplace);
                        res = StringUtils.replace(sText, "[pattern_dictonary:" + pattern + "]", valueToReplace);
                    }
                }
                sText = res;
            }
            return res;
            
        } catch (ExceptionInInitializerError e) {
            LOG.warn("FAIL: {} (sText={})", e.getMessage(), sText);
            throw e;
        } catch (NumberFormatException e) {
            LOG.warn("FAIL: {} (sText={})", e.getMessage(), sText);
            LOG.trace("FAIL:",e);
            throw e;
        } catch (Exception e) {
            LOG.warn("FAIL: {} (sText={})", e.getMessage(), sText);
            LOG.trace("FAIL:",e);
            throw e;
        }
        
    }

}
