package org.igov.io.db.kv.statical;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.igov.io.db.kv.statical.exceptions.RecordNotFoundException;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/org-igov-io-db-kv-statical-context.xml"})
public class BytesDataStorageTest {

	@Autowired
	private IBytesDataStorage oStorage;

	@Test(expected=RecordNotFoundException.class)
	public void openStreamShouldThrowOnInexistentKey() throws RecordNotFoundException {
		oStorage.openDataStream("inexistent_key");
	}
        
        @Test
	public void getDataShouldReturnNullOnInexistentKey()  {
		Assert.assertNull(oStorage.getData("inexistent_key"));
	}

	@Test
	public void shouldSaveReadDeleteRecord() {
		byte[] data1 = new byte[] {1, 2, 3};
		byte[] data2 = new byte[] {4, 5, 6, 7};
		
		String id = oStorage.saveData(data1);
		Assert.assertTrue(oStorage.keyExists(id));
		byte[] readData1 = oStorage.getData(id);
		Assert.assertArrayEquals(data1, readData1);
		Assert.assertTrue(oStorage.setData(id, data2));
		byte[] readData2 = oStorage.getData(id);
		Assert.assertArrayEquals(data2, readData2);
		Assert.assertTrue(oStorage.remove(id));
		Assert.assertFalse(oStorage.keyExists(id));
	}
	
}