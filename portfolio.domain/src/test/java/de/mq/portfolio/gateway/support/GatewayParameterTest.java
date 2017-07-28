package de.mq.portfolio.gateway.support;


import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.share.support.HistoryDateUtil;

public class GatewayParameterTest {

	private static final String URL_TEMPLATE_ARH = "http://www.ariva.de/quote/historic/historic.csv?secu={shareId}&boerse_id={stockExchangeId}&clean_split=1&clean_payout=0&clean_bezug=1&min_time={startDate}&max_time={endDate}&trenner={delimiter}&go=Download";
	private static final String CODE_JNJ = "JNJ";
	private static final String STOCK_EXCHANGE_ID_NYSE = "21";
	private static final String SHARE_ID_JNJ = "412";
	private static final String DELIMITER = "|";
	private static final String PARAM_SHARE_ID = "shareId";
	private static final String PARAM_STOCK_EXCHANGE_ID = "stockExchangeId";
	private static final String PARAM_START_DATE = "startDate";
	private static final String PARAM_END_DATE = "endDate";
	private static final String PARAM_DELIMITER = "delimiter";
	
	private HistoryDateUtil historyDateUtil = new HistoryDateUtil();
	private final String parameterString = String.format("{%s:'%s', %s:'%s', %s:oneYearBack(germanYearToDayDateFormat), %s:oneDayBack(germanYearToDayDateFormat), %s:'%s'}", 
			
			PARAM_SHARE_ID,	SHARE_ID_JNJ, 
			PARAM_STOCK_EXCHANGE_ID,STOCK_EXCHANGE_ID_NYSE, 
			PARAM_START_DATE,PARAM_END_DATE, 
			PARAM_DELIMITER ,DELIMITER);
	private final GatewayParameter gatewayParameter = new GatewayParameterImpl(CODE_JNJ, Gateway.ArivaRateHistory, URL_TEMPLATE_ARH, parameterString);

	@Test
	public final void parameters() {
		final Map<String,String> parameters = gatewayParameter.parameters();
		Assert.assertEquals(5, parameters.size());
		Assert.assertEquals(SHARE_ID_JNJ, parameters.get(PARAM_SHARE_ID));
		Assert.assertEquals(STOCK_EXCHANGE_ID_NYSE, parameters.get(PARAM_STOCK_EXCHANGE_ID));
		Assert.assertEquals(DELIMITER, parameters.get(PARAM_DELIMITER));
		Assert.assertEquals(historyDateUtil.oneYearBack(historyDateUtil.getGermanYearToDayDateFormat()), parameters.get(PARAM_START_DATE));
		Assert.assertEquals(historyDateUtil.oneDayBack(historyDateUtil.getGermanYearToDayDateFormat()), parameters.get(PARAM_END_DATE));
	}

	@Test
	public final void code() {
		Assert.assertEquals(CODE_JNJ, gatewayParameter.code());
	}

	@Test
	public final void gateway() {
		Assert.assertEquals(Gateway.ArivaRateHistory, gatewayParameter.gateway());
	}

	@Test
	public final void urlTemplate() {
		Assert.assertEquals(URL_TEMPLATE_ARH, gatewayParameter.urlTemplate());
	}
	
	@Test
	public final void parameterExpression() {
		Assert.assertEquals(parameterString, ReflectionTestUtils.getField(gatewayParameter, "parameterExpression"));
	}

	@Test
	public final void privateConstructor() {
		Assert.assertNotNull(BeanUtils.instantiateClass(GatewayParameterImpl.class));
	}

	

}
