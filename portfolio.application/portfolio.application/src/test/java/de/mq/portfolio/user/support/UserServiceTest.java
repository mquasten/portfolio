package de.mq.portfolio.user.support;

import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import de.mq.portfolio.user.User;
import de.mq.portfolio.user.UserService;
import org.junit.Assert;
public class UserServiceTest {
	private final UserRepository userRepository = Mockito.mock(UserRepository.class);
	private final UserService userService = new UserServiceImpl(userRepository);
	
	private final User user = Mockito.mock(User.class);
	
	@Test
	public final void user() {
		Mockito.when(userRepository.userByLogin(UserRepositoryTest.LOGIN)).thenReturn(user);
		Assert.assertEquals(Optional.of(user), userService.user(UserRepositoryTest.LOGIN));
	}
	
	@Test
	public final void userNotFound() {
		Mockito.doThrow(IncorrectResultSizeDataAccessException.class).when(userRepository).userByLogin(UserRepositoryTest.LOGIN);
		Assert.assertFalse(userService.user(UserRepositoryTest.LOGIN).isPresent());
	}
	
	@Test
	public final void enhanceUser() {
		Mockito.when(userRepository.userByLogin(UserRepositoryTest.LOGIN)).thenReturn(user);
		final User changedUser = Mockito.mock(User.class);
		Mockito.when(changedUser.login()).thenReturn(UserRepositoryTest.LOGIN);
		Assert.assertEquals(this.user, userService.user(changedUser));
		Mockito.verify(this.user).assign(changedUser);
	}
	
	@Test
	public final void enhanceUserNew() {
		final User changedUser = Mockito.mock(User.class);
		Mockito.when(changedUser.login()).thenReturn(UserRepositoryTest.LOGIN);
		Mockito.doThrow(IncorrectResultSizeDataAccessException.class).when(userRepository).userByLogin(UserRepositoryTest.LOGIN);
		Assert.assertEquals(changedUser, userService.user(changedUser));
	}
	
	@Test
	public final void  save() {
		userService.save(user);
		Mockito.verify(userRepository).save(user);
	}

}
