package org.igov.service.business.backup;

import java.util.List;

import org.igov.model.backup.ActGeBytearray;
import org.igov.model.backup.BackupResult;

public interface ActGeBytearrayBackupService {

	public BackupResult insertActGeBytearrayBackup(ActGeBytearray actGeBytearray);
	
	public List<ActGeBytearray> getActGeBytearray(String condition);
	
	public BackupResult deleteActGeBytearray(String id);


}
