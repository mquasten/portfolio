
package de.mq.portfolio.gateway.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

public class GatewayParameterCSVLineConverterImpl implements Converter<String[], GatewayParameter> {

	final int columnsCount = 4;

	public GatewayParameterCSVLineConverterImpl() {

	}

	@Override
	public final GatewayParameter convert(String[] cols) {
		Assert.notNull(cols, "Columns is mandatory.");
		Assert.isTrue(cols.length == columnsCount, String.format("A line should have %s columns.", columnsCount));

		return new GatewayParameterImpl(cols[0], Gateway.valueOf(cols[1]), cols[2], cols[3]);
	}

}
