package de.mq.portfolio.support;

import java.io.BufferedReader;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import org.junit.Assert;

public class JsonInputServiceTest {
	
	private final AbstractJsonInputService inputService = Mockito.mock(AbstractJsonInputService.class, Mockito.CALLS_REAL_METHODS);
	
	
	
	@Before
	public void setup() {
		Mockito.doAnswer(a -> new ExceptionTranslationBuilderImpl<Void,BufferedReader>() ).when(inputService).exceptionTranslationBuilder();
		Arrays.asList(AbstractJsonInputService.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(String.class)).filter(field -> ! Modifier.isStatic(field.getModifiers())).forEach(field -> ReflectionTestUtils.setField(inputService, field.getName(), System.getProperty("line.separator")));
	   
	}

	@Test
	public final void read() {
		final Collection<String> results = inputService.read("data/mq-minRisk.json");
		Assert.assertEquals(1, results.size());
		
		Assert.assertTrue(results.stream().findAny().get().contains("\"name\" : \"mq-minRisk\""));
		
		Assert.assertFalse(results.stream().findAny().get().contains("\"_id\""));
		Assert.assertTrue(results.stream().findAny().get().startsWith("{"));
		Assert.assertTrue(results.stream().findAny().get().length()> 10000);
		Assert.assertTrue(results.stream().findAny().get().replaceAll("[\t, \r, \0, \n]", "").endsWith("}"));
		
		
	
	}
	@Test
	public final void lineSeparator() {
		final AbstractJsonInputService inputService = BeanUtils.instantiateClass(this.inputService.getClass());
		Arrays.asList(AbstractJsonInputService.class.getDeclaredFields()).stream().filter(field -> ! Modifier.isStatic(field.getModifiers())).filter(field -> field.getType().equals(String.class)).forEach(field -> Assert.assertEquals(System.getProperty(AbstractJsonInputService.LINE_SEPARATOR), ReflectionTestUtils.getField(inputService, field.getName())));
	}
	
	
}
