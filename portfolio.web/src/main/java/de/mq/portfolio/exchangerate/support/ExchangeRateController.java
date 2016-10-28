package de.mq.portfolio.exchangerate.support;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
import de.mq.portfolio.share.Data;

@Component("exchangeRateController")
@Scope("singleton")
public abstract class ExchangeRateController {
	
	
	private Optional<Date> startDate = Optional.empty();

	private final ExchangeRateService exchangeRateService;
	
	private final Converter<String, String> currencyConverter;
	
	private final Collection<ExchangeRateRetrospective> exchangeRateRetrospectives = new ArrayList<>();
	
	static final String REDIRECT_PATTERN = "exchangeRates?filter=%s&period=%s&faces-redirect=true";
	
	@Autowired
	ExchangeRateController(final ExchangeRateService exchangeRateService, @Qualifier("currencyConverter") final Converter<String, String> currencyConverter) {
		this.exchangeRateService = exchangeRateService;
		this.currencyConverter=currencyConverter;
	}


	public void init() {
		final ExchangeRatesAO exchangeRatesAO = exchangeRatesAO();
		final Date startDate =  Date.from(LocalDateTime.now().minusDays(exchangeRatesAO.period()).truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant());
		
		final Collection<ExchangeRate> rates = exchangeRateService.exchangeRates();
		final Date initialDate = this.startDate.orElse(startDate);
		final Collection<ExchangeRateRetrospective> exchangeRateRetrospectives =
		rates.stream().map(rate -> { 
			//:TODO extract Builder
			
			final Collection<Data> ratesSince = rate.rates().stream().filter(data -> ! data.date().before(initialDate)).collect(Collectors.toList());
			
			final Comparator<Data> comparator = (d1,d2)-> (int)  Math.signum((double)(d1.date().getTime()- d2.date().getTime()));
			final Optional<Data> start = ratesSince.stream().min(comparator); 
			
			final Optional<Data> max = ratesSince.stream().max(comparator); 
		
			
			if( start.isPresent() && max.isPresent()) {
				
				return new ExchangeRateRetrospectiveImpl(rate.source() +":" + rate.target(), start.get().date(), max.get().date(),  start.get().value(), max.get().value());
			}
			
			
			return  new ExchangeRateRetrospectiveImpl(rate.source() +":" + rate.target());
			
		}).collect(Collectors.toList());
		
		exchangeRateRetrospectives.forEach(r -> System.out.println(r.startDate() + "," + r.endDate() + "," + r.rate()));
		
		assign(exchangeRateRetrospectives);
		
		exchangeRatesAO.assign(rates.stream().map(exchangeRate -> new AbstractMap.SimpleImmutableEntry<>( exchangeRate.source() + "-" + exchangeRate.target(), series(exchangeRate, startDate))).collect(Collectors.toList()));
	}


	private void assign(final Collection<ExchangeRateRetrospective> exchangeRateRetrospectives) {
		this.exchangeRateRetrospectives.clear();
		this.exchangeRateRetrospectives.addAll(exchangeRateRetrospectives);
	}
	
	

	private ChartSeries series(final ExchangeRate exchangeRate, final Date startDate) {
		
		final String title = currencyConverter.convert(exchangeRate.source()) + " - " + currencyConverter.convert(exchangeRate.target());
		final LineChartSeries series =  new LineChartSeries(title);
		series.setShowMarker(false);
		exchangeRate.rates().stream().filter(data -> !data.date().before(startDate) ).forEach(data -> series.set(data.date().getTime(), Double.valueOf(data.value())));
		return series;
	}
	
	public String show() {
		return String.format(REDIRECT_PATTERN, exchangeRatesAO().getFilter(), exchangeRatesAO().getPeriod());
	}
	
	
	void setStartDate(final Date startDate) {
		if( startDate != null) {
			this.startDate=Optional.of(startDate);
		}
	}
	
	@Lookup
	abstract ExchangeRatesAO exchangeRatesAO(); 
	
}
