package de.mq.portfolio.shareportfolio.support;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.ShareRepository;
import de.mq.portfolio.shareportfolio.SharePortfolio;

public class SharePortfolioServiceTest {

	private static final String CODE_SAP = "SAP.DE";
	private static final String CODE_JNJ = "JNJ";
	private static final Double EXCHANGE_RATE_EUR_USD = 0.9d;
	private static final Double EXCHANGE_RATE_EUR_EUR = 1d;
	private static final String CURRENCY_USD = "USD";
	private static final String CURRENCY_EUR = "EUR";
	private static final String JSON_STRING = "jsonString";
	private static final String ID = "19680528";
	private static final long NEXT_COUNTER = 2L;
	private static final String STATUS_STPPED = "STOPPED";
	private static final Long LIMIT = 10L;
	private static final Long COUNTER = 1L;

	private static final String NAME = "mq-test";

	private SharePortfolioRepository sharePortfolioRepository = Mockito.mock(SharePortfolioRepository.class);

	private ShareRepository shareRepository = Mockito.mock(ShareRepository.class);

	private ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class);

	private SharePortfolioService sharePortfolioService = Mockito.mock(AbstractSharePortfolioService.class, Mockito.CALLS_REAL_METHODS);

	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);

	private final Pageable pageable = Mockito.mock(Pageable.class);

	private final ShareService shareService = Mockito.mock(ShareService.class);

	private final Sort sort = Mockito.mock(Sort.class);

	private final Map<Class<?>, Object> dependencies = new HashMap<>();

	@Before
	public final void setup() {
		dependencies.put(SharePortfolioRepository.class, sharePortfolioRepository);
		dependencies.put(ShareRepository.class, shareRepository);
		dependencies.put(ExchangeRateService.class, exchangeRateService);
		dependencies.put(ShareService.class, shareService);

		ReflectionUtils.doWithFields(sharePortfolioService.getClass(), field -> ReflectionTestUtils.setField(sharePortfolioService, field.getName(), dependencies.get(field.getType())), field -> dependencies.containsKey(field.getType()));

	}

	@Test
	public final void committedPortfolio() {
		Mockito.when(sharePortfolioRepository.portfolio(NAME)).thenReturn(sharePortfolio);
		Assert.assertEquals(sharePortfolio, sharePortfolioService.committedPortfolio(NAME));

		Mockito.verify(sharePortfolio).commit();
		Mockito.verify(sharePortfolioRepository).save(sharePortfolio);
	}

	@Test
	public final void committedPortfolioAlredyCommitted() {
		Mockito.when(sharePortfolioRepository.portfolio(NAME)).thenReturn(sharePortfolio);
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);

		Assert.assertEquals(sharePortfolio, sharePortfolioService.committedPortfolio(NAME));

		Mockito.verify(sharePortfolio, Mockito.never()).commit();
		Mockito.verify(sharePortfolioRepository, Mockito.never()).save(sharePortfolio);
	}

	@Test
	public final void save() {
		sharePortfolioService.save(sharePortfolio);
		Mockito.verify(sharePortfolioRepository).save(sharePortfolio);
	}

	@Test
	public final void status() {
		Assert.assertEquals(AbstractSharePortfolioService.STATUS_CONTINUE, (((AbstractSharePortfolioService) sharePortfolioService).status(AbstractSharePortfolioService.STATUS_COMPLETED, COUNTER, LIMIT)));
		Assert.assertEquals(AbstractSharePortfolioService.STATUS_COMPLETED, (((AbstractSharePortfolioService) sharePortfolioService).status(AbstractSharePortfolioService.STATUS_COMPLETED, LIMIT, LIMIT)));
		Assert.assertEquals(STATUS_STPPED, (((AbstractSharePortfolioService) sharePortfolioService).status(STATUS_STPPED, COUNTER, LIMIT)));
		Assert.assertEquals(AbstractSharePortfolioService.STATUS_COMPLETED, (((AbstractSharePortfolioService) sharePortfolioService).status(AbstractSharePortfolioService.STATUS_COMPLETED, COUNTER, null)));
	}

	@Test
	public final void incCounter() {
		Assert.assertEquals(NEXT_COUNTER, (long) ((AbstractSharePortfolioService) sharePortfolioService).incCounter(COUNTER));
		Assert.assertEquals(COUNTER, ((AbstractSharePortfolioService) sharePortfolioService).incCounter(null));
	}

	@Test
	public final void sharePortfolio() {
		Mockito.when(sharePortfolioRepository.sharePortfolio(ID)).thenReturn(sharePortfolio);
		Assert.assertEquals(sharePortfolio, sharePortfolioService.sharePortfolio(ID));
		Mockito.verify(sharePortfolioRepository).sharePortfolio(ID);
	}

	@Test
	public final void portfolios() {
		Mockito.when(sharePortfolioRepository.portfolios(pageable, sharePortfolio)).thenReturn(Arrays.asList(sharePortfolio));
		Assert.assertEquals(Arrays.asList(sharePortfolio), sharePortfolioService.portfolios(pageable, sharePortfolio));
		Mockito.verify(sharePortfolioRepository).portfolios(pageable, sharePortfolio);
	}

	@Test
	public final void pageable() {
		Mockito.when(sharePortfolioRepository.pageable(sharePortfolio, sort, LIMIT)).thenReturn(pageable);
		Assert.assertEquals(pageable, sharePortfolioService.pageable(sharePortfolio, sort, LIMIT));
		Mockito.verify(sharePortfolioRepository).pageable(sharePortfolio, sort, LIMIT);
	}

	@Test
	public final void assignTimecourses() {
		final List<TimeCourse> timeCourses = newTimecourses();
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(timeCourses);
		Mockito.when(sharePortfolio.id()).thenReturn(ID);
		Mockito.when(sharePortfolioRepository.sharePortfolio(ID)).thenReturn(sharePortfolio);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Collection<String>> codesCaptor = (ArgumentCaptor<Collection<String>>) ArgumentCaptor.forClass((Class<?>) Collection.class);
		final List<TimeCourse> newTimeCourses = Arrays.asList(Mockito.mock(TimeCourse.class), Mockito.mock(TimeCourse.class));
		Mockito.when(shareRepository.timeCourses(codesCaptor.capture())).thenReturn(newTimeCourses);

		sharePortfolioService.assign(sharePortfolio, timeCourses);

		Mockito.verify(sharePortfolio).assign(newTimeCourses);
		sharePortfolioRepository.save(sharePortfolio);
		Assert.assertEquals(2, codesCaptor.getValue().size());

		timeCourses.stream().map(tc -> tc.share().code()).forEach(code -> Assert.assertTrue(codesCaptor.getValue().contains(code)));
		;

	}

	private List<TimeCourse> newTimecourses() {
		final List<TimeCourse> timeCourses = new ArrayList<>();
		IntStream.range(1, 3).forEach(i -> {
			final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
			final Share share = Mockito.mock(Share.class);
			Mockito.when(share.code()).thenReturn("code" + i);
			Mockito.when(timeCourse.share()).thenReturn(share);
			final Data data = Mockito.mock(Data.class);
			Mockito.when(data.value()).thenReturn(50d);

			timeCourses.add(timeCourse);
		});
		return timeCourses;
	}

	@Test
	public final void delete() {
		Mockito.when(sharePortfolio.id()).thenReturn(ID);
		Mockito.when(sharePortfolioRepository.sharePortfolio(ID)).thenReturn(sharePortfolio);
		sharePortfolioService.delete(ID);
		Mockito.verify(sharePortfolioRepository).delete(sharePortfolio);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void deleteCommitted() {
		Mockito.when(sharePortfolio.id()).thenReturn(ID);
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		Mockito.when(sharePortfolioRepository.sharePortfolio(ID)).thenReturn(sharePortfolio);
		sharePortfolioService.delete(ID);

	}

	@Test
	public final void retrospective() {

		final ArgumentCaptor<SharePortfolio> sharePortfolioCaptor = ArgumentCaptor.forClass(SharePortfolio.class);
		final ArgumentCaptor<ExchangeRateCalculator> exchangeRateCalculatorCaptor = ArgumentCaptor.forClass(ExchangeRateCalculator.class);

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Collection<TimeCourse>> timeCourseCaptor = (ArgumentCaptor<Collection<TimeCourse>>) ArgumentCaptor.forClass((Class<?>) Collection.class);
		final SharePortfolioRetrospectiveBuilder builder = Mockito.mock(SharePortfolioRetrospectiveBuilder.class);
		Mockito.when(builder.withCommitedSharePortfolio(sharePortfolioCaptor.capture())).thenReturn(builder);
		Mockito.when(builder.withExchangeRateCalculator(exchangeRateCalculatorCaptor.capture())).thenReturn(builder);

		Mockito.when(builder.withTimeCourses(timeCourseCaptor.capture())).thenReturn(builder);
		SharePortfolioRetrospective sharePortfolioRetrospective = Mockito.mock(SharePortfolioRetrospective.class);
		Mockito.when(builder.build()).thenReturn(sharePortfolioRetrospective);

		final List<TimeCourse> timeCourses = newTimecourses();
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(timeCourses);
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Collection<String>> codesCaptor = (ArgumentCaptor<Collection<String>>) ArgumentCaptor.forClass((Class<?>) Collection.class);
		final List<TimeCourse> newTimeCourses = Arrays.asList(Mockito.mock(TimeCourse.class), Mockito.mock(TimeCourse.class));
		Mockito.when(shareRepository.timeCourses(codesCaptor.capture())).thenReturn(newTimeCourses);

		final Collection<ExchangeRate> exchangeRates = Arrays.asList(Mockito.mock(ExchangeRate.class));
		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(exchangeRates);

		final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);

		Mockito.when(sharePortfolioRepository.sharePortfolio(ID)).thenReturn(sharePortfolio);

		Mockito.when(exchangeRateService.exchangeRateCalculator(exchangeRates)).thenReturn(exchangeRateCalculator);

		Mockito.doReturn(builder).when((AbstractSharePortfolioService) sharePortfolioService).newBuilder();

		Assert.assertEquals(sharePortfolioRetrospective, sharePortfolioService.retrospective(ID));

		Assert.assertEquals(sharePortfolio, sharePortfolioCaptor.getValue());
		Assert.assertEquals(exchangeRateCalculator, exchangeRateCalculatorCaptor.getValue());
		Assert.assertEquals(newTimeCourses, timeCourseCaptor.getValue());

	}

	@Test
	public final void newSharePortfolioService() throws Exception {
		final SharePortfolioService sharePortfolioService = BeanUtils.instantiateClass(this.sharePortfolioService.getClass().getDeclaredConstructor(SharePortfolioRepository.class, ShareRepository.class, ExchangeRateService.class, ShareService.class), sharePortfolioRepository, shareRepository,
				exchangeRateService, shareService);
		final Map<Class<?>, Object> results = new HashMap<>();
		ReflectionUtils.doWithFields(sharePortfolioService.getClass(), field -> results.put(field.getType(), ReflectionTestUtils.getField(sharePortfolioService, field.getName())), field -> dependencies.containsKey(field.getType()));
		Assert.assertEquals(dependencies, results);
	}

	@Test
	public final void saveJson() {
		((AbstractSharePortfolioService) sharePortfolioService).save(JSON_STRING);
		Mockito.verify(sharePortfolioRepository).save(JSON_STRING);

	}

	@Test

	public final void realtimePortfolioAggregation() {

		final TimeCourse sap = Mockito.mock(TimeCourse.class);
		Mockito.doReturn(CODE_SAP).when(sap).code();

		final List<Data> sapRealtimeRates = Arrays.asList(Mockito.mock(Data.class), Mockito.mock(Data.class));
		final List<Data> jnjRealtimeRates = Arrays.asList(Mockito.mock(Data.class), Mockito.mock(Data.class));
		Mockito.doReturn(sapRealtimeRates).when(sap).rates();

		final Date endDate =  new Date();
		Mockito.doReturn(endDate).when(sap).end();
	
		
		final TimeCourse jnj = Mockito.mock(TimeCourse.class);
		Mockito.doReturn(jnjRealtimeRates).when(jnj).rates();
		Mockito.doReturn(CODE_JNJ).when(jnj).code();
		Mockito.doReturn(endDate).when(jnj).end();
		final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
		final ExchangeRate exchangeRateEur = Mockito.mock(ExchangeRate.class);

		Mockito.doReturn(CURRENCY_EUR).when(exchangeRateEur).target();
		Mockito.doReturn(CURRENCY_EUR).when(exchangeRateEur).source();

		final ExchangeRate exchangeRateUsd = Mockito.mock(ExchangeRate.class);
		Mockito.doReturn(CURRENCY_USD).when(exchangeRateUsd).target();

		Mockito.doReturn(CURRENCY_EUR).when(exchangeRateUsd).source();

		final Data realTimeDataExchangeRateEur = Mockito.mock(Data.class);
		final Data realTimeDataExchangeRateUsd = Mockito.mock(Data.class);

		Mockito.doReturn(EXCHANGE_RATE_EUR_EUR).when(exchangeRateCalculator).factor(exchangeRateEur, endDate);
		Mockito.doReturn(EXCHANGE_RATE_EUR_USD).when(exchangeRateCalculator).factor(exchangeRateUsd, endDate);

		Mockito.doReturn(Arrays.asList(realTimeDataExchangeRateEur)).when(exchangeRateEur).rates();
		Mockito.doReturn(Arrays.asList(realTimeDataExchangeRateUsd)).when(exchangeRateUsd).rates();

		Mockito.doReturn(sharePortfolio).when(sharePortfolioRepository).sharePortfolio(ID);
		Mockito.doReturn(Arrays.asList(sap, jnj)).when(sharePortfolio).timeCourses();

		Mockito.doReturn(exchangeRateEur).when(sharePortfolio).exchangeRate(sap);
		Mockito.doReturn(exchangeRateUsd).when(sharePortfolio).exchangeRate(jnj);

		Mockito.doReturn(Arrays.asList(sap, jnj)).when(shareRepository).timeCourses(Sets.newSet(CODE_SAP, CODE_JNJ));
		Mockito.doReturn(Arrays.asList(exchangeRateEur, exchangeRateUsd)).when(sharePortfolio).exchangeRateTranslations();
		Mockito.doReturn(exchangeRateCalculator).when(exchangeRateService).exchangeRateCalculator(Arrays.asList(exchangeRateEur, exchangeRateUsd));

		final RealtimePortfolioAggregationBuilder realtimePortfolioAggregationBuilder = Mockito.mock(RealtimePortfolioAggregationBuilder.class);

		Mockito.doReturn(realtimePortfolioAggregationBuilder).when(realtimePortfolioAggregationBuilder).withRealtimeExchangeRates(Mockito.any());

		Mockito.doReturn(realtimePortfolioAggregationBuilder).when(realtimePortfolioAggregationBuilder).withSharePortfolio(Mockito.any());

		Mockito.doReturn(realtimePortfolioAggregationBuilder).when(realtimePortfolioAggregationBuilder).withRealtimeCourses(Mockito.any());

		Mockito.doReturn(realtimePortfolioAggregationBuilder).when((AbstractSharePortfolioService) sharePortfolioService).newRealtimePortfolioAggregationBuilder();

		Mockito.doReturn(Arrays.asList(exchangeRateEur, exchangeRateUsd)).when(exchangeRateService).realTimeExchangeRates(Sets.newSet(exchangeRateEur, exchangeRateUsd));

		Mockito.doReturn(Arrays.asList(sap, jnj)).when(shareService).realTimeCourses(Arrays.asList(CODE_SAP, CODE_JNJ), true);

		final RealtimePortfolioAggregation realtimePortfolioAggregation = Mockito.mock(RealtimePortfolioAggregation.class);

		Mockito.doReturn(realtimePortfolioAggregation).when(realtimePortfolioAggregationBuilder).build();
		Assert.assertEquals(realtimePortfolioAggregation, sharePortfolioService.realtimePortfolioAggregation(ID, true));

		Mockito.verify(realtimePortfolioAggregationBuilder).build();

		final ArgumentCaptor<SharePortfolio> sharePortfolioArgumentCaptor = ArgumentCaptor.forClass(SharePortfolio.class);

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Collection<ExchangeRate>> exchangeRatesArgumentCaptor = (ArgumentCaptor<Collection<ExchangeRate>>) ArgumentCaptor.forClass((Class<?>) Collection.class);
		Mockito.verify(realtimePortfolioAggregationBuilder).withSharePortfolio(sharePortfolioArgumentCaptor.capture());

		Mockito.verify(realtimePortfolioAggregationBuilder).withRealtimeExchangeRates(exchangeRatesArgumentCaptor.capture());

		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Collection<Entry<TimeCourse, List<Data>>>> realTimeRatesArgumentCaptor = (ArgumentCaptor<Collection<Entry<TimeCourse, List<Data>>>>) ArgumentCaptor.forClass((Class<?>) Collection.class);

		Mockito.verify(realtimePortfolioAggregationBuilder).withRealtimeCourses(realTimeRatesArgumentCaptor.capture());

		Assert.assertEquals(sharePortfolio, sharePortfolioArgumentCaptor.getValue());

		Assert.assertEquals(2, exchangeRatesArgumentCaptor.getValue().size());

		final ExchangeRate rateEuro = ((List<ExchangeRate>) exchangeRatesArgumentCaptor.getValue()).get(0);

		Assert.assertEquals(CURRENCY_EUR, rateEuro.source());
		Assert.assertEquals(CURRENCY_EUR, rateEuro.target());

		Assert.assertEquals(2, rateEuro.rates().size());

	
		
		Assert.assertEquals(truncate(endDate), rateEuro.rates().get(0).date());

		Assert.assertEquals(EXCHANGE_RATE_EUR_EUR, (Double) rateEuro.rates().get(0).value());

		Assert.assertEquals(realTimeDataExchangeRateEur, rateEuro.rates().get(1));

		final ExchangeRate rateUsd = ((List<ExchangeRate>) exchangeRatesArgumentCaptor.getValue()).get(1);

		Assert.assertEquals(CURRENCY_EUR, rateUsd.source());
		Assert.assertEquals(CURRENCY_USD, rateUsd.target());

		Assert.assertEquals(2, rateUsd.rates().size());
		Assert.assertEquals(truncate(endDate), rateUsd.rates().get(0).date());

		Assert.assertEquals(EXCHANGE_RATE_EUR_USD, (Double) rateUsd.rates().get(0).value());

		Assert.assertEquals(realTimeDataExchangeRateUsd, rateUsd.rates().get(1));

		Assert.assertEquals(2, ((Collection<?>) realTimeRatesArgumentCaptor.getValue()).size());

		final Entry<TimeCourse, List<Data>> sapRealTimeEntry = ((List<Entry<TimeCourse, List<Data>>>) realTimeRatesArgumentCaptor.getValue()).get(0);

		Assert.assertEquals(sap, sapRealTimeEntry.getKey());
		Assert.assertEquals(sapRealtimeRates, sapRealTimeEntry.getValue());

		final Entry<TimeCourse, List<Data>> jnjRealTimeEntry = ((List<Entry<TimeCourse, List<Data>>>) realTimeRatesArgumentCaptor.getValue()).get(1);

		Assert.assertEquals(jnj, jnjRealTimeEntry.getKey());
		Assert.assertEquals(jnjRealtimeRates, jnjRealTimeEntry.getValue());
	}

	private Date truncate(final Date endDate) {
		return Date.from(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant());
	
	    
	}

	@Test(expected = IllegalArgumentException.class)
	public final void realtimePortfolioAggregationExchangeRatesMissing() {
		Mockito.doReturn(sharePortfolio).when(sharePortfolioRepository).sharePortfolio(ID);
		sharePortfolioService.realtimePortfolioAggregation(ID, true);

	}

}
