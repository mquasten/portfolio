package de.mq.portfolio.batch.support;

import java.util.ArrayList;

import java.util.List;


import org.springframework.util.Assert;

import de.mq.portfolio.batch.Rule;
import de.mq.portfolio.batch.RulesEngine;


class SimpleRuleEngineBuilderImpl implements RulesEngineBuilder {
	
	private final List<Rule> rules = new ArrayList<>();

	private boolean skipOnFailure=true;

	private  boolean skipOnFirstApplied=false;
	
	SimpleRuleEngineBuilderImpl() {
		
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.batch.support.RulesEngineBuilder#withContinueOnFailure()
	 */
	@Override
	public final RulesEngineBuilder withContinueOnFailure() {
		this.skipOnFailure=false;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.batch.support.RulesEngineBuilder#withSkipOnFirstAppliedRule()
	 */
	@Override
	public final RulesEngineBuilder withSkipOnFirstAppliedRule() {
		this.skipOnFirstApplied=true;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.batch.support.RulesEngineBuilder#withRule(org.easyrules.api.Rule)
	 */
	@Override
	public final RulesEngineBuilder withRule(final Rule rule) {
		Assert.notNull(rule, "Rule ist mandatory.");
		this.rules.add(rule);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.batch.support.RulesEngineBuilder#withRules(java.util.List)
	 */
	@Override
	public final RulesEngineBuilder withRules(final List<Rule> rules) {
		Assert.notEmpty(rules, "At least one Rule must be given.");
		this.rules.addAll(rules);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.batch.support.RulesEngineBuilder#build()
	 */
	@Override
	public final RulesEngine build() {
		Assert.notEmpty(rules, "At least one Rule must be given.");
		return new SimpleRulesEngineImpl(rules, skipOnFailure, skipOnFirstApplied);
		
	}

}
