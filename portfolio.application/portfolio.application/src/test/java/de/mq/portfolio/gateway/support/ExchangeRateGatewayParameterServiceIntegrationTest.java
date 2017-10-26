package de.mq.portfolio.gateway.support;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.support.ExchangeRateImpl;
import de.mq.portfolio.gateway.ExchangeRateGatewayParameterService;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo-test.xml", "/application-test.xml" })
@Ignore
public class ExchangeRateGatewayParameterServiceIntegrationTest {

	private static final String CURRENCY_USD = "USD";

	private static final String CURRENCY_EUR = "EUR";

	private static final String TARGET_CURRENCY_PARAM_NAME = "targetCurrency";

	private static final String SOURCE_CURRENCY_PARAM_NAME = "sourceCurrency";

	@Autowired
	private GatewayParameterRepository gatewayParameterRepository;

	@Autowired
	private RestOperations restOperations;

	private final static String URL = "http://www.bundesbank.de/cae/servlet/StatisticDownload?tsId=BBEX3.D.{targetCurrency}.{sourceCurrency}.BB.AC.000&its_csvFormat=de&its_fileFormat=csv&mode=its";

	private GatewayHistoryRepository gatewayHistoryRepository;

	private ExchangeRateGatewayParameterService exchangeRateGatewayParameterService;

	private final ExchangeRate exchangeRate = new ExchangeRateImpl(CURRENCY_EUR, CURRENCY_USD);

	private final GatewayParameterAggregationBuilderImpl<ExchangeRate> gatewayParameterAggregationBuilder = new GatewayParameterAggregationBuilderImpl<>();

	private final MergedGatewayParameterBuilder mergedGatewayParameterBuilder = new MergedGatewayParameterBuilderImpl();
	
	@Before
	public final void setup() {
		gatewayHistoryRepository = new GatewayHistoryRepositoryImpl(restOperations);
		exchangeRateGatewayParameterService = new AbstractExchangeRateGatewayParameterService(gatewayParameterRepository, gatewayHistoryRepository) {

			@SuppressWarnings("unchecked")
			@Override
			GatewayParameterAggregationBuilder<ExchangeRate> gatewayParameterAggregationBuilder() {
				return gatewayParameterAggregationBuilder;
			}


			@Override
			MergedGatewayParameterBuilder mergedGatewayParameterBuilder() {
				return mergedGatewayParameterBuilder;
			}
		};

	}

	@Test
	public final void aggregationForRequiredGateway() {
		final GatewayParameterAggregation<ExchangeRate> result = exchangeRateGatewayParameterService.aggregationForRequiredGateway(exchangeRate, Gateway.CentralBankExchangeRates);
		Assert.assertEquals(exchangeRate, result.domain());

		Assert.assertEquals(1, result.gatewayParameters().size());

		assertGatewayParameter(result.gatewayParameters().stream().findAny().get());
	}

	private void assertGatewayParameter(final GatewayParameter gatewayParameter) {
		Assert.assertEquals(URL, gatewayParameter.urlTemplate());
		Assert.assertEquals(2, gatewayParameter.parameters().size());
		Assert.assertEquals(CURRENCY_EUR, gatewayParameter.parameters().get(SOURCE_CURRENCY_PARAM_NAME));
		Assert.assertEquals(CURRENCY_USD, gatewayParameter.parameters().get(TARGET_CURRENCY_PARAM_NAME));
	}

	@Test
	public final void aggregationForAllGateways() {
		final GatewayParameterAggregation<ExchangeRate> result = exchangeRateGatewayParameterService.aggregationForAllGateways(exchangeRate);

		Assert.assertEquals(exchangeRate, result.domain());
		Assert.assertEquals(1, result.gatewayParameters().size());
		assertGatewayParameter(result.gatewayParameters().stream().findAny().get());
	}

	@Test
	public final void history() {
		final GatewayParameter gatewayParameter = new GatewayParameterImpl(String.format("%s-%s", exchangeRate.source(), exchangeRate.target()), Gateway.CentralBankExchangeRates, URL,
				String.format("{%s:'%s', %s:'%s'}", SOURCE_CURRENCY_PARAM_NAME, CURRENCY_EUR, TARGET_CURRENCY_PARAM_NAME, CURRENCY_USD));
		
		final List<String[]> lines = Arrays.asList(exchangeRateGatewayParameterService.history(gatewayParameter).replaceAll("[\"]", "").split("\n")).stream().map(line -> line.split("[;]")).filter(cols -> cols.length == 2 && cols[0].matches("[0-9].*")).collect(Collectors.toList());

		Assert.assertTrue(lines.size() > 4500);
		DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
		lines.forEach(cols -> {
			final double value = Double.parseDouble(cols[1].replace(',', '.'));
			Assert.assertTrue(value > 0.8d);
			Assert.assertTrue(value < 1.6d);
			Assert.assertTrue(dateValid(dateFormat, cols));
		});
	}

	protected boolean dateValid(DateFormat dateFormat, String[] cols) {
		try {
			dateFormat.parse(cols[0]);
			return true;
		} catch (final ParseException parseException) {
			return false;
		}
	}

}
