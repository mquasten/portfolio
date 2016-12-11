package de.mq.portfolio.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
	
	public static final String DEFAULT_PARAMETER = "arg";

	String value() default DEFAULT_PARAMETER; 
	

}
