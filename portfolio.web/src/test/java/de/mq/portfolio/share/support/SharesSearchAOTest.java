package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;


import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import org.junit.Assert;

public class SharesSearchAOTest {
	
	
	private static final String ID = "19680528";
	private static final String PORTFOLIO_NAME = "Hottest Artists ever";
	private static final String SELECTED_SORT_COLUMN = "hotScore";
	private static final int MAX_PAGE = 42;
	private static final int PAGE_NUMBER = 10;
	private static final String INDEX = "hot female artsists";
	private static final String CODE = "KM";
	private static final String NAME = "Minogue Music";
	private final SharesSearchAO sharesSearchAO = new SharesSearchAO();
	
	private final ClosedIntervalPageRequest pageRequest = Mockito.mock(ClosedIntervalPageRequest.class);
	
	@Test
	public void getSearch() {
		
		sharesSearchAO.setName(NAME);
		sharesSearchAO.setCode(CODE);
		sharesSearchAO.setIndex(INDEX);
		
		final Share share = sharesSearchAO.getSearch();
		Assert.assertEquals(NAME, share.name());
		Assert.assertEquals(CODE, share.code());
		Assert.assertEquals(INDEX, share.index());
		Assert.assertNull(share.currency());
		Assert.assertNull(share.wkn());
		
	}
	
	@Test
	public void name() {
		Assert.assertNull(sharesSearchAO.getName());
		sharesSearchAO.setName(NAME);
		Assert.assertEquals(NAME, sharesSearchAO.getName());
	}
	
	@Test
	public void code() {
		Assert.assertNull(sharesSearchAO.getCode());
		sharesSearchAO.setCode(CODE);
		Assert.assertEquals(CODE, sharesSearchAO.getCode());
	}
	
	@Test
	public void  index() {
		Assert.assertNull(sharesSearchAO.getIndex());
		sharesSearchAO.setIndex(INDEX);
		Assert.assertEquals(INDEX, sharesSearchAO.getIndex());
	}
	
	@Test
	public void pageable() {
		Assert.assertNull(sharesSearchAO.getPageable());
		sharesSearchAO.setPageable(pageRequest);
		Assert.assertEquals(pageRequest, sharesSearchAO.getPageable());
	}
	
	@Test
	public void getPageInfo() {
		Assert.assertNull(sharesSearchAO.getPageInfo());
		
		Mockito.when(pageRequest.getPageNumber()).thenReturn(PAGE_NUMBER);
		Mockito.when(pageRequest.maxPage()).thenReturn(MAX_PAGE);
		sharesSearchAO.setPageable(pageRequest);
		Assert.assertEquals(String.format("%s/%s",  PAGE_NUMBER+1, MAX_PAGE+1), sharesSearchAO.getPageInfo());
	}
	
	@Test
	public void timeCourses() {
		Assert.assertTrue(sharesSearchAO.getTimeCourses().isEmpty());
		final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
		final Share share = Mockito.mock(Share.class);
		Mockito.when(timeCourse.share()).thenReturn(share);
		final Collection<TimeCourse> timeCourses = Arrays.asList(timeCourse);
		sharesSearchAO.setTimeCorses(timeCourses);
		
		Collection<Entry<Share,TimeCourse>> results = sharesSearchAO.getTimeCourses();
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(share, results.stream().findAny().get().getKey());
		Assert.assertEquals(timeCourse, results.stream().findAny().get().getValue());
	}
	
	@Test
	public void  indexes() {
		Assert.assertTrue(sharesSearchAO.getIndexes().isEmpty());
		sharesSearchAO.setIndexes(Arrays.asList(INDEX));
		Assert.assertEquals(1, sharesSearchAO.getIndexes().size());
		Assert.assertEquals(INDEX, sharesSearchAO.getIndexes().stream().findAny().get());
	}
	
	@Test
	public void  selectedSort() {
		Assert.assertEquals(SharesSearchAO.DEFAULT_SELECTED_SORT_COLUMN, sharesSearchAO.getSelectedSort());
		sharesSearchAO.setSelectedSort(SELECTED_SORT_COLUMN);
		Assert.assertEquals(SELECTED_SORT_COLUMN, sharesSearchAO.getSelectedSort());
	}
	
	@Test
	public void  portfolio() {
		Assert.assertTrue(sharesSearchAO.getPortfolio().isEmpty());
		@SuppressWarnings("unchecked")
		final Entry<String,String> timeCourseEntry  = Mockito.mock(Entry.class);
		
		sharesSearchAO.setPortfolio(Arrays.asList(timeCourseEntry));
		Assert.assertEquals(1, sharesSearchAO.getPortfolio().size());
		Assert.assertEquals(timeCourseEntry, sharesSearchAO.getPortfolio().stream().findAny().get());
		
	}
	
	@Test
	public void  portfolioName() {
		Assert.assertNull(sharesSearchAO.getPortfolioName());
		sharesSearchAO.setPortfolioName(PORTFOLIO_NAME);
		Assert.assertEquals(PORTFOLIO_NAME, sharesSearchAO.getPortfolioName());
	}

	@Test
	public void selectedTimeCourse() {
		Assert.assertNull(sharesSearchAO.getSelectedTimeCourse());
		
		@SuppressWarnings("unchecked")
		final Entry<Share,TimeCourse> timeCourseEntry  = Mockito.mock(Entry.class);
		TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
		Mockito.when(timeCourse.code()).thenReturn(CODE);
		Mockito.when(timeCourseEntry.getValue()).thenReturn(timeCourse);
		
		sharesSearchAO.setSelectedTimeCourse(timeCourseEntry);
		Assert.assertEquals(timeCourseEntry, sharesSearchAO.getSelectedTimeCourse());
	
	}
	
	@Test
	public void  selectedPortfolioItem() {
		Assert.assertNull(sharesSearchAO.getSelectedPortfolioItem());
		sharesSearchAO.setSelectedPortfolioItem(ID);
		Assert.assertEquals(ID, sharesSearchAO.getSelectedPortfolioItem());
	}
	
	
	
	
	
	
}
