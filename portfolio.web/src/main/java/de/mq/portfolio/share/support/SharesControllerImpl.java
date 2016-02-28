package de.mq.portfolio.share.support;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;

@Component("sharesController")
@Scope("singleton")
public class SharesControllerImpl {
	
	private final ShareService shareService;
	
	@Autowired
	SharesControllerImpl(final ShareService shareService) {
		this.shareService = shareService;
	}

	public final Collection<Entry<Share,TimeCourse>> timeCourses(final SharesSearchAO sharesSearchAO) {
		
		System.out.println("*** search ***");
	if( sharesSearchAO.getPageable() == null) {
		sharesSearchAO.setPageable(shareService.pageable(sharesSearchAO.getSearch(), 10));
	}
		
	 return shareService.timeCourses(sharesSearchAO.getPageable(), sharesSearchAO.getSearch()).stream().map(tc -> new AbstractMap.SimpleImmutableEntry<Share,TimeCourse>(tc.share(), tc)).collect(Collectors.toList());
	}
	
	public final void page(final SharesSearchAO sharesSearchAO) {
		System.out.println("setPage");
		sharesSearchAO.setPageable(shareService.pageable(sharesSearchAO.getSearch(), 10));
		
	}
	
	
	public final void next(final SharesSearchAO sharesSearchAO) {
		if( sharesSearchAO.getPageable() == null ) {
			
			return;
		}
		
		sharesSearchAO.setPageable(sharesSearchAO.getPageable().next());
	}
	
	public final void previous(final SharesSearchAO sharesSearchAO) {
		if( sharesSearchAO.getPageable() == null ) {
			
			return;
		}
		
	
		sharesSearchAO.setPageable(((ClosedIntervalPageRequest)sharesSearchAO.getPageable()).previous());
	}
	
	public final void first(final SharesSearchAO sharesSearchAO) {
		if( sharesSearchAO.getPageable() == null ) {
			
			return;
		}
		sharesSearchAO.setPageable(sharesSearchAO.getPageable().first());
		
	}
	
	public final void last(final SharesSearchAO sharesSearchAO) {
		if( sharesSearchAO.getPageable() == null ) {
			
			return;
		}
		sharesSearchAO.setPageable(((ClosedIntervalPageRequest)sharesSearchAO.getPageable()).last());
		
	}

}
