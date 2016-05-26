package de.mq.portfolio.share.support;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.Share;

public class ShareTest {
	
	private static final String CURRENCY = "EUR";
	private static final String WKN = "wkn123456";
	private static final String ID_FIELD = "id";
	private static final String COLLECTION = "Share";
	private static final String INDEX = "Dow";
	private static final String NAME = "Coca Cola";
	private static final String CODE = "KO";
	private final Share share = new ShareImpl(CODE, NAME, INDEX, WKN, CURRENCY);
	
	@Test
	public final void name() {
		Assert.assertEquals(NAME, share.name());
	}
	
	@Test
	public final void code() {
		Assert.assertEquals(CODE, share.code());
	}
	
	
	@Test
	public final void index() {
		Assert.assertEquals(INDEX, share.index());
	}
	
	@Test
	public final void isIndex() {
		Assert.assertFalse(share.isIndex());
	}
	
	@Test
	public final void constructorCodeName() {
		final Share share = new ShareImpl(CODE, NAME,null, WKN, CURRENCY);
		Assert.assertEquals(NAME, share.name());
		Assert.assertEquals(CODE, share.code());
		Assert.assertNull(share.index());
		Assert.assertTrue(share.isIndex());
	}
	
	@Test
	public final void constructorCode() {
		final Share share = new ShareImpl(CODE);
		Assert.assertNull(share.name());
		Assert.assertEquals(CODE, share.code());
		Assert.assertNull(share.index());
		Assert.assertTrue(share.isIndex());
	}
	
	@Test
	public final void constructor() {
		final Share share = BeanUtils.instantiateClass(ShareImpl.class);
		
		Assert.assertNull(share.name());
		Assert.assertNull(share.code());
		Assert.assertNull(share.index());
		Assert.assertTrue(share.isIndex());
	}
	
	@Test
	public final void annotations() {
		Assert.assertTrue(ShareImpl.class.isAnnotationPresent(Document.class));
		Assert.assertEquals(COLLECTION, ShareImpl.class.getAnnotation(Document.class).collection());
		Assert.assertTrue(ReflectionUtils.findField(ShareImpl.class, ID_FIELD).isAnnotationPresent(Id.class));
		
	}
	
	
	
	

}
