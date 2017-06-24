package de.mq.portfolio.share;

import java.util.HashMap;
import java.util.Map;

public interface Share {
	
	String name();

	String index();

	String code();

	boolean isIndex();
	
	String wkn();

	String currency();
	
	String code2();
	
	default Map<String,String> gatewayParameter() {
		return new HashMap<>();
	}

}