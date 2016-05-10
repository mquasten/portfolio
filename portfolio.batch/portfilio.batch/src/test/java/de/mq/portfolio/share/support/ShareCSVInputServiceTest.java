package de.mq.portfolio.share.support;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;

import de.mq.portfolio.share.Share;

public class ShareCSVInputServiceTest {
	
	
	@SuppressWarnings("unchecked")
	private final Converter<String[], Share>  converter = Mockito.mock(Converter.class);
	
	private final SimpleCSVInputServiceImpl<Share> service = new SimpleCSVInputServiceImpl<>(converter);
	
	@Before
	public final void setup() {
		Mockito.doAnswer(i -> {
			final String[] cols = (String[]) i.getArguments()[0];
			return (cols.length == 4) ? new ShareImpl(cols[0], cols[2], cols[3], cols[1]) : new ShareImpl(cols[0], cols[2],null, cols[1]);
			
		}).when(converter).convert(Mockito.any(String[].class));
	}
	
	
	@Test
	public final void read() {
		final Collection<Share> shares = service.read("data/stocks.csv");
		Assert.assertEquals(63, shares.size());
		shares.stream().forEach( share -> {Assert.assertTrue(share.name().trim().length() > 0);Assert.assertTrue(share.code().trim().length() > 0);} ); 
	}
	
	@Test(expected=IllegalStateException.class)
	public final void readWrongName() {
	service.read("dontLetMeGetMe");
		
	}

}
