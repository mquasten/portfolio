package de.mq.portfolio.batch.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.ItemWriter;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.batch.MethodParameterInjection;
import de.mq.portfolio.share.TimeCourse;

public class ItemWriterServiceAdapterTest {
	
	
	private static final String PRE_PROCESSOR_FIELD = "preProcessor";
	private static final String CONTENT_FIELD = "content";
	private static final String METHOD_PARAMETER_INJECTION_FIELD = "methodParameterInjection";

	@SuppressWarnings("unchecked")
	private final MethodParameterInjection<String> methodParameterInjection = Mockito.mock(MethodParameterInjection.class); 
	@SuppressWarnings("unchecked")
	private final MethodParameterInjection<String> preProcessor = Mockito.mock(MethodParameterInjection.class);
	
	private final ItemWriter<TimeCourse> itemWriter = new SimpleItemWriterServiceAdapterImpl<>(methodParameterInjection, preProcessor);
	
	private final TimeCourse timeCourseIn = Mockito.mock(TimeCourse.class);
	private final TimeCourse timeCourseOut = Mockito.mock(TimeCourse.class);
	
	
	@SuppressWarnings("unchecked")
	@Test
	public final void afterStep() throws Exception {
		
		final List<TimeCourse> items = new ArrayList<>();
		items.add(timeCourseIn);
		itemWriter.write(items);
		
		final List<TimeCourse> aggregatedItems = new ArrayList<>();
		aggregatedItems.add(timeCourseOut);
		
		Mockito.when(preProcessor.invokeMethod(Mockito.anyMap())).thenReturn(aggregatedItems);
		final Map<String, Object> params = new HashMap<>();
		params.put(null, timeCourseOut);
		((SimpleItemWriterServiceAdapterImpl<?>)itemWriter).afterStep();
		Mockito.verify(methodParameterInjection).invokeMethod(params);
	}
	
	@Test
	public final void afterStepWithOutPreProcessor() throws Exception {
		
		final List<TimeCourse> items = new ArrayList<>();
		items.add(timeCourseIn);
		itemWriter.write(items);
		
		final ItemWriter<TimeCourse> itemWriter = new SimpleItemWriterServiceAdapterImpl<>(methodParameterInjection);
		itemWriter.write(items);
		final Map<String, Object> params = new HashMap<>();
		params.put(null, timeCourseIn);
		((SimpleItemWriterServiceAdapterImpl<?>)itemWriter).afterStep();
		Mockito.verify(methodParameterInjection).invokeMethod(params);
		
		
	}
	
	@Test
	public final void constructor() {
		@SuppressWarnings("unchecked")
		JobContent<String> content = Mockito.mock(JobContent.class);
		final ItemWriter<TimeCourse> writer = new SimpleItemWriterServiceAdapterImpl<>(methodParameterInjection,content);
		Assert.assertEquals(methodParameterInjection, ReflectionTestUtils.getField(writer, METHOD_PARAMETER_INJECTION_FIELD));
		Assert.assertEquals(content, ReflectionTestUtils.getField(writer, CONTENT_FIELD));
	}
	
	@Test
	public final void constructor2() {
		@SuppressWarnings("unchecked")
		JobContent<String> content = Mockito.mock(JobContent.class);
		final ItemWriter<TimeCourse> writer = new SimpleItemWriterServiceAdapterImpl<>(methodParameterInjection,content, preProcessor);
	
		Assert.assertEquals(methodParameterInjection, ReflectionTestUtils.getField(writer, METHOD_PARAMETER_INJECTION_FIELD));
		Assert.assertEquals(content, ReflectionTestUtils.getField(writer, CONTENT_FIELD));
		
		Assert.assertEquals(Optional.of(preProcessor), ReflectionTestUtils.getField(writer, PRE_PROCESSOR_FIELD));
		
	}
	
}
