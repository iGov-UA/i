package org.activiti.test.bp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
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
    // private static final String[] srcBPMN = {
    // "src/main/resources/bpmn/CheckIdTest" };
    private static final String[] srcBPMN = { "src/main/resources/bpmn/autodeploy" };

    // Список расширений файлов с описанием БП
    private static final String[] extBPMN = { ".bpmn" };

    // Теущий каталог. Относительно его ищем каталоги с БП
    private static final String curPath = Paths.get("").toAbsolutePath().toString();

    // Допустимые символы в id-полях процессов
    // Цифры, символы английского алфавита, знаки пунктуации
    // "#$%&'()*+,-./:;<=>?@[\]^_`{|}~
    private static final String ALLOWED_CHARS_MATCHES = "^[a-zA-Z0-9\\p{Punct}]+$";
    boolean isError = false;
    
    int countid = 0;

    @Test
    public void getAllBP() throws FileNotFoundException {
	// Расскоментировать если необходим вывод в файл
	File file = new File("checkidcyrilic.txt");
	FileOutputStream fis = new FileOutputStream(file);
	PrintStream out = new PrintStream(fis);
	System.setOut(out);
	System.setErr(out);
	//////////////////////////////////////////////

	// System.out.println(
	// "------------------------------ Недопустимые символы в id-полях
	// процессов --------------------------------");
	System.out.println(curPath + "/");
	for (int i = 0; i < srcBPMN.length; i++) {
	    findFileInDirectory(new File(curPath + File.separator + srcBPMN[i]));
	}
	// System.out.println(
	// "---------------------------------------------------------------------------------------------------------");
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
	    System.out.println(e.getMessage());
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
				    System.out.printf("%s:\n", tempNode.getBaseURI().replaceAll(curPath, ""));
				}
				countid++;
				System.out.printf("%4d %s  id=\"%s\"\n", countid, tempNode.getNodeName(), val);
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

}
