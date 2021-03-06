package de.mq.portfolio.share.support;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.shareportfolio.support.SharePortfolioService;
import de.mq.portfolio.support.DeSerialize;
import de.mq.portfolio.support.Parameter;
import de.mq.portfolio.support.Serialize;
import de.mq.portfolio.support.UserModel;

@Component("sharesController")
@Scope("singleton")
public  class SharesControllerImpl {

	static final String SHARE_FIELDS_NAME = "share";
	static final String STANDARD_DEVIATION_FIELD_NAME = "standardDeviation";
	static final String TOTAL_RATE_DIVIDENDS_FIELD_NAME = "totalRateDividends";
	static final String TOTAL_RATE_FIELD_NAME = "totalRate";
	static final String MEAN_RATE_FRIELDE_NAME = "meanRate";
	static final String NAME_FIELD_NAME = "name";
	static final String ID_FIELD_NAME = "id";
	private final ShareService shareService;
	private final SharePortfolioService sharePortfolioService;
	private final Map<String, Sort> orderBy = new HashMap<>();

	@Autowired
	SharesControllerImpl(final ShareService shareService, final SharePortfolioService sharePortfolioService) {
		this.shareService = shareService;
		this.sharePortfolioService = sharePortfolioService;
		orderBy.put(ID_FIELD_NAME, new Sort(ID_FIELD_NAME));
		orderBy.put(NAME_FIELD_NAME, new Sort(SHARE_FIELDS_NAME + "." + NAME_FIELD_NAME, ID_FIELD_NAME));
		orderBy.put(MEAN_RATE_FRIELDE_NAME, new Sort(Direction.DESC, MEAN_RATE_FRIELDE_NAME, ID_FIELD_NAME));
		orderBy.put(TOTAL_RATE_FIELD_NAME, new Sort(Direction.DESC, TOTAL_RATE_FIELD_NAME, ID_FIELD_NAME));
		orderBy.put(TOTAL_RATE_DIVIDENDS_FIELD_NAME, new Sort(Direction.DESC, TOTAL_RATE_DIVIDENDS_FIELD_NAME, ID_FIELD_NAME));
		orderBy.put(STANDARD_DEVIATION_FIELD_NAME, new Sort(STANDARD_DEVIATION_FIELD_NAME, ID_FIELD_NAME));

	}

	@DeSerialize(mappings={"page=pageable.page", "counter=pageable.counter", "sort=pageable.sort", "selectedTimeCourseCode=" }, methodRegex="restoreState")
	public void init(final SharesSearchAO sharesSearchAO, UserModel userModel) {

		refreshPortfolioList(sharesSearchAO, userModel);
		sharesSearchAO.setIndexes(shareService.indexes());

		page(sharesSearchAO);

	}
	
	
	void restoreState(@Parameter final SharesSearchAO sharesSearchAO, @Parameter("selectedTimeCourseCode") final  String selectedTimeCourseCode ) {
		refreshTimeCourses(sharesSearchAO);
		
		sharesSearchAO.getTimeCourses().stream().filter(tc -> tc.getValue().code().equals(selectedTimeCourseCode)).findAny().ifPresent(selected ->sharesSearchAO.setSelectedTimeCourse(selected) );
	}

	private void refreshPortfolioList(final SharesSearchAO sharesSearchAO, UserModel userModel) {
		final Collection<Entry<String, String>> portfolio = new ArrayList<>();
		if (userModel.getPortfolioId() != null) {
			final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(userModel.getPortfolioId());

			sharePortfolio.timeCourses().stream().forEach(tc -> Assert.notNull(tc.id(), "TimeCourses must be persistent"));
			portfolio.addAll(sharePortfolio.timeCourses().stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(tc.share().name(), tc.id())).collect(Collectors.toList()));
			sharesSearchAO.setPortfolioName(sharePortfolio.name());
		}
		sharesSearchAO.setSelectedPortfolioItem(null);
		sharesSearchAO.setPortfolio(portfolio);
	}

	@Serialize(mappings={"pageable.page=page", "pageable.counter=counter", "pageable.sort=sort", "selectedTimeCourse.value.code=selectedTimeCourseCode"}, fields={"code", "name" , "index", "selectedSort", "selectedTimeCourse.value.code",  "pageable.page", "pageable.counter", "pageable.sort"})
	public void page(final SharesSearchAO sharesSearchAO) {

		sharesSearchAO.setPageable(shareService.pageable(sharesSearchAO.getSearch(), orderBy.get(sharesSearchAO.getSelectedSort()), 10));
		refreshTimeCourses(sharesSearchAO);
	}

	@Serialize(mappings={"pageable.page=page", "pageable.counter=counter", "pageable.sort=sort", "selectedTimeCourse.value.code=selectedTimeCourseCode"}, fields={"code", "name" , "index", "selectedSort", "selectedTimeCourse.value.code",  "pageable.page", "pageable.counter", "pageable.sort"})
	public void assignState(final SharesSearchAO sharesSearchAO) {

	}

	void refreshTimeCourses(final SharesSearchAO sharesSearchAO) {

		sharesSearchAO.setSelectedTimeCourse(null);

		sharesSearchAO.setTimeCorses(shareService.timeCourses(sharesSearchAO.getPageable(), sharesSearchAO.getSearch()));

	}

	@Serialize(mappings={"pageable.page=page", "pageable.counter=counter", "pageable.sort=sort", "selectedTimeCourse.value.code=selectedTimeCourseCode"}, fields={"code", "name" , "index", "selectedSort", "selectedTimeCourse.value.code",  "pageable.page", "pageable.counter", "pageable.sort"})
	public void next(final SharesSearchAO sharesSearchAO) {
		if (sharesSearchAO.getPageable() == null) {

			return;
		}

		sharesSearchAO.setPageable(sharesSearchAO.getPageable().next());
		refreshTimeCourses(sharesSearchAO);
	}

	@Serialize(mappings={"pageable.page=page", "pageable.counter=counter", "pageable.sort=sort", "selectedTimeCourse.value.code=selectedTimeCourseCode"}, fields={"code", "name" , "index", "selectedSort", "selectedTimeCourse.value.code",  "pageable.page", "pageable.counter", "pageable.sort"})
	public void previous(final SharesSearchAO sharesSearchAO) {
		if (sharesSearchAO.getPageable() == null) {

			return;
		}

		sharesSearchAO.setPageable(((ClosedIntervalPageRequest) sharesSearchAO.getPageable()).previous());
		refreshTimeCourses(sharesSearchAO);
	}

	@Serialize(mappings={"pageable.page=page", "pageable.counter=counter", "pageable.sort=sort", "selectedTimeCourse.value.code=selectedTimeCourseCode"}, fields={"code", "name" , "index", "selectedSort", "selectedTimeCourse.value.code",  "pageable.page", "pageable.counter", "pageable.sort"})
	public void first(final SharesSearchAO sharesSearchAO) {
		if (sharesSearchAO.getPageable() == null) {

			return;
		}
		sharesSearchAO.setPageable(sharesSearchAO.getPageable().first());
		refreshTimeCourses(sharesSearchAO);

	}

	@Serialize(mappings={"pageable.page=page", "pageable.counter=counter", "pageable.sort=sort", "selectedTimeCourse.value.code=selectedTimeCourseCode"}, fields={"code", "name" , "index", "selectedSort", "selectedTimeCourse.value.code",  "pageable.page", "pageable.counter", "pageable.sort"})
	public void last(final SharesSearchAO sharesSearchAO) {
		if (sharesSearchAO.getPageable() == null) {

			return;
		}
		sharesSearchAO.setPageable(((ClosedIntervalPageRequest) sharesSearchAO.getPageable()).last());
		refreshTimeCourses(sharesSearchAO);

	}

	public void add2Portfolio(final SharesSearchAO sharesSearchAO, final UserModel userModel) {

		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(userModel.getPortfolioId());
		sharePortfolio.assign(sharesSearchAO.getSelectedTimeCourse().getValue());
		sharePortfolioService.save(sharePortfolio);
		refreshPortfolioList(sharesSearchAO, userModel);
	}

	public void removeFromPortfolio(final SharesSearchAO sharesSearchAO, final UserModel userModel) {
		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(userModel.getPortfolioId());
		final Optional<TimeCourse> toBeRemoved = sharePortfolio.timeCourses().stream().filter(tc -> tc.id().equals(sharesSearchAO.getSelectedPortfolioItem())).findFirst();
		if (!toBeRemoved.isPresent()) {
			return;
		}

		sharePortfolio.remove(toBeRemoved.get());
		sharePortfolioService.save(sharePortfolio);

		refreshPortfolioList(sharesSearchAO, userModel);

	}
	

	

}
