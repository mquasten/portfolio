package de.mq.portfolio.shareportfolio.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm.AlgorithmType;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Component("sharePortfolio")
@Scope("view")
public class PortfolioAO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String id;

	private final List<TimeCourse> timeCourses = new ArrayList<>();

	private boolean editable;

	private final List<Entry<String, Map<String, Double>>> correlations = new ArrayList<>();;

	private final List<String> shares = new ArrayList<>();

	private final Map<TimeCourse, Double> weights = new HashMap<>();

	private Double minStandardDeviation;

	private Double totalRate;

	private String currency;

	private Double totalRateDividends;
	
	private boolean exchangeRateTranslationsAware=false;
	
	private OptimisationAlgorithm.AlgorithmType algorithmType;

	private  Map<AlgorithmType, OptimisationAlgorithm> optimisationAlgorithms = new HashMap<>();

	@Autowired
	void setOptimisationAlgorithms(Collection<OptimisationAlgorithm> optimisationAlgorithms) {
		this.optimisationAlgorithms.clear();
		optimisationAlgorithms.forEach( a -> this.optimisationAlgorithms.put(a.algorithmType(), a));
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public  void setSharePortfolio(final SharePortfolio sharePortfolio, final Optional<ExchangeRateCalculator> exchangeRateCalculator) {
		this.name = sharePortfolio.name();
		this.currency = sharePortfolio.currency();
		this.algorithmType=sharePortfolio.algorithmType() ;
	
		
		this.timeCourses.clear();
		timeCourses.addAll(sharePortfolio.timeCourses());

		this.correlations.clear();
		this.correlations.addAll(sharePortfolio.correlationEntries());
		this.shares.clear();
		this.shares.addAll(sharePortfolio.timeCourses().stream().map(tc -> tc.share().name()).collect(Collectors.toList()));
		this.weights.clear();
		this.weights.putAll(sharePortfolio.min());
		this.editable = !sharePortfolio.isCommitted();
		if (this.timeCourses.size() < 2) {

			return;
		}

		this.minStandardDeviation = sharePortfolio.standardDeviation();

		Assert.isTrue(exchangeRateCalculator.isPresent(), "ExchangeRateCalculator is mandatory.");

		this.totalRate = sharePortfolio.totalRate(exchangeRateCalculator.get());

		this.totalRateDividends = sharePortfolio.totalRateDividends(exchangeRateCalculator.get());
		exchangeRateTranslationsAware=!sharePortfolio.exchangeRateTranslations().isEmpty();
	}

	public SharePortfolio getSharePortfolio() {
		
		System.out.println("*****");
		System.out.println(getAlgorithmType());
		final SharePortfolio result = new SharePortfolioImpl(name, timeCourses, optimisationAlgorithms.get(getAlgorithmType()));
		ReflectionUtils.doWithFields(result.getClass(), field -> {
			/* "...touched for the very first time." mdna (like a virgin **/ field.setAccessible(true);
			ReflectionUtils.setField(field, result, id);
		}, field -> field.isAnnotationPresent(Id.class));
		((SharePortfolioImpl) result).onBeforeSave();
		return result;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Entry<String, Map<String, Double>>> getCorrelations() {
		return correlations;
	}

	public List<String> getShares() {
		return shares;
	}

	public Map<TimeCourse, Double> getWeights() {
		return weights;
	}

	public Double getMinStandardDeviation() {
		return minStandardDeviation;
	}

	public List<TimeCourse> getTimeCourses() {
		return timeCourses;
	}

	public Double getTotalRate() {
		return totalRate;
	}

	public Double getTotalRateDividends() {
		return totalRateDividends;
	}

	public boolean getEditable() {
		return editable;
	}

	public String getCurrency() {
		return currency;
	}
	
	public boolean getExchangeRateTranslationsAware() {
		return exchangeRateTranslationsAware;
	}
	
	public OptimisationAlgorithm.AlgorithmType getAlgorithmType() {
		return algorithmType==null ?  AlgorithmType.MVP  : algorithmType;
	}

	public void setAlgorithmType(OptimisationAlgorithm.AlgorithmType algorithmType) {
		System.out.println(">>>" + algorithmType);
		this.algorithmType = algorithmType;
	}

}
