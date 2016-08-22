package de.mq.portfolio.support;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import com.mscharhag.et.ReturningTryBlock;
import com.mscharhag.et.TryBlock;

import de.mq.portfolio.support.ExceptionTranslationBuilderImpl.ReturningTryBlockWithResource;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl.TryBlockWithResource;
import junit.framework.Assert;

public class ExceptionTranslationBuilderTest {
	
	private final ExceptionTranslationBuilder<Object,AutoCloseable> exceptionTranslationBuilder = new  ExceptionTranslationBuilderImpl<>();
	
	@Test
	public final void withStatementReturningTryBlockWithResource() {
		@SuppressWarnings("unchecked")
		final ReturningTryBlockWithResource<Object,AutoCloseable> returningWithResource = Mockito.mock(ReturningTryBlockWithResource.class);
		
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement( returningWithResource));
		
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(returningWithResource, results.get(ReturningTryBlockWithResource.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.ReturningWithResource, results.get(ExceptionTranslationBuilderImpl.Type.class));
		
		Arrays.asList(ReturningTryBlock.class, TryBlockWithResource.class, TryBlock.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));
		
	}

	private final Map<Class<?>, Object>  fields2Map(ExceptionTranslationBuilder<Object,AutoCloseable> exceptionTranslationBuilder) {
		final Map<Class<?>, Object> results = new HashMap<>();
		ReflectionUtils.doWithFields(exceptionTranslationBuilder.getClass(), field -> results.put(field.getType(), ReflectionTestUtils.getField(exceptionTranslationBuilder, field.getName())), field -> ! Modifier.isStatic(field.getModifiers()));
		return results;
	}
	
	
	
	@Test
	public final void withStatementReturningTryBlock() {
		@SuppressWarnings("unchecked")
		final ReturningTryBlock<Object> returningWithoutResource = Mockito.mock(ReturningTryBlock.class);
		
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement( returningWithoutResource));
		
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(returningWithoutResource, results.get(ReturningTryBlock.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.ReturningWithoutResource, results.get(ExceptionTranslationBuilderImpl.Type.class));
		
		Arrays.asList(ReturningTryBlockWithResource.class, TryBlockWithResource.class, TryBlock.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));
		
	}

	@Test
	public final void withStatementTryBlockWithResource() {
		@SuppressWarnings("unchecked")
		final TryBlockWithResource<AutoCloseable> tryBlockWithResource = Mockito.mock(TryBlockWithResource.class);
		
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement( tryBlockWithResource));
		
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(tryBlockWithResource, results.get(TryBlockWithResource.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.VoidWithResource, results.get(ExceptionTranslationBuilderImpl.Type.class));
		
		Arrays.asList(ReturningTryBlockWithResource.class, ReturningTryBlock.class, TryBlock.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));
		
	}
	
	@Test
	public final void withStatementTryBlock() {
	
		final TryBlock tryBlock = Mockito.mock(TryBlock.class);
		
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement( tryBlock));
		
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(tryBlock, results.get(TryBlock.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.VoidWithoutResource, results.get(ExceptionTranslationBuilderImpl.Type.class));
		
		Arrays.asList(ReturningTryBlockWithResource.class, ReturningTryBlock.class, TryBlockWithResource.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));
		
	}
	
	 

	@Test
	public final void  withResource() {
		@SuppressWarnings("unchecked")
		final Supplier<AutoCloseable> supplier = Mockito.mock(Supplier.class);
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withResource(supplier));
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(supplier, results.get(Supplier.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public final void  withTranslation() {
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withTranslation(IllegalStateException.class, new Class[]{IOException.class}));
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(1, ((Collection<?>)results.get(Collection.class)).size());
		final Optional<Entry<Class<?>, Class<?>[]>> result = (Optional<Entry<Class<?>, Class<?>[]>>) ((Collection<?>)results.get(Collection.class)).stream().findAny();
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(IllegalStateException.class, result.get().getKey());
		Assert.assertEquals(1,  result.get().getValue().length);
		Assert.assertEquals(IOException.class,  result.get().getValue()[0]);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=IllegalArgumentException.class)
	public final void  withTranslationEmptySourceArray() {
		exceptionTranslationBuilder.withTranslation(IllegalStateException.class, new Class[]{});
	}
	
}
