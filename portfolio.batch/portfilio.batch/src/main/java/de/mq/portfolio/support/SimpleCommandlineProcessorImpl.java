package de.mq.portfolio.support;



import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import de.mq.portfolio.support.ExceptionTranslationBuilder.ResourceSupplier;


public  class SimpleCommandlineProcessorImpl implements ApplicationContextAware {
	
	private static Method method;
	private static Object target;
	
	private static String [] packages= new String [] {SimpleCommandlineProcessorImpl.class.getPackage().getName()};
	
	
	private  static  ResourceSupplier<ConfigurableApplicationContext> applicationContextSupplier =  () -> new AnnotationConfigApplicationContext(packages) ;
	
	

	
	@Retention(RetentionPolicy.RUNTIME)
	@interface Main {
		
	}
	
	private final Class<?> targetClass; 
	
	public SimpleCommandlineProcessorImpl(final Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	

	
	public  void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		
		target  = applicationContext.getBean(targetClass);
	
		final Collection<Method> methods = new  HashSet<>();
		ReflectionUtils.doWithMethods(targetClass,method -> methods.add(method), method -> method.isAnnotationPresent(Main.class));
		
		Assert.isTrue( methods.size() == 1 , String.format("One and only one method should be annotated with @Main in class %s.", targetClass.getName()));
		
		method= DataAccessUtils.requiredSingleResult(methods);
		Assert.isTrue(method.getParameterTypes().length <= 1, "Max. 1 Argument is expected.");
		method.setAccessible(true);
		if( method.getParameterTypes().length==0) {
			return;
		}
		if( method.getParameterTypes()[0].isArray() ) {
			return;
		}
		
		if(Collection.class.isAssignableFrom(method.getParameterTypes()[0])){
			return;
		}
		
		throw new IllegalArgumentException(String.format("Wrong Parameterarguments in method: %s, only array and collection is supported.", method.getName()));
	}
	
	public static final void main(final String[] args) {
	
		if( ! StringUtils.hasText(System.getProperty("spring.profiles.active") )) {
			System.setProperty("spring.profiles.active", "yahoo");
		} 
		new ExceptionTranslationBuilderImpl<Void, ConfigurableApplicationContext>().withResource(applicationContextSupplier).withStatement(ctx -> { doInApplicationContext(args); }).translate();

	}


	private static void doInApplicationContext(final String[] args) {
		Assert.notNull(target, "TargetBean is required, possibly BeanFactoryPostProcessor is not registered.");
		Assert.notNull(method, "Method is required, possibly BeanFactoryPostProcessor is not registered.");
		
		if( method.getParameterTypes().length==0) {
			ReflectionUtils.invokeMethod(method, target);
			return ;
		}
		
		if(method.getParameterTypes()[0].isArray() ) {
			ReflectionUtils.invokeMethod(method, target, new Object[] { args});
			return ;
		}
		
		if(Set.class.isAssignableFrom(method.getParameterTypes()[0])){
			ReflectionUtils.invokeMethod(method, target, new HashSet<>(Arrays.asList(args)));
			return ;
		}

		ReflectionUtils.invokeMethod(method, target, Arrays.asList(args));
	}




	
	
	


	

}

