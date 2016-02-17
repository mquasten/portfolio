package de.mq.portfolio.batch.support;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

class SimpleDeciderImpl implements JobExecutionDecider {
	
	private int counter = 0;
   public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
   	System.out.println(counter);
       if (counter >= 9 ) {
      	 return FlowExecutionStatus.STOPPED;
      	
       }
      
      	 counter++;
          return FlowExecutionStatus.COMPLETED;
      
   }
}