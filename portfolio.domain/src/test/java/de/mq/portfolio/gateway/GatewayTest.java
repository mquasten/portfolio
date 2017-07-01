package de.mq.portfolio.gateway;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class GatewayTest {

	private static final String ID_PATTERN = "%s-%s";
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
}
