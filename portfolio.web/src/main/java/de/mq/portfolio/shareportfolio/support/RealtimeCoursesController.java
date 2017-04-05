package de.mq.portfolio.shareportfolio.support;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("realtimeCoursesController")
public class RealtimeCoursesController {
	
	private final SharePortfolioService sharePortfolioService;
	private final ShareService shareService;
	
	private final ExchangeRateService exchangeRateService;





	@Autowired
	public RealtimeCoursesController(final SharePortfolioService sharePortfolioService, final ShareService shareService, final ExchangeRateService exchangeRateService) {
		this.sharePortfolioService = sharePortfolioService;
		this.shareService = shareService;
		this.exchangeRateService =  exchangeRateService;
	}


	
	
	
	public void init(final RealtimeCoursesAO realtimeCourses) {
	
		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(realtimeCourses.getPortfolioId());
		
		realtimeCourses.setFactors(factors(sharePortfolio));
		final Map<String, TimeCourse> timeCoursesMap = new HashMap<>();
		
		sharePortfolio.timeCourses().stream().forEach(tc -> timeCoursesMap.put(tc.code(),tc));
			
		realtimeCourses.assign(shareService.realTimeCourses(timeCoursesMap.keySet()).stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(timeCoursesMap.get(tc.code()), tc.rates())).collect(Collectors.toList()));
	
		
	}





	private final Map<String, Double>  factors(final SharePortfolio sharePortfolio ) {
		final Map<String, Double> factors = new HashMap<>();
		final ExchangeRateCalculator exchangeRateCalculator = exchangeRateService.exchangeRateCalculator(sharePortfolio.exchangeRateTranslations());
		final Map<TimeCourse,Double> weights = sharePortfolio.min();
		final Map<String,Date> endDates = new HashMap<>();
		sharePortfolio.timeCourses().stream().map(tc -> tc.code()).forEach(code -> endDates.put(code, shareService.timeCourse(code).get().end()));
		
		sharePortfolio.timeCourses().forEach(tc -> factors.put(tc.code(), exchangeRateCalculator.factor(sharePortfolio.exchangeRate(tc), endDates.get(tc.code())) * weights.get(tc)));
	    return factors;
	}

	
	
}
