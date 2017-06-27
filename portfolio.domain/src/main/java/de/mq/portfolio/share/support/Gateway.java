package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.util.Assert;

public enum Gateway {
	
	ArivaRateHistory("ARH");
	
	
	private final String id;
	Gateway(final String id) {
		this.id=id;
	}
	
	String id() {
		return id;
	}
	
	static Gateway forId(final String id) {
		Assert.hasText("id" , "Id is mandatory.");
		return DataAccessUtils.requiredSingleResult(Arrays.asList(Gateway.values()).stream().filter(value -> value.id().equals(id)).collect(Collectors.toSet()));
		
	}

}
