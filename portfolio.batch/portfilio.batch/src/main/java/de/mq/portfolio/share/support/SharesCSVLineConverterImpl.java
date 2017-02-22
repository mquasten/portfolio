package de.mq.portfolio.share.support;


import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.support.ShareImpl;


// :TODO new -> builder + methodInjection
public class SharesCSVLineConverterImpl implements Converter<String[], Share>{

	@Override
	public final Share  convert(final String[] cols) {
		Assert.notNull(cols, "Columns is mandatory.");
		Assert.isTrue(cols.length >= 4 , "A Line should have at least 4 columns.");
		Assert.isTrue(cols.length <= 6 , "A Line should have not more than 6 columns.");
		return (cols.length == 6) ? new ShareImpl(cols[0], cols[3], cols[4], cols[5], cols[1], cols[2]) :(cols.length == 5) ?  new ShareImpl(cols[0], cols[3],cols[4], null, cols[1],cols[2]): new ShareImpl(cols[0], cols[3],null, null, cols[1],cols[2]);
	}

}
