package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("retrospective")
@Scope("view")
public class RetrospectiveAO implements Serializable {

 
	private static final long serialVersionUID = 1L;

	private String portfolioId;

	private final  LineChartModel chartModel =  new LineChartModel();
	private final   DateAxis axis = new DateAxis("t");
	
	public RetrospectiveAO() {
		axis.setTickFormat("%b %#d, %y");
		chartModel.getAxes().put(AxisType.X, axis);
		
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
	
	public void assign(final LineChartSeries lineChartSeries) {
		chartModel.clear();
		chartModel.addSeries(lineChartSeries);
		
	}
	
	
	public final void setOrdinateTitle(final String ordinateTitle) {
		chartModel.getAxis(AxisType.Y).setLabel(ordinateTitle);
	}

	
	public final void setTitle(final String title) {
		chartModel.setTitle(title);
	}
}
