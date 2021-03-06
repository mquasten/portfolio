package de.mq.portfolio.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Serialize {
	
	String[] fields() default {};

	String[] mappings() default {};
}
