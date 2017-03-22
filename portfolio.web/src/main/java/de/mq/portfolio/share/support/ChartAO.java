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
import org.springframework.util.StringUtils;

import de.mq.portfolio.share.Data;

@Component("chart")
@Scope("view")
public class ChartAO implements Serializable {

	static final String LABEL_TIME = "t";



	static final String TICK_FORMAT = "%b %#d, %y";



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	private String name; 

	private String code;

	private String wkn;

	private String currency;
	
	private String code2;
	
	private String index;
	
	private Double current;
	
	private Double last;

	
	private List<Data> dividends = new ArrayList<>();

	private final LineChartModel chartModel = new LineChartModel();

	private final DateAxis axis = new DateAxis(LABEL_TIME);

	public ChartAO() {
		axis.setTickFormat(TICK_FORMAT);
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

	public String getCode2() {
		return code2;
	}

	public void setCode2(String code2) {
		this.code2 = code2;
	}

	
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	
	public Double getCurrent() {
		return current;
	}

	public void setRealTimeCourses(final List<Data> rates) {
		last=null;
		current=null;
		if( rates.size() != 2 ) {
			return ;
		}
		last=rates.get(0).value();
		current=rates.get(1).value();
	}

	public Double getLast() {
		return last;
	}


	public boolean isRealTimeRateValid() {
		return last != null && current != null; 
	}
	
	public boolean isShare() {
		return StringUtils.hasText(index); 
	}
}
