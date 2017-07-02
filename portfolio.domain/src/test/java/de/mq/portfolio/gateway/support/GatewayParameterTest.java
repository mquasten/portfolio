package de.mq.portfolio.gateway.support;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

public class GatewayParameterTest {

	private static final String URL_TEMPLATE_ARH = "http://www.ariva.de/quote/historic/historic.csv?secu={shareId}&boerse_id={stockExchangeId}&clean_split=1&clean_payout=0&clean_bezug=1&min_time={startDate}&max_time={endDate}&trenner={delimiter}&go=Download";
	private static final String CODE_JNJ = "JNJ";
	private static final String STOCK_EXCHANGE_ID_NYSE = "21";
	private static final String SHARE_ID_JNJ = "412";
	private final GatewayParameter gatewayParameter = new GatewayParameterImpl(CODE_JNJ, Gateway.ArivaRateHistory, URL_TEMPLATE_ARH, parameterMap());

	@Test
	public final void parameters() {
		Assert.assertEquals(parameterMap(), gatewayParameter.parameters());
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
	public final void privateConstructor() {
		Assert.assertNotNull(BeanUtils.instantiateClass(GatewayParameterImpl.class));
	}

	private final Map<String, String> parameterMap() {
		final Map<String, String> params = new HashMap<>();
		params.put("shareId", SHARE_ID_JNJ);
		params.put("stockExchangeId", STOCK_EXCHANGE_ID_NYSE);
		return params;
	}

}
