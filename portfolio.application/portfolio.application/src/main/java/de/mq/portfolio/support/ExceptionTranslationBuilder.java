package de.mq.portfolio.support;

import java.util.function.Supplier;

import com.mscharhag.et.ReturningTryBlock;
import com.mscharhag.et.TryBlock;

import de.mq.portfolio.support.ExceptionTranslationBuilderImpl.ReturningTryBlockWithResource;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl.TryBlockWithResource;

public interface ExceptionTranslationBuilder<R, T extends AutoCloseable> {

	ExceptionTranslationBuilder<R, T> withStatement(final ReturningTryBlockWithResource<R, T> returningWithResource);

	ExceptionTranslationBuilder<R, T> withStatement(final ReturningTryBlock<R> returningWithoutResource);

	ExceptionTranslationBuilder<R, T> withStatement(final TryBlockWithResource<T> voidWithResource);

	ExceptionTranslationBuilder<R, T> withStatement(final TryBlock voidWithoutResource);

	ExceptionTranslationBuilder<R, T> withResource(final Supplier<T> resourceSupplier);

	ExceptionTranslationBuilder<R, T> withTranslation(final Class<? extends RuntimeException> targetClass, final Class<? extends Exception>[] sourceClasses);

	R translate();

}