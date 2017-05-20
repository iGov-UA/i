package org.igov.processor;

import org.igov.service.business.flow.FlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobBuilderFlowSlotsProcessor {

	  private final static Logger LOG = LoggerFactory.getLogger(JobBuilderFlowSlotsProcessor.class);
	
  @Autowired
  private FlowService oFlowService;	
  
  public void executeFlowSlots()  {
      try{
          LOG.info("executeFlowSlots start.....");
          oFlowService.buildFlowSlots();
          LOG.info("executeFlowSlots finish.....");
      }
      catch(Exception ex){
          LOG.info("JobBuilderFlowSlots throws an error: {}", ex);
      }
  }

}
