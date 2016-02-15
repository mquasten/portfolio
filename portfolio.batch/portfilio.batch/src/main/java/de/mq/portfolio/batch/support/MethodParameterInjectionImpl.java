package de.mq.portfolio.batch.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.batch.MethodParameterInjection;



class MethodParameterInjectionImpl<T> implements MethodParameterInjection<T> {
	
	private final Method method;
	private final Object target;
	
	
	
	private final List<T> parameterKeys = new ArrayList<>();
	
	MethodParameterInjectionImpl(final Object target, final String methodName) {
		this(target, methodName, new ArrayList<>());
	}
	
	MethodParameterInjectionImpl(final Object target, final String methodName, final List<T> parameterKeys  ) {
		Assert.notNull(target, "Target is mandatory");
		Assert.notNull(methodName, "MethodName is mandatory");
		this.target=target; 
		method =Arrays.asList(target.getClass().getDeclaredMethods()).stream().filter(m -> m.getName().equals(methodName) ).reduce((a, b) -> a != null ? a: b).orElse(null);
		Assert.notNull(method, String.format("Method %s not found on Service %s", methodName, target.getClass().getName()));
		this.parameterKeys.addAll(parameterKeys);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.batch.MethodParameterInjection#invokeMethod(java.util.Map)
	 */
	@Override
	public final Object invokeMethod(final Map<T,Object> dependencies) {
		method.setAccessible(true);
	   return ReflectionUtils.invokeMethod(method,target, arguments(dependencies));
	}
	
	
	private final   Object[] arguments(final Map<T,Object>  dependencies) {
		final List<Object> argumentsAsList =  parameterKeys.stream().map(name ->  dependencies.get(name)).collect(Collectors.toList());
		 final Object[] arguments = argumentsAsList.toArray( new Object[argumentsAsList.size()]);
		return arguments;
	}

	
}
