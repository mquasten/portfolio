package de.mq.portfolio.batch.support;




<<<<<<< HEAD
=======
import java.util.Collection;

>>>>>>> 5968e539d5a8fd9d4158676393fd00821240e789
import org.easyrules.annotation.Rule;

import de.mq.portfolio.batch.JobEnvironment;

<<<<<<< HEAD
@Rule()
//must be apublic class for easy rules ...
public class ImportServiceRuleImpl<T> extends AbstractServiceRule<T> {
	
	
	static final int DEFAULT_PRIORITY = 1;



	ImportServiceRuleImpl(final T target, final JobEnvironment jobEnvironment, final String spEl) {
		super(target, jobEnvironment, spEl, DEFAULT_PRIORITY);
=======
@Rule
//must be apublic class for easy rules ...
public class ImportServiceRuleImpl<T> extends AbstractServiceRule<T> {
	

	
	private final  Class<? extends Object> resultType = Collection.class;
	
	ImportServiceRuleImpl(final T target, final JobEnvironment jobEnvironment, final String spEl) {
		super(target, jobEnvironment, spEl);
>>>>>>> 5968e539d5a8fd9d4158676393fd00821240e789
	}



	@Override
	protected void action(final JobEnvironment jobEnvironment) {
<<<<<<< HEAD
		jobEnvironment.assign(ITEMS_PARAMETER, executeEl());
	}



	



	
=======
		jobEnvironment.assign(ITEMS_PARAMETER, executeEl(resultType));
	}
>>>>>>> 5968e539d5a8fd9d4158676393fd00821240e789
	


	
}
