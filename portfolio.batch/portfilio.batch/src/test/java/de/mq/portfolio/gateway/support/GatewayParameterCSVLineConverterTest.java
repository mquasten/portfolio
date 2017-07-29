package de.mq.portfolio.gateway.support;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;


public class GatewayParameterCSVLineConverterTest {
	
	
	private static final String URL = "http://www.ariva.de/quote/historic/historic.csv?secu={shareId}&boerse_id={stockExchangeId}&clean_split=1&clean_payout=0&clean_bezug=1&min_time={startDate}&max_time={endDate}&trenner={delimiter}&go=Download";
	private static final String CODE = "KO";
	private static final String PARAMETER_SPEL =  "{shareId:'400', stockExchangeId:'21', startDate:oneYearBack(germanYearToDayDateFormat), endDate:oneDayBack(germanYearToDayDateFormat), delimiter:'|'}";
	
	@Test
	public final void convert() {
		final Converter<String[], GatewayParameter> gatewayParameterArivaRateHistoryCSVLineConverter =  new GatewayParameterCSVLineConverterImpl();
		final String[] columns = new String[] {CODE,Gateway.ArivaRateHistory.name(),URL,PARAMETER_SPEL};

		final GatewayParameter gatewayParameter = gatewayParameterArivaRateHistoryCSVLineConverter.convert(columns);
		Assert.assertEquals(Gateway.ArivaRateHistory, gatewayParameter.gateway());
		Assert.assertEquals(CODE, gatewayParameter.code());
		Assert.assertEquals(URL, gatewayParameter.urlTemplate());
		
		Assert.assertEquals(PARAMETER_SPEL, ReflectionTestUtils.getField(gatewayParameter, "parameterExpression"));
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void convertWrongColumnSize() {
		final Converter<String[], GatewayParameter> gatewayParameterArivaRateHistoryCSVLineConverter =  new GatewayParameterCSVLineConverterImpl();
		gatewayParameterArivaRateHistoryCSVLineConverter.convert(new String[] {});
	}
	
	
	
	
	

}