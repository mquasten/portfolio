package de.mq.portfolio.share.support;

import java.util.Collection;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

public interface RealTimeRateRestRepository {

	/**
	 * Get the RealTimeRates as a Collection of Data with entry from today and yesterday
	 * @param shares the share for that the rates will be needed 
	 * @return a Collection of TimeCourses with 2 entries, realtime rate from today and rate from yesterday
	 */
	Collection<TimeCourse> rates(Collection<Share> shares);

}