package de.mq.portfolio.batch.support;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.support.ConfigurableConversionService;

import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.batch.MethodParameterInjection;
import de.mq.portfolio.share.TimeCourse;


public class MethodParameterInjectionTest {
	private static final String TIME_COURSE_KEY = "timeCourse";
	private static final double VARIANCE = 1e-3;
	private static final double[] SAMPLES = new double[] {0.5,0.2, 0.3};
	private static final String PARAM_NAME = "samples";
	private final  List<String>  params = Arrays.asList(PARAM_NAME);
	private final ServiceMock service = Mockito.mock(ServiceMock.class);
	private final MethodParameterInjection<String> methodParameterInjection = new MethodParameterInjectionImpl<>(service, "calculate", params); 


	@Test
	public final void invokeMethod() {
		Map<String,Object> dependencies = new HashMap<>();
		dependencies.put(PARAM_NAME, SAMPLES);
		Mockito.when(service.calculate(SAMPLES)).thenReturn(VARIANCE);
		Assert.assertEquals(VARIANCE, methodParameterInjection.invokeMethod(dependencies));
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public final void invokeMethodWrongArguments() {
		final MethodParameterInjection<String> methodParameterInjection = new MethodParameterInjectionImpl<>(service, "calculate");
		final Map<String,Object> dependencies = new HashMap<>();
		
		Mockito.when(service.calculate(SAMPLES)).thenReturn(VARIANCE);
		Assert.assertEquals(VARIANCE, methodParameterInjection.invokeMethod(dependencies));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void invokeMethodWrongName() {
		new MethodParameterInjectionImpl<>(service, "dontLetMeGetMe");
		
	}
	
	@Test
	public final void invokeNull() {
		final MethodParameterInjection<String> methodParameterInjection = new MethodParameterInjectionImpl<>(service, "save", Arrays.asList(TIME_COURSE_KEY));
		final Map<String,Object> dependencies = new HashMap<>();
		
	
		 methodParameterInjection.invokeMethod(dependencies);
		 Mockito.verify(service).save(null);
	}
	
	@Test
	public final void invokeConverter() {
		final ConfigurableConversionService configurableConversionService = Mockito.mock(ConfigurableConversionService.class);
	
		final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
		final MethodParameterInjection<String> methodParameterInjection = new MethodParameterInjectionImpl<>(service, "save", Arrays.asList(TIME_COURSE_KEY));
		ReflectionTestUtils.setField(methodParameterInjection, "conversionService" , configurableConversionService);
		final Map<String,Object> dependencies = new HashMap<>();
		dependencies.put(TIME_COURSE_KEY, timeCourse);
	
		 methodParameterInjection.invokeMethod(dependencies);
		 Mockito.verify(service).save(timeCourse);
		 Mockito.verify(configurableConversionService).canConvert(timeCourse.getClass(),  TimeCourse.class);
	}
	
}


interface ServiceMock {
	
	double calculate(double[] samples);
	
	double save(final TimeCourse timeCourse);
	
}
