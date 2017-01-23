package de.mq.portfolio.user.support;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.user.User;
import junit.framework.Assert;

public class UserTest {

	private static final String LOGIN_FIELD = "login";
	private static final String ID_FIELD = "id";
	private static final String USER_COLLECTION_NAME = "User";
	private static final String PASSWORD = "kinkyKylie";
	private static final String LOGIN = "kminogue";

	private final User user = new UserImpl(LOGIN, PASSWORD);
	
	@Test
	public final void checkPassword() {
		
		Assert.assertTrue(user.checkPassword(PASSWORD));
		Assert.assertFalse(user.checkPassword(LOGIN));
		Assert.assertFalse(user.checkPassword(null));
		Assert.assertFalse(new UserImpl(LOGIN, null).checkPassword(PASSWORD));
		Assert.assertFalse(new UserImpl(LOGIN, null).checkPassword(null));
	}

	@Test
	public final void annotations() {
		Assert.assertTrue(UserImpl.class.isAnnotationPresent(Document.class));
		Assert.assertEquals(USER_COLLECTION_NAME, UserImpl.class.getAnnotation(Document.class).collection());

		final Optional<String> id = findField(Id.class);
		Assert.assertTrue(id.isPresent());
		Assert.assertEquals(ID_FIELD, id.get());

		final Optional<String> login = findField(Indexed.class);
		Assert.assertTrue(login.isPresent());
		Assert.assertEquals(LOGIN_FIELD, login.get());
		Assert.assertTrue(ReflectionUtils.findField(UserImpl.class, login.get()).getAnnotation(Indexed.class).unique());

	}

	private Optional<String> findField(Class<? extends Annotation> clazz) {
		return Arrays.asList(UserImpl.class.getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(clazz)).map(field -> field.getName()).findAny();
	}

	@Test
	public final void login() {
		Assert.assertEquals(LOGIN, user.login());
	}
	
	@Test
	public final void password() {
		Assert.assertEquals(PASSWORD, user.password());
	}
	
	@Test
	public final void string() {
		Assert.assertEquals(String.format(UserImpl.TO_STRING_PATTERN, LOGIN), user.toString());
	}
	@Test
	public final void assign() {
		final User user = new UserImpl(null, null);
		Assert.assertNull(user.login());
		Assert.assertNull(user.password());
		
		user.assign(this.user);
		
		Assert.assertEquals(LOGIN, user.login());
		Assert.assertEquals(PASSWORD, user.password());
	}
}
