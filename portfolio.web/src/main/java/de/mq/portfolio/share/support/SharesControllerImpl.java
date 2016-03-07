package de.mq.portfolio.share.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.ShareService;

@Component("sharesController")
@Scope("singleton")
public class SharesControllerImpl {
	
	private final ShareService shareService;
	private final Map<String,Sort> orderBy = new HashMap<>();
	
	@Autowired
	SharesControllerImpl(final ShareService shareService) {
		this.shareService = shareService;
		orderBy.put("id", new Sort("id"));
		orderBy.put("name", new Sort("share.name", "id"));
		orderBy.put("meanRate", new Sort(Direction.DESC, "meanRate" ,"id"));
		orderBy.put("totalRate", new Sort(Direction.DESC, "totalRate" ,"id"));
		orderBy.put("totalRateDividends", new Sort(Direction.DESC, "totalRateDividends" ,"id"));
		orderBy.put("standardDeviation", new Sort("standardDeviation" ,"id"));
	}

	
	public final void init(final SharesSearchAO sharesSearchAO) {
		
		sharesSearchAO.setIndexes(shareService.indexes());
	
		/* orderBy.add(new SelectItem(new Sort("id") , "---"));
		orderBy.add(new SelectItem(new Sort("name", "id") , "name"));
		orderBy.add(new SelectItem(new Sort(Direction.DESC , "meanRate", "id") , "Performance Tag"));
		orderBy.add(new SelectItem(new Sort(Direction.DESC , "totalRate", "id") , "Performance Gesamt"));
		orderBy.add(new SelectItem(new Sort(Direction.DESC , "totalRateDividends", "id") , "Performance Dividenden"));
		orderBy.add(new SelectItem(new Sort("standardDeviation", "id") , "Risiko"));*/
		
		page(sharesSearchAO);
	
	}
	
	public final void page(final SharesSearchAO sharesSearchAO) {
		System.out.println(sharesSearchAO.getSelectedSort());
		System.out.println(orderBy.get(sharesSearchAO.getSelectedSort()));
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
