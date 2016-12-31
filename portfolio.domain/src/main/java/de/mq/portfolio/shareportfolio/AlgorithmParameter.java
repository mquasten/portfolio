package de.mq.portfolio.shareportfolio;

public interface AlgorithmParameter {

	Enum<?> type();
	
	<T> T value(); 
	
	
}
