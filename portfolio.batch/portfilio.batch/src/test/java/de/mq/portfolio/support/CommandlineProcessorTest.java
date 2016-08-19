package de.mq.portfolio.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.support.SimpleCommandlineProcessorImpl.Main;
import junit.framework.Assert;

public class CommandlineProcessorTest {

	private static final String[] ARGS = new String[] { "Kylie", "is", "nice" };
	private final Map<Class<?>, Field> fields = new HashMap<>();
	private TestBean testBean = Mockito.mock(TestBean.class);
	private Method method = ReflectionUtils.findMethod(testBean.getClass(), "process", Collection.class);
	@SuppressWarnings("unchecked")
	private final Function<String[], ApplicationContext> applicationContextFunction = Mockito.mock(Function.class);

	private static Function<String[], ApplicationContext> applicationContextFunctionOrg;

	private final BeanFactoryPostProcessor beanFactoryPostProcessor = new SimpleCommandlineProcessorImpl(TestBean.class);

	private final ConfigurableListableBeanFactory beanFactory = Mockito.mock(ConfigurableListableBeanFactory.class);

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() {
		CommandlineProcessorTest.applicationContextFunctionOrg = (Function<String[], ApplicationContext>) DataAccessUtils.requiredSingleResult(Arrays.asList(SimpleCommandlineProcessorImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Function.class)).map(field -> {
			field.setAccessible(true);
			return ReflectionUtils.getField(field, null);

		}).collect(Collectors.toSet()));

	}

	@Before
	public final void setup() throws NoSuchMethodException, SecurityException, IllegalArgumentException, IllegalAccessException {

		ReflectionUtils.doWithFields(SimpleCommandlineProcessorImpl.class, field -> {
			field.setAccessible(true);
			fields.put(field.getType(), field);
		}, field -> Modifier.isStatic(field.getModifiers()));

		ReflectionUtils.setField(fields.get(Function.class), null, applicationContextFunction);

		ReflectionUtils.setField(fields.get(Method.class), null, method);
		ReflectionUtils.setField(fields.get(Object.class), null, testBean);

	}

	@Test
	public final void mainCollection() {

		SimpleCommandlineProcessorImpl.main(ARGS);

		Mockito.verify(testBean).process(Arrays.asList(ARGS));
		Mockito.verify(applicationContextFunction).apply((String[]) ReflectionUtils.getField(fields.get(String[].class), null));

	}

	@Test
	public final void mainArray() {

		ReflectionUtils.setField(fields.get(Method.class), null, ReflectionUtils.findMethod(testBean.getClass(), "process", String[].class));
		SimpleCommandlineProcessorImpl.main(ARGS);

		Mockito.verify(applicationContextFunction).apply((String[]) ReflectionUtils.getField(fields.get(String[].class), null));
		Mockito.verify(testBean).process(ARGS);

	}

	@Test
	public final void mainSet() {

		ReflectionUtils.setField(fields.get(Method.class), null, ReflectionUtils.findMethod(testBean.getClass(), "process", Set.class));
		SimpleCommandlineProcessorImpl.main(ARGS);

		Mockito.verify(applicationContextFunction).apply((String[]) ReflectionUtils.getField(fields.get(String[].class), null));
		Mockito.verify(testBean).process(new HashSet<>(Arrays.asList(ARGS)));

	}

	@Test
	public final void main() {

		ReflectionUtils.setField(fields.get(Method.class), null, ReflectionUtils.findMethod(testBean.getClass(), "process"));
		SimpleCommandlineProcessorImpl.main(ARGS);

		Mockito.verify(applicationContextFunction).apply((String[]) ReflectionUtils.getField(fields.get(String[].class), null));
		Mockito.verify(testBean).process();

	}

	@Test
	public final void applicationContext() {
		Assert.assertTrue(applicationContextFunctionOrg.apply(new String[] { "kylie.minogue.com" }) instanceof AnnotationConfigApplicationContext);
	}

	@Test
	public final void postProcessBeanFactory() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		Mockito.when(beanFactory.getBean(TestBean.class)).thenReturn(testBean);

		beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

		Mockito.verify(beanFactory).getBean(TestBean.class);

		Assert.assertEquals(ReflectionUtils.findMethod(TestBean.class, "process", Collection.class), ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertEquals(testBean, ReflectionUtils.getField(fields.get(Object.class), null));
	}

	@Test
	public final void postProcessBeanFactoryArray() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		TestArray testBean = Mockito.mock(TestArray.class);
		Mockito.when(beanFactory.getBean(TestArray.class)).thenReturn(testBean);

		ReflectionUtils.doWithFields(beanFactoryPostProcessor.getClass(), field -> ReflectionTestUtils.setField(beanFactoryPostProcessor, field.getName(), TestArray.class), field -> field.getType().equals(Class.class));

		beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

		Mockito.verify(beanFactory).getBean(TestArray.class);

		Assert.assertEquals(ReflectionUtils.findMethod(TestArray.class, "process", String[].class), ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertEquals(testBean, ReflectionUtils.getField(fields.get(Object.class), null));
	}

	@Test
	public final void postProcessBeanFactoryWithoutArg() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		TestWithoutArgs testBean = Mockito.mock(TestWithoutArgs.class);
		Mockito.when(beanFactory.getBean(TestWithoutArgs.class)).thenReturn(testBean);

		ReflectionUtils.doWithFields(beanFactoryPostProcessor.getClass(), field -> ReflectionTestUtils.setField(beanFactoryPostProcessor, field.getName(), TestWithoutArgs.class), field -> field.getType().equals(Class.class));

		beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

		Mockito.verify(beanFactory).getBean(TestWithoutArgs.class);

		Assert.assertEquals(ReflectionUtils.findMethod(TestWithoutArgs.class, "process"), ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertEquals(testBean, ReflectionUtils.getField(fields.get(Object.class), null));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void postProcessBeanFactoryWrongArg() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		TestWrongArg testBean = Mockito.mock(TestWrongArg.class);
		Mockito.when(beanFactory.getBean(TestWrongArg.class)).thenReturn(testBean);

		ReflectionUtils.doWithFields(beanFactoryPostProcessor.getClass(), field -> ReflectionTestUtils.setField(beanFactoryPostProcessor, field.getName(), TestWrongArg.class), field -> field.getType().equals(Class.class));

		beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void postProcessBeanFactoryAnnotationNotFound() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		Function<?, ?> testBean = Mockito.mock(Function.class);
		Mockito.when(beanFactory.getBean(Function.class)).thenReturn(testBean);

		ReflectionUtils.doWithFields(beanFactoryPostProcessor.getClass(), field -> ReflectionTestUtils.setField(beanFactoryPostProcessor, field.getName(), Function.class), field -> field.getType().equals(Class.class));

		beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void postProcessBeanFactoryToMutchArgs() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		TestToMutchArgs testBean = Mockito.mock(TestToMutchArgs.class);
		Mockito.when(beanFactory.getBean(TestToMutchArgs.class)).thenReturn(testBean);

		ReflectionUtils.doWithFields(beanFactoryPostProcessor.getClass(), field -> ReflectionTestUtils.setField(beanFactoryPostProcessor, field.getName(), TestToMutchArgs.class), field -> field.getType().equals(Class.class));

		beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

	}

}

interface TestBean {
	@Main
	void process(Collection<String> args);

	void process();

	void process(String[] args);

	void process(Set<String> args);
}

interface TestArray {

	@Main
	void process(String[] args);

}

interface TestWithoutArgs {

	@Main
	void process();

}

interface TestWrongArg {

	@Main
	void process(final Date date);

}

interface TestToMutchArgs {

	@Main
	void process(final Collection<String> args, final Object diNotSupported);

}
