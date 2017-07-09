package de.mq.portfolio.share.support;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo-test.xml", "/application-test.xml" })
@Ignore
public class HistoryArivaRestRepositoryIntegrationTest {

	@Autowired
	@Qualifier("arivaHistoryRepository")
	private HistoryRepository historyRestRepository;
	private final Share share = Mockito.mock(Share.class);

	@Test
	@Ignore
	public final void historyKO() {

		// 400, 412
		Assert.assertNotNull(historyRestRepository);

		Mockito.doReturn("850663").when(share).wkn();
		Mockito.doReturn("KO").when(share).code();
		historyRestRepository.history(share).rates().forEach(rate -> System.out.println(rate.date() + "=" + rate.value()));
	}

	@Test
	@Ignore
	public final void historyJNJ() {
		Assert.assertNotNull(historyRestRepository);

		// Mockito.doReturn("412").when(share).id2(); //JNJ
		Mockito.doReturn("853260").when(share).wkn();// JNJ
		Mockito.doReturn("JNJ").when(share).code();

		historyRestRepository.history(share).rates().forEach(rate -> System.out.println(rate.date() + "=" + rate.value()));
	}

	@Test
	@Ignore
	public final void dax() {

		Mockito.doReturn("846900").when(share).wkn();
		Mockito.doReturn("^GDAXI").when(share).code();
		Mockito.doReturn(true).when(share).isIndex();
		historyRestRepository.history(share).rates().forEach(rate -> System.out.println(rate.date() + "=" + rate.value()));
	}

	@Test
	@Ignore
	public final void dow() {

		Mockito.doReturn("969420").when(share).wkn();
		Mockito.doReturn(true).when(share).isIndex();
		Mockito.doReturn("^DJI").when(share).code();
		historyRestRepository.history(share).rates().forEach(rate -> System.out.println(rate.date() + "=" + rate.value()));
	}

	@Test
	@Ignore
	public final void dividends() {

		ReflectionTestUtils.setField(historyRestRepository, "imports", Arrays.asList(HistoryArivaRestRepositoryImpl.Imports.Dividends));

		Mockito.doReturn("SAP.DE").when(share).code();
		Mockito.doReturn("EUR").when(share).currency();

		final TimeCourse timeCourse = historyRestRepository.history(share);

		Assert.assertEquals(1, timeCourse.dividends().size());
		System.out.println(timeCourse.dividends().stream().findAny().get().date());
		System.out.println(timeCourse.dividends().stream().findAny().get().value());

	}
	
	@Test
	@Ignore
	public final void dividendsKO() {

		ReflectionTestUtils.setField(historyRestRepository, "imports", Arrays.asList(HistoryArivaRestRepositoryImpl.Imports.Dividends));

		Mockito.doReturn("KO").when(share).code();
		Mockito.doReturn("USD").when(share).currency();

		final TimeCourse timeCourse = historyRestRepository.history(share);

		
		timeCourse.dividends().forEach(dividend -> System.out.println(dividend.date() + ":" + dividend.value()));

	}
}
