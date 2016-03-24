package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	private final List<Entry<String,Double>> standardDeviations  = new ArrayList<>();
	
	private final List<Entry<String,Map<String,Double>>> correlations  = new ArrayList<>();;
	
	private final List<String> shares = new ArrayList<>();
	
	private final List<Entry<String, Double>> weights = new ArrayList<>();
	
	private Double 	minStandardDeviation;
	
	private Double uniformlyDistributedStandardDeviation; 
	
	

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	} 
	
	
	public final void setSharePortfolio(final SharePortfolio sharePortfolio) {
		this.name=sharePortfolio.name();
		this.timeCourses.clear();
		timeCourses.addAll(sharePortfolio.timeCourses());
		this.standardDeviations.clear();
		this.standardDeviations.addAll(sharePortfolio.standardDeviations());
		this.correlations.clear();
		this.correlations.addAll(sharePortfolio.correlationEntries());
		this.shares.clear();
		this.shares.addAll(sharePortfolio.timeCourses().stream().map(tc -> tc.share().name()).collect(Collectors.toList()));
		this.weights.clear();
		this.weights.addAll(sharePortfolio.min());
		if (this.timeCourses.size() < 2) {
			return;
		}
		final double[] weightingVector = new double[timeCourses.size()]; 
		IntStream.range(0, weightingVector.length).forEach(i -> weightingVector[i] = weights.get(i).getValue());
		this.minStandardDeviation = Math.sqrt(sharePortfolio.risk(weightingVector));

		IntStream.range(0, weightingVector.length).forEach(i -> weightingVector[i] = 1d / timeCourses.size());
		this.uniformlyDistributedStandardDeviation = Math.sqrt(sharePortfolio.risk(weightingVector));
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
	
	public List<Entry<String, Double>> getStandardDeviations() {
		return standardDeviations;
	}
	
	public List<Entry<String, Map<String, Double>>> getCorrelations() {
		return correlations;
	}
	
	public List<String> getShares() {
		return shares;
	}
	
	public List<Entry<String, Double>> getWeights() {
		return weights;
	}
	
	public Double getMinStandardDeviation() {
		return minStandardDeviation;
	}


	public Double getUniformlyDistributedStandardDeviation() {
		return uniformlyDistributedStandardDeviation;
	}

}
