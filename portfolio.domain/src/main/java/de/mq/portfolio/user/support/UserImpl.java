package de.mq.portfolio.user.support;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import de.mq.portfolio.user.User;

@Document(collection = "User")
class UserImpl implements User {

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

	@Override
	public String login() {
        return login;
    }

	@Override
	public String password() {
        return password;
    }
	

	@Override 
   public void assign(final User user) {
       this.login=user.login();
       this.password=user.password();
   }


    @Override
    public String toString() {
        return String.format("login= %s, id= %s", login, id);
    }



	
}
