
package de.mq.portfolio.support;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;


public class AbstractServiceRuleTest {
	

	private static final String SPEL_EXPRESSION = "spel";
	private static final String PARAM_VALUE = "paramValue";
	private static final String PARAM_NAME = "paramName";

	private final Expression expression = Mockito.mock(Expression.class);
	private final AbstractServiceRule<?> abstractServiceRule = Mockito.mock(AbstractServiceRule.class, Mockito.CALLS_REAL_METHODS );
	private final Map<String,Object> parameters = new HashMap<>();
	
	private final  ShareService shareService = Mockito.mock(ShareService.class);
	
	private final Share share = Mockito.mock(Share.class);
	
	private final ArgumentCaptor<StandardEvaluationContext> contextCaptor = ArgumentCaptor.forClass(StandardEvaluationContext.class);
	
	@Before
	public final void setup() {
		ReflectionUtils.doWithFields(AbstractServiceRule.class, field -> ReflectionTestUtils.setField(abstractServiceRule, field.getName(), expression), field -> field.getType().equals(Expression.class));
		ReflectionTestUtils.setField(abstractServiceRule, "target", shareService);
		
		Mockito.when(expression.getExpressionString()).thenReturn(SPEL_EXPRESSION);
		parameters.put(PARAM_NAME, PARAM_VALUE);
	}
	
	@Test
	public final void action() {
		
		Mockito.when(expression.getValue(contextCaptor.capture())).thenReturn(share);
		
		Assert.assertEquals(share, abstractServiceRule.executeEl(parameters));
		
		
		
		Assert.assertEquals(shareService, contextCaptor.getValue().getRootObject().getValue());
		Assert.assertEquals(PARAM_VALUE, contextCaptor.getValue().lookupVariable(PARAM_NAME));
	}
	
	@Test
	public final void evaluate() {
		Assert.assertTrue(abstractServiceRule.evaluate());
	}
	
	@Test
	public final void name() {
		Assert.assertEquals(String.format(AbstractServiceRule.NAME_PATTERN, shareService.getClass().getSimpleName(), SPEL_EXPRESSION ), abstractServiceRule.getName());
	}

	@Test
	public final void execute() {
		
		abstractServiceRule.execute(parameters);
		Mockito.verify(abstractServiceRule).action(parameters);
	}
	
	@Test
	public final void hash() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final AbstractServiceRule<?> rule = new ImportServiceRuleImpl<>(shareService, SPEL_EXPRESSION);
		Assert.assertEquals(String.format(AbstractServiceRule.NAME_PATTERN, shareService.getClass().getSimpleName(), SPEL_EXPRESSION).hashCode() + rule.getClass().hashCode(), rule.hashCode());
	}
	
	@Test
	public final void equals() {
		final AbstractServiceRule<?> rule = new ImportServiceRuleImpl<>(shareService, SPEL_EXPRESSION);
		Assert.assertFalse(rule.equals(new Date()));
		Assert.assertTrue(rule.equals(new ImportServiceRuleImpl<>(shareService, SPEL_EXPRESSION)));
		Assert.assertFalse(rule.equals(new ImportServiceRuleImpl<>(shareService, "otherSpel")));
		Assert.assertFalse(rule.equals(new ProcessServiceRuleImpl<>(shareService, SPEL_EXPRESSION)));
		
	}
		
	
	
}
