package de.mq.portfolio.support;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import java.util.function.Supplier;

import org.springframework.util.Assert;

import com.mscharhag.et.ET;
import com.mscharhag.et.ExceptionTranslator;
import com.mscharhag.et.ExceptionTranslatorConfigurer;
import com.mscharhag.et.ReturningTryBlock;
import com.mscharhag.et.TryBlock;

public class ExceptionTranslationBuilderImpl<R, T extends AutoCloseable> implements ExceptionTranslationBuilder<R, T> {

	private ReturningTryBlockWithResource<R, T> returningWithResource;

	private ReturningTryBlock<R> returningWithoutResource;

	private TryBlockWithResource<T> voidWithResource;

	private TryBlock voidWithoutResource;

	private ResourceSupplier<T> resourceSupplier;

	private final Collection<Entry<Class<? extends RuntimeException>, Class<? extends Exception>[]>> translations = new HashSet<>();

	private Type type;

	enum Type {
		ReturningWithResource(true), ReturningWithoutResource(false), VoidWithResource(true), VoidWithoutResource(false);
		private final boolean resourceNeeded;

		Type(final boolean resourceNeeded) {
			this.resourceNeeded = resourceNeeded;
		}

		boolean resourceNeeded() {
			return resourceNeeded;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.support.ExceptionTranslationBuilder#withStatement(java.
	 * util.function.Function)
	 */
	@Override
	public final ExceptionTranslationBuilder<R, T> withStatement(final ReturningTryBlockWithResource<R, T> returningWithResource) {
		statementMandatoryGuard(returningWithResource);
		typeNotAssignedGuard();
		this.returningWithResource = returningWithResource;
		this.type = Type.ReturningWithResource;
		return this;
	}

	private void typeNotAssignedGuard() {
		Assert.isNull(this.type, "Statement already assigned.");
	}

	private void statementMandatoryGuard(final Object returningWithResource) {
		Assert.notNull(returningWithResource, "Statement is mandatory.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.support.ExceptionTranslationBuilder#withStatement(java.
	 * util.function.Supplier)
	 */
	@Override
	public final ExceptionTranslationBuilder<R, T> withStatement(final ReturningTryBlock<R> returningWithoutResource) {
		statementMandatoryGuard(returningWithoutResource);
		typeNotAssignedGuard();
		this.returningWithoutResource = returningWithoutResource;
		type = Type.ReturningWithoutResource;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.support.ExceptionTranslationBuilder#withStatement(java.
	 * util.function.Consumer)
	 */
	@Override
	public final ExceptionTranslationBuilder<R, T> withStatement(final TryBlockWithResource<T> voidWithResource) {
		statementMandatoryGuard(voidWithResource);
		typeNotAssignedGuard();
		this.voidWithResource = voidWithResource;
		type = Type.VoidWithResource;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.support.ExceptionTranslationBuilder#withStatement(de.mq.
	 * portfolio.support.ExceptionTranslationBuilderImpl.Command)
	 */
	@Override
	public final ExceptionTranslationBuilder<R, T> withStatement(final TryBlock voidWithoutResource) {
		statementMandatoryGuard(voidWithoutResource);
		typeNotAssignedGuard();
		this.voidWithoutResource = voidWithoutResource;
		type = Type.VoidWithoutResource;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.support.ExceptionTranslationBuilder#withResource(java.
	 * util.function.Supplier)
	 */
	@Override
	public final ExceptionTranslationBuilder<R, T> withResource(final ResourceSupplier<T> resourceSupplier) {
		statementMandatoryGuard(resourceSupplier);
		this.resourceSupplier = resourceSupplier;
		return this;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.support.ExceptionTranslationBuilder#withTranslation(java.
	 * lang.Class, java.lang.Class)
	 */
	@Override
	public final ExceptionTranslationBuilder<R, T> withTranslation(final Class<? extends RuntimeException> targetClass, Class<? extends Exception>[] sourceClasses) {
		Assert.notNull(targetClass);
		Assert.notNull(sourceClasses);
		Assert.isTrue(sourceClasses.length > 0);
		translations.add(new AbstractMap.SimpleImmutableEntry<>(targetClass, sourceClasses));
		return this;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.support.ExceptionTranslationBuilder#translate()
	 */
	@Override
	public final R translate() {
		statementMandatoryGuard(type);
		final ExceptionTranslatorConfigurer exceptionTranslatorConfigurer = ET.newConfiguration();
		translations.forEach(entry -> exceptionTranslatorConfigurer.translate(entry.getValue()).to(entry.getKey()));
		final ExceptionTranslator exceptionTranslator = exceptionTranslatorConfigurer.done();
		return type(exceptionTranslator);
	}

	private R type(final ExceptionTranslator exceptionTranslator) {
		final HashMap<Type, Supplier<R>> types = new HashMap<>();
		types.put(Type.ReturningWithResource, () -> returningWithResource(exceptionTranslator));
		types.put(Type.ReturningWithoutResource, () -> returningWithoutResource(exceptionTranslator));
		types.put(Type.VoidWithResource, () -> voidWithResource(exceptionTranslator));
		types.put(Type.VoidWithoutResource, () -> voidWithoutResource(exceptionTranslator));
		Assert.isTrue(types.containsKey(type));
		Assert.isTrue(type.resourceNeeded() == (resourceSupplier != null), "Resource <-> Statement: mismatch.");
		return types.get(type).get();
	}

	private R voidWithoutResource(final ExceptionTranslator exceptionTranslator) {
		exceptionTranslator.withTranslation(() -> voidWithoutResource.run());
		return null;
	}

	private R voidWithResource(final ExceptionTranslator exceptionTranslator) {
		exceptionTranslator.withTranslation(() -> {
			try (final T resource = resourceSupplier.get()) {
				voidWithResource.run(resource);
			}
			;
		});
		return null;
	}

	private R returningWithoutResource(final ExceptionTranslator exceptionTranslator) {
		return exceptionTranslator.withReturningTranslation(() -> returningWithoutResource.run());
	}

	private R returningWithResource(final ExceptionTranslator exceptionTranslator) {
		return exceptionTranslator.withReturningTranslation(() -> {
			try (final T resource = resourceSupplier.get()) {
				return returningWithResource.run(resource);
			}

		});
	}

}
