package de.mq.portfolio.shareportfolio.support;

import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Service
public class SimpleOptimisationImpl implements OptimisationAlgorithm {

	@Override
	public double[] weights(SharePortfolio sharePortfolio) {
		final double[] results = new double[sharePortfolio.timeCourses().size()];
		
		IntStream.range(0, results.length).forEach(i -> results[i]= 1d/results.length);
		return results;
	}

	@Override
	public AlgorithmType algorithmType() {
		return AlgorithmType.ManualDistribution;
	}

}
