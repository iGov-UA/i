package org.igov.model.subject;

import org.igov.model.subject.organ.SubjectOrgan;
import org.igov.model.subject.organ.SubjectOrganDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: lyashenkoGS
 * Date: 02.04.2016
 * Time: 23:35
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/dao-test-context.xml")
public class SubjectOrganDaoTest {

    @Autowired
    private SubjectOrganDao subjectOrganDao;

    @Test
    public void testGetSubjectHumanBySubjectId() {
        final Long subjectId=24l;
        Subject subject = new Subject();
        subject.setId(subjectId);
        SubjectOrgan subjectOrgan=subjectOrganDao.getSubjectOrgan(subject);
        Assert.assertEquals(subjectOrgan.getoSubject().getId(),subjectId);
    }

}
