package de.mq.portfolio.gateway.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.ShareGatewayParameter;

@Repository
class ShareGatewayParameterRepositoryImpl  implements ShareGatewayParameterRepository{

	final MongoOperations mongoOperations; 
	
	@Autowired
	ShareGatewayParameterRepositoryImpl(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	@Override
	public ShareGatewayParameter shareGatewayParameter(final Gateway gateway, final String... keys) {
		
		final Query query = new Query(Criteria.where("id").is(gateway.id(keys)));
		return DataAccessUtils.requiredSingleResult(mongoOperations.find(query, ShareGatewayParameterImpl.class));
		
	}
	
	public final void save(final ShareGatewayParameter shareGatewayParameter) {
		mongoOperations.save(shareGatewayParameter);
	}

}
