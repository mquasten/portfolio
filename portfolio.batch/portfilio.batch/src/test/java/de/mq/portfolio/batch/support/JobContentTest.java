package de.mq.portfolio.batch.support;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.share.TimeCourse;

public class JobContentTest {
	
	private static final String TIME_COURSE_KEY = "timeCourse";

	private final JobContent<String> jobContent = new JobContentImpl<>();
	
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	
	@Test
	public final void content() {
		jobContent.putContent(TIME_COURSE_KEY , timeCourse);
		Assert.assertEquals(timeCourse, jobContent.content().get(TIME_COURSE_KEY));
	}

	@Test
	public final void ConstructorMap() {
		final Map<String,Object> beans = new HashMap<>();
		beans.put(TIME_COURSE_KEY, timeCourse);
		Assert.assertEquals(timeCourse, new JobContentImpl<>(beans).content().get(TIME_COURSE_KEY));
	}
	
}
