package de.mq.portfolio.support;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.support.ExchangeRateImpl;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;


class GatewayParameter2ExchangeRateConverterImpl implements Converter<GatewayParameter, ExchangeRate> {

	@Override
	public ExchangeRate convert(final GatewayParameter gatewayParameter) {
		final List<String> ids = Gateway.ids(gatewayParameter.gateway().id(gatewayParameter.code()));
		Assert.isTrue(ids.size() == 3, "Ids should habe 3 parts.");
		
		return new ExchangeRateImpl(ids.get(0), ids.get(1));
	}

}
