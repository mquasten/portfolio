package de.mq.portfolio.share.support;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class TimeCourseConverterTypeTest {
	
	@Test
	public final void types() {
		 Assert.assertEquals(2, TimeCourseConverter.TimeCourseConverterType.values().length);
	}

	@Test
	public final void create() {
		Arrays.asList(TimeCourseConverter.TimeCourseConverterType.values()).forEach(value -> Assert.assertEquals(value, TimeCourseConverter.TimeCourseConverterType.valueOf(value.name())));
	}
}
