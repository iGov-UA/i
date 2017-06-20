package org.igov.processor;

import java.util.List;

import org.apache.camel.Header;
import org.igov.model.backup.ActGeBytearray;
import org.igov.model.backup.BackupResult;
import org.igov.service.business.backup.ActGeBytearrayBackupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobActGeBytearrayBackupProcessor {

	  private final static Logger LOG = LoggerFactory.getLogger(JobActGeBytearrayBackupProcessor.class);
	
  @Autowired
  private ActGeBytearrayBackupService actGeBytearrayBackupService;	
  
  public void createBackup(@Header(value = "condition") String condition)  {
      try{
          LOG.info("createBackup start.....");
       List<ActGeBytearray>actGeBytearrayList = actGeBytearrayBackupService.getActGeBytearray(condition);
       
       if(actGeBytearrayList!=null && !actGeBytearrayList.isEmpty()){
    	   for(ActGeBytearray actGeBytearray:actGeBytearrayList){
    		   BackupResult backupResult = actGeBytearrayBackupService.insertActGeBytearrayBackup(actGeBytearray);
    		   
    		   if(BackupResult.PRSTATE_OK.equals(backupResult.getState())){
    			   actGeBytearrayBackupService.deleteActGeBytearray(actGeBytearray.getId_());
    		   }else{
    			   throw new Exception ("insetrt to ActGeBytearrayBackup is fail.. ");
    		   }
    	   }
       }else{
    	   LOG.info("actGeBytearrayList isEmpty....."+ actGeBytearrayList);
       }
          
          LOG.info("createBackup finish.....");
      }
      catch(Exception ex){
          LOG.info("createBackup throws an error: {}", ex);
      }
  }

}
