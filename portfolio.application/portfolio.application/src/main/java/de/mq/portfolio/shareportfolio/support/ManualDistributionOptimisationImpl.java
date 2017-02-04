package de.mq.portfolio.shareportfolio.support;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.mq.portfolio.shareportfolio.AlgorithmParameter;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.SharePortfolio;


@Service
class ManualDistributionOptimisationImpl implements OptimisationAlgorithm {

	
	enum ParameterType  implements AlgorithmParameter {
		Weights;

		@Override
		public boolean isVector() {
		
			return true;
		}
		
		
	}


	
	@Override
	public double[] weights(SharePortfolio sharePortfolio) {
		
		final List<Double> parameters = sharePortfolio.parameterVector(ParameterType.Weights);
		
		if(CollectionUtils.isEmpty(parameters)){
			return weights(sharePortfolio.varianceMatrix().length);	
		}
		
		Assert.isTrue(parameters.stream().filter(p -> p == null || p == 0d ).count() == 0, "Parameter values are mandatory and should be <> 0.");
		Assert.isTrue(parameters.stream().map(value -> BigDecimal.valueOf(value)).reduce( BigDecimal.ZERO, (a,b) -> a.add(b)).doubleValue() == 1d, "Sum of vector values must be 1.");
		
		final double[] results = new double[parameters.size()];
		IntStream.range(0, results.length).forEach(i -> results[i]= parameters.get(i));
		return results;
	}

	private double[] weights(final int size) {
		final double[] results = new double[size];
		IntStream.range(0, results.length).forEach(i -> results[i]= 1d/results.length);
		return results;
	}

	@Override
	public AlgorithmType algorithmType() {
		return AlgorithmType.ManualDistribution;
	}

	@Override
	public Collection<AlgorithmParameter> params() {
		return Collections.unmodifiableList(Arrays.asList(ParameterType.values()));
		
	}
}
