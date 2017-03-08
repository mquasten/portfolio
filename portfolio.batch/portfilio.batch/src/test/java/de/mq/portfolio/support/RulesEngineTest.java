package de.mq.portfolio.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.batch.Rule;
import de.mq.portfolio.batch.RulesEngine;
import org.junit.Assert;

public class RulesEngineTest {
	
	private static final String SUCCESS_RULE_NAME = "successRule";
	private static final String RULE_NAME = "ImportRule";
	private static final String PARAM_VALUE = "exchangeRates.csv";
	private static final String PARAM_KEY = "filename";
	private static final String NAME = "importExchangeRates";
	
	private final Rule rule = Mockito.mock(Rule.class);
	private final Rule successRule = Mockito.mock(Rule.class);
	
	
	private final  RulesEngine rulesEngine = new SimpleRulesEngineImpl(NAME, Arrays.asList(rule), true, false);
	
	private final Map<String,Object> parameters = new HashMap<>();
	
	private final Throwable throwable = Mockito.mock(RuntimeException.class);
	
	@Before
	public final void setup() {
		parameters.put(PARAM_KEY, PARAM_VALUE);
		Mockito.when(rule.evaluate()).thenReturn(true);
		Mockito.when(rule.getName()).thenReturn(RULE_NAME);
		Mockito.when(successRule.evaluate()).thenReturn(true);
		Mockito.when(successRule.getName()).thenReturn(SUCCESS_RULE_NAME);
	}
	
	@Test
	public final void name() {
		Assert.assertEquals(NAME, rulesEngine.name());
	}
	
	
	@Test
	public final void fireRules() {
		rulesEngine.fireRules(parameters);
		
		Mockito.verify(rule).evaluate();
		Mockito.verify(rule).execute(parameters);
		Assert.assertEquals(1, rulesEngine.processed().size());
		Assert.assertTrue(rulesEngine.processed().stream().findAny().isPresent());
		Assert.assertEquals(rule.getName(), rulesEngine.processed().stream().findAny().get());
	}
	
	@Test
	public final void fireRulesEvaluateFalse() {
		Mockito.when(rule.evaluate()).thenReturn(false);
		rulesEngine.fireRules(parameters);
		
		Mockito.verify(rule).evaluate();
		Mockito.verify(rule, Mockito.never()).execute(parameters);
		Assert.assertEquals(0, rulesEngine.processed().size());
	}
	
	@Test
	public final void fireRulesSucks() {
		Mockito.doThrow(throwable).when(rule).execute(parameters);
		rulesEngine.fireRules(parameters);
		
		Mockito.verify(rule).evaluate();
		Mockito.verify(rule).execute(parameters);
		Assert.assertEquals(0, rulesEngine.processed().size());
		Assert.assertEquals(1, rulesEngine.failed().size());
	
		Assert.assertTrue(rulesEngine.failed().stream().findAny().isPresent());
		Assert.assertEquals(rule.getName(), rulesEngine.failed().stream().findAny().get().getKey());
		Assert.assertEquals(throwable, rulesEngine.failed().stream().findAny().get().getValue());
	}
	
	@Test
	public final void fireRulesSucksSkipOnFailureFalse() {
		
		Mockito.doThrow(throwable).when(rule).execute(parameters);
		final  RulesEngine rulesEngine = new SimpleRulesEngineImpl(NAME, Arrays.asList(this.rule,successRule), false, false);
		rulesEngine.fireRules(parameters);
		
		Mockito.verify(rule).evaluate();
		Mockito.verify(rule).execute(parameters);
		Mockito.verify(successRule).evaluate();
		Mockito.verify(successRule).execute(parameters);
		
		Assert.assertEquals(1, rulesEngine.processed().size());
		Assert.assertTrue( rulesEngine.processed().stream().findAny().isPresent());
		Assert.assertEquals(successRule.getName(), rulesEngine.processed().stream().findAny().get());
		
		Assert.assertEquals(1, rulesEngine.failed().size());
		Assert.assertTrue(rulesEngine.failed().stream().findAny().isPresent());
		Assert.assertEquals(throwable, rulesEngine.failed().stream().findAny().get().getValue());
		Assert.assertEquals(rule.getName(), rulesEngine.failed().stream().findAny().get().getKey());
	}
	
	@Test
	public final void fireRulesSkipOnFirstApplied() {
		final  RulesEngine rulesEngine = new SimpleRulesEngineImpl(NAME, Arrays.asList(this.rule,successRule), false, true);
		rulesEngine.fireRules(parameters);
		
		Mockito.verify(rule).evaluate();
		Mockito.verify(rule).execute(parameters);
		Mockito.verify(successRule, Mockito.never()).evaluate();
		Mockito.verify(successRule,  Mockito.never()).execute(parameters);
		
		Assert.assertEquals(1, rulesEngine.processed().size());
		Assert.assertTrue( rulesEngine.processed().stream().findAny().isPresent());
		Assert.assertEquals(rule.getName(), rulesEngine.processed().stream().findAny().get());
	}
	
	

}
