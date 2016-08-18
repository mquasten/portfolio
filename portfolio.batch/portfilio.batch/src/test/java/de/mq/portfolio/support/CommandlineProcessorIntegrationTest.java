package de.mq.portfolio.support;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.ReflectionUtils;





public class CommandlineProcessorIntegrationTest {

	private static final String[] ARGS = new String[] {"Kylie", "is" , "nice"};
	private final Map<Class<?>, Field> fields = new HashMap<>();
	private TestBean testBean = Mockito.mock(TestBean.class);
	private Method method = ReflectionUtils.findMethod(testBean.getClass(), "process", Collection.class);
	

	
	@Before
	public final void setup() throws NoSuchMethodException, SecurityException {
		ReflectionUtils.doWithFields(SimpleCommandlineProcessorImpl.class, field -> {field.setAccessible(true);fields.put(field.getType(), field);}, field -> Modifier.isStatic(field.getModifiers()));
		final String[] packages = (String[]) ReflectionUtils.getField(fields.get(String[].class),null);
		IntStream.range(0, packages.length).forEach(i -> packages[i]=" ");
		
		ReflectionUtils.setField(fields.get(Method.class), null, method);
		ReflectionUtils.setField(fields.get(Object.class), null, testBean);
	}
	
	@Test
	public final void main()  {
		
		SimpleCommandlineProcessorImpl.main(ARGS);
		
		Mockito.verify(testBean).process(Arrays.asList(ARGS));
		
	}
	
}




 interface  TestBean {
	
	void process(Collection<String> args);
	
	
}