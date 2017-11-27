package de.mq.portfolio.gateway.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

public class MergedGatewayParameterBuilderTest {
	
	private static final String CODE_JNJ = "JNJ";

	private static final String QUERY_PARAMETER_NAME = "query";

	private static final String URL = "url?s={query}";
	
	final GatewayParameter jnj = prepareForGatewayParameter(CODE_JNJ, "url?s={query }");
	final GatewayParameter pg = prepareForGatewayParameter("PG", "url?s={ query }");
	final GatewayParameter ko = prepareForGatewayParameter("KO", "url?s={\r    query  \r    }");
	final GatewayParameter sap = prepareForGatewayParameter("SAP.DE","url?s={\tquery\t}"	);
	final GatewayParameter vz = prepareForGatewayParameter("VZ",  "url?s={\t\n query \t\n}" );
	
	
	@Test
	public final void build() {
		final MergedGatewayParameterBuilder mergedGatewayParameterBuilder = new MergedGatewayParameterBuilderImpl().withGateway(Gateway.GoogleRealtimeRate).withGatewayParameter(Arrays.asList(jnj,pg,ko,sap,vz));
		final GatewayParameter  mergedGatewayParameter = mergedGatewayParameterBuilder.build();
		final String code = CODE_JNJ+ ",PG,KO,SAP.DE,VZ";
		
		
		Assert.assertEquals(code, mergedGatewayParameter.code());
		Assert.assertEquals(URL, mergedGatewayParameter.urlTemplate());
		Assert.assertEquals(Gateway.GoogleRealtimeRate, mergedGatewayParameter.gateway());
		Assert.assertEquals(1, mergedGatewayParameter.parameters().size());
		
		Assert.assertEquals(QUERY_PARAMETER_NAME, mergedGatewayParameter.parameters().keySet().stream().findAny().get());
		Assert.assertEquals(code, mergedGatewayParameter.parameters().values().stream().findAny().get());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void mergeInvalidKeysize() {
		final Gateway gateway = Mockito.mock(Gateway.class);
		final MergedGatewayParameterBuilder mergedGatewayParameterBuilder = new MergedGatewayParameterBuilderImpl().withGateway(gateway).withGatewayParameter(Arrays.asList(jnj));
		Mockito.when(gateway.id(Mockito.any())).thenReturn(CODE_JNJ);
		mergedGatewayParameterBuilder.build();
	}
	
	private   GatewayParameter prepareForGatewayParameter(final String code, final String url) {
		final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
		Mockito.when(gatewayParameter.code()).thenReturn(code);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(url);
		final Map<String,String> parameters = new HashMap<>();
		parameters.put(QUERY_PARAMETER_NAME, code);
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		return gatewayParameter;
	}
	
	@Test
	public final void withGateway() {
		final MergedGatewayParameterBuilder mergedGatewayParameterBuilder = new MergedGatewayParameterBuilderImpl();
		Assert.assertEquals(mergedGatewayParameterBuilder, mergedGatewayParameterBuilder.withGateway(Gateway.GoogleRealtimeRate));
		Assert.assertEquals(Gateway.GoogleRealtimeRate, getFieldValue(mergedGatewayParameterBuilder, Gateway.class));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void withGatewayNotNull() {
		new MergedGatewayParameterBuilderImpl().withGateway(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void withGatewayAlreadyAssigned() {
		new MergedGatewayParameterBuilderImpl().withGateway(Gateway.GoogleRealtimeRate).withGateway(Gateway.ArivaDividendHistory);
	}


	@SuppressWarnings("unchecked")
	private <T>  T getFieldValue(final MergedGatewayParameterBuilder mergedGatewayParameterBuilder, Class<?> fieldType) {
		return (T)  DataAccessUtils.requiredSingleResult(Arrays.asList(MergedGatewayParameterBuilderImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(fieldType)).map(field -> ReflectionTestUtils.getField(mergedGatewayParameterBuilder, field.getName())).collect(Collectors.toList()));
	}
	
	@Test
	public final void withGatewayParameter() {
		final MergedGatewayParameterBuilder mergedGatewayParameterBuilder = new MergedGatewayParameterBuilderImpl();
	    Assert.assertEquals(mergedGatewayParameterBuilder, mergedGatewayParameterBuilder.withGatewayParameter(Arrays.asList(jnj)));
	    Assert.assertEquals(Arrays.asList(jnj), getFieldValue(mergedGatewayParameterBuilder, Collection.class));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void withGatewayParameterNull() {
		new MergedGatewayParameterBuilderImpl().withGatewayParameter(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void withGatewayParameterEmpty() {
		new MergedGatewayParameterBuilderImpl().withGatewayParameter(new ArrayList<>());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void withGatewayParameterAlreadyAssigned() {
		new MergedGatewayParameterBuilderImpl().withGatewayParameter(Arrays.asList(jnj,pg)).withGatewayParameter(Arrays.asList(ko,sap,vz));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void gatewayExistsGuard() {
		new MergedGatewayParameterBuilderImpl().withGatewayParameter(Arrays.asList(jnj,pg)).build();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void gatewayParameterExistsGuard() {
		new MergedGatewayParameterBuilderImpl().withGateway(Gateway.ApiLayerRealtimeExchangeRates).build();
	}

}
