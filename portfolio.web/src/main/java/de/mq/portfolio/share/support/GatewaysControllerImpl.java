package de.mq.portfolio.share.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.share.ShareService;

@Component("gatewaysController")
@Scope("singleton")
public class GatewaysControllerImpl {
	
	private final ShareGatewayParameterService shareGatewayParameterService; 
	private final ShareService shareService; 
	
	static final String HTML_EXTENSION = ".html";
	static final String ERROR_HTML_PATTERN = "<h2>Error during Download %s</h2><h4>%s</h4><label>%s</label>";
	static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
	static final String FILE_ATTACHEMENT_FORMAT = "attachment; filename=\"%s\"";
	
	@Autowired
	GatewaysControllerImpl(ShareGatewayParameterService shareGatewayParameterService, final ShareService shareService) {
		this.shareGatewayParameterService = shareGatewayParameterService;
		this.shareService=shareService;
	}

	public void init(final GatewaysAO gatewaysAO) {
	
		shareService.timeCourse(gatewaysAO.getCode()).ifPresent(timecourse -> gatewaysAO.assign(timecourse.updates()));
		try {
			gatewaysAO.setGatewayParameters(shareGatewayParameterService.aggregationForAllGateways(new ShareImpl(gatewaysAO.getCode())).gatewayParameters());
		} catch (final Exception ex) {
			gatewaysAO.setMessage(ex.getMessage());
		}
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
			return gatewaysAO.isPortfolio() ?  String.format( "/realtimeCourses.xhtml?portfolioId=%s&filter=.*" , gatewaysAO.getPortfolioId()) : String.format("/chart.xhtml?shareCode=%s" ,gatewaysAO.getCode());
		}
		return gatewaysAO.isPortfolio() ? String.format("/exchangeRatesPortfolio.xhtml?portfolioId=%s" , gatewaysAO.getPortfolioId()) : "/exchangeRates.xhtml" ; 
	}

}
