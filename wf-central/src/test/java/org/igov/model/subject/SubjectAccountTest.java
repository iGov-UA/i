package org.igov.model.subject;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/dao-test-context.xml")
public class SubjectAccountTest {

    @Autowired
    private SubjectAccountDao subjectAccountDao;

    @Autowired
    private SubjectAccountTypeDao subjectAccountTypeDao;

    private SubjectAccountType testSubjectAccountType;

    private static final Long test_nID_Server1 = 1L;
    private static final Long test_nID_SubjectHuman1 = 1L;
    private static final String test_sLogin1 = "mylogin1";
    private static final String test_sNote1 = "note string1";

    private static final Long test_nID_Server2 = 2L;
    private static final Long test_nID_SubjectHuman2 = 2L;
    private static final String test_sLogin2 = "mylogin2";
    private static final String test_sNote2 = "note string2";

    @Test
    public void test01SaveAndUpdateSubjectAccount() {
	// Проверили получение типов Аккаунтов
	List<SubjectAccountType> subjectAccountTypes = subjectAccountTypeDao.findAll();
	Assert.notNull(subjectAccountTypes, "Не заданы типы аккаунтов в SubjectAccountType.csv");
	Assert.notEmpty(subjectAccountTypes, "Не заданы типы аккаунтов в SubjectAccountType.csv");

	testSubjectAccountType = subjectAccountTypes.get(0);

	// Проверяем добавление записи
	SubjectAccount subjectAccount1 = new SubjectAccount();
	subjectAccount1.setnID_Server(test_nID_Server1);
	subjectAccount1.setnID_SubjectHuman(test_nID_SubjectHuman1);
	subjectAccount1.setsLogin(test_sLogin1);
	subjectAccount1.setsNote(test_sNote1);
	subjectAccount1.setSubjectAccountType(testSubjectAccountType);
	SubjectAccount subjectAccount2 = subjectAccountDao.saveOrUpdate(subjectAccount1);
	Assert.notNull(subjectAccount2, "Ошибка добавления записи в таблицу SubjectAccount");

	// Проверяем сохранение записи
	subjectAccount1.setnID_Server(test_nID_Server2);
	subjectAccount1.setnID_SubjectHuman(test_nID_SubjectHuman2);
	subjectAccount1.setsLogin(test_sLogin2);
	subjectAccount1.setsNote(test_sNote2);
	subjectAccount1.setSubjectAccountType(testSubjectAccountType);
	SubjectAccount subjectAccount3 = subjectAccountDao.saveOrUpdate(subjectAccount1);
	Assert.notNull(subjectAccount3, "Ошибка сохраниния записи в таблицу SubjectAccount");
    }

    // Попытка записи с идентичными полями: sLogin, nID_SubjectAccountType,
    // nID_Server, nID_SubjectHuman
    // Такая запись недопутима
    @Test(expected = org.hibernate.exception.ConstraintViolationException.class)
    public void test02CheckConstraintViolationExceptionSubjectAccount() {
	SubjectAccount subjectAccount4 = new SubjectAccount();
	subjectAccount4.setnID_Server(test_nID_Server2);
	subjectAccount4.setnID_SubjectHuman(test_nID_SubjectHuman2);
	subjectAccount4.setsLogin(test_sLogin2);
	subjectAccount4.setsNote(test_sNote2);
	subjectAccount4.setSubjectAccountType(testSubjectAccountType);
	subjectAccountDao.saveOrUpdate(subjectAccount4);
	
        fail("Expected exception was missing. Ошибка проверки ограничения на уникальность полей sLogin, nID_SubjectAccountType, nID_Server, nID_SubjectHuman");
    }

}
