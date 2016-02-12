package de.mq.portfolio.shareportfolio;

import java.util.List;

import de.mq.portfolio.share.TimeCourse;

public interface SharePortfolio {

	List<TimeCourse> timeCourses();

	double[] variances();

	double[][] getCovariances();

	double[][] correlations();

}