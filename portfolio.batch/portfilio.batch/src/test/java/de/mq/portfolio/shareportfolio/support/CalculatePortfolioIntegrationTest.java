package de.mq.portfolio.shareportfolio.support;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/calculatePortfolio.xml" })
@Ignore
public class CalculatePortfolioIntegrationTest {

	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private Job job; 
	
	@Test
	public final void runJob() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
	
		
	
		
		final Map<String,JobParameter> params = new HashMap<>();
		params.put("portfolioName", new JobParameter("mq-test"));
		params.put("samples", new JobParameter((long) 500000));
	
		final JobParameters jobParameters = new JobParameters(params);
	
		System.out.println("*** CalculatePortfolioIntegrationTest stared ***");
		
		JobExecution execution =  jobLauncher.run(job, jobParameters);
		System.out.println(execution.getStepExecutions());
	}
	
}
