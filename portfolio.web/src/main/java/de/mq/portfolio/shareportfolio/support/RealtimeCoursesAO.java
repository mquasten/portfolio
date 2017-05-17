package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
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

@Component("realtimeCourses")
@Scope("view")
public class RealtimeCoursesAO implements Serializable {

	static final String DELTA_PERCENT_COLUMN = "deltaPercent";

	static final String DELTA_COLUMN = "delta";

	static final String CURRENT_COLUMN = "current";

	static final String LAST_COLUMN = "last";
	static final String LAST_DATE_COLUMN = "lastDate";

	static final String NAME_COLUMN = "name";

	static final String CURRENCY_COLUMN = "currency";

	
	static final String WEIGHT_COLUMN = "weight";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	private final Collection<Map<String, Object>> shares = new ArrayList<>();

	private final Collection<Map<String, Object>> realtimeCourses = new ArrayList<>();

	private String portfolioName;
	private boolean lastStoredTimeCourse = true;

	private String portfolioCurrency;

	private final Map<String, Double> exchangeRates = new HashMap<>();
	
	
	private final Collection<Map<String, Object>> realtimeExchangeRates = new ArrayList<>();
	
	

	private String portfolioId;

	private String filter;
	private Map<String, Double> weights = new HashMap<>();

	public String getPortfolioName() {
		return portfolioName;
	}

	public String getPortfolioCurrency() {
		return portfolioCurrency;
	}

	public Collection<Map<String, Object>> getRealtimeCourses() {
		return realtimeCourses;
	}

	public String getCurrencyColumn() {
		return CURRENCY_COLUMN;
	}

	public String getNameColumn() {
		return NAME_COLUMN;
	}

	public String getLastColumn() {
		return LAST_COLUMN;
	}

	public String getLastDateColumn() {
		return LAST_DATE_COLUMN;
	}

	public String getCurrentColumn() {
		return CURRENT_COLUMN;
	}

	public String getDeltaColumn() {
		return DELTA_COLUMN;
	}

	public String getDeltaPercentColumn() {
		return DELTA_PERCENT_COLUMN;
	}
	public String getWeightColumn() {
		return WEIGHT_COLUMN;
	}

	public Collection<Map<String, Object>> getShares() {
		return shares;
	}

	void assign(final SharePortfolio sharePortfolio) {
		Assert.notNull(sharePortfolio, "SharePortfolio is mandatory");
		portfolioName = sharePortfolio.name();
		portfolioCurrency = sharePortfolio.currency();
		weights.clear();
		weights.putAll(sharePortfolio.min().entrySet().stream().map(entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey().code(), entry.getValue())).collect(Collectors.toMap(entry -> entry.getKey(), entry ->entry.getValue())));
	
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}

	void assign(final List<Entry<TimeCourse, List<Data>>> entries) {
		addShares(entries);
		addRealTimeCourses(entries);
	}

	private void addRealTimeCourses(final List<Entry<TimeCourse, List<Data>>> entries) {
		realtimeCourses.clear();
		realtimeCourses.add(realTimeEntryForPortfolio(entries));
		realtimeCourses.addAll(entries.stream().map(entry -> shareEntryToRealtimeCourseMap(entry)).collect(Collectors.toList()));

	}

	private Map<String, Object> realTimeEntryForPortfolio(final List<Entry<TimeCourse, List<Data>>> entries) {
		final double lastSum = sum(entries, 0);
		final double currentSum = sum(entries, 1);

		final Map<String, Object> values = new HashMap<>();
		values.put(NAME_COLUMN, this.portfolioName);
	
		values.put(LAST_COLUMN, lastSum);

		values.put(CURRENT_COLUMN, currentSum);
		values.put(DELTA_COLUMN, currentSum - lastSum);
		values.put(DELTA_PERCENT_COLUMN, 100 * (currentSum - lastSum) / lastSum);
		return values;
	}

	private double sum(final List<Entry<TimeCourse, List<Data>>> entries, final int index) {
		return entries.stream().mapToDouble(entry -> entry.getValue().get(index).value() * factor(entry.getKey().code())).sum();
	}

	private void addShares(final List<Entry<TimeCourse, List<Data>>> entries) {
		shares.clear();
		shares.addAll(entries.stream().map(entry -> shareEntryToMap(entry)).collect(Collectors.toList()));
	}

	void setExchangeRates(final Map<String, Double> exchangeRates) {
		this.exchangeRates.clear();
		this.exchangeRates.putAll(exchangeRates);
	}
	
	void setExchangeRates(Collection<ExchangeRate> realtimeExchangeRates) {
		this.realtimeExchangeRates.clear();
		realtimeExchangeRates.stream().forEach(rate -> this.realtimeExchangeRates.add(exchangeRatesToMap(rate)));
	}

	

	private Map<String,Object> exchangeRatesToMap(ExchangeRate exchangeRate) {
		final Map<String,Object> results = new HashMap<>();
		results.put(NAME_COLUMN,  exchangeRate.source());
		results.put(LAST_COLUMN,  exchangeRate.rates().get(0).value());
		results.put(CURRENT_COLUMN,  exchangeRate.rates().get(1).value());
		return results;
	}

	
	
	
	
	

	private Map<String, Object> shareEntryToMap(final Entry<TimeCourse, List<Data>> entry) {
		final Map<String, Object> values = new HashMap<>();
		values.put(NAME_COLUMN, entry.getKey().name() + " (" + entry.getKey().code() + ")");

		values.put(LAST_COLUMN, entry.getValue().get(0).value());
		values.put(CURRENT_COLUMN, entry.getValue().get(1).value());
		values.put(DELTA_COLUMN, entry.getValue().get(1).value() - (Double) entry.getValue().get(0).value());
		values.put(DELTA_PERCENT_COLUMN, 100 * (entry.getValue().get(1).value() - (Double) entry.getValue().get(0).value()) / entry.getValue().get(0).value());
		values.put(CURRENCY_COLUMN, entry.getKey().share().currency());

		return values;
	}

	private Map<String, Object> shareEntryToRealtimeCourseMap(final Entry<TimeCourse, List<Data>> entry) {
		final Map<String, Object> values = new HashMap<>();
		values.put(NAME_COLUMN, entry.getKey().name() + " (" + entry.getKey().code() + ")");
		
		
		values.put(WEIGHT_COLUMN, weights.get(entry.getKey().code()));
		final double factor = factor(entry.getKey().code());

		final double last = entry.getValue().get(0).value() * factor;
		values.put(LAST_COLUMN, last);

		values.put(LAST_DATE_COLUMN, entry.getValue().get(0).date());
		final double current = entry.getValue().get(1).value() * factor;
		values.put(CURRENT_COLUMN, current);

		values.put(DELTA_COLUMN, current - last);
		values.put(DELTA_PERCENT_COLUMN, 100 * (current - last) / last);
		return values;

	}

	private double factor(final String code) {
		Assert.isTrue(exchangeRates.containsKey(code) , String.format("ExchangeRate not found for %s", code));
		Assert.isTrue(weights.containsKey(code) , String.format("Weight not found for %s", code));
		return exchangeRates.get(code) * weights.get(code);
	}

	public Boolean getLastStoredTimeCourse() {
		return lastStoredTimeCourse;
	}

	public void setLastStoredTimeCourse(Boolean lastStoredTimeCourse) {
		this.lastStoredTimeCourse = lastStoredTimeCourse;
	}
	
	public Collection<Map<String, Object>> getRealtimeExchangeRates() {
		return realtimeExchangeRates;
	}

}
