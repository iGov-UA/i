package org.igov.service.controller;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import io.swagger.annotations.Api;

@Controller
@Api(tags = { "ActionIdentityCommonController" })
@RequestMapping(value = "/action/identity")
public class ActionIdentityCommonController {

	private static final Logger log = LoggerFactory.getLogger(ActionIdentityCommonController.class);

	@Autowired
	private IdentityService identityService;

	@RequestMapping(value = "/setUser", method = { RequestMethod.GET })
	@ResponseBody
	public void setUser(@RequestParam(value = "sLogin", required = true) String sLogin,
			@RequestParam(value = "sPassword", required = true) String sPassword,
			@RequestParam(value = "sName", required = true) String sName,
			@RequestParam(value = "sDescription", required = true) String sDescription,
			@RequestParam(value = "sEmail", required = false) String sEmail) throws Exception {

		if (identityService.createUserQuery().userId(sLogin).count() == 0) {
			User user = identityService.newUser(sLogin);
			user.setPassword(sPassword);
			user.setFirstName(sName);
			user.setLastName(sDescription);
			if (sEmail != null) {
				user.setEmail(sEmail);
			}
			identityService.saveUser(user);
		}
	}

	@RequestMapping(value = "/setGroup", method = { RequestMethod.GET })
	@ResponseBody
	public void setGroup(@RequestParam(value = "sID", required = true) String sID,
			@RequestParam(value = "sName", required = true) String sName) throws Exception {

		if (identityService.createGroupQuery().groupId(sID).count() == 0) {
			Group group = identityService.newGroup(sID);
			group.setName(sName);
			identityService.saveGroup(group);
		}
	}

	@RequestMapping(value = "/getGroups", method = RequestMethod.GET)
	public @ResponseBody List<Group> getGroups(@RequestParam(value = "sLogin", required = false) String sLogin) {
		if (sLogin != null) {
			return identityService.createGroupQuery().groupMember(sLogin).list();
		} else {
			return identityService.createGroupQuery().list();
		}
	}

	@RequestMapping(value = "/getUsers", method = RequestMethod.GET)
	public @ResponseBody List<User> getUsers(@RequestParam(value = "sID_Group", required = true) String sID_Group){
				 return identityService.createUserQuery().memberOfGroup(sID_Group).list();
	}

	@RequestMapping(value = "/removeUser", method = RequestMethod.DELETE)
	public @ResponseBody void removeUser(@RequestParam(value = "sLogin", required = false) String sLogin)
			throws Exception {
		identityService.deleteUser(sLogin);
	}

	@RequestMapping(value = "/removeGroup", method = RequestMethod.DELETE)
	public @ResponseBody void removeGroup(@RequestParam(value = "sID", required = false) String sID) {
		identityService.deleteGroup(sID);
	}

}