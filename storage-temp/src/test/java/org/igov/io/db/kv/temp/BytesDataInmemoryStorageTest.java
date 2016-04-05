package org.igov.io.db.kv.temp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.igov.io.db.kv.temp.exception.RecordInmemoryException;
import org.igov.io.db.kv.temp.model.ByteArrayMultipartFile;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "/META-INF/spring/org-igov-io-db-kv-temp-context.xml",
    "/META-INF/spring/org-igov-io-db-kv-temp-test-context.xml"})
public class BytesDataInmemoryStorageTest {

    static final transient Logger LOG = LoggerFactory
            .getLogger(BytesDataInmemoryStorageTest.class);

    @Autowired
    private IBytesDataInmemoryStorage oStorage;

    @Value("#{testProps['loadFile']}")
    private String loadFile;

    @Value("#{testProps['pathToFile']}")
    private String pathToFile;

    @Test
    public void testBytesDataInmemoryStorage() throws IOException, RecordInmemoryException {
        byte[] aByte = loadfile(loadFile);
        File oFile = new File(loadFile);
        MultipartFile oByteArrayMultipartFile = new ByteArrayMultipartFile(
                aByte, oFile.getName(), oFile.getName(), "text/plain");
        ByteArrayOutputStream oByteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream oOutputStream = new ObjectOutputStream(oByteArrayOutputStream)) {
            oOutputStream.writeObject(oByteArrayMultipartFile);
            oOutputStream.flush();
        }
        String sKey = oStorage.putBytes(oByteArrayOutputStream.toByteArray());
        //System.out.println(key);
        byte[] byteFile = oStorage.getBytes(sKey);
        ByteArrayMultipartFile oByteArrayMultipartFile_Return = null;
        try {
            oByteArrayMultipartFile_Return = getByteArrayMultipartFileFromInmemoryStorage(byteFile);
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
        //System.out.println(contentMultipartFile);
    }

    public byte[] loadfile(String sPathFile) {
        Path oPath = Paths.get(sPathFile);
        byte[] aByte = null;
        try {
            aByte = Files.readAllBytes(oPath);
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return aByte;
    }

    public static ByteArrayMultipartFile getByteArrayMultipartFileFromInmemoryStorage(
            byte[] aByte) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(aByte);
        ObjectInputStream oInputStream = new ObjectInputStream(byteArrayInputStream);
        ByteArrayMultipartFile oByteArrayMultipartFile_Return = (ByteArrayMultipartFile) oInputStream.readObject();
        oInputStream.close();
        return oByteArrayMultipartFile_Return;
    }

}
