package org.igov.service.buisness.dictionary;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import static org.igov.io.fs.FileSystemData.getBufferedReader_PatternDictonary;
import static org.igov.io.fs.FileSystemDictonary.VALUES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kovilin
 */
@Component("documentStepService")
@Service
public class DictionaryService {
    
    private static final Logger LOG = LoggerFactory.getLogger(DictionaryService.class);
    
    public Map<String, String> processDictionary(String sPath, String sID_Field, String sValue){
        Map<String, String> values = new HashMap<>();
        
        try (BufferedReader oBufferedReader = 
                getBufferedReader_PatternDictonary(sPath + ".csv")) {

            String sLine;
            while ((sLine = oBufferedReader.readLine()) != null) {
                String key = StringUtils.substringBefore(sLine, ";");

                values.put(key, sLine);
            }

            oBufferedReader.close();
        } catch (Exception e) {
            LOG.warn("Error during loading csv file: {}", e.getMessage());
            LOG.trace("FAIL:", e);
            throw new ExceptionInInitializerError(e);
        }
        
        return values;
    }
    
}
