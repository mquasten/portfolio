package de.mq.portfolio.batch.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.batch.MethodParameterInjection;
import de.mq.portfolio.share.TimeCourse;

public class ItemReaderServiceAdapterTest {

	private static final String ENRICHTERS_FIELDS = "enrichters";
	private static final String CONTENT_FIELD = "content";
	private static final String METHOD_PARAMETER_INJECTION_FIELD = "methodParameterInjection";
	@SuppressWarnings("unchecked")
	private final MethodParameterInjection<String> methodParameterInjection = Mockito.mock(MethodParameterInjection.class);
	private final ItemReader<TimeCourse> reader = new SimpleListItemReaderServiceAdapterImpl<>(methodParameterInjection);  
	private TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	
	@SuppressWarnings("unchecked")
	@Test
	public final void read() throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception {
		final  Collection<TimeCourse> results = new ArrayList<>();
		results.add(timeCourse);
		Mockito.when(methodParameterInjection.invokeMethod(Mockito.anyMap())).thenReturn(results);
		
		Assert.assertEquals(timeCourse, reader.read());
		Assert.assertNull(reader.read());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public final void readSingle() throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception {
		Mockito.when(methodParameterInjection.invokeMethod(Mockito.anyMap())).thenReturn(timeCourse);
		Assert.assertEquals(timeCourse, reader.read());
		Assert.assertNull(reader.read());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public final void readArray() throws UnexpectedInputException, ParseException, NonTransientResourceException, Exception  {
		Mockito.when(methodParameterInjection.invokeMethod(Mockito.anyMap())).thenReturn(new TimeCourse[] {timeCourse});
		Assert.assertEquals(timeCourse, reader.read());
		Assert.assertNull(reader.read());
	}
	
	@Test
	public final void constructor() {
		@SuppressWarnings("unchecked")
		JobContent<String> content = Mockito.mock(JobContent.class);
		final ItemReader<TimeCourse> reader = new SimpleListItemReaderServiceAdapterImpl<>(methodParameterInjection,content);
		Assert.assertEquals(methodParameterInjection, ReflectionTestUtils.getField(reader, METHOD_PARAMETER_INJECTION_FIELD));
		Assert.assertEquals(content, ReflectionTestUtils.getField(reader, CONTENT_FIELD));
	}
	
	@Test
	public final void constructorWithEnritcher() {
		@SuppressWarnings("unchecked")
		JobContent<String> content = Mockito.mock(JobContent.class);
		final Map<String, MethodParameterInjection<String>> enrichters = new HashMap<>();
		enrichters.put("result", methodParameterInjection);
		final ItemReader<TimeCourse> reader = new SimpleListItemReaderServiceAdapterImpl<>(methodParameterInjection,content, enrichters);
		Assert.assertEquals(methodParameterInjection, ReflectionTestUtils.getField(reader, METHOD_PARAMETER_INJECTION_FIELD));
		Assert.assertEquals(content, ReflectionTestUtils.getField(reader, CONTENT_FIELD));
		Assert.assertEquals(enrichters, ReflectionTestUtils.getField(reader, ENRICHTERS_FIELDS));
		
		
	}
}
