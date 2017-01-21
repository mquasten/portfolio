package de.mq.portfolio.user.support;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import de.mq.portfolio.user.User;
import de.mq.portfolio.user.UserService;

@Service
class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Autowired
	UserServiceImpl(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/* (non-Javadoc)
	 * @see de.mq.portfolio.user.support.UserService#user(java.lang.String)
	 */
	@Override
	public final Optional<User> user(final String login) {
		try {
			return Optional.of(userRepository.userByLogin(login));
		} catch (IncorrectResultSizeDataAccessException rse) {
			return Optional.empty();
		}

	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.user.UserService#user(de.mq.portfolio.user.User)
	 */
	@Override
	public final User user(final User user) {
	    final Optional<User> existingUser = user(user.login());
	    if( existingUser.isPresent()) {
	        existingUser.get().assign(user);
	        return existingUser.get();
	    }
        return user;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.user.UserService#save(de.mq.portfolio.user.User)
	 */
	@Override
	public final void save(final User user) {
	    userRepository.save(user);
	}

}
