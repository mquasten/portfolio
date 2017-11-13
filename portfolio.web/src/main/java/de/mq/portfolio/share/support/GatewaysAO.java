package de.mq.portfolio.share.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.gateway.GatewayParameter;

@Component("gateways")
@Scope("view")
public class GatewaysAO implements Serializable{

	private static final long serialVersionUID = 1L;
	private String code;
	
	private final List<GatewayParameter> gatewayParameters = new ArrayList<>();

	public List<GatewayParameter> getGatewayParameters() {
		return gatewayParameters;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
