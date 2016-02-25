package de.mq.portfolio.batch.support;



import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.batch.MethodParameterInjection;

public class DeciderTest {
	
	private static final String CONTINUE_STATE = "CONTINUE";

	@SuppressWarnings("unchecked")
	final MethodParameterInjection<String> methodParameterInjection = Mockito.mock(MethodParameterInjection.class);
	
	final JobExecution jobExecution = Mockito.mock(JobExecution.class);
	final StepExecution stepExecution = Mockito.mock(StepExecution.class);
	
	final JobExecutionDecider  jobExecutionDecider = new SimpleDeciderImpl(methodParameterInjection);
	
	final JobParameters jobParameters = Mockito.mock(JobParameters.class);
	
	private static final String CONTENT_FIELD = "content";
	
	
	@Before
	public void setup() {
		Mockito.when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		Mockito.when(stepExecution.getJobExecution()).thenReturn(jobExecution);
	
		Mockito.when(stepExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);
	}
	
	@Test
	public final void decide() {
	
		
		
		final FlowExecutionStatus result = jobExecutionDecider.decide(jobExecution, stepExecution);
		Assert.assertEquals(FlowExecutionStatus.COMPLETED, result);
		
		
		
		Mockito.verify(jobExecution).getJobParameters();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public final void decideString() {
		Mockito.when(methodParameterInjection.invokeMethod(Mockito.anyMap())).thenReturn(CONTINUE_STATE);
		Assert.assertEquals(new FlowExecutionStatus(CONTINUE_STATE), jobExecutionDecider.decide(jobExecution, stepExecution));
	
		Mockito.verify(jobExecution).getJobParameters();
		final Map<String,Object> params =  ((AbstractServiceAdapter) jobExecutionDecider).params();
		Assert.assertEquals(1, params.size());
		Assert.assertNull(params.keySet().iterator().next());
		Assert.assertEquals(FlowExecutionStatus.COMPLETED.getName(), params.values().iterator().next());
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public final void decideFlowExecutionStatus() {
		final FlowExecutionStatus status = new FlowExecutionStatus(CONTINUE_STATE);
		Mockito.when(methodParameterInjection.invokeMethod((Mockito.anyMap() ))).thenReturn(status);
		
		Assert.assertEquals(status, jobExecutionDecider.decide(jobExecution, stepExecution));
		
		Mockito.verify(jobExecution).getJobParameters();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public final void decideExitStatus() {
		final ExitStatus status = new ExitStatus(CONTINUE_STATE);
		
		Mockito.when(methodParameterInjection.invokeMethod((Mockito.anyMap() ))).thenReturn(status);
		
		Assert.assertEquals(new FlowExecutionStatus(CONTINUE_STATE), jobExecutionDecider.decide(jobExecution, stepExecution));
		
		Mockito.verify(jobExecution).getJobParameters();
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=IllegalArgumentException.class)
	public final void decideInvalidType() {
	
		
		Mockito.when(methodParameterInjection.invokeMethod((Mockito.anyMap() ))).thenReturn(1L);
		
		jobExecutionDecider.decide(jobExecution, stepExecution);
		
	
	}
	
	@Test
	public final void constructorWithEnritcher() {
		@SuppressWarnings("unchecked")
		JobContent<String> content = Mockito.mock(JobContent.class);
		final Map<String, MethodParameterInjection<String>> enrichters = new HashMap<>();
		enrichters.put("result", methodParameterInjection);
		final JobExecutionDecider decider = new SimpleDeciderImpl(methodParameterInjection,content);
	
		Assert.assertEquals(content, ReflectionTestUtils.getField(decider, CONTENT_FIELD));
	
		
		
	}

	
}
