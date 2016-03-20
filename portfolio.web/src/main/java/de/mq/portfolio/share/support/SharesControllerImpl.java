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

import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.shareportfolio.support.SharePortfolioService;
import de.mq.portfolio.support.UserModel;

@Component("sharesController")
@Scope("singleton")
public class SharesControllerImpl {
	
	private final ShareService shareService;
	private final SharePortfolioService sharePortfolioService;
	private final Map<String,Sort> orderBy = new HashMap<>();
	
	@Autowired
	SharesControllerImpl(final ShareService shareService, final SharePortfolioService sharePortfolioService) {
		this.shareService = shareService;
		this.sharePortfolioService=sharePortfolioService;
		orderBy.put("id", new Sort("id"));
		orderBy.put("name", new Sort("share.name", "id"));
		orderBy.put("meanRate", new Sort(Direction.DESC, "meanRate" ,"id"));
		orderBy.put("totalRate", new Sort(Direction.DESC, "totalRate" ,"id"));
		orderBy.put("totalRateDividends", new Sort(Direction.DESC, "totalRateDividends" ,"id"));
		orderBy.put("standardDeviation", new Sort("standardDeviation" ,"id"));
	}

	
	public final void init(final SharesSearchAO sharesSearchAO, UserModel userModel) {
	
		refreshPortfolioList(sharesSearchAO, userModel);
		sharesSearchAO.setIndexes(shareService.indexes());
	
		
		page(sharesSearchAO);
	
	}


	private void refreshPortfolioList(final SharesSearchAO sharesSearchAO, UserModel userModel) {
		final Collection<Entry<String,String>> portfolio = new ArrayList<>();
		if( userModel.getPortfolioId() !=null){
			final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(userModel.getPortfolioId());
			portfolio.addAll(sharePortfolio.timeCourses().stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(tc.share().name(), tc.id())).collect(Collectors.toList()));
		   sharesSearchAO.setPortfolioName(sharePortfolio.name());
		}
		sharesSearchAO.setSelectedPortfolioItem(null);
		sharesSearchAO.setPortfolio(portfolio);
	}
	
	public final void page(final SharesSearchAO sharesSearchAO) {
		
		sharesSearchAO.setPageable(shareService.pageable(sharesSearchAO.getSearch(),orderBy.get(sharesSearchAO.getSelectedSort()), 10));
		
		refreshTimeCourses(sharesSearchAO);
		
	}

	
	

	private void refreshTimeCourses(final SharesSearchAO sharesSearchAO) {
		sharesSearchAO.setTimeCorses(shareService.timeCourses(sharesSearchAO.getPageable(), sharesSearchAO.getSearch()));
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
