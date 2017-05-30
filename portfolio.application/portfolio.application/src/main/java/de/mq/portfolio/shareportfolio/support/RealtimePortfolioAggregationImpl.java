package de.mq.portfolio.shareportfolio.support;


import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

class RealtimePortfolioAggregationImpl {
	
	enum RealTimeCourseAttribute {
		Currency,
		ShareName,
		Weight,
		LastRate,
		RealTimeRate
		
	}

	private final String portfolioName;
	
	



	private final String portfolioCurrency;
	
	private final Map<String,Map<RealTimeCourseAttribute,Object>> timeCourseAttributeMap = new HashMap<>();

	
	
	private final Map<String, Data[]> exchangeRates = new HashMap<>();
	
	
	RealtimePortfolioAggregationImpl(final SharePortfolio sharePortfolio,  Collection<Entry<TimeCourse, List<Data>>> realtimeCourses, final Map<String, Data[]> exchangeRates ) {
		portfolioName=sharePortfolio.name();
		portfolioCurrency=sharePortfolio.currency();
		
		final Map<String, Double> weights = sharePortfolio.min().entrySet().stream().map(entry -> new AbstractMap.SimpleImmutableEntry<>( entry.getKey().code(), (Double) entry.getValue())).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		realtimeCourses.stream().forEach(entry -> add(entry, weights));
		
		
		this.exchangeRates.putAll(exchangeRates);
		
	}


	
	void add(Entry<TimeCourse, List<Data>> entry, final Map<String,Double> weights  ) {
		final Map<RealTimeCourseAttribute,Object> values = new HashMap<>();
		values.put(RealTimeCourseAttribute.Currency, entry.getKey().share().currency());
		values.put(RealTimeCourseAttribute.ShareName, entry.getKey().share().name());
		values.put(RealTimeCourseAttribute.Weight, weights.get(entry.getKey().code()));
		values.put(RealTimeCourseAttribute.LastRate, entry.getValue().get(0));
		values.put(RealTimeCourseAttribute.RealTimeRate, entry.getValue().get(1));
		timeCourseAttributeMap.put(entry.getKey().code(), values);
	}
	
	
	
	
	public String portfolioCurrency() {
		return portfolioCurrency;
	}
	
	public String portfolioName() {
		return portfolioName;
	}


	
	public final double lastShareRate(final String code) {
		return ((Data) timeCourseAttributeMap(code, RealTimeCourseAttribute.LastRate)).value();
	}
	
	public final Date lastShareDate(final String code) {
		return ((Data) timeCourseAttributeMap(code, RealTimeCourseAttribute.LastRate)).date();
	}
	
	public final double shareRealtimeRate(final String code) {
		return  ((Data)timeCourseAttributeMap(code, RealTimeCourseAttribute.RealTimeRate)).value();
	}
	
	
	public final double shareRateOfReturn(final String code) {
		return shareRealtimeRate(code) - lastShareRate(code);
	}
	
	public final double shareRateOfReturnPercent (final String code) {
		return 100d* (shareRealtimeRate(code) - lastShareRate(code)) / lastShareRate(code) ;
	}
	
	public final String shareName(final String code) {
		return  timeCourseAttributeMap(code, RealTimeCourseAttribute.ShareName);
	}
	
	public final String shareCurrency(final String code) {
		return  timeCourseAttributeMap(code, RealTimeCourseAttribute.Currency);
	}
	
	public final Collection<String> shareCodes() {
		return Collections.unmodifiableSet(timeCourseAttributeMap.keySet());
	}

	
	public final double lastExchangeRateForCurrency(final String currencyCode) {
		return exchangeRates.get(currencyCode)[0].value();
	}
	
	public final Date lastExchangeRateDate(final String currencyCode) {
		return exchangeRates.get(currencyCode)[0].date();
	}
	
	public final double realtimeExchangeRateForCurrency(final String currencyCode) {
		return exchangeRates.get(currencyCode)[1].value();
	}
	
	public final Date realtimeExchangeRateDate(final String currencyCode) {
		return exchangeRates.get(currencyCode)[1].date();
	}
	
	public final double rateOfReturnPercentExchangeRate (final String currency) {
		return 100d* (realtimeExchangeRateForCurrency(currency) - lastExchangeRateForCurrency(currency)) / lastExchangeRateForCurrency(currency) ;
	}
	
	public final Collection<String> currencies() {
		return Collections.unmodifiableSet(exchangeRates.keySet());
	}
	
	public final Collection<String> translatedCurrencies() {
		return currencies().stream().filter(currency -> ! currency.equals(this.portfolioCurrency)).collect(Collectors.toSet());
	}

	
	@SuppressWarnings("unchecked")
	private <T>  T timeCourseAttributeMap(final String code, final RealTimeCourseAttribute key) {
		Assert.isTrue(timeCourseAttributeMap.containsKey(code), "Attributes missing for " + code );
		Assert.notNull((((Map<RealTimeCourseAttribute,T>) timeCourseAttributeMap.get(code)).get(key)), "Value missing for " + key + " code " + code );
		return ((Map<RealTimeCourseAttribute,T>) timeCourseAttributeMap.get(code)).get(key);
	}

}
