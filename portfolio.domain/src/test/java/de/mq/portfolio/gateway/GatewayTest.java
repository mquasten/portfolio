package de.mq.portfolio.gateway;

import org.junit.Assert;
import org.junit.Test;



public class GatewayTest {
	
	private static final String ID_PATTERN = "%s-%s";
	private static String CODE = "SAP.DE";
	
	
	private static String EXCHANGE_RATE  = "EUR-USD";
	
	@Test
	public final void code() {
		Assert.assertEquals(CODE, Gateway.code(String.format(ID_PATTERN, CODE, Gateway.ArivaRateHistory.id())));
		
		Assert.assertEquals(EXCHANGE_RATE, Gateway.code(String.format(ID_PATTERN, EXCHANGE_RATE, Gateway.CentralBankExchangeRates.id())));
	}
	
	
	@Test
	public final void gateway() {
		Assert.assertEquals(Gateway.ArivaRateHistory, Gateway.gateway(String.format(ID_PATTERN, CODE,Gateway.ArivaRateHistory.id() )));
		
		Assert.assertEquals(Gateway.CentralBankExchangeRates, Gateway.gateway(String.format(ID_PATTERN, EXCHANGE_RATE, Gateway.CentralBankExchangeRates.id() )));
	}

}
