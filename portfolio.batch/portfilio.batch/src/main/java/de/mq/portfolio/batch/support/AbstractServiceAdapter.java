package de.mq.portfolio.batch.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

abstract  class AbstractServiceAdapter {
	
	private final Object service;
	
	private final Method method;
	
	
	private final List<String> parameterNames = new ArrayList<>();;

	private final Map<String,Object> params = new HashMap<>();
	
	protected AbstractServiceAdapter(final Object service, final String methodName) {
		this(service,methodName, new ArrayList<>());
	}
	
	protected AbstractServiceAdapter(final Object service, final String methodName, final List<String> parameterNames) {
		Assert.notNull(service, "Service is mandatory");
		Assert.notNull(methodName, "MethodName is mandatory");
		this.service=service; 
		method =Arrays.asList(service.getClass().getDeclaredMethods()).stream().filter(m -> m.getName().equals(methodName) && m.getParameters().length == parameterNames.size()).reduce((a, b) -> a != null ? a: b).orElse(null);
		Assert.notNull(method, String.format("Method %s not found on Service %s", methodName, service.getClass().getName()));
		this.parameterNames.addAll(parameterNames);
	}
	
	
	


	protected final  Object executeMethod(DefaultHandler defaultHandler) {
		method.setAccessible(true);
		return ReflectionUtils.invokeMethod(method, service, arguments( defaultHandler));
	}

	private  Object[] arguments(final DefaultHandler defaultHandler ) {
		final List<Object> argumentsAsList =  parameterNames.stream().map(name ->  name ==null ? defaultHandler.getDefault()  :params.get(name)).collect(Collectors.toList());
		 final Object[] arguments = argumentsAsList.toArray( new Object[argumentsAsList.size()]);
		return arguments;
	}



	@BeforeStep
	protected final void beforeStep(StepExecution stepExecution) {
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
