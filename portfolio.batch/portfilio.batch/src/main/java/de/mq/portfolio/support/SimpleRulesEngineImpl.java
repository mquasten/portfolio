package de.mq.portfolio.support;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.Assert;

import de.mq.portfolio.batch.Rule;
import de.mq.portfolio.batch.RulesEngine;

class SimpleRulesEngineImpl implements RulesEngine {

	private final Collection<Rule> rules = new ArrayList<>();

	private final boolean skipOnFailure;

	private final boolean skipOnFirstApplied;

	private final List<Entry<String, ? extends Throwable>> exceptions = new ArrayList<>();
	private final List<String> processed = new ArrayList<>();

	SimpleRulesEngineImpl(final List<Rule> rules, boolean skipOnFailure, boolean skipOnFirstApplied) {
		Assert.notEmpty(rules, "At least one Rule is mandatory");
		this.rules.addAll(rules);
		this.skipOnFailure = skipOnFailure;
		this.skipOnFirstApplied = skipOnFirstApplied;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.batch.support.RulesEngine#fireRules(java.util.Map)
	 */
	@Override
	public final void fireRules(final Map<String, Object> parameters) {
		for (final Rule rule : rules) {
			if (!rule.evaluate()) {
				continue;
			}

			execute(rule, parameters);

			if (skipOnFirstApplied) {
				break;
			}

			if ((skipOnFailure) && (!exceptions.isEmpty())) {
				break;
			}

		}

	}

	private void execute(final Rule rule, final Map<String, Object> parameters) {
		try {
			rule.execute(parameters);
			processed.add(rule.getName());
		} catch (final Exception ex) {
			exceptions.add(new SimpleImmutableEntry<>(rule.getName(), ex));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.batch.support.RulesEngine#processed()
	 */
	@Override
	public final Collection<String> processed() {
		return Collections.unmodifiableCollection(processed);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.batch.support.RulesEngine#failed()
	 */
	@Override
	public final Collection<Entry<String, ? extends Throwable>> failed() {
		return Collections.unmodifiableCollection(exceptions);
	}

}
