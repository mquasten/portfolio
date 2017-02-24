package de.mq.portfolio.support;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/stockcodes-test.xml" })
@Ignore
public class MigrateSareCsvFileTest {

	private static final String CSV_FILE_NAME = "data/stocks.csv.bak";
	private static final String DELIMITER = ";";
	@Value("#{stocks}")
	private Map<String, String> stocks;

	@Test
	public final void createNewFile() throws IOException {
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(CSV_FILE_NAME), Charset.forName("UTF-8")))) {
			process(reader);

		}

	}

	private void process(final BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			migrateLine( line.split(DELIMITER));
		}
	}

	private void migrateLine(final String[] cols) {
		final StringBuffer buffer = new StringBuffer();
		IntStream.range(0, cols.length).forEach(i -> migrateColumns(buffer, cols, i));
		System.out.println(buffer);
	}

	private void migrateColumns(StringBuffer buffer, final String[] cols, int i) {
		if (buffer.length() != 0) {
			buffer.append(DELIMITER);
		}

		buffer.append(cols[i]);
		if ((i == 3) && stocks.containsKey(cols[0])) {
			buffer.append(DELIMITER + stocks.get(cols[0]));
		}
	}
}
