package de.mq.portfolio.share.support;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
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
		
		System.out.println(userModel.getPortfolioId());
		final Collection<Entry<String,TimeCourse>> portfolio = new ArrayList<>();
		if( userModel.getPortfolioId() !=null){
			portfolio.addAll(sharePortfolioService.sharePortfolio(userModel.getPortfolioId()).timeCourses().stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(tc.share().name(), tc)).collect(Collectors.toList()));
		}
		
		sharesSearchAO.setPortfolio(portfolio);
		sharesSearchAO.setIndexes(shareService.indexes());
	
		
		page(sharesSearchAO);
	
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
	
	

}
