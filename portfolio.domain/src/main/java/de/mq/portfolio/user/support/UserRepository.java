package de.mq.portfolio.user.support;

import de.mq.portfolio.user.User;

interface UserRepository {
	
	User userByLogin(final String login);

}
