package de.mq.portfolio.share.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;
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
	
	/**
	 * Stone age (JSF)  ...
	 */
	private static final long serialVersionUID = 1L;
	private final LineChartModel chartModel = new LineChartModel();
	final DateAxis axis = new DateAxis("t");
	static final String TICKFORMAT = "%b %#d, %y";
	static final String LEGEGEND_POSITION = "e";
	
	private String filter = ".*";
	
	private final Collection<SelectItem> curves = new ArrayList<>();
	
	

	public ExchangeRatesAO() {
		
		
		axis.setTickFormat(TICKFORMAT);
		chartModel.getAxes().put(AxisType.X, axis);
		chartModel.setLegendPosition(LEGEGEND_POSITION);
		chartModel.setLegendPlacement(LegendPlacement.OUTSIDEGRID);
		
		
		chartModel.getAxis(AxisType.Y).setLabel("Wechselkurs");
		
	
		
	
	}
	
	
	
	public final void assign(final Collection<Entry<String, ChartSeries>> charts) {
		chartModel.clear();
		curves.clear();
		
		charts.stream().filter(e -> e.getKey().matches(filter)).map(e -> e.getValue()).forEach(s -> chartModel.addSeries(s));
		
		addDummyIfEmpty();
		
	
		curves.addAll(charts.stream().map(e -> new SelectItem(e.getKey(), e.getValue().getLabel())).collect(Collectors.toList()));
	}

	private void addDummyIfEmpty() {
		if(chartModel.getSeries().stream().mapToInt(s -> s.getData().size()).sum() == 0 ) {
			final LineChartSeries empty = new LineChartSeries(" ");
			empty.set(new Date().getTime(), 0);
		    chartModel.addSeries(empty);
			
		}
	}


	
	public ChartModel getChartModel() {
		return chartModel;
	}
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(final String filter) {
		this.filter = filter;
	}
	
	public Collection<SelectItem> getCurves() {
		return curves;
	}

}
