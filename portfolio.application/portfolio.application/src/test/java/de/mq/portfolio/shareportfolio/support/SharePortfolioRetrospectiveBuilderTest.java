package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.DataImpl;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;

public class SharePortfolioRetrospectiveBuilderTest {
	
	
	private static final String COMMITTED_SHARE_PORTFOLIO_FIELD = "committedSharePortfolio";

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
		Assert.assertEquals(sharePortfolio, ReflectionTestUtils.getField(builder, COMMITTED_SHARE_PORTFOLIO_FIELD));
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
   
   @Test
   public void build() {
	   final Long date = new Date().getTime();
	   final TimeCourse tc01 = Mockito.mock(TimeCourse.class);
	   Mockito.when(tc01.code()).thenReturn("CODE01");
	   final TimeCourse tc02 = Mockito.mock(TimeCourse.class);
	   Mockito.when(tc02.code()).thenReturn("CODE02");
	   
	 
	   Mockito.when(tc01.rates()).thenReturn(Arrays.asList(new DataImpl(date(date,360), 50d),new DataImpl(date(date,179), 60d)));
	   Mockito.when(tc02.rates()).thenReturn(Arrays.asList(new DataImpl(date(date,360), 30d),new DataImpl(date(date,179), 40d)));
	   
	   Mockito.when(tc01.end()).thenReturn(date(date,179));
	   Mockito.when(tc02.end()).thenReturn(date(date,179));
	   Mockito.when(sharePortfolio.timeCourses()).thenReturn(Arrays.asList(tc01, tc02));
	   
	   System.out.println(tc01.end());
	  
	   final TimeCourse tc11 = Mockito.mock(TimeCourse.class);
	   Mockito.when(tc11.code()).thenReturn("CODE01");
	   final TimeCourse tc12 = Mockito.mock(TimeCourse.class);
	   Mockito.when(tc12.code()).thenReturn("CODE02");
	   
	   Mockito.when(tc11.rates()).thenReturn(Arrays.asList(new DataImpl(date(date,180), 60d),new DataImpl(new Date(date),70d)));
	   Mockito.when(tc12.rates()).thenReturn(Arrays.asList(new DataImpl(date(date,180), 40d),new DataImpl(new Date(date) , 50d)));
	   
	   final Map<String,TimeCourse> timeCourses = new HashMap<>();
	   timeCourses.put(tc11.code(), tc11);
	   timeCourses.put(tc12.code(), tc12);
	   
	   
	   setField(ExchangeRateCalculator.class, exchangeRateCalculator);
	   setField(Map.class, timeCourses);
	 
	   
	   ReflectionTestUtils.setField(builder, COMMITTED_SHARE_PORTFOLIO_FIELD, sharePortfolio);
	   
	   Map<TimeCourse,Double> weights = new HashMap<>();
	   weights.put(tc01, 0.5d);
	   weights.put(tc02, 0.5d);
	   
	   Mockito.when(sharePortfolio.min()).thenReturn(weights);
	   final ExchangeRate er01 = Mockito.mock(ExchangeRate.class);
	   final ExchangeRate er02 = Mockito.mock(ExchangeRate.class);
	   Mockito.when(sharePortfolio.exchangeRate(tc01)).thenReturn(er01);
	   Mockito.when(sharePortfolio.exchangeRate(tc02)).thenReturn(er02);
	   Mockito.when(exchangeRateCalculator.factor(er01, tc01.end())).thenReturn(1D);
	   Mockito.when(exchangeRateCalculator.factor(er02, tc02.end())).thenReturn(1.25D);
	   
	   
	   
	   
	 // builder.build();
	   
   }

private Date date(final Long date, long offset) {
	return new Date(date - (long)(60*60*24*1000d*offset));
}

private void setField(final Class<?> clazz, final Object value) {
	Arrays.asList(SharePortfolioRetrospectiveBuilderImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(clazz)).forEach(field ->  ReflectionTestUtils.setField(builder, field.getName(), value));
}

}
