package de.mq.portfolio.share.support;

import java.util.ArrayList;

public class TimeCourseCriteriaImpl extends TimeCourseImpl {

	public TimeCourseCriteriaImpl(final String share){
		super(new ShareImpl("", share, "", "", ""), new ArrayList<>(), new ArrayList<>());
		
	}
	
}
