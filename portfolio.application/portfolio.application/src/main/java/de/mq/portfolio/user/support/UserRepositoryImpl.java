package de.mq.portfolio.user.support;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import de.mq.portfolio.user.User;

@Repository
class UserRepositoryImpl  implements UserRepository {

	
	static final String LOGIN_FIELD_NAME = "login";
	private MongoOperations mongoOperations;
	
	@Autowired
	UserRepositoryImpl(final MongoOperations mongoOperations){
		this.mongoOperations=mongoOperations;
	}
	
	
	@Override
	public User userByLogin(final String login) {
		return DataAccessUtils.requiredSingleResult(mongoOperations.find(Query.query(Criteria.where(LOGIN_FIELD_NAME).is(login)), UserImpl.class));
	}


    @Override
    public void save(final User user) {
        mongoOperations.save(user);
    }

}
