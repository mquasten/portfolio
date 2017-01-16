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
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.AlgorithmParameter;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm.AlgorithmType;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;

public class PortfolioAOTest {

	private static final String RESPONSE = "response";
	private static final String PARAMETER_NAME = "parameterName";
	private static final Double PARAMETER_VALUE = new Double(42);
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

	private final OptimisationAlgorithm optimisationAlgorithm = Mockito.mock(OptimisationAlgorithm.class);
	private final OptimisationAlgorithm optimisationAlgorithm2 = Mockito.mock(OptimisationAlgorithm.class);
	
	@Before
	public final void setup() {

		Mockito.when(optimisationAlgorithm.algorithmType()).thenReturn(AlgorithmType.MVP);
		Mockito.when(optimisationAlgorithm2.algorithmType()).thenReturn(AlgorithmType.RiskGainPreference);
		portfolioAO.setOptimisationAlgorithms(Arrays.asList(optimisationAlgorithm, optimisationAlgorithm2));
		
		Mockito.when(optimisationAlgorithm.params()).thenReturn(Arrays.asList());
		
		
		Mockito.when(optimisationAlgorithm2.params()).thenReturn(Arrays.asList());
		
		
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
		portfolioAO.setName(NAME);
		final SharePortfolio sharePortfolio = portfolioAO.getSharePortfolio();

		Assert.assertEquals(ID, sharePortfolio.id());

		Assert.assertEquals(NAME, sharePortfolio.name());

		Assert.assertEquals(AlgorithmType.MVP, sharePortfolio.algorithmType());
		Assert.assertEquals(optimisationAlgorithm, sharePortfolio.optimisationAlgorithm());
	}
	
	@Test
	public final void algorithmType() {
		Assert.assertEquals(AlgorithmType.MVP, portfolioAO.getAlgorithmType());
		
		portfolioAO.setAlgorithmType(AlgorithmType.RiskGainPreference);
		
		Assert.assertEquals(AlgorithmType.RiskGainPreference, portfolioAO.getAlgorithmType());
	}
	
	
	@Test
	public final void  getParameters() {
		final Map<String,Object> parameters = new HashMap<>();
		parameters.put(PARAMETER_NAME, PARAMETER_VALUE);
		Arrays.asList(portfolioAO.getClass().getDeclaredFields()).stream().filter(field -> field.getType().equals(Map.class)).forEach(field -> {
			ReflectionTestUtils.setField(portfolioAO, field.getName(), parameters);
		});
		
		Assert.assertEquals(1, portfolioAO.getParameters().size());
		
		Assert.assertEquals(PARAMETER_VALUE, portfolioAO.getParameters().get(PARAMETER_NAME));
	}
	
	@Test
	public final void  isInvalidParameters() {
		
		portfolioAO.setSharePortfolio(sharePortfolio, Optional.of(exchangeRateCalculator));
		Assert.assertFalse(portfolioAO.isInvalidParameters());
		
		Assert.assertNull(portfolioAO.getResponse());
		
		Mockito.when(optimisationAlgorithm.algorithmType()).thenReturn(AlgorithmType.MVP);
		
		portfolioAO.setAlgorithmType(AlgorithmType.MVP);
		
		Mockito.doThrow(new IllegalArgumentException(RESPONSE)).when(optimisationAlgorithm).weights(Mockito.any());
		
		portfolioAO.getSharePortfolio();
		
		Assert.assertTrue(portfolioAO.isInvalidParameters());
		Assert.assertEquals(RESPONSE, portfolioAO.getResponse());
		
		Mockito.doThrow(new IllegalArgumentException()).when(optimisationAlgorithm).weights(Mockito.any());
		portfolioAO.getSharePortfolio();
		Assert.assertTrue(portfolioAO.isInvalidParameters());
		Assert.assertEquals(IllegalArgumentException.class.getSimpleName(), portfolioAO.getResponse());
		
		
	}
	
	@Test
	public final void isVector() {
		
		portfolioAO.setAlgorithmType(AlgorithmType.MVP);
		
		AlgorithmParameter parameter = Mockito.mock(AlgorithmParameter.class);
		Mockito.when(parameter.isVector()).thenReturn(true);
		Mockito.when(optimisationAlgorithm.params()).thenReturn(Arrays.asList(parameter));
		Mockito.when(parameter.name()).thenReturn(PARAMETER_NAME);
		Assert.assertTrue(portfolioAO.isVector(PARAMETER_NAME));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void isVectorInvalid() {
		portfolioAO.isVector(PARAMETER_NAME);
	}

}
