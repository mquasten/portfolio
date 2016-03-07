package de.mq.portfolio.share.support;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;

@Component
class TimeCourseListenerImpl extends AbstractMongoEventListener<TimeCourseImpl> {

	static final String STANDARD_DEVIATION = "standardDeviation";
	static final String TOTAL_RATE_DIVIDENDS = "totalRateDividends";
	static final String TOTAL_RATE = "totalRate";
	static final String CODE = "code";
	static final String VARIANCE = "variance";
	static final String MEAN_RATE = "meanRate";

	@Override
	public void onBeforeSave(final TimeCourseImpl timeCourse, final DBObject dbo) {
		 timeCourse.onBeforeSave();
		 dbo.put(MEAN_RATE, timeCourse.meanRate());
		 dbo.put(VARIANCE, timeCourse.variance());
		 dbo.put(CODE , timeCourse.share().code());
		 
		 dbo.put(TOTAL_RATE , timeCourse.totalRate());
		 dbo.put(TOTAL_RATE_DIVIDENDS , timeCourse.totalRateDividends());
		 dbo.put(STANDARD_DEVIATION , timeCourse.standardDeviation());
	}
	
	

}
