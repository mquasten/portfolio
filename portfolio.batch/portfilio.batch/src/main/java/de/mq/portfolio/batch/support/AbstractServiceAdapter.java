package de.mq.portfolio.batch.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;

import de.mq.portfolio.batch.MethodParameterInjection;

abstract  class AbstractServiceAdapter   {
	
	private final MethodParameterInjection<String> methodParameterInjection;
	
	


	private final Map<String,Object> params = new HashMap<>();
	
	
	
	protected AbstractServiceAdapter(final MethodParameterInjection<String> methodParameterInjection) {
		this.methodParameterInjection=methodParameterInjection;
	}
	
	
	 protected final Object invokeMethod(){
		return methodParameterInjection.invokeMethod(params);
		 
	 }

	



	@BeforeStep
	protected final void beforeStep(final StepExecution stepExecution) {
		params.clear();
	   stepExecution.getJobExecution().getJobParameters().getParameters().entrySet().forEach(e -> params.put(e.getKey(), e.getValue().getValue()));
	}



	protected final void putItem(final Object item) {
		params.put(null, item);
	}
	
	
	

}
