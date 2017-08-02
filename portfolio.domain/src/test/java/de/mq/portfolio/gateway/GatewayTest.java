package de.mq.portfolio.gateway;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import de.mq.portfolio.gateway.Gateway.GatewayGroup;

public class GatewayTest {

	private static final String ID_PATTERN = "%s-%s";
	private static final String FILENAME_FORMAT = ID_PATTERN+".%s";
	private static String CODE = "SAP.DE";

	private static String EXCHANGE_RATE = "EUR-USD";

	@Test
	public final void code() {
		Assert.assertEquals(CODE, Gateway.code(String.format(ID_PATTERN, CODE, Gateway.ArivaRateHistory.id())));

		Assert.assertEquals(EXCHANGE_RATE, Gateway.code(String.format(ID_PATTERN, EXCHANGE_RATE, Gateway.CentralBankExchangeRates.id())));
	}

	@Test
	public final void gateway() {
		Assert.assertEquals(Gateway.ArivaRateHistory, Gateway.gateway(String.format(ID_PATTERN, CODE, Gateway.ArivaRateHistory.id())));

		Assert.assertEquals(Gateway.CentralBankExchangeRates, Gateway.gateway(String.format(ID_PATTERN, EXCHANGE_RATE, Gateway.CentralBankExchangeRates.id())));
	}

	@Test
	public final void idWithKey() {
		Assert.assertEquals(String.format(ID_PATTERN, CODE, Gateway.ArivaRateHistory.id()), Gateway.ArivaRateHistory.id(CODE));

		Assert.assertEquals(String.format(ID_PATTERN, EXCHANGE_RATE, Gateway.ArivaRateHistory.id()), Gateway.ArivaRateHistory.id(EXCHANGE_RATE.split(Gateway.DELIMITER)));
	}

	@Test
	public final void gatewayOnly() {
		Assert.assertEquals(Gateway.ArivaRateHistory, Gateway.gateway(Gateway.ArivaRateHistory.id()));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void gatewayInvalid() {
		try {
			Gateway.gateway(String.format(ID_PATTERN, CODE, ""));
		} catch (final IllegalArgumentException ia) {
			Assert.assertTrue(ia.getMessage().endsWith("gatewayId at the end."));
			throw ia;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public final void wrongcode() {
		try {
			Gateway.code(Gateway.ArivaRateHistory.id());
		} catch (IllegalArgumentException ia) {
			Assert.assertTrue(ia.getMessage().endsWith("must contain: " + Gateway.DELIMITER + "."));
			throw ia;
		}

	}

	@Test
	public final void createValues() {
		Arrays.asList(Gateway.values()).forEach(value -> Assert.assertEquals(value, Gateway.valueOf(value.name())));
	}
	
	@Test
	public final void gatewayGroups() {
		Assert.assertEquals(3, (Gateway.GatewayGroup.values().length));
		Assert.assertEquals(GatewayGroup.RateHistory, Gateway.GoogleRateHistory.gatewayGroup());
		Assert.assertEquals(GatewayGroup.RateHistory, Gateway.ArivaRateHistory.gatewayGroup());
		Assert.assertEquals(GatewayGroup.DividendHistory, Gateway.ArivaDividendHistory.gatewayGroup());
		Assert.assertEquals(GatewayGroup.ExchangeRate, Gateway.CentralBankExchangeRates.gatewayGroup());
	}
	
	@Test
	public final void createGatewayGroup() {
		Arrays.asList(GatewayGroup.values()).forEach(value -> Assert.assertEquals(value, GatewayGroup.valueOf(value.name())));
	}
	
	@Test
	public final void downloadName() {
		Assert.assertEquals(String.format(FILENAME_FORMAT, CODE, Gateway.ArivaDividendHistory.id(), "html" ), Gateway.ArivaDividendHistory.downloadName(CODE));
		
		
		Arrays.asList(Gateway.values()).stream().filter(value -> value != Gateway.ArivaDividendHistory).forEach(value -> Assert.assertEquals(String.format(FILENAME_FORMAT, CODE, value.id(), "csv" ),value.downloadName(CODE)));
		
	}
}
