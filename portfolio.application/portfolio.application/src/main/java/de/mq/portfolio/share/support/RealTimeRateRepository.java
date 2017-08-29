package de.mq.portfolio.share.support;

import java.util.Collection;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

public interface RealTimeRateRepository {

	/**
	 * Get the RealTimeRates as a Collection of Data with entry from today and yesterday
	 * @param gatewayParameterAggregation GatewayParameterAggregation with merged Parameters from all Shares
	 * @return a Collection of TimeCourses with 2 entries, realtime rate from today and rate from yesterday
	 */
	Collection<TimeCourse> rates(GatewayParameterAggregation<Collection<Share>> gatewayParameterAggregation);

	Gateway supports(Collection<Share> shares);

}