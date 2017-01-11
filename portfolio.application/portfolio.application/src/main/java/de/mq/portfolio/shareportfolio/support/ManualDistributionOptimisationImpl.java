package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

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
		return weights(sharePortfolio.varianceMatrix().length);
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
