package de.mq.portfolio.exchangerate.support;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
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

import de.mq.portfolio.exchangerate.ExchangeRate;


@Component("exchangeRateController")
@Scope("singleton")
public abstract class ExchangeRateController {
	
	
	//private Optional<Date> startDate = Optional.empty();

	private final ExchangeRateService exchangeRateService;
	
	private final Converter<String, String> currencyConverter;
	
	
	
	static final String REDIRECT_PATTERN = "exchangeRates?filter=%s&period=%s&faces-redirect=true";
	
	@Autowired
	ExchangeRateController(final ExchangeRateService exchangeRateService, @Qualifier("currencyConverter") final Converter<String, String> currencyConverter) {
		this.exchangeRateService = exchangeRateService;
		this.currencyConverter=currencyConverter;
	}


	public void init() {
			
		final ExchangeRatesAO exchangeRatesAO = exchangeRatesAO();
		
		final Optional<Date> portfolioDate = Optional.empty();
		final Date startDate = Date.from(LocalDateTime.now().minusDays(exchangeRatesAO.period()).truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant());
		
		
		
		
		
		final Collection<ExchangeRate> rates = exchangeRateService.exchangeRates();
		exchangeRatesAO.setExchangeRateRetrospectives(rates.stream().map(rate -> exchangeRateRetrospectiveBuilder().withName(currencyConverter.convert(rate.source()) + "-" +currencyConverter.convert(rate.target())).withStartDate(portfolioDate.orElse(startDate)).withExchangeRates(rate.rates()).build()).collect(Collectors.toList()));
		
		exchangeRatesAO.assign(rates.stream().map(exchangeRate -> new AbstractMap.SimpleImmutableEntry<>( exchangeRate.source() + "-" + exchangeRate.target(), series(exchangeRate, Collections.max(Arrays.asList(portfolioDate.orElse(startDate),startDate))))).collect(Collectors.toList()));
	}


	
	
	

	private ChartSeries series(final ExchangeRate exchangeRate, final Date startDate) {
		final ExchangeRateRetrospective exchangeRateRetrospective = exchangeRateRetrospectiveBuilder().withName(currencyConverter.convert(exchangeRate.source()) + " - " + currencyConverter.convert(exchangeRate.target())).withStartDate(startDate).withExchangeRates(exchangeRate.rates()).build();
		final LineChartSeries series =  new LineChartSeries(exchangeRateRetrospective.name());
		series.setShowMarker(false);
		exchangeRateRetrospective.exchangeRates().forEach(data -> series.set(data.date().getTime(), Double.valueOf(data.value())));
		return series;
	}
	
	public String show() {
		return String.format(REDIRECT_PATTERN, exchangeRatesAO().getFilter(), exchangeRatesAO().getPeriod());
	}
	
	
	
	
	@Lookup
	abstract ExchangeRatesAO exchangeRatesAO(); 
	
	@Lookup
	abstract ExchangeRateRetrospectiveBuilder exchangeRateRetrospectiveBuilder();
	
}
