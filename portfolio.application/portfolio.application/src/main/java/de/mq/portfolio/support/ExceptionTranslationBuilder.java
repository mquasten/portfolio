package de.mq.portfolio.support;


import java.util.Collection;

import com.mscharhag.et.ReturningTryBlock;
import com.mscharhag.et.TryBlock;

public interface ExceptionTranslationBuilder<R, T extends AutoCloseable> {

	ExceptionTranslationBuilder<R, T> withStatement(final ReturningTryBlockWithResource<R, T> returningWithResource);

	ExceptionTranslationBuilder<R, T> withStatement(final ReturningTryBlock<R> returningWithoutResource);

	ExceptionTranslationBuilder<R, T> withStatement(final TryBlockWithResource<T> voidWithResource);

	ExceptionTranslationBuilder<R, T> withStatement(final TryBlock voidWithoutResource);

	ExceptionTranslationBuilder<R, T> withResource(final ResourceSupplier<T> resourceSupplier);

	ExceptionTranslationBuilder<R, T> withTranslation(final Class<? extends RuntimeException> targetClass, final Collection<Class<? extends Exception>>  sourceClasses );

	R translate();



	@FunctionalInterface
	public interface ReturningTryBlockWithResource<R, T extends AutoCloseable> {
		R run(final T resource) throws Exception;
	}

	@FunctionalInterface
	public interface TryBlockWithResource<T extends AutoCloseable> {
		void run(final T resource) throws Exception;
	}
	
	@FunctionalInterface
	public interface ResourceSupplier<T extends AutoCloseable> {
		T get() throws Exception; 
	}


}


