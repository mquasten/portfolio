package de.mq.portfolio.support;

import org.easyrules.api.Rule;
import org.easyrules.api.RuleListener;

import de.mq.portfolio.batch.JobEnvironment;


class SimpleRuleListenerImpl implements RuleListener {

	
	private final JobEnvironment jobEnvironment;
	
	SimpleRuleListenerImpl(final JobEnvironment jobEnvironment) {
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
		exception.printStackTrace();
		jobEnvironment.assignFailed(rule.getName(), exception);
	}	
	
	

}
