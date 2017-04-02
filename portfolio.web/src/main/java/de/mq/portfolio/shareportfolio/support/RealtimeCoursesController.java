package de.mq.portfolio.shareportfolio.support;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("realtimeCoursesController")
public class RealtimeCoursesController {
	
	private final SharePortfolioService sharePortfolioService;
	private final ShareService shareService;
	
	





	@Autowired
	public RealtimeCoursesController(SharePortfolioService sharePortfolioService, ShareService shareService) {
		this.sharePortfolioService = sharePortfolioService;
		this.shareService = shareService;
	}


	
	
	
	public void init(RealtimeCoursesAO realtimeCourses) {
		
		System.out.println(realtimeCourses);
		
		System.out.println(realtimeCourses.getPortfolioId());
	
		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(realtimeCourses.getPortfolioId());
		final Map<String, TimeCourse> timeCoursesMap = new HashMap<>();
		
		sharePortfolio.timeCourses().stream().forEach(tc -> timeCoursesMap.put(tc.code(),tc));
		
		
		realtimeCourses.assign(shareService.realTimeCourses(timeCoursesMap.keySet()).stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(timeCoursesMap.get(tc.code()), tc.rates())).collect(Collectors.toList()));
	
		
	}

	
	
}
