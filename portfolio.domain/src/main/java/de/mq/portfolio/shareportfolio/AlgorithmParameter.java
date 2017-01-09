package de.mq.portfolio.shareportfolio;

public interface AlgorithmParameter {
	
	String name();
	default boolean isVector() {
		return false;
	}

}
