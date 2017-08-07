package de.mq.portfolio.exchangerate.support;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
@ActiveProfiles("yahoo")
@Ignore
public class ExchangeRateRepositoryIntegrationTest {
	
	@Autowired
	private ExchangeRateRepository exchangeRateRepository; 
	
	private static String URL =  "http://www.bundesbank.de/cae/servlet/StatisticDownload?tsId=BBEX3.D.{targetCurrency}.{sourceCurrency}.BB.AC.000&its_csvFormat=de&its_fileFormat=csv&mode=its"; 
	
	@Test
	public final void exchangeRates() {
		final Map<String,String> parameters = new HashMap<>();
		parameters.put("targetCurrency", "USD");
		parameters.put("sourceCurrency", "EUR");
		final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
		
		@SuppressWarnings("unchecked")
		final GatewayParameterAggregation<ExchangeRate> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.CentralBankExchangeRates)).thenReturn(gatewayParameter);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL);
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		final Collection<Data> results = exchangeRateRepository.history(gatewayParameterAggregation);
		
		results.forEach(result -> {
			Assert.assertTrue(result.date().after(new GregorianCalendar(1995, 0, 1).getTime()));
			Assert.assertTrue(result.date().before(new Date()));
			Assert.assertNotNull(result.value());
			Assert.assertTrue(result.value() > 0.8);
			Assert.assertTrue(result.value() < 1.6);
		});
	}

}
