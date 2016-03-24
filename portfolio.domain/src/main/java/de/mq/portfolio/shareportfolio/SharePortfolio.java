package de.mq.portfolio.shareportfolio;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import de.mq.portfolio.share.TimeCourse;

public interface SharePortfolio {

	List<TimeCourse> timeCourses();

	double risk(double[] weightingVector);

	boolean isCommitted();

	void commit();

	String name();

	Optional<PortfolioOptimisation> minVariance();

	double standardDeviation();

	String id();
	
	void assign(final TimeCourse timeCourse);

	void remove(final TimeCourse timeCourse);

	List<Entry<String, Double>> standardDeviations();

	List<Entry<String, Map<String, Double>>> correlationEntries();

	List<Entry<String, Double>> min();

}