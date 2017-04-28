package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.exchangerate.ExchangeRate;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })
@Ignore
public class RealtimeExchangeRateRepositoryIntegrationTest {
	
	@Autowired
	private RealtimeExchangeRateRepository realtimeExchangeRateRepository; 
	
	@Test
	public final void exchangeRates() {
		final Collection<ExchangeRate> exchangeRates = realtimeExchangeRateRepository.exchangeRates(null);
		Assert.assertEquals(3, exchangeRates.size());
		
		System.out.println(exchangeRates);
	}

}
