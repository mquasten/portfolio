package de.mq.portfolio.shareportfolio.support;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;

public class PortfolioAOTest {

	private static final double TOTAL_RATE_DIVIDENTS = 0.025;
	private static final double TOTAL_RATE = 0.05;
	private static final double STANDARD_DERIVATION = 1.51e-8;
	private static final String SHARE_NAME01 = "share01";
	private static final String SHARE_NAME02 = "share02";
	private static final String CURRENCY = "EUR";
	private static final String ID = "19680528";
	private static final String NAME = "min risk";
	private final PortfolioAO portfolioAO = new PortfolioAO();

	private final TimeCourse timeCourse01 = Mockito.mock(TimeCourse.class);
	private final TimeCourse timeCourse02 = Mockito.mock(TimeCourse.class);

	private final Share share01 = Mockito.mock(Share.class);
	private final Share share02 = Mockito.mock(Share.class);

	final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);

	private final Map<String, Double> corelations01 = new HashMap<>();

	private final Map<String, Double> corelations02 = new HashMap<>();

	private final Map<TimeCourse, Double> weights = new HashMap<>();
	private final ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);

	@Before
	public final void setup() {

		corelations01.put(SHARE_NAME01, 1D);
		corelations01.put(SHARE_NAME02, 0.5D);

		corelations02.put(SHARE_NAME01, 0.5D);
		corelations02.put(SHARE_NAME02, 1D);

		weights.put(timeCourse01, 0.4D);
		weights.put(timeCourse02, 0.6D);

		Mockito.when(share01.name()).thenReturn(SHARE_NAME01);
		Mockito.when(share02.name()).thenReturn(SHARE_NAME02);
		Mockito.when(sharePortfolio.name()).thenReturn(NAME);
		Mockito.when(sharePortfolio.currency()).thenReturn(CURRENCY);
		Mockito.when(timeCourse01.share()).thenReturn(share01);
		Mockito.when(timeCourse02.share()).thenReturn(share02);

		Mockito.when(sharePortfolio.timeCourses()).thenReturn(Arrays.asList(timeCourse01, timeCourse02));

		Mockito.when(sharePortfolio.correlationEntries()).thenReturn(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(SHARE_NAME01, corelations01), new AbstractMap.SimpleImmutableEntry<>(SHARE_NAME02, corelations02)));

		Mockito.when(sharePortfolio.min()).thenReturn(weights);

		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);

		Mockito.when(sharePortfolio.standardDeviation()).thenReturn(STANDARD_DERIVATION);

		Mockito.when(sharePortfolio.totalRate(exchangeRateCalculator)).thenReturn(TOTAL_RATE);

		Mockito.when(sharePortfolio.totalRateDividends(exchangeRateCalculator)).thenReturn(TOTAL_RATE_DIVIDENTS);
		
		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(Arrays.asList(exchangeRate));
	}

	@Test
	public final void name() {
		Assert.assertNull(portfolioAO.getName());
		portfolioAO.setName(NAME);
		Assert.assertEquals(NAME, portfolioAO.getName());
	}

	@Test
	public final void id() {
		Assert.assertNull(portfolioAO.getId());
		portfolioAO.setId(ID);
		Assert.assertEquals(ID, portfolioAO.getId());
	}

	@Test
	public final void sharePortfolio() {
		portfolioAO.setSharePortfolio(sharePortfolio, Optional.of(exchangeRateCalculator));

		Assert.assertEquals(NAME, portfolioAO.getName());

		Assert.assertEquals(CURRENCY, portfolioAO.getCurrency());

		Assert.assertEquals(Arrays.asList(timeCourse01, timeCourse02), portfolioAO.getTimeCourses());

		Assert.assertEquals(Arrays.asList(SHARE_NAME01, SHARE_NAME02), portfolioAO.getShares());
		Assert.assertEquals(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(SHARE_NAME01, corelations01), new AbstractMap.SimpleImmutableEntry<>(SHARE_NAME02, corelations02)), portfolioAO.getCorrelations());

		Assert.assertEquals(weights, portfolioAO.getWeights());
		Assert.assertFalse(portfolioAO.getEditable());
		Assert.assertEquals(STANDARD_DERIVATION, portfolioAO.getMinStandardDeviation());
		Assert.assertEquals(TOTAL_RATE, portfolioAO.getTotalRate());
		Assert.assertEquals(TOTAL_RATE_DIVIDENTS, portfolioAO.getTotalRateDividends());

		Mockito.when(sharePortfolio.isCommitted()).thenReturn(false);
		portfolioAO.setSharePortfolio(sharePortfolio, Optional.of(exchangeRateCalculator));
		Assert.assertTrue(portfolioAO.getEditable());
		
		Assert.assertTrue(portfolioAO.getExchangeRateTranslationsAware());
		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(new ArrayList<>());
		portfolioAO.setSharePortfolio(sharePortfolio, Optional.of(exchangeRateCalculator));
		Assert.assertFalse(portfolioAO.getExchangeRateTranslationsAware());
	}

	@Test
	public final void sharePortfolioLessThan2TimeCourses() {
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(Arrays.asList(timeCourse01));

		portfolioAO.setSharePortfolio(sharePortfolio, Optional.of(exchangeRateCalculator));

		Assert.assertEquals(NAME, portfolioAO.getName());

		Assert.assertEquals(CURRENCY, portfolioAO.getCurrency());

		Assert.assertEquals(Arrays.asList(timeCourse01), portfolioAO.getTimeCourses());

		Assert.assertEquals(Arrays.asList(SHARE_NAME01), portfolioAO.getShares());
		Assert.assertEquals(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(SHARE_NAME01, corelations01), new AbstractMap.SimpleImmutableEntry<>(SHARE_NAME02, corelations02)), portfolioAO.getCorrelations());

		Assert.assertEquals(weights, portfolioAO.getWeights());
		Assert.assertFalse(portfolioAO.getEditable());
		Assert.assertNull(portfolioAO.getMinStandardDeviation());
		Assert.assertNull(portfolioAO.getTotalRate());
		Assert.assertNull(portfolioAO.getTotalRateDividends());
	}

	@Test
	public final void getSharePortfolio() {
		portfolioAO.setId(ID);
		final SharePortfolio sharePortfolio = portfolioAO.getSharePortfolio();

		Assert.assertEquals(ID, sharePortfolio.id());

		sharePortfolio.timeCourses().forEach(tc -> System.out.println(tc.name() + ":" + tc.share().name()));

	}

}
