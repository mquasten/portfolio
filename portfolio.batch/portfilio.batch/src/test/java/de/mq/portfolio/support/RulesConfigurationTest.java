package de.mq.portfolio.support;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.expression.Expression;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.ResourceAccessException;

import de.mq.portfolio.batch.Rule;
import de.mq.portfolio.batch.RulesEngine;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.support.SharesCSVLineConverterImpl;
import junit.framework.Assert;


public class RulesConfigurationTest {
	
	private static final String RULES_ENGINE_NAME = "importExchangeRates";

	private static final String SCOPE_SINGLETON = "singleton";

	private static final String SCOPE_PROTOTYPE = "prototype";

	private static final String TARGET_FIELD = "target";

	private final RulesConfiguration rulesConfiguration  = new RulesConfiguration();
	
	private final ShareService shareService = Mockito.mock(ShareService.class);
	
	private final RulesEngineBuilder rulesEngineBuilder = Mockito.mock(RulesEngineBuilder.class);
	private final ArgumentCaptor<Rule> ruleCapor = ArgumentCaptor.forClass(Rule.class);
	private final ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
	private final RulesEngine rulesEngine = Mockito.mock(RulesEngine.class);
	
	private final  ExchangeRateService  exchangeRateService = Mockito.mock(ExchangeRateService.class);
	
	@Before
	public final void setup() {
		Mockito.when(rulesEngineBuilder.withRule(ruleCapor.capture())).thenReturn(rulesEngineBuilder);
		Mockito.when(rulesEngineBuilder.withName(nameCaptor.capture())).thenReturn(rulesEngineBuilder);
		Mockito.when(rulesEngineBuilder.build()).thenReturn(rulesEngine);
		Mockito.when(rulesEngine.name()).thenReturn(RULES_ENGINE_NAME);
	}
	
	@Test
	public void rulesEngineBuilder() {
		Assert.assertEquals(SimpleRuleEngineBuilderImpl.class,rulesConfiguration.rulesEngineBuilder().getClass());
	}
	
	@Test
	public void importShares() {
		Assert.assertEquals(rulesEngine, rulesConfiguration.importShares(shareService, rulesEngineBuilder));
		
		Assert.assertEquals(RulesConfiguration.IMPORT_SHARES_RULE_ENGINE_NAME, nameCaptor.getValue());
		
		final List<Rule> rules = ruleCapor.getAllValues();
		Assert.assertEquals(2, rules.size());
		Assert.assertEquals(ImportServiceRuleImpl.class, rules.get(0).getClass());
		final SimpleCSVInputServiceImpl<?> reader = (SimpleCSVInputServiceImpl<?>) ReflectionTestUtils.getField(rules.get(0), TARGET_FIELD);
		
		Assert.assertEquals(SharesCSVLineConverterImpl.class, fieldValue(reader,Converter.class).getClass());
		final Expression inputExpression = fieldValue(rules.get(0), Expression.class);
		Assert.assertEquals(RulesConfiguration.SPEL_READ_FILENAME, inputExpression.getExpressionString());
		
		Assert.assertEquals(ProcessServiceRuleImpl.class, rules.get(1).getClass());
		final Expression outputExpression = fieldValue(rules.get(1), Expression.class);
		Assert.assertEquals(RulesConfiguration.SPEL_SAVE_ITEM, outputExpression.getExpressionString());
		
		Assert.assertEquals(shareService, ReflectionTestUtils.getField(rules.get(1), TARGET_FIELD));
		
		
	}

	@SuppressWarnings("unchecked")
	private <T>  T  fieldValue(final Object target, final Class<T> type) {
		
		final Collection<T> results = new ArrayList<>();
		ReflectionUtils.doWithFields(target.getClass(), field -> results.add((T) ReflectionTestUtils.getField(target, field.getName())), field -> field.getType().equals(type));
		
		DataAccessUtils.requiredSingleResult(results);
	   
	    return results.stream().findAny().get();
	}
	
	@Test
	public void importTimeCourses() {
		Assert.assertEquals(rulesEngine, rulesConfiguration.importTimeCourses(shareService, rulesEngineBuilder));
		Assert.assertEquals(RulesConfiguration.IMPORT_TIME_COURSES_RULE_ENGINE_NAME, nameCaptor.getValue());
		
		final List<Rule> rules = ruleCapor.getAllValues();
		Assert.assertEquals(3, rules.size());
		IntStream.range(0, 3).forEach(i ->Assert.assertEquals(shareService, ReflectionTestUtils.getField(rules.get(i), TARGET_FIELD)));
		
		
		Assert.assertEquals(ImportServiceRuleImpl.class, rules.get(0).getClass());
		
		final Expression inputExpression = fieldValue(rules.get(0), Expression.class);
		Assert.assertEquals(RulesConfiguration.SPEL_INPUT_SHARES, inputExpression.getExpressionString());
		
		IntStream.range(1, 3).forEach(i -> Assert.assertEquals(ProcessServiceRuleImpl.class, rules.get(i).getClass()));
		
		final Expression processExpression = fieldValue(rules.get(1), Expression.class);
		Assert.assertEquals(RulesConfiguration.SPEL_PROCESS_TIME_COURSE_ITEM, processExpression.getExpressionString());
		
		final Expression outputExpression = fieldValue(rules.get(2), Expression.class);
		Assert.assertEquals(RulesConfiguration.SPEL_REPLACE_TIME_COURSE_ITEM, outputExpression.getExpressionString());
		
		
	}
	
	@Test
	public void  importExchangeRates() {
		Assert.assertEquals(rulesEngine, rulesConfiguration.importExchangeRates(exchangeRateService, rulesEngineBuilder));
		Assert.assertEquals(RulesConfiguration.IMPORT_EXCHANGE_RATES_RULE_ENGINE_NAME, nameCaptor.getValue());
		final List<Rule> rules = ruleCapor.getAllValues();
		Assert.assertEquals(3, rules.size());
		
		Assert.assertEquals(ImportServiceRuleImpl.class, rules.get(0).getClass());
		IntStream.range(1, 3).forEach(i -> Assert.assertEquals(ProcessServiceRuleImpl.class, rules.get(i).getClass()));
		
		Assert.assertEquals(SimpleCSVInputServiceImpl.class, ReflectionTestUtils.getField(rules.get(0), TARGET_FIELD).getClass());
		IntStream.range(1, 3).forEach(i ->Assert.assertEquals(exchangeRateService, ReflectionTestUtils.getField(rules.get(i), TARGET_FIELD)));
		
		final Expression inputExpression = fieldValue(rules.get(0), Expression.class);
		Assert.assertEquals(RulesConfiguration.SPEL_READ_FILENAME, inputExpression.getExpressionString());
		
		final Expression processExpression = fieldValue(rules.get(1), Expression.class);
		Assert.assertEquals(RulesConfiguration.SPEL_PROCESS_EXCHANGE_RATE_ITEM, processExpression.getExpressionString());
		
		final Expression outputExpression = fieldValue(rules.get(2), Expression.class);
		Assert.assertEquals(RulesConfiguration.SPEL_SAVE_ITEM, outputExpression.getExpressionString());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public final void batchProcessor() {
		
		final BatchProcessorImpl batchProcessor = rulesConfiguration.batchProcessor(Arrays.asList(rulesEngine));
		final Collection<Map<String,RulesEngine>> results = new ArrayList<>();
		ReflectionUtils.doWithFields(batchProcessor.getClass(), field -> results.add( (Map<String, RulesEngine>) ReflectionTestUtils.getField(batchProcessor, field.getName())), field -> field.getType().equals(Map.class));
		Assert.assertEquals(1, results.size());
		
		final Map<String,RulesEngine> result = results.stream().findAny().get();
		Assert.assertEquals(1, result.size());
	    Assert.assertEquals(RULES_ENGINE_NAME, result.keySet().stream().findAny().get());
	    Assert.assertEquals(rulesEngine, result.values().stream().findAny().get());
	}
	
	@Test
	public final void beanFactoryPostProcessor() {
		final ApplicationContextAware applicationContextAware = rulesConfiguration.commandlineProcessor();
		final Collection<Class<?>> results = new ArrayList<>();
		ReflectionUtils.doWithFields(applicationContextAware.getClass(), field -> results.add((Class<?>) ReflectionTestUtils.getField(applicationContextAware, field.getName())), field -> field.getType().equals(Class.class));
		Assert.assertEquals(BatchProcessorImpl.class, DataAccessUtils.requiredSingleResult(results));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public final void ecceptionTranslator() {
		final ExceptionTranslationBuilder<Void, InputStreamReader> builder = rulesConfiguration.exceptionTranslationBuilder();
		final Collection<Entry<?,?>> results = new HashSet<>();
		ReflectionUtils.doWithFields(builder.getClass(), field -> results.addAll((Collection<Entry<?,?>>) ReflectionTestUtils.getField(builder, field.getName())), field -> field.getType().equals(Collection.class));
		final Entry<?,?> result = DataAccessUtils.requiredSingleResult(results);
		Assert.assertEquals(ResourceAccessException.class, result.getKey());
		Assert.assertEquals(1,  ((Class<?>[])result.getValue()).length);
		Assert.assertEquals(IOException.class, ((Class<?>[])result.getValue())[0]);
	}
	
	@Test
	public void  annotationsAware() {
		Assert.assertTrue(RulesConfiguration.class.isAnnotationPresent(Configuration.class));
		Assert.assertTrue(RulesConfiguration.class.isAnnotationPresent(ImportResource.class));
		final int[]counters =  {0};
		ReflectionUtils.doWithMethods(RulesConfiguration.class, method -> {
			counters[0]=counters[0]+1;
						
			Assert.assertTrue(method.isAnnotationPresent(Bean.class));
			
			if(  method.getReturnType().equals(BatchProcessorImpl.class)||method.getReturnType().equals(ApplicationContextAware.class)) {
				Assert.assertEquals(method.getReturnType().equals(BeanFactoryPostProcessor.class), Modifier.isStatic(method.getModifiers()));
				if(method.isAnnotationPresent(Scope.class)) {
					Assert.assertEquals(SCOPE_SINGLETON, method.getAnnotation(Scope.class).value());
				}
			} else {
				Assert.assertFalse(Modifier.isStatic(method.getModifiers()));
				Assert.assertTrue(method.isAnnotationPresent(Scope.class));
				Assert.assertEquals(SCOPE_PROTOTYPE, method.getAnnotation(Scope.class).value());
			}
		}, method -> method.getDeclaringClass().equals(RulesConfiguration.class)&& (!Modifier.isStatic(method.getModifiers()) || method.getReturnType().equals(BeanFactoryPostProcessor.class) ));
		
		Assert.assertEquals(7, counters[0]);
	}
	
	

}
