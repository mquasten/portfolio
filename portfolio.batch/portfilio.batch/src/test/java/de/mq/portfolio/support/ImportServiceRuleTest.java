package de.mq.portfolio.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.TimeCourse;
import org.junit.Assert;

public class ImportServiceRuleTest {

	private static final String SPEL_EXPRESSION = "spel";
	private static final String PARAM_VALUE = "paramValue";
	private static final String PARAM_NAME = "paramName";
	private final Expression expression = Mockito.mock(Expression.class);
	private SimpleCSVInputServiceImpl<?> target = Mockito.mock(SimpleCSVInputServiceImpl.class);
	private final AbstractServiceRule<?> rule = new ImportServiceRuleImpl<>(target, SPEL_EXPRESSION); 
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	private final Collection<TimeCourse> timeCourses = Arrays.asList(timeCourse);
	private final ArgumentCaptor<StandardEvaluationContext> contextCaptor = ArgumentCaptor.forClass(StandardEvaluationContext.class);

	
	@Test
	public  void test() {
		final Map<String,Object> parameters = new HashMap<>();
		parameters.put(PARAM_NAME, PARAM_VALUE);
		Mockito.when(expression.getValue(contextCaptor.capture())).thenReturn(timeCourses);
		
		final Optional<Expression> existingExpression = Arrays.asList(AbstractServiceRule.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Expression.class)).map(field ->(Expression) ReflectionTestUtils.getField(rule, field.getName())).collect(Collectors.toSet()).stream().findAny();
		Assert.assertTrue(existingExpression.isPresent());
		Assert.assertEquals(SPEL_EXPRESSION, existingExpression.get().getExpressionString());
		
		ReflectionUtils.doWithFields(AbstractServiceRule.class, field -> ReflectionTestUtils.setField(rule, field.getName(), expression), field -> field.getType().equals(Expression.class));
		
		rule.action(parameters);
		
		Assert.assertEquals(2, parameters.size());
		Assert.assertTrue(parameters.containsKey(AbstractServiceRule.ITEMS_PARAMETER));
		Assert.assertEquals(timeCourses, parameters.get(AbstractServiceRule.ITEMS_PARAMETER));
		
		Assert.assertEquals(target, contextCaptor.getValue().getRootObject().getValue());
		Assert.assertEquals(PARAM_VALUE, contextCaptor.getValue().lookupVariable(PARAM_NAME));
	}
	
	
	
}
