package de.mq.portfolio.shareportfolio.support;

import java.lang.reflect.Constructor;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;

public class RealtimePortfolioAggregationBuilderTest {

	private final Data lastExchangeRateData = Mockito.mock(Data.class);
	private final Data realtimeExchangeRateData = Mockito.mock(Data.class);

	private static final String CURRENCY_USD = "USD";

	private final RealtimePortfolioAggregationBuilder realtimePortfolioAggregationBuilder = BeanUtils.instantiateClass(Mockito.mock(RealtimePortfolioAggregationBuilderImpl.class).getClass());
	final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	private TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	final List<Entry<TimeCourse, List<Data>>> realTimeCourses = Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(timeCourse, Arrays.asList(Mockito.mock(Data.class), Mockito.mock(Data.class))));

	private final ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);

	private final Share share = Mockito.mock(Share.class);

	private final List<ExchangeRate> exchangeRates = Arrays.asList(exchangeRate);

	final Map<String, Data[]> exchangeRatesMap = new HashMap<>();

	@Before
	public final void setup() {

		exchangeRatesMap.put(CURRENCY_USD, new Data[] { lastExchangeRateData, realtimeExchangeRateData });

		Mockito.doReturn(CURRENCY_USD).when(exchangeRate).source();

		Mockito.doReturn(Arrays.asList(lastExchangeRateData, realtimeExchangeRateData)).when(exchangeRate).rates();
		Mockito.doReturn("mq-minrisk").when(sharePortfolio).name();
		Mockito.doReturn("EUR").when(sharePortfolio).currency();

		Mockito.doReturn(share).when(timeCourse).share();
		Mockito.doReturn(CURRENCY_USD).when(share).currency();
		Mockito.doReturn("JNJ").when(timeCourse).code();
	}

	@Test
	public final void withSharePortfolio() {
		Assert.assertEquals(realtimePortfolioAggregationBuilder, realtimePortfolioAggregationBuilder.withSharePortfolio(sharePortfolio));

		Assert.assertEquals(sharePortfolio, builderFields().get(SharePortfolio.class));
	}

	@Test
	public final void withRealtimeCourses() {

		Assert.assertEquals(realtimePortfolioAggregationBuilder, realtimePortfolioAggregationBuilder.withRealtimeCourses(realTimeCourses));

		Assert.assertEquals(realTimeCourses, builderFields().get(Collection.class));
	}

	@Test
	public final void withRealtimeExchangeRates() {
		Assert.assertEquals(realtimePortfolioAggregationBuilder, realtimePortfolioAggregationBuilder.withRealtimeExchangeRates(exchangeRates));

		@SuppressWarnings("unchecked")
		final Map<String, Data[]> exchangeRates = (Map<String, Data[]>) builderFields().get(Map.class);

		Assert.assertEquals(1, exchangeRates.size());
		Assert.assertEquals(CURRENCY_USD, exchangeRates.keySet().stream().findAny().get());
		Assert.assertEquals(2, exchangeRates.values().stream().findAny().get().length);
		Assert.assertEquals(lastExchangeRateData, exchangeRates.values().stream().findAny().get()[0]);
		Assert.assertEquals(realtimeExchangeRateData, exchangeRates.values().stream().findAny().get()[1]);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void withRealtimeExchangeRatesRealTimeMissing() {

		Mockito.doReturn(Arrays.asList(lastExchangeRateData)).when(exchangeRate).rates();

		realtimePortfolioAggregationBuilder.withRealtimeExchangeRates(exchangeRates);
	}

	@Test
	public final void target() {
		Assert.assertEquals(RealtimePortfolioAggregationImpl.class, (((RealtimePortfolioAggregationBuilderImpl) realtimePortfolioAggregationBuilder).target()));
	}

	@Test
	public final void build() {
		final RealtimePortfolioAggregationBuilderImpl realtimePortfolioAggregationBuilder = realtimePortfolioAggregationBuilderMock(this.realTimeCourses);

		final RealtimePortfolioAggregationMock result = (RealtimePortfolioAggregationMock) realtimePortfolioAggregationBuilder.build();

		Assert.assertEquals(sharePortfolio, result.sharePortfolio);
		Assert.assertEquals(realTimeCourses, result.realtimeCourses);
		Assert.assertEquals(exchangeRatesMap, result.exchangeRates);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void buildWrongTimeCourseData() {
		realtimePortfolioAggregationBuilderMock(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(timeCourse, Arrays.asList()))).build();
	}

	private RealtimePortfolioAggregationBuilderImpl realtimePortfolioAggregationBuilderMock(final List<Entry<TimeCourse, List<Data>>> realTimeCourses) {
		final RealtimePortfolioAggregationBuilderImpl realtimePortfolioAggregationBuilder = Mockito.mock(RealtimePortfolioAggregationBuilderImpl.class, Mockito.CALLS_REAL_METHODS);
		Mockito.doReturn(RealtimePortfolioAggregationMock.class).when(realtimePortfolioAggregationBuilder).target();

		Mockito.doAnswer(answer -> new ExceptionTranslationBuilderImpl<Constructor<? extends RealtimePortfolioAggregation>, AutoCloseable>()).when(realtimePortfolioAggregationBuilder).exceptionTranslator();

		final Map<Class<?>, Object> dependencies = new HashMap<>();
		dependencies.put(SharePortfolio.class, sharePortfolio);
		dependencies.put(Collection.class, realTimeCourses);
		dependencies.put(Map.class, exchangeRatesMap);
		Arrays.asList(RealtimePortfolioAggregationBuilderImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(realtimePortfolioAggregationBuilder, field.getName(), dependencies.get(field.getType())));
		return realtimePortfolioAggregationBuilder;
	}

	private Map<Class<?>, Object> builderFields() {
		final Collection<Class<?>> dependencies = Arrays.asList(SharePortfolio.class, Collection.class, Map.class);
		return Arrays.asList(RealtimePortfolioAggregationBuilderImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.contains(field.getType()) && ReflectionTestUtils.getField(realtimePortfolioAggregationBuilder, field.getName()) != null).collect(Collectors.toMap(field -> field.getType(), field -> ReflectionTestUtils.getField(realtimePortfolioAggregationBuilder, field.getName())));

	}

}

class RealtimePortfolioAggregationMock extends RealtimePortfolioAggregationImpl {

	final SharePortfolio sharePortfolio;
	final Collection<Entry<TimeCourse, List<Data>>> realtimeCourses;
	final Map<String, Data[]> exchangeRates;

	RealtimePortfolioAggregationMock(SharePortfolio sharePortfolio, Collection<Entry<TimeCourse, List<Data>>> realtimeCourses, Map<String, Data[]> exchangeRates) {
		super(Mockito.mock(SharePortfolio.class), Arrays.asList(), new HashMap<>());
		this.sharePortfolio = sharePortfolio;
		this.realtimeCourses = realtimeCourses;
		this.exchangeRates = exchangeRates;
	}

}
