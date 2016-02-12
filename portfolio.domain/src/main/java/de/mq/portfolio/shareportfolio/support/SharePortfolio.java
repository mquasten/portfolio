package de.mq.portfolio.shareportfolio.support;

import java.util.List;

import de.mq.portfolio.share.TimeCourse;

interface SharePortfolio {

	List<TimeCourse> timeCourses();

	double[] variances();

	double[][] getCovariances();

	double[][] getCorrelation();

}