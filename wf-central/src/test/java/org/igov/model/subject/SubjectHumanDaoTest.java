package org.igov.model.subject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import org.junit.Ignore;

/**
 * User: goodg_000
 * Date: 27.12.2015
 * Time: 18:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/dao-test-context.xml")
public class SubjectHumanDaoTest {

    @Autowired
    private SubjectHumanDao subjectHumanDao;

    @Autowired
    private SubjectContactDao subjectContactDao;


    @Test
    public void testSetSubjectHumanWithEmail() {
        testSetSubjectHuman(SubjectHumanIdType.Email, "a@b.com", 1);
    }

    @Test
    public void testSetSubjectHumanWithPhone() {
        testSetSubjectHuman(SubjectHumanIdType.Phone, "22-22-22", 1);
    }

    @Test
    public void testSetSubjectHumanWithINN() {
        testSetSubjectHuman(SubjectHumanIdType.INN, "123", 0);
    }

    @Test
    public void testSetSubjectHumanWithPassport() {
        testSetSubjectHuman(SubjectHumanIdType.Passport, "MB123456", 0);
    }

    private void testSetSubjectHuman(SubjectHumanIdType subjectHumanIdType, String code, int expectedContactsCount) {
        Assert.assertNull(subjectHumanDao.getSubjectHuman(subjectHumanIdType, code));

        subjectHumanDao.saveSubjectHuman(subjectHumanIdType, code);

        SubjectHuman subjectHuman = subjectHumanDao.getSubjectHuman(subjectHumanIdType, code);
        Assert.assertNotNull(subjectHuman);
        Assert.assertEquals(subjectHumanIdType, subjectHuman.getSubjectHumanIdType());

        final List<SubjectContact> contacts = subjectContactDao.findContacts(subjectHuman.getoSubject());
        Assert.assertEquals(expectedContactsCount, contacts.size());
        for (SubjectContact contact : contacts) {
            Assert.assertEquals(subjectHumanIdType.name(), contact.getSubjectContactType().getsName_EN());
        }
    }
}
