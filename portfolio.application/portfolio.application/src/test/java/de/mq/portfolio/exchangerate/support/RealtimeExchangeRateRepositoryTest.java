package de.mq.portfolio.exchangerate.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

public class RealtimeExchangeRateRepositoryTest {

	private static final String CURRENCY_EUR = "EUR";

	private static final String CURRENCY_USD = "USD";

	private static final String URL = "urlTemplate";

	private static Double RATE_USD_EUR = 0.9171d;

	private RestOperations restOperations = Mockito.mock(RestOperations.class);

	private final RealtimeExchangeRateRepository realtimeExchangeRateRepository = new RealtimeExchangeRateApiLayerRepositoryImpl(restOperations);

	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);

	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<Collection<ExchangeRate>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);

	private final Map<String, String> parameters = new HashMap<>();

	final Map<String, Map<String, Number>> resultMap = new HashMap<>();

	@Before
	public final void setup() {

		resultMap.put(RealtimeExchangeRateApiLayerRepositoryImpl.QUOTES_KEY, new HashMap<>());
		resultMap.get(RealtimeExchangeRateApiLayerRepositoryImpl.QUOTES_KEY).put(CURRENCY_USD + CURRENCY_EUR, RATE_USD_EUR);
		parameters.put("currencies", "EUR,EUR");
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL);
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.ApiLayerRealtimeExchangeRates)).thenReturn(gatewayParameter);
		Mockito.when(restOperations.getForObject(URL, HashMap.class, parameters)).thenReturn((HashMap<?, ?>) resultMap);
	}

	@Test
	public final void exchangeRates() {
		final List<ExchangeRate> results = new ArrayList<>(realtimeExchangeRateRepository.exchangeRates(gatewayParameterAggregation));

		Assert.assertEquals(2, results.size());

		Assert.assertEquals(new ExchangeRateImpl(CURRENCY_USD, CURRENCY_EUR), results.get(0));
		Assert.assertEquals(1, results.get(0).rates().size());
		Assert.assertEquals(RATE_USD_EUR, (Double) results.get(0).rates().get(0).value());

		Assert.assertEquals(new ExchangeRateImpl(CURRENCY_EUR, CURRENCY_EUR), results.get(1));
		Assert.assertEquals(1, results.get(0).rates().size());
		Assert.assertEquals(Double.valueOf(1d), (Double) results.get(1).rates().get(0).value());

		results.stream().map(exchangeRate -> exchangeRate.rates().get(0).date()).forEach(date -> Assert.assertEquals(30, Math.abs(date.getTime() - System.currentTimeMillis()) / 1000 / 60));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void exchangeRatesInvalidCurrencyCode() {
		resultMap.get(RealtimeExchangeRateApiLayerRepositoryImpl.QUOTES_KEY).put(CURRENCY_EUR, RATE_USD_EUR);
		realtimeExchangeRateRepository.exchangeRates(gatewayParameterAggregation);
	}

	@Test
	public final void supports() {
		Assert.assertEquals(Gateway.ApiLayerRealtimeExchangeRates, realtimeExchangeRateRepository.supports(Arrays.asList()));
	}

}
