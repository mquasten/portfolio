package de.mq.portfolio.share.support;



import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Rule;
import org.springframework.beans.factory.annotation.Autowired;

import de.mq.portfolio.exchangerate.ExchangeRate;

@Rule
public class CsvImportRuleImpl {
	
	
	
	private static final String ITEMS_PARAMETER = "items";
	private static final String FILENAME = "filename";
	private final SimpleCSVInputServiceImpl<ExchangeRate> csvInputService;
	private final JobEnvironment jobEnvironment;
	
	@Autowired
	CsvImportRuleImpl(final SimpleCSVInputServiceImpl<ExchangeRate> csvInputService, final JobEnvironment jobEnvironment ) {
		this.csvInputService=csvInputService;
		this.jobEnvironment=jobEnvironment;
	}
	
	@Condition
	public final  boolean condition() {
		return true;
	}

	@Action
	public final  void action()  {
		jobEnvironment.assign(ITEMS_PARAMETER , csvInputService.read(jobEnvironment.parameter(FILENAME)));
		
	}
	
}
