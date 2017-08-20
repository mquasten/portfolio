package de.mq.portfolio.support;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;


public class GatewayParameter2ExchangeRateConverterTest {
	
	
	private static final String CURRENCY_USD = "USD";

	private static final String CURRENCY_EUR = "EUR";

	private final Converter<GatewayParameter, ExchangeRate> converter  = new GatewayParameter2ExchangeRateConverterImpl();
	
	private GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	
	@Before
	public final void setup() {
		Mockito.when(gatewayParameter.code()).thenReturn(String.format("%s-%s", CURRENCY_EUR, CURRENCY_USD));
		Mockito.when(gatewayParameter.gateway()).thenReturn(Gateway.CentralBankExchangeRates);
	}
	
	
	@Test
	public final void convert() {
		final ExchangeRate exchangeRate = converter.convert(gatewayParameter);
		
		Assert.assertEquals(CURRENCY_EUR, exchangeRate.source());
		Assert.assertEquals(CURRENCY_USD, exchangeRate.target());
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public final void convertWrongIds() {
		Mockito.when(gatewayParameter.code()).thenReturn("NYSE:JNJ");
		Mockito.when(gatewayParameter.gateway()).thenReturn(Gateway.GoogleRateHistory);
		 converter.convert(gatewayParameter);
	}
	

}
