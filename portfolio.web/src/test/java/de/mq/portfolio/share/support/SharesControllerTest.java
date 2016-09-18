package de.mq.portfolio.share.support;


import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.shareportfolio.support.SharePortfolioService;
import junit.framework.Assert;

public class SharesControllerTest {
	
	private final ShareService shareService = Mockito.mock(ShareService.class);
	
	private final SharePortfolioService sharePortfolioService = Mockito.mock(SharePortfolioService.class);
	
	private final SharesControllerImpl sharesController = new SharesControllerImpl(shareService, sharePortfolioService);
	
	@Test
	public final void orderBy() {
	
		@SuppressWarnings("unchecked")
		final Collection<Map<String,Sort>> results = Arrays.asList(SharesControllerImpl.class.getDeclaredFields()).stream().filter(field ->   field.getType().equals(Map.class)).map(field -> (Map<String,Sort>) ReflectionTestUtils.getField(sharesController, field.getName())).collect(Collectors.toSet());
		
		final Map<String,Sort> result =  DataAccessUtils.requiredSingleResult(results);
		
		Assert.assertEquals(6,result.size());
		Assert.assertEquals(new Sort(SharesControllerImpl.ID_FIELD_NAME), result.get(SharesControllerImpl.ID_FIELD_NAME));
		Assert.assertEquals(new Sort( SharesControllerImpl.SHARE_FIELDS_NAME + "." + SharesControllerImpl.NAME_FIELD_NAME, SharesControllerImpl.ID_FIELD_NAME),  result.get(SharesControllerImpl.NAME_FIELD_NAME));
		Assert.assertEquals(new Sort(Direction.DESC, SharesControllerImpl.MEAN_RATE_FRIELDE_NAME ,SharesControllerImpl.ID_FIELD_NAME) ,result.get(SharesControllerImpl.MEAN_RATE_FRIELDE_NAME));
		Assert.assertEquals( new Sort(Direction.DESC, SharesControllerImpl.TOTAL_RATE_FIELD_NAME ,SharesControllerImpl.ID_FIELD_NAME), result.get(SharesControllerImpl.TOTAL_RATE_FIELD_NAME));
		Assert.assertEquals(new Sort(Direction.DESC, SharesControllerImpl.TOTAL_RATE_DIVIDENDS_FIELD_NAME ,SharesControllerImpl.ID_FIELD_NAME), result.get(SharesControllerImpl.TOTAL_RATE_DIVIDENDS_FIELD_NAME));
		Assert.assertEquals(new Sort(SharesControllerImpl.STANDARD_DEVIATION_FIELD_NAME ,SharesControllerImpl.ID_FIELD_NAME), result.get(SharesControllerImpl.STANDARD_DEVIATION_FIELD_NAME));
	
	}

}
