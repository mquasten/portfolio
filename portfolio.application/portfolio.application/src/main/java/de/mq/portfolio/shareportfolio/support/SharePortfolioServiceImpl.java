package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Service("sharePortfolioService")
class SharePortfolioServiceImpl implements SharePortfolioService {

	static final String STATUS_CONTINUE = "CONTINUE";
	static final String STATUS_COMPLETED = "COMPLETED";
	private final SharePortfolioRepository sharePortfolioRepository;

	@Autowired
	SharePortfolioServiceImpl(SharePortfolioRepository sharePortfolioRepository) {
		this.sharePortfolioRepository = sharePortfolioRepository;
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

}
