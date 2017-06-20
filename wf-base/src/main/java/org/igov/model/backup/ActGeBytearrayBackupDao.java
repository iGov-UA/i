package org.igov.model.backup;

import java.util.List;

public interface ActGeBytearrayBackupDao {
	
	public BackupResult insertActGeBytearrayBackup(ActGeBytearray actGeBytearray);
	
	public List<ActGeBytearray> getActGeBytearray(String condition);
	
	public BackupResult deleteActGeBytearray(String id);

}
