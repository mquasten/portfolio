package de.mq.portfolio.share.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.support.ExchangeRateImpl;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.gateway.ExchangeRateGatewayParameterService;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.share.ShareService;


@Component("gatewaysController")
@Scope("singleton")
public class GatewaysControllerImpl {
	
	static final String WRONG_EXCHANGE_RATE_PATTERN = "Wrong ExchangeRate: %s";
	static final String URL_EXCHANGE_RATES = "/exchangeRates.xhtml";
	static final String URL_PATTERN_EXCHANGE_RATES_PORTFOLIO = "/exchangeRatesPortfolio.xhtml?portfolioId=%s";
	static final String URL_PATTERN_CHART = "/chart.xhtml?shareCode=%s";
	static final String URL_PATTERN_REALTIME_COURSES = "/realtimeCourses.xhtml?portfolioId=%s&filter=.*";
	private final ShareGatewayParameterService shareGatewayParameterService; 
	private final ShareService shareService; 
	
	private final ExchangeRateGatewayParameterService exchangeRateGatewayParameterService;
	
	
	private final ExchangeRateService exchangeRateService;
	
	static final String HTML_EXTENSION = ".html";
	static final String ERROR_HTML_PATTERN = "<h2>Error during Download %s</h2><h4>%s</h4><label>%s</label>";
	static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
	static final String FILE_ATTACHEMENT_FORMAT = "attachment; filename=\"%s\"";
	
	@Autowired
	GatewaysControllerImpl(final ShareGatewayParameterService shareGatewayParameterService,final ExchangeRateGatewayParameterService exchangeRateGatewayParameterService, final ShareService shareService, final ExchangeRateService exchangeRateService) {
		this.shareGatewayParameterService = shareGatewayParameterService;
		this.exchangeRateGatewayParameterService=exchangeRateGatewayParameterService;
		this.shareService=shareService;
		this.exchangeRateService=exchangeRateService;
	}

	public void init(final GatewaysAO gatewaysAO) {
	
		if( !gatewaysAO.isExchangeRate()) {
			shareService.timeCourse(gatewaysAO.getCode()).ifPresent(timecourse -> gatewaysAO.assign(timecourse.updates()));
		} else {
					
			Assert.isTrue(gatewaysAO.getCode().split("[-]").length==2 , String.format(WRONG_EXCHANGE_RATE_PATTERN, gatewaysAO.getCode()));
			exchangeRateService.exchangeRateOrReverse(exchangeRate(gatewaysAO.getCode())).ifPresent(exchangeRate -> gatewaysAO.assign(exchangeRate.updates()));
		}
		try {
			gatewaysAO.setGatewayParameters(gatewayParameters(gatewaysAO));
		} catch (final Exception ex) {
			gatewaysAO.setMessage(ex.getMessage());
		}
	}
	
	
	private Collection<GatewayParameter> gatewayParameters(final GatewaysAO gatewaysAO) {
		if( gatewaysAO.isExchangeRate()){
			return exchangeRateGatewayParameterService.allGatewayParameters(exchangeRate(gatewaysAO.getCode()));
		}
		return shareGatewayParameterService.allGatewayParameters(new ShareImpl(gatewaysAO.getCode()));
	}

	private ExchangeRate exchangeRate(final String code) {
		final String[] codes = code.split("[-]");
		final ExchangeRate exchangeRate = new ExchangeRateImpl(codes[0], codes[1]);
		return exchangeRate;
	}

	public void download(final FacesContext facesContext, final GatewayParameter gatewayParameter) throws IOException {
		final ExternalContext externalContext = facesContext.getExternalContext();
		try {
			final byte[] content = shareGatewayParameterService.history(gatewayParameter).getBytes();
			externalContext.responseReset();
			externalContext.setResponseContentLength(content.length);
			externalContext.setResponseHeader(CONTENT_DISPOSITION_HEADER, String.format(FILE_ATTACHEMENT_FORMAT, gatewayParameter.gateway().downloadName(gatewayParameter.code())));
			externalContext.getResponseOutputStream().write(content);
			facesContext.responseComplete();
		} catch (final HttpClientErrorException clientErrorException) {
			final StringWriter errors = new StringWriter();
			clientErrorException.printStackTrace(new PrintWriter(errors));
			final byte[] content = String.format(ERROR_HTML_PATTERN, gatewayParameter.gateway(), clientErrorException.getMessage(), errors).getBytes();
			
			externalContext.responseReset();
			externalContext.setResponseContentLength(content.length);
			externalContext.setResponseHeader(CONTENT_DISPOSITION_HEADER, String.format(FILE_ATTACHEMENT_FORMAT, gatewayParameter.gateway().id(gatewayParameter.code()) + HTML_EXTENSION));
			externalContext.getResponseOutputStream().write(content);
			facesContext.responseComplete();
		}

	}
	
	public String back(final GatewaysAO gatewaysAO) {
		if( ! gatewaysAO.isExchangeRate() ) {			
			return gatewaysAO.isPortfolio() ?  String.format( URL_PATTERN_REALTIME_COURSES , gatewaysAO.getPortfolioId()) : String.format(URL_PATTERN_CHART ,gatewaysAO.getCode());
		}
		return gatewaysAO.isPortfolio() ? String.format(URL_PATTERN_EXCHANGE_RATES_PORTFOLIO , gatewaysAO.getPortfolioId()) : URL_EXCHANGE_RATES ; 
	}

}
