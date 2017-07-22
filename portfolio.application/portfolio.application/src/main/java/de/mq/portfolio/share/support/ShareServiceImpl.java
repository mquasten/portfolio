 package de.mq.portfolio.share.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;


@Service("shareService")
class ShareServiceImpl implements ShareService {

	
	private final HistoryRepository historyRepository;

	private final ShareRepository shareRepository;
	
	private final RealTimeRateRepository realTimeRateRepository;
	
	private final ShareGatewayParameterService shareGatewayParameterService;
	
	private final Map<TimeCourseConverter.TimeCourseConverterType, TimeCourseConverter> timeCourseConverters = new HashMap<>();

	@Autowired
	ShareServiceImpl(final HistoryRepository historyRepository, final ShareRepository shareRepository, final RealTimeRateRepository realTimeRateRepository, final ShareGatewayParameterService shareGatewayParameterService, final Collection<TimeCourseConverter> timeCourseConverters) {
		this.historyRepository = historyRepository;
		this.shareRepository = shareRepository;
		this.realTimeRateRepository = realTimeRateRepository;
		this.shareGatewayParameterService=shareGatewayParameterService;
		this.timeCourseConverters.putAll(timeCourseConverters.stream().collect(Collectors.toMap(converter -> converter.timeCourseConverterType(), converter -> converter)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.share.support.ShareService#timeCourse(de.mq.portfolio.
	 * share.support.Share)
	 */
	@Override
	public final TimeCourse timeCourse(final Share share) {
		final Collection<Gateway> supportedGateways = historyRepository.supports(share);
		
		if( supportedGateways.isEmpty()){
			System.err.println("No Gateway supported for share:" + share.code());
			return new TimeCourseImpl(share, Arrays.asList(), Arrays.asList());
		}
		
		final GatewayParameterAggregation<Share> gatewayParameterAggregation = shareGatewayParameterService.gatewayParameter(share, supportedGateways);
		
		
		final TimeCourse  result =  historyRepository.history(gatewayParameterAggregation);
		
		historyRepository.converters(share).forEach(type -> result.assign(timeCourseConverters.get(type).convert(result), true));
		
		return result;
		
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.ShareService#timeCourses(org.springframework.data.domain.Pageable, de.mq.portfolio.share.Share)
	 */
	@Override
	public final Collection<TimeCourse> timeCourses(final Pageable pageable, final Share share) {
		return shareRepository.timeCourses(pageable, share);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.ShareService#timeCourse(java.lang.String)
	 */
	@Override
	public final Optional<TimeCourse> timeCourse(final String code) {
		return shareRepository.timeCourses(Arrays.asList(code)).stream().findFirst();
	}

	@Override
	public Pageable pageable(final Share share, final Sort sort, final Number size) {
		return shareRepository.pageable(share,sort, size);
	}
	
	@Override
	public Collection<String> indexes() {
		return shareRepository.distinctIndex();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.share.support.ShareService#replacetTmeCourse(de.mq.portfolio
	 * .share.support.TimeCourse)
	 */
	@Override
	public final void replaceTimeCourse(final TimeCourse timeCourse) {
		final TimeCourse toBeUpdated =  shareRepository.timeCourses(Arrays.asList(timeCourse.code())).stream().findAny().orElse(new TimeCourseImpl(timeCourse.share(), new ArrayList<>(), new ArrayList<>()));
		toBeUpdated.assign(timeCourse);
		shareRepository.save(toBeUpdated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.share.support.ShareService#shares()
	 */
	@Override
	public final Collection<Share> shares() {
		return shareRepository.shares();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.share.support.ShareService#save(de.mq.portfolio.share.support.Share)
	 */
	@Override
	public final void save(final Share share) {
		shareRepository.save(share);
	}
	
	
	
	@Override
	public  final Collection<TimeCourse> realTimeCourses(final Collection<String> codes, final boolean useLastStoredTimeCourse){
		final Map<String, TimeCourse> timeCourses = shareRepository.timeCourses(codes).stream().collect(Collectors.toMap(tc -> tc.code(), tc -> tc));
		final Map<String,TimeCourse> realTimeCourses =   realTimeRateRepository.rates(timeCourses.values().stream().map(tc -> tc.share()).collect(Collectors.toList())).stream().collect(Collectors.toMap(tc -> tc.code(), tc -> tc));
			
	
		return codes.stream().map(code -> {
			final TimeCourse timeCourse = timeCourses.get(code);
			if (useLastStoredTimeCourse&&!timeCourse.rates().isEmpty()) {
				return new TimeCourseImpl(timeCourse.share(), Arrays.asList(timeCourse.rates().get(timeCourse.rates().size()-1), realTimeCourses.get(code).rates().get(1)), Arrays.asList());
			}
			return realTimeCourses.get(code);
		}).collect(Collectors.toList());		
				
			
		
		
		
	} 
	

}
