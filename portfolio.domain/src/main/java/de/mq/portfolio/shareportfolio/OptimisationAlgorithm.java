package de.mq.portfolio.shareportfolio;

import java.util.Collection;

public interface  OptimisationAlgorithm {
	
	public enum AlgorithmType {
		MVP,
		RiskGainPreference,
		ManualDistribution
	}
	
	
	
	
	AlgorithmType algorithmType();

	double[] weights(final SharePortfolio sharePortfolio);

	Collection<Enum<?>> params();

}
