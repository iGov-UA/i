/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.subject;

import java.util.List;
import org.igov.model.subject.SubjectGroup;
import org.springframework.stereotype.Service;

/**
 *
 * @author olga
 */
@Service
public class SubjectGroupService {

    public List<SubjectGroup> getSubjectGroups(String sID_Group_Activiti, Integer nDeepLevel) {
        if(nDeepLevel == null || nDeepLevel == 0){
            nDeepLevel = 1000;
        }
        
        //получить по группе сабджектгрупп и по нему получ
        //если nDeepLevel ноль или нал, то делаем его равного 1000
        //из перентов получаем список детей. идем в цикле по детям и получаем список детей пока не получим ситуацию, когда ребенок не имеет родителя
        return null;
    }

}
