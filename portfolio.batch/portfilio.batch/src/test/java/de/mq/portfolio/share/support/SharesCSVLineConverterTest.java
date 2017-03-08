package de.mq.portfolio.share.support;


import org.junit.Assert;
import org.junit.Test;

import de.mq.portfolio.share.Share;


public class SharesCSVLineConverterTest {

	private static final String CODE2 = "ETR:SAP";

	private static final String CURRENCY = "EUR";

	private static final String WKN = "4711DE";

	private static final String INDEX = "DAX";

	private static final String NAME = "SAP AG";

	private static final String CODE = "SAP";

	private final SharesCSVLineConverterImpl sharesCSVLineConverter = new SharesCSVLineConverterImpl();
	
	
	
	@Test
	public final void convert() {
		final Share share = sharesCSVLineConverter.convert(new String[] {CODE, WKN, CURRENCY , NAME, CODE2, INDEX });
		Assert.assertEquals(CODE, share.code());
		Assert.assertEquals(NAME, share.name());
		Assert.assertEquals(WKN, share.wkn());
		Assert.assertEquals(INDEX, share.index());
		Assert.assertEquals(CURRENCY, share.currency());
		Assert.assertEquals(CODE2, share.code2());
		
	}
	
	@Test
	public final void convertWithoutWkn() {
		final Share share = sharesCSVLineConverter.convert(new String[] {CODE, WKN, CURRENCY , NAME  });
		Assert.assertEquals(CODE, share.code());
		Assert.assertEquals(NAME, share.name());
		Assert.assertEquals(WKN, share.wkn());
		Assert.assertNull(share.code2());
		Assert.assertNull(share.index());
		Assert.assertEquals(CURRENCY, share.currency());
		
	}
	
	@Test
	public final void convertWithoutIndex() {
		final Share share = sharesCSVLineConverter.convert(new String[] {CODE, WKN, CURRENCY , NAME , CODE2 });
		
		Assert.assertEquals(CODE, share.code());
		Assert.assertEquals(NAME, share.name());
		Assert.assertEquals(WKN, share.wkn());
		Assert.assertEquals(CODE2, share.code2());
		Assert.assertNull(share.index());
		Assert.assertEquals(CURRENCY, share.currency());
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void convertWrongLength() {
		sharesCSVLineConverter.convert(new String[] {CODE, WKN, CURRENCY  });
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void convertWrongLengthToMuch() {
		sharesCSVLineConverter.convert(new String[] {CODE, WKN, CURRENCY , NAME, CODE2, INDEX  , "xx"  });
	}
	
}
