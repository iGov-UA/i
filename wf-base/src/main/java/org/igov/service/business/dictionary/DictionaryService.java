package org.igov.service.business.dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.igov.util.ToolFS;
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
@Component("dictionaryService")
@Service
public class DictionaryService {
    
    private static final Logger LOG = LoggerFactory.getLogger(DictionaryService.class);
    
    public List<Map<String, String>> processDictionary(String sPath, String sID_Field, String sValue) {

        List<Map<String, String>> aResult = new ArrayList<>();

        try {
            BufferedReader oBufferedReader
                    = new BufferedReader(new InputStreamReader(
                            ToolFS.getInputStream("patterns/dictionary/", sPath + ".csv"), "UTF-8"));

            String sLine;
            String sLineColums = oBufferedReader.readLine();
            LOG.info("sLineColums is {}", sLineColums);
            String[] columns = sLineColums.split("\\;");
            int columnNumber = -1;

            for (int i = 0; i < columns.length; i++) {
                if (columns[i].equals(sID_Field)) {
                    columnNumber = i;
                    break;
                }
            }

            if (columnNumber == -1) {
                throw new RuntimeException("There is no column with name " + sID_Field + " in " + sPath + ".csv");
            }

            while ((sLine = oBufferedReader.readLine()) != null) {
                //String key = StringUtils.substringBefore(sLine, ";");

                //if(key.equals(sValue)){
                String[] columnsValues = sLine.split("\\;");
                for (int i = 0; i < columnsValues.length; i++) {
                    if (columnsValues[i].equals(sValue) && i == columnNumber) {
                        Map<String, String> values = new HashMap<>();
                        for (int j = 0; j < columnsValues.length; j++) {
                            values.put(columns[j], columnsValues[j]);
                        }
                        aResult.add(values);
                        break;
                    }
                }
            }

            oBufferedReader.close();
        } catch (IOException e) {
            LOG.warn("Error during loading csv file: {}", e.getMessage());
            LOG.trace("FAIL:", e);
        }

        return aResult;
    }
    
}
