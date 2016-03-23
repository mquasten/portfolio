package de.mq.portfolio.support;

import java.util.Locale;

public class UserModelImpl implements UserModel {
	
	private String name;
	
	private String portfolioId;
	
	private Locale locale = Locale.GERMAN;


	public UserModelImpl(String name) {
		this.name=name;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.support.UserModel#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	

	/* (non-Javadoc)
	 * @see de.mq.portfolio.support.UserModel#getPortfolioId()
	 */
	@Override
	public String getPortfolioId() {
		return portfolioId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.support.UserModel#setPortfolioId(java.lang.String)
	 */
	@Override
	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.support.UserModel#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.support.UserModel#setLocale(java.util.Locale)
	 */
	@Override
	public void setLocale(final Locale locale) {
		this.locale=locale;
	}

	

	

	

}
