package de.mq.portfolio.batch;

import java.util.Map;

public interface Rule {

	String getName();

	boolean evaluate();

	void execute(final Map<String,Object> parameters);

}