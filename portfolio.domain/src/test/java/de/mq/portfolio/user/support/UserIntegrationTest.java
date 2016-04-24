package de.mq.portfolio.user.support;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.user.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo.xml" })
@Ignore
public class UserIntegrationTest {
	
	
	@Autowired
	private MongoOperations mongoOperations;

	@Test
	public final void createUser() {
		final User user = new UserImpl("kminogue", "fever");
		mongoOperations.save(user);
		
	}
	
}
