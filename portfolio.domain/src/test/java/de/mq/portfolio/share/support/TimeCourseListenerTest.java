package de.mq.portfolio.share.support;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

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
	
	@SuppressWarnings("unchecked")
	private final BeforeSaveEvent<TimeCourseImpl> event = Mockito.mock(BeforeSaveEvent.class);
	
	@Before
	public void setup() {
		Mockito.when(timeCourse.share()).thenReturn(share);
		Mockito.when(timeCourse.meanRate()).thenReturn(MEAN_RATE);
		Mockito.when(timeCourse.variance()).thenReturn(VARIANCE);
		Mockito.when(share.code()).thenReturn(CODE);
		
		Mockito.when(event.getDBObject()).thenReturn(dbo);
		Mockito.when(event.getSource()).thenReturn(timeCourse);
	}
	
	
	@Test
	public void onBeforeSave() {
		listener.onBeforeSave(timeCourse, dbo);
		
		verifyDBObject();
	}


	private void verifyDBObject() {
		Mockito.verify(dbo).put(TimeCourseListenerImpl.CODE, CODE);
		Mockito.verify(dbo).put(TimeCourseListenerImpl.VARIANCE, VARIANCE);
		Mockito.verify(dbo).put(TimeCourseListenerImpl.MEAN_RATE, MEAN_RATE);
	}

	@Test
	public void onBeforeSaveEvent() {
		listener.onBeforeSave(event);
		
		verifyDBObject();
	}
}
