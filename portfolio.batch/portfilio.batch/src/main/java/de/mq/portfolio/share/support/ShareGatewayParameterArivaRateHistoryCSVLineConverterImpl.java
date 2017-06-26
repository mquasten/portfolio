package de.mq.portfolio.share.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;


import de.mq.portfolio.share.ShareGatewayParameter;

public class ShareGatewayParameterArivaRateHistoryCSVLineConverterImpl implements Converter<String[], ShareGatewayParameter> {

	@Override
	public final ShareGatewayParameter convert(String[] cols) {
		Assert.notNull(cols, "Columns is mandatory.");
		Assert.isTrue(cols.length == 4 , "A Line should have 4 columns.");
		
		final Map<String,String> gatewayParameters = new HashMap<>();
		gatewayParameters.put("shareId", cols[2]);
		gatewayParameters.put("stockExchangeId", cols[3]);
		
		return new ShareGatewayParameterImpl(cols[0], Gateway.valueOf(cols[1]), gatewayParameters);
	}

}
