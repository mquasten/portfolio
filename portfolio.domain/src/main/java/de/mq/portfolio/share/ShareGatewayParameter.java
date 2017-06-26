package de.mq.portfolio.share;

import java.util.Map;

import de.mq.portfolio.share.support.Gateway;

public interface ShareGatewayParameter {

	String code();

	Gateway gateway();

	Map<String, String> parameters();

}