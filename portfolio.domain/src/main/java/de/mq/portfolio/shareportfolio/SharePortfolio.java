package de.mq.portfolio.shareportfolio;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import de.mq.portfolio.IdentifierAware;
import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm.AlgorithmType;

public interface SharePortfolio  extends IdentifierAware<String>{

	List<TimeCourse> timeCourses();

	double risk(double[] weightingVector);

	boolean isCommitted();

	void commit();

	String name();


	
	void assign(final TimeCourse timeCourse);

	void remove(final TimeCourse timeCourse);



	List<Entry<String, Map<String, Double>>> correlationEntries();

	Map<TimeCourse, Double> min();

	void assign(Collection<TimeCourse> timeCourses);

	double[] minWeights();

	Double standardDeviation();


	String currency();

	ExchangeRate exchangeRate(TimeCourse timeCourse);

	Collection<ExchangeRate> exchangeRateTranslations();

	Double totalRateDividends(double[] weights, ExchangeRateCalculator exchangeRateCalculator);

	Double totalRate(double[] weights, ExchangeRateCalculator exchangeRateCalculator);

	
	Double totalRate(final ExchangeRateCalculator exchangeRateCalculator);

	Double totalRateDividends(final ExchangeRateCalculator exchangeRateCalculator);

	Double standardDeviation(double[] weights);
	
	
	OptimisationAlgorithm optimisationAlgorithm() ;

	AlgorithmType algorithmType();

	double[][] varianceMatrix();

	



	Double param(final AlgorithmParameter key);

	void assign(final AlgorithmParameter key, final double value);
	void assign(final AlgorithmParameter key, final List<Double> values);
	

	void clearParameter();

	Double param(final AlgorithmParameter key, final int index);

	

	

}