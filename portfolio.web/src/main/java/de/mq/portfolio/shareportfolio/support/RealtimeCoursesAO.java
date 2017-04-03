package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.TimeCourse;

@Component("realtimeCourses")
@Scope("view")
public class RealtimeCoursesAO  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Collection<Map<String,Object>> shares= new ArrayList<>(); 
	
	 private final Map<String, Double> factors = new HashMap<>();

	
	private final List<String> shareColumns = Arrays.asList("name", "delta", "deltaPercent");
	

	public Collection<String> getShareColumns() {
		return shareColumns;
	}

	public Collection<Map<String, Object>> getShares() {
		return shares;
	}

	
	private String portfolioId;
	
	private String filter;

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}
	
	void assign(final List<Entry<TimeCourse,List<Data>>> entries) {
		shares.clear();
		shares.addAll(entries.stream().map(entry ->  shareEntryToMap(entry)).collect(Collectors.toList()));
	}
	
	void setFactors(final Map<String, Double> factors ) {
		this.factors.clear();
		this.factors.putAll(factors);
	}

	private Map<String, Object> shareEntryToMap(final Entry<TimeCourse, List<Data>> entry) {
		final Map<String,Object> values = new HashMap<>();
		values.put(shareColumns.get(0), entry.getKey().name() +" (" + entry.getKey().code() +")");
		values.put(shareColumns.get(1),   entry.getValue().get(1).value() - (Double) entry.getValue().get(0).value() );
		values.put(shareColumns.get(2),   (entry.getValue().get(1).value() - (Double) entry.getValue().get(0).value()) / entry.getValue().get(0).value() );
		return values;
	}
	


}
