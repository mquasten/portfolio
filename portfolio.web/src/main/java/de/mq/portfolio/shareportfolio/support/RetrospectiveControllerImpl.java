package de.mq.portfolio.shareportfolio.support;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.LongStream;

import org.primefaces.model.chart.LineChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("retrospectiveController")
@Scope("singleton")
public class RetrospectiveControllerImpl {
	
	private final SharePortfolioService sharePortfolioService;
	private final Converter<String, String> currencyConverter;
	private final  DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	RetrospectiveControllerImpl(final SharePortfolioService sharePortfolioService, @Qualifier("currencyConverter") final Converter<String, String> currencyConverter) {
		this.sharePortfolioService = sharePortfolioService;
	    this.currencyConverter=currencyConverter;
	}

	public void init(final RetrospectiveAO retrospectiveAO) {
		
		final SharePortfolio sharePortfolio = sharePortfolioService.sharePortfolio(retrospectiveAO.getPortfolioId());
		final SharePortfolioRetrospective sharePortfolioRetrospective = sharePortfolioService.retrospective(retrospectiveAO.getPortfolioId());
		
		
		
		retrospectiveAO.setOrdinateTitle(String.format("Wert Anteil / %s" , currencyConverter.convert(sharePortfolio.currency())));
		retrospectiveAO.setTitle(sharePortfolio.name());
		final Collection<LineChartSeries> ratesSeries = new ArrayList<>();
		sharePortfolioRetrospective.timeCoursesWithExchangeRate().forEach(tc -> {
			final LineChartSeries series = new LineChartSeries();
			series.setShowMarker(false);
			tc.rates().forEach(data -> series.set( df.format(data.date()), Double.valueOf(data.value()) ));
			series.setLabel(tc.share().name());
			ratesSeries.add(series);
		});
		
		
		final LocalDate start = sharePortfolioRetrospective.initialRateWithExchangeRate().date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final LineChartSeries startLine = new LineChartSeries();
        startLine.setShowMarker(false);
        startLine.setLabel("Start");
      
        LongStream.rangeClosed(1, ChronoUnit.DAYS.between(start,sharePortfolioRetrospective.endRateWithExchangeRate().date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())).forEach(i ->  startLine.set(df.format(Date.from(start.plusDays(i).atStartOfDay(ZoneId.systemDefault()).toInstant())), sharePortfolioRetrospective.initialRateWithExchangeRate().value() ));
        ratesSeries.add(startLine);
        retrospectiveAO.assign(ratesSeries);
	}

}
