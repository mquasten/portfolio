package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("sharePortfolio")
@Scope("view")
public class PortfolioAO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name ;
	
	private String id ; 

	private final List<TimeCourse> timeCourses = new ArrayList<>();;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	} 
	
	
	public final SharePortfolio getSharePortfolio() {
		final SharePortfolio result = new SharePortfolioImpl(name, timeCourses);
		ReflectionUtils.doWithFields(result.getClass(), field -> {field.setAccessible(true); ReflectionUtils.setField(field, field.getName(), id); },field -> field.isAnnotationPresent(Id.class)); 
		return result;
		
	}
	

}
