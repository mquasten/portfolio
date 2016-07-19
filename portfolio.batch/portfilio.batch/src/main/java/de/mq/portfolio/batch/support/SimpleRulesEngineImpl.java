package de.mq.portfolio.batch.support;


import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.easyrules.api.Rule;
import org.springframework.util.Assert;

import de.mq.portfolio.batch.JobEnvironment;
import de.mq.portfolio.support.JobEnvironmentImpl;

class SimpleRulesEngineImpl {

	private final Collection<Rule> rules = new ArrayList<>();

	private final boolean skipOnFailure;

	private final boolean skipOnFirstApplied;
	

	private final List<Entry<String,? extends Throwable>> exceptions = new ArrayList<>();
	private final List<String> processed = new ArrayList<>();

	SimpleRulesEngineImpl(final List<Rule> rules, boolean skipOnFailure, boolean skipOnFirstApplied) {
		Assert.notEmpty(rules, "At least one Rule is mandatory");
		this.rules.addAll(rules);
		this.skipOnFailure = skipOnFailure;
		this.skipOnFirstApplied = skipOnFirstApplied;
	}

	SimpleRulesEngineImpl(final List<Rule> rules) {
		this(rules, true, false);
	}
	
	public final JobEnvironment fireRules(final Map<String,Object> parameters) {
		final JobEnvironment jobEnvironment = new JobEnvironmentImpl();
		parameters.entrySet().forEach(entry -> jobEnvironment.assign(entry.getKey(), entry.getValue()) );
		
		
		for(final Rule rule : rules ){
			if ( ! rule.evaluate() ) {
				continue;
			}
			
			execute(rule);
			
			if( skipOnFirstApplied) {
				break;
			}
			
			if(( skipOnFailure) && (!exceptions.isEmpty())) {
				break;
			}
				
		}
		
		return jobEnvironment;
		
	}

	private void execute(final Rule rule) {
		try {
			rule.execute();
			processed.add(rule.getName());
		} catch (final Exception ex) {
			exceptions.add(new SimpleImmutableEntry<>(rule.getName(), ex));
		}
	}
	
	
	public final Collection<String> processed() {
		return Collections.unmodifiableCollection(processed);
		
	}
	
	
	public final Collection<Entry<String,? extends Throwable>> failed() {
		return Collections.unmodifiableCollection(exceptions);
	}

}
