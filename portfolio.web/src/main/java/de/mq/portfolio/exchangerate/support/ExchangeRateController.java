package de.mq.portfolio.exchangerate.support;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
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
	
	
	//private Optional<Date> startDate = Optional.empty();

	private final ExchangeRateService exchangeRateService;
	
	private final  SharePortfolioService sharePortfolioService;
	
	private final Converter<String, String> currencyConverter;
	
	
	
	static final String REDIRECT_PATTERN = "exchangeRates?filter=%s&period=%s&faces-redirect=true";
	static final String REDIRECT_PATTERN_PORTFOLIO = "exchangeRatesPortfolio?portfolioId=%s&filter=%s&period=%s&faces-redirect=true";
	
	
	
	@Autowired
	ExchangeRateController(final ExchangeRateService exchangeRateService, final SharePortfolioService sharePortfolioService, @Qualifier("currencyConverter") final Converter<String, String> currencyConverter) {
		this.exchangeRateService = exchangeRateService;
		this.sharePortfolioService=sharePortfolioService;
		this.currencyConverter=currencyConverter;
	}


	public void init() {
			
		final ExchangeRatesAO exchangeRatesAO = exchangeRatesAO();
		
		//Optional<Date> portfolioDate = Optional.empty();
		final Date startDate = Date.from(LocalDateTime.now().minusDays(exchangeRatesAO.period()).truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant());
		
		
		final Collection<ExchangeRate> rates = new ArrayList<>();
		final Optional<Date> portfolioDate  = rates(exchangeRatesAO.getPortfolioId(), rates);
		final Date maxDate = Collections.max(Arrays.asList(portfolioDate.orElse(startDate),startDate));
		exchangeRatesAO.setExchangeRateRetrospectives(rates.stream().map(rate -> exchangeRateRetrospectiveBuilder().withName(currencyConverter.convert(rate.source()) + "-" +currencyConverter.convert(rate.target())).withStartDate(/*portfolioDate.orElse(startDate)*/ maxDate ).withExchangeRates(rate.rates()).build()).collect(Collectors.toList()));
		
		exchangeRatesAO.assign(rates.stream().map(exchangeRate -> new AbstractMap.SimpleImmutableEntry<>( exchangeRate.source() + "-" + exchangeRate.target(), series(exchangeRate, maxDate))
		).collect(Collectors.toList()));
	}


	private Optional<Date> rates(final String portfolioId, final Collection<ExchangeRate> rates) {
		if( ! StringUtils.hasText(portfolioId)) {
			rates.addAll(exchangeRateService.exchangeRates());
			return Optional.empty();
		} 
		//final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(exchangeRatesAO.getPortfolioId());
		final SharePortfolioRetrospective  sharePortfolioRetrospective = sharePortfolioService.retrospective(portfolioId);
		rates.addAll(exchangeRateService.exchangeRates(sharePortfolioRetrospective.committedSharePortfolio().exchangeRateTranslations()));
		return Optional.of(sharePortfolioRetrospective.initialRateWithExchangeRate().date());
		
		
	}


	
	
	

	private ChartSeries series(final ExchangeRate exchangeRate, final Date startDate) {
		final ExchangeRateRetrospective exchangeRateRetrospective = exchangeRateRetrospectiveBuilder().withName(currencyConverter.convert(exchangeRate.source()) + " - " + currencyConverter.convert(exchangeRate.target())).withStartDate(startDate).withExchangeRates(exchangeRate.rates()).build();
		final LineChartSeries series =  new LineChartSeries(exchangeRateRetrospective.name());
		series.setShowMarker(false);
		exchangeRateRetrospective.exchangeRates().forEach(data -> series.set(data.date().getTime(), Double.valueOf(data.value())));
		return series;
	}
	
	public String show() {
		final ExchangeRatesAO exchangeRatesAO = exchangeRatesAO();
		if(exchangeRatesAO.getPortfolioId()!=null) {
			return String.format(REDIRECT_PATTERN_PORTFOLIO,exchangeRatesAO().getPortfolioId(), exchangeRatesAO().getFilter(), exchangeRatesAO().getPeriod());
		}
		return String.format(REDIRECT_PATTERN, exchangeRatesAO().getFilter(), exchangeRatesAO().getPeriod());
	}
	
	
	
	
	@Lookup
	abstract ExchangeRatesAO exchangeRatesAO(); 
	
	@Lookup
	abstract ExchangeRateRetrospectiveBuilder exchangeRateRetrospectiveBuilder();
	
}
