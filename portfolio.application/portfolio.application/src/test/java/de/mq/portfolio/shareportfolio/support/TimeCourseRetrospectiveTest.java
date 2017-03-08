package de.mq.portfolio.shareportfolio.support;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.TimeCourse;
import org.junit.Assert;

public class TimeCourseRetrospectiveTest {
	
	private static final String NAME = "mq-min-risk";

	private static final double END = 60d;

	private static final double START = 55d;

	private  final TimeCourse timeCourse = Mockito.mock(TimeCourse.class); 
	
	private  final TimeCourseRetrospective timeCourseRetrospective = new TimeCourseRetrospectiveImpl(timeCourse, START, END);
	
	@Test
	public final  void timeCourse() {
		Assert.assertEquals(timeCourse, timeCourseRetrospective.timeCourse());
	}
	
	@Test
	public final  void end() {
		Assert.assertEquals((Double) END, (Double) timeCourseRetrospective.end());
	}
	
	@Test
	public final  void start() {
		Assert.assertEquals((Double) START, (Double) timeCourseRetrospective.start());
	}
	
	@Test
	public final  void name() {
		Mockito.when(timeCourse.name()).thenReturn(NAME);
		Assert.assertEquals(NAME, timeCourseRetrospective.name());
	}
	
	@Test
	public final void nameTimeCourseNull() {
		ReflectionUtils.doWithFields(timeCourseRetrospective.getClass(), field -> ReflectionTestUtils.setField(timeCourseRetrospective, field.getName(), null), field -> field.getType().equals(TimeCourse.class));
	    Assert.assertNull(timeCourseRetrospective.name());
	}
	
	@Test
	public final void rate() {
		Assert.assertEquals((Double) ((END-START)/START) , (Double)  timeCourseRetrospective.rate());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void timeCourseNotNull(){
		new TimeCourseRetrospectiveImpl(null, START, END);
	}

}
