package de.mq.portfolio.share;

public interface Share {
	
	public enum StockExchange {
		ETR,
		FRA, 
		NYSE;
		
	}

	public abstract String name();

	public abstract String index();

	public abstract String code();

	public abstract boolean isIndex();
	
	StockExchange stockExchange();

	String wkn();

	String currency();
	
	

}