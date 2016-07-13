package de.mq.portfolio.batch.support;




import java.util.Collection;

import org.easyrules.annotation.Rule;

import de.mq.portfolio.batch.JobEnvironment;

@Rule
//must be apublic class for easy rules ...
public class ImportServiceRuleImpl<T> extends AbstractServiceRule<T> {
	

	
	private final  Class<? extends Object> resultType = Collection.class;
	
	ImportServiceRuleImpl(final T target, final JobEnvironment jobEnvironment, final String spEl) {
		super(target, jobEnvironment, spEl);
	}



	@Override
	protected void action(final JobEnvironment jobEnvironment) {
		jobEnvironment.assign(ITEMS_PARAMETER, executeEl(resultType));
	}
	


	
}
