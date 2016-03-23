package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

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
	private final List<Entry<String,Double>> standardDeviations  = new ArrayList<>();;
	
	
	

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

}
