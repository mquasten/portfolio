package de.mq.portfolio.share;

public interface Share {
	
	public enum StockExchange {
		ETR,
		FRA, 
		NYSE;
		
	}

	String name();

	String index();

	String code();

	boolean isIndex();
	
	String wkn();

	String currency();
	
	String code2();

}