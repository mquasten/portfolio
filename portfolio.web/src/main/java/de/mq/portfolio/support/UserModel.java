package de.mq.portfolio.support;

import java.util.Locale;

public interface UserModel {

	String getName();

	String getPortfolioId();

	void setPortfolioId(final String portfolioId);

	Locale getLocale();
	
	void setLocale(Locale locale);
	
	void assign(final String view, final String state);
	
	String state(final String view);


	
	
}