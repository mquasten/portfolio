package de.mq.portfolio.batch.support;

import java.util.ArrayList;
import java.util.Collection;

import de.mq.portfolio.batch.JobEnvironment;


//must be apublic class for easy rules ...
public class ProcessServiceRuleImpl<T,R> extends AbstractServiceRule<T> {

	static final int DEFAULT_PRIORITY = 2; 
	ProcessServiceRuleImpl(final T target, final JobEnvironment jobEnvironment, final String spEl) {
		this(target, jobEnvironment, spEl, DEFAULT_PRIORITY);
		
	}
	
	ProcessServiceRuleImpl(final T target, final JobEnvironment jobEnvironment, final String spEl, final int priority) {
		super(target, jobEnvironment, spEl, priority);
		
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
