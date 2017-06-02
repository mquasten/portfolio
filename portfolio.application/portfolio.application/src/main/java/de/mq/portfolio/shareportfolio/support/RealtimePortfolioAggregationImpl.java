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

class RealtimePortfolioAggregationImpl implements RealtimePortfolioAggregation {

	enum RealTimeCourseAttribute {
		Currency, ShareName, Weight, LastRate, RealTimeRate

	}

	private final String portfolioName;

	private final String portfolioCurrency;

	private final Map<String, Map<RealTimeCourseAttribute, Object>> timeCourseAttributeMap = new HashMap<>();

	private final Map<String, String> currencies = new HashMap<>();
	final Map<String, Double> weights = new HashMap<>();
	private final Map<String, Data[]> exchangeRates = new HashMap<>();

	RealtimePortfolioAggregationImpl(final SharePortfolio sharePortfolio, Collection<Entry<TimeCourse, List<Data>>> realtimeCourses, final Map<String, Data[]> exchangeRates) {
		portfolioName = sharePortfolio.name();
		portfolioCurrency = sharePortfolio.currency();

		this.weights.putAll(sharePortfolio.min().entrySet().stream().map(entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey().code(), (Double) entry.getValue())).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
		realtimeCourses.stream().forEach(entry -> add(entry, weights));

		currencies.putAll(realtimeCourses.stream().map(x -> x.getKey()).collect(Collectors.toMap(tc -> tc.code(), tc -> tc.share().currency())));

		exchangeRateDataSizeGuard(exchangeRates);

		this.exchangeRates.putAll(exchangeRates);

	}

	private void exchangeRateDataSizeGuard(final Map<String, Data[]> exchangeRates) {
		exchangeRates.values().forEach(rates -> Assert.isTrue(rates.length == 2, "2 Exchangerates required (Last and realtime)."));
	}

	void add(Entry<TimeCourse, List<Data>> entry, final Map<String, Double> weights) {
		final Map<RealTimeCourseAttribute, Object> values = new HashMap<>();
		Assert.notNull(entry.getKey().share(), "Share is mandatory.");
		Assert.notNull(entry.getKey().share().currency(), "Currency is mandatory.");
		values.put(RealTimeCourseAttribute.Currency, entry.getKey().share().currency());
		values.put(RealTimeCourseAttribute.ShareName, entry.getKey().share().name());
		Assert.notNull(entry.getKey().code(), "Code is mandatory.");
		values.put(RealTimeCourseAttribute.Weight, weights.get(entry.getKey().code()));
		Assert.isTrue(entry.getValue().size() == 2, "2 TimeCourses expected, (last and realtime).");
		values.put(RealTimeCourseAttribute.LastRate, entry.getValue().get(0));
		values.put(RealTimeCourseAttribute.RealTimeRate, entry.getValue().get(1));
		timeCourseAttributeMap.put(entry.getKey().code(), values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * portfolioCurrency()
	 */
	@Override
	public final String portfolioCurrency() {
		return portfolioCurrency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * portfolioName()
	 */
	@Override
	public final String portfolioName() {
		return portfolioName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * lastShareRate(java.lang.String)
	 */
	@Override
	public final double lastShareRate(final String code) {
		return ((Data) timeCourseAttributeMap(code, RealTimeCourseAttribute.LastRate)).value();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * lastShareDate(java.lang.String)
	 */
	@Override
	public final Date lastShareDate(final String code) {
		return ((Data) timeCourseAttributeMap(code, RealTimeCourseAttribute.LastRate)).date();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * shareRealtimeRate(java.lang.String)
	 */
	@Override
	public final double shareRealtimeRate(final String code) {
		return ((Data) timeCourseAttributeMap(code, RealTimeCourseAttribute.RealTimeRate)).value();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * shareRateOfReturn(java.lang.String)
	 */
	@Override
	public final double shareDelata(final String code) {
		return shareRealtimeRate(code) - lastShareRate(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * shareRateOfReturnPercent(java.lang.String)
	 */
	@Override
	public final double shareDeltaPercent(final String code) {
		return 100d * (shareRealtimeRate(code) - lastShareRate(code)) / lastShareRate(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * shareName(java.lang.String)
	 */
	@Override
	public final String shareName(final String code) {
		return timeCourseAttributeMap(code, RealTimeCourseAttribute.ShareName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * shareCurrency(java.lang.String)
	 */
	@Override
	public final String shareCurrency(final String code) {
		return timeCourseAttributeMap(code, RealTimeCourseAttribute.Currency);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * shareCodes()
	 */
	@Override
	public final Collection<String> shareCodes() {
		return Collections.unmodifiableSet(timeCourseAttributeMap.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * lastExchangeRateForCurrency(java.lang.String)
	 */
	@Override
	public final double lastExchangeRateForCurrency(final String currencyCode) {
		return exchangeRates.get(currencyCode)[0].value();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * lastExchangeRateDate(java.lang.String)
	 */
	@Override
	public final Date lastExchangeRateDate(final String currencyCode) {
		return exchangeRates.get(currencyCode)[0].date();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * realtimeExchangeRateForCurrency(java.lang.String)
	 */
	@Override
	public final double realtimeExchangeRateForCurrency(final String currencyCode) {
		return exchangeRates.get(currencyCode)[1].value();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * realtimeExchangeRateDate(java.lang.String)
	 */
	@Override
	public final Date realtimeExchangeRateDate(final String currencyCode) {
		return exchangeRates.get(currencyCode)[1].date();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * rateOfReturnPercentExchangeRate(java.lang.String)
	 */
	@Override
	public final double deltaPercentExchangeRate(final String currency) {
		return 100d * (realtimeExchangeRateForCurrency(currency) - lastExchangeRateForCurrency(currency)) / lastExchangeRateForCurrency(currency);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * currencies()
	 */
	@Override
	public final Collection<String> currencies() {
		return Collections.unmodifiableSet(exchangeRates.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * translatedCurrencies()
	 */
	@Override
	public final Collection<String> translatedCurrencies() {
		return currencies().stream().filter(currency -> !currency.equals(this.portfolioCurrency)).collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	private <T> T timeCourseAttributeMap(final String code, final RealTimeCourseAttribute key) {
		Assert.isTrue(timeCourseAttributeMap.containsKey(code), "Attributes missing for " + code);
		Assert.notNull((((Map<RealTimeCourseAttribute, T>) timeCourseAttributeMap.get(code)).get(key)), "Value missing for " + key + " code " + code);
		return ((Map<RealTimeCourseAttribute, T>) timeCourseAttributeMap.get(code)).get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * lastRatePortfolio(java.lang.String)
	 */
	@Override
	public final double lastRatePortfolio(final String code) {
		return lastShareRate(code) * weight(code) * exchangeRate(code, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * realtimeRatePortfolio(java.lang.String)
	 */
	@Override
	public final double realtimeRatePortfolio(final String code) {
		return shareRealtimeRate(code) * weight(code) * exchangeRate(code, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * weight(java.lang.String)
	 */
	@Override
	public final Double weight(final String code) {
		Assert.isTrue(weights.containsKey(code), "Weight id mandatory for " + code);
		return weights.get(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * deltaPortfolio(java.lang.String)
	 */
	@Override
	public final double deltaPortfolio(final String code) {
		return realtimeRatePortfolio(code) - lastRatePortfolio(code);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.RealtimePortfolioAggregation#
	 * rateOfReturnPercent(java.lang.String)
	 */
	@Override
	public final double deltaPortfolioPercent(final String code) {
		final double wahr = lastRatePortfolio(code);
		return (realtimeRatePortfolio(code) - wahr) / wahr;
	}

	private double exchangeRate(final String code, final int index) {
		final String currency = (currencies.get(code));
		Assert.notNull(currency, "Currency not found for share " + code);
		final Data[] exchangerates = exchangeRates.get(currency);
		Assert.notNull(exchangerates, "ExchangeRate not found for currency " + currency);

		return exchangerates[index].value();
	}

}
