package de.mq.portfolio.batch.support;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.batch.support.MethodInvoker;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;



class MethodTargetImpl implements MethodInvoker {
	
	private final Method method;
	private final Object target;
	
	MethodTargetImpl(final Object target, final String methodName) {
		Assert.notNull(target, "Target is mandatory");
		Assert.notNull(methodName, "MethodName is mandatory");
		this.target=target; 
		method =Arrays.asList(target.getClass().getDeclaredMethods()).stream().filter(m -> m.getName().equals(methodName) ).reduce((a, b) -> a != null ? a: b).orElse(null);
		Assert.notNull(method, String.format("Method %s not found on Service %s", methodName, target.getClass().getName()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.batch.support.MethodInvoker#invokeMethod(java.lang.Object[])
	 */
	@Override
	public Object invokeMethod(final Object... args) {
		method.setAccessible(true);
		return ReflectionUtils.invokeMethod(method,target, args);
	}
	
	
	
	
}
