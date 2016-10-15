package org.igov.service.business.action.task.systemtask.misc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;

@Component("markerService")
public class MarkerService {

    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    public static final String MARKERS_DIR = "bpmn/markers/motion/";
    @Autowired
    private ApplicationContext appContext;

    public String loadFromFile(String fileName) {
        Resource resource = getResource(fileName);
        try {
            File file = resource.getFile();
            String result = Files.toString(file, DEFAULT_ENCODING);
            return result;
        }
        catch (IOException e) {
            throw new ActivitiIllegalArgumentException("Could not read file " + fileName, e);
        }
    }

    private Resource getResource(String fileName) {
        if (fileName.contains("..")) {
            throw new ActivitiIllegalArgumentException("Incorrect fileName!");
        }
        Resource resource = appContext.getResource("classpath:" + MARKERS_DIR + fileName);

        return resource;

    }
}
