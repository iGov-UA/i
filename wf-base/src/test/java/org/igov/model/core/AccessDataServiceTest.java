/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.igov.model.core;

import org.igov.service.business.access.AccessDataService;
import org.igov.service.business.access.AccessDataService;
import org.igov.service.controller.IntegrationTestsApplicationConfiguration;
import org.igov.util.ToolWeb;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.igov.util.Tool;

/**
 * @author olya
 */
@Ignore
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationTestsApplicationConfiguration.class)
public class AccessDataServiceTest {

    @Autowired
    private AccessDataService accessDataDao;

    @Test //@Ignore
    public void workWithAccessData() {
        byte[] content = new byte[] { 1, 2, 3 };
        String contentString = ToolWeb.contentByteToString(content);
        String key = accessDataDao.setAccessData(contentString);
        Assert.assertNotNull(key);
        String contentReturn = accessDataDao.getAccessData(key);
        Assert.assertEquals(contentString, contentReturn);
        Assert.assertTrue(accessDataDao.removeAccessData(key));
    }
}
