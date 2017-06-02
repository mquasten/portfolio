package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

public interface RealtimePortfolioAggregationBuilder {
	
	RealtimePortfolioAggregationBuilder withSharePortfolio(final SharePortfolio sharePortfolio); 
	
	RealtimePortfolioAggregationBuilder withRealtimeCourses(Collection<Entry<TimeCourse, List<Data>>> realtimeCourses); 
	
	RealtimePortfolioAggregationBuilder withRealtimeExchangeRates(Collection<ExchangeRate> realtimeExhangeRates);
	
	RealtimePortfolioAggregation build();
	
	default Class<? extends RealtimePortfolioAggregation> target() {
		return RealtimePortfolioAggregationImpl.class;
	}

}
