package de.mq.portfolio.batch.support;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.batch.MethodParameterInjection;
import de.mq.portfolio.share.TimeCourse;

public class AbstractServiceAdapterTest {

	private static final String TIMEKURSE_ID = "19680528";

	private static final String TIME_KURSE_ID_KEY = "timeKurseId";

	private static final String TIME_COURSE_KEY = "timeCourse";

	private static final String PARAM_VALUE = "file.csv";

	private static final String PARAM_KEY = "fimename";

	private final AbstractServiceAdapter abstractServiceAdapter = Mockito.mock(AbstractServiceAdapter.class);

	@SuppressWarnings("unchecked")
	private MethodParameterInjection<String> methodParameterInjection = Mockito.mock(MethodParameterInjection.class);

	private final Map<String, MethodParameterInjection<String>> enrichters = new HashMap<>();

	private final Map<String, Object> params = new HashMap<>();
	@SuppressWarnings("unchecked")
	private MethodParameterInjection<String> enrichter = Mockito.mock(MethodParameterInjection.class);

	@SuppressWarnings("unchecked")
	private final JobContent<String> content = Mockito.mock(JobContent.class);

	@Before
	public void setup() {

		enrichters.put(TIME_COURSE_KEY, enrichter);
		params.put(PARAM_KEY, PARAM_VALUE);
		ReflectionTestUtils.setField(abstractServiceAdapter, "methodParameterInjection", methodParameterInjection);
		ReflectionTestUtils.setField(abstractServiceAdapter, "params", params);
		ReflectionTestUtils.setField(abstractServiceAdapter, "enrichters", enrichters);
		ReflectionTestUtils.setField(abstractServiceAdapter, "content", content);
	}

	@Test
	public final void invokeMethod() {

		abstractServiceAdapter.invokeMethod();

		Mockito.verify(methodParameterInjection).invokeMethod(params);
	}

	@Test
	public final void params() {
		Assert.assertEquals(params, abstractServiceAdapter.params());
	}

	@Test
	public final void putItem() {
		final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
		abstractServiceAdapter.putItem(timeCourse);

		Assert.assertEquals(timeCourse, params.get(null));
	}

	@SuppressWarnings("unchecked")
	@Test
	public final void beforeStep() {
		TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
		final StepExecution stepExecution = Mockito.mock(StepExecution.class);
		final JobExecution jobExecution = Mockito.mock(JobExecution.class);
		final JobParameters jobParameters = Mockito.mock(JobParameters.class);
		final Map<String, JobParameter> parameter = new HashMap<>();
		parameter.put(TIME_KURSE_ID_KEY, new JobParameter(TIMEKURSE_ID));
		Mockito.when(jobParameters.getParameters()).thenReturn(parameter);
		Mockito.when(stepExecution.getJobExecution()).thenReturn(jobExecution);
		Mockito.when(jobExecution.getJobParameters()).thenReturn(jobParameters);
		Mockito.when(enrichter.invokeMethod(Mockito.anyMap())).thenReturn(timeCourse);

		abstractServiceAdapter.beforeStep(stepExecution);
		Mockito.verify(content).putContent(TIME_COURSE_KEY, timeCourse);
		Assert.assertEquals(TIMEKURSE_ID, params.get(TIME_KURSE_ID_KEY));
		Assert.assertEquals(timeCourse, params.get(TIME_COURSE_KEY));
	}
}
