package de.mq.portfolio.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.mq.portfolio.batch.RulesEngine;
import junit.framework.Assert;

public class BatchProcessorTest {
	
	
	private static final String RULE_ENGINE_BEAN_NAME = "importExchangeRates";
	private static final String PARAM_KEY  = "filename";
	private static final String PARAM_VALUE  = "exchangeRates.csv";
	private static final String SUCCESS_RULE = "aRule";
	private final BatchProcessorImpl batchProcessor =  Mockito.mock(BatchProcessorImpl.class, Mockito.CALLS_REAL_METHODS);
	private final List<String> arguments = Arrays.asList(RULE_ENGINE_BEAN_NAME , PARAM_KEY+"="+ PARAM_VALUE);
	
	private final AnnotationConfigApplicationContext applicationContext = Mockito.mock(AnnotationConfigApplicationContext.class);
	
	private final RulesEngine  rulesEngine = Mockito.mock(RulesEngine.class);
	
	
	
	
	@SuppressWarnings("rawtypes")
	ArgumentCaptor<Map> paramsCaptor= ArgumentCaptor.forClass(Map.class);
	
	
	@SuppressWarnings("unchecked")
	@Test
	public final void process() {
		Mockito.when(batchProcessor.applicationContext()).thenReturn(applicationContext);
		
		Mockito.when(applicationContext.getBean(RULE_ENGINE_BEAN_NAME, RulesEngine.class)).thenReturn(rulesEngine);
		Mockito.when(rulesEngine.processed()).thenReturn( Arrays.asList(SUCCESS_RULE));
		
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
	
	@Test(expected=IllegalArgumentException.class)
	public final void processNoRuleProcesses() {
		Mockito.when(batchProcessor.applicationContext()).thenReturn(applicationContext);
		
		Mockito.when(applicationContext.getBean(RULE_ENGINE_BEAN_NAME, RulesEngine.class)).thenReturn(rulesEngine);
		Mockito.when(rulesEngine.processed()).thenReturn( new ArrayList<>());
		
		batchProcessor.process(arguments);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void processWrornParameters() {
		Mockito.when(batchProcessor.applicationContext()).thenReturn(applicationContext);
		
		Mockito.when(applicationContext.getBean(RULE_ENGINE_BEAN_NAME, RulesEngine.class)).thenReturn(rulesEngine);
		Mockito.when(rulesEngine.processed()).thenReturn( Arrays.asList(SUCCESS_RULE));
		
		batchProcessor.process(Arrays.asList(RULE_ENGINE_BEAN_NAME , PARAM_KEY));
		
		
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public final void failed() {
		Mockito.when(batchProcessor.applicationContext()).thenReturn(applicationContext);
		
		Mockito.when(applicationContext.getBean(RULE_ENGINE_BEAN_NAME, RulesEngine.class)).thenReturn(rulesEngine);
		@SuppressWarnings("unchecked")
		final Entry<String,Throwable> entry = Mockito.mock(Entry.class);
		Mockito.when(entry.getValue()).thenReturn(Mockito.mock(RuntimeException.class));
		Mockito.when(entry.getKey()).thenReturn(SUCCESS_RULE);
		Mockito.when(rulesEngine.failed()).thenReturn(Arrays.asList(entry));
		
		batchProcessor.process(arguments);
	}
	
	

	
} 
