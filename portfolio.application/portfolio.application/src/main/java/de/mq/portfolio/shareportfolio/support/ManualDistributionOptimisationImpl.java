package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.portfolio.shareportfolio.AlgorithmParameter;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;

@Service
class ManualDistributionOptimisationImpl implements OptimisationAlgorithm {

	
	enum ParameterType {
		Weights;
	}


	
	@Override
	public double[] weights(final double[][] varianceMatrix, final AlgorithmParameter ... params) {
		final double[] results =  Arrays.asList(params).stream().filter(param -> param.type()== ParameterType.Weights).map(p -> (double[]) p.value()).findAny().orElse(weights(varianceMatrix.length)); 
	    Assert.isTrue(varianceMatrix.length == results.length, String.format("WeightingsVector has wrong size, expected: %s, size: %s", varianceMatrix.length , results.length));
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
	public Collection<Enum<?>> params() {
		return Collections.unmodifiableList(Arrays.asList(ParameterType.values()));
		
	}
}
