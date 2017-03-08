package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import org.junit.Assert;

public class SharePortfolioRepositoryTest {

	private static final String JSON_STRING = "jsonString";

	private static final String PORTFOLIO_DOCNAME = "Portfolio";

	private static final String ID = "19680528";

	private static final long COUNTER = 42L;

	private static final int PAGE_SIZE = 50;

	private static final int OFFSET = 100;

	private static final String SHARE_NAME = "Minogue-Music AG";

	private static final String NAME = "mq-test";

	private final MongoOperations mongoOperations = Mockito.mock(MongoOperations.class);

	private final SharePortfolioRepository sharePortfolioRepository = new SharePortfolioRepositoryImpl(mongoOperations);

	private final ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
	

	@SuppressWarnings("rawtypes")
	private final ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);

	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);


	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);

	private Share share = Mockito.mock(Share.class);

	private Sort sort = new Sort(new Order(SharePortfolioRepositoryImpl.NAME_FIELD));

	private final Pageable pageable = Mockito.mock(Pageable.class);

	@SuppressWarnings("unchecked")
	@Test
	public final void portfolio() {
		final List<SharePortfolio> results = new ArrayList<>();
		results.add(sharePortfolio);
		Mockito.when(mongoOperations.find(queryCaptor.capture(), classCaptor.capture())).thenReturn(results);
		Assert.assertEquals(sharePortfolio, sharePortfolioRepository.portfolio(NAME));

		Assert.assertEquals(SharePortfolioImpl.class, classCaptor.getValue());
		final Query query = queryCaptor.getValue();
		Assert.assertEquals(1, query.getQueryObject().keySet().stream().count());
		Assert.assertEquals(SharePortfolioRepositoryImpl.NAME_FIELD, query.getQueryObject().keySet().stream().findAny().get());
		Assert.assertEquals(NAME, query.getQueryObject().toMap().values().stream().findAny().get());
	}

	@Test
	public final void save() {
		sharePortfolioRepository.save(sharePortfolio);
		Mockito.verify(mongoOperations).save(sharePortfolio);
	}

	

	

	@Test
	public final void portfolios() {
		preparePortfoliosSearch();
		final Collection<SharePortfolio> results = sharePortfolioRepository.portfolios(pageable, sharePortfolio);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(sharePortfolio, results.stream().findAny().get());

		Assert.assertEquals(SharePortfolioImpl.class, classCaptor.getValue());

		Assert.assertEquals(OFFSET, queryCaptor.getValue().getSkip());
		Assert.assertEquals(PAGE_SIZE, queryCaptor.getValue().getLimit());
		Assert.assertEquals(1, queryCaptor.getValue().getSortObject().toMap().size());
		Assert.assertEquals(SharePortfolioRepositoryImpl.NAME_FIELD, queryCaptor.getValue().getSortObject().toMap().keySet().stream().findAny().get());
		Assert.assertEquals(1, queryCaptor.getValue().getSortObject().toMap().values().stream().findAny().get());
		Assert.assertEquals(2, queryCaptor.getValue().getQueryObject().keySet().size());
		Assert.assertEquals(NAME, ((Pattern) queryCaptor.getValue().getQueryObject().get(SharePortfolioRepositoryImpl.NAME_FIELD)).pattern());
		Assert.assertEquals(SHARE_NAME, ((Pattern) queryCaptor.getValue().getQueryObject().get(SharePortfolioRepositoryImpl.SHARE_NAME_FIELD)).pattern());

	}

	@SuppressWarnings("unchecked")
	private void preparePortfoliosSearch() {
		Mockito.when(sharePortfolio.name()).thenReturn(NAME);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Mockito.when(share.name()).thenReturn(SHARE_NAME);
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(Arrays.asList(timeCourse));
		Mockito.when(pageable.getSort()).thenReturn(sort);
		Mockito.when(pageable.getOffset()).thenReturn(OFFSET);
		Mockito.when(pageable.getPageSize()).thenReturn(PAGE_SIZE);

		Mockito.when(mongoOperations.find(queryCaptor.capture(), classCaptor.capture())).thenReturn(Arrays.asList(sharePortfolio));
	}
	
	@Test
	public final void portfoliosAllEmty() {
		preparePortfoliosSearch();
		Mockito.when(share.name()).thenReturn(null);
		Mockito.when(sharePortfolio.name()).thenReturn(null);
		Mockito.when(pageable.getSort()).thenReturn(null);
		
		checkPortfolioSearchAllEmpty(sharePortfolioRepository.portfolios(pageable, sharePortfolio));
		
		
	}

	private void checkPortfolioSearchAllEmpty(final Collection<SharePortfolio> results) {
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(sharePortfolio, results.stream().findAny().get());
		
		Assert.assertEquals(SharePortfolioImpl.class, classCaptor.getValue());

		Assert.assertEquals(OFFSET, queryCaptor.getValue().getSkip());
		Assert.assertEquals(PAGE_SIZE, queryCaptor.getValue().getLimit());
		Assert.assertNull(queryCaptor.getValue().getSortObject());
		
		
		Assert.assertEquals(0, queryCaptor.getValue().getQueryObject().keySet().size());
	}
	
	@Test
	public final void portfoliosAllEmtyNoShare() {
		preparePortfoliosSearch();
		Mockito.when(timeCourse.share()).thenReturn(null);
		Mockito.when(sharePortfolio.name()).thenReturn(null);
		Mockito.when(pageable.getSort()).thenReturn(null);
		
		checkPortfolioSearchAllEmpty(sharePortfolioRepository.portfolios(pageable, sharePortfolio));
		
	}
	
	@Test
	public final void portfoliosAllEmtyNoTimeCourses() {
		preparePortfoliosSearch();
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(new ArrayList<>());
		Mockito.when(sharePortfolio.name()).thenReturn(null);
		Mockito.when(pageable.getSort()).thenReturn(null);
		checkPortfolioSearchAllEmpty(sharePortfolioRepository.portfolios(pageable, sharePortfolio));
	}
	
	@Test
	public final void pageable() {
		preparePortfoliosSearch();
		
		Mockito.when(mongoOperations.count(queryCaptor.capture(), classCaptor.capture())).thenReturn(COUNTER);
		
		final Pageable pageable = sharePortfolioRepository.pageable(sharePortfolio, sort, PAGE_SIZE);
		Assert.assertEquals(COUNTER, ReflectionTestUtils.getField(pageable, "counter"));
		Assert.assertEquals(PAGE_SIZE, ReflectionTestUtils.getField(pageable, "size"));
		Assert.assertEquals(sort, pageable.getSort());
		Assert.assertEquals(SharePortfolioImpl.class, classCaptor.getValue());
		Assert.assertNull(queryCaptor.getValue().getSortObject());
		Assert.assertEquals(2, queryCaptor.getValue().getQueryObject().keySet().size());
		Assert.assertEquals(NAME, ((Pattern) queryCaptor.getValue().getQueryObject().get(SharePortfolioRepositoryImpl.NAME_FIELD)).pattern());
		Assert.assertEquals(SHARE_NAME, ((Pattern) queryCaptor.getValue().getQueryObject().get(SharePortfolioRepositoryImpl.SHARE_NAME_FIELD)).pattern());
		
	}
	
	
	@Test
	public final void sharePortfolio() {
		final SharePortfolio sharePortfolio = BeanUtils.instantiateClass(SharePortfolioImpl.class);
		Mockito.when(mongoOperations.findById(ID, SharePortfolioImpl.class)).thenReturn((SharePortfolioImpl) sharePortfolio);
		Assert.assertEquals(sharePortfolio, sharePortfolioRepository.sharePortfolio(ID));
		Mockito.verify(mongoOperations).findById(ID,SharePortfolioImpl.class);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void sharePortfolioNotFound() {
		Mockito.when(mongoOperations.findById(ID, SharePortfolioImpl.class)).thenReturn(null);
		sharePortfolioRepository.sharePortfolio(ID);
	}
	
	@Test
	public final void delete() {
		sharePortfolioRepository.delete(sharePortfolio);
		
		Mockito.verify(mongoOperations).remove(sharePortfolio);
	}
	
	@Test
	public final void saveJson() {
		sharePortfolioRepository.save(JSON_STRING);
		Mockito.verify(mongoOperations).save(JSON_STRING , PORTFOLIO_DOCNAME);
	}
}
