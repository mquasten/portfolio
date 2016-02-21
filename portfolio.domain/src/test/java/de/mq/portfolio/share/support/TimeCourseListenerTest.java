package de.mq.portfolio.share.support;

import org.junit.Test;
import org.mockito.Mockito;

import com.mongodb.DBObject;

import de.mq.portfolio.share.Share;


public class TimeCourseListenerTest {

	private static final double VARIANCE = 1e-6;

	private static final double MEAN_RATE = 1e-3;

	private static final String CODE = "^IDAXI";

	private final  TimeCourseListenerImpl listener = new TimeCourseListenerImpl();
	
	private final TimeCourseImpl timeCourse = Mockito.mock(TimeCourseImpl.class);
	
	private final DBObject dbo = Mockito.mock(DBObject.class);
	
	private final Share share = Mockito.mock(Share.class);
	
	@Test
	public void onBeforeSave() {
		Mockito.when(timeCourse.share()).thenReturn(share);
		Mockito.when(timeCourse.meanRate()).thenReturn(MEAN_RATE);
		Mockito.when(timeCourse.variance()).thenReturn(VARIANCE);
		Mockito.when(share.code()).thenReturn(CODE);
		
		listener.onBeforeSave(timeCourse, dbo);
		
		Mockito.verify(dbo).put(TimeCourseListenerImpl.CODE, CODE);
		Mockito.verify(dbo).put(TimeCourseListenerImpl.VARIANCE, VARIANCE);
		Mockito.verify(dbo).put(TimeCourseListenerImpl.MEAN_RATE, MEAN_RATE);
	}
	
}
