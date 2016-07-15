package de.mq.portfolio.batch.support;

import org.easyrules.api.Rule;
import org.easyrules.api.RuleListener;

import de.mq.portfolio.batch.JobEnvironment;


class SimpleRuleListenerImpl implements RuleListener {

	
	private final JobEnvironment jobEnvironment;
	
	SimpleRuleListenerImpl(JobEnvironment jobEnvironment) {
		this.jobEnvironment = jobEnvironment;
	}

	@Override
	public void beforeExecute(final Rule rule) {
		
		
	}

	@Override
	public void onSuccess(final Rule rule) {
		jobEnvironment.assignProcessed(rule.getName());
		
	}

	@Override
	public void onFailure(final Rule rule, final Exception exception) {
		jobEnvironment.assignFailed(rule.getName(), exception.getCause());
	}	
	
	

}
