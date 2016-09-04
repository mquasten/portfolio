package de.mq.portfolio.share.support;

import org.junit.Test;

import junit.framework.Assert;

public class TimeCourseCriteriaTest {
	
	private static final String SHARE_NAME = "Minogue-Music";
	private final  TimeCourseCriteriaImpl timeCourseCriteria = new TimeCourseCriteriaImpl(SHARE_NAME);
	
	
	@Test
	public final void create() {
		
		Assert.assertEquals(SHARE_NAME, timeCourseCriteria.name());
		Assert.assertTrue(timeCourseCriteria.code().isEmpty());
		Assert.assertTrue(timeCourseCriteria.rates().isEmpty());
		Assert.assertTrue(timeCourseCriteria.dividends().isEmpty());
		Assert.assertTrue(timeCourseCriteria.share().code().isEmpty());
		Assert.assertTrue(timeCourseCriteria.share().wkn().isEmpty());
		Assert.assertTrue(timeCourseCriteria.share().index().isEmpty());
		Assert.assertEquals(SHARE_NAME, timeCourseCriteria.share().name());
	}

}
