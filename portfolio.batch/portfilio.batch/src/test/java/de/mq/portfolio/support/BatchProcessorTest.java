package de.mq.portfolio.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.mq.portfolio.batch.RulesEngine;


public class BatchProcessorTest {

	private static final String RULE_ENGINE_BEAN_NAME = "importExchangeRates";
	private static final String PARAM_KEY = "filename";
	private static final String PARAM_VALUE = "exchangeRates.csv";
	private static final String SUCCESS_RULE = "aRule";
	
	private final List<String> arguments = Arrays.asList(RULE_ENGINE_BEAN_NAME, PARAM_KEY + "=" + PARAM_VALUE);

//	private final AnnotationConfigApplicationContext applicationContext = Mockito.mock(AnnotationConfigApplicationContext.class);

	private final RulesEngine rulesEngine = Mockito.mock(RulesEngine.class);
	
	private  BatchProcessorImpl batchProcessor ;

	@SuppressWarnings("rawtypes")
	ArgumentCaptor<Map> paramsCaptor = ArgumentCaptor.forClass(Map.class);

	@Before
	public final void setup(){
		Mockito.when(rulesEngine.name()).thenReturn(RULE_ENGINE_BEAN_NAME);
		batchProcessor = new BatchProcessorImpl(Arrays.asList(rulesEngine));
	}
	
	

	@SuppressWarnings("unchecked")
	@Test
	public final void process() {
		

		
		Mockito.when(rulesEngine.processed()).thenReturn(Arrays.asList(SUCCESS_RULE));

		batchProcessor.process(arguments);

		Mockito.verify(rulesEngine).fireRules(paramsCaptor.capture());
		Mockito.verify(rulesEngine, Mockito.atLeastOnce()).processed();
		Mockito.verify(rulesEngine, Mockito.atLeastOnce()).failed();
		Assert.assertEquals(1, paramsCaptor.getValue().size());
		Assert.assertTrue(paramsCaptor.getValue().keySet().stream().findAny().isPresent());
		Assert.assertEquals(PARAM_KEY, paramsCaptor.getValue().keySet().stream().findAny().get());

		Assert.assertTrue(paramsCaptor.getValue().values().stream().findAny().isPresent());
		Assert.assertEquals(PARAM_VALUE, paramsCaptor.getValue().values().stream().findAny().get());

	}

	@Test(expected = IllegalArgumentException.class)
	public final void processNoRuleProcesses() {
		
		Mockito.when(rulesEngine.processed()).thenReturn(new ArrayList<>());

		batchProcessor.process(arguments);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void processWrornParameters() {
	
		Mockito.when(rulesEngine.processed()).thenReturn(Arrays.asList(SUCCESS_RULE));

		batchProcessor.process(Arrays.asList(RULE_ENGINE_BEAN_NAME, PARAM_KEY));

	}

	@Test(expected = IllegalArgumentException.class)
	public final void failed() {
		
		@SuppressWarnings("unchecked")
		final Entry<String, Throwable> entry = Mockito.mock(Entry.class);
		Mockito.when(entry.getValue()).thenReturn(Mockito.mock(RuntimeException.class));
		Mockito.when(entry.getKey()).thenReturn(SUCCESS_RULE);
		Mockito.when(rulesEngine.failed()).thenReturn(Arrays.asList(entry));

		batchProcessor.process(arguments);
	}

	

}
