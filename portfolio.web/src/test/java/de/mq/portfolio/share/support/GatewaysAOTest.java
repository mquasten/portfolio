package de.mq.portfolio.share.support;


import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

public class GatewaysAOTest {
	
	private static final String PORTFOLIO_ID = UUID.randomUUID().toString();
	private static final Date DATE = new Date();
	private static final String CODE = "code";
	private static final String MESSAGE = "message";
	private final GatewaysAO gatewaysAO = new GatewaysAO();
	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	
	
	@Test
	public final void message() {
		Assert.assertNull(gatewaysAO.getMessage());
		gatewaysAO.setMessage(MESSAGE);
		Assert.assertEquals(MESSAGE, gatewaysAO.getMessage());
	}
	@Test
	public final  void  code() {
		Assert.assertNull(gatewaysAO.getCode());
		gatewaysAO.setCode(CODE);
		Assert.assertEquals(CODE, gatewaysAO.getCode());
	}
	@Test
	public final void lastUpdate() {
		Arrays.asList(Gateway.values()).forEach(gateway -> Assert.assertNull(gatewaysAO.lastUpdate(gateway)));
		gatewaysAO.assign(Arrays.asList(new AbstractMap.SimpleImmutableEntry<Gateway, Date>(Gateway.GoogleRateHistory, DATE)));
		Assert.assertEquals(DATE, gatewaysAO.lastUpdate(Gateway.GoogleRateHistory));
		Arrays.asList(Gateway.values()).stream().filter(gateway -> gateway != Gateway.GoogleRateHistory).forEach(gateway-> Assert.assertNull(gatewaysAO.lastUpdate(gateway)));
	}
	
	@Test
	public final void gatewayParameters() {
		Assert.assertTrue(gatewaysAO.getGatewayParameters().isEmpty());
		gatewaysAO.setGatewayParameters(Arrays.asList(gatewayParameter));
		Assert.assertEquals(Arrays.asList(gatewayParameter), gatewaysAO.getGatewayParameters());
	}
	
	@Test
	public final void isExchangeRate() {
		Assert.assertFalse(gatewaysAO.isExchangeRate());
		gatewaysAO.setCode("EUR-USD");
		Assert.assertTrue(gatewaysAO.isExchangeRate());
	}
	
	@Test
	public final void isPortfolio() {
		Assert.assertFalse(gatewaysAO.isPortfolio());
		gatewaysAO.setPortfolioId(PORTFOLIO_ID);
		Assert.assertTrue(gatewaysAO.isPortfolio());
	}
	
	@Test
	public final void portfolioId() {
		Assert.assertNull(gatewaysAO.getPortfolioId());
		gatewaysAO.setPortfolioId(PORTFOLIO_ID);
		Assert.assertEquals(PORTFOLIO_ID, gatewaysAO.getPortfolioId());
	}
	

}
