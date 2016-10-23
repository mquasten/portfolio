package de.mq.portfolio.share.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

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
		
		
		chartModel.addSeries(newChartSeriesMock());	
		
		curves.add(new SelectItem("EUR-US$", "EUR-USD"));
		
	
	}

	private ChartSeries newChartSeriesMock() {
		final ChartSeries x =  new LineChartSeries("EUR-US$");
		x.set(2016-0-01, 1);
		return x;
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
