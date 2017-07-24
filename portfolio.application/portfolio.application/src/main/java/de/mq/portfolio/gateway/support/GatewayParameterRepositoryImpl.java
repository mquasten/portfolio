package de.mq.portfolio.gateway.support;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

@Repository
class GatewayParameterRepositoryImpl  implements GatewayParameterRepository {

	final MongoOperations mongoOperations; 
	
	@Autowired
	GatewayParameterRepositoryImpl(final MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	@Override
	public GatewayParameter gatewayParameter(final Gateway gateway, final String... keys) {
		Assert.notNull(gateway, "Gateway is mandatory.");
		final Query query = new Query(Criteria.where("id").is(gateway.id(keys)));
		return DataAccessUtils.requiredSingleResult(mongoOperations.find(query, GatewayParameterImpl.class));
		
	}
	
	public final void save(final GatewayParameter gatewayParameter) {
		Assert.notNull(gatewayParameter, "GatewayParameter is mandatory.");
		mongoOperations.save(gatewayParameter);
	}

	@Override
	public Collection<GatewayParameter> gatewayParameters(final String... keys) {
		return Collections.unmodifiableList(mongoOperations.find(new Query(Criteria.where("id").regex(Gateway.pattern(".*" , keys))), GatewayParameterImpl.class));
	}

}
