package de.mq.portfolio.exchangerate.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml", "/mongo-test.xml" })
@Ignore
public class RealtimeExchangeRateRepositoryIntegrationTest {

	static final String URL_PATH = "http://www.apilayer.net/api/live?access_key=be9610f3981bac8e49bcb4d90329c376&currencies={currencies}";
	
	@Autowired
	private RealtimeExchangeRateRepository realtimeExchangeRateRepository;

	@Test
	public final void exchangeRates() {
		

		@SuppressWarnings("unchecked")
		final GatewayParameterAggregation<Collection<ExchangeRate>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
		final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL_PATH);
		final Map<String, String> parameter = new HashMap<>();
		parameter.put("currencies", "EUR,GBP");
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameter);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.ApiLayerRealtimeExchangeRates)).thenReturn(gatewayParameter);

		final Collection<ExchangeRate> results = realtimeExchangeRateRepository.exchangeRates(gatewayParameterAggregation);

		Assert.assertEquals(3, results.size());

		results.forEach(result -> System.out.println(result.source() + "-" + result.target() + "=[" + result.rates().get(0).date() + "," + result.rates().get(0).value() + "]"));

		Assert.assertTrue(results.stream().map(er -> er.source() + "-" + er.target()).collect(Collectors.toSet()).containsAll(Arrays.asList("USD-EUR", "USD-GBP", "EUR-EUR")));

		
		Assert.assertTrue(rate(results, "USDEUR") > 0.8d && rate(results, "USDEUR") < 1d);
		Assert.assertTrue(rate(results, "USDGBP") > 0.7d && rate(results, "USDGBP") < 0.8d);
		Assert.assertTrue(rate(results, "EUREUR") == 1d) ;
		results.stream().map(er -> er.rates().get(0).date()).forEach(date -> Assert.assertEquals(30, Math.abs(date.getTime()  -   System.currentTimeMillis()) / 1000 / 60 ));
	}

	private double rate(final Collection<ExchangeRate> results, String code) {
		return DataAccessUtils.requiredSingleResult(results.stream().filter(er -> (er.source() + er.target()).equals(code)).map(er -> er.rates().get(0).value()).collect(Collectors.toSet())).doubleValue();
	}

}
