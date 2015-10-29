package org.activiti.rest.controller;

import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wf.dp.dniprorada.base.util.JsonRestUtils;
import org.wf.dp.dniprorada.dao.ServerDao;
import org.wf.dp.dniprorada.model.Server;
import org.wf.dp.dniprorada.model.Service;

/**
 * User: goodg_000
 * Date: 29.10.2015
 * Time: 22:07
 */
@Controller
@RequestMapping(value = "/server")
public class ActivitiRestServerController {

    @Autowired
    private ServerDao serverDao;

    @RequestMapping(value = "/getServer", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity getService(@RequestParam(value = "nID") Long nID) throws RecordNotFoundException {
        Optional<Server> serverOpt = serverDao.findById(nID);
        if (!serverOpt.isPresent()) {
            throw new RecordNotFoundException();
        }

        return JsonRestUtils.toJsonResponse(serverOpt.get());
    }
}
