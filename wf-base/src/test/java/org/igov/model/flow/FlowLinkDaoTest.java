package org.igov.model.flow;

import org.igov.service.controller.IntegrationTestsApplicationConfiguration;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
@Ignore
public class FlowLinkDaoTest {
	@Autowired
	private FlowLinkDao flowLinkDao;

	@Test
	public void findFlowLinksByServiceDataTest() {
		Long nID_Service = 26l;
		Long nID_SubjectOrganDepartment = null;
		FlowLink flowLink = flowLinkDao.findLinkByService(nID_Service, nID_SubjectOrganDepartment);
		Assert.assertTrue(flowLink.getId() == 6l);

		nID_SubjectOrganDepartment = 23l;
		flowLink = flowLinkDao.findLinkByService(nID_Service, nID_SubjectOrganDepartment);
		Assert.assertTrue(flowLink.getId() == 18l);

		nID_SubjectOrganDepartment = -1l;
		flowLink = flowLinkDao.findLinkByService(nID_Service, nID_SubjectOrganDepartment);
		Assert.assertNull(flowLink);

	}
}
