package de.mq.portfolio.share.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.primefaces.model.chart.LineChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;

@Component("chartController")
@Scope("singleton")
public  class ChartControllerImpl {

	private final ShareService shareService;
	
	@Autowired
	private  ShareGatewayParameterService shareGatewayParameterService;

	final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	ChartControllerImpl(final ShareService shareService) {
		this.shareService = shareService;
	}

	public void init(final ChartAO chartAO) {

		final Optional<TimeCourse> timeCourse = shareService.timeCourse(chartAO.getCode());
		if (!timeCourse.isPresent()) {
			return;
		}
		chartAO.setTimeCourse(timeCourse.get());
	
		
	
		refresh(chartAO);
	
		
		final Collection<LineChartSeries> ratesSeries = new ArrayList<>();

		final LineChartSeries series = new LineChartSeries();

		timeCourse.get().rates().forEach(data -> series.set(df.format(data.date()), Double.valueOf(data.value())));
		ratesSeries.add(series);
		series.setShowMarker(false);
		series.setLabel(timeCourse.get().share().name().replaceAll("'", " "));
		chartAO.assign(ratesSeries);
		
		
		setGatewayParameters(chartAO, timeCourse);

	}

	private void setGatewayParameters(final ChartAO chartAO, final Optional<TimeCourse> timeCourse) {
		try {
		
			chartAO.setGatewayParameters(shareGatewayParameterService.gatewayParameters(timeCourse.get().share()).gatewayParameters());
		} catch (final Exception ex ) {
			chartAO.setMessage(ex.getMessage());
		}
	}
	
	
	
	public void refresh(final ChartAO chartAO) {
		final Optional<TimeCourse> timeCourse = shareService.realTimeCourses(Arrays.asList(chartAO.getCode()), false).stream().findAny();
		chartAO.setRealTimeRates(Arrays.asList());
		if( ! timeCourse.isPresent()){
			return;
		}
		chartAO.setRealTimeRates(timeCourse.get().rates());
	}
	
	

}
