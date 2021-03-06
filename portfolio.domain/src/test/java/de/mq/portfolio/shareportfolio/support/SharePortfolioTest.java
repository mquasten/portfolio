package de.mq.portfolio.shareportfolio.support;

import java.sql.Date;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateImpl;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.AlgorithmParameter;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm.AlgorithmType;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import org.junit.Assert;

public class SharePortfolioTest {

	private static final double PARAMETER_VALUE = 42d;

	private static final String PARAMETER_NAME = "parameterName";

	private static final String CURRENCY_USD = "USD";

	private static final String CODE = "CODE";

	private static final String SHARE_NAME_02 = "Share02";

	private static final String SHARE_NAME_01 = "Share01";

	private static final String NEW_SHARE_NAME = "Coca Cola";


	static final String VARIANCES_FIELD = "variances";

	static final String COVARIANCES_FIELD = "covariances";

	static final String CORRELATIONS_FIELD = "correlations";

	static final String COLLECTION = "Portfolio";

	static final String NAME = "mq-test";

	private final List<TimeCourse> timeCourses = new ArrayList<>();

	private SharePortfolio sharePortfolio;

	private double[] variances = new double[] { 1d / 144d, 1d / (24d * 24d) };

	private double[][] covariances = new double[2][2];

	private double[][] correlations = new double[2][2];

	private final TimeCourse timeCourse1 = Mockito.mock(TimeCourse.class);
	private final TimeCourse timeCourse2 = Mockito.mock(TimeCourse.class);

	private final double[] weights = new double[] { 1d / 3d, 2d / 3d };

	private final Share share = Mockito.mock(Share.class);
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	private final Share share1 = Mockito.mock(Share.class);
	private final Share share2 = Mockito.mock(Share.class);
	private final Map<TimeCourse, Double> minWeights = new HashMap<>();
	
	private final AlgorithmParameter algorithmParameter = Mockito.mock(AlgorithmParameter.class);

	final ExchangeRate exchangeRateUSDEuro = new ExchangeRateImpl(CURRENCY_USD, SharePortfolioImpl.DEFAULT_CURRENCY);
	final ExchangeRate exchangeRateEuroEuro = new ExchangeRateImpl(SharePortfolioImpl.DEFAULT_CURRENCY, SharePortfolioImpl.DEFAULT_CURRENCY);


	private Date startDate = Mockito.mock(Date.class);
	private Date endDate = Mockito.mock(Date.class);
	
	private final OptimisationAlgorithm optimisationAlgorithm = Mockito.mock(OptimisationAlgorithm.class);

	// page 38 Performancemessung example results
	private final double standardDerivation = Math.round(100 * 10.81 / Math.sqrt(12)) / 100d;
	
	private final static double[] TOTAL_RATES =  {25d, 42.22d};

	@Before
	public void setup() {

		Mockito.when(algorithmParameter.name()).thenReturn(PARAMETER_NAME);
		Mockito.when(optimisationAlgorithm.algorithmType()).thenReturn(AlgorithmType.MVP);
		
		Mockito.when(share.name()).thenReturn(NEW_SHARE_NAME);
		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Mockito.when(timeCourse.name()).thenReturn(NEW_SHARE_NAME);

		randomId(timeCourse);
		randomId(timeCourse1);
		randomId(timeCourse2);

		Mockito.when(share1.name()).thenReturn(SHARE_NAME_01);
		Mockito.when(share1.code()).thenReturn(CODE);
		Mockito.when(timeCourse1.name()).thenReturn(SHARE_NAME_01);

		Mockito.when(share2.name()).thenReturn(SHARE_NAME_02);
		Mockito.when(share2.code()).thenReturn(CODE);
		Mockito.when(timeCourse2.name()).thenReturn(SHARE_NAME_02);

		Mockito.when(timeCourse1.share()).thenReturn(share1);
		Mockito.when(timeCourse2.share()).thenReturn(share2);

		timeCourses.add(timeCourse1);
		timeCourses.add(timeCourse2);
		sharePortfolio = new SharePortfolioImpl(NAME, timeCourses);
		covariances[0][1] = 1d / (12d * 24);
		covariances[1][0] = 1d / (12d * 24);

		correlations[0][0] = 1d;
		correlations[1][1] = 1d;
		correlations[0][1] = 1d / (12d * 24 * Math.sqrt(variances[0]) * Math.sqrt(variances[1]));
		correlations[1][0] = 1d / (12d * 24 * Math.sqrt(variances[0]) * Math.sqrt(variances[1]));

		ReflectionTestUtils.setField(sharePortfolio, VARIANCES_FIELD, variances);
		ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD, covariances);
		ReflectionTestUtils.setField(sharePortfolio, CORRELATIONS_FIELD, correlations);


		// page 38 Performancemessung example results
		minWeights.put(timeCourse1, 54.50d);
		minWeights.put(timeCourse2, 44.29d);
		minWeights.put(timeCourse, 1.21d);
		
		
		ReflectionUtils.doWithFields(SharePortfolioImpl.class, field -> ReflectionTestUtils.setField(sharePortfolio, field.getName(), optimisationAlgorithm), field -> field.getType().equals(OptimisationAlgorithm.class));

		Mockito.when(optimisationAlgorithm.weights(sharePortfolio)).thenReturn(new double [] {1e-2* minWeights.get(timeCourse1), 1e-2 *minWeights.get(timeCourse2), 1e-2 *minWeights.get(timeCourse)});

	}

	private void randomId(TimeCourse timeCourse) {
		Mockito.when(timeCourse.id()).thenReturn("" + Math.random() * 1e16);
	}

	@Test
	public final void variance() {

		Assert.assertEquals((Double) (1d / (9d * 36d)), (Double) sharePortfolio.risk(weights));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void varianceWrongData() {
		ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD, new double[1][1]);
		sharePortfolio.risk(weights);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void weightWrong() {
		sharePortfolio.risk(new double[] { 1d, 2d, 3d });
	}

	@Test
	public final void variances() {
		Assert.assertEquals(variances, ((SharePortfolioImpl) sharePortfolio).variances());
	}

	@Test
	public final void covariances() {
		Assert.assertArrayEquals(covariances, ((SharePortfolioImpl) sharePortfolio).covariances());
	}

	@Test
	public final void correlations() {
		Assert.assertArrayEquals(correlations, ((SharePortfolioImpl) sharePortfolio).correlations());
	}

	@Test
	public final void name() {
		Assert.assertEquals(NAME, sharePortfolio.name());
	}

	@Test
	public final void commit() {
		Assert.assertFalse(sharePortfolio.isCommitted());
		sharePortfolio.commit();
		Assert.assertTrue(sharePortfolio.isCommitted());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void commitNoTimeCourses() {
		new SharePortfolioImpl(NAME, new ArrayList<>()).commit();
	}


	@Test
	public final void timeCourses() {
		Assert.assertEquals(timeCourses, sharePortfolio.timeCourses());
	}

	@Test
	public final void onBeforeSave() {
		Mockito.when(timeCourse1.variance()).thenReturn(1e-3);
		Mockito.when(timeCourse1.variance()).thenReturn(2e-3);
		Mockito.when(timeCourse1.covariance(timeCourse1)).thenReturn(4e-6);
		Mockito.when(timeCourse2.covariance(timeCourse2)).thenReturn(5e-6);

		Mockito.when(timeCourse1.covariance(timeCourse2)).thenReturn(6e-3);
		Mockito.when(timeCourse2.covariance(timeCourse1)).thenReturn(7e-3);

		Assert.assertTrue(((SharePortfolioImpl) sharePortfolio).onBeforeSave());

		final double[] variances = (double[]) ReflectionTestUtils.getField(sharePortfolio, VARIANCES_FIELD);
		Assert.assertEquals(2, variances.length);
		Assert.assertEquals((Double) timeCourse1.variance(), (Double)  variances[0]);
		Assert.assertEquals((Double) timeCourse2.variance(), (Double) variances[1]);

		final double[][] covariances = (double[][]) ReflectionTestUtils.getField(sharePortfolio, COVARIANCES_FIELD);
		Assert.assertEquals(2, covariances.length);
		Assert.assertEquals(2, covariances[0].length);
		Assert.assertEquals(2, covariances[1].length);

		Assert.assertEquals((Double) timeCourse1.covariance(timeCourse1), (Double) covariances[0][0]);
		Assert.assertEquals((Double) timeCourse2.covariance(timeCourse2), (Double) covariances[1][1]);
		Assert.assertEquals((Double)timeCourse1.covariance(timeCourse2), (Double)covariances[0][1]);
		Assert.assertEquals((Double)timeCourse2.covariance(timeCourse1), (Double)covariances[1][0]);

		final double[][] correlations = (double[][]) ReflectionTestUtils.getField(sharePortfolio, CORRELATIONS_FIELD);
		final int[] counter = { 0 };
		IntStream.range(0, 2).forEach(i -> {
			IntStream.range(0, 2).forEach(j -> {
				Assert.assertEquals((Double) (covariances[i][j] / (Math.sqrt(variances[i]) * Math.sqrt(variances[j]))),(Double)correlations[i][j]);
				counter[0] = counter[0] + 1;
			});

		});
		Assert.assertEquals(4, counter[0]);
	}

	@Test
	public final void onBeforeSaveNoTimeCourses() {
		Assert.assertFalse(new SharePortfolioImpl(NAME, new ArrayList<>()).onBeforeSave());
	}

	@Test
	public final void annotations() {
		Assert.assertTrue(SharePortfolioImpl.class.isAnnotationPresent(Document.class));
		Assert.assertEquals(COLLECTION, SharePortfolioImpl.class.getAnnotation(Document.class).collection());
	}

	@Test
	public final void assignTimeCourse() {

		Assert.assertEquals(timeCourses, sharePortfolio.timeCourses());
		sharePortfolio.assign(timeCourse);
		Assert.assertEquals(timeCourses.size() + 1, sharePortfolio.timeCourses().size());

		final Optional<TimeCourse> result = sharePortfolio.timeCourses().stream().filter(tc -> tc.share().name() == share.name()).findFirst();
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(timeCourse, result.get());
	}

	@Test
	public final void assignTimeCourseExists() {
		Assert.assertEquals(timeCourses, sharePortfolio.timeCourses());
		Mockito.when(share.name()).thenReturn(SHARE_NAME_01);
		sharePortfolio.assign(timeCourse);
		Assert.assertEquals(timeCourses.size(), sharePortfolio.timeCourses().size());
		Assert.assertEquals(timeCourses.size(), sharePortfolio.timeCourses().stream().filter(tc -> tc.share().equals(share1) || tc.share().equals(share2)).count());
	}

	@Test
	public final void assignTimeCourseCollection() {
		Assert.assertEquals(timeCourses, sharePortfolio.timeCourses());
		final Collection<TimeCourse> tcs = Arrays.asList(timeCourse);
		sharePortfolio.assign(tcs);
		Assert.assertEquals(1, sharePortfolio.timeCourses().size());
		Assert.assertTrue(sharePortfolio.timeCourses().stream().findAny().isPresent());
		Assert.assertEquals(timeCourse, sharePortfolio.timeCourses().stream().findAny().get());

	}

	@Test(expected = IllegalArgumentException.class)
	public final void assignTimeCourseCollectionNameGuard() {
		Mockito.when(share.name()).thenReturn(null);
		sharePortfolio.assign(Arrays.asList(timeCourse));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void assignTimeCourseCollectionCodeGuard() {
		Mockito.when(share.code()).thenReturn(null);
		sharePortfolio.assign(Arrays.asList(timeCourse));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void assignTimeCourseCollectionIdGuard() {
		Mockito.when(timeCourse.id()).thenReturn(null);
		sharePortfolio.assign(Arrays.asList(timeCourse));
	}

	@Test
	public final void removeTimeCourse() {
		Assert.assertEquals(timeCourses, sharePortfolio.timeCourses());
	
		sharePortfolio.remove(timeCourse1);
		Assert.assertEquals(1, sharePortfolio.timeCourses().size());
		Assert.assertTrue(sharePortfolio.timeCourses().stream().findAny().isPresent());
		Assert.assertEquals(timeCourse2, sharePortfolio.timeCourses().stream().findAny().get());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void removeTimeCourseIdMissing() {
		Mockito.when(timeCourse.name()).thenReturn(null);
		Mockito.when(timeCourse.id()).thenReturn(null);
		sharePortfolio.remove(timeCourse);
	}

	@Test
	public final void id() {
		Assert.assertNull(sharePortfolio.id());
		final String id = "" + Math.random() * 1e16;
		ReflectionUtils.doWithFields(SharePortfolioImpl.class, field -> ReflectionTestUtils.setField(sharePortfolio, field.getName(), id), field -> field.isAnnotationPresent(Id.class));
		Assert.assertEquals(id, sharePortfolio.id());
	}

	@Test
	public final void min() {

		preparePortfolioForMinWeightTest();


		
		final Map<TimeCourse, Double> results = sharePortfolio.min();

		Assert.assertEquals(timeCourses.size() + 1, results.size());

		timeCourses.forEach(tc -> results.containsKey(tc));
		Assert.assertTrue(results.containsKey(timeCourse));

		Assert.assertEquals((Double) minWeights.get(timeCourse1), (Double) percentRound(results.get(timeCourse1)));
		Assert.assertEquals((Double) minWeights.get(timeCourse2), (Double) percentRound(results.get(timeCourse2)));
		Assert.assertEquals((Double) minWeights.get(timeCourse), (Double) percentRound(results.get(timeCourse)));
		
		
	}

	@Test
	public final void minNoTimeCourses() {
		resetTimeCourses();
		Assert.assertTrue(sharePortfolio.timeCourses().isEmpty());
		Assert.assertTrue(sharePortfolio.min().isEmpty());
	}

	@Test
	public final void minVariancesNull() {
		Assert.assertFalse(sharePortfolio.timeCourses().isEmpty());
		ReflectionTestUtils.setField(sharePortfolio, VARIANCES_FIELD, null);
		Assert.assertTrue(sharePortfolio.min().isEmpty());
	}

	@Test
	public final void minCovariancesNull() {
		Assert.assertFalse(sharePortfolio.timeCourses().isEmpty());
		ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD, null);
		Assert.assertTrue(sharePortfolio.min().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void minVariancesSize() {
		Assert.assertFalse(sharePortfolio.timeCourses().isEmpty());
		ReflectionTestUtils.setField(sharePortfolio, VARIANCES_FIELD, new double[] { 0 });
		sharePortfolio.min();
	}

	@Test(expected = IllegalArgumentException.class)
	public final void minCovariancesSize() {
		Assert.assertFalse(sharePortfolio.timeCourses().isEmpty());
		ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD, new double[][] { new double[] { 0 } });
		sharePortfolio.min();
	}

	@Test(expected = IllegalArgumentException.class)
	public final void minCovariancesLineSize() {
		preparePortfolioForMinWeightTest();
		final double[][] covariances = (double[][]) ReflectionTestUtils.getField(sharePortfolio, COVARIANCES_FIELD);
		covariances[covariances.length - 1] = new double[] { 0 };
		sharePortfolio.min();
	}

	@Test
	public final void minWeights() {
		preparePortfolioForMinWeightTest();
		final double[] results = sharePortfolio.minWeights();
		Assert.assertEquals(sharePortfolio.timeCourses().size(), results.length);
		IntStream.range(0, sharePortfolio.timeCourses().size()).forEach(i -> Assert.assertEquals((Double) minWeights.get(sharePortfolio.timeCourses().get(i)), (Double) percentRound(results[i])));
	}

	@Test
	public final void standardDeviation() {
		preparePortfolioForMinWeightTest();
		Assert.assertEquals((Double) standardDerivation, (Double)  percentRound(sharePortfolio.standardDeviation()));
	}

	@Test
	public final void standardDeviationNull() {
		resetTimeCourses();
		Assert.assertNull(sharePortfolio.standardDeviation());
	}

	private void resetTimeCourses() {
		ReflectionUtils.doWithFields(sharePortfolio.getClass(), field -> ReflectionTestUtils.setField(sharePortfolio, field.getName(), new ArrayList<>()), field -> field.getType().equals(List.class));
	}

	@Test
	public final void standardDeviationWithWeights() {
		preparePortfolioForMinWeightTest();
		Assert.assertEquals((Double) standardDerivation, (Double) percentRound(sharePortfolio.standardDeviation(new double[] { minWeights.get(timeCourse1) / 100d, minWeights.get(timeCourse2) / 100d, minWeights.get(timeCourse) / 100d })));
	}

	@Test
	public final void standardDeviationWithWeightsNull() {
		resetTimeCourses();
		sharePortfolio.standardDeviation(new double[] { 0.5, 0.5 });
		Assert.assertNull(sharePortfolio.standardDeviation());
	}

	@Test
	public final void currency() {
		Assert.assertEquals(SharePortfolioImpl.DEFAULT_CURRENCY, sharePortfolio.currency());
	}

	@Test
	public final void defaultConstructor() {
		Assert.assertNotNull(BeanUtils.instantiateClass(SharePortfolioImpl.class));
	}

	private void preparePortfolioForMinWeightTest() {
		Mockito.when(timeCourse1.name()).thenReturn("All REITs");
		Mockito.when(timeCourse2.name()).thenReturn("S&P 500");
		Mockito.when(timeCourse.name()).thenReturn("Euro Stoxx 50");

		// page 37, 38 Performancemessung
		sharePortfolio.assign(timeCourse);
		covariances = new double[3][3];
		variances = new double[]  {0.0014023, 0.0015854, 0.0028889 };
		IntStream.range(0, 3).forEach(i -> covariances[i][i] = variances[i]);
		covariances[0][1] = 0.0004629;
		covariances[0][2] = 0.0004031;

		covariances[1][0] = 0.0004629;
		covariances[1][2] = 0.0016245;

		covariances[2][0] = 0.0004031;
		covariances[2][1] = 0.0016245;

		ReflectionTestUtils.setField(sharePortfolio, VARIANCES_FIELD, variances);
		ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD, covariances);
	}

	private double percentRound(double value) {
		return Math.round(10000 * value) / 100d;
	}

	@Test
	public final void correlationEntries() {

		final double correlation = 0.25d;
		ReflectionTestUtils.setField(sharePortfolio, CORRELATIONS_FIELD, new double[][] { new double[] { 1, correlation }, new double[] { 1, correlation } });

		final List<Entry<String, Map<String, Double>>> results = sharePortfolio.correlationEntries();

		Assert.assertEquals((Double) 1d, (Double) filterEntry(results, share1.name()).get(share1.name()));
		Assert.assertEquals((Double) correlation, (Double) filterEntry(results, share1.name()).get(share2.name()));
		Assert.assertEquals(filterEntry(results, share1.name()), filterEntry(results, share2.name()));

	}

	@Test
	public final void correlationEntriesArrayNull() {
		ReflectionTestUtils.setField(sharePortfolio, CORRELATIONS_FIELD, null);
		Assert.assertTrue(sharePortfolio.correlationEntries().isEmpty());
	}

	@Test
	public final void correlationEntriesWrongSize() {
		resetTimeCourses();
		Assert.assertTrue(sharePortfolio.correlationEntries().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void correlationEntriesWrongLineSize() {

		ReflectionTestUtils.setField(sharePortfolio, CORRELATIONS_FIELD, new double[][] { new double[] { 1, 1 }, new double[] { 1 } });
		sharePortfolio.correlationEntries();

	}

	private Map<String, Double> filterEntry(final List<Entry<String, Map<String, Double>>> results, final String name) {
		return results.stream().filter(e -> e.getKey().equals(name)).map(e -> e.getValue()).findAny().get();
	}

	@Test
	public final void totalRate() {
		// example markowitz.pdf
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();

		Assert.assertEquals((Double) percentRound(47d / 14d / 9d), (Double) percentRound(sharePortfolio.totalRate(weights, exchangeRateCalculator)));

	}

	@Test
	public final void totalRateNoTimeCourses() {
		// example markowitz.pdf
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();
		resetTimeCourses();

		sharePortfolio.totalRate(weights, exchangeRateCalculator);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void totalRateWeightsGuard() {
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();
		sharePortfolio.totalRate(new double[] { 1 / 3d, 1 / 1 / 3d, 1 / 3d }, exchangeRateCalculator);
	}

	private ExchangeRateCalculator prepareForMinWeights() {
		final List<Data> ratesShare1 = prepareRate(20, 25);
		Mockito.when(timeCourse1.rates()).thenReturn(ratesShare1);
		Mockito.when(share1.currency()).thenReturn(SharePortfolioImpl.DEFAULT_CURRENCY);
		List<Data> ratesShare2 = prepareRate(30, 40);
		Mockito.when(timeCourse2.rates()).thenReturn(ratesShare2);
		Mockito.when(share2.currency()).thenReturn(CURRENCY_USD);

		final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
		final ExchangeRate exchangeRate = new ExchangeRateImpl(CURRENCY_USD, SharePortfolioImpl.DEFAULT_CURRENCY);
		final ExchangeRate exchangeRate2 = new ExchangeRateImpl(SharePortfolioImpl.DEFAULT_CURRENCY, SharePortfolioImpl.DEFAULT_CURRENCY);

		Mockito.when(exchangeRateCalculator.factor(exchangeRate, startDate)).thenReturn(1 / 1.2d);
		Mockito.when(exchangeRateCalculator.factor(exchangeRate2, startDate)).thenReturn(1d);

		Mockito.when(exchangeRateCalculator.factor(exchangeRate, endDate)).thenReturn(1 / 1.125d);
		Mockito.when(exchangeRateCalculator.factor(exchangeRate2, endDate)).thenReturn(1d);
		return exchangeRateCalculator;
	}

	private List<Data> prepareRate(final double start, final double end) {
		final Data dataStart = Mockito.mock(Data.class);
		Mockito.when(dataStart.value()).thenReturn(start);
		Mockito.when(dataStart.date()).thenReturn(startDate);
		final Data dataEnd = Mockito.mock(Data.class);
		Mockito.when(dataEnd.value()).thenReturn(end);
		Mockito.when(dataEnd.date()).thenReturn(endDate);
		return Arrays.asList(dataStart, dataEnd);

	}

	@Test
	public final void totalRateDefaultWeights() {
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();
		// ugly, nasty, dirrrrrrrrty ...
		final SharePortfolio mock = Mockito.spy(sharePortfolio);

		//Mockito.when(mock.minWeights()).thenReturn(weights);

		Mockito.doAnswer(a-> weights).when(mock).minWeights();
		
		Assert.assertEquals((Double)percentRound(47d / 14d / 9d), (Double)percentRound(mock.totalRate(exchangeRateCalculator)));
	}
	
	@Test
	public final void totalRates() {
		
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();
		// ugly, nasty, dirrrrrrrrty ...
		final SharePortfolio mock = Mockito.spy(sharePortfolio);
		Mockito.doAnswer(a-> weights).when(mock).minWeights();
		
		final double[] results = mock.totalRates(exchangeRateCalculator);
		Assert.assertEquals(TOTAL_RATES.length, results.length);
		
		IntStream.range(0, TOTAL_RATES.length).forEach(i -> Assert.assertEquals((Double) TOTAL_RATES[i],(Double) percentRound(results[i])));
		
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void totalRatesNoTimeCourses() {
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();
	
		final SharePortfolio mock = Mockito.spy(sharePortfolio);
		Mockito.doAnswer(a-> weights).when(mock).minWeights();
		
		timeCourses.forEach(ic -> mock.remove(ic));
		
		mock.totalRates(exchangeRateCalculator);
	}
	

	@Test
	public final void totalRateDividends() {
		// example markowitz.pdf
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();

		prepareDividends(exchangeRateCalculator);

		Assert.assertEquals((Double) percentRound(11d / 70d), (Double) percentRound(sharePortfolio.totalRateDividends(weights, exchangeRateCalculator)));
	}

	@Test
	public final void totalRateDividendsTimeCourseMissing() {
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();

		prepareDividends(exchangeRateCalculator);

		resetTimeCourses();

		Assert.assertNull(sharePortfolio.totalRateDividends(weights, exchangeRateCalculator));
	}

	private void prepareDividends(final ExchangeRateCalculator exchangeRateCalculator) {
		final Date firstDividendDate = Mockito.mock(Date.class);
		final Date secondDividendDate = Mockito.mock(Date.class);

		// example markowitz.pdf
		Mockito.when(exchangeRateCalculator.factor(exchangeRateEuroEuro, firstDividendDate)).thenReturn(1.0d);
		Mockito.when(exchangeRateCalculator.factor(exchangeRateUSDEuro, firstDividendDate)).thenReturn(1 / 1.2d);
		Mockito.when(exchangeRateCalculator.factor(exchangeRateUSDEuro, secondDividendDate)).thenReturn(1 / 1.125);

		final List<Data> dividends1 = prepareDividends(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(firstDividendDate, 5d)));
		Mockito.when(timeCourse1.dividends()).thenReturn(dividends1);

		final List<Data> dividends2 = prepareDividends(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(firstDividendDate, 2d), new AbstractMap.SimpleImmutableEntry<>(secondDividendDate, 1.5d)));
		Mockito.when(timeCourse2.dividends()).thenReturn(dividends2);
	}

	private List<Data> prepareDividends(final Collection<Entry<Date, Double>> entries) {
		final List<Data> dividends = new ArrayList<>();

		entries.forEach(entry -> {
			final Data dataStart = Mockito.mock(Data.class);
			Mockito.when(dataStart.value()).thenReturn(entry.getValue());
			Mockito.when(dataStart.date()).thenReturn(entry.getKey());
			dividends.add(dataStart);
		});
		return dividends;
	}

	@Test
	public final void totalRateDividendsMinWeights() {
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();
		prepareDividends(exchangeRateCalculator);
		// ugly, nasty, dirrrrrrrrty ...
		final SharePortfolio mock = Mockito.spy(sharePortfolio);

		
		Mockito.doAnswer(a -> weights).when(mock).minWeights();
		
		//Mockito.when(mock.minWeights()).thenReturn(weights);
		Assert.assertEquals((Double) percentRound(11d / 70d), (Double) percentRound(mock.totalRateDividends(exchangeRateCalculator)));
	}

	@Test
	public final void exchangeRateTranslations() {
		Mockito.when(share1.currency()).thenReturn(SharePortfolioImpl.DEFAULT_CURRENCY);
		Mockito.when(share2.currency()).thenReturn(CURRENCY_USD);

		final Collection<ExchangeRate> results = sharePortfolio.exchangeRateTranslations();

		Assert.assertEquals(1, results.size());
		final ExchangeRate result = results.stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Result expected"));
		Assert.assertEquals(SharePortfolioImpl.DEFAULT_CURRENCY, result.source());

		Assert.assertEquals(CURRENCY_USD, result.target());
	}
	
	@Test
	public final void create() {
		final SharePortfolio sharePortfolio = new SharePortfolioImpl(NAME, Arrays.asList(timeCourse1, timeCourse2, timeCourse), optimisationAlgorithm);
	    Assert.assertEquals(optimisationAlgorithm, sharePortfolio.optimisationAlgorithm());
	    Assert.assertEquals(NAME, sharePortfolio.name());
	    Assert.assertEquals(Arrays.asList(timeCourse1, timeCourse2, timeCourse), sharePortfolio.timeCourses());
	}
	
	@Test
	public final void varianceMatrix() {
		preparePortfolioForMinWeightTest();
		final double[][] results = sharePortfolio.varianceMatrix();
		Assert.assertEquals(variances.length, results.length);
		IntStream.range(0, variances.length).forEach(i -> Assert.assertEquals(variances.length, results[i].length) );
		
	
		IntStream.range(0, variances.length).forEach(i -> Assert.assertEquals((Double) variances[i], (Double) results[i][i]));
		
		IntStream.range(0, variances.length).forEach(i -> IntStream.range(0, variances.length).filter(j -> i != j).forEach(j -> Assert.assertEquals((Double) covariances[i][j], (Double) results[i][j]))); 
	}
	
	@Test
	public final void param() {
		Assert.assertNull(sharePortfolio.param(algorithmParameter));
		sharePortfolio.assign(algorithmParameter, PARAMETER_VALUE);
		Assert.assertEquals((Double) PARAMETER_VALUE, (Double)sharePortfolio.param(algorithmParameter));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void paramNotScalar() {
		Mockito.when(algorithmParameter.isVector()).thenReturn(true);
		sharePortfolio.param(algorithmParameter);
	}
	
	
	@Test
	public final void  paramVector() {
		Mockito.when(algorithmParameter.isVector()).thenReturn(true);
		Assert.assertNull(sharePortfolio.param(algorithmParameter, 0));
		Assert.assertTrue(sharePortfolio.parameterVector(algorithmParameter).isEmpty());
		sharePortfolio.assign(algorithmParameter, Arrays.asList(PARAMETER_VALUE, PARAMETER_VALUE));
		
		IntStream.range(0, 2).forEach(i -> Assert.assertEquals((Double) PARAMETER_VALUE, (Double) sharePortfolio.param(algorithmParameter, i)));
		
		Assert.assertEquals(Arrays.asList(PARAMETER_VALUE, PARAMETER_VALUE), sharePortfolio.parameterVector(algorithmParameter));
		
		Assert.assertNull(sharePortfolio.param(algorithmParameter, Integer.MAX_VALUE));
	}
	
	@Test
	public final void  algorithmType() {
		Assert.assertEquals(optimisationAlgorithm.algorithmType(), sharePortfolio.algorithmType());
	}
	
	
	@Test
	public final void clearParameter() {
		sharePortfolio.assign(algorithmParameter, PARAMETER_VALUE);
		Assert.assertNotNull(sharePortfolio.param(algorithmParameter));
	
		sharePortfolio.clearParameter();
		
		Assert.assertNull(sharePortfolio.param(algorithmParameter));
		
	}
	
	

}
