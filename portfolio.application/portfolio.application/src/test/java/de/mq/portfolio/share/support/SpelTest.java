package de.mq.portfolio.share.support;

import java.util.Map;

import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelTest {
	
	@Test
	public final void simpleMap() throws NoSuchMethodException, SecurityException {
		
		final ExpressionParser parser = new SpelExpressionParser();

		// Inventions Array
		final StandardEvaluationContext context = new StandardEvaluationContext(new HistoryDateUtil());

				
		//System.out.println(mapOfMaps);
		@SuppressWarnings("unchecked")
		Map<String,String> map = (Map<String,String>) parser.parseExpression("{date: oneYearBack(googleDateFormat) , trenner: '|' , query: 'NYSE:KO'}").getValue(context);
		
		System.out.println(map);
		
	}

}


