package de.mq.portfolio.share.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

@Component("gateways")
@Scope("view")
public class GatewaysAO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String code;
	private String message;
	
	private String portfolioId; 

	private final List<GatewayParameter> gatewayParameters = new ArrayList<>();
	private final Map<Gateway, Date> updated = new HashMap<>();

	public String getMessage() {
		return message;
	}

	void setMessage(String message) {
		this.message = message;
	}

	public final Date lastUpdate(final Gateway gateway) {
		return updated.get(gateway);
	}

	public void assign(Collection<Entry<Gateway, Date>> updates) {

		this.updated.putAll(updates.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
	}

	public List<GatewayParameter> getGatewayParameters() {
		return gatewayParameters;
	}

	void setGatewayParameters(Collection<GatewayParameter> gatewayParameters) {
		this.gatewayParameters.clear();
		this.gatewayParameters.addAll(gatewayParameters);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	boolean isExchangeRate() {
		if( ! StringUtils.hasText(code)) {
			return false;
		}
		return code.contains("USD");
	}
	
	boolean isPortfolio() {
		return StringUtils.hasText(portfolioId);
	}
	
	public String getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}
	
}
