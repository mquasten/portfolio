package de.mq.portfolio.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.client.ResourceAccessException;

import de.mq.portfolio.share.Share;

import de.mq.portfolio.support.SimpleCSVInputServiceImpl;

public class ShareCSVInputServiceTest {
	
	
	@SuppressWarnings("unchecked")
	private final Converter<String[], Share>  converter = Mockito.mock(Converter.class);
	
	
	private final ExceptionTranslationBuilder<Collection<Share>, BufferedReader> exceptionTranslationBuilder = new ExceptionTranslationBuilderImpl<Collection<Share>,BufferedReader>().withTranslation(ResourceAccessException.class, Arrays.asList(IOException.class));
	
	private final SimpleCSVInputServiceImpl<Share> service = new SimpleCSVInputServiceImpl<>(converter, exceptionTranslationBuilder);
	
	@Before
	public final void setup() {
		Mockito.doAnswer(i -> {
			final String[] cols = (String[]) i.getArguments()[0];
			final Share share = Mockito.mock(Share.class);
			Mockito.when(share.code()).thenReturn(cols[0]);
			Mockito.when(share.name()).thenReturn(cols[3]);
			Mockito.when(share.wkn()).thenReturn(cols[1]);
			Mockito.when(share.currency()).thenReturn(cols[2]);
			if(cols.length==5 ){
				Mockito.when(share.index()).thenReturn(cols[4]);
			}
			return share;
		}).when(converter).convert(Mockito.any(String[].class));
	}
	
	
	@Test
	public final void read() {
		final Collection<Share> shares = service.read("data/stocks.csv");
		Assert.assertEquals(63, shares.size());
		shares.stream().forEach( share -> {Assert.assertTrue(share.name().trim().length() > 0);Assert.assertTrue(share.code().trim().length() > 0);} ); 
	}
	
	@Test(expected=ResourceAccessException.class)
	public final void readWrongName() {
	service.read("dontLetMeGetMe");
		
	}

}
