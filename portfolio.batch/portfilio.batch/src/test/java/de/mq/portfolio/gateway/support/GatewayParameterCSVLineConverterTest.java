package de.mq.portfolio.gateway.support;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.support.RulesConfigurationConstants;

public class GatewayParameterCSVLineConverterTest {
	
	private static final String EXCHANGE_ID = "21";
	private static final String SHARE_ID = "400";
	private static final String URL = "http://www.ariva.de/quote/historic/historic.csv?secu={shareId}&boerse_id={stockExchangeId}&clean_split=1&clean_payout=0&clean_bezug=1&min_time={startDate}&max_time={endDate}&trenner={delimiter}&go=Download";
	private static final String CODE = "KO";
	
	
	@Test
	public final void convert() {
		final Converter<String[], GatewayParameter> gatewayParameterArivaRateHistoryCSVLineConverter =  new GatewayParameterCSVLineConverterImpl(RulesConfigurationConstants.SHARE_ID_PARAMETER_NAME, RulesConfigurationConstants.STOCK_EXCHANGE_ID_PARAMETER_NAME);
		final String[] columns = new String[] {CODE,Gateway.ArivaRateHistory.name(),URL,SHARE_ID,EXCHANGE_ID};

		final GatewayParameter gatewayParameter = gatewayParameterArivaRateHistoryCSVLineConverter.convert(columns);
		Assert.assertEquals(Gateway.ArivaRateHistory, gatewayParameter.gateway());
		Assert.assertEquals(CODE, gatewayParameter.code());
		Assert.assertEquals(URL, gatewayParameter.urlTemplate());
		Assert.assertTrue(gatewayParameter.parameters().containsKey(RulesConfigurationConstants.SHARE_ID_PARAMETER_NAME));
		Assert.assertTrue(gatewayParameter.parameters().containsKey(RulesConfigurationConstants.STOCK_EXCHANGE_ID_PARAMETER_NAME));
		
		Assert.assertEquals(SHARE_ID, gatewayParameter.parameters().get(RulesConfigurationConstants.SHARE_ID_PARAMETER_NAME));
		Assert.assertEquals(EXCHANGE_ID, gatewayParameter.parameters().get(RulesConfigurationConstants.STOCK_EXCHANGE_ID_PARAMETER_NAME));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void convertWrongColumnSize() {
		final Converter<String[], GatewayParameter> gatewayParameterArivaRateHistoryCSVLineConverter =  new GatewayParameterCSVLineConverterImpl();
		gatewayParameterArivaRateHistoryCSVLineConverter.convert(new String[] {});
	}
	
	
	
	
	

}