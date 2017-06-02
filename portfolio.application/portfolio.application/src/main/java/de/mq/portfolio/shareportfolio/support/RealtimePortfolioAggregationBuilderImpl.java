package de.mq.portfolio.shareportfolio.support;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component
@Scope(scopeName = "prototype")
public class RealtimePortfolioAggregationBuilderImpl implements RealtimePortfolioAggregationBuilder {
	
	private Class<? extends RealtimePortfolioAggregation> targetClass= RealtimePortfolioAggregationImpl.class;

	
	private SharePortfolio sharePortfolio;
	
	private  final Collection<Entry<TimeCourse, List<Data>>> realtimeCourses = new ArrayList<>();
	
	
	
	private final Map<String, Data[]> exchangeRates = new HashMap<>();
	@Override
	public RealtimePortfolioAggregationBuilder withSharePortfolio(SharePortfolio sharePortfolio) {
		Assert.notNull(sharePortfolio, "Shareportfolio is mandatory.");
		Assert.isNull(this.sharePortfolio , "Shareportfolio already assigned.");
		this.sharePortfolio=sharePortfolio;
		return this;
	}

	@Override
	public RealtimePortfolioAggregationBuilder withRealtimeCourses(Collection<Entry<TimeCourse, List<Data>>> realtimeCourses) {
		Assert.notEmpty(realtimeCourses , "At least one TimeCourse must be given");
		Assert.isTrue(this.realtimeCourses.isEmpty(),"TimeCourseEntries already assigned." );
		this.realtimeCourses.clear();
		this.realtimeCourses.addAll(realtimeCourses);  
		return this;
	}

	@Override
	public RealtimePortfolioAggregationBuilder withRealtimeExchangeRates(final Collection<ExchangeRate> realtimeExhangeRates) {
		Assert.notEmpty(realtimeExhangeRates , "At least one ExhangeRate must be given");
		Assert.isTrue(this.exchangeRates.isEmpty(),"ExhangeRates already assigned." );
		
		realtimeExhangeRates.stream().map(exchangeRate -> exchangeRate.rates().size()).forEach(size -> Assert.isTrue(size==2, "2 TimeCourses expected, (last and realtime)." ));
		exchangeRates.clear();
		exchangeRates.putAll(realtimeExhangeRates.stream().map(exchangeRate -> new AbstractMap.SimpleImmutableEntry<>(exchangeRate.source(), new Data[] {exchangeRate.rates().get(0), exchangeRate.rates().get(1) })).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
		
		return this;
	}

	@Override
	public RealtimePortfolioAggregation build() {
		
		return null;
	}

}
