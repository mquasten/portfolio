package de.mq.portfolio.batch.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.batch.MethodParameterInjection;

class MethodParameterInjectionImpl<T> implements MethodParameterInjection<T> {

	private final Method method;
	private final Object target;

	final ConfigurableConversionService conversionService = new DefaultConversionService();
	private final List<T> parameterKeys = new ArrayList<>();

	MethodParameterInjectionImpl(final Object target, final String methodName) {
		this(target, methodName, new ArrayList<>());
	}

	MethodParameterInjectionImpl(final Object target, final String methodName, final List<T> parameterKeys) {
		Assert.notNull(target, "Target is mandatory");
		Assert.notNull(methodName, "MethodName is mandatory");
		this.target = target;
		method = Arrays.asList(target.getClass().getDeclaredMethods()).stream().filter(m -> m.getName().equals(methodName)).findFirst().orElse(null);
		Assert.notNull(method, String.format("Method %s not found on Service %s", methodName, target.getClass().getName()));
		this.parameterKeys.addAll(parameterKeys);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.batch.MethodParameterInjection#invokeMethod(java.util.Map)
	 */
	@Override
	public final Object invokeMethod(final Map<T, Object> dependencies) {
		method.setAccessible(true);

		final List<Class<?>> classes = Arrays.asList(method.getParameterTypes()).stream().map(t -> t).collect(Collectors.toList());

		return ReflectionUtils.invokeMethod(method, target, arguments(dependencies, classes));

	}

	private final Object[] arguments(final Map<T, Object> dependencies, List<Class<?>> types) {

		Assert.isTrue(parameterKeys.size() == types.size(), String.format("Confiured methodparameters for method %s did not match to arguments: %s", method, parameterKeys));

		final List<Object> argumentsAsList = IntStream.range(0, parameterKeys.size()).mapToObj(i -> convert(dependencies, i, types)).collect(Collectors.toList());


		final Object[] arguments = argumentsAsList.toArray(new Object[argumentsAsList.size()]);
		return arguments;
	}

	private Object convert(final Map<T, Object> dependencies, int i, final List<Class<?>> types) {
		final Object value = dependencies.get(parameterKeys.get(i));
		final Class<?> type = types.get(i);

		return convert(value, type);
	}

	private Object convert(final Object value, final Class<?> type) {
		if (value == null) {
			return null;
		}

		if (conversionService.canConvert(value.getClass(), type)) {
			return conversionService.convert(value, type);
		}
		return value;
	}

}
