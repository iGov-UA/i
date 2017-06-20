package org.igov.service.business.backup;

import java.util.List;

import org.igov.model.backup.ActGeBytearray;
import org.igov.model.backup.ActGeBytearrayBackupDao;
import org.igov.model.backup.BackupResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActGeBytearrayBackupServiceImpl implements ActGeBytearrayBackupService {
	
	@Autowired
    private ActGeBytearrayBackupDao actGeBytearrayBackupDao;

	@Override
	public BackupResult insertActGeBytearrayBackup(ActGeBytearray actGeBytearray) {
		return actGeBytearrayBackupDao.insertActGeBytearrayBackup(actGeBytearray);
	}

	@Override
	public List<ActGeBytearray> getActGeBytearray(String condition) {
		return actGeBytearrayBackupDao.getActGeBytearray(condition);
	}

	@Override
	public BackupResult deleteActGeBytearray(String id) {
		return actGeBytearrayBackupDao.deleteActGeBytearray(id);
	}



	
	

}
