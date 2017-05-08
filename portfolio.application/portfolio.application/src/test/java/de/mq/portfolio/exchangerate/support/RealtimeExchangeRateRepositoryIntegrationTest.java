package de.mq.portfolio.exchangerate.support;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.exchangerate.ExchangeRate;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })
@TestPropertySource(properties={"realtime.exchangerates.url=" + RealtimeExchangeRateRepositoryTest.EXCHANGERATES_URL,"realtime.exchangerates.dateformat=" + RealtimeExchangeRateRepositoryTest.EXCHANGERATES_DATEFORMAT})
@Ignore
public class RealtimeExchangeRateRepositoryIntegrationTest {
	
	@Autowired
	private RealtimeExchangeRateRepository realtimeExchangeRateRepository; 
	
	@Test
	public final void exchangeRates() {
		final Collection<ExchangeRate> exchangeRates = Arrays.asList(new ExchangeRateImpl("EUR", "USD"), new ExchangeRateImpl("EUR", "GBP"),  new ExchangeRateImpl("USD", "GBP"));
		final Collection<ExchangeRate> results = realtimeExchangeRateRepository.exchangeRates(exchangeRates);
		Assert.assertEquals(3, results.size());
		
		System.out.println(results);
	}

}
