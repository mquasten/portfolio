package de.mq.portfolio.user;

import java.util.Optional;

import de.mq.portfolio.user.User;

public interface UserService {

	Optional<User> user(String login);

    User user(User user);

}