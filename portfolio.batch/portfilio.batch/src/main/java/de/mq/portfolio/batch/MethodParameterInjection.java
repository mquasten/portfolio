package de.mq.portfolio.batch;

import java.util.Map;

public interface MethodParameterInjection<T> {

	
	 Object invokeMethod(final Map<T,Object> dependencies);
}
