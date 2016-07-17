package de.mq.portfolio.support;

import java.util.ArrayList;
import java.util.Collection;

import de.mq.portfolio.batch.JobEnvironment;


//must be apublic class for easy rules ...
class ProcessServiceRuleImpl<T,R> extends AbstractServiceRule<T> {

	static final int DEFAULT_PRIORITY = 2; 
	ProcessServiceRuleImpl(final T target, final String spEl, final JobEnvironment jobEnvironment) {
		this(target, spEl,jobEnvironment, DEFAULT_PRIORITY);
		
	}
	
	ProcessServiceRuleImpl(final T target, final String spEl, final JobEnvironment jobEnvironment, final int priority) {
		super(target, spEl, jobEnvironment, priority);
		
	}

	@Override
	protected void action(final JobEnvironment jobEnvironment) {
		final Collection<R> items = jobEnvironment.parameters(ITEMS_PARAMETER);
		final Collection<R> results = new ArrayList<>();
		items.forEach(item -> {
			jobEnvironment.assign("item", item);
			final R result = executeEl();
			results.add( (result !=null) ? result :item);
		});
		jobEnvironment.assign(ITEMS_PARAMETER, results);
		
	
		
	}



	

	

	


	

	

	
}
