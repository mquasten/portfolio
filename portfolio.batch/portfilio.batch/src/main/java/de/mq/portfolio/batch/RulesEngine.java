package de.mq.portfolio.batch;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public interface RulesEngine {

	void fireRules(final Map<String, Object> parameters);

	Collection<String> processed();

	Collection<Entry<String, ? extends Throwable>> failed();
	
	String name();

}