package de.mq.portfolio.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.batch.Rule;
import de.mq.portfolio.batch.RulesEngine;
import org.junit.Assert;

public class RulesEngineBuilderTest {
	
	private static final String MATCH_ALL_PATTERN = ".*";
	private static final String RULE_PATTERN = ".*rule.*";
	private static final String NAME = "importExchangeRates";
	private static final String SKIP_ON_FIRST_APPLIED_PATTERN = ".*first.*";
	private static final String CONTINUE_ON_FAILURE_PATTERN = ".*fail.*";
	private final RulesEngineBuilder rulesEngineBuilder = new SimpleRuleEngineBuilderImpl();
	private final Rule rule = Mockito.mock(Rule.class);
	
	@Test
	public final void withRule() {
		Assert.assertEquals(rulesEngineBuilder, rulesEngineBuilder.withRule(rule));
		
		final Collection<?> results = values(rulesEngineBuilder,Collection.class).values();
		Assert.assertEquals(1, results.size());
		Assert.assertTrue(results.stream().findAny().isPresent());
		Assert.assertEquals(Arrays.asList(rule), results.stream().findAny().get());
	}
	
	@Test
	public final void withRules() {
		Assert.assertEquals(rulesEngineBuilder, rulesEngineBuilder.withRules(Arrays.asList(rule)));
		
		final Collection<?> results = values(rulesEngineBuilder, Collection.class).values();
		Assert.assertEquals(1, results.size());
		Assert.assertTrue(results.stream().findAny().isPresent());
		Assert.assertEquals(Arrays.asList(rule), results.stream().findAny().get());
	}
	
	@Test
	public final void withContinueOnFailure() {
		Assert.assertTrue(valueLikeName(rulesEngineBuilder, boolean.class, CONTINUE_ON_FAILURE_PATTERN));
		Assert.assertEquals(rulesEngineBuilder, rulesEngineBuilder.withContinueOnFailure()); 
		Assert.assertFalse(valueLikeName(rulesEngineBuilder, boolean.class, CONTINUE_ON_FAILURE_PATTERN));
		
		
	}
	
	@Test
	public final void withSkipOnFirstAppliedRule() {
		Assert.assertFalse(valueLikeName(rulesEngineBuilder, boolean.class, SKIP_ON_FIRST_APPLIED_PATTERN));
		Assert.assertEquals(rulesEngineBuilder, rulesEngineBuilder.withSkipOnFirstAppliedRule()); 
		Assert.assertTrue(valueLikeName(rulesEngineBuilder, boolean.class, SKIP_ON_FIRST_APPLIED_PATTERN));
		
		
	}
	
	@Test
	public final void withName() {
		Assert.assertEquals(rulesEngineBuilder, rulesEngineBuilder.withName(NAME));
		Assert.assertEquals(NAME, valueLikeName(rulesEngineBuilder, String.class, MATCH_ALL_PATTERN));
	}

	private <T> T  valueLikeName(final Object target, final Class<T> type, final String regex ) {
		final Collection<T> results =  values(target, type).entrySet().stream().filter(entry -> entry.getKey().toLowerCase().matches(regex.toLowerCase())).map(entry -> entry.getValue()).collect(Collectors.toSet());
		Assert.assertEquals(1, results.size());
		return results.stream().findAny().get();
	}

	@SuppressWarnings("unchecked")
	private <T> Map<String,T> values(final Object target, Class<T> fieldType) {
		final Map<String, T> results = new HashMap<>();
		ReflectionUtils.doWithFields(target.getClass(), field -> results.put(field.getName(), (T) ReflectionTestUtils.getField(target, field.getName())), field -> field.getType().equals(fieldType));
		
		return results;
	}
	
	@Test
	public final void build() {
		final RulesEngine rulesEngine = rulesEngineBuilder.withRule(rule).withContinueOnFailure().withName(NAME).withSkipOnFirstAppliedRule().build();
		
		final Collection<?> rules = valueLikeName(rulesEngine, Collection.class, RULE_PATTERN);		
		Assert.assertEquals(Arrays.asList(rule), rules);
		Assert.assertFalse(valueLikeName(rulesEngine, boolean.class, CONTINUE_ON_FAILURE_PATTERN));
		Assert.assertTrue(valueLikeName(rulesEngine, boolean.class, SKIP_ON_FIRST_APPLIED_PATTERN));
		Assert.assertEquals(NAME, valueLikeName(rulesEngine, String.class, MATCH_ALL_PATTERN));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void buildNoRuleExists() {
		rulesEngineBuilder.build();
	}

}
