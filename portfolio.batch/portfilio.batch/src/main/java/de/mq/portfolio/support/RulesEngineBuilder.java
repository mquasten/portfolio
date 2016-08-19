package de.mq.portfolio.support;

import java.util.List;

import de.mq.portfolio.batch.Rule;
import de.mq.portfolio.batch.RulesEngine;



public interface RulesEngineBuilder {

	RulesEngineBuilder withContinueOnFailure();

	RulesEngineBuilder withSkipOnFirstAppliedRule();

	RulesEngineBuilder withRule(final Rule rule);

	RulesEngineBuilder withRules(final List<Rule> rules);
	
	RulesEngineBuilder withName(final String name);

	RulesEngine build();
	
	
}