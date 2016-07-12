package de.mq.portfolio.share.support;

import org.easyrules.api.Rule;
import org.easyrules.api.RuleListener;
import org.springframework.beans.factory.annotation.Autowired;


public class SimpleRuleListenerImpl implements RuleListener {

	@Autowired
	private JobEnvironment jobEnvironment;
	
	@Override
	public void beforeExecute(final Rule rule) {
		
		
	}

	@Override
	public void onSuccess(final Rule rule) {
		jobEnvironment.assign(rule.getName());
		
	}

	@Override
	public void onFailure(final Rule rule, final Exception exception) {
		jobEnvironment.assign(rule.getName(), exception.getCause());
	}	
	
	

}
