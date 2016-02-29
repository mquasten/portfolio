package de.mq.portfolio.share.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.ShareService;

@Component("sharesController")
@Scope("singleton")
public class SharesControllerImpl {
	
	private final ShareService shareService;
	
	@Autowired
	SharesControllerImpl(final ShareService shareService) {
		this.shareService = shareService;
	}

	
	public final void init(final SharesSearchAO sharesSearchAO) {
		
		sharesSearchAO.setIndexes(shareService.indexes());
		page(sharesSearchAO);
	
	}
	
	public final void page(final SharesSearchAO sharesSearchAO) {
		
		sharesSearchAO.setPageable(shareService.pageable(sharesSearchAO.getSearch(), 10));
		
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
