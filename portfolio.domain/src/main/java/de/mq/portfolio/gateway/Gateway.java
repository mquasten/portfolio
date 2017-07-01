package de.mq.portfolio.gateway;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public enum Gateway {

	ArivaRateHistory("ARH"), CentralBankExchangeRates("BER");

	static final String DELIMITER = "-";
	private final String id;

	Gateway(final String id) {
		this.id = id;
	}

	String id() {
		return id;
	}

	public String id(final String... keys) {
		Assert.notNull(keys, "Keys is mandatory.");
		Assert.notEmpty(Arrays.asList(keys), "At lets one key expected.");
		return StringUtils.arrayToDelimitedString(keys, DELIMITER) + DELIMITER + id;
	}

	public static String code(final String id) {
		final int index = id.lastIndexOf(DELIMITER);
		idSyntaxGuard(id, index);
		return id.substring(0, index);
	}

	private static void idSyntaxGuard(final String id, final int index) {
		Assert.hasText(id, "Id is mandatory.");
		Assert.isTrue(index > 0, "Invalid id, must contain: " + DELIMITER + ".");
		Assert.isTrue(id.length() > index + 1, "Invalid id, must contains gatewayId at the end.");
	}

	public static Gateway gateway(final String id) {
		Assert.hasText(id, "Id is mandatory.");
		final int index = id.lastIndexOf(DELIMITER);
		if ((index < 0)) {
			return gatewayValue(id);
		}

		idSyntaxGuard(id, index);
		final String gatewayId = id.substring(index + 1, id.length());
		return gatewayValue(gatewayId);
	}

	private static Gateway gatewayValue(final String gatewayId) {
		return DataAccessUtils.requiredSingleResult(Arrays.asList(values()).stream().filter(value -> value.id().equals(gatewayId)).collect(Collectors.toSet()));
	}

}
