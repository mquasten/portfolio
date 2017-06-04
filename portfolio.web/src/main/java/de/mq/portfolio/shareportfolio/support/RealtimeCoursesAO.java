package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component("realtimeCourses")
@Scope("view")
public class RealtimeCoursesAO implements Serializable {

	static final String DELTA_PERCENT_COLUMN = "deltaPercent";

	static final String DELTA_COLUMN = "delta";

	static final String CURRENT_COLUMN = "current";

	static final String LAST_COLUMN = "last";
	static final String LAST_DATE_COLUMN = "lastDate";
	
	static final String CURRENT_DATE_COLUMN = "currentDate";

	static final String NAME_COLUMN = "name";

	static final String CURRENCY_COLUMN = "currency";

	
	static final String WEIGHT_COLUMN = "weight";
	
	private static final long serialVersionUID = 1L;

	

	private String portfolioName;
	

	private String portfolioCurrency;

	
	
	
	private final Collection<Map<String, Object>> shares = new ArrayList<>();

	private final Collection<Map<String, Object>> realtimeCourses = new ArrayList<>();
	private final Collection<Map<String, Object>> realtimeExchangeRates = new ArrayList<>();
	
	
	private boolean lastStoredTimeCourse = true;
	private String portfolioId;

	private String filter;
	

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
	
	public String getCurrentDateColumn() {
		return CURRENT_DATE_COLUMN;
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

	void assign(final RealtimePortfolioAggregation realtimePortfolioAggregation) {
		
		Assert.notNull(realtimePortfolioAggregation, "RealtimePortfolioAggregation is mandatory");
		portfolioName = realtimePortfolioAggregation.portfolioName();
		portfolioCurrency = realtimePortfolioAggregation.portfolioCurrency();
		
		
		
		
		shares.clear();
		shares.addAll(realtimePortfolioAggregation.shareCodes().stream().map(code -> shareToMap(realtimePortfolioAggregation, code)).collect(Collectors.toList()));
		
		
		realtimeExchangeRates.clear();
		realtimeExchangeRates.addAll(realtimePortfolioAggregation.currencies().stream().map(currency -> exchangeRateToMap(realtimePortfolioAggregation, currency)).collect(Collectors.toList()));
		
		realtimeCourses.clear();
		
		realtimeCourses.add(portfolioSharesToMap(realtimePortfolioAggregation));
		
		
		realtimeCourses.addAll(realtimePortfolioAggregation.shareCodes().stream().map(code -> portfolioSharesToMap(realtimePortfolioAggregation, code)).collect(Collectors.toList()));
		
		
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

	

	
	
	private Map<String, Object> portfolioSharesToMap(final RealtimePortfolioAggregation realtimePortfolioAggregation) {
		final Map<String, Object> values = new HashMap<>();
		values.put(NAME_COLUMN, realtimePortfolioAggregation.portfolioName() );
		values.put(LAST_COLUMN, realtimePortfolioAggregation.lastRatePortfolio());
		values.put(CURRENT_COLUMN, realtimePortfolioAggregation.realtimeRatePortfolio());
		values.put(DELTA_COLUMN, realtimePortfolioAggregation.deltaPortfolio());
		values.put(DELTA_PERCENT_COLUMN, realtimePortfolioAggregation.deltaPortfolioPercent());
		return values;
	}

	

	

	private Map<String,Object> exchangeRateToMap(final RealtimePortfolioAggregation realtimePortfolioAggregation, final String currencyCode) {
		final Map<String,Object> results = new HashMap<>();
		results.put(NAME_COLUMN,  currencyCode);
		results.put(LAST_COLUMN,  realtimePortfolioAggregation.lastExchangeRateForCurrency(currencyCode));
		results.put(LAST_DATE_COLUMN, realtimePortfolioAggregation.lastExchangeRateDate(currencyCode));
		results.put(CURRENT_COLUMN, realtimePortfolioAggregation.realtimeExchangeRateForCurrency(currencyCode));
		results.put(CURRENT_DATE_COLUMN, realtimePortfolioAggregation.realtimeExchangeRateDate(currencyCode));
		results.put(DELTA_PERCENT_COLUMN, realtimePortfolioAggregation.deltaPercentExchangeRate(currencyCode));
		return results;
	}
	
	

	private  Map<String, Object> shareToMap(final RealtimePortfolioAggregation realtimePortfolioAggregation, final String code) {
		final Map<String, Object> values = new HashMap<>();
		
		realtimePortfolioAggregation.shareName(code);
		values.put(NAME_COLUMN, realtimePortfolioAggregation.shareName(code) + " (" + code + ")");
		values.put(LAST_COLUMN, realtimePortfolioAggregation.lastShareRate(code));
		values.put(CURRENT_COLUMN, realtimePortfolioAggregation.shareRealtimeRate(code));
		values.put(DELTA_COLUMN, realtimePortfolioAggregation.shareDelata(code));
		values.put(DELTA_PERCENT_COLUMN, realtimePortfolioAggregation.shareDeltaPercent(code));
		values.put(CURRENCY_COLUMN, realtimePortfolioAggregation.shareCurrency(code));
		return values;
		
	}
	

	
	private Map<String, Object> portfolioSharesToMap(final RealtimePortfolioAggregation realtimePortfolioAggregation, final String shareCode) {
		final Map<String, Object> values = new HashMap<>();
		values.put(NAME_COLUMN, realtimePortfolioAggregation.shareName(shareCode) + " (" + shareCode + ")");
		values.put(WEIGHT_COLUMN, realtimePortfolioAggregation.weight(shareCode));
		values.put(LAST_COLUMN, realtimePortfolioAggregation.lastRatePortfolio(shareCode));
		values.put(LAST_DATE_COLUMN, realtimePortfolioAggregation.lastShareDate(shareCode));
		values.put(CURRENT_COLUMN, realtimePortfolioAggregation.realtimeRatePortfolio(shareCode));
		values.put(DELTA_COLUMN, realtimePortfolioAggregation.deltaPortfolio(shareCode));
		values.put(DELTA_PERCENT_COLUMN, realtimePortfolioAggregation.deltaPortfolioPercent(shareCode));
		return values;
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
