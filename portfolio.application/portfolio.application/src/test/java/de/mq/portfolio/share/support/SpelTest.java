package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelTest {

	private static final String CURRENCY_USD = "USD";
	private static final String CURRENCY_EUR = "EUR";
	private static final String IDS_VARIABLE_NAME = "ids";
	private static final String DELIMITER_PARAM_NAME = "trenner";
	private static final Object DELIMITER_PARAM_VALUE = "|";
	private static String DATE_PARAM_NAME = "date";
	private static String QUERY_PARAM_NAME = "query";
	private static final Object QUERY_PARAM_VALUE = "NYSE:KO";

	private static String SOURCE_CURRENCY_PARAM_NAME = "sourceCurrency";
	private static String TARGET_CURRENCY_PARAM_NAME = "targetCurrency";

	private final HistoryDateUtil historyDateUtil = new HistoryDateUtil();

	private final ExpressionParser parser = new SpelExpressionParser();

	private final StandardEvaluationContext context = new StandardEvaluationContext(historyDateUtil);

	@Test
	public final void simpleMap() throws NoSuchMethodException, SecurityException {

		// System.out.println(mapOfMaps);
		@SuppressWarnings("unchecked")
		final Map<String, String> map = (Map<String, String>) parser.parseExpression(String.format("{%s: oneYearBack(googleDateFormat) , %s: '%s' , %s: '%s'}", DATE_PARAM_NAME, DELIMITER_PARAM_NAME, DELIMITER_PARAM_VALUE, QUERY_PARAM_NAME, QUERY_PARAM_VALUE)).getValue(context);

		Assert.assertEquals(3, map.size());
		Assert.assertEquals(DELIMITER_PARAM_VALUE, map.get(DELIMITER_PARAM_NAME));
		Assert.assertEquals(QUERY_PARAM_VALUE, map.get(QUERY_PARAM_NAME));

		Assert.assertEquals(historyDateUtil.oneYearBack(historyDateUtil.getGoogleDateFormat()), map.get(DATE_PARAM_NAME));

	}

	@Test
	public final void array() throws NoSuchMethodException, SecurityException {

		context.setVariable(IDS_VARIABLE_NAME, Arrays.asList(CURRENCY_EUR, CURRENCY_USD));

		@SuppressWarnings("unchecked")
		final Map<String, String> map = (Map<String, String>) parser.parseExpression(String.format("{%s:#%s[0], %s:#%s[1]}", SOURCE_CURRENCY_PARAM_NAME, IDS_VARIABLE_NAME, TARGET_CURRENCY_PARAM_NAME, IDS_VARIABLE_NAME)).getValue(context);

		System.out.println(map);
		Assert.assertEquals(2, map.size());
		Assert.assertEquals(CURRENCY_EUR, map.get(SOURCE_CURRENCY_PARAM_NAME));
		Assert.assertEquals(CURRENCY_USD, map.get(TARGET_CURRENCY_PARAM_NAME));

	}

}
