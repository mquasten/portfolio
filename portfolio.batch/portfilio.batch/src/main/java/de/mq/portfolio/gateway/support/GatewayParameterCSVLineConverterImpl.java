
package de.mq.portfolio.gateway.support;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;


public class GatewayParameterCSVLineConverterImpl implements Converter<String[], GatewayParameter> {

	final int fixedColumns = 3;
	private  final List<String> parameters = new ArrayList<>(); 
	
	public GatewayParameterCSVLineConverterImpl(final String ... parameters) {
		this.parameters.addAll(Arrays.asList(parameters));
	}

	@Override
	public final GatewayParameter convert(String[] cols) {
		Assert.notNull(cols, "Columns is mandatory.");
		
		Assert.isTrue(cols.length == fixedColumns+parameters.size() , String.format("A line should have %s columns.", fixedColumns + parameters.size()));
		
		final Map<String,String> gatewayParameters = new HashMap<>();
		
		IntStream.range(0, parameters.size()).forEach(i -> gatewayParameters.put(parameters.get(i), cols[fixedColumns+i]));
		
		
		return new GatewayParameterImpl(cols[0], Gateway.valueOf(cols[1]), cols[2], gatewayParameters);
	}
	
}
