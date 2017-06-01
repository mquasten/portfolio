package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;
import java.util.Date;

public interface RealtimePortfolioAggregation {

	String portfolioCurrency();

	String portfolioName();

	double lastShareRate(final String code);

	Date lastShareDate(final String code);

	double shareRealtimeRate(final String code);

	double shareRateOfReturn(final String code);

	double shareRateOfReturnPercent(final String code);

	String shareName(final String code);

	String shareCurrency(final String code);

	Collection<String> shareCodes();

	double lastExchangeRateForCurrency(final String currencyCode);

	Date lastExchangeRateDate(final String currencyCode);

	double realtimeExchangeRateForCurrency(final String currencyCode);

	Date realtimeExchangeRateDate(final String currencyCode);

	double rateOfReturnPercentExchangeRate(final String currency);

	Collection<String> translatedCurrencies();

	Collection<String> currencies();

	double lastRatePortfolio(final String code);

	double realtimeRatePortfolio(final String code);

	double rateOfReturn(final String code);

	Double weight(final String code);

	double rateOfReturnPercent(final String code);

}
