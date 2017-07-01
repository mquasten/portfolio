package de.mq.portfolio.gateway.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

@Repository
class GatewayParameterRepositoryImpl  implements GatewayParameterRepository{

	final MongoOperations mongoOperations; 
	
	@Autowired
	GatewayParameterRepositoryImpl(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	@Override
	public GatewayParameter shareGatewayParameter(final Gateway gateway, final String... keys) {
		
		final Query query = new Query(Criteria.where("id").is(gateway.id(keys)));
		return DataAccessUtils.requiredSingleResult(mongoOperations.find(query, GatewayParameterImpl.class));
		
	}
	
	public final void save(final GatewayParameter shareGatewayParameter) {
		mongoOperations.save(shareGatewayParameter);
	}

}
