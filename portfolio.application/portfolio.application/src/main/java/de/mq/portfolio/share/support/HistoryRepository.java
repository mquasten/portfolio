package de.mq.portfolio.share.support;

import java.util.Collection;

import de.mq.portfolio.gateway.Gateway;

import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

interface HistoryRepository {

	TimeCourse history(final GatewayParameterAggregation<Share> gatewayParameterAggregation);
	
	Collection<Gateway> supports(final  Share  share);

}