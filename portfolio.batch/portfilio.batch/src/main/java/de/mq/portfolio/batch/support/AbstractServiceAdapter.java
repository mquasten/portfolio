package de.mq.portfolio.batch.support;


import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;




import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.batch.MethodParameterInjection;

abstract  class AbstractServiceAdapter   {
	
	private final MethodParameterInjection<String> methodParameterInjection;
	
	
	private final JobContent<String> content; 

	private final Map<String,Object> params = new HashMap<>();
	
	final Map<String, MethodParameterInjection<String>> enrichters = new HashMap<>();
	
	
	
	protected AbstractServiceAdapter(final MethodParameterInjection<String> methodParameterInjection) {
		this(methodParameterInjection, new JobContentImpl<String>(), new HashMap<>());
	}
	
	
	protected AbstractServiceAdapter(final MethodParameterInjection<String> methodParameterInjection, final JobContent<String> content, final Map<String, MethodParameterInjection<String>> enrichters) {
		this.methodParameterInjection=methodParameterInjection;
		this.content=content;
		this.enrichters.putAll(enrichters);
	
	}
	
	protected AbstractServiceAdapter(final MethodParameterInjection<String> methodParameterInjection, final JobContent<String> content) {
		 this(methodParameterInjection, content , new HashMap<>());
	
	}
	
	
	 protected final Object invokeMethod(){
		return methodParameterInjection.invokeMethod(params);
		 
	 }

	



	@BeforeStep
	protected final void beforeStep(final StepExecution stepExecution) {
		params.clear();
	   stepExecution.getJobExecution().getJobParameters().getParameters().entrySet().forEach(e -> params.put(e.getKey(), e.getValue().getValue()));
	
	   enrichters.entrySet().stream().forEach(entry -> {
	   	final Object result = entry.getValue().invokeMethod(params);
	   	content.putContent(entry.getKey(), result);
	   	params.put(entry.getKey(), result);
	   
	   });
	   params.putAll(content.content());
	   
	}



	protected final void putItem(final Object item) {
		params.put(null, item);
	}
	
	
	

}
