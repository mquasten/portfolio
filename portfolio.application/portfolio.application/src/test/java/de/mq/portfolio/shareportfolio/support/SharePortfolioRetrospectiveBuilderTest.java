package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;

public class SharePortfolioRetrospectiveBuilderTest {
	
	
	private static final String CODE = "KO";

	private final SharePortfolioRetrospectiveBuilder builder =  new SharePortfolioRetrospectiveBuilderImpl();
	
	private final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
	private SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	
	private TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	
	private final Share share = Mockito.mock(Share.class);
	@Test
	public final void withExchangeRateCalculator () {
		Assert.assertEquals(builder, builder.withExchangeRateCalculator(exchangeRateCalculator));
		
		Assert.assertEquals(exchangeRateCalculator, field(ExchangeRateCalculator.class));
	
		
		
	}
	
	private <T>  T field(Class<? extends T> clazz) {
		@SuppressWarnings("unchecked")
		final Optional<T> result = (Optional<T>) Arrays.asList(SharePortfolioRetrospectiveBuilderImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(clazz)).map(field ->  ReflectionTestUtils.getField(builder,field.getName())).findFirst();
	    Assert.assertTrue(result.isPresent());
	    return result.get();
	}
	
	@Test
	public final void withCommitedSharePortfolio() {
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		Assert.assertEquals(builder, builder.withCommitedSharePortfolio(sharePortfolio));
		Assert.assertEquals(sharePortfolio, ReflectionTestUtils.getField(builder, "committedSharePortfolio"));
	}
	
	@Test
	public final void withTimeCourse() {
		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Assert.assertEquals(builder, builder.withTimeCourse(timeCourse));
		@SuppressWarnings("unchecked")
		final Map<String, TimeCourse> results = field(Map.class);
		Assert.assertEquals(1, results.size());
		Assert.assertTrue(results.keySet().stream().findAny().isPresent());
		Assert.assertEquals(CODE, results.keySet().stream().findAny().get());
		Assert.assertTrue(results.values().stream().findAny().isPresent());
		Assert.assertEquals(timeCourse, results.values().stream().findAny().get());
	}
	
   @Test(expected=IllegalArgumentException.class)
	public final void withTimeCourseCodeAlreadyAssigned() {
		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Assert.assertEquals(builder, builder.withTimeCourse(timeCourse));
		@SuppressWarnings("unchecked")
		final Map<String, TimeCourse> results = field(Map.class);
		results.put(CODE, timeCourse);
		builder.withTimeCourse(timeCourse);
		
	}
   
   @Test
	public final void withTimeCourses() {
	   Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Assert.assertEquals(builder, builder.withTimeCourses(Arrays.asList(timeCourse)));
		@SuppressWarnings("unchecked")
		final Map<String, TimeCourse> results = field(Map.class);
		Assert.assertEquals(1, results.size());
		Assert.assertTrue(results.keySet().stream().findAny().isPresent());
		Assert.assertEquals(CODE, results.keySet().stream().findAny().get());
		Assert.assertTrue(results.values().stream().findAny().isPresent());
		Assert.assertEquals(timeCourse, results.values().stream().findAny().get());
		
		
   }
   
   @Test(expected=IllegalArgumentException.class)
	public final void withTimeCoursesEmptyCollection() {
	   builder.withTimeCourses(new ArrayList<>());
   }

}
