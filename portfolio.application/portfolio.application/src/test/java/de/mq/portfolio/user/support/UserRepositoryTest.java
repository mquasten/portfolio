package de.mq.portfolio.user.support;

import java.util.Arrays;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import de.mq.portfolio.user.User;
import org.junit.Assert;



public class UserRepositoryTest {
	
	private final User user = Mockito.mock(User.class);

	static final String LOGIN = "kminogue";

	private final MongoOperations mongoOperations =  Mockito.mock(MongoOperations.class);
	
	private final UserRepository userRepository = new UserRepositoryImpl(mongoOperations);  
	
	private final ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
	
	@SuppressWarnings("rawtypes")
	private final ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
	
	private final List<User> users = (List<User>) Arrays.asList(user);
	
	@SuppressWarnings("unchecked")
	@Before
	public final void setup() {
		Mockito.when(mongoOperations.find(queryCaptor.capture(), classCaptor.capture())).thenReturn(users);
	}
	
	
	@Test
	public final void userByLogin() {
		final User result =  userRepository.userByLogin(LOGIN);
		Assert.assertEquals(users.stream().findAny().get(), result);
		Assert.assertEquals(UserImpl.class, classCaptor.getValue());
		Assert.assertEquals(1, queryCaptor.getValue().getQueryObject().keySet().size());
		Assert.assertTrue(queryCaptor.getValue().getQueryObject().keySet().stream().findAny().isPresent());
		Assert.assertEquals(UserRepositoryImpl.LOGIN_FIELD_NAME, queryCaptor.getValue().getQueryObject().keySet().stream().findAny().get());
		Assert.assertEquals(LOGIN,  queryCaptor.getValue().getQueryObject().get(UserRepositoryImpl.LOGIN_FIELD_NAME));
	}

	@Test
	public final void save() {
		userRepository.save(user);
		Mockito.verify(mongoOperations).save(user);
	}
}
