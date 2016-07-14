package de.mq.portfolio.batch;

import java.util.Collection;
import java.util.Map.Entry;

public interface JobEnvironment {

	<T> void assign(String name, T value);

	<T> T parameters(String name);

	<T> void assignProcessed(String name);

	Collection<String> processed();

	<T extends Throwable> void assignFailed(String rule, T exception);

	

	Collection<String> parameterNames();

	void clearParameters();

	Collection<Entry<String, ? extends Throwable>> failed();



}