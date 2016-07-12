package de.mq.portfolio.share.support;

import java.util.Collection;
import java.util.Map.Entry;

public interface JobEnvironment {

	<T> void assign(String name, T value);

	<T> T parameter(String name);

	<T> void assign(Class<? extends T> clazz, T value);

	<T> T bean(Class<? extends T> clazz);

	<T> void assign(String name);

	Collection<String> processed();

	<T extends Throwable> void assign(String rule, T exception);

	Collection<Entry<String, ? extends Throwable>> exceptions();

}