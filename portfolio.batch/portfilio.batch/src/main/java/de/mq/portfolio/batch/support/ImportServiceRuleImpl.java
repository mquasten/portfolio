package de.mq.portfolio.batch.support;

import org.easyrules.annotation.Rule;

import de.mq.portfolio.batch.JobEnvironment;

@Rule()
//must be apublic class for easy rules ...
public class ImportServiceRuleImpl<T> extends AbstractServiceRule<T> {
	
	
	static final int DEFAULT_PRIORITY = 1;



	ImportServiceRuleImpl(final T target, final JobEnvironment jobEnvironment, final String spEl) {
		super(target, jobEnvironment, spEl, DEFAULT_PRIORITY);


	}
	
	


	@Override
	protected void action(final JobEnvironment jobEnvironment) {

		jobEnvironment.assign(ITEMS_PARAMETER, executeEl());
	}
	

	


	
}
