package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm.AlgorithmType;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;

public class RiskGainPreferenceOptimisationTest {

	private static final double RATE_RATIO = 1.1107248492475688;
	private static final double TARGET_RATE = 0.018769;
	private final double[][] matrix = new double[3][3];

	private final ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class);
	private final OptimisationAlgorithm optimisationAlgorithm = new RiskGainPreferenceOptimisationImpl(exchangeRateService);

	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);

	private final TimeCourse timeCourses[] = new TimeCourse[RATES.length];

	private final ArgumentCaptor<ExchangeRateCalculator> exchangeRateCalculatorCaptor = ArgumentCaptor.forClass(ExchangeRateCalculator.class);

	private final ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);

	private static final double[] WEIGHTS = { 28.99, 25.00, 46.01 };

	private static final double[] WEIGHTS_MVP = { 45.56, 25.41, 29.03 };

	private static final double[] RATES = { 0.0139178, 0.0130860, 0.0249126 };

	@Before
	public void setup() {

		matrix[0][0] = 0.0005786;
		matrix[0][1] = 0.0003741;
		matrix[0][2] = 0.0002840;

		matrix[1][0] = 0.0003741;
		matrix[1][1] = 0.0010420;
		matrix[1][2] = 0.0000202;

		matrix[2][0] = 0.0002840;
		matrix[2][1] = 0.0000202;
		matrix[2][2] = 0.0010562;

		IntStream.range(0, RATES.length).forEach(i -> timeCourses[i] = Mockito.mock(TimeCourse.class));

		IntStream.range(0, RATES.length).forEach(i -> Mockito.when(timeCourses[i].totalRate()).thenReturn(RATES[i]));

		Mockito.when(sharePortfolio.varianceMatrix()).thenReturn(matrix);
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(Arrays.asList(timeCourses));

		Mockito.when(sharePortfolio.totalRates(Mockito.any(ExchangeRateCalculator.class))).thenReturn(RATES);

	}

	@Test
	public final void weightsTotalRate() {

		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.RateRatio)).thenReturn(null);

		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.TargetRate)).thenReturn(TARGET_RATE);
		final double[] results = optimisationAlgorithm.weights(sharePortfolio);

		Assert.assertEquals(matrix.length, results.length);

		IntStream.range(0, WEIGHTS.length).forEach(i -> Assert.assertEquals(WEIGHTS[i], percentRound(results[i])));

	}

	@Test
	public final void weightsRateRatio() {
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.TargetRate)).thenReturn(null);
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.RateRatio)).thenReturn(RATE_RATIO);
		final double[] results = optimisationAlgorithm.weights(sharePortfolio);

		Assert.assertEquals(matrix.length, results.length);

		IntStream.range(0, WEIGHTS.length).forEach(i -> Assert.assertEquals(WEIGHTS[i], percentRound(results[i])));

	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void weightsRateRatioThetaGuard() {
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.TargetRate)).thenReturn(null);
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.RateRatio)).thenReturn(0d);
		final double[] results = optimisationAlgorithm.weights(sharePortfolio);

		Assert.assertEquals(matrix.length, results.length);

		IntStream.range(0, WEIGHTS.length).forEach(i -> Assert.assertEquals(WEIGHTS[i], percentRound(results[i])));

	}

	@Test
	public final void weightsMVP() {
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.TargetRate)).thenReturn(null);
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.RateRatio)).thenReturn(null);

		final double[] results = optimisationAlgorithm.weights(sharePortfolio);

		Assert.assertEquals(matrix.length, results.length);

		IntStream.range(0, WEIGHTS.length).forEach(i -> Assert.assertEquals(WEIGHTS_MVP[i], percentRound(results[i])));

	}

	@Test(expected = IllegalArgumentException.class)
	public final void weightsTotalRateTheataWrong() {

		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.TargetRate)).thenReturn(0d);
		optimisationAlgorithm.weights(sharePortfolio);

	}

	@Test
	public final void weightsNoExchangeRate() {
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.TargetRate)).thenReturn(null);
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.RateRatio)).thenReturn(RATE_RATIO);
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.UseExchangeRates)).thenReturn(null);
		optimisationAlgorithm.weights(sharePortfolio);

		Mockito.verify(sharePortfolio).totalRates(exchangeRateCalculatorCaptor.capture());

		Assert.assertEquals(1d, exchangeRateCalculatorCaptor.getValue().factor(exchangeRate, new Date()));

	}

	@Test
	public final void weightsWithExchangeRate() {
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.TargetRate)).thenReturn(null);
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.RateRatio)).thenReturn(RATE_RATIO);
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.UseExchangeRates)).thenReturn(1d);

		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(Arrays.asList(exchangeRate));
		final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
		Mockito.when(exchangeRateService.exchangeRateCalculator(Arrays.asList(exchangeRate))).thenReturn(exchangeRateCalculator);
		optimisationAlgorithm.weights(sharePortfolio);

		Mockito.verify(sharePortfolio).totalRates(exchangeRateCalculatorCaptor.capture());

		Assert.assertEquals(exchangeRateCalculator, exchangeRateCalculatorCaptor.getValue());
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public final void weightsExchangeRateGuard() {
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.TargetRate)).thenReturn(null);
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.RateRatio)).thenReturn(RATE_RATIO);
		Mockito.when(sharePortfolio.param(RiskGainPreferenceOptimisationImpl.ParameterType.UseExchangeRates)).thenReturn(0.5d);
		optimisationAlgorithm.weights(sharePortfolio);
	}

	private double percentRound(double value) {
		return Math.round(10000 * value) / 100d;
	}

	@Test
	public void algorithmType() {
		Assert.assertEquals(AlgorithmType.RiskGainPreference, optimisationAlgorithm.algorithmType());
	}

	@Test
	public void params() {
		Assert.assertEquals(RiskGainPreferenceOptimisationImpl.ParameterType.values().length, optimisationAlgorithm.params().size());
		Assert.assertTrue(optimisationAlgorithm.params().contains(RiskGainPreferenceOptimisationImpl.ParameterType.RateRatio));
		Assert.assertTrue(optimisationAlgorithm.params().contains(RiskGainPreferenceOptimisationImpl.ParameterType.TargetRate));
	}

	@Test
	public final void parameter() {
		Arrays.asList(RiskGainPreferenceOptimisationImpl.ParameterType.values()).forEach(value -> {
			Assert.assertEquals(value, RiskGainPreferenceOptimisationImpl.ParameterType.valueOf(value.name()));
			Assert.assertFalse(value.isVector());
		});
	}

}
