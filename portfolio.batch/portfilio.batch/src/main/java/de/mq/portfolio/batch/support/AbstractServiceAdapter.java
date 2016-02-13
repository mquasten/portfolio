package de.mq.portfolio.batch.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.support.MethodInvoker;

abstract  class AbstractServiceAdapter {
	
	private final MethodInvoker methodInvoker;
	
	
	private final List<String> parameterNames = new ArrayList<>();;

	private final Map<String,Object> params = new HashMap<>();
	
	protected AbstractServiceAdapter(final MethodInvoker methodInvoker) {
		this(methodInvoker, new ArrayList<>());
	}
	
	protected AbstractServiceAdapter(final MethodInvoker methodInvoker, final List<String> parameterNames) {
		this.methodInvoker=methodInvoker;
		this.parameterNames.addAll(parameterNames);
	}
	
	
	


	protected final  Object executeMethod(DefaultHandler defaultHandler) {
		return methodInvoker.invokeMethod( arguments( defaultHandler));
	} 

	protected final   Object[] arguments(final DefaultHandler defaultHandler ) {
		final List<Object> argumentsAsList =  parameterNames.stream().map(name ->  name ==null ? defaultHandler.getDefault()  :params.get(name)).collect(Collectors.toList());
		 final Object[] arguments = argumentsAsList.toArray( new Object[argumentsAsList.size()]);
		return arguments;
	}



	@BeforeStep
	protected final void beforeStep(StepExecution stepExecution) {
		System.out.println("BeforeStep");
		if( parameterNames.isEmpty()){
			return;
		}
		
	   stepExecution.getJobExecution().getJobParameters().getParameters().entrySet().stream().filter(e -> parameterNames.contains(e.getKey())).forEach(e -> params.put(e.getKey(), e.getValue().getValue()));
	  
	
	}



	@FunctionalInterface
	interface DefaultHandler {
		Object getDefault();
	}

}
