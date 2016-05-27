package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("sharePortfolio")
@Scope("view")
public class PortfolioAO implements Serializable {
	

	private static final long serialVersionUID = 1L;

	private String name ;
	
	private String id ; 

	private final List<TimeCourse> timeCourses = new ArrayList<>();

	private boolean editable; 



	


	private final List<Entry<String,Map<String,Double>>> correlations  = new ArrayList<>();;
	
	private final List<String> shares = new ArrayList<>();
	
	private final Map<TimeCourse, Double> weights = new HashMap<>();
	
	private Double 	minStandardDeviation;
	
	
	private Double totalRate; 
	
	
	private String currency;



	private Double totalRateDividends; 
	

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	} 
	
	
	public final void setSharePortfolio(final SharePortfolio sharePortfolio) {
		this.name=sharePortfolio.name();
		this.currency=sharePortfolio.currency();
		this.timeCourses.clear();
		timeCourses.addAll(sharePortfolio.timeCourses());
		
		this.correlations.clear();
		this.correlations.addAll(sharePortfolio.correlationEntries());
		this.shares.clear();
		this.shares.addAll(sharePortfolio.timeCourses().stream().map(tc -> tc.share().name()).collect(Collectors.toList()));
		this.weights.clear();
		this.weights.putAll(sharePortfolio.min());
		
		this.editable=!sharePortfolio.isCommitted();
		if (this.timeCourses.size() < 2) {
			
			return;
		}
	
		this.minStandardDeviation = sharePortfolio.standardDeviation();
				

		this.totalRate=sharePortfolio.totalRate();
			
		this.totalRateDividends=sharePortfolio.totalRateDividends();
	
	}
	
	public final SharePortfolio getSharePortfolio() {
		final SharePortfolio result = new SharePortfolioImpl(name, timeCourses);
		ReflectionUtils.doWithFields(result.getClass(), field -> { /*"...touched for the very first time."  mdna (like a virgin**/ field.setAccessible(true); ReflectionUtils.setField(field, result, id); },field -> field.isAnnotationPresent(Id.class)); 
		return result;
		
	}
	
	
	public String getId() {
		return id;
	}
	

	public void setId(String id) {
		this.id = id;
	}
	

	
	public List<Entry<String, Map<String, Double>>> getCorrelations() {
		return correlations;
	}
	
	public List<String> getShares() {
		return shares;
	}
	
	public Map<TimeCourse, Double> getWeights() {
		return weights;
	}
	
	public Double getMinStandardDeviation() {
		return minStandardDeviation;
	}



	
	public List<TimeCourse> getTimeCourses() {
		return timeCourses;
	}
	
	public Double getTotalRate() {
		return totalRate;
	}
	

	public Double getTotalRateDividends() {
		return totalRateDividends;
	}
	
	public boolean getEditable() {
		return editable;
	}
	
	public String getCurrency() {
		return currency;
	}

}
