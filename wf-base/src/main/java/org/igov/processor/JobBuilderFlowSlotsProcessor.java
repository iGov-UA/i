package org.igov.processor;

import org.igov.service.business.flow.FlowService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobBuilderFlowSlotsProcessor {

	  private final static Logger LOG = LoggerFactory.getLogger(JobBuilderFlowSlotsProcessor.class);
	  final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.000");
	
  @Autowired
  private FlowService oFlowService;	
  
  public void executeFlowSlots()  {
  	String dateStartTime = "2017-05-08 08:00:00.000";
  	String dateEndTime = "2017-06-10 18:27:00.000";
  	DateTime startTime = convertDate(dateStartTime);
  	DateTime endTime = convertDate(dateEndTime);
      
      try{
          LOG.info("executeFlowSlots start.....");
          oFlowService.buildFlowSlots(20L,startTime,endTime);
          LOG.info("executeFlowSlots finish.....");
      }
      catch(Exception ex){
          LOG.info("JobBuilderFlowSlots throws an error: {}", ex);
      }
  }

	public DateTime convertDate(String date) {
		// Parsing the date
		DateTime startTime = dtf.parseDateTime(date);
		return startTime;
	} 

}
