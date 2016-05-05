package de.mq.portfolio.share.support;


import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

import de.mq.portfolio.share.Share;



public class SharesCSVLineConverterImpl implements Converter<String[], Share>{

	@Override
	public final Share  convert(final String[] cols) {
		Assert.notNull(cols, "Columns is mandatory.");
		Assert.isTrue(cols.length >= 3 , "At least 3 Columns must exists.");
		return (cols.length == 4) ? new ShareImpl(cols[0], cols[2], cols[3], cols[1]) : new ShareImpl(cols[0], cols[2],null, cols[1]);
	}

}
