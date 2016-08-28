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
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.support.ExceptionTranslationBuilder.ResourceSupplier;
import de.mq.portfolio.support.SimpleCommandlineProcessorImpl.Main;
import junit.framework.Assert;


public class CommandlineProcessorTest {

	private static final String[] ARGS = new String[] { "Kylie", "is", "nice" };
	private final Map<Class<?>, Field> fields = new HashMap<>();
	private TestBean testBean = Mockito.mock(TestBean.class);
	private Method method = ReflectionUtils.findMethod(testBean.getClass(), "process", Collection.class);
	@SuppressWarnings("unchecked")
	private final ResourceSupplier<ConfigurableApplicationContext> resourceSupplier = Mockito.mock(ResourceSupplier.class);

	private static ResourceSupplier<ConfigurableApplicationContext> applicationContextSupplierOrg;

	private final ApplicationContextAware applicationContextAware = new SimpleCommandlineProcessorImpl(TestBean.class);

	private final ConfigurableApplicationContext applicationContext = Mockito.mock(ConfigurableApplicationContext.class);

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() {
		
		CommandlineProcessorTest.applicationContextSupplierOrg = (ResourceSupplier<ConfigurableApplicationContext>) DataAccessUtils.requiredSingleResult(Arrays.asList(SimpleCommandlineProcessorImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(ResourceSupplier.class)).map(field -> {
			field.setAccessible(true);
			return ReflectionUtils.getField(field, null);

		}).collect(Collectors.toSet()));

	}

	@Before
	public final void setup() throws Exception {

		Mockito.when(resourceSupplier.get()).thenReturn(applicationContext);
		ReflectionUtils.doWithFields(SimpleCommandlineProcessorImpl.class, field -> {
			field.setAccessible(true);
			fields.put(field.getType(), field);
		}, field -> Modifier.isStatic(field.getModifiers()));

		ReflectionUtils.setField(fields.get(ResourceSupplier.class), null, resourceSupplier);

		ReflectionUtils.setField(fields.get(Method.class), null, method);
		ReflectionUtils.setField(fields.get(Object.class), null, testBean);
		
		ReflectionUtils.setField(fields.get(String[].class), null, new String[] {" "});

	}

	@Test
	public final void mainCollection() throws Exception {

		SimpleCommandlineProcessorImpl.main(ARGS);

		Mockito.verify(testBean).process(Arrays.asList(ARGS));
		Mockito.verify(resourceSupplier).get();
		Mockito.verify(applicationContext).close();

	}

	@Test
	public final void mainArray() throws Exception {

		ReflectionUtils.setField(fields.get(Method.class), null, ReflectionUtils.findMethod(testBean.getClass(), "process", String[].class));
		SimpleCommandlineProcessorImpl.main(ARGS);

		Mockito.verify(resourceSupplier).get();
		Mockito.verify(testBean).process(ARGS);
		Mockito.verify(applicationContext).close();

	}

	@Test
	public final void mainSet() throws Exception {

		ReflectionUtils.setField(fields.get(Method.class), null, ReflectionUtils.findMethod(testBean.getClass(), "process", Set.class));
		SimpleCommandlineProcessorImpl.main(ARGS);

		Mockito.verify(resourceSupplier).get();
		Mockito.verify(testBean).process(new HashSet<>(Arrays.asList(ARGS)));
		Mockito.verify(applicationContext).close();

	}

	@Test
	public final void main() throws Exception {
		ReflectionUtils.setField(fields.get(Method.class), null, ReflectionUtils.findMethod(testBean.getClass(), "process"));
		SimpleCommandlineProcessorImpl.main(ARGS);

		Mockito.verify(resourceSupplier).get();
		Mockito.verify(testBean).process();
		Mockito.verify(applicationContext).close();

	}

	@Test
	public final void applicationContext() throws Exception {
		
		
		
		Assert.assertTrue(applicationContextSupplierOrg.get() instanceof AnnotationConfigApplicationContext);
	}

	@Test
	public final void setApplicationContext() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		Mockito.when(applicationContext.getBean(TestBean.class)).thenReturn(testBean);

		applicationContextAware.setApplicationContext(applicationContext);

		Mockito.verify(applicationContext).getBean(TestBean.class);

		Assert.assertEquals(ReflectionUtils.findMethod(TestBean.class, "process", Collection.class), ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertEquals(testBean, ReflectionUtils.getField(fields.get(Object.class), null));
	}

	@Test
	public final void setApplicationContextArray() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		TestArray testBean = Mockito.mock(TestArray.class);
		Mockito.when(applicationContext.getBean(TestArray.class)).thenReturn(testBean);

		ReflectionUtils.doWithFields(applicationContextAware.getClass(), field -> ReflectionTestUtils.setField(applicationContextAware, field.getName(), TestArray.class), field -> field.getType().equals(Class.class));

		applicationContextAware.setApplicationContext(applicationContext);

		Mockito.verify(applicationContext).getBean(TestArray.class);

		Assert.assertEquals(ReflectionUtils.findMethod(TestArray.class, "process", String[].class), ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertEquals(testBean, ReflectionUtils.getField(fields.get(Object.class), null));
	}

	@Test
	public final void setApplicationContextWithoutArg() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		TestWithoutArgs testBean = Mockito.mock(TestWithoutArgs.class);
		Mockito.when(applicationContext.getBean(TestWithoutArgs.class)).thenReturn(testBean);

		ReflectionUtils.doWithFields(applicationContextAware.getClass(), field -> ReflectionTestUtils.setField(applicationContextAware, field.getName(), TestWithoutArgs.class), field -> field.getType().equals(Class.class));

		applicationContextAware.setApplicationContext(applicationContext);

		Mockito.verify(applicationContext).getBean(TestWithoutArgs.class);

		Assert.assertEquals(ReflectionUtils.findMethod(TestWithoutArgs.class, "process"), ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertEquals(testBean, ReflectionUtils.getField(fields.get(Object.class), null));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void setApplicationContextWrongArg() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		TestWrongArg testBean = Mockito.mock(TestWrongArg.class);
		Mockito.when(applicationContext.getBean(TestWrongArg.class)).thenReturn(testBean);

		ReflectionUtils.doWithFields(applicationContextAware.getClass(), field -> ReflectionTestUtils.setField(applicationContextAware, field.getName(), TestWrongArg.class), field -> field.getType().equals(Class.class));

		applicationContextAware.setApplicationContext(applicationContext);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void setApplicationContextAnnotationNotFound() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		Function<?, ?> testBean = Mockito.mock(Function.class);
		Mockito.when(applicationContext.getBean(Function.class)).thenReturn(testBean);

		ReflectionUtils.doWithFields(applicationContextAware.getClass(), field -> ReflectionTestUtils.setField(applicationContextAware, field.getName(), Function.class), field -> field.getType().equals(Class.class));

		applicationContextAware.setApplicationContext(applicationContext);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void setApplicationContextToMutchArgs() {
		ReflectionUtils.setField(fields.get(Method.class), null, null);
		ReflectionUtils.setField(fields.get(Object.class), null, null);

		Assert.assertNull(ReflectionUtils.getField(fields.get(Method.class), null));
		Assert.assertNull(ReflectionUtils.getField(fields.get(Object.class), null));

		TestToMutchArgs testBean = Mockito.mock(TestToMutchArgs.class);
		Mockito.when(applicationContext.getBean(TestToMutchArgs.class)).thenReturn(testBean);

		ReflectionUtils.doWithFields(applicationContextAware.getClass(), field -> ReflectionTestUtils.setField(applicationContextAware, field.getName(), TestToMutchArgs.class), field -> field.getType().equals(Class.class));

		applicationContextAware.setApplicationContext(applicationContext);

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
