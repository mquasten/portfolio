package de.mq.portfolio.share.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.primefaces.model.chart.LineChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;

@Component("chartController")
@Scope("singleton")
public class ChartControllerImpl {

	private final ShareService shareService;

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
		chartAO.setName(timeCourse.get().share().name());
		chartAO.setDividends(timeCourse.get().dividends());
		chartAO.setWkn(timeCourse.get().share().wkn());
		chartAO.setCurrency(timeCourse.get().share().currency());
		chartAO.setCode2(timeCourse.get().share().code2());
		chartAO.setIndex(timeCourse.get().share().index());
		
		final Collection<LineChartSeries> ratesSeries = new ArrayList<>();

		final LineChartSeries series = new LineChartSeries();

		timeCourse.get().rates().forEach(data -> series.set(df.format(data.date()), Double.valueOf(data.value())));
		ratesSeries.add(series);
		series.setShowMarker(false);
		series.setLabel(timeCourse.get().share().name().replaceAll("'", " "));
		chartAO.assign(ratesSeries);

	}

}
