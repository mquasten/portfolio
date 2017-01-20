package de.mq.portfolio.user;

public interface User {

	boolean checkPassword(final String password);


    String login();

    String password();


    void assign(final User user);
	
	

}