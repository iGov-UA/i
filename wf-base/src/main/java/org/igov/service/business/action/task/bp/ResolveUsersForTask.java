/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.service.business.action.task.bp;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author olga
 */
@Component("resolveUsersForTask")
@Service
public class ResolveUsersForTask {

    public List<String> getUsers() {
        List<String> aUser = new ArrayList<>();
        aUser.add("gonzo");
        aUser.add("fozzie");
        return aUser;
    }
}
