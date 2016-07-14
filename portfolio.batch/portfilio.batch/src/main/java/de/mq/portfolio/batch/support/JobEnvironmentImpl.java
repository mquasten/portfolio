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
<<<<<<< HEAD:portfolio.batch/portfilio.batch/src/main/java/de/mq/portfolio/batch/support/JobEnvironmentImpl.java
	private final Map<String,Object> parameters = new HashMap<>();
=======
	private final Map<String,Object> parameter = new HashMap<>();
>>>>>>> 5968e539d5a8fd9d4158676393fd00821240e789:portfolio.batch/portfilio.batch/src/main/java/de/mq/portfolio/batch/support/JobEnvironmentImpl.java
	
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
<<<<<<< HEAD:portfolio.batch/portfilio.batch/src/main/java/de/mq/portfolio/batch/support/JobEnvironmentImpl.java
	public final  <T> T parameters(final String name) {
		Assert.isTrue(parameters.containsKey(name), String.format("Parameter %s is mandatory", name));
		return (T) parameters.get(name);
=======
	public final  <T> T parameter(final String name) {
		Assert.isTrue(parameter.containsKey(name), String.format("Parameter %s is mandatory", name));
		return (T) parameter.get(name);
>>>>>>> 5968e539d5a8fd9d4158676393fd00821240e789:portfolio.batch/portfilio.batch/src/main/java/de/mq/portfolio/batch/support/JobEnvironmentImpl.java
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.JobEnvironment#parameterNames()
<<<<<<< HEAD:portfolio.batch/portfilio.batch/src/main/java/de/mq/portfolio/batch/support/JobEnvironmentImpl.java
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
=======
	 */
	@Override
	public final Collection<String> parameterNames() {
		return Collections.unmodifiableCollection(parameter.keySet());
		
>>>>>>> 5968e539d5a8fd9d4158676393fd00821240e789:portfolio.batch/portfilio.batch/src/main/java/de/mq/portfolio/batch/support/JobEnvironmentImpl.java
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
