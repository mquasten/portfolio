package de.mq.portfolio.shareportfolio.support;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("realtimeCoursesController")
public class RealtimeCoursesController {
	
	private final SharePortfolioService sharePortfolioService;
	private final ShareService shareService;
	
	private final List<Entry<TimeCourse, List<Data>>> timeCourses = new ArrayList<>(); ; 
	





	@Autowired
	public RealtimeCoursesController(SharePortfolioService sharePortfolioService, ShareService shareService) {
		this.sharePortfolioService = sharePortfolioService;
		this.shareService = shareService;
	}


	
	
	
	public void init(RealtimeCoursesAO realtimeCourses) {
		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(realtimeCourses.getPortfolioId());
		final Map<String, TimeCourse> timeCoursesMap = new HashMap<>();
		
		sharePortfolio.timeCourses().stream().forEach(tc -> timeCoursesMap.put(tc.code(),tc));
		timeCourses.clear();
		
		timeCourses.addAll(shareService.realTimeCourses(timeCoursesMap.keySet()).stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(timeCoursesMap.get(tc.code()), tc.rates())).collect(Collectors.toList()));
		
		
	}

	
	public List<Entry<TimeCourse, List<Data>>> getTimeCourses() {
		return timeCourses;
	}
}
