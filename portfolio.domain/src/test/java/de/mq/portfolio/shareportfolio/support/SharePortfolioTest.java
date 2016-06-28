package de.mq.portfolio.shareportfolio.support;

import java.sql.Date;
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
import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;

public class SharePortfolioTest {
	
	

	private static final String CODE = "CODE";

	private static final String SHARE_NAME_02 = "Share02";

	private static final String SHARE_NAME_01 = "Share01";

	private static final String NEW_SHARE_NAME = "Coca Cola";

	private static final String MIN_VARIANCE_FIELD = "minVariance";

	static final String VARIANCES_FIELD = "variances";

	static final String COVARIANCES_FIELD = "covariances";

	static final String CORRELATIONS_FIELD = "correlations";

	static final String COLLECTION = "Portfolio";

	static final String NAME = "mq-test";

	private final List<TimeCourse> timeCourses = new ArrayList<>(); 
	
	private  SharePortfolio sharePortfolio;
	
	private final double[] variances = new double[] {1d/144d , 1d/(24d*24d) }; 
   
	private final double[] []  covariances = new double[2][2] ;
	
	private final double[] []  correlations = new double[2][2] ;
   
	private final TimeCourse timeCourse1 = Mockito.mock(TimeCourse.class);
	private final TimeCourse timeCourse2 = Mockito.mock(TimeCourse.class);
	
	private final double[] weights = new double[] { 1d/3d , 2d/3d};
	
	private final Share share = Mockito.mock(Share.class);
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	private final Share share1 = Mockito.mock(Share.class);
	private final Share share2 = Mockito.mock(Share.class);
	private final Map<TimeCourse,Double> minWeights = new HashMap<>();

	private final PortfolioOptimisation portfolioOptimisation = Mockito.mock(PortfolioOptimisation.class);
	
	private Date startDate = Mockito.mock(Date.class);
	private Date endDate = Mockito.mock(Date.class);
	
	// page 38 Performancemessung example results
	private final  double standardDerivation = Math.round(100*10.81/Math.sqrt(12))/100d;
   
	@Before
	public void setup() {
		
		Mockito.when(share.name()).thenReturn(NEW_SHARE_NAME);
		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		
		randomId(timeCourse);
		randomId(timeCourse1);
		randomId(timeCourse2);
		
		
		
		Mockito.when(share1.name()).thenReturn(SHARE_NAME_01);
		Mockito.when(share1.code()).thenReturn(CODE);
		
		Mockito.when(share2.name()).thenReturn(SHARE_NAME_02);
		Mockito.when(share2.code()).thenReturn(CODE);
		
		
		
		Mockito.when(timeCourse1.share()).thenReturn(share1);
		Mockito.when(timeCourse2.share()).thenReturn(share2);
		
		timeCourses.add(timeCourse1);
		timeCourses.add(timeCourse2);
		sharePortfolio = new SharePortfolioImpl(NAME, timeCourses);
	   covariances[0][1]=1d/(12d*24);
	   covariances[1][0]=1d/(12d*24);
		
	  
	   correlations[0][0]=1d;
	   correlations[1][1]=1d;
	   correlations[0][1]=1d/(12d*24 * Math.sqrt(variances[0])* Math.sqrt(variances[1]));
	   correlations[1][0]=1d/(12d*24* Math.sqrt(variances[0])* Math.sqrt(variances[1]));
	   
	   ReflectionTestUtils.setField(sharePortfolio, VARIANCES_FIELD, variances);
	   ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD, covariances);
	   ReflectionTestUtils.setField(sharePortfolio, CORRELATIONS_FIELD, correlations);
	   
	   ReflectionTestUtils.setField(sharePortfolio, MIN_VARIANCE_FIELD, portfolioOptimisation);
	   
	   // page 38 Performancemessung example results
	   minWeights.put(timeCourse1, 54.50d);
	   minWeights.put(timeCourse2, 44.29d);
	   minWeights.put(timeCourse, 1.21d);
	}
	
	


	private void randomId(TimeCourse timeCourse) {
		Mockito.when(timeCourse.id()).thenReturn(""+ Math.random()*1e16);
	}
	
	
	@Test
	public final void variance() {
		
		
		Assert.assertEquals(1d/(9d*36d), sharePortfolio.risk(weights));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void varianceWrongData() {
		 ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD, new double[1][1]);
		 sharePortfolio.risk(weights);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void weightWrong() {
		sharePortfolio.risk(new double[] { 1d,2d,3d});
	}

	@Test
	public final void variances() {
		Assert.assertEquals(variances, ((SharePortfolioImpl)sharePortfolio).variances());
	}
	

	@Test
	public final void covariances() {
		Assert.assertEquals(covariances, ((SharePortfolioImpl)sharePortfolio).covariances());
	}

	
	@Test
	public final void correlations() {
		Assert.assertEquals(correlations, ((SharePortfolioImpl)sharePortfolio).correlations());
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
	
	@Test(expected=IllegalArgumentException.class)
	public final void commitNoTimeCourses() {
		new SharePortfolioImpl(NAME, new ArrayList<>()).commit();
	}
	
	@Test
	public final void minVariance() {
		Assert.assertEquals(Optional.of(portfolioOptimisation), sharePortfolio.minVariance());
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
		
		Assert.assertTrue(((SharePortfolioImpl)sharePortfolio).onBeforeSave());
		
		final double[] variances = (double[]) ReflectionTestUtils.getField(sharePortfolio, VARIANCES_FIELD);
		Assert.assertEquals(2, variances.length);
		Assert.assertEquals(timeCourse1.variance(), variances[0]);
		Assert.assertEquals(timeCourse2.variance(), variances[1]);
		
		final double[][] covariances = (double[][]) ReflectionTestUtils.getField(sharePortfolio, COVARIANCES_FIELD);
		Assert.assertEquals(2, covariances.length);
		Assert.assertEquals(2, covariances[0].length);
		Assert.assertEquals(2, covariances[1].length);
		
		Assert.assertEquals(timeCourse1.covariance(timeCourse1), covariances[0][0]);
		Assert.assertEquals(timeCourse2.covariance(timeCourse2), covariances[1][1]);
		Assert.assertEquals(timeCourse1.covariance(timeCourse2), covariances[0][1]);
		Assert.assertEquals(timeCourse2.covariance(timeCourse1), covariances[1][0]);
		
		final double[][] correlations = (double[][]) ReflectionTestUtils.getField(sharePortfolio, CORRELATIONS_FIELD);
		final int[] counter = { 0};
		IntStream.range(0, 2).forEach(i -> {
			IntStream.range(0, 2).forEach(j -> {
				Assert.assertEquals(covariances[i][j] / ( Math.sqrt(variances[i])*  Math.sqrt(variances[j])), correlations[i][j]);
				counter[0]= counter[0] +1;
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
		Assert.assertEquals(timeCourses.size()+1, sharePortfolio.timeCourses().size());
	
		final Optional<TimeCourse> result = sharePortfolio.timeCourses().stream().filter(tc -> tc.share().name()==share.name()).findFirst();
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(timeCourse, result.get());
	}
	
	@Test
	public final void assignTimeCourseExists() {
		Assert.assertEquals(timeCourses, sharePortfolio.timeCourses());
		Mockito.when(share.name()).thenReturn(SHARE_NAME_01);	
		sharePortfolio.assign(timeCourse);
		Assert.assertEquals(timeCourses.size(), sharePortfolio.timeCourses().size());
		Assert.assertEquals(timeCourses.size(), sharePortfolio.timeCourses().stream().filter(tc -> tc.share().equals(share1)||tc.share().equals(share2)).count());
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
	
	@Test(expected=IllegalArgumentException.class)
	public final void assignTimeCourseCollectionNameGuard() {
		Mockito.when(share.name()).thenReturn(null);
		sharePortfolio.assign(Arrays.asList(timeCourse));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void assignTimeCourseCollectionCodeGuard() {
		Mockito.when(share.code()).thenReturn(null);
		sharePortfolio.assign(Arrays.asList(timeCourse));
	}
	
	@Test(expected=IllegalArgumentException.class)
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
	
	@Test(expected=IllegalArgumentException.class)
	public final void removeTimeCourseIdMissing() {
		Mockito.when(timeCourse.id()).thenReturn(null);
		sharePortfolio.remove(timeCourse);
	}
	
	@Test
	public final void id() {
		Assert.assertNull(sharePortfolio.id());
		final String id = "" + Math.random()*1e16;
		ReflectionUtils.doWithFields(SharePortfolioImpl.class, field -> ReflectionTestUtils.setField(sharePortfolio,field.getName(), id ) , field -> field.isAnnotationPresent(Id.class));
	    Assert.assertEquals(id, sharePortfolio.id()); 
	}
	
	@Test
	public final void min() {
		
		preparePortfolioForMinWeightTest();
		
		final Map<TimeCourse, Double> results= sharePortfolio.min();
		
		Assert.assertEquals(timeCourses.size()+1, results.size());
		
		timeCourses.forEach(tc -> results.containsKey(tc));
		Assert.assertTrue(results.containsKey(timeCourse));
		
		Assert.assertEquals(minWeights.get(timeCourse1), percentRound(results.get(timeCourse1)));
		Assert.assertEquals(minWeights.get(timeCourse2), percentRound(results.get(timeCourse2)));
		Assert.assertEquals(minWeights.get(timeCourse), percentRound(results.get(timeCourse)));
		
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
	
	@Test(expected=IllegalArgumentException.class)
	public final void minVariancesSize() {
		Assert.assertFalse(sharePortfolio.timeCourses().isEmpty());
		ReflectionTestUtils.setField(sharePortfolio, VARIANCES_FIELD, new double[]{0});
		sharePortfolio.min();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void minCovariancesSize() {
		Assert.assertFalse(sharePortfolio.timeCourses().isEmpty());
		ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD, new double[][]{new double[]{0}});
		sharePortfolio.min();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void minCovariancesLineSize() {
		preparePortfolioForMinWeightTest();
		final double[][] covariances = (double[][]) ReflectionTestUtils.getField(sharePortfolio, COVARIANCES_FIELD);
		covariances[covariances.length-1]=new double[]{0};
		sharePortfolio.min();
	}
	
	@Test
	public final void minWeights() {
		preparePortfolioForMinWeightTest();
		final double[] results = sharePortfolio.minWeights();
		Assert.assertEquals(sharePortfolio.timeCourses().size(), results.length);
		IntStream.range(0, sharePortfolio.timeCourses().size()).forEach(i -> Assert.assertEquals(minWeights.get(sharePortfolio.timeCourses().get(i)), percentRound(results[i])));
	}
	
	@Test
	public final void standardDeviation() {
		preparePortfolioForMinWeightTest();
		Assert.assertEquals(standardDerivation, percentRound(sharePortfolio.standardDeviation()));
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
	public final void  standardDeviationWithWeights() {
		preparePortfolioForMinWeightTest();
		Assert.assertEquals(standardDerivation, percentRound(sharePortfolio.standardDeviation(new double[] { minWeights.get(timeCourse1)/100d, minWeights.get(timeCourse2)/100d, minWeights.get(timeCourse)/100d})));
	}
	
	@Test
	public final void  standardDeviationWithWeightsNull() {
		resetTimeCourses(); 
		sharePortfolio.standardDeviation(new double[] {0.5, 0.5});
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
		final double[][] covariances = new double[3][3];
		final double[] variances = new double[] {0.0014023, 0.0015854, 0.0028889};
		IntStream.range(0, 3).forEach(i -> covariances[i][i]=variances[i]);
		covariances[0][1]=0.0004629;
		covariances[0][2]=0.0004031;
		
		covariances[1][0]=0.0004629;
		covariances[1][2]=0.0016245;
		
		covariances[2][0]=0.0004031;
		covariances[2][1]=0.0016245;
		
		
		ReflectionTestUtils.setField(sharePortfolio, VARIANCES_FIELD,  variances);
		ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD,  covariances);
	}
	
	
	private double percentRound(double value) {
		return Math.round(10000 * value)/100d;
	}
	
	@Test
	public final void  correlationEntries() {
	
		final double correlation = 0.25d;
		ReflectionTestUtils.setField(sharePortfolio, CORRELATIONS_FIELD, new double[][]{new double[] {1, correlation}, new double[] {1, correlation}});
		
		final List<Entry<String,Map<String,Double>>> results = sharePortfolio.correlationEntries();
		
		Assert.assertEquals(1d, filterEntry(results, share1.name()).get(share1.name()));
		Assert.assertEquals(correlation, filterEntry(results, share1.name()).get(share2.name()));
		Assert.assertEquals( filterEntry(results, share1.name()),  filterEntry(results, share2.name()));
		
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
	
	@Test(expected=IllegalArgumentException.class)
	public final void correlationEntriesWrongLineSize() {
		
		ReflectionTestUtils.setField(sharePortfolio, CORRELATIONS_FIELD, new double[][]{new double[] {1, 1}, new double[] {1}});
		sharePortfolio.correlationEntries();
		
	}


	private Map<String, Double> filterEntry(final List<Entry<String, Map<String, Double>>> results, final String name) {
		return results.stream().filter(e -> e.getKey().equals(name)).map(e -> e.getValue()).findAny().get();
	}
	
	
	@Test
	public final void totalRate() {
		// example markowitz.pdf
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();
	
		
		Assert.assertEquals(percentRound(47d/14d/9d), percentRound(sharePortfolio.totalRate(weights, exchangeRateCalculator)));
		
		
	}
	
	@Test
	public final void totalRateNoTimeCourses() {
		// example markowitz.pdf
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();
		resetTimeCourses();
		
		sharePortfolio.totalRate(weights, exchangeRateCalculator);
		
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void totalRateWeightsGuard() {
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();
		sharePortfolio.totalRate(new double[]{1/3d, 1/1/3d, 1/3d}, exchangeRateCalculator);
	}
	
	
	private ExchangeRateCalculator prepareForMinWeights() {
		final List<Data> ratesShare1 = prepareRate(20,25);
		Mockito.when(timeCourse1.rates()).thenReturn(ratesShare1);
		Mockito.when(share1.currency()).thenReturn("EUR");
		List<Data> ratesShare2 = prepareRate(30,40);
		Mockito.when(timeCourse2.rates()).thenReturn(ratesShare2);
		Mockito.when(share2.currency()).thenReturn("USD");
		
		final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
		final ExchangeRate exchangeRate = new ExchangeRateImpl("USD", "EUR");
		final ExchangeRate exchangeRate2 = new ExchangeRateImpl("EUR", "EUR");
	
		Mockito.when(exchangeRateCalculator.factor(exchangeRate, startDate)).thenReturn(1/1.2d);
		Mockito.when(exchangeRateCalculator.factor(exchangeRate2, startDate)).thenReturn(1d);
		
		
		
		Mockito.when(exchangeRateCalculator.factor(exchangeRate, endDate)).thenReturn(1/1.125d);
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
		return Arrays.asList(dataStart,dataEnd);
		
	}
	
	
	@Test
	public final void totalRateDefaultWeights() {
		final ExchangeRateCalculator exchangeRateCalculator = prepareForMinWeights();
		//ugly, nasty, dirrrrrrrrty  ...
		final SharePortfolio mock = Mockito.spy(sharePortfolio);
		
		Mockito.when(mock.minWeights()).thenReturn(weights);
		
		Assert.assertEquals(percentRound(47d/14d/9d), percentRound(mock.totalRate(exchangeRateCalculator)));
	}
	
	

	
}
