package de.mq.portfolio.share.support;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;

import de.mq.portfolio.share.Share;

public class ShareCSVInputServiceTest {
	
	private ShareCSVInputServiceImpl service = new ShareCSVInputServiceImpl();
	
	@Test
	public final void read() {
		final Collection<Share> shares = service.shares("data/stocks.csv");
		Assert.assertEquals(62, shares.size());
		shares.stream().forEach( share -> {Assert.assertTrue(share.name().trim().length() > 0);Assert.assertTrue(share.code().trim().length() > 0);} ); 
	}
	
	@Test(expected=IllegalStateException.class)
	public final void readWrongName() {
	service.shares("dontLetMeGetMe");
		
	}

}
