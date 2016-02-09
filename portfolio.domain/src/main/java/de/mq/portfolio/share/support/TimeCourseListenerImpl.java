package de.mq.portfolio.share.support;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

import com.mongodb.DBObject;

@Component
class TimeCourseListenerImpl extends AbstractMongoEventListener<TimeCourseImpl> {

	@Override
	public void onBeforeSave(final TimeCourseImpl timeCourse, final DBObject dbo) {
		 timeCourse.onBeforeSave();
		 dbo.put("meanRate", timeCourse.meanRate());
		 dbo.put("variance", timeCourse.variance());
	}
	
	

}
