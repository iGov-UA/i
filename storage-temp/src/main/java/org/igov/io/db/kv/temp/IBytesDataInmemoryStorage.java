package org.igov.io.db.kv.temp;

import org.igov.io.db.kv.temp.exception.RecordInmemoryException;


/**
 * @author bw
 *
 */
public interface IBytesDataInmemoryStorage {
    
	String putBytes(byte[] aByte) throws RecordInmemoryException;
	
	byte[] getBytes(String sKey) throws RecordInmemoryException;
	
	String putString(String sKey, String sValue) throws RecordInmemoryException;

	String getString(String sKey) throws RecordInmemoryException;
}
