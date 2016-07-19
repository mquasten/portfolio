package de.mq.portfolio.batch.support;

import java.util.List;

import de.mq.portfolio.batch.Rule;
import de.mq.portfolio.batch.RulesEngine;



public interface RulesEngineBuilder {

	RulesEngineBuilder withContinueOnFailure();

	RulesEngineBuilder withSkipOnFirstAppliedRule();

	RulesEngineBuilder withRule(final Rule rule);

	RulesEngineBuilder withRules(final List<Rule> rules);

	RulesEngine build();
	
	static RulesEngineBuilder newBuilder() {
		return new SimpleRuleEngineBuilderImpl();
	}

}