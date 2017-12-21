package de.mq.portfolio.exchangerate.support;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective;
import de.mq.portfolio.shareportfolio.support.SharePortfolioService;

@Component("exchangeRateController")
@Scope("singleton")
public abstract class ExchangeRateController {

	private final ExchangeRateService exchangeRateService;

	private final SharePortfolioService sharePortfolioService;

	private final Converter<String, String> currencyConverter;

	static final String REDIRECT_PATTERN = "exchangeRates?filter=%s&period=%s&faces-redirect=true";
	static final String REDIRECT_PATTERN_PORTFOLIO = "exchangeRatesPortfolio?portfolioId=%s&filter=%s&period=%s&faces-redirect=true";

	@Autowired
	ExchangeRateController(final ExchangeRateService exchangeRateService, final SharePortfolioService sharePortfolioService, @Qualifier("currencyConverter") final Converter<String, String> currencyConverter) {
		this.exchangeRateService = exchangeRateService;
		this.sharePortfolioService = sharePortfolioService;
		this.currencyConverter = currencyConverter;
	}

	public void init() {

		final ExchangeRatesAO exchangeRatesAO = exchangeRatesAO();
		final List<Date> dates = new ArrayList<>();

		dates.add(Date.from(LocalDateTime.now().minusDays(exchangeRatesAO.period()).truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant()));

		final Collection<ExchangeRate> rates = new ArrayList<>();
		rates(exchangeRatesAO, rates, dates);

		exchangeRatesAO.setExchangeRateRetrospectives(
				rates.stream().map(rate -> exchangeRateRetrospectiveBuilder().withTarget(rate.target()).withName(currencyConverter.convert(rate.source()) + "-" + currencyConverter.convert(rate.target())).withStartDate(Collections.max(dates)).withExchangeRates(rate.rates()).build()).collect(Collectors.toList()));

		exchangeRatesAO.assign(rates.stream().map(exchangeRate -> new AbstractMap.SimpleImmutableEntry<>(exchangeRate.source() + "-" + exchangeRate.target(), series(exchangeRate, Collections.max(dates)))).collect(Collectors.toList()));
	}

	private void rates(final ExchangeRatesAO exchangeRatesAO, final Collection<ExchangeRate> rates, final Collection<Date> dates) {
		if (!StringUtils.hasText(exchangeRatesAO.getPortfolioId())) {
			rates.addAll(exchangeRateService.exchangeRates());
			return;
		}

		final SharePortfolioRetrospective sharePortfolioRetrospective = sharePortfolioService.retrospective(exchangeRatesAO.getPortfolioId());

		rates.addAll(exchangeRateService.exchangeRates(sharePortfolioRetrospective.committedSharePortfolio().exchangeRateTranslations()));
		dates.add(sharePortfolioRetrospective.initialRateWithExchangeRate().date());

		exchangeRatesAO.setPortfolioName(sharePortfolioRetrospective.committedSharePortfolio().name());

	}

	private ChartSeries series(final ExchangeRate exchangeRate, final Date startDate) {
		final ExchangeRateRetrospective exchangeRateRetrospective = exchangeRateRetrospectiveBuilder().withTarget(exchangeRate.target()).withName(currencyConverter.convert(exchangeRate.source()) + "-" + currencyConverter.convert(exchangeRate.target())).withStartDate(startDate).withExchangeRates(exchangeRate.rates()).build();
		final LineChartSeries series = new LineChartSeries(exchangeRateRetrospective.name());
		series.setShowMarker(false);

		exchangeRateRetrospective.exchangeRates().forEach(data -> series.set(data.date().getTime(), Double.valueOf(data.value())));
		return series;
	}

	public String show() {
		final ExchangeRatesAO exchangeRatesAO = exchangeRatesAO();
		if (exchangeRatesAO.getPortfolioId() != null) {
			return String.format(REDIRECT_PATTERN_PORTFOLIO, exchangeRatesAO().getPortfolioId(), exchangeRatesAO().getFilter(), exchangeRatesAO().getPeriod());
		}
		return String.format(REDIRECT_PATTERN, exchangeRatesAO().getFilter(), exchangeRatesAO().getPeriod());
	}

	@Lookup
	abstract ExchangeRatesAO exchangeRatesAO();

	@Lookup
	abstract ExchangeRateRetrospectiveBuilder exchangeRateRetrospectiveBuilder();

}
