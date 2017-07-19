package de.mq.portfolio.share.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo-test.xml", "/application-test.xml" })
@Ignore
public class HistoryArivaRestRepositoryIntegrationTest {

	private static final String CURRENCY_USD = "USD";
	private static final String URL_DIVIDENDS = "http://www.ariva.de/{shareName}/historische_ereignisse";
	private static final String URL_RATES = "http://www.ariva.de/quote/historic/historic.csv?secu={shareId}&boerse_id={stockExchangeId}&clean_split=1&clean_payout=0&clean_bezug=1&min_time={startDate}&max_time={endDate}&trenner={delimiter}&go=Download";
	@Autowired
	@Qualifier("arivaHistoryRepository")
	private HistoryRepository historyRestRepository;
	private final Share share = Mockito.mock(Share.class);

	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<Share> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);

	private final GatewayParameter gatewayParameterRates = Mockito.mock(GatewayParameter.class);

	GatewayParameter gatewayParameterDividends = Mockito.mock(GatewayParameter.class);

	@Before
	public final void setup() {
		Mockito.when(gatewayParameterAggregation.domain()).thenReturn(share);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.ArivaRateHistory)).thenReturn(gatewayParameterRates);
		Mockito.when(gatewayParameterRates.urlTemplate()).thenReturn(URL_RATES);

		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.ArivaDividendHistory)).thenReturn(gatewayParameterDividends);
		Mockito.when(gatewayParameterDividends.urlTemplate()).thenReturn(URL_DIVIDENDS);
	}

	@Test
	@Ignore
	public final void historyKO() {

		// 400, 412
		Assert.assertNotNull(historyRestRepository);

		Mockito.doReturn("850663").when(share).wkn();
		Mockito.when(share.currency()).thenReturn(CURRENCY_USD);

		Mockito.when(gatewayParameterRates.parameters()).thenReturn(rateParameters("400", "21"));

		Mockito.when(gatewayParameterDividends.parameters()).thenReturn(dividendParameters("coca-cola-aktie"));

		final TimeCourse timeCourse = historyRestRepository.history(gatewayParameterAggregation);
		Assert.assertTrue(timeCourse.rates().size() > 250);
		Assert.assertTrue(timeCourse.dividends().size() == 4);
		// printRates(timeCourse.rates());
		// printRates(timeCourse.dividends());
	}

	private void printRates(final Collection<Data> rates) {
		rates.forEach(rate -> System.out.println(rate.date() + "=" + rate.value()));
	}

	private final Map<String, String> rateParameters(final String shareId, final String stockExchangeId) {
		final Map<String, String> results = new HashMap<>();
		results.put("shareId", shareId);
		results.put("stockExchangeId", stockExchangeId);

		return results;
	}

	private final Map<String, String> dividendParameters(final String shareName) {
		final Map<String, String> results = new HashMap<>();
		results.put("shareName", shareName);

		return results;
	}

	@Test
	@Ignore
	public final void historyJNJ() {
		Assert.assertNotNull(historyRestRepository);

		Mockito.when(gatewayParameterRates.parameters()).thenReturn(rateParameters("412", "21"));

		Mockito.when(gatewayParameterDividends.parameters()).thenReturn(dividendParameters("johnson_&_johnson-aktie"));
		Mockito.doReturn("853260").when(share).wkn();// JNJ
		Mockito.doReturn(CURRENCY_USD).when(share).currency();

		final TimeCourse timeCourses = historyRestRepository.history(gatewayParameterAggregation);
		Assert.assertTrue(timeCourses.rates().size() > 250);
		Assert.assertTrue(timeCourses.dividends().size() == 4);
		// printRates(timeCourses.rates());
		// printRates(timeCourses.dividends());
	}

	@Test
	@Ignore
	public final void dax() {

		Mockito.when(gatewayParameterRates.parameters()).thenReturn(rateParameters("290", "12"));
		Assert.assertNotNull(historyRestRepository);
		Mockito.doReturn("846900").when(share).wkn();
		// Mockito.doReturn("^GDAXI").when(share).code();
		Mockito.doReturn(true).when(share).isIndex();
		final TimeCourse timeCourses = historyRestRepository.history(gatewayParameterAggregation);
		Assert.assertTrue(timeCourses.rates().size() > 250);
		Assert.assertTrue(timeCourses.dividends().size() == 0);
		// printRates(timeCourses.rates());

	}

	@Test
	@Ignore
	public final void dow() {
		Mockito.when(gatewayParameterRates.parameters()).thenReturn(rateParameters("4325", "71"));
		Mockito.doReturn("969420").when(share).wkn();
		Mockito.doReturn(true).when(share).isIndex();

		final TimeCourse timeCourses = historyRestRepository.history(gatewayParameterAggregation);

		Assert.assertTrue(timeCourses.rates().size() > 250);
		Assert.assertTrue(timeCourses.dividends().size() == 0);

		printRates(timeCourses.rates());
	}

	@Test
	@Ignore
	public final void sap() {

		Mockito.when(gatewayParameterRates.parameters()).thenReturn(rateParameters("910", "6"));
		Mockito.when(gatewayParameterDividends.parameters()).thenReturn(dividendParameters("sap-aktie"));
		Mockito.doReturn("SAP.DE").when(share).code();
		Mockito.doReturn("EUR").when(share).currency();
		Mockito.doReturn("716460").when(share).wkn();

		final TimeCourse timeCourse = historyRestRepository.history(gatewayParameterAggregation);

		Assert.assertEquals(1, timeCourse.dividends().size());
		Assert.assertTrue(timeCourse.rates().size() > 250);

		printRates(timeCourse.rates());
		printRates(timeCourse.dividends());
	}

}
