package org.igov.service.business.finance;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import liquibase.util.csv.CSVReader;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.igov.run.schedule.JobPaymentProcessor;
import org.igov.util.ToolLuna;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Service
public class PaymentProcessorService {

	private static final String VARIABLE_WITH_ORDER_ID = "PURPOSE";

	private final static Logger LOG = LoggerFactory.getLogger(PaymentProcessorService.class);
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService oTaskService;
    
    public void loadPaymentInformation(){
    	List<Map<String, String>> paymentsList = loadPayments();
        
        for (Map<String, String> currPayment : paymentsList){
        	String orderID = loadOrderId(currPayment);
        	LOG.info("Loaded order ID {}", orderID);
        	
        	String[] as=orderID.split("\\-");
            Long nID_Protected = Long.valueOf(as[1]);
            
            Long nID_Process = ToolLuna.getOriginalNumber(nID_Protected);
            
        	List<Task> tasks = oTaskService.createTaskQuery().processInstanceId(nID_Process.toString()).active().list();
        	for (Task task : tasks){
        		task.getTaskLocalVariables().putAll(currPayment);
        		task.getProcessVariables().putAll(currPayment);
        		LOG.info("Set variables {} to the task {}:{}", currPayment, task.getId(), task.getName());
        	}
        }
    }
    
    private String loadOrderId(Map<String, String> currPayment) {
		if (currPayment.containsKey(VARIABLE_WITH_ORDER_ID)){
			String cutSring = StringUtils.substringAfter(currPayment.get(VARIABLE_WITH_ORDER_ID), "=");
			return StringUtils.substringBefore(cutSring, " ");
		}
		return null;
	}

	private List<Map<String, String>> loadPayments() {
		List<Map<String, String>> res = new LinkedList<Map<String, String>>();

		String fileName = loadFileFromServer();

		try {
			CSVReader reader = new CSVReader(new FileReader(fileName));
			String[] headerArr = reader.readNext();
			LOG.info("Parsed header {}:{}", headerArr.toString(), headerArr.length);

			String[] nextLine = null;
			while ((nextLine = reader.readNext()) != null) {
				LOG.info("Parsing array {}:{}", nextLine.toString(), nextLine.length);
				Map<String, String> currElem = new HashMap<String, String>();
				for (int i = 0; i < nextLine.length; i++) {
					currElem.put(headerArr[i], nextLine[i]);
				}
				res.add(currElem);
			}
			reader.close();
		} catch (Exception e) {
			LOG.error("Exception occured while parsing csv file {}",
					e.getMessage(), e);
		}

		File file = new File(fileName);

		file.delete();

		return res;
	}
	
	private String loadFileFromServer(){
		JSch jsch = new JSch();
        Session session = null;
        File file = null;
        try {
        	file = File.createTempFile("11082016", ".csv");
            session = jsch.getSession("sftpuser", "alpha.test.igov.org.ua", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("zaqxswcde");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get("./logs/11082016.csv", file.getAbsolutePath());
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
        	LOG.error("Exception occured while getting file from sftp {}", e.getMessage(), e);  
        } catch (SftpException e) {
        	LOG.error("Exception occured while getting file from sftp {}", e.getMessage(), e);  
        } catch (IOException e) {
        	LOG.error("Exception occured while getting file from sftp {}", e.getMessage(), e);  
		}
        return file != null ? file.getAbsolutePath() : null;
	}
}
