package de.mq.portfolio.gateway.support;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

public class GatewayParameterArivaDividendHistoryCSVLineConverterTest {

	private static final String SHARE_NAME_PARAM = "shareName";
	private static final String SHARE_NAME = "coca-cola-aktie";
	private static final String URL = "http://www.ariva.de/{shareName}/historische_ereignisse";
	private static final String CODE = "KO";
	final Converter<String[], GatewayParameter> converter = new GatewayParameterArivaDividendHistoryCSVLineConverterImpl();

	@Test
	public final void converter() {
		final String[] columns = new String[] { CODE, Gateway.ArivaDividendHistory.name(), URL, SHARE_NAME };
		final GatewayParameter gatewayParameter = converter.convert(columns);

		Assert.assertEquals(CODE, gatewayParameter.code());
		Assert.assertEquals(Gateway.ArivaDividendHistory, gatewayParameter.gateway());
		Assert.assertEquals(URL, gatewayParameter.urlTemplate());

		Assert.assertTrue(gatewayParameter.parameters().containsKey(SHARE_NAME_PARAM));
		Assert.assertEquals(SHARE_NAME, gatewayParameter.parameters().get(SHARE_NAME_PARAM));

	}

	@Test(expected = IllegalArgumentException.class)
	public final void convertWrongNumberOfColumns() {
		converter.convert(new String[] {});
	}

}
