package de.mq.portfolio.batch.support;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.batch.item.ItemProcessor;

import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.batch.MethodParameterInjection;
import de.mq.portfolio.share.TimeCourse;

public class ItemProcessorServiceAdapterTest {

	private static final String CONTENT_FIELD = "content";
	private static final String METHOD_PARAMETER_INJECTION_FIELD = "methodParameterInjection";
	
	@SuppressWarnings("unchecked")
	private final MethodParameterInjection<String> methodParameterInjection = Mockito.mock(MethodParameterInjection.class);
	private final ItemProcessor<TimeCourse,TimeCourse> itemProcessor =  new SimpleItemProcessorServiceAdapterImpl<>(methodParameterInjection);
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	
	@SuppressWarnings("unchecked")
	@Test
	public final void process() throws Exception {
		@SuppressWarnings("rawtypes")
		ArgumentCaptor<Map> argumentCaptor = ArgumentCaptor.forClass(Map.class);
		Mockito.when(methodParameterInjection.invokeMethod(argumentCaptor.capture())).thenReturn(itemProcessor);
		Assert.assertEquals(itemProcessor, itemProcessor.process(timeCourse));
		
		Assert.assertEquals(1, ((AbstractServiceAdapter)itemProcessor).params().entrySet().size());
		Assert.assertNull(((AbstractServiceAdapter)itemProcessor).params().entrySet().stream().findAny().get().getKey());
		Assert.assertEquals(timeCourse, ((AbstractServiceAdapter)itemProcessor).params().entrySet().stream().findAny().get().getValue());
		Assert.assertEquals(((AbstractServiceAdapter)itemProcessor).params(), argumentCaptor.getValue());
	}
	
	@Test
	public final void constructor() {
		@SuppressWarnings("unchecked")
		JobContent<String> content = Mockito.mock(JobContent.class);
		final ItemProcessor<TimeCourse, TimeCourse> processor = new SimpleItemProcessorServiceAdapterImpl<>(methodParameterInjection,content);
		Assert.assertEquals(methodParameterInjection, ReflectionTestUtils.getField(processor, METHOD_PARAMETER_INJECTION_FIELD));
		Assert.assertEquals(content, ReflectionTestUtils.getField(processor, CONTENT_FIELD));
	}
}
