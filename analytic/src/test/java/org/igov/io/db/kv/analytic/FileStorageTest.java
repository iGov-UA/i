package org.igov.io.db.kv.analytic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.igov.io.db.kv.analytic.impl.FileMongoStorageAnalytic;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import org.igov.io.db.kv.statical.exceptions.RecordNotFoundException;
import org.igov.io.db.kv.statical.model.UploadedFile;
 
//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:context-analytic.xml"})
public class FileStorageTest {

	@Autowired
	private FileMongoStorageAnalytic oStorage;

	
	@Test(expected=RecordNotFoundException.class)
	public void openStreamShouldThrowOnInexistentKey() throws RecordNotFoundException {
		oStorage.openFileStream("inexistent_key");
	}
	
	@Test
	public void getFileShouldReturnNullOnInexistentKey()  {
		Assert.assertNull(oStorage.getFile("inexistent_key"));
	}
	
	@Test
	public void getMetadataShouldReturnNullOnInexistentKey()  {
		Assert.assertNull(oStorage.getFileMetadata("inexistent_key"));
	}
	
	@Test
	public void shouldSaveReadDeleteFile() throws IOException {
		MultipartFile mf1 = new MockMultipartFile("test", "testOriginalFilename1", "text/plain", "test content first file".getBytes());
		MultipartFile mf2 = new MockMultipartFile("test", "testOriginalFilename1", "text/plain", "test,content,second,file".getBytes());
		
		String id = oStorage.createFile(mf1);
		UploadedFile readFile1 = oStorage.getFile(id);
		Assert.assertNotNull(readFile1);
		Assert.assertArrayEquals(mf1.getBytes(), readFile1.getContent());
		Assert.assertEquals(mf1.getContentType(), readFile1.getMetadata().getContentType());
		Assert.assertEquals(mf1.getOriginalFilename(), readFile1.getMetadata().getOriginalFilename());
                
		Assert.assertTrue(oStorage.saveFile(id, mf2));
		UploadedFile readFile2 = oStorage.getFile(id);
		Assert.assertArrayEquals(mf2.getBytes(), readFile2.getContent());
		Assert.assertEquals(mf2.getContentType(), readFile2.getMetadata().getContentType());
		Assert.assertEquals(mf2.getOriginalFilename(), readFile2.getMetadata().getOriginalFilename());
                
		
		Assert.assertTrue(oStorage.remove(id));
		Assert.assertFalse(oStorage.keyExists(id));
	}
	
	public static byte[] loadfile(String pathDirFile) {
		Path path = Paths.get(pathDirFile);
		byte[] data = null;
		try {
			data = Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
}
