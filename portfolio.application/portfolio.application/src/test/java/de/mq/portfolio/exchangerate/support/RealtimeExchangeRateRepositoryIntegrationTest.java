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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml", "/mongo-test.xml" })
@TestPropertySource(properties = { "realtime.exchangerates.dateformat=" + RealtimeExchangeRateRepositoryTest.EXCHANGERATES_DATEFORMAT })
@Ignore
public class RealtimeExchangeRateRepositoryIntegrationTest {

	static final String URL_PATH = "http://download.finance.yahoo.com/d/quotes.csv?s={query}&f=sl1d1t1";
	
	@Autowired
	private RealtimeExchangeRateRepository realtimeExchangeRateRepository;

	@Test
	public final void exchangeRates() {
		@SuppressWarnings("unchecked")
		final GatewayParameterAggregation<Collection<ExchangeRate>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
		final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL_PATH);
		final Map<String, String> parameter = new HashMap<>();
		parameter.put("query", "EURUSD=X,EURGBP=X,USDGBP=X");
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameter);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.YahooRealtimeExchangeRates)).thenReturn(gatewayParameter);

		final Collection<ExchangeRate> results = realtimeExchangeRateRepository.exchangeRates(gatewayParameterAggregation);

		Assert.assertEquals(3, results.size());

		results.forEach(result -> System.out.println(result.source() + "-" + result.target() + "=[" + result.rates().get(0).date() + "," + result.rates().get(0).value() + "]"));

		Assert.assertTrue(results.stream().map(er -> er.source() + "-" + er.target()).collect(Collectors.toSet()).containsAll(Arrays.asList("EUR-USD", "EUR-GBP", "USD-GBP")));

		Assert.assertTrue(rate(results, "EURUSD") > 1d && rate(results, "EURUSD") < 1.2d);
		Assert.assertTrue(rate(results, "EURGBP") > 0.8d && rate(results, "EURGBP") < 1d);
		Assert.assertTrue(rate(results, "USDGBP") > 0.7d && rate(results, "USDGBP") < 0.8d);

		results.stream().map(er -> er.rates().get(0).date()).forEach(date -> Assert.assertTrue(100d > (Math.abs(date.getTime() - System.currentTimeMillis()) / 1000 / 60)));
	}

	protected double rate(final Collection<ExchangeRate> results, String code) {
		return DataAccessUtils.requiredSingleResult(results.stream().filter(er -> (er.source() + er.target()).equals(code)).map(er -> er.rates().get(0).value()).collect(Collectors.toSet())).doubleValue();
	}

}
