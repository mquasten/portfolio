package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LegendPlacement;
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
	
	private final Collection<SelectItem> curves = new ArrayList<>();
	
	private String filter=".*"; 
	

	

	public RetrospectiveAO() {
		axis.setTickFormat("%b %#d, %y");
		chartModel.getAxes().put(AxisType.X, axis);
		chartModel.setLegendPosition("e");
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
	
	public void assign(final Collection<LineChartSeries> ratesSeries) {
		chartModel.clear();
		curves.clear();
		ratesSeries.forEach(rs -> {
			if( rs.getLabel().matches(filter)){
				chartModel.addSeries(rs);
			}
			curves.add(new SelectItem(rs.getLabel().replaceAll("[&?=]", "."), rs.getLabel()));
		    
		});
		
	}
	
	
	public final void setOrdinateTitle(final String ordinateTitle) {
		chartModel.getAxis(AxisType.Y).setLabel(ordinateTitle);
	}

	
	public final void setTitle(final String title) {
		chartModel.setTitle(title);
	}
	
	public Collection<SelectItem> getCurves() {
		return curves;
	}
	
	public final String getFilter() {
		return filter;
	}

	public void setFilter(final String filter) {
		this.filter = filter;
	}
}
