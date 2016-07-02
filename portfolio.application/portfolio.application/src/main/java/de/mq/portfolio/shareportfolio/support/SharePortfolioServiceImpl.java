package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.ShareRepository;
import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Service("sharePortfolioService")
class SharePortfolioServiceImpl implements SharePortfolioService {

	static final String STATUS_CONTINUE = "CONTINUE";
	static final String STATUS_COMPLETED = "COMPLETED";
	private final SharePortfolioRepository sharePortfolioRepository;
	private final ShareRepository shareRepository;
	
	private final ExchangeRateService exchangeRateService;
	
	private final  Class<? extends SharePortfolioRetrospectiveBuilder> builderClass = SharePortfolioRetrospectiveBuilderImpl.class;
	
	

	@Autowired
	SharePortfolioServiceImpl(final SharePortfolioRepository sharePortfolioRepository, final ShareRepository shareRepository, final  ExchangeRateService exchangeRateService) {
		this.sharePortfolioRepository = sharePortfolioRepository;
		this.shareRepository=shareRepository;
		this.exchangeRateService=exchangeRateService;
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
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#sharePortfolio(java.lang.String)
	 */
	@Override
	public final SharePortfolio sharePortfolio(final String id) {
		return sharePortfolioRepository.sharePortfolio(id);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#portfolios(org.springframework.data.domain.Pageable, de.mq.portfolio.shareportfolio.SharePortfolio)
	 */
	@Override
	public final Collection<SharePortfolio> portfolios(final Pageable pageable, final SharePortfolio sharePortfolio) {
		return sharePortfolioRepository.portfolios(pageable, sharePortfolio);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#pageable(de.mq.portfolio.shareportfolio.SharePortfolio, org.springframework.data.domain.Sort, java.lang.Number)
	 */
	@Override
	public Pageable pageable(final SharePortfolio sharePortfolio, final Sort sort, final Number size) {
		return sharePortfolioRepository.pageable(sharePortfolio,sort, size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioService#samples(de
	 * .mq.portfolio.shareportfolio.SharePortfolio)
	 */
	@Override
	public final Collection<double[]> samples(final SharePortfolio sharePortfolio, final Number size) {
		Assert.notNull(sharePortfolio);
		Assert.notNull(size);
		Assert.isTrue(size.intValue() > 1);
		final int n = sharePortfolio.timeCourses().size();
		Assert.isTrue(n > 2 , "Al least 2 Timecourses must be present." );
		return IntStream.range(0, size.intValue()).mapToObj(i -> sample(n)).collect(Collectors.toList());
	}

	private double[] sample(final int n) {
		final double[] result = new double[n];
		final double sum[] = { 0 };
		IntStream.range(0, n).forEach(i -> {
			final double x = Math.random();
			result[i] = x;
			sum[0] = sum[0] + x;

		});

		IntStream.range(0, n).forEach(i -> {
			result[i] = result[i] / sum[0];
		});
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioService#risk(de.mq
	 * .portfolio.shareportfolio.SharePortfolio, double[])
	 */
	@Override
	public final PortfolioOptimisation variance(final SharePortfolio sharePortfolio, final double[] samples) {
		Assert.notNull(sharePortfolio);
		Assert.notNull(samples);
		final double variance = sharePortfolio.risk(samples);
		return new PortfolioOptimisationImpl(sharePortfolio.name(), samples, variance, 1L);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioService#save(de.mq
	 * .portfolio.shareportfolio.PortfolioOptimisation)
	 */
	@Override
	public final void create(final PortfolioOptimisation portfolioOptimisation) {
		sharePortfolioRepository.save(portfolioOptimisation);
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
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioService#assign(de
	 * .mq.portfolio.shareportfolio.PortfolioOptimisation)
	 */
	@Override
	public final SharePortfolio assign(final PortfolioOptimisation portfolioOptimisation) {
		Assert.notNull(portfolioOptimisation, "PortfolioOptimisation should be given.");
		final SharePortfolio result = sharePortfolioRepository.portfolio(portfolioOptimisation.portfolio());
		ReflectionUtils.doWithFields(result.getClass(), field -> {
			field.setAccessible(true);
			ReflectionUtils.setField(field, result, portfolioOptimisation);
		}, field -> field.getType().isAssignableFrom(PortfolioOptimisation.class));

		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#assign(de.mq.portfolio.shareportfolio.SharePortfolio, java.util.Collection)
	 */
	@Override
	public final void assign(final SharePortfolio sharePortfolio, final Collection<TimeCourse> timeCourses){
		Assert.notNull(sharePortfolio.id(), "Shareportfolio should be persistent.");
		Assert.notNull(sharePortfolio.id(), String.format("Shareportfolio not found, id: %s", sharePortfolio.id()));
		
		final SharePortfolio existing = sharePortfolioRepository.sharePortfolio(sharePortfolio.id());
	    existing.assign(shareRepository.timeCourses(existing.timeCourses().stream().map(tc -> tc.share().code()).collect(Collectors.toList())));
	    sharePortfolioRepository.save(existing);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioService#minVariance
	 * (java.lang.String)
	 */
	@Override
	public final PortfolioOptimisation minVariance(final String portfolioName) {
		final Optional<PortfolioOptimisation> result = sharePortfolioRepository.minVariance(portfolioName);

		if (!result.isPresent()) {
			throw new IllegalArgumentException(String.format("No Results found for Portfolio %s.", portfolioName));
		}
		return result.get();

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

	final Collection<PortfolioOptimisation> aggregate(final Collection<PortfolioOptimisation> portfolioOptimisations) {
		final List<PortfolioOptimisation> results = new ArrayList<>();
		portfolioOptimisations.stream().reduce((y, x) -> x.variance() < y.variance() ? x : y).ifPresent(r -> {
			
			ReflectionUtils.doWithFields(r.getClass(), field -> {field.setAccessible(true); ReflectionUtils.setField(field, r,  new Long(portfolioOptimisations.size()));}, field -> field.getName().equals("samples") ); 
			results.add(r);
		});
		return Collections.unmodifiableList(results);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#delete(java.lang.String)
	 */
	@Override
	public final void delete(final String sharePortfolioId) {
		Assert.hasText(sharePortfolioId , "Id is mandatory");
		final SharePortfolio existing = sharePortfolioRepository.sharePortfolio(sharePortfolioId);
		Assert.isTrue(!existing.isCommitted(), "SharePortfolio should not be committed");
		sharePortfolioRepository.delete(existing);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioService#retrospective(java.lang.String)
	 */
	@Override
	public final  SharePortfolioRetrospective retrospective(final String sharePortfolioId ) {
		Assert.hasText(sharePortfolioId , "Id is mandatory");
		final SharePortfolio portfolio = sharePortfolioRepository.sharePortfolio(sharePortfolioId);
		return  BeanUtils.instantiateClass(builderClass).withCommitedSharePortfolio(portfolio).withExchangeRateCalculator(exchangeRateService.exchangeRateCalculator(portfolio.exchangeRateTranslations())).withTimeCourses(shareRepository.timeCourses(portfolio.timeCourses().stream().map(tc -> tc.code()).collect(Collectors.toSet()))).build();
		
	}

	

}
