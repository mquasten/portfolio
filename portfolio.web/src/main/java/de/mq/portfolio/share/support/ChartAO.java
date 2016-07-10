package de.mq.portfolio.share.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Data;

@Component("chart")
@Scope("view")
public class ChartAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	private String name; 

	private String code;

	private String wkn;

	private String currency;
	
	

	private List<Data> dividends = new ArrayList<>();

	private final LineChartModel chartModel = new LineChartModel();

	private final DateAxis axis = new DateAxis("t");

	public ChartAO() {
		axis.setTickFormat("%b %#d, %y");
		chartModel.getAxes().put(AxisType.X, axis);
		

	}

	public void assign(final Collection<LineChartSeries> ratesSeries) {
		chartModel.clear(); 
		ratesSeries.forEach(rs -> chartModel.addSeries(rs));
	}

	

	public List<Data> getDividends() {
		return dividends;
	}

	public void setDividends(final List<Data> dividends) {
		this.dividends = dividends;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	public String getWkn() {
		return wkn;
	}

	public void setWkn(String wkn) {
		this.wkn = wkn;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public LineChartModel getChartModel() {
		return chartModel;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
