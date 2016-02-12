package de.mq.portfolio.share;

import java.util.List;



public interface TimeCourse {

	

	public abstract Share share();

	double meanRate();

	double variance();

	double covariance(final TimeCourse other);

	List<Data> rates();

	double correlation(final TimeCourse other);

	

}
