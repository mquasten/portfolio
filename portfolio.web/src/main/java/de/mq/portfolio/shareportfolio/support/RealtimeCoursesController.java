package de.mq.portfolio.shareportfolio.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("realtimeCoursesController")
public class RealtimeCoursesController {
	
	private final SharePortfolioService sharePortfolioService;

	






	@Autowired
	public RealtimeCoursesController(final SharePortfolioService sharePortfolioService) {
		this.sharePortfolioService = sharePortfolioService;
	}


	
	
	
	public void init(final RealtimeCoursesAO realtimeCourses) {
		realtimeCourses.assign(sharePortfolioService.sharePortfolio(realtimeCourses.getPortfolioId()));
		SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio((realtimeCourses.getPortfolioId()));
		realtimeCourses.setExchangeRates(sharePortfolioService.realtimeExchangeRates(sharePortfolio));
		realtimeCourses.assign(sharePortfolioService.realtimeTimeCourses(sharePortfolio, realtimeCourses.getLastStoredTimeCourse()));
		
		final RealtimePortfolioAggregation realtimePortfolioAggregation = sharePortfolioService.realtimePortfolioAggregation(realtimeCourses.getPortfolioId(), realtimeCourses.getLastStoredTimeCourse());
		
		
		System.out.println(realtimePortfolioAggregation);
	}





	
	
	
}
