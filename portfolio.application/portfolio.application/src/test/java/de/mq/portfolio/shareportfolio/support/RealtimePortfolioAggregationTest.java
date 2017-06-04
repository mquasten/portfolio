package de.mq.portfolio.shareportfolio.support;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

public class RealtimePortfolioAggregationTest {

	private static final Double WEIGHT_KO = 0.6;
	private static final Double WEIGHT_SAP = 0.4;
	private static final Double REALTIME_EXCHANGE_RATE_USD = 0.95;
	private static final Double LAST_EXCHANGE_RATE_USD = 0.90;
	private static final Double LAST_EXCHANGE_RATE_EUR = 1d;
	private static final Double REAL_TIME_RATE_KO = 45.50d;
	private static final Double REAL_TIME_RATE_SAP = 95.50d;
	private static final Double LAST_RATE_SAP = 95.00d;
	private static final String KO_NAME = "The coca cola company";
	private static final String SAP_NAME = "SAP SE";
	private static final String CURRENCY_USD = "USD";
	private static final String PORTFOLIO_NAME = "mq-minRisk";
	private static final String CURRENCY_EUR = "EUR";
	private static final String SAP_CODE = "SAP.DE";
	private static final String KO_CODE = "KO";
	private static final Double LAST_RATE_KO = 45.00d;
	final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	final Collection<Entry<TimeCourse, List<Data>>> realtimeCourses = new ArrayList<>();
	final Map<String, Data[]> exchangeRates = new HashMap<>();

	private RealtimePortfolioAggregationImpl realtimePortfolioAggregation;

	private final Map<TimeCourse, Double> weights = new HashMap<>();

	private final TimeCourse timeCourseSap = Mockito.mock(TimeCourse.class);
	private final Share shareSap = Mockito.mock(Share.class);

	private final TimeCourse timeCourseKo = Mockito.mock(TimeCourse.class);
	private final Share shareKo = Mockito.mock(Share.class);

	private final Data lastTimeCourseSap = Mockito.mock(Data.class);
	private final Data realTimeTimeCourseSap = Mockito.mock(Data.class);

	private final Data lastTimeCourseKo = Mockito.mock(Data.class);
	private final Data realTimeTimeCourseKo = Mockito.mock(Data.class);

	private final static Date LAST_DATE_SAP = Mockito.mock(Date.class);

	private final Date LAST_DATE_KO = Mockito.mock(Date.class);

	private final Date LAST_EXCHANGE_RATE_DATE_USD = Mockito.mock(Date.class);
	private final Date REALTIME_EXCHANGE_RATE_DATE_USD = Mockito.mock(Date.class);

	private final Date LAST_EXCHANGE_RATE_DATE_EUR = Mockito.mock(Date.class);
	private final Date REALTIME_EXCHANGE_RATE_DATE_EUR = Mockito.mock(Date.class);
	private static final Double REALTIME_EXCHANGE_RATE_EUR = LAST_EXCHANGE_RATE_EUR;

	private Data lastExchangeRateUsd = Mockito.mock(Data.class);
	private Data realtimeExchangeRateUsd = Mockito.mock(Data.class);

	private Data lastExchangeRateEur = Mockito.mock(Data.class);
	private Data realtimeExchangeRateEur = Mockito.mock(Data.class);

	@Before
	public final void setup() {

		Mockito.doReturn(LAST_RATE_SAP).when(lastTimeCourseSap).value();
		Mockito.doReturn(LAST_DATE_SAP).when(lastTimeCourseSap).date();
		Mockito.doReturn(REAL_TIME_RATE_SAP).when(realTimeTimeCourseSap).value();

		Mockito.doReturn(LAST_RATE_KO).when(lastTimeCourseKo).value();
		Mockito.doReturn(LAST_DATE_KO).when(lastTimeCourseKo).date();
		Mockito.doReturn(REAL_TIME_RATE_KO).when(realTimeTimeCourseKo).value();

		realtimeCourses.add(new AbstractMap.SimpleImmutableEntry<>(timeCourseSap, Arrays.asList(lastTimeCourseSap, realTimeTimeCourseSap)));
		realtimeCourses.add(new AbstractMap.SimpleImmutableEntry<>(timeCourseKo, Arrays.asList(lastTimeCourseKo, realTimeTimeCourseKo)));
		weights.put(timeCourseSap, 0.4);
		weights.put(timeCourseKo, WEIGHT_KO);
		Mockito.doReturn(SAP_CODE).when(timeCourseSap).code();
		Mockito.doReturn(shareSap).when(timeCourseSap).share();
		Mockito.doReturn(SAP_NAME).when(shareSap).name();

		Mockito.doReturn(CURRENCY_EUR).when(shareSap).currency();

		Mockito.doReturn(KO_CODE).when(timeCourseKo).code();
		Mockito.doReturn(shareKo).when(timeCourseKo).share();
		Mockito.doReturn(KO_NAME).when(shareKo).name();
		Mockito.doReturn(CURRENCY_USD).when(shareKo).currency();

		Mockito.doReturn(PORTFOLIO_NAME).when(sharePortfolio).name();
		Mockito.doReturn(CURRENCY_EUR).when(sharePortfolio).currency();
		Mockito.doReturn(weights).when(sharePortfolio).min();

		Mockito.doReturn(LAST_EXCHANGE_RATE_DATE_USD).when(lastExchangeRateUsd).date();
		Mockito.doReturn(REALTIME_EXCHANGE_RATE_DATE_USD).when(realtimeExchangeRateUsd).date();
		Mockito.doReturn(LAST_EXCHANGE_RATE_USD).when(lastExchangeRateUsd).value();
		Mockito.doReturn(REALTIME_EXCHANGE_RATE_USD).when(realtimeExchangeRateUsd).value();

		Mockito.doReturn(LAST_EXCHANGE_RATE_DATE_EUR).when(lastExchangeRateEur).date();
		Mockito.doReturn(REALTIME_EXCHANGE_RATE_DATE_EUR).when(realtimeExchangeRateEur).date();
		Mockito.doReturn(LAST_EXCHANGE_RATE_EUR).when(lastExchangeRateEur).value();
		Mockito.doReturn(LAST_EXCHANGE_RATE_EUR).when(realtimeExchangeRateEur).value();

		exchangeRates.put(CURRENCY_USD, Arrays.asList(lastExchangeRateUsd, realtimeExchangeRateUsd).toArray(new Data[2]));

		exchangeRates.put(CURRENCY_EUR, Arrays.asList(lastExchangeRateEur, realtimeExchangeRateEur).toArray(new Data[2]));

		realtimePortfolioAggregation = new RealtimePortfolioAggregationImpl(sharePortfolio, realtimeCourses, exchangeRates);
	}

	@Test
	public final void portfolioCurrency() {
		Assert.assertEquals(CURRENCY_EUR, realtimePortfolioAggregation.portfolioCurrency());
	}

	@Test
	public final void portfolioName() {
		Assert.assertEquals(PORTFOLIO_NAME, realtimePortfolioAggregation.portfolioName());
	}

	@Test
	public final void lastShareRate() {
		Assert.assertEquals(LAST_RATE_SAP, (Double) realtimePortfolioAggregation.lastShareRate(SAP_CODE));
		Assert.assertEquals(LAST_RATE_KO, (Double) realtimePortfolioAggregation.lastShareRate(KO_CODE));
	}

	@Test
	public final void lastShareDate() {
		Assert.assertEquals(LAST_DATE_SAP, realtimePortfolioAggregation.lastShareDate(SAP_CODE));
		Assert.assertEquals(LAST_DATE_KO, realtimePortfolioAggregation.lastShareDate(KO_CODE));
	}

	@Test
	public final void shareRealtimeRate() {
		Assert.assertEquals(REAL_TIME_RATE_SAP, (Double) realtimePortfolioAggregation.shareRealtimeRate(SAP_CODE));
		Assert.assertEquals(REAL_TIME_RATE_KO, (Double) realtimePortfolioAggregation.shareRealtimeRate(KO_CODE));
	}

	@Test
	public final void shareDelta() {
		Assert.assertEquals(Double.valueOf(REAL_TIME_RATE_SAP - LAST_RATE_SAP), (Double) realtimePortfolioAggregation.shareDelata(SAP_CODE));
		Assert.assertEquals(Double.valueOf(REAL_TIME_RATE_KO - LAST_RATE_KO), (Double) realtimePortfolioAggregation.shareDelata(KO_CODE));
	}

	@Test
	public final void shareDeltaPercent() {
		Assert.assertEquals(Double.valueOf(100 * (REAL_TIME_RATE_SAP - LAST_RATE_SAP) / LAST_RATE_SAP), (Double) realtimePortfolioAggregation.shareDeltaPercent(SAP_CODE));
		Assert.assertEquals(Double.valueOf(100 * (REAL_TIME_RATE_KO - LAST_RATE_KO) / LAST_RATE_KO), (Double) realtimePortfolioAggregation.shareDeltaPercent(KO_CODE));
	}

	@Test
	public final void shareName() {
		Assert.assertEquals(SAP_NAME, realtimePortfolioAggregation.shareName(SAP_CODE));
		Assert.assertEquals(KO_NAME, realtimePortfolioAggregation.shareName(KO_CODE));
	}

	@Test
	public final void shareCurrency() {
		Assert.assertEquals(CURRENCY_EUR, realtimePortfolioAggregation.shareCurrency(SAP_CODE));
		Assert.assertEquals(CURRENCY_USD, realtimePortfolioAggregation.shareCurrency(KO_CODE));
	}

	@Test
	public final void shareCodes() {
		Assert.assertEquals(new HashSet<>(Arrays.asList(SAP_CODE, KO_CODE)), realtimePortfolioAggregation.shareCodes());
	}

	@Test
	public final void lastExchangeRateForCurrency() {
		Assert.assertEquals(LAST_EXCHANGE_RATE_USD, (Double) realtimePortfolioAggregation.lastExchangeRateForCurrency(CURRENCY_USD));
		Assert.assertEquals(LAST_EXCHANGE_RATE_EUR, (Double) realtimePortfolioAggregation.lastExchangeRateForCurrency(CURRENCY_EUR));
	}

	@Test
	public final void lastExchangeRateDate() {
		Assert.assertEquals(LAST_EXCHANGE_RATE_DATE_USD, realtimePortfolioAggregation.lastExchangeRateDate(CURRENCY_USD));
		Assert.assertEquals(LAST_EXCHANGE_RATE_DATE_EUR, realtimePortfolioAggregation.lastExchangeRateDate(CURRENCY_EUR));
	}

	@Test
	public final void realtimeExchangeRateForCurrency() {
		Assert.assertEquals(REALTIME_EXCHANGE_RATE_USD, (Double) realtimePortfolioAggregation.realtimeExchangeRateForCurrency(CURRENCY_USD));
		Assert.assertEquals(REALTIME_EXCHANGE_RATE_EUR, (Double) realtimePortfolioAggregation.realtimeExchangeRateForCurrency(CURRENCY_EUR));
	}

	@Test
	public final void realtimeExchangeRateDate() {
		Assert.assertEquals(REALTIME_EXCHANGE_RATE_DATE_USD, realtimePortfolioAggregation.realtimeExchangeRateDate(CURRENCY_USD));
		Assert.assertEquals(REALTIME_EXCHANGE_RATE_DATE_EUR, realtimePortfolioAggregation.realtimeExchangeRateDate(CURRENCY_EUR));
	}

	@Test
	public final void deltaExchangeRatePercent() {
		Assert.assertEquals(Double.valueOf(100d * (REALTIME_EXCHANGE_RATE_USD - LAST_EXCHANGE_RATE_USD) / LAST_EXCHANGE_RATE_USD), (Double) realtimePortfolioAggregation.deltaPercentExchangeRate(CURRENCY_USD));
		Assert.assertEquals(Double.valueOf(0d), (Double) realtimePortfolioAggregation.deltaPercentExchangeRate(CURRENCY_EUR));
	}

	@Test
	public final void currencies() {
		Assert.assertEquals(new HashSet<>(Arrays.asList(CURRENCY_EUR, CURRENCY_USD)), realtimePortfolioAggregation.currencies());
	}

	@Test
	public final void translatedCurrencies() {
		Assert.assertEquals(new HashSet<>(Arrays.asList(CURRENCY_USD)), realtimePortfolioAggregation.translatedCurrencies());
	}

	@Test
	public final void lastRatePortfolio() {
		Assert.assertEquals(expectedLastRateKo(), (Double) realtimePortfolioAggregation.lastRatePortfolio(KO_CODE));
		Assert.assertEquals(expectedLastRateSap(), (Double) realtimePortfolioAggregation.lastRatePortfolio(SAP_CODE));
	}

	private Double expectedLastRateSap() {
		return Double.valueOf(WEIGHT_SAP * LAST_EXCHANGE_RATE_EUR * LAST_RATE_SAP);
	}

	@Test
	public final void realtimeRatePortfolio() {

		Assert.assertEquals(expectedRealTimeRatePortfolioKo(), (Double) realtimePortfolioAggregation.realtimeRatePortfolio(KO_CODE));
		Assert.assertEquals(expectedRealTimeRatePortfolioSap(), (Double) realtimePortfolioAggregation.realtimeRatePortfolio(SAP_CODE));
	}

	private Double expectedRealTimeRatePortfolioSap() {
		return WEIGHT_SAP * REALTIME_EXCHANGE_RATE_EUR * REAL_TIME_RATE_SAP;
	}

	@Test
	public final void weight() {
		Assert.assertEquals(WEIGHT_KO, realtimePortfolioAggregation.weight(KO_CODE));
		Assert.assertEquals(WEIGHT_SAP, realtimePortfolioAggregation.weight(SAP_CODE));
	}

	@Test
	public final void deltaPortfolio() {
		Assert.assertEquals(Double.valueOf(expectedRealTimeRatePortfolioKo() - expectedLastRateKo()), (Double) realtimePortfolioAggregation.deltaPortfolio(KO_CODE));
		Assert.assertEquals(Double.valueOf(expectedRealTimeRatePortfolioSap() - expectedLastRateSap()), (Double) realtimePortfolioAggregation.deltaPortfolio(SAP_CODE));
	}
	
	@Test
	public final void deltaPortfolioPercent() {
		Assert.assertEquals(Double.valueOf(100d*(expectedRealTimeRatePortfolioKo() - expectedLastRateKo()) / expectedLastRateKo() ), (Double) realtimePortfolioAggregation.deltaPortfolioPercent(KO_CODE));
		Assert.assertEquals(Double.valueOf(100d*(expectedRealTimeRatePortfolioSap() - expectedLastRateSap()) / expectedLastRateSap()), (Double) realtimePortfolioAggregation.deltaPortfolioPercent(SAP_CODE));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void wrongExchangeRates() {
		exchangeRates.put(CURRENCY_EUR, Arrays.asList(lastExchangeRateEur).toArray(new Data[1]));

		new RealtimePortfolioAggregationImpl(sharePortfolio, realtimeCourses, exchangeRates);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void wrongTimeCourses() {
		realtimeCourses.clear();
		realtimeCourses.add(new AbstractMap.SimpleImmutableEntry<>(timeCourseKo, Arrays.asList(lastTimeCourseKo)));
		realtimeCourses.add(new AbstractMap.SimpleImmutableEntry<>(timeCourseSap, Arrays.asList(lastTimeCourseSap, realTimeTimeCourseSap)));
		new RealtimePortfolioAggregationImpl(sharePortfolio, realtimeCourses, exchangeRates);
	}

	@Test
	public final void realTimeCourseAttributes() {
		Arrays.asList(RealtimePortfolioAggregationImpl.RealTimeCourseAttribute.values()).forEach(value -> Assert.assertEquals(value, RealtimePortfolioAggregationImpl.RealTimeCourseAttribute.valueOf(value.name())));
	}

	private Double expectedLastRateKo() {
		return WEIGHT_KO * LAST_EXCHANGE_RATE_USD * LAST_RATE_KO;
	}

	private Double expectedRealTimeRatePortfolioKo() {
		return WEIGHT_KO * REALTIME_EXCHANGE_RATE_USD * REAL_TIME_RATE_KO;
	}
	
	@Test
	public final void lastRatePortfolioTotal() {
		Assert.assertEquals(Double.valueOf(expectedLastRateKo() + expectedLastRateSap()) , (Double) realtimePortfolioAggregation.lastRatePortfolio());
	}
	
	@Test
	public final void realtimeRatePortfolioTotal() {
		Assert.assertEquals(Double.valueOf(expectedRealTimeRatePortfolioKo() + expectedRealTimeRatePortfolioSap()), (Double) realtimePortfolioAggregation.realtimeRatePortfolio());
	}
	
	@Test
	public final void  deltaPortfolioTotal() {
		Assert.assertEquals(Double.valueOf(expectedRealTimeRatePortfolioKo() + expectedRealTimeRatePortfolioSap()- expectedLastRateKo() - expectedLastRateSap())  , (Double) realtimePortfolioAggregation.deltaPortfolio());
	}
	
	@Test
	public final void  deltaPortfolioPercentTotal() {
		Assert.assertEquals(Double.valueOf(100d*(expectedRealTimeRatePortfolioKo() + expectedRealTimeRatePortfolioSap()- expectedLastRateKo() - expectedLastRateSap()) /(expectedLastRateKo() + expectedLastRateSap())), (Double) realtimePortfolioAggregation.deltaPortfolioPercent());
	}

}
