package de.mq.portfolio.share.support;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;


//@Service("shareCSVInputService")
class SimpleCSVInputServiceImpl<T> {

	private final String delimiterRegex;
	
	private final Converter<String[], T> converter; 
	
	SimpleCSVInputServiceImpl(final Converter<String[], T> converter) {
		this.converter=converter;
		this.delimiterRegex= "[;]";
	}

	public final  Collection<T> read(final String filename) {
		Assert.notNull(filename , "Filename is mandatory.");
		try (final InputStream in = new FileInputStream(filename); final InputStreamReader isr = new InputStreamReader(in, Charset.forName("UTF-8")); final BufferedReader br = new BufferedReader(isr)) {
			return doRead(br);
		} catch( IOException ex) {
			throw new IllegalStateException(String.format("Unable to read csv-file %s", filename), ex);
		}

	}

	
	private Collection<T> doRead(final BufferedReader br) throws IOException {
		String line;
		final Collection<T> results = new ArrayList<>();
		while ((line = br.readLine()) != null) {
			final String[] cols = line.split(delimiterRegex);
			results.add((T) converter.convert(cols));
		}

		return results;

	}

}
