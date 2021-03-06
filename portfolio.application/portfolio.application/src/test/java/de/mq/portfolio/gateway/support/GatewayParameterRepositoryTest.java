package de.mq.portfolio.gateway.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

public class GatewayParameterRepositoryTest {

	private static final String CODE = "JNJ";
	private final MongoOperations mongoOperations = Mockito.mock(MongoOperations.class);
	private final GatewayParameterRepository gatewayParameterRepository = new GatewayParameterRepositoryImpl(mongoOperations);
	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);

	@Test
	public final void gatewayParameter() {
		final Collection<Query> queries = new ArrayList<>();
		Mockito.doAnswer(answer -> {
			Assert.assertEquals(GatewayParameterImpl.class, answer.getArgument(1));
			queries.add(answer.getArgument(0));
			return Arrays.asList(gatewayParameter);
		}).when(mongoOperations).find(Mockito.any(Query.class), Mockito.any());

		Assert.assertEquals(gatewayParameter, gatewayParameterRepository.gatewayParameter(Gateway.GoogleRateHistory, CODE));

		Assert.assertEquals(1, queries.size());
		final Query query = queries.stream().findAny().get();

		final Map<?, ?> queryParams = query.getQueryObject().toMap();

		Assert.assertEquals(1, queryParams.size());

		Assert.assertEquals(idFieldName(), queryParams.keySet().stream().findAny().get());
		Assert.assertEquals(Gateway.GoogleRateHistory.id(CODE), queryParams.values().stream().findAny().get());
	}

	private String idFieldName() {
		return DataAccessUtils.requiredSingleResult(Arrays.asList(GatewayParameterImpl.class.getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(Id.class)).map(field -> field.getName()).collect(Collectors.toList()));
	}

	@Test
	public final void save() {
		gatewayParameterRepository.save(gatewayParameter);

		Mockito.verify(mongoOperations).save(gatewayParameter);
	}

	@Test
	public final void gatewayParameters() {
		final GatewayParameter otherGatewayParameter = Mockito.mock(GatewayParameter.class);
		final Collection<Query> queries = prepareForGatewayParameters(otherGatewayParameter);

		Assert.assertEquals(Arrays.asList(gatewayParameter, otherGatewayParameter), gatewayParameterRepository.gatewayParameters(CODE));

		Assert.assertEquals(1, queries.size());
		final Query query = queries.stream().findAny().get();
		

		final Map<?, ?> queryParams = query.getQueryObject().toMap();

		Assert.assertEquals(idFieldName(), queryParams.keySet().stream().findAny().get());

		Assert.assertEquals(Gateway.pattern(".*", CODE), queryParams.values().stream().findAny().get().toString());
	}

	private Collection<Query> prepareForGatewayParameters(final GatewayParameter otherGatewayParameter) {
		final Collection<Query> queries = new ArrayList<>();
		Mockito.doAnswer(answer -> {
			Assert.assertEquals(GatewayParameterImpl.class, answer.getArgument(1));
			queries.add(answer.getArgument(0));
			return Arrays.asList(gatewayParameter, otherGatewayParameter);
		}).when(mongoOperations).find(Mockito.any(Query.class), Mockito.any());
		return queries;
	}
	
	@Test
	public final void gatewayParametersIndex() {
		final GatewayParameter otherGatewayParameter = Mockito.mock(GatewayParameter.class);
		
		final Collection<Query> queries = prepareForGatewayParameters(otherGatewayParameter);

		String code = "DJI";
		Assert.assertEquals(Arrays.asList(gatewayParameter, otherGatewayParameter), gatewayParameterRepository.gatewayParameters("^"+code));

		Assert.assertEquals(1, queries.size());
		final Query query = queries.stream().findAny().get();

		final Map<?, ?> queryParams = query.getQueryObject().toMap();

		Assert.assertEquals(idFieldName(), queryParams.keySet().stream().findAny().get());

		Assert.assertEquals(Gateway.pattern(".*", "." +code), queryParams.values().stream().findAny().get().toString());
	}

	
	@Test
	public final void gatewayParametersForGateWay() {
		final Collection<Query> queries = new ArrayList<>();
		Mockito.doAnswer(answer -> {
			Assert.assertEquals(GatewayParameterImpl.class, answer.getArguments()[1]);
			queries.add((Query) answer.getArguments()[0]);
			return Arrays.asList(gatewayParameter);
		}).when(mongoOperations).find(Mockito.any(Query.class), Mockito.any());
		
		final Collection<GatewayParameter> results = ((GatewayParameterRepositoryImpl)gatewayParameterRepository).gatewayParameters(Gateway.CentralBankExchangeRates);
		
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(gatewayParameter, DataAccessUtils.requiredSingleResult(results));
		
		Assert.assertEquals(1, queries.size());
		final Map<?, ?> queryParams = DataAccessUtils.requiredSingleResult(queries).getQueryObject().toMap();
		Assert.assertEquals(1, queryParams.size());
		Assert.assertEquals(idFieldName(), DataAccessUtils.requiredSingleResult(queryParams.keySet()));
		Assert.assertEquals(".*-BER", DataAccessUtils.requiredSingleResult(queryParams.values()).toString());
		Assert.assertEquals(Pattern.class, DataAccessUtils.requiredSingleResult(queries).getQueryObject().get(idFieldName()).getClass());
		
	}
}
