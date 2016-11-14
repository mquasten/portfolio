package de.mq.portfolio.exchangerate.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("exchangeRates")
@Scope("view")
public class ExchangeRatesAO implements Serializable {

	static final String ORDINATE_TITLE = "Wechselkurs";

	static final String DATE_AXIS_TITLE = "t";

	static final String DEFAULT_FILTER = ".*";

	static final int PERIOD_FOREVER = 99999;
	/**
	 * Stone age (JSF) ...
	 */
	private static final long serialVersionUID = 1L;
	private final LineChartModel chartModel = new LineChartModel();
	final DateAxis axis = new DateAxis(DATE_AXIS_TITLE);
	static final String TICKFORMAT = "%b %#d, %y";
	static final String LEGEGEND_POSITION = "e";

	private String filter = DEFAULT_FILTER;

	private Integer period = PERIOD_FOREVER;

	private final Collection<SelectItem> curves = new ArrayList<>();

	final private Collection<ExchangeRateRetrospective> exchangeRateRetrospectives = new ArrayList<>();

	private String portfolioId;

	private String portfolioName;

	public ExchangeRatesAO() {

		axis.setTickFormat(TICKFORMAT);
		chartModel.getAxes().put(AxisType.X, axis);
		chartModel.setLegendPosition(LEGEGEND_POSITION);
		chartModel.setLegendPlacement(LegendPlacement.OUTSIDEGRID);

		chartModel.getAxis(AxisType.Y).setLabel(ORDINATE_TITLE);

	}

	public  void assign(final Collection<Entry<String, ChartSeries>> charts) {
		
	
		chartModel.clear();
		curves.clear();

		charts.stream().filter(e -> e.getKey().matches(filter)).map(e -> e.getValue()).forEach(s -> chartModel.addSeries(s));

		addDummyIfEmpty();

		curves.addAll(charts.stream().map(e -> new SelectItem(e.getKey(), e.getValue().getLabel())).collect(Collectors.toList()));
	}

	private void addDummyIfEmpty() {
		if (chartModel.getSeries().stream().mapToInt(s -> s.getData().size()).sum() == 0) {
			chartModel.addSeries(dummyEntry());
		}
	}

	private LineChartSeries dummyEntry() {
		final LineChartSeries empty = new LineChartSeries(" ");
		empty.set(new Date().getTime(), 0);
		return empty;
	}

	public ChartModel getChartModel() {
		return chartModel;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(final String filter) {
		try {
			Pattern.compile(filter);
			this.filter = filter;
		} catch (PatternSyntaxException pse) {
			this.filter = DEFAULT_FILTER;
		}
	}

	public Collection<SelectItem> getCurves() {
		return curves;
	}

	public String getPeriod() {
		return "" + period;
	}

	public void setPeriod(final String period) {
		try {
			this.period = Integer.parseInt(period);
		} catch (final NumberFormatException nf) {
			this.period = PERIOD_FOREVER;
		}
	}

	public String getPortfolioName() {
		return portfolioName;
	}

	void setPortfolioName(final String portfolioName) {
		this.portfolioName = portfolioName;
	}

	int period() {
		return period;
	}

	public Collection<ExchangeRateRetrospective> getExchangeRateRetrospectives() {
		return exchangeRateRetrospectives;
	}

	void setExchangeRateRetrospectives(final Collection<ExchangeRateRetrospective> exchangeRateRetrospectives) {
		this.exchangeRateRetrospectives.clear();
		this.exchangeRateRetrospectives.addAll(exchangeRateRetrospectives);
	}

	public String getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}

}
