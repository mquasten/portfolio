package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.shareportfolio.support.SharePortfolioService;
import de.mq.portfolio.support.SerialisationUtil;
import de.mq.portfolio.support.UserModel;
import junit.framework.Assert;

public class SharesControllerTest {

	private static final String SHARE_NAME = "SAP AG";

	private static final String TIMECOURSE_ID = "0815";

	private static final String PORTFOLIO_NAME = "most lowest risk ever";

	private static final String PORTFOLIO_ID = "16680528";

	private final ShareService shareService = Mockito.mock(ShareService.class);

	private final SharePortfolioService sharePortfolioService = Mockito.mock(SharePortfolioService.class);
	
	private final SerialisationUtil serialisationUtil = Mockito.mock(SerialisationUtil.class);

	private final SharesControllerImpl sharesController = new SharesControllerImpl(shareService, sharePortfolioService, serialisationUtil);

	private final SharesSearchAO sharesSearchAO = Mockito.mock(SharesSearchAO.class);

	private final UserModel userModel = Mockito.mock(UserModel.class);

	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);

	private final TimeCourse timecourse = Mockito.mock(TimeCourse.class);

	private Share share = Mockito.mock(Share.class);

	private Collection<String> indexes = Arrays.asList("Dow", "Dax");

	private final ClosedIntervalPageRequest pageable = Mockito.mock(ClosedIntervalPageRequest.class);

	private final Pageable newPageable = Mockito.mock(Pageable.class);

	@SuppressWarnings("unchecked")
	private final ArgumentCaptor<Collection<Entry<String, String>>> entries = (ArgumentCaptor<Collection<Entry<String, String>>>) ArgumentCaptor.forClass((Class<?>) Collection.class);

	@Before
	public final void setup() {

		Mockito.when(share.name()).thenReturn(SHARE_NAME);

		Mockito.when(userModel.getPortfolioId()).thenReturn(PORTFOLIO_ID);
		Mockito.when(timecourse.share()).thenReturn(share);
		Mockito.when(timecourse.id()).thenReturn(TIMECOURSE_ID);

		Mockito.when(sharePortfolio.name()).thenReturn(PORTFOLIO_NAME);

		Mockito.when(sharePortfolioService.sharePortfolio(PORTFOLIO_ID)).thenReturn(sharePortfolio);

		Mockito.when(sharePortfolio.timeCourses()).thenReturn(Arrays.asList(timecourse));

		Mockito.when(shareService.indexes()).thenReturn(indexes);

		Mockito.when(sharesSearchAO.getSelectedSort()).thenReturn(SharesControllerImpl.ID_FIELD_NAME);
		Mockito.when(sharesSearchAO.getSearch()).thenReturn(share);

		Mockito.when(shareService.pageable(sharesSearchAO.getSearch(), orderByMap().entrySet().stream().filter(e -> e.getKey().equals(SharesControllerImpl.ID_FIELD_NAME)).map(e -> e.getValue()).findAny().get(), 10)).thenReturn(pageable);

		Mockito.when(sharesSearchAO.getPageable()).thenReturn(pageable);
		Mockito.when(shareService.timeCourses(pageable, share)).thenReturn(Arrays.asList(timecourse));

	}

	@Test
	public final void orderBy() {

		final Map<String, Sort> result = orderByMap();

		Assert.assertEquals(6, result.size());
		Assert.assertEquals(new Sort(SharesControllerImpl.ID_FIELD_NAME), result.get(SharesControllerImpl.ID_FIELD_NAME));
		Assert.assertEquals(new Sort(SharesControllerImpl.SHARE_FIELDS_NAME + "." + SharesControllerImpl.NAME_FIELD_NAME, SharesControllerImpl.ID_FIELD_NAME), result.get(SharesControllerImpl.NAME_FIELD_NAME));
		Assert.assertEquals(new Sort(Direction.DESC, SharesControllerImpl.MEAN_RATE_FRIELDE_NAME, SharesControllerImpl.ID_FIELD_NAME), result.get(SharesControllerImpl.MEAN_RATE_FRIELDE_NAME));
		Assert.assertEquals(new Sort(Direction.DESC, SharesControllerImpl.TOTAL_RATE_FIELD_NAME, SharesControllerImpl.ID_FIELD_NAME), result.get(SharesControllerImpl.TOTAL_RATE_FIELD_NAME));
		Assert.assertEquals(new Sort(Direction.DESC, SharesControllerImpl.TOTAL_RATE_DIVIDENDS_FIELD_NAME, SharesControllerImpl.ID_FIELD_NAME), result.get(SharesControllerImpl.TOTAL_RATE_DIVIDENDS_FIELD_NAME));
		Assert.assertEquals(new Sort(SharesControllerImpl.STANDARD_DEVIATION_FIELD_NAME, SharesControllerImpl.ID_FIELD_NAME), result.get(SharesControllerImpl.STANDARD_DEVIATION_FIELD_NAME));

	}

	private Map<String, Sort> orderByMap() {
		@SuppressWarnings("unchecked")
		final Collection<Map<String, Sort>> results = Arrays.asList(SharesControllerImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Map.class)).map(field -> (Map<String, Sort>) ReflectionTestUtils.getField(sharesController, field.getName()))
				.collect(Collectors.toSet());

		final Map<String, Sort> result = DataAccessUtils.requiredSingleResult(results);
		return result;
	}

	@Test
	public final void init() {

		sharesController.init(sharesSearchAO, userModel);

		Mockito.verify(sharesSearchAO).setPortfolio(entries.capture());

		Assert.assertEquals(1, entries.getValue().size());
		Assert.assertEquals(SHARE_NAME, entries.getValue().stream().findAny().get().getKey());
		Assert.assertEquals(TIMECOURSE_ID, entries.getValue().stream().findAny().get().getValue());

		Mockito.verify(sharesSearchAO).setPortfolioName(PORTFOLIO_NAME);

		Mockito.verify(sharesSearchAO).setSelectedPortfolioItem(null);

		Mockito.verify(sharesSearchAO).setIndexes(indexes);

		Mockito.verify(sharesSearchAO).setPageable(pageable);

		Mockito.verify(sharesSearchAO).setSelectedTimeCourse(null);

		Mockito.verify(sharesSearchAO).setTimeCorses(Arrays.asList(timecourse));

	}

	@Test
	public final void initNoPortfolioSelected() {

		Mockito.when(userModel.getPortfolioId()).thenReturn(null);

		sharesController.init(sharesSearchAO, userModel);

		Mockito.verify(sharesSearchAO).setPortfolio(entries.capture());

		Assert.assertEquals(0, entries.getValue().size());

		Mockito.verify(sharesSearchAO, Mockito.never()).setPortfolioName(Mockito.anyString());

		Mockito.verify(sharesSearchAO).setSelectedPortfolioItem(null);

		Mockito.verify(sharesSearchAO).setIndexes(indexes);

		Mockito.verify(sharesSearchAO).setPageable(pageable);

		Mockito.verify(sharesSearchAO).setSelectedTimeCourse(null);

		Mockito.verify(sharesSearchAO).setTimeCorses(Arrays.asList(timecourse));

	}

	@Test
	public final void page() {
		sharesController.page(sharesSearchAO);

		Mockito.verify(sharesSearchAO).setPageable(pageable);

		Mockito.verify(sharesSearchAO).setSelectedTimeCourse(null);

		Mockito.verify(sharesSearchAO).setTimeCorses(Arrays.asList(timecourse));

	}

	@Test
	public final void next() {

		Mockito.when(pageable.next()).thenReturn(newPageable);

		sharesController.next(sharesSearchAO);

		Mockito.verify(pageable).next();
		Mockito.verify(sharesSearchAO).setPageable(newPageable);
		Mockito.verify(sharesSearchAO).setSelectedTimeCourse(null);
	}

	@Test
	public final void nextPageableNull() {

		Mockito.when(sharesSearchAO.getPageable()).thenReturn(null);

		sharesController.next(sharesSearchAO);

		Mockito.verify(pageable, Mockito.never()).next();
		Mockito.verify(sharesSearchAO, Mockito.never()).setPageable(Mockito.any());
		Mockito.verify(sharesSearchAO, Mockito.never()).setSelectedTimeCourse(null);
	}

	@Test
	public final void previous() {
		Mockito.when(pageable.previous()).thenReturn(newPageable);
		sharesController.previous(sharesSearchAO);

		Mockito.verify(pageable).previous();
		Mockito.verify(sharesSearchAO).setPageable(newPageable);
		Mockito.verify(sharesSearchAO).setSelectedTimeCourse(null);
	}

	@Test
	public final void previousPageableNull() {
		Mockito.when(sharesSearchAO.getPageable()).thenReturn(null);
		sharesController.previous(sharesSearchAO);

		Mockito.verify(pageable, Mockito.never()).previous();
		Mockito.verify(sharesSearchAO, Mockito.never()).setPageable(Mockito.any());
		Mockito.verify(sharesSearchAO, Mockito.never()).setSelectedTimeCourse(null);
	}

	@Test
	public final void first() {
		Mockito.when(pageable.first()).thenReturn(newPageable);
		sharesController.first(sharesSearchAO);

		Mockito.verify(pageable).first();
		Mockito.verify(sharesSearchAO).setPageable(newPageable);
		Mockito.verify(sharesSearchAO).setSelectedTimeCourse(null);

	}

	@Test
	public final void firstPageableNull() {
		Mockito.when(sharesSearchAO.getPageable()).thenReturn(null);
		sharesController.first(sharesSearchAO);

		Mockito.verify(pageable, Mockito.never()).first();
		Mockito.verify(sharesSearchAO, Mockito.never()).setPageable(Mockito.any());
		Mockito.verify(sharesSearchAO, Mockito.never()).setSelectedTimeCourse(null);
	}

	@Test
	public final void last() {
		Mockito.when(pageable.last()).thenReturn(newPageable);
		sharesController.last(sharesSearchAO);

		Mockito.verify(pageable).last();
		Mockito.verify(sharesSearchAO).setPageable(newPageable);
		Mockito.verify(sharesSearchAO).setSelectedTimeCourse(null);

	}

	@Test
	public final void lastPageableNull() {
		Mockito.when(sharesSearchAO.getPageable()).thenReturn(null);
		sharesController.last(sharesSearchAO);

		Mockito.verify(pageable, Mockito.never()).last();
		Mockito.verify(sharesSearchAO, Mockito.never()).setPageable(Mockito.any());
		Mockito.verify(sharesSearchAO, Mockito.never()).setSelectedTimeCourse(null);
	}

	@Test
	public final void add2Portfolio() {

		@SuppressWarnings("unchecked")
		final Entry<Share, TimeCourse> selected = Mockito.mock(Entry.class);
		Mockito.when(selected.getKey()).thenReturn(share);
		Mockito.when(selected.getValue()).thenReturn(timecourse);

		Mockito.when(sharesSearchAO.getSelectedTimeCourse()).thenReturn(selected);
		sharesController.add2Portfolio(sharesSearchAO, userModel);

		Mockito.verify(sharePortfolio).assign(timecourse);
		Mockito.verify(sharePortfolioService).save(sharePortfolio);
		Mockito.verify(sharesSearchAO).setSelectedPortfolioItem(null);
	}

	@Test
	public final void removeFromPortfolio() {

		Mockito.when(sharesSearchAO.getSelectedPortfolioItem()).thenReturn(TIMECOURSE_ID);
		sharesController.removeFromPortfolio(sharesSearchAO, userModel);

		Mockito.verify(sharePortfolio).remove(timecourse);

		Mockito.verify(sharePortfolioService).save(sharePortfolio);
		Mockito.verify(sharesSearchAO).setSelectedPortfolioItem(null);

	}

	@Test
	public final void removeFromPortfolioNotFound() {
		Mockito.when(sharesSearchAO.getSelectedPortfolioItem()).thenReturn(PORTFOLIO_ID);
		sharesController.removeFromPortfolio(sharesSearchAO, userModel);

		Mockito.verify(sharePortfolio, Mockito.never()).remove(Mockito.any());

		Mockito.verify(sharePortfolioService, Mockito.never()).save(sharePortfolio);
		Mockito.verify(sharesSearchAO, Mockito.never()).setSelectedPortfolioItem(null);
	}

}
