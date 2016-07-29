package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateImpl;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.DataImpl;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;

public class SharePortfolioRetrospectiveBuilderTest {

	private static final String PORTFOLIO_NAME = "mq-min-risk";

	private static final String COMMITTED_SHARE_PORTFOLIO_FIELD = "committedSharePortfolio";

	private static final String CODE = "KO";

	private final SharePortfolioRetrospectiveBuilder builder = new SharePortfolioRetrospectiveBuilderImpl();

	private final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
	private SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);

	private TimeCourse timeCourse = Mockito.mock(TimeCourse.class);

	private final Share share = Mockito.mock(Share.class);

	@Test
	public final void withExchangeRateCalculator() {
		Assert.assertEquals(builder, builder.withExchangeRateCalculator(exchangeRateCalculator));

		Assert.assertEquals(exchangeRateCalculator, field(ExchangeRateCalculator.class));

	}

	private <T> T field(Class<? extends T> clazz) {
		@SuppressWarnings("unchecked")
		final Optional<T> result = (Optional<T>) Arrays.asList(SharePortfolioRetrospectiveBuilderImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(clazz)).map(field -> ReflectionTestUtils.getField(builder, field.getName())).findFirst();
		Assert.assertTrue(result.isPresent());
		return result.get();
	}

	@Test
	public final void withCommitedSharePortfolio() {
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		Assert.assertEquals(builder, builder.withCommitedSharePortfolio(sharePortfolio));
		Assert.assertEquals(sharePortfolio, ReflectionTestUtils.getField(builder, COMMITTED_SHARE_PORTFOLIO_FIELD));
	}

	@Test
	public final void withTimeCourse() {
		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Assert.assertEquals(builder, builder.withTimeCourse(timeCourse));
		@SuppressWarnings("unchecked")
		final Map<String, TimeCourse> results = field(Map.class);
		Assert.assertEquals(1, results.size());
		Assert.assertTrue(results.keySet().stream().findAny().isPresent());
		Assert.assertEquals(CODE, results.keySet().stream().findAny().get());
		Assert.assertTrue(results.values().stream().findAny().isPresent());
		Assert.assertEquals(timeCourse, results.values().stream().findAny().get());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void withTimeCourseCodeAlreadyAssigned() {
		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Assert.assertEquals(builder, builder.withTimeCourse(timeCourse));
		@SuppressWarnings("unchecked")
		final Map<String, TimeCourse> results = field(Map.class);
		results.put(CODE, timeCourse);
		builder.withTimeCourse(timeCourse);

	}

	@Test
	public final void withTimeCourses() {
		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Assert.assertEquals(builder, builder.withTimeCourses(Arrays.asList(timeCourse)));
		@SuppressWarnings("unchecked")
		final Map<String, TimeCourse> results = field(Map.class);
		Assert.assertEquals(1, results.size());
		Assert.assertTrue(results.keySet().stream().findAny().isPresent());
		Assert.assertEquals(CODE, results.keySet().stream().findAny().get());
		Assert.assertTrue(results.values().stream().findAny().isPresent());
		Assert.assertEquals(timeCourse, results.values().stream().findAny().get());

	}

	@Test(expected = IllegalArgumentException.class)
	public final void withTimeCoursesEmptyCollection() {
		builder.withTimeCourses(new ArrayList<>());
	}

	@Test
	public void build() {

		Mockito.when(sharePortfolio.name()).thenReturn(PORTFOLIO_NAME);

		final Share share01 = Mockito.mock(Share.class);
		final Share share02 = Mockito.mock(Share.class);
		Mockito.when(share01.name()).thenReturn("Share01");
		Mockito.when(share02.name()).thenReturn("Share02");
		Mockito.when(share01.currency()).thenReturn("EUR");
		Mockito.when(share02.currency()).thenReturn("USD");
		final Long date = new Date().getTime();
		final TimeCourse tc01 = Mockito.mock(TimeCourse.class);
		Mockito.when(tc01.code()).thenReturn("CODE01");
		final TimeCourse tc02 = Mockito.mock(TimeCourse.class);
		Mockito.when(tc02.code()).thenReturn("CODE02");

		Mockito.when(tc01.rates()).thenReturn(Arrays.asList(new DataImpl(date(date, 360), 50d), new DataImpl(date(date, 181), 60d)));
		Mockito.when(tc02.rates()).thenReturn(Arrays.asList(new DataImpl(date(date, 360), 30d), new DataImpl(date(date, 181), 40d)));

		Mockito.when(tc01.end()).thenReturn(date(date, 181));
		Mockito.when(tc02.end()).thenReturn(date(date, 181));

		Mockito.when(tc01.start()).thenReturn(date(date, 360));
		Mockito.when(tc02.start()).thenReturn(date(date, 360));
		Mockito.when(tc01.share()).thenReturn(share01);
		Mockito.when(tc02.share()).thenReturn(share02);
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(Arrays.asList(tc01, tc02));

		final TimeCourse tc11 = Mockito.mock(TimeCourse.class);

		Mockito.when(tc11.code()).thenReturn("CODE01");
		final TimeCourse tc12 = Mockito.mock(TimeCourse.class);
		Mockito.when(tc12.code()).thenReturn("CODE02");
		Mockito.when(tc11.variance()).thenReturn(1e-6);
		Mockito.when(tc12.variance()).thenReturn(1.5e-6);

		Mockito.when(tc11.covariance(tc12)).thenReturn(0.25e-6);
		Mockito.when(tc12.covariance(tc11)).thenReturn(0.25e-6);

		Mockito.when(tc11.share()).thenReturn(share01);
		Mockito.when(tc12.share()).thenReturn(share02);

		Mockito.when(tc11.rates()).thenReturn(Arrays.asList(new DataImpl(date(date, 180), 60d), new DataImpl(new Date(date), 70d)));

		Mockito.when(tc12.rates()).thenReturn(Arrays.asList(new DataImpl(date(date, 180), 40d), new DataImpl(new Date(date), 50d)));
		
		
		Mockito.when(tc11.dividends()).thenReturn(Arrays.asList(new DataImpl(date(date, 180), 6d)));

		Mockito.when(tc12.dividends()).thenReturn(Arrays.asList(new DataImpl(date(date, 180), 4d)));
		

		final Map<String, TimeCourse> timeCourses = new HashMap<>();
		timeCourses.put(tc11.code(), tc11);
		timeCourses.put(tc12.code(), tc12);

		setField(ExchangeRateCalculator.class, exchangeRateCalculator);
		setField(Map.class, timeCourses);

		ReflectionTestUtils.setField(builder, COMMITTED_SHARE_PORTFOLIO_FIELD, sharePortfolio);

		final Map<TimeCourse, Double> weights = new HashMap<>();
		weights.put(tc01, 0.5d);
		weights.put(tc02, 0.5d);

		Mockito.when(sharePortfolio.min()).thenReturn(weights);
		final ExchangeRate er01 = new ExchangeRateImpl("EUR", "EUR");
		// Mockito.mock(ExchangeRate.class);
		final ExchangeRate er02 = new ExchangeRateImpl("USD", "EUR");

		// Mockito.mock(ExchangeRate.class);
		Mockito.when(sharePortfolio.exchangeRate(tc01)).thenReturn(er01);
		Mockito.when(sharePortfolio.exchangeRate(tc02)).thenReturn(er02);

		Mockito.when(exchangeRateCalculator.factor(er01, tc01.end())).thenReturn(1D);
		Mockito.when(exchangeRateCalculator.factor(er02, tc02.end())).thenReturn(1.25D);

		Mockito.when(exchangeRateCalculator.factor(er01, tc01.start())).thenReturn(1D);
		Mockito.when(exchangeRateCalculator.factor(er02, tc02.start())).thenReturn(1.25D);

		Mockito.when(exchangeRateCalculator.factor(er01, date(date, 180))).thenReturn(1D);
		Mockito.when(exchangeRateCalculator.factor(er02, date(date, 180))).thenReturn(1.25D);

		Mockito.when(exchangeRateCalculator.factor(er01, date(date, 0))).thenReturn(1D);
		Mockito.when(exchangeRateCalculator.factor(er02, date(date, 0))).thenReturn(1.25D);
		
		

		Mockito.when(sharePortfolio.minWeights()).thenReturn(new double[] { 0.6, 0.4 });

		final SharePortfolioRetrospective result = builder.build();

		Assert.assertEquals(sharePortfolio, result.committedSharePortfolio());
		Assert.assertEquals(2, result.currentSharePortfolio().timeCourses().size());
		result.currentSharePortfolio().timeCourses().stream().map(tc -> tc.code()).forEach(code -> Assert.assertTrue(timeCourses.containsKey(code)));
		Assert.assertEquals(timeCourses.size() + 1, result.timeCoursesWithExchangeRate().size());
		Assert.assertEquals(PORTFOLIO_NAME, result.timeCoursesWithExchangeRate().stream().map(t -> t.name()).findFirst().get());
		Assert.assertTrue(result.timeCoursesWithExchangeRate().stream().map(tcr -> tcr.name()).filter(name -> name.equals(share01.name())).findAny().isPresent());
		Assert.assertTrue(result.timeCoursesWithExchangeRate().stream().map(tcr -> tcr.name()).filter(name -> name.equals(share02.name())).findAny().isPresent());

		result.timeCoursesWithExchangeRate().stream().filter(tcr -> tcr.name().equals(PORTFOLIO_NAME)).forEach(tcr -> {
			Assert.assertEquals(55d, tcr.start());
			Assert.assertEquals(66.25d, tcr.end());
		});

		result.timeCoursesWithExchangeRate().stream().filter(tcr -> tcr.name().equals(share01.name())).forEach(tcr -> {
			Assert.assertEquals(30d, tcr.start());
			Assert.assertEquals(35d, tcr.end());
		});

		result.timeCoursesWithExchangeRate().stream().filter(tcr -> tcr.name().equals(share02.name())).forEach(tcr -> {
			Assert.assertEquals(25d, tcr.start());
			Assert.assertEquals(31.25d, tcr.end());
		});
		Assert.assertEquals(date(date, 0), result.endRateWithExchangeRate().date());
		Assert.assertEquals(66.25d, result.endRateWithExchangeRate().value());

		Assert.assertEquals(date(date, 181), result.initialRateWithExchangeRate().date());

		Assert.assertEquals(55d, result.initialRateWithExchangeRate().value());

		
		Assert.assertEquals(Math.sqrt(0.72e-6), result.standardDeviation());

		Assert.assertEquals((67d - 56d) / 56d, result.totalRate());
		
		Assert.assertEquals(5.6d/56d, result.totalRateDividends());
		

	}

	private Date date(final Long date, long offset) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date(date - (long) (60 * 60 * 24 * 1000d * offset)));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	private void setField(final Class<?> clazz, final Object value) {
		Arrays.asList(SharePortfolioRetrospectiveBuilderImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(clazz)).forEach(field -> ReflectionTestUtils.setField(builder, field.getName(), value));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void buildTimeCoursesEmpty() {
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		builder.withCommitedSharePortfolio(sharePortfolio);
		builder.build();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void buildTimeCoursesOnlyOneExists() {
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		builder.withCommitedSharePortfolio(sharePortfolio);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Mockito.when(share.code()).thenReturn(CODE);
		builder.withTimeCourse(timeCourse);
		builder.build();
	}
	
	@Test
	public void declaredConstructorTimeCourse() {
		Assert.assertNotNull(((SharePortfolioRetrospectiveBuilderImpl)builder).declaredConstructor(SharePortfolioRetrospectiveBuilderImpl.TIME_COURSE_PATH, Share.class, Collection.class, Collection.class));
	}
	
	@Test(expected=IllegalStateException.class)
	public void declaredConstructorTimeCourseSucks() {
		Assert.assertNotNull(((SharePortfolioRetrospectiveBuilderImpl)builder).declaredConstructor(SharePortfolioRetrospectiveBuilderImpl.TIME_COURSE_PATH, Share.class));
	}
	
	@Test
	public void declaredConstructorShare() {
		Assert.assertNotNull(((SharePortfolioRetrospectiveBuilderImpl)builder).declaredConstructor(SharePortfolioRetrospectiveBuilderImpl.SHARE_PATH, String.class, String.class, String.class, String.class, String.class));
	}
	@Test(expected=IllegalStateException.class)
	public void declaredConstructorShareSucks() {
		Assert.assertNotNull(((SharePortfolioRetrospectiveBuilderImpl)builder).declaredConstructor(SharePortfolioRetrospectiveBuilderImpl.SHARE_PATH,String.class, String.class));
	}

}
