package de.mq.portfolio.share.support;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.portfolio.share.Share;

@Service("shareCSVInputService")
class ShareCSVInputServiceImpl {

	private final String REGEX = "[;]";

	final Collection<Share> shares(final String filename) {
		Assert.notNull(filename , "Filename is mandatory.");
		try (final InputStream in = new FileInputStream(filename); final InputStreamReader isr = new InputStreamReader(in, Charset.forName("UTF-8")); final BufferedReader br = new BufferedReader(isr)) {
			return read(br);
		} catch( IOException ex) {
			throw new IllegalStateException(String.format("Unable to read csv-file %s", filename), ex);
		}

	}

	private Collection<Share> read(final BufferedReader br) throws IOException {
		String line;
		final Collection<Share> shares = new ArrayList<>();
		while ((line = br.readLine()) != null) {

			final String[] cols = line.split(REGEX);
			shares.add((cols.length == 4) ? new ShareImpl(cols[0], cols[2], cols[3], cols[1]) : new ShareImpl(cols[0], cols[2],null, cols[1]));
		}

		return shares;

	}

}
