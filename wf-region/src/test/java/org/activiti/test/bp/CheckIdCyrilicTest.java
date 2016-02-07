package org.activiti.test.bp;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import org.junit.Test;

/**
 * 
 * @author kr110666kai
 * 
 *         Тест предназначен для обнаружения Бизнес-Процессов которые имеют в
 *         своем составе id-поля с недопустимыми символами Допустимые символы
 *         определяются в переменной ALLOWED_CHARS_MATCHES
 *
 */
public class CheckIdCyrilicTest {

    // Список каталогов где искать файлы с описанием БП
    private static final String[] srcBPMN = { "src/main/resources/bpmn/autodeploy" };

    // Список расширений файлов с описанием БП
    private static final String[] extBPMN = { ".bpmn" };

    // Теущий каталог. Относительно его ищем каталоги с БП
    private static final String curPath = Paths.get("").toAbsolutePath().toString();

    private List<ErrorID> errorIDs = new ArrayList<>();

    private ErrorID errorId = null;

    // Допустимые символы в id-полях процессов
    // 1. Цифры
    // 2. Символы английского алфавита
    // 3. Знаки пунктуации "#$%&'()*+,-./:;<=>?@[\]^_`{|}~
    private static final String ALLOWED_CHARS_MATCHES = "^[a-zA-Z0-9\\p{Punct}]+$";
    boolean isError = false;

    int countid = 0;

    @Test
    public void checkIdBP() throws FileNotFoundException {
	// Расскоментировать если необходим вывод в файл
	// File file = new File("checkidcyrilic.txt");
	// FileOutputStream fis = new FileOutputStream(file);
	// PrintStream out = new PrintStream(fis);
	// System.setOut(out);
	// System.setErr(out);
	//////////////////////////////////////////////

	for (int i = 0; i < srcBPMN.length; i++) {
	    findFileInDirectory(new File(curPath + File.separator + srcBPMN[i]));
	}

	// Вывод найденных ошибок
	if (countid != 0) {
	    Collections.sort(errorIDs, errorIDComparator);
	    System.out.println(
		    "---------------------------------------------------------------------------------------------------------");
	    System.out.println("[ERROR] Недопустимые символы в id-полях процессов:");
	    System.out.println(curPath + "/");
	    int k = 0;
	    for (ErrorID errorId : errorIDs) {
		System.out.printf("%s:\n", errorId.getBaseUrl().replaceAll(curPath, ""));
		for (DescrID descrId : errorId.getDescrID()) {
		    k++;
		    System.out.printf("%4d %s id=\"%s\"\n", k, descrId.getNodeName(), descrId.getVal());
		}
	    }
	    System.out.println(
		    "---------------------------------------------------------------------------------------------------------");

	    // Расскоментировать если нужно прервать сборку из-за ошибок
	    fail("ERROR. Обнаружены недопустимые символы в id-полях БизнесПроцессов");
	}
    }

    // Поиск файлов БП в каталоге и всех его подкаталогах
    private void findFileInDirectory(File dir) {
	if (dir.isDirectory()) {
	    String[] children = dir.list();
	    for (int i = 0; i < children.length; i++) {
		File f = new File(dir, children[i]);
		findFileInDirectory(f);
	    }
	} else {
	    checkBP(dir);
	}
    }

    private void checkBP(File f) {

	// Проверка на расширение фала БП. см. extBPMN
	boolean isBmpnFile = false;
	for (int i = 0; i < extBPMN.length; i++) {
	    if (f.getName().endsWith(extBPMN[i])) {
		isBmpnFile = true;
		break;
	    }
	}
	if (!isBmpnFile) {
	    return;
	}

	// Парсинг файла БП
	try {
	    isError = false;
	    DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document doc = dBuilder.parse(f);

	    if (doc.hasChildNodes()) {
		getNode(doc.getChildNodes());
	    }

	} catch (Exception e) {
	    System.out.println("ERROR in file: " + f.getAbsolutePath());
	    System.out.println(e.getMessage());
	    System.out.println("");
	}

    }

    private void getNode(NodeList nodeList) {

	for (int count = 0; count < nodeList.getLength(); count++) {
	    Node tempNode = nodeList.item(count);
	    tempNode.getParentNode().getNodeName();

	    if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

		if (tempNode.hasAttributes()) {
		    NamedNodeMap nodeMap = tempNode.getAttributes();
		    for (int i = 0; i < nodeMap.getLength(); i++) {
			Node node = nodeMap.item(i);

			String nodeName = node.getNodeName();
			String val = node.getNodeValue();
			if (nodeName.toLowerCase().equals("id")) {
			    if (!val.matches(ALLOWED_CHARS_MATCHES)) {
				if (!isError) {
				    isError = true;
				    errorId = new ErrorID(tempNode.getBaseURI());
				    errorIDs.add(errorId);
				}
				countid++;
				errorId.addDescrID(new DescrID(tempNode.getNodeName(), val));
			    }
			}

		    }
		}

		if (tempNode.hasChildNodes()) {
		    // loop again if has child nodes
		    getNode(tempNode.getChildNodes());
		}
	    }
	}
    }

    static Comparator<ErrorID> errorIDComparator = new Comparator<ErrorID>() {
	@Override
	public int compare(ErrorID o1, ErrorID o2) {
	    // TODO Auto-generated method stub
	    return o1.getBaseUrl().compareTo(o2.getBaseUrl());
	}
    };

}

class ErrorID {
    private String baseUrl;
    private List<DescrID> descrID = new ArrayList<>();

    public ErrorID(String baseUrl) {
	this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
	return baseUrl;
    }

    public List<DescrID> getDescrID() {
	return descrID;	
    }

    public void addDescrID(DescrID descrID) {
	this.descrID.add(descrID);
    }
}

class DescrID {
    private String nodeName;
    private String val;

    public DescrID(String nodeName, String val) {
	this.nodeName = nodeName;
	this.val = val;
    }

    public String getNodeName() {
	return nodeName;
    }

    public String getVal() {
	return val;
    }

}