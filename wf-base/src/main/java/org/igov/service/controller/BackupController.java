package org.igov.service.controller;

import java.util.List;

import org.igov.model.backup.ActGeBytearray;
import org.igov.model.backup.BackupResult;
import org.igov.service.business.backup.ActGeBytearrayBackupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@Api(tags = {"BackupController — Создание бэкапа данных таблицы ActGeBytearray"})
@RequestMapping(value = "/backup")
public class BackupController {
	private static final Logger LOG = LoggerFactory.getLogger(BackupController.class);

	@Autowired
    private ActGeBytearrayBackupService actGeBytearrayBackupService;
	
    @ApiOperation(value = "Получение данных из таблицы act_ge_bytearray по заданному условию", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/backup/getActGeBytearray?condition=!\n")
    @RequestMapping(value = "/getActGeBytearray", method = RequestMethod.GET)
    @ResponseBody
    public List<ActGeBytearray> getActGeBytearray(@ApiParam(value = "Условие для выборки ", required = true) @RequestParam(value = "condition") String condition)
            throws Exception {
    	List<ActGeBytearray> actGeBytearrayList = null;
        try {
        	actGeBytearrayList = actGeBytearrayBackupService.getActGeBytearray(condition);

        } catch (Exception e) {
        	LOG.error("FAIL: {}", e.getMessage());
        }
        return actGeBytearrayList;
    }
    
    
    @ApiOperation(value = "Создание записи в act_ge_bytearray_backup", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/backup/insertActGeBytearrayBackup\n")
    @RequestMapping(value = "/insertActGeBytearrayBackup", method = RequestMethod.POST)
    @ResponseBody
    public BackupResult insertActGeBytearrayBackup(@ApiParam(value = "Данные для cоздание записи в act_ge_bytearray_backup ", required = true) @RequestBody ActGeBytearray actGeBytearray)
            throws Exception {
    	BackupResult backupResult = null;
    	try {
    		backupResult =  actGeBytearrayBackupService.insertActGeBytearrayBackup(actGeBytearray);
    	 } catch (Exception e) {
    		 return BackupResult.fillResult(e.getMessage(),BackupResult.PRCODE_ERROR,BackupResult.PRSTATE_ERROR);
         }
		return backupResult;
    }
    
    
    @ApiOperation(value = "Удаление записи из таблицы act_ge_bytearray", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/backup/deleteActGeBytearray\n")
    @RequestMapping(value = "/deleteActGeBytearray", method = RequestMethod.POST)
    @ResponseBody
    public BackupResult deleteActGeBytearray(@ApiParam(value = "Данные для удаления записи из таблицы act_ge_bytearray ", required = true) @RequestParam(value = "id") String id)
            throws Exception {
    	BackupResult backupResult = null;
    	try {
    		backupResult =  actGeBytearrayBackupService.deleteActGeBytearray(id);
    	 } catch (Exception e) {
    		 return BackupResult.fillResult(e.getMessage(),BackupResult.PRCODE_ERROR,BackupResult.PRSTATE_ERROR);
         }
    	
    	return backupResult;
    }
    
    
    @ApiOperation(value = "Создание бэкапа для таблицы act_ge_bytearray", notes = "##### Пример:\n"
            + "https://alpha.test.region.igov.org.ua/wf/service/backup/createBackup\n")
    @RequestMapping(value = "/createBackup", method = RequestMethod.POST)
    @ResponseBody
    public BackupResult createBackup(@ApiParam(value = "Данные для создания бэкапа таблицы act_ge_bytearray по заданному условию", required = true) @RequestParam(value = "condition") String condition)
            throws Exception {
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
      	   return BackupResult.fillResult("backup not create..actGeBytearrayList isEmpty "+actGeBytearrayList,BackupResult.PRCODE_ERROR,BackupResult.PRSTATE_ERROR);
         }
            LOG.info("createBackup finish.....");
            return BackupResult.fillResult("createBackup finish ",BackupResult.PRCODE_OK,BackupResult.PRSTATE_OK);
        }
        catch(Exception ex){
            LOG.info("createBackup throws an error: {}", ex);
            return BackupResult.fillResult(ex.getMessage(),BackupResult.PRCODE_ERROR,BackupResult.PRSTATE_ERROR);
        }
    }


}
