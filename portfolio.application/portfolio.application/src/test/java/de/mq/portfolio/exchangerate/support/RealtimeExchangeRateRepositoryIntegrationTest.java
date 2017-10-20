package de.mq.portfolio.exchangerate.support;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.GatewayParameterAggregation;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml",  "/mongo-test.xml" })
@TestPropertySource(properties={"realtime.exchangerates.url=" + RealtimeExchangeRateRepositoryTest.EXCHANGERATES_URL,"realtime.exchangerates.dateformat=" + RealtimeExchangeRateRepositoryTest.EXCHANGERATES_DATEFORMAT})
@Ignore
public class RealtimeExchangeRateRepositoryIntegrationTest {
	
	@Autowired
	private RealtimeExchangeRateRepository realtimeExchangeRateRepository; 
	
	@Test
	public final void exchangeRates() {
		@SuppressWarnings("unchecked")
		GatewayParameterAggregation<Collection<ExchangeRate>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
		final Collection<ExchangeRate> exchangeRates = Arrays.asList(new ExchangeRateImpl("EUR", "USD"), new ExchangeRateImpl("EUR", "GBP"),  new ExchangeRateImpl("USD", "GBP"));
		final Collection<ExchangeRate> results = realtimeExchangeRateRepository.exchangeRates(gatewayParameterAggregation);
		
		Assert.assertEquals(3, results.size());
	}

}
