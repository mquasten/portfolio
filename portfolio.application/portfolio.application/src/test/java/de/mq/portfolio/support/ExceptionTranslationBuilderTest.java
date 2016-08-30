package de.mq.portfolio.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import com.mscharhag.et.ReturningTryBlock;
import com.mscharhag.et.TryBlock;

import de.mq.portfolio.support.ExceptionTranslationBuilder.ResourceSupplier;
import de.mq.portfolio.support.ExceptionTranslationBuilder.ReturningTryBlockWithResource;
import de.mq.portfolio.support.ExceptionTranslationBuilder.TryBlockWithResource;
import junit.framework.Assert;

public class ExceptionTranslationBuilderTest {

	private static final byte[] TEXT_AS_BYTES = "Kylie is nice and ...".getBytes();
	private final ExceptionTranslationBuilder<Object, AutoCloseable> exceptionTranslationBuilder = new ExceptionTranslationBuilderImpl<>();

	@SuppressWarnings({ "unchecked" })
	private final ResourceSupplier<ByteArrayInputStream> supplier = Mockito.mock(ResourceSupplier.class);

	@SuppressWarnings({ "rawtypes" })
	private final Entry entry = Mockito.mock(Entry.class);

	@Before
	public final void setup() throws Exception {
		Mockito.when(supplier.get()).thenReturn(new ByteArrayInputStream(TEXT_AS_BYTES));
		Mockito.when(entry.getKey()).thenReturn(IllegalStateException.class);
		Mockito.when(entry.getValue()).thenReturn(Arrays.asList(IOException.class ));
	}

	@Test
	public final void withStatementReturningTryBlockWithResource() {
		@SuppressWarnings("unchecked")
		final ReturningTryBlockWithResource<Object, AutoCloseable> returningWithResource = Mockito.mock(ReturningTryBlockWithResource.class);

		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement(returningWithResource));

		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(returningWithResource, results.get(ReturningTryBlockWithResource.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.ReturningWithResource, results.get(ExceptionTranslationBuilderImpl.Type.class));

		Arrays.asList(ReturningTryBlock.class, TryBlockWithResource.class, TryBlock.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));

	}

	private final Map<Class<?>, Object> fields2Map(ExceptionTranslationBuilder<Object, AutoCloseable> exceptionTranslationBuilder) {
		final Map<Class<?>, Object> results = new HashMap<>();
		ReflectionUtils.doWithFields(exceptionTranslationBuilder.getClass(), field -> results.put(field.getType(), ReflectionTestUtils.getField(exceptionTranslationBuilder, field.getName())), field -> !Modifier.isStatic(field.getModifiers()));
		return results;
	}

	@Test
	public final void withStatementReturningTryBlock() {
		@SuppressWarnings("unchecked")
		final ReturningTryBlock<Object> returningWithoutResource = Mockito.mock(ReturningTryBlock.class);

		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement(returningWithoutResource));

		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(returningWithoutResource, results.get(ReturningTryBlock.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.ReturningWithoutResource, results.get(ExceptionTranslationBuilderImpl.Type.class));

		Arrays.asList(ReturningTryBlockWithResource.class, TryBlockWithResource.class, TryBlock.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));

	}

	@Test
	public final void withStatementTryBlockWithResource() {
		@SuppressWarnings("unchecked")
		final TryBlockWithResource<AutoCloseable> tryBlockWithResource = Mockito.mock(TryBlockWithResource.class);

		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement(tryBlockWithResource));

		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(tryBlockWithResource, results.get(TryBlockWithResource.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.VoidWithResource, results.get(ExceptionTranslationBuilderImpl.Type.class));

		Arrays.asList(ReturningTryBlockWithResource.class, ReturningTryBlock.class, TryBlock.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));

	}

	@Test
	public final void withStatementTryBlock() {

		final TryBlock tryBlock = Mockito.mock(TryBlock.class);

		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withStatement(tryBlock));

		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(tryBlock, results.get(TryBlock.class));
		Assert.assertEquals(ExceptionTranslationBuilderImpl.Type.VoidWithoutResource, results.get(ExceptionTranslationBuilderImpl.Type.class));

		Arrays.asList(ReturningTryBlockWithResource.class, ReturningTryBlock.class, TryBlockWithResource.class).forEach(clazz -> Assert.assertNull(results.get(clazz)));

	}

	@Test
	public final void withResource() {
		@SuppressWarnings("unchecked")
		final ResourceSupplier<AutoCloseable> supplier = Mockito.mock(ResourceSupplier.class);
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withResource(supplier));
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(supplier, results.get(ResourceSupplier.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public final void withTranslation() {
		Assert.assertEquals(exceptionTranslationBuilder, exceptionTranslationBuilder.withTranslation(IllegalStateException.class, Arrays.asList(IOException.class) ));
		final Map<Class<?>, Object> results = fields2Map(exceptionTranslationBuilder);
		Assert.assertEquals(1, ((Collection<?>) results.get(Collection.class)).size());
		final Optional<Entry<Class<?>, Collection<?>>> result = (Optional<Entry<Class<?>, Collection<?>>>) ((Collection<?>) results.get(Collection.class)).stream().findAny();
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(IllegalStateException.class, result.get().getKey());
		Assert.assertEquals(1, result.get().getValue().size());
		Assert.assertEquals(IOException.class, result.get().getValue().stream().findAny().get());

	}

	@Test(expected = IllegalArgumentException.class)
	public final void withTranslationEmptySourceArray() {
		exceptionTranslationBuilder.withTranslation(IllegalStateException.class, new ArrayList<>());
	}

	@Test
	public final void translateReturningTryBlockWithResource() {
		final Map<Class<?>, Field> fields = fields();
		ReturningTryBlockWithResource<Object, ByteArrayInputStream> returningTryBlockWithResource = bis -> {
			final byte[] buffer = new byte[TEXT_AS_BYTES.length];
			bis.read(buffer);
			return new String(buffer);
		};

		ReflectionUtils.setField(fields.get(ResourceSupplier.class), exceptionTranslationBuilder, supplier);
		ReflectionUtils.setField(fields.get(ReturningTryBlockWithResource.class), exceptionTranslationBuilder, returningTryBlockWithResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithResource);
		Assert.assertEquals(new String(TEXT_AS_BYTES), exceptionTranslationBuilder.translate());

	}

	private Map<Class<?>, Field> fields() {
		final Map<Class<?>, Field> fields = new HashMap<>();
		ReflectionUtils.doWithFields(exceptionTranslationBuilder.getClass(), field -> {
			field.setAccessible(true);
			fields.put(field.getType(), field);
		}, field -> !Modifier.isStatic(field.getModifiers()));
		return fields;
	}

	@Test(expected = IllegalStateException.class)
	public final void translateReturningTryBlockWithResourceSupplierSucks() throws Exception  {

		final ReturningTryBlockWithResource<?, ?> returningTryBlockWithResource = Mockito.mock(ReturningTryBlockWithResource.class);

		final ResourceSupplier<?> supplier = Mockito.mock(ResourceSupplier.class);
		Mockito.doThrow(IOException.class).when(supplier).get();
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(ResourceSupplier.class), exceptionTranslationBuilder, supplier);
		ReflectionUtils.setField(fields.get(ReturningTryBlockWithResource.class), exceptionTranslationBuilder, returningTryBlockWithResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithResource);
		ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));
		exceptionTranslationBuilder.translate();

	}

	@Test(expected = IllegalStateException.class)
	public final void translateReturningTryBlockWithResourceBlockSucks() {

		ReturningTryBlockWithResource<Object, ByteArrayInputStream> returningTryBlockWithResource = bis -> {
			throw new IOException("Don't worry, only for test");
		};

		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(ResourceSupplier.class), exceptionTranslationBuilder, supplier);
		ReflectionUtils.setField(fields.get(ReturningTryBlockWithResource.class), exceptionTranslationBuilder, returningTryBlockWithResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithResource);
		ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));
		exceptionTranslationBuilder.translate();
	}

	@Test(expected = IllegalArgumentException.class)
	public final void translateResourceStatementMissmatch() {
		ReturningTryBlockWithResource<Object, ByteArrayInputStream> returningTryBlockWithResource = bis -> new String(TEXT_AS_BYTES);
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(ReturningTryBlockWithResource.class), exceptionTranslationBuilder, returningTryBlockWithResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithResource);
		ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));
		exceptionTranslationBuilder.translate();
	}

	@Test
	public final void translateReturningWithoutResource() {

		final ReturningTryBlock<String> returningTryBlock = () -> new String(TEXT_AS_BYTES);
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(ReturningTryBlock.class), exceptionTranslationBuilder, returningTryBlock);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithoutResource);
		Assert.assertEquals(new String(TEXT_AS_BYTES), exceptionTranslationBuilder.translate());

	}

	@Test(expected = IllegalStateException.class)
	public final void translateReturningWithoutResourceSucks() {
		final ReturningTryBlock<String> returningTryBlock = () -> {
			throw new IOException("Don't worry, only for test");
		};
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(ReturningTryBlock.class), exceptionTranslationBuilder, returningTryBlock);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithoutResource);
		ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));
		Assert.assertEquals(new String(TEXT_AS_BYTES), exceptionTranslationBuilder.translate());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void translateReturningWithoutResourceStatementMissmatch() {

		final ReturningTryBlock<?> returningTryBlock = Mockito.mock(ReturningTryBlock.class);

		final ResourceSupplier<?> supplier = Mockito.mock(ResourceSupplier.class);
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(ReturningTryBlock.class), exceptionTranslationBuilder, returningTryBlock);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.ReturningWithoutResource);
		ReflectionUtils.setField(fields.get(ResourceSupplier.class), exceptionTranslationBuilder, supplier);
		exceptionTranslationBuilder.translate();
	}

	@Test
	public final void translateVoidWithResource() {
		final Collection<String> results = new ArrayList<>();
		final TryBlockWithResource<ByteArrayInputStream> tryBlockWithResource = is -> {
			byte[] buffer = new byte[TEXT_AS_BYTES.length];
			is.read(buffer);
			results.add(new String(buffer));
		};
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(TryBlockWithResource.class), exceptionTranslationBuilder, tryBlockWithResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.VoidWithResource);
		ReflectionUtils.setField(fields.get(ResourceSupplier.class), exceptionTranslationBuilder, supplier);
		exceptionTranslationBuilder.translate();
		Assert.assertEquals(1, results.size());
		Assert.assertTrue(results.stream().findAny().isPresent());
		Assert.assertEquals(Optional.of(new String(TEXT_AS_BYTES)), results.stream().findAny());
	}

	@Test(expected = IllegalStateException.class)
	public final void translateVoidWithResourceSupplierSucks() throws Exception {

		final TryBlockWithResource<?> tryBlockWithResource = Mockito.mock(TryBlockWithResource.class);

		Mockito.doThrow(IOException.class).when(supplier).get();
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(TryBlockWithResource.class), exceptionTranslationBuilder, tryBlockWithResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.VoidWithResource);
		ReflectionUtils.setField(fields.get(ResourceSupplier.class), exceptionTranslationBuilder, supplier);
		ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));
		exceptionTranslationBuilder.translate();

	}

	@Test(expected = IllegalStateException.class)
	public final void translateVoidWithResourceBlockSucks() {
		final TryBlockWithResource<?> tryBlockWithResource = is -> {
			throw new IOException("Don't worry, only for test");
		};
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(TryBlockWithResource.class), exceptionTranslationBuilder, tryBlockWithResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.VoidWithResource);
		ReflectionUtils.setField(fields.get(ResourceSupplier.class), exceptionTranslationBuilder, supplier);
		ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));

		exceptionTranslationBuilder.translate();
	}

	@Test(expected = IllegalArgumentException.class)
	public final void translateVoidWithResourceStatementMissmatch() {
		final TryBlockWithResource<ByteArrayInputStream> tryBlockWithResource = is -> {
		};
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(TryBlockWithResource.class), exceptionTranslationBuilder, tryBlockWithResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.VoidWithResource);

		exceptionTranslationBuilder.translate();
	}

	@Test
	public final void translateVoidWithoutResource() {
		final Collection<String> results = new ArrayList<>();
		final TryBlock tryBlockWithoutResource = () -> results.add(new String(TEXT_AS_BYTES));
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(TryBlock.class), exceptionTranslationBuilder, tryBlockWithoutResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.VoidWithoutResource);

		exceptionTranslationBuilder.translate();
		Assert.assertEquals(1, results.size());
		Assert.assertTrue(results.stream().findAny().isPresent());
		Assert.assertEquals(Optional.of(new String(TEXT_AS_BYTES)), results.stream().findAny());
	}

	@Test(expected = IllegalStateException.class)
	public final void translateVoidWithoutResourceBlockSucks() {
		final TryBlock tryBlockWithoutResource = () -> {
			throw new IOException("Don't worry, only for test");
		};
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(TryBlock.class), exceptionTranslationBuilder, tryBlockWithoutResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.VoidWithoutResource);
		ReflectionUtils.setField(fields.get(Collection.class), exceptionTranslationBuilder, Arrays.asList(entry));
		exceptionTranslationBuilder.translate();
	}

	@Test(expected = IllegalArgumentException.class)
	public final void translateVoidWithoutResourceStatementMissmatch() {

		final TryBlock tryBlockWithoutResource = () -> {
		};
		final Map<Class<?>, Field> fields = fields();
		ReflectionUtils.setField(fields.get(TryBlock.class), exceptionTranslationBuilder, tryBlockWithoutResource);
		ReflectionUtils.setField(fields.get(ExceptionTranslationBuilderImpl.Type.class), exceptionTranslationBuilder, ExceptionTranslationBuilderImpl.Type.VoidWithoutResource);
		ReflectionUtils.setField(fields.get(ResourceSupplier.class), exceptionTranslationBuilder, supplier);
		exceptionTranslationBuilder.translate();

	}

	@Test
	public final void type() {
		Assert.assertEquals(4, ExceptionTranslationBuilderImpl.Type.values().length);
		Arrays.asList(ExceptionTranslationBuilderImpl.Type.values()).forEach(value -> Assert.assertEquals(value, ExceptionTranslationBuilderImpl.Type.valueOf(value.name())));
		Collection<ExceptionTranslationBuilderImpl.Type> results = new HashSet<>();
		results.addAll(Arrays.asList(ExceptionTranslationBuilderImpl.Type.values()).stream().filter(value -> value.resourceNeeded()).collect(Collectors.toSet()));
		Assert.assertEquals(2, results.size());
		Assert.assertTrue(results.containsAll(Arrays.asList(ExceptionTranslationBuilderImpl.Type.ReturningWithResource, ExceptionTranslationBuilderImpl.Type.VoidWithResource)));
		results.clear();

		results.addAll(Arrays.asList(ExceptionTranslationBuilderImpl.Type.values()).stream().filter(value -> !value.resourceNeeded()).collect(Collectors.toSet()));
		Assert.assertEquals(2, results.size());
		Assert.assertTrue(results.containsAll(Arrays.asList(ExceptionTranslationBuilderImpl.Type.ReturningWithoutResource, ExceptionTranslationBuilderImpl.Type.VoidWithoutResource)));

	}

}
