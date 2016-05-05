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

import de.mq.portfolio.share.Share;

//@Service("shareCSVInputService")
class SimpleCSVInputServiceImpl {

	private final String delimiterRegex;
	
	private final Converter<String[], Share> converter; 
	
	SimpleCSVInputServiceImpl(final Converter<String[], Share> converter) {
		this.converter=converter;
		this.delimiterRegex= "[;]";
	}

	final Collection<Share> read(final String filename) {
		Assert.notNull(filename , "Filename is mandatory.");
		try (final InputStream in = new FileInputStream(filename); final InputStreamReader isr = new InputStreamReader(in, Charset.forName("UTF-8")); final BufferedReader br = new BufferedReader(isr)) {
			return doRead(br);
		} catch( IOException ex) {
			throw new IllegalStateException(String.format("Unable to read csv-file %s", filename), ex);
		}

	}

	private Collection<Share> doRead(final BufferedReader br) throws IOException {
		String line;
		final Collection<Share> shares = new ArrayList<>();
		while ((line = br.readLine()) != null) {

			final String[] cols = line.split(delimiterRegex);
			shares.add(converter.convert(cols));
		}

		return shares;

	}

}
