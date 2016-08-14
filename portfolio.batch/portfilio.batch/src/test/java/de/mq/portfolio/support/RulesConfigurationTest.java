package de.mq.portfolio.support;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.expression.Expression;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.batch.Rule;
import de.mq.portfolio.batch.RulesEngine;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.support.SharesCSVLineConverterImpl;
import junit.framework.Assert;

public class RulesConfigurationTest {
	
	private static final String SCOPE_PROTOTYPE = "prototype";

	private static final String TARGET_FIELD = "target";

	private final RulesConfiguration rulesConfiguration  = new RulesConfiguration();
	
	private final ShareService shareService = Mockito.mock(ShareService.class);
	
	private final RulesEngineBuilder rulesEngineBuilder = Mockito.mock(RulesEngineBuilder.class);
	private final ArgumentCaptor<Rule> ruleCapor = ArgumentCaptor.forClass(Rule.class);
	private final RulesEngine rulesEngine = Mockito.mock(RulesEngine.class);
	
	private final  ExchangeRateService  exchangeRateService = Mockito.mock(ExchangeRateService.class);
	
	@Before
	public final void setup() {
		Mockito.when(rulesEngineBuilder.withRule(ruleCapor.capture())).thenReturn(rulesEngineBuilder);
		Mockito.when(rulesEngineBuilder.build()).thenReturn(rulesEngine);
	}
	
	@Test
	public void rulesEngineBuilder() {
		Assert.assertEquals(SimpleRuleEngineBuilderImpl.class,rulesConfiguration.rulesEngineBuilder().getClass());
	}
	
	@Test
	public void importShares() {
		Assert.assertEquals(rulesEngine, rulesConfiguration.importShares(shareService, rulesEngineBuilder));
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
	
	@Test
	public void  annotationsAware() {
		Assert.assertTrue(RulesConfiguration.class.isAnnotationPresent(Configuration.class));
		Assert.assertTrue(RulesConfiguration.class.isAnnotationPresent(ImportResource.class));
		
		ReflectionUtils.doWithMethods(RulesConfiguration.class, method -> {
			
			Assert.assertTrue(method.isAnnotationPresent(Bean.class));
			Assert.assertTrue(method.isAnnotationPresent(Scope.class));
			Assert.assertEquals(SCOPE_PROTOTYPE, method.getAnnotation(Scope.class).value());
		}, method -> method.getDeclaringClass().equals(RulesConfiguration.class)&& ! Modifier.isStatic(method.getModifiers()));
	}

}
