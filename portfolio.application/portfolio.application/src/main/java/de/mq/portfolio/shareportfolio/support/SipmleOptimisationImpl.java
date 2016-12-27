package de.mq.portfolio.shareportfolio.support;

import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Service
public class SipmleOptimisationImpl implements OptimisationAlgorithm {

	@Override
	public double[] weights(SharePortfolio sharePortfolio) {
		final double[] results = new double[sharePortfolio.timeCourses().size()];
		
		IntStream.rangeClosed(0, results.length).forEach(i -> results[i]= 1d/results.length);
		return results;
	}

}
