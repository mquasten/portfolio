package de.mq.portfolio.shareportfolio;

import java.util.List;

import de.mq.portfolio.share.TimeCourse;

public interface SharePortfolio {

	List<TimeCourse> timeCourses();

	double risk(double[] weightingVector);

	boolean isCommitted();
	
	

}