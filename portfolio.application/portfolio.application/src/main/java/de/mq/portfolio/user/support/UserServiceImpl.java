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

}
