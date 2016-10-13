package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.LongStream;

import javax.faces.model.SelectItem;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;

@Component("retrospective")
@Scope("view")
public class RetrospectiveAO implements Serializable {

	static final String DEFAULT_FILTER = ".*";

	static final String TICKFORMAT = "%b %#d, %y";

	static final String LEGEGEND_POSITION = "e";

	private static final long serialVersionUID = 1L;

	private String portfolioId;

	private final LineChartModel chartModel = new LineChartModel();
	final DateAxis axis = new DateAxis("t");

	private final Collection<SelectItem> curves = new ArrayList<>();

	private final Collection<TimeCourseRetrospective> timeCourseRetrospectives = new ArrayList<>();

	private String currency = "EURxx";

	private double standardDeviation = 0d;

	private Double totalRate = 0d;

	private Double totalRateDividends = 0d;

	private Date startDate;

	private Date endDate;

	private String filter = DEFAULT_FILTER;

	private final PortfolioAO committedPortfolio = new PortfolioAO();

	private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	private final PortfolioAO currentPortfolio = new PortfolioAO();

	public RetrospectiveAO() {
		axis.setTickFormat(TICKFORMAT);
		chartModel.getAxes().put(AxisType.X, axis);
		chartModel.setLegendPosition(LEGEGEND_POSITION);
		chartModel.setLegendPlacement(LegendPlacement.OUTSIDEGRID);

	}

	public ChartModel getChartModel() {
		return chartModel;
	}

	public String getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}

	void assign(final Collection<LineChartSeries> ratesSeries) {
		chartModel.clear();
		curves.clear();
		ratesSeries.forEach(rs -> {
			if (rs.getLabel().matches(filter)) {
				chartModel.addSeries(rs);
			}
			curves.add(new SelectItem(rs.getLabel().replaceAll("[&?=]", "."), rs.getLabel()));

		});

	}

	void assign(final SharePortfolioRetrospective aggregation, final Converter<String, String> currencyConverter, final Optional<ExchangeRateCalculator> exchangeRateCalculator) {

		committedPortfolio.setSharePortfolio(aggregation.committedSharePortfolio(), exchangeRateCalculator);
		currentPortfolio.setSharePortfolio(aggregation.currentSharePortfolio(), exchangeRateCalculator);

		this.currency = aggregation.committedSharePortfolio().currency();

		chartModel.getAxis(AxisType.Y).setLabel(String.format("Wert Anteil / %s", currencyConverter.convert(aggregation.committedSharePortfolio().currency())));
		chartModel.setTitle(aggregation.committedSharePortfolio().name());
		startDate = aggregation.initialRateWithExchangeRate().date();

		endDate = aggregation.endRateWithExchangeRate().date();

		standardDeviation = aggregation.standardDeviation();

		totalRate = aggregation.totalRate();

		totalRateDividends = aggregation.totalRateDividends();

		final Collection<LineChartSeries> ratesSeries = new ArrayList<>();
		setTimeCourseRetrospectives(aggregation.timeCoursesWithExchangeRate());
		aggregation.timeCoursesWithExchangeRate().stream().forEach(tcr -> {
			final LineChartSeries series = new LineChartSeries();
			series.setShowMarker(false);
			tcr.timeCourse().rates().forEach(data -> series.set(df.format(data.date()), Double.valueOf(data.value())));
			series.setLabel(tcr.timeCourse().share().name().replaceAll("'", " "));
			ratesSeries.add(series);
		});

		final LocalDate start = aggregation.initialRateWithExchangeRate().date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		final LineChartSeries startLine = new LineChartSeries();
		startLine.setShowMarker(false);
		startLine.setLabel("Start");

		LongStream.rangeClosed(0, ChronoUnit.DAYS.between(start, aggregation.endRateWithExchangeRate().date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
				.forEach(i -> startLine.set(df.format(Date.from(start.plusDays(i).atStartOfDay(ZoneId.systemDefault()).toInstant())), aggregation.initialRateWithExchangeRate().value()));
		ratesSeries.add(startLine);

		assign(ratesSeries);

	}

	public final void setTitle(final String title) {
		chartModel.setTitle(title);
	}

	public Collection<SelectItem> getCurves() {
		return curves;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(final String filter) {
		this.filter = filter;
	}

	public Collection<TimeCourseRetrospective> getTimeCourseRetrospectives() {
		return timeCourseRetrospectives;
	}

	private void setTimeCourseRetrospectives(Collection<TimeCourseRetrospective> timeCourseRetrospectives) {
		this.timeCourseRetrospectives.clear();
		this.timeCourseRetrospectives.addAll(timeCourseRetrospectives);
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getCurrency() {
		return currency;
	}

	public final PortfolioAO getCommittedPortfolio() {
		return committedPortfolio;
	}

	public PortfolioAO getCurrentPortfolio() {
		return currentPortfolio;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public Double getTotalRate() {
		return totalRate;
	}

	public Double getTotalRateDividends() {
		return totalRateDividends;
	}

}
