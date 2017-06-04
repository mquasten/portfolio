package de.mq.portfolio.shareportfolio.support;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateImpl;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.DataImpl;
import de.mq.portfolio.share.support.ShareRepository;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Service("sharePortfolioService")
abstract class AbstractSharePortfolioService implements SharePortfolioService {

	static final String STATUS_CONTINUE = "CONTINUE";
	static final String STATUS_COMPLETED = "COMPLETED";

	private final SharePortfolioRepository sharePortfolioRepository;

	private final ShareRepository shareRepository;

	private final ExchangeRateService exchangeRateService;
	
	private final ShareService shareService;
	
	static final String TIME_COURSE_PATH = "de.mq.portfolio.share.support.TimeCourseImpl";
	

	@Autowired
	AbstractSharePortfolioService(final SharePortfolioRepository sharePortfolioRepository, final ShareRepository shareRepository, final ExchangeRateService exchangeRateService, final ShareService shareService) {
		this.sharePortfolioRepository = sharePortfolioRepository;
		this.shareRepository = shareRepository;
		this.exchangeRateService = exchangeRateService;
		this.shareService	 = shareService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#
	 * committedPortfolio(java.lang.String)
	 */
	@Override
	public final SharePortfolio committedPortfolio(final String name) {
		Assert.notNull(name);
		final SharePortfolio sharePortfolio = sharePortfolioRepository.portfolio(name);
		if (!sharePortfolio.isCommitted()) {
			sharePortfolio.commit();
			sharePortfolioRepository.save(sharePortfolio);
		}
		return sharePortfolio;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#
	 * sharePortfolio(java.lang.String)
	 */
	@Override
	public final SharePortfolio sharePortfolio(final String id) {
		return sharePortfolioRepository.sharePortfolio(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioService#portfolios(
	 * org.springframework.data.domain.Pageable,
	 * de.mq.portfolio.shareportfolio.SharePortfolio)
	 */
	@Override
	public final Collection<SharePortfolio> portfolios(final Pageable pageable, final SharePortfolio sharePortfolio) {
		return sharePortfolioRepository.portfolios(pageable, sharePortfolio);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioService#pageable(de.
	 * mq.portfolio.shareportfolio.SharePortfolio,
	 * org.springframework.data.domain.Sort, java.lang.Number)
	 */
	@Override
	public Pageable pageable(final SharePortfolio sharePortfolio, final Sort sort, final Number size) {
		return sharePortfolioRepository.pageable(sharePortfolio, sort, size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioService#save(de.mq
	 * .portfolio.shareportfolio.SharePortfolio)
	 */
	@Override
	public final void save(final SharePortfolio sharePortfolio) {
		Assert.notNull(sharePortfolio, "SharePortfolio should be given.");
		sharePortfolioRepository.save(sharePortfolio);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioService#assign(de.mq
	 * .portfolio.shareportfolio.SharePortfolio, java.util.Collection)
	 */
	@Override
	public final void assign(final SharePortfolio sharePortfolio, final Collection<TimeCourse> timeCourses) {
		Assert.notNull(sharePortfolio.id(), "Shareportfolio should be persistent.");
		Assert.notNull(sharePortfolio.id(), String.format("Shareportfolio not found, id: %s", sharePortfolio.id()));
		final SharePortfolio existing = sharePortfolioRepository.sharePortfolio(sharePortfolio.id());
		existing.assign(shareRepository.timeCourses(existing.timeCourses().stream().map(tc -> tc.share().code()).collect(Collectors.toList())));
		sharePortfolioRepository.save(existing);
	}

	final String status(final String status, final Long counter, final Long limit) {

		final long max = (limit == null) ? 0 : limit;

		if (!status.equalsIgnoreCase(STATUS_COMPLETED)) {
			return status;
		}

		Assert.notNull(counter, "Counter should be defined in JobContent.");

		if (counter < max) {
			return STATUS_CONTINUE;
		}

		return STATUS_COMPLETED;

	}

	final Long incCounter(final Long counter) {
		if (counter == null) {
			return 1L;
		}

		return counter + 1;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioService#delete(java.
	 * lang.String)
	 */
	@Override
	public final void delete(final String sharePortfolioId) {
		Assert.hasText(sharePortfolioId, "Id is mandatory");
		final SharePortfolio existing = sharePortfolioRepository.sharePortfolio(sharePortfolioId);
		Assert.isTrue(!existing.isCommitted(), "SharePortfolio should not be committed");
		sharePortfolioRepository.delete(existing);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#
	 * retrospective(java.lang.String)
	 */
	@Override
	public final SharePortfolioRetrospective retrospective(final String sharePortfolioId) {
		Assert.hasText(sharePortfolioId, "Id is mandatory");
		final SharePortfolio portfolio = sharePortfolioRepository.sharePortfolio(sharePortfolioId);
		return newBuilder().withCommitedSharePortfolio(portfolio).withExchangeRateCalculator(exchangeRateService.exchangeRateCalculator(portfolio.exchangeRateTranslations()))
				.withTimeCourses(shareRepository.timeCourses(portfolio.timeCourses().stream().map(tc -> tc.code()).collect(Collectors.toSet()))).build();

	}

	public final void save(final String json) {
		sharePortfolioRepository.save(json);
	}


	
	private Collection<ExchangeRate> realtimeExchangeRates(final SharePortfolio portfolio) {
		
		final Set<ExchangeRate> usedExchangeRates = portfolio.timeCourses().stream().map(tc -> portfolio.exchangeRate(tc)).distinct().collect(Collectors.toSet());
		
		final Set<String> codes = portfolio.timeCourses().stream().map(timeCourse -> timeCourse.code()).collect(Collectors.toSet());
		final Date endDate = shareRepository.timeCourses(codes).stream().map(tc -> tc.end()).min((d1, d2) -> Long.valueOf(d1.getTime() - d2.getTime()).intValue()).orElseThrow(() -> new IllegalArgumentException("No rates aware."));
		
		final ExchangeRateCalculator exchangeRateCalculator = exchangeRateService.exchangeRateCalculator(portfolio.exchangeRateTranslations());
		return  exchangeRateService.realTimeExchangeRates(usedExchangeRates).stream().map(exchangeRate -> new ExchangeRateImpl(exchangeRate.source(), exchangeRate.target(),Arrays.asList( new DataImpl(endDate, exchangeRateCalculator.factor(exchangeRate, endDate)), DataAccessUtils.requiredSingleResult(exchangeRate.rates()) ))).collect(Collectors.toList());
	}
	
	
	
	
	
	
	
	private final Collection<Entry<TimeCourse, List<Data>>>  realtimeTimeCourses(final SharePortfolio sharePortfolio, final boolean useLastStoredTimeCourse) {
		final Map<String, TimeCourse> timeCoursesMap = new HashMap<>();
		sharePortfolio.timeCourses().stream().forEach(tc -> timeCoursesMap.put(tc.code(),tc));
		return  shareService.realTimeCourses(sharePortfolio.timeCourses().stream().map(tc -> tc.code()).collect(Collectors.toList()),useLastStoredTimeCourse ).stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(timeCoursesMap.get(tc.code()), tc.rates())).collect(Collectors.toList());

	}
	
	@Override
	public final RealtimePortfolioAggregation realtimePortfolioAggregation(final String sharePortfolioId, final boolean useLastStoredTimeCourse) {
		final SharePortfolio sharePortfolio = this.sharePortfolio(sharePortfolioId);
		final Collection<ExchangeRate> realtimeExchangeRates= realtimeExchangeRates(sharePortfolio);
		
		final Collection<Entry<TimeCourse, List<Data>>>  realtimeTimeCourses= realtimeTimeCourses(sharePortfolio, useLastStoredTimeCourse );
		
		return newRealtimePortfolioAggregationBuilder().withRealtimeCourses(realtimeTimeCourses).withSharePortfolio(sharePortfolio).withRealtimeExchangeRates(realtimeExchangeRates).build();
		
		
	}

	@Lookup
	abstract SharePortfolioRetrospectiveBuilder newBuilder();
	
	@Lookup
	abstract RealtimePortfolioAggregationBuilder newRealtimePortfolioAggregationBuilder();

}
