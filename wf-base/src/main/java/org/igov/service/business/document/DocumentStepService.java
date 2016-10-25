package org.igov.service.business.document;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.igov.model.core.GenericEntityDao;
import org.igov.model.document.DocumentStep;
import org.igov.model.document.DocumentStepSubjectRight;
import org.igov.model.document.DocumentStepSubjectRightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentStepService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentStepService.class);

    @Autowired
    @Qualifier("documentStepDao")
    private GenericEntityDao<Long, DocumentStep> documentStepDao;

    @Autowired
    @Qualifier("documentStepSubjectRightDao")
    private GenericEntityDao<Long, DocumentStepSubjectRight> documentStepSubjectRightDao;

    @Autowired
    @Qualifier("documentStepSubjectRightFieldDao")
    private GenericEntityDao<Long, DocumentStepSubjectRightField> documentStepSubjectRightFieldDao;

    public void setDocumentSteps(String snID_Process_Activiti, String soJSON) {
        JSONObject steps = new JSONObject(soJSON);
        List<DocumentStep> resultSteps = new ArrayList<>();
        //process common step if it exists
        Object _step = steps.opt("_");
        LOG.info("Common step is - {}", _step);
        if (_step != null) {
            DocumentStep commonStep = mapToDocumentStep(_step);
            commonStep.setnOrder(0L);//common step with name "_" has order 0
            commonStep.setsKey_Step("_");
            commonStep.setSnID_Process_Activiti(snID_Process_Activiti);
            resultSteps.add(commonStep);
        }
        //process all other steps
        //first of all we filter common step with name "_" and then just convert each step from JSON to POJO
        List<String> stepsNames = Arrays.asList(JSONObject.getNames(steps));
        LOG.info("List of steps: {}", stepsNames);
        stepsNames = stepsNames.stream().
                filter(stepName -> !"_".equals(stepName))
                .collect(Collectors.toList());
        long i = 1L;
        for (String stepName : stepsNames) {
            DocumentStep step = mapToDocumentStep(steps.get(stepName));
            step.setnOrder(i++);
            step.setsKey_Step(stepName);
            step.setSnID_Process_Activiti(snID_Process_Activiti);
            documentStepDao.saveOrUpdate(step);
            resultSteps.add(step);
        }
        LOG.info("Result list of steps: {}", resultSteps);
    }

    private DocumentStep mapToDocumentStep(Object singleStep) {
        JSONObject step = (JSONObject) singleStep;
        LOG.info("try to parse step: {}", step);
        if (step == null) {
            return null;
        }

        DocumentStep resultStep = new DocumentStep();
        String[] groupNames = JSONObject.getNames(step);
        List<DocumentStepSubjectRight> rights = new ArrayList<>();
        for (String groupName : groupNames) {
            JSONObject group = step.optJSONObject(groupName);
            LOG.info("group for step: {}", group);

            if (group == null) {
                continue;
            }
            Boolean bWrite = (Boolean) group.opt("bWrite");
            if (bWrite == null) {
                throw new IllegalArgumentException("Group " + groupName + " hasn't property bWrite.");
            }

            DocumentStepSubjectRight rightForGroup = new DocumentStepSubjectRight();
            rightForGroup.setsKey_GroupPostfix(groupName);
            rightForGroup.setbWrite(bWrite);

            Object sName = group.opt("sName");
            if (sName != null) {
                rightForGroup.setsName((String) sName);
            }

            List<DocumentStepSubjectRightField> fields = mapToFields(group, rightForGroup);
            rightForGroup.setDocumentStepSubjectRightFields(fields);
            rightForGroup.setDocumentStep(resultStep);
            LOG.info("right for step: {}", rightForGroup);
            rights.add(rightForGroup);
        }
        resultStep.setRights(rights);
        return resultStep;
    }

    private List<DocumentStepSubjectRightField> mapToFields(JSONObject group, DocumentStepSubjectRight rightForGroup) {
        List<DocumentStepSubjectRightField> resultFields = new ArrayList<>();
        String[] fieldNames = JSONObject.getNames(group);
        LOG.info("fields for right: {}", Arrays.toString(fieldNames));
        for (String fieldName : fieldNames) {
            if (fieldName == null || fieldName.equals("bWrite")) {
                continue;
            }
            if (fieldName.contains("Read")) {
                JSONArray masks = group.optJSONArray(fieldName);
                LOG.info("Read branch for masks: {}", masks);
                for (int i = 0; masks.length() > i; i++) {
                    String mask = masks.getString(i);
                    DocumentStepSubjectRightField field = new DocumentStepSubjectRightField();
                    field.setsMask_FieldID(mask);
                    field.setbWrite(false);
                    field.setDocumentStepSubjectRight(rightForGroup);
                    resultFields.add(field);
                }
            }
            if (fieldName.contains("Write")) {
                JSONArray masks = group.getJSONArray(fieldName);
                LOG.info("Write branch for masks: {}", masks);
                for (int i = 0; masks.length() > i; i++) {
                    String mask = masks.getString(i);
                    DocumentStepSubjectRightField field = new DocumentStepSubjectRightField();
                    field.setsMask_FieldID(mask);
                    field.setbWrite(true);
                    field.setDocumentStepSubjectRight(rightForGroup);
                    resultFields.add(field);
                }
            }
        }

        return resultFields;
    }

}
