package de.mq.portfolio.user.support;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import de.mq.portfolio.user.User;

@Document(collection = "User")
class UserImpl implements User {

	static final String TO_STRING_PATTERN = "login= %s";

	@Id
	private String id;

	@Indexed(unique = true)
	private String login;

	private String password;

	UserImpl(final String login, final String password) {
		this.login = login;
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.user.support.User#checkPassword(java.lang.String)
	 */
	@Override
	public boolean checkPassword(final String password) {
		if (!StringUtils.hasText(this.password)) {
			return false;
		}
		if (!StringUtils.hasText(password)) {
			return false;
		}
		return this.password.equals(password);

	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.user.User#login()
	 */
	@Override
	public String login() {
		return login;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.user.User#password()
	 */
	@Override
	public String password() {
		return password;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.user.User#assign(de.mq.portfolio.user.User)
	 */
	@Override
	public void assign(final User user) {
		this.login = user.login();
		this.password = user.password();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(TO_STRING_PATTERN, login);
	}

}
