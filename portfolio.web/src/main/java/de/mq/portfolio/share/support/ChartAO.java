package de.mq.portfolio.share.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;

@Component("chart")
@Scope("view")
public class ChartAO implements Serializable {

	static final String LABEL_TIME = "t";
	
	static final String TICK_FORMAT = "%b %#d, %y";

	private static final long serialVersionUID = 1L;

	private String name; 

	private String code;

	private String wkn;

	private String currency;
	
	
	private String index;
	
	private Double current;
	
	private Double last;
	
	private List<Data> dividends = new ArrayList<>();

	private final LineChartModel chartModel = new LineChartModel();

	private final DateAxis axis = new DateAxis(LABEL_TIME);
	
	private final List<GatewayParameter> gatewayParameters = new ArrayList<>();

	private final Map<Gateway,Date> updated = new HashMap<>(); 
	
	private String message; 
	
	
	public ChartAO() {
		axis.setTickFormat(TICK_FORMAT);
		chartModel.getAxes().put(AxisType.X, axis);
		chartModel.setLegendPlacement(LegendPlacement.OUTSIDEGRID);

		

	}

	public void assign(final Collection<LineChartSeries> ratesSeries) {
		chartModel.clear(); 
		ratesSeries.forEach(rs -> chartModel.addSeries(rs));
	}

	public List<Data> getDividends() {
		return dividends;
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



	public String getCurrency() {
		return currency;
	}

	

	public LineChartModel getChartModel() {
		return chartModel;
	}
	
	public String getName() {
		return name;
	}

	
	
	
	
	public String getIndex() {
		return index;
	}

	
	
	public Double getCurrent() {
		return current;
	}

	void setRealTimeRates(final List<Data> rates) {
		
		last=null;
		current=null;
		
		if( rates.size() != 2 ) {
			return ;
		}
		
		
		last=rates.get(0).value();
		current=rates.get(1).value();
	}
	
	
	void setTimeCourse(final TimeCourse timeCourse) {
		name = timeCourse.share().name();
		dividends = timeCourse.dividends();
		wkn = timeCourse.share().wkn();
		currency = timeCourse.share().currency();
		index = timeCourse.share().index();
		updated.clear();
		updated.putAll(timeCourse.updates().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
	}
	
	void setGatewayParameters(final Collection<GatewayParameter> gatewayParameters) {
		this.gatewayParameters.clear();
		this.gatewayParameters.addAll(gatewayParameters);
	}
	
	
	public final Date lastUpdate(final Gateway gateway) {
		return updated.get(gateway);
	}
	
	
	public List<GatewayParameter> getGatewayParameters() {
		return gatewayParameters;
	}

	public Double getLast() {
		return last;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	public boolean isRealTimeRateValid() {
		return last != null && current != null; 
	}
	
	
}
