package de.mq.portfolio.gateway.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
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
	public ShareGatewayParameter shareGatewayParameter(Gateway gateway, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public final void save(final ShareGatewayParameter shareGatewayParameter) {
		mongoOperations.save(shareGatewayParameter);
	}

}
