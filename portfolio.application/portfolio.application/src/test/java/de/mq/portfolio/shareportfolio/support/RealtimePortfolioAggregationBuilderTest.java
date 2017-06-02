package de.mq.portfolio.shareportfolio.support;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;


public class RealtimePortfolioAggregationBuilderTest {
	
	private final RealtimePortfolioAggregationBuilder realtimePortfolioAggregationBuilder = new RealtimePortfolioAggregationBuilderImpl();

	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	private TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	final List<Entry<TimeCourse, List<Data>>> realTimeCourses = Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(timeCourse, Arrays.asList(Mockito.mock(Data.class), Mockito.mock(Data.class))));
	
	@Test
	public final void withSharePortfolio() {
		Assert.assertEquals(realtimePortfolioAggregationBuilder, realtimePortfolioAggregationBuilder.withSharePortfolio(sharePortfolio));
		
		Assert.assertEquals(sharePortfolio, builderFields().get(SharePortfolio.class));
	}
	
	
	@Test
	public final void withRealtimeCourses() {
		
		Assert.assertEquals(realtimePortfolioAggregationBuilder, realtimePortfolioAggregationBuilder.withRealtimeCourses(realTimeCourses));
		
		Assert.assertEquals(realTimeCourses, builderFields().get(Collection.class));
	}

	private  Map<Class<?>,Object> builderFields() {
		final Collection<Class<?>> dependencies = Arrays.asList(SharePortfolio.class, Collection.class, Map.class); 
		

		return  Arrays.asList(RealtimePortfolioAggregationBuilderImpl.class.getDeclaredFields()).stream().filter(field-> dependencies.contains(field.getType()) && ReflectionTestUtils.getField(realtimePortfolioAggregationBuilder, field.getName())!= null).collect(Collectors.toMap(field ->field.getType() , field -> ReflectionTestUtils.getField(realtimePortfolioAggregationBuilder, field.getName())));
	  
		
	}

}
