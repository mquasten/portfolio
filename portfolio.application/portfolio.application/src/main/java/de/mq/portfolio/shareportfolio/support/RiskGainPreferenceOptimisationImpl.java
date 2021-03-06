package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import Jama.Matrix;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.shareportfolio.AlgorithmParameter;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Service
class RiskGainPreferenceOptimisationImpl implements OptimisationAlgorithm {

	final ExchangeRateService exchangeRateService;

	@Autowired
	RiskGainPreferenceOptimisationImpl(final ExchangeRateService exchangeRateService) {
		this.exchangeRateService = exchangeRateService;
	}

	enum ParameterType implements AlgorithmParameter {
		TargetRate, RateRatio, UseExchangeRates;

		@Override
		public boolean isVector() {
			return false;
		}

	}

	@Override
	public AlgorithmType algorithmType() {
		return AlgorithmType.RiskGainPreference;
	}

	@Override
	public double[] weights(final SharePortfolio sharePortfolio) {

		final double[][] varianceMatrix = sharePortfolio.varianceMatrix();

		final Double targetRate = sharePortfolio.param(ParameterType.TargetRate);
		final Double rateRatio = sharePortfolio.param(ParameterType.RateRatio);

		final Double useExchangeRates = sharePortfolio.param(ParameterType.UseExchangeRates) != null ? sharePortfolio.param(ParameterType.UseExchangeRates) : 0d;

		Assert.isTrue(useExchangeRates == 1d || useExchangeRates == 0d, ParameterType.UseExchangeRates + " should be 0 or 1, default is 0.");
		Assert.isTrue(!(targetRate != null && rateRatio != null), "TargetRate and RateRatio are mutuall exclusive");

		final double[][] array = new double[varianceMatrix.length + 1][varianceMatrix.length + 1];
		IntStream.range(0, varianceMatrix.length).forEach(i -> IntStream.range(0, varianceMatrix.length).forEach(j -> array[i][j] = varianceMatrix[i][j]));

		IntStream.range(0, varianceMatrix.length).forEach(i -> {

			array[i][varianceMatrix.length] = 1;
			array[varianceMatrix.length][i] = 1;
		});
		array[varianceMatrix.length][varianceMatrix.length] = 0d;

		final Matrix inverseMatrix = new Matrix(array).inverse();

		final Matrix m = new Matrix(varianceMatrix.length, varianceMatrix.length);
		IntStream.range(0, varianceMatrix.length).forEach(i -> IntStream.range(0, varianceMatrix.length).forEach(j -> m.set(i, j, inverseMatrix.get(i, j))));

		final double[] gain = gain2(sharePortfolio, useExchangeRates != 0d);
		final double[][] gainVector = gainVector(varianceMatrix, gain);
		final Matrix d = m.times(new Matrix(gainVector));

		final double ke = IntStream.range(0, varianceMatrix.length).mapToDouble(i -> d.get(i, 0) * gain[i]).sum();

		final double[] weightsMVP = new double[varianceMatrix.length];
		IntStream.range(0, varianceMatrix.length).forEach(i -> weightsMVP[i] = inverseMatrix.get(varianceMatrix.length, i));

		final double totalGainMVP = IntStream.range(0, varianceMatrix.length).mapToDouble(i -> weightsMVP[i] * gain[i]).sum();

		final double theta = theta(targetRate, rateRatio, ke, totalGainMVP);
		
		Assert.isTrue(theta >= 0, "Θ should be >= 0.");

		final double[] results = new double[varianceMatrix.length];
		IntStream.range(0, varianceMatrix.length).forEach(i -> results[i] = weightsMVP[i] + theta * d.get(i, 0));

		return results;
	}

	private double[][] gainVector(final double[][] varianceMatrix, final double[] gain) {
		final double[][] gainVector = new double[varianceMatrix.length][1];

		IntStream.range(0, gain.length).forEach(i -> gainVector[i][0] = gain[i]);
		return gainVector;
	}

	private double[] gain2(final SharePortfolio sharePortfolio, boolean useExchangeRates) {
		return sharePortfolio.totalRates(useExchangeRates ? exchangeRateService.exchangeRateCalculator(sharePortfolio.exchangeRateTranslations()) : (er, date) -> 1);
	}

	private double theta(final Double targetRate, final Double rateRatio, final double ke, final double totalGainMVP) {

		if (targetRate != null) {
			return (targetRate - totalGainMVP) / ke;
		}

		if (rateRatio != null) {
			return totalGainMVP * (rateRatio - 1) / ke;
		}
		return 0;
	}

	@Override
	public Collection<AlgorithmParameter> params() {
		return Collections.unmodifiableCollection(Arrays.asList(ParameterType.values()));
	}

}
