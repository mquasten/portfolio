package de.mq.portfolio.shareportfolio.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("realtimeCoursesController")
public class RealtimeCoursesController {
	
	private final SharePortfolioService sharePortfolioService;

	






	@Autowired
	public RealtimeCoursesController(final SharePortfolioService sharePortfolioService) {
		this.sharePortfolioService = sharePortfolioService;
	}


	
	
	
	public void init(final RealtimeCoursesAO realtimeCourses) {
		final RealtimePortfolioAggregation realtimePortfolioAggregation = sharePortfolioService.realtimePortfolioAggregation(realtimeCourses.getPortfolioId(), realtimeCourses.getLastStoredTimeCourse());
		
		realtimeCourses.assign( realtimePortfolioAggregation);
	
	}





	
	
	
}
