package de.mq.portfolio.share.support;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.shareportfolio.support.SharePortfolioService;
import de.mq.portfolio.support.SerialisationUtil;
import de.mq.portfolio.support.UserModel;


@Component("sharesController")
@Scope("singleton")
public class SharesControllerImpl {
	
	static final String SHARE_FIELDS_NAME = "share";
	 static final String STANDARD_DEVIATION_FIELD_NAME = "standardDeviation";
	static final String TOTAL_RATE_DIVIDENDS_FIELD_NAME = "totalRateDividends";
	static final String TOTAL_RATE_FIELD_NAME = "totalRate";
	static final String MEAN_RATE_FRIELDE_NAME = "meanRate";
	static final String NAME_FIELD_NAME = "name";
	static final String ID_FIELD_NAME = "id";
	private final ShareService shareService;
	private final SharePortfolioService sharePortfolioService;
	private final Map<String,Sort> orderBy = new HashMap<>();
	private final SerialisationUtil serialisationUtil;
	
	@Autowired
	SharesControllerImpl(final ShareService shareService, final SharePortfolioService sharePortfolioService, final SerialisationUtil serialisationUtil) {
		this.shareService = shareService;
		this.sharePortfolioService=sharePortfolioService;
		this.serialisationUtil=serialisationUtil;
		orderBy.put(ID_FIELD_NAME, new Sort(ID_FIELD_NAME));
		orderBy.put(NAME_FIELD_NAME, new Sort( SHARE_FIELDS_NAME + "." + NAME_FIELD_NAME, ID_FIELD_NAME));
		orderBy.put(MEAN_RATE_FRIELDE_NAME, new Sort(Direction.DESC, MEAN_RATE_FRIELDE_NAME ,ID_FIELD_NAME));
		orderBy.put(TOTAL_RATE_FIELD_NAME, new Sort(Direction.DESC, TOTAL_RATE_FIELD_NAME ,ID_FIELD_NAME));
		orderBy.put(TOTAL_RATE_DIVIDENDS_FIELD_NAME, new Sort(Direction.DESC, TOTAL_RATE_DIVIDENDS_FIELD_NAME ,ID_FIELD_NAME));
		orderBy.put(STANDARD_DEVIATION_FIELD_NAME, new Sort(STANDARD_DEVIATION_FIELD_NAME ,ID_FIELD_NAME));
		
		
		
	}

	
	public final void init(final SharesSearchAO sharesSearchAO, UserModel userModel) {
	
		if( StringUtils.hasText(sharesSearchAO.getState()) ){
			final Map<String,Object> values = serialisationUtil.deSerialize(sharesSearchAO.getState());
			
			serialisationUtil.toBean(values, sharesSearchAO);
		}
		
		refreshPortfolioList(sharesSearchAO, userModel);
		sharesSearchAO.setIndexes(shareService.indexes());
	
		
		page(sharesSearchAO);
		
		
	
	}


	


	private void refreshPortfolioList(final SharesSearchAO sharesSearchAO, UserModel userModel) {
		final Collection<Entry<String,String>> portfolio = new ArrayList<>();
		if( userModel.getPortfolioId() !=null){
			final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(userModel.getPortfolioId());
		
			sharePortfolio.timeCourses().stream().forEach(tc -> Assert.notNull(tc.id(), "TimeCourses must be persistent"));
			portfolio.addAll(sharePortfolio.timeCourses().stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(tc.share().name(), tc.id())).collect(Collectors.toList()));
		   sharesSearchAO.setPortfolioName(sharePortfolio.name());
		}
		sharesSearchAO.setSelectedPortfolioItem(null);
		sharesSearchAO.setPortfolio(portfolio);
	}
	
	public final void page(final SharesSearchAO sharesSearchAO) {
		
		Pageable likeAVirgin = sharesSearchAO.getPageable();
		sharesSearchAO.setPageable(shareService.pageable(sharesSearchAO.getSearch(),orderBy.get(sharesSearchAO.getSelectedSort()), 10));
		
		if( StringUtils.hasText(sharesSearchAO.getState())&&(likeAVirgin==null) ){
			final Map<String,Object> values = serialisationUtil.deSerialize(sharesSearchAO.getState());
			serialisationUtil.toBean(values, sharesSearchAO.getPageable());
		}
		
		
		refreshTimeCourses(sharesSearchAO);
		
	
		
		
	}


	private void assignState(final SharesSearchAO sharesSearchAO) {
		final Map<String,Object> state = new HashMap<>();
		state.putAll(serialisationUtil.toMap(sharesSearchAO, Arrays.asList("code", "name" , "index")));
		state.putAll(serialisationUtil.toMap(sharesSearchAO, Arrays.asList("selectedSort")));
		state.putAll(serialisationUtil.toMap(sharesSearchAO.getPageable(), Arrays.asList("page")));
		
		sharesSearchAO.setState(serialisationUtil.serialize(state));
	}

	
	

	private void refreshTimeCourses(final SharesSearchAO sharesSearchAO) {
		sharesSearchAO.setSelectedTimeCourse(null);
		
		
		
		
		sharesSearchAO.setTimeCorses(shareService.timeCourses(sharesSearchAO.getPageable(), sharesSearchAO.getSearch()));
		assignState(sharesSearchAO);
	}
	
	
	public final void next(final SharesSearchAO sharesSearchAO) {
		if( sharesSearchAO.getPageable() == null ) {
			
			return;
		}
		
		sharesSearchAO.setPageable(sharesSearchAO.getPageable().next());
		refreshTimeCourses(sharesSearchAO);
	}
	
	public final void previous(final SharesSearchAO sharesSearchAO) {
		if( sharesSearchAO.getPageable() == null ) {
			
			return;	
		}
		
	
		sharesSearchAO.setPageable(((ClosedIntervalPageRequest)sharesSearchAO.getPageable()).previous());
		refreshTimeCourses(sharesSearchAO);
	}
	
	public final void first(final SharesSearchAO sharesSearchAO) {
		if( sharesSearchAO.getPageable() == null ) {
			
			return;
		}
		sharesSearchAO.setPageable(sharesSearchAO.getPageable().first());
		refreshTimeCourses(sharesSearchAO);
		
	}
	
	public final void last(final SharesSearchAO sharesSearchAO) {
		if( sharesSearchAO.getPageable() == null ) {
			
			return;
		}
		sharesSearchAO.setPageable(((ClosedIntervalPageRequest)sharesSearchAO.getPageable()).last());
		refreshTimeCourses(sharesSearchAO);
		
	}
	
	public final void  add2Portfolio(final SharesSearchAO sharesSearchAO, final UserModel userModel) {
	
		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(userModel.getPortfolioId());
		sharePortfolio.assign(sharesSearchAO.getSelectedTimeCourse().getValue());
		sharePortfolioService.save(sharePortfolio);
		refreshPortfolioList(sharesSearchAO, userModel);
	}
	
	public final void removeFromPortfolio(final SharesSearchAO sharesSearchAO, final UserModel userModel) {
		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(userModel.getPortfolioId());
		final Optional<TimeCourse> toBeRemoved = sharePortfolio.timeCourses().stream().filter(tc -> tc.id().equals(sharesSearchAO.getSelectedPortfolioItem())).findFirst();
		if ( !toBeRemoved.isPresent()) {
			return;
		}
		
		sharePortfolio.remove(toBeRemoved.get());
		sharePortfolioService.save(sharePortfolio);
	
		refreshPortfolioList(sharesSearchAO, userModel);
		
	}
	
	

}
