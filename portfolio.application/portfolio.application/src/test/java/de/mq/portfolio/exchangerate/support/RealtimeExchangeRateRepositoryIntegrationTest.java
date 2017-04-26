package de.mq.portfolio.exchangerate.support;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
@ActiveProfiles("yahoo")
@Ignore
public class RealtimeExchangeRateRepositoryIntegrationTest {
	
	@Autowired
	private RealtimeExchangeRateRepository realtimeExchangeRateRepository; 
	
	@Test
	public final void exchangeRates() {
		realtimeExchangeRateRepository.exchangeRates(null);
	}

}
