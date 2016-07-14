package de.mq.portfolio.batch;

import java.util.Collection;
import java.util.Map.Entry;

public interface JobEnvironment {

	<T> void assign(final String name, final T value);

	<T> T parameters(final String name);

	<T> void assignProcessed(final String name);

	Collection<String> processed();

	<T extends Throwable> void assignFailed(final String rule, final T exception);

	Collection<Entry<String, ? extends Throwable>> failed();

	Collection<String> parameterNames();

	void clearParameters();



}