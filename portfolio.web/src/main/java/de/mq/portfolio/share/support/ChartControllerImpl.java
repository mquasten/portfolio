package de.mq.portfolio.share.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.model.chart.LineChartSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;

@Component("chartController")
@Scope("singleton")
public class ChartControllerImpl {

	static final String HTML_EXTENSION = ".html";
	static final String ERROR_HTML_PATTERN = "<h2>Error during Download %s</h2><h4>%s</h4><label>%s</label>";
	static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
	static final String FILE_ATTACHEMENT_FORMAT = "attachment; filename=\"%s\"";

	private final ShareService shareService;

	private final ShareGatewayParameterService shareGatewayParameterService;

	final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	ChartControllerImpl(final ShareService shareService, final ShareGatewayParameterService shareGatewayParameterService) {
		this.shareService = shareService;
		this.shareGatewayParameterService = shareGatewayParameterService;
	}

	public void init(final ChartAO chartAO) {

		final Optional<TimeCourse> timeCourse = shareService.timeCourse(chartAO.getCode());
		if (!timeCourse.isPresent()) {
			return;
		}
		chartAO.setTimeCourse(timeCourse.get());

		refresh(chartAO);

		final Collection<LineChartSeries> ratesSeries = new ArrayList<>();

		final LineChartSeries series = new LineChartSeries();

		timeCourse.get().rates().forEach(data -> series.set(df.format(data.date()), Double.valueOf(data.value())));
		ratesSeries.add(series);
		series.setShowMarker(false);
		series.setLabel(timeCourse.get().share().name().replaceAll("'", " "));
		chartAO.assign(ratesSeries);

		setGatewayParameters(chartAO, timeCourse);

	}

	private void setGatewayParameters(final ChartAO chartAO, final Optional<TimeCourse> timeCourse) {
		try {

			chartAO.setGatewayParameters(shareGatewayParameterService.aggregationForAllGateways(timeCourse.get().share()).gatewayParameters());
		} catch (final Exception ex) {
			chartAO.setMessage(ex.getMessage());
		}
	}

	public void refresh(final ChartAO chartAO) {
		final Optional<TimeCourse> timeCourse = shareService.realTimeCourses(Arrays.asList(chartAO.getCode()), false).stream().findAny();
		chartAO.setRealTimeRates(Arrays.asList());
		if (!timeCourse.isPresent()) {
			return;
		}
		chartAO.setRealTimeRates(timeCourse.get().rates());
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

}
