package org.igov.io.db.kv.statical;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import org.igov.io.db.kv.statical.exceptions.RecordNotFoundException;
import org.igov.io.db.kv.statical.model.UploadedFile;
import org.igov.io.db.kv.statical.model.UploadedFileMetadata;

/**
 * @author vbolshutkin & BW
 */
public interface IFileStorage {
	
	/**
	 * Saves MultipartFile to the storage. Generates a random key.
	 * 
         * @param file 
	 * @return key as String. <b>null</b> if saving failed. 
	 */
	public String createFile(MultipartFile file);
	
	/**
	 * Saves MultipartFile to the storage with the predefined key. 
	 * Overwrites previously saved MultipartFile with the same key.
	 * 
	 * @param key String
         * @param file 
	 * @return boolean, whether the data was successfully saved 
	 */
	public boolean saveFile(String key, MultipartFile file);
	
	/**
	 * Removes file from the storage by given key.
	 * 
	 * @param key String
	 * @return <b>true</b> if the data 
	 * 	was removed or not found, <b>false</b> otherwise . 
	 */
	public boolean remove(String key);
	
	
	/**
	 * Determines whether there exists file with the provided key.
	 * It is NOT GUARANTEED that consequent getFile, getFileMetadata or openFileStream
	 *  will return the data, as the file might be removed from a different thread. 
	 * 
	 * @param key String
	 * @return boolean whether there exists data with the provided key. 
	 */
	public boolean keyExists(String key);
	
	/**
	 * Queries file byte array by the key. 
	 * 
	 * @param key String
	 * @return file. <b>null</b> if not found.
	 */
	public UploadedFile getFile(String key);
	
	/**
	 * Queries file metadata byte array by the key. 
	 * 
	 * @param key String
	 * @return file metadata. <b>null</b> if not found.
	 */
	public UploadedFileMetadata getFileMetadata(String key);
	
	/**
	 * Queries file by the key and opens a stream. Ideal 
	 * for streaming large data. Recommended to use with
	 * try-with-resources syntax.
	 * 
	 * @param key String
	 * @return file content as InputStream.
     * @throws org.igov.io.db.kv.statical.exceptions.RecordNotFoundException
	 */
	public InputStream openFileStream(String key) throws RecordNotFoundException;
}
