package de.mq.portfolio.support;



import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.util.ReflectionUtils;

import junit.framework.Assert;





public class CommandlineProcessorTest {

	private static final String[] ARGS = new String[] {"Kylie", "is" , "nice"};
	private final Map<Class<?>, Field> fields = new HashMap<>();
	private TestBean testBean = Mockito.mock(TestBean.class);
	private Method method = ReflectionUtils.findMethod(testBean.getClass(), "process", Collection.class);
	@SuppressWarnings("unchecked")
	private final Function<String[], ApplicationContext>   applicationContextFunction = Mockito.mock(Function.class);
	
	private static  Function<String[], ApplicationContext>  applicationContextFunctionOrg; 
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() {
		CommandlineProcessorTest.applicationContextFunctionOrg = (Function<String[], ApplicationContext>) DataAccessUtils.requiredSingleResult(Arrays.asList(SimpleCommandlineProcessorImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Function.class)).map(field -> {
			field.setAccessible(true);
			return  ReflectionUtils.getField(field, null);
		
	}).collect(Collectors.toSet()));
		
	}

	
	@Before
	public final void setup() throws NoSuchMethodException, SecurityException, IllegalArgumentException, IllegalAccessException {
	
		ReflectionUtils.doWithFields(SimpleCommandlineProcessorImpl.class, field -> {field.setAccessible(true);fields.put(field.getType(), field);}, field -> Modifier.isStatic(field.getModifiers()));
		
	
		ReflectionUtils.setField(fields.get(Function.class), null, applicationContextFunction);
		
		ReflectionUtils.setField(fields.get(Method.class), null, method);
		ReflectionUtils.setField(fields.get(Object.class), null, testBean);
		
		
		
	}
	
	@Test
	public final void mainCollection()  {
		
		
		SimpleCommandlineProcessorImpl.main(ARGS);
		
		Mockito.verify(testBean).process(Arrays.asList(ARGS));
		Mockito.verify(applicationContextFunction).apply((String[]) ReflectionUtils.getField(fields.get(String[].class),null));
		
	}
	
	@Test
	public final void mainArray()  {
		
		
		ReflectionUtils.setField(fields.get(Method.class), null, ReflectionUtils.findMethod(testBean.getClass(), "process", String[].class));
		SimpleCommandlineProcessorImpl.main(ARGS);
		
		Mockito.verify(applicationContextFunction).apply((String[]) ReflectionUtils.getField(fields.get(String[].class),null));
		Mockito.verify(testBean).process(ARGS);
		
	}
	
	@Test
	public final void mainSet()  {
		
		
		ReflectionUtils.setField(fields.get(Method.class), null, ReflectionUtils.findMethod(testBean.getClass(), "process", Set.class));
		SimpleCommandlineProcessorImpl.main(ARGS);
		
		Mockito.verify(applicationContextFunction).apply((String[]) ReflectionUtils.getField(fields.get(String[].class),null));
		Mockito.verify(testBean).process(new HashSet<>(Arrays.asList(ARGS)));
		
	}
	
	@Test
	public final void main()  {
	
		
		ReflectionUtils.setField(fields.get(Method.class), null, ReflectionUtils.findMethod(testBean.getClass(), "process"));
		SimpleCommandlineProcessorImpl.main(ARGS);
		
		Mockito.verify(applicationContextFunction).apply((String[]) ReflectionUtils.getField(fields.get(String[].class),null));
		Mockito.verify(testBean).process();
		
	}
	
	
	@Test
	public final void applicationContext() {
		Assert.assertTrue(applicationContextFunctionOrg.apply(new String[] {"kylie.minogue.com"}) instanceof AnnotationConfigApplicationContext); 
	}
	
}




 interface  TestBean {
	
	void process(Collection<String> args);
	void process();
	void process(String[] args);
	void process(Set<String> args);
}