package de.mq.portfolio.shareportfolio.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.primefaces.model.chart.LineChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Data;
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
		final Collection<Data> result = sharePortfolioService.retrospective(retrospectiveAO.getPortfolioId());
		retrospectiveAO.setOrdinateTitle(String.format("Wert Anteil / %s" , currencyConverter.convert(sharePortfolio.currency())));
		retrospectiveAO.setTitle(sharePortfolio.name());
		final LineChartSeries series = new LineChartSeries();
		series.setShowMarker(false);
       
        result.forEach(data -> series.set( df.format(data.date()), Double.valueOf(data.value()) ));
        retrospectiveAO.assign(series);
	}

}
