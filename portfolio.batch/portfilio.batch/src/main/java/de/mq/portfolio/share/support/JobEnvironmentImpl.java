package de.mq.portfolio.share.support;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.Assert;



class JobEnvironmentImpl implements JobEnvironment {
	private final Map<String,Object> parameter = new HashMap<>();
	
	private final Map<Class<?>,Object> beans = new HashMap<>();
	
	private final List<String> processed = new ArrayList<>();
	
	private final List<Entry<String,? extends Throwable>> exceptions = new ArrayList<>();
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#assign(java.lang.String, T)
	 */
	@Override
	public final  <T> void assign(final String name, T value) {
		parameter.put(name, value);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#parameter(java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final  <T> T parameter(final String name) {
		Assert.isTrue(parameter.containsKey(name), String.format("Parameter %s is mandatory", name));
		return (T) parameter.get(name);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#assign(java.lang.Class, T)
	 */
	@Override
	public final  <T> void assign(final Class<? extends T> clazz, T value) {
		beans.put(clazz, value);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#bean(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T bean(final Class<? extends T> clazz) {
		Assert.isTrue(parameter.containsKey(clazz), String.format("Bean of type %s is mandatory.", clazz.getName()));
		return (T) beans.get(clazz);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#assign(java.lang.String)
	 */
	@Override
	public final <T> void assign(final String name) {
		processed.add(name);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#processed()
	 */
	@Override
	public final Collection<String> processed() {
		return Collections.unmodifiableCollection(processed);
		
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#assign(java.lang.String, T)
	 */
	@Override
	public final  <T extends Throwable> void assign(final String rule, final T exception) {
		exceptions.add(new AbstractMap.SimpleImmutableEntry<>(rule, exception));
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#exceptions()
	 */
	@Override
	public Collection<Entry<String,? extends Throwable>>  exceptions() {
		return Collections.unmodifiableList(exceptions);
	}

}
