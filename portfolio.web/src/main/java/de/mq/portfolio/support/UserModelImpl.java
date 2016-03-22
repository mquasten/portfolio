package de.mq.portfolio.support;

public class UserModelImpl implements UserModel {
	
	private String name;
	
	private String portfolioId;
	
	

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


	


	

	

	

}
