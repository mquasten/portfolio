package de.mq.portfolio.exchangerate.support;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.Data;
import junit.framework.Assert;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
@Ignore
public class ExchangeRateRepositoryIntegrationTest {
	
	@Autowired
	private ExchangeRateRepository exchangeRateRepository; 
	
	private static String URL =  "http://www.bundesbank.de/cae/servlet/StatisticDownload?tsId=BBEX3.D.USD.EUR.BB.AC.000&its_csvFormat=de&its_fileFormat=csv&mode=its"; 
	
	@Test
	public final void exchangeRates() {
		
		
		final Collection<Data> results = exchangeRateRepository.history(URL);
		
		results.forEach(result -> {
			Assert.assertTrue(result.date().after(new GregorianCalendar(1995, 0, 1).getTime()));
			Assert.assertTrue(result.date().before(new Date()));
		
			Assert.assertNotNull(result.value());
			Assert.assertTrue(result.value() > 0.8);
			Assert.assertTrue(result.value() < 1.6);
		});
	}

}
