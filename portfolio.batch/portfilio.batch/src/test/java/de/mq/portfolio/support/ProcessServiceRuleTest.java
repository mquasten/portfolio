package de.mq.portfolio.support;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.TimeCourse;
import junit.framework.Assert;

public class ProcessServiceRuleTest {
	
	private static final String SPEL_EXPRESSION = "spel";

	private final Expression expression = Mockito.mock(Expression.class);
	private SimpleCSVInputServiceImpl<?> target = Mockito.mock(SimpleCSVInputServiceImpl.class);
	private final AbstractServiceRule<?> rule = new ProcessServiceRuleImpl<>(target,SPEL_EXPRESSION);
	private final ArgumentCaptor<StandardEvaluationContext> contextCaptor = ArgumentCaptor.forClass(StandardEvaluationContext.class);
	
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	private final TimeCourse inputTimeCourse = Mockito.mock(TimeCourse.class);
	
	private final Map<String,Object> parameters = new HashMap<>();
	
	@Before
	public final void setup() {
		final Optional<Field> expressionField = Arrays.asList(AbstractServiceRule.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Expression.class)).collect(Collectors.toSet()).stream().findAny();
		Assert.assertTrue(expressionField.isPresent());
		expressionField.get().setAccessible(true);
		final Expression existing = (Expression) ReflectionUtils.getField(expressionField.get(), rule);
		Assert.assertEquals(SPEL_EXPRESSION, existing.getExpressionString());
		
		ReflectionUtils.setField(expressionField.get(), rule, expression);
		
		parameters.clear();
		parameters.put(AbstractServiceRule.ITEMS_PARAMETER, Arrays.asList(inputTimeCourse));
	}
	
	@Test
	public final void action() {
		
		
		Mockito.when(expression.getValue(contextCaptor.capture())).thenReturn(timeCourse);
	
	
		rule.action(parameters);
		
		
		Assert.assertEquals(Arrays.asList(timeCourse), parameters.get(AbstractServiceRule.ITEMS_PARAMETER));
		
		Assert.assertEquals(target, contextCaptor.getValue().getRootObject().getValue());
		
		Assert.assertEquals(Arrays.asList(inputTimeCourse), contextCaptor.getValue().lookupVariable(AbstractServiceRule.ITEMS_PARAMETER));
		Assert.assertEquals(inputTimeCourse, contextCaptor.getValue().lookupVariable(ProcessServiceRuleImpl.ITEM_PARAMETER));
		
		
		
		
	}
	
	@Test
	public final void actionSpelReturnNull() {
		Mockito.when(expression.getValue(contextCaptor.capture())).thenReturn(null);
		
		rule.action(parameters);
		
		Assert.assertEquals(Arrays.asList(inputTimeCourse), parameters.get(AbstractServiceRule.ITEMS_PARAMETER));
		
		Assert.assertEquals(target, contextCaptor.getValue().getRootObject().getValue());
		
		Assert.assertEquals(Arrays.asList(inputTimeCourse), contextCaptor.getValue().lookupVariable(AbstractServiceRule.ITEMS_PARAMETER));
		Assert.assertEquals(inputTimeCourse, contextCaptor.getValue().lookupVariable(ProcessServiceRuleImpl.ITEM_PARAMETER));
		
	}

}
