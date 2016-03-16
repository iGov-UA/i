package org.igov.model.subject;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: kr110666kai
 * 
 * Тест для проверки работы с сущностями SubjectAccount и SubjectAccountType
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/dao-test-context.xml")
public class SubjectAccountTest {

    @Autowired
    private SubjectAccountDao subjectAccountDao;

    @Autowired
    private SubjectAccountTypeDao subjectAccountTypeDao;

    private static final Long test_nID_Server1 = 1L;
    private static final Long test_nID_Subject1 = 11L;
    private static final String test_sLogin1 = "mylogin_1";
    private static final String test_sNote1 = "note string_1";

    private static final Long test_nID_Server2 = 2L;
    private static final Long test_nID_Subject2 = 22L;
    private static final String test_sLogin2 = "mylogin_222";
    private static final String test_sNote2 = "note string_222";

    private static final Long test_nID_Server3 = 3L;
    private static final Long test_nID_Subject3 = 8L;
    private static final String test_sLogin3 = "mylogin_333";
    private static final String test_sNote3 = "note string_333";

    private static final Long test_nID_SubjectError = 777777L;

    private static final Long test_nID_SubjectAccountType1 = 1L;
    private static final Long test_nID_SubjectAccountType2 = 2L;

    @Test
    public void testSaveAndUpdateSubjectAccount() {
	// Проверили получение типов Аккаунтов
	List<SubjectAccountType> subjectAccountTypes = subjectAccountTypeDao.findAll();
	Assert.assertNotNull("Не заданы типы аккаунтов в SubjectAccountType.csv", subjectAccountTypes);

	SubjectAccountType testSubjectAccountType1 = subjectAccountTypeDao
		.findByIdExpected(test_nID_SubjectAccountType1);

	// Проверяем добавление записи
	SubjectAccount subjectAccount = new SubjectAccount();
	subjectAccount.setnID_Server(test_nID_Server1);
	subjectAccount.setnID_Subject(test_nID_Subject1);
	subjectAccount.setsLogin(test_sLogin1);
	subjectAccount.setsNote(test_sNote1);
	subjectAccount.setSubjectAccountType(testSubjectAccountType1);
	saveAndUpdateSubjectAccount(subjectAccount, " Добавление новой записи");

	SubjectAccountType testSubjectAccountType2 = subjectAccountTypeDao
		.findByIdExpected(test_nID_SubjectAccountType2);

	// Проверяем сохранение записи:
	subjectAccount.setnID_Server(test_nID_Server2);
	subjectAccount.setnID_Subject(test_nID_Subject2);
	subjectAccount.setsLogin(test_sLogin2);
	subjectAccount.setsNote(test_sNote2);
	subjectAccount.setSubjectAccountType(testSubjectAccountType2);
	saveAndUpdateSubjectAccount(subjectAccount, " Добавление новой записи");

	// Проверяем добавление новой записи, изменен только логин
	subjectAccount = new SubjectAccount();
	subjectAccount.setnID_Server(test_nID_Server2);
	subjectAccount.setnID_Subject(test_nID_Subject2);
	subjectAccount.setsLogin(test_sLogin1);
	subjectAccount.setsNote(test_sNote2);
	subjectAccount.setSubjectAccountType(testSubjectAccountType2);
	saveAndUpdateSubjectAccount(subjectAccount, " Добавление новой записи, изменен только логин");

	// Проверяем добавление новой записи, изменен только тип логина
	subjectAccount = new SubjectAccount();
	subjectAccount.setnID_Server(test_nID_Server2);
	subjectAccount.setnID_Subject(test_nID_Subject2);
	subjectAccount.setsLogin(test_sLogin2);
	subjectAccount.setsNote(test_sNote2);
	subjectAccount.setSubjectAccountType(testSubjectAccountType1);
	saveAndUpdateSubjectAccount(subjectAccount, " Добавление новой записи, изменен только тип логина");

	// Проверяем добавление новой записи, изменен только id-сервера
	subjectAccount = new SubjectAccount();
	subjectAccount.setnID_Server(test_nID_Server1);
	subjectAccount.setnID_Subject(test_nID_Subject2);
	subjectAccount.setsLogin(test_sLogin2);
	subjectAccount.setsNote(test_sNote2);
	subjectAccount.setSubjectAccountType(testSubjectAccountType2);
	saveAndUpdateSubjectAccount(subjectAccount, " Добавление новой записи, изменен только id-сервера");

	// Проверяем добавление новой записи, изменен только id-subject
	subjectAccount = new SubjectAccount();
	subjectAccount.setnID_Server(test_nID_Server2);
	subjectAccount.setnID_Subject(test_nID_Subject1);
	subjectAccount.setsLogin(test_sLogin2);
	subjectAccount.setsNote(test_sNote2);
	subjectAccount.setSubjectAccountType(testSubjectAccountType2);
	saveAndUpdateSubjectAccount(subjectAccount, " Добавление новой записи, изменен только id-subject");
    }

    // Попытка записи с идентичными полями: sLogin, nID_SubjectAccountType,
    // nID_Server, nID_Subject
    // Такая запись недопуcтима
    @Test(expected = org.hibernate.exception.ConstraintViolationException.class)
    public void checkFK_SubjectAccount_SubjectAccountType() {
	SubjectAccountType testSubjectAccountType = subjectAccountTypeDao
		.findByIdExpected(test_nID_SubjectAccountType2);

	// Добавли первую запись
	SubjectAccount subjectAccount = new SubjectAccount();
	subjectAccount.setnID_Server(test_nID_Server3);
	subjectAccount.setnID_Subject(test_nID_Subject3);
	subjectAccount.setsLogin(test_sLogin3);
	subjectAccount.setsNote(test_sNote3);
	subjectAccount.setSubjectAccountType(testSubjectAccountType);
	subjectAccountDao.saveOrUpdate(subjectAccount);

	// Пробуем добавить вторую запись: должна быть ошибка
	subjectAccount = new SubjectAccount();
	subjectAccount.setnID_Server(test_nID_Server3);
	subjectAccount.setnID_Subject(test_nID_Subject3);
	subjectAccount.setsLogin(test_sLogin3);
	subjectAccount.setsNote(test_sNote1);
	subjectAccount.setSubjectAccountType(testSubjectAccountType);
	subjectAccountDao.saveOrUpdate(subjectAccount);

	fail("Expected exception was missing. Ошибка проверки ограничения на уникальность полей sLogin, nID_SubjectAccountType, nID_Server, nID_Subject");
    }

    @Test(expected = org.hibernate.exception.ConstraintViolationException.class )
    public void checkFK_AccountContact_Subject() {
	SubjectAccountType testSubjectAccountType1 = subjectAccountTypeDao
		.findByIdExpected(test_nID_SubjectAccountType1);

	// Проверяем добавление записи с несуществующим nID_Subject 
	SubjectAccount subjectAccount = new SubjectAccount();
	subjectAccount.setnID_Server(test_nID_Server1);
	subjectAccount.setnID_Subject(test_nID_SubjectError);
	subjectAccount.setsLogin(test_sLogin1);
	subjectAccount.setsNote(test_sNote1);
	subjectAccount.setSubjectAccountType(testSubjectAccountType1);
	saveAndUpdateSubjectAccount(subjectAccount, " Добавление новой записи");

    }

    @Ignore
    @Test
    public void testGetSubjectAccount() {
	SubjectAccountType testSubjectAccountType2 = subjectAccountTypeDao
		.findByIdExpected(test_nID_SubjectAccountType2);

	// Проверка выборки на основании данных SubjectAccount.csv
	getSubjectAccounts(1L, null, null, null, 5);
	getSubjectAccounts(1L, "Логин01", null, null, 5);
	getSubjectAccounts(null, "Логин01", null, null, 3);
	getSubjectAccounts(1L, null, 1L, null, 2);
	getSubjectAccounts(1L, null, 1L, testSubjectAccountType2, 1);
    }

    private void saveAndUpdateSubjectAccount(SubjectAccount subjectAccount, String msg) {
	SubjectAccount sbret = subjectAccountDao.saveOrUpdate(subjectAccount);
	Assert.assertNotNull("Ошибка сохраниния записи в таблицу SubjectAccount. " + msg, sbret);
	Assert.assertNotNull("Ошибка сохраниния записи в таблицу SubjectAccount. " + msg, sbret.getId());
    }

    private void getSubjectAccounts(Long nID_Subject, String sLogin, Long nID_Server,
	    SubjectAccountType nID_SubjectAccountType, int retCount) {

	List<SubjectAccount> subjectAccountRet;
	subjectAccountRet = subjectAccountDao.findSubjectAccounts(nID_Subject, sLogin, nID_Server,
		nID_SubjectAccountType);

	String fm = String.format("nID_Subject=%d, sLogin=%s, nID_Server=%d, nID_SubjectAccountType=%s\n", nID_Subject,
		sLogin, nID_Server, nID_SubjectAccountType);
	Assert.assertNotNull("Ошибка получения данных из таблицы SubjectAccount, параметры: " + fm, subjectAccountRet);
	Assert.assertEquals("Ошибка получения данных из таблицы SubjectAccount, параметры: " + fm, retCount,
		subjectAccountRet.size());
    }

    // private void printall(String title) {
    // List<SubjectAccount> subjectAccounts = subjectAccountDao.findAll();
    // System.out.println(">--------------------------");
    // System.out.println(title);
    // for (SubjectAccount s : subjectAccounts) {
    // System.out.printf("id=%d login=%s id_server=%d id_subject=%d
    // id_subjectType=%d\n", s.getId(), s.getsLogin(),
    // s.getnID_Server(), s.getnID_Subject(),
    // s.getSubjectAccountType().getId());
    // }
    // System.out.println("<--------------------------");
    //
    // }
}
