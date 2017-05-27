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
		realtimeCourses.assign(sharePortfolioService.sharePortfolio(realtimeCourses.getPortfolioId()));
		realtimeCourses.setExchangeRates(sharePortfolioService.realtimeExchangeRates(realtimeCourses.getPortfolioId()));
		realtimeCourses.assign(sharePortfolioService.realtimeTimeCourses(realtimeCourses.getPortfolioId(), realtimeCourses.getLastStoredTimeCourse()));
		
	}





	
	
	
}
