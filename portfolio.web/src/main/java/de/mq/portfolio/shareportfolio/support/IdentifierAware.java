package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;

public interface IdentifierAware<T> {

	@SuppressWarnings("unchecked")
	default Optional<T> id() {
		return Optional.ofNullable((T) ReflectionUtils.getField(Arrays.asList(getClass().getDeclaredFields()).stream().filter(field -> field.isAnnotationPresent(Id.class)).map(field -> {field.setAccessible(true); return field;}).findAny().orElseThrow(() -> new IllegalStateException("Id field not found.")), this));
	}

}
