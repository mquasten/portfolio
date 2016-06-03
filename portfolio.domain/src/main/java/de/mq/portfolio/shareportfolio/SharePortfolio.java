package de.mq.portfolio.shareportfolio;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.TimeCourse;

public interface SharePortfolio {

	List<TimeCourse> timeCourses();

	double risk(double[] weightingVector);

	boolean isCommitted();

	void commit();

	String name();

	Optional<PortfolioOptimisation> minVariance();


	String id();
	
	void assign(final TimeCourse timeCourse);

	void remove(final TimeCourse timeCourse);



	List<Entry<String, Map<String, Double>>> correlationEntries();

	Map<TimeCourse, Double> min();

	@Deprecated
	Double totalRate(final double[] weights);

	
	@Deprecated
	Double totalRateDividends(final double[] weights);

	void assign(Collection<TimeCourse> timeCourses);

	double[] minWeights();

	Double standardDeviation();

	@Deprecated
	Double totalRate();

	@Deprecated
	Double totalRateDividends();

	String currency();

	ExchangeRate exchangeRate(TimeCourse timeCourse);

	Collection<ExchangeRate> exchangeRateTranslations();

	Double totalRateDividends(double[] weights, ExchangeRateCalculator exchangeRateCalculator);

	Double totalRate(double[] weights, ExchangeRateCalculator exchangeRateCalculator);

	

	

}