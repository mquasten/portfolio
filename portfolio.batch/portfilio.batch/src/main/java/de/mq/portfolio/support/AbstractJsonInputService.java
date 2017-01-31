package de.mq.portfolio.support;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
abstract class AbstractJsonInputService {
	static final String LINE_SEPARATOR = "line.separator";

	final String lineSeparator = System.getProperty(LINE_SEPARATOR);

	@Lookup
	abstract ExceptionTranslationBuilder<Void, BufferedReader> exceptionTranslationBuilder();

	public final Collection<String> read(final String filenames) {
		Assert.notNull(filenames, "Filenames is mandatory.");
		final Collection<String> results = new ArrayList<>();
		Arrays.asList(filenames.split("[,;:|]")).forEach(filename -> {
			final StringBuilder builder = new StringBuilder();
			exceptionTranslationBuilder().withResource(() -> new BufferedReader(new FileReader(filename))).withStatement(reader -> {
				reader.lines().forEach(line -> builder.append(line + lineSeparator));
			}).translate();
			results.add(builder.toString().replaceAll("\"_id\".*[,]", ""));
		});

		return results;

	}

}
