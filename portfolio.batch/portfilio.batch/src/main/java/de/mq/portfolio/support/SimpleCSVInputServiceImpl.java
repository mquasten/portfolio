package de.mq.portfolio.support;

import java.io.BufferedReader;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

 class SimpleCSVInputServiceImpl<T> {

	private final String delimiterRegex;
	
	private final Converter<String[], T> converter; 
	
	
	SimpleCSVInputServiceImpl(final Converter<String[], T> converter) {
		this.converter=converter;
		this.delimiterRegex= "[;]";
	}

	public final  Collection<T> read(final String filename)  {
		Assert.notNull(filename , "Filename is mandatory.");
		try ( final BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream(filename), Charset.forName("UTF-8")))) {
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
