package de.mq.portfolio.batch.support;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.Assert;

import de.mq.portfolio.batch.JobEnvironment;



class JobEnvironmentImpl implements JobEnvironment {
	private final Map<String,Object> parameters = new HashMap<>();
	
	private final List<String> processed = new ArrayList<>();
	
	private final List<Entry<String,? extends Throwable>> exceptions = new ArrayList<>();
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#assign(java.lang.String, T)
	 */
	@Override
	public final  <T> void assign(final String name, T value) {
		parameters.put(name, value);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#parameter(java.lang.String)
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	public final  <T> T parameters(final String name) {
		Assert.isTrue(parameters.containsKey(name), String.format("Parameter %s is mandatory", name));
		return (T) parameters.get(name);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#parameterNames()
	 */
	@Override
	public final Collection<String> parameterNames() {
		return Collections.unmodifiableCollection(parameters.keySet());
		
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.batch.JobEnvironment#clearParameters()
	 */
	@Override
	public final void clearParameters() {
		parameters.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.batch.JobEnvironment#assignProcessed(java.lang.String)
	 */
	@Override
	public final <T> void assignProcessed(final String name) {
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
	public final  <T extends Throwable> void assignFailed(final String rule, final T exception) {
		exceptions.add(new AbstractMap.SimpleImmutableEntry<>(rule, exception));
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#exceptions()
	 */
	@Override
	public Collection<Entry<String,? extends Throwable>>  failed() {
		return Collections.unmodifiableList(exceptions);
	}

	
}
