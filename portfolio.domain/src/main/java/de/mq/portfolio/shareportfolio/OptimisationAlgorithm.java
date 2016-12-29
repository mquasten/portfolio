package de.mq.portfolio.shareportfolio;

public interface  OptimisationAlgorithm {
	
	public enum AlgorithmType {
		MVP,
		RiskGainPreference,
		ManualDistribution
	}
	
	
	double[] weights(final SharePortfolio sharePortfolio);
	
	AlgorithmType algorithmType();

}
