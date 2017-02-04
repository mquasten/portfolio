package de.mq.portfolio.shareportfolio.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.mq.portfolio.support.ExceptionTranslationBuilder;

@Component("portfolio2PdfConverter")
public abstract class AbstractPortfolio2PdfConverter implements Converter<PortfolioAO, byte[]> {

	static final String DATE_RANGE_PATTERN = "%s - %s";

	static final String VARIANCE_TABLE_SHARE_HEADER = "Aktie";

	static final String VARIANCE_TABLE_WKN_HEADER = "WKN";

	static final String VARIANCE_TABLE_TIME_FRAME_HEADER = "Zeitreihe";

	static final String VARIANCE_TABLE_STANDARD_DEVIATION_HEADER = "Standardabweichung [‰]";

	static final String VARIANCE_TABLE_RATIO_HEADER = "Anteil [%]";

	static final String VARIANCE_TABLE_RATE_HEADER = "Rendite [%]";

	static final String VARIANCE_TABLE_RATE_DIVIDENDS_HEADER = "Dividenden [%]";

	static final String CORRELATION_TABLE_CORRELATIONS_HEADER = "Korrelationen [%]";

	static final int VARIANCE_TABLE_COL_SIZE = 7;

	static final int WIDTH_TABLE = 100;

	static final String HEADLINE_CORRELATION_PATTERN = "Korrelationen %s";

	static final String HEADLINE_SHARE_PATTERN = "Aktien %s [Währung: %s]";
	
	static final String HEADLINE_ALGORITHM_PATTERN =  "Algorithmus: %s";

	final Font headline = FontFactory.getFont(FontFactory.TIMES_BOLD, 24);

	final Font tableHeadline = FontFactory.getFont(FontFactory.TIMES_BOLD, 10);
	final Font tableCell = FontFactory.getFont(FontFactory.TIMES, 10);

	final NumberFormat numberFormat = NumberFormat.getInstance();
	final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");


	private final Converter<String, String> currencyConverter;

	@Autowired
	AbstractPortfolio2PdfConverter(@Qualifier("currencyConverter") final Converter<String, String> currencyConverter) {
		this.currencyConverter = currencyConverter;
	}

	@Override
	public byte[] convert(final PortfolioAO portfolioAO) {
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		final Document document = newDocument();
		return  (byte[]) translator().withResource(() -> new ByteArrayOutputStream()).withStatement(bas -> (byte[]) writeDocument(portfolioAO, document, (ByteArrayOutputStream) bas) ).translate();
	}

	private byte[] writeDocument(final PortfolioAO portfolioAO, final Document document, final ByteArrayOutputStream os) {
		assignWriter(document, os);
		document.open();
		document.addTitle(portfolioAO.getName());
		add(document, new Paragraph(String.format(HEADLINE_SHARE_PATTERN, portfolioAO.getName(), currencyConverter.convert(portfolioAO.getCurrency())), headline));
		final Table varianceSharesTable = newVarianceTable();

		varianceSharesTable.setWidth(WIDTH_TABLE);
		addCellHeader(varianceSharesTable, VARIANCE_TABLE_SHARE_HEADER);
		addCellHeader(varianceSharesTable, VARIANCE_TABLE_WKN_HEADER);
		addCellHeader(varianceSharesTable, VARIANCE_TABLE_TIME_FRAME_HEADER);
		addCellHeader(varianceSharesTable, VARIANCE_TABLE_STANDARD_DEVIATION_HEADER);
		addCellHeader(varianceSharesTable, VARIANCE_TABLE_RATIO_HEADER);
		addCellHeader(varianceSharesTable, VARIANCE_TABLE_RATE_HEADER);
		addCellHeader(varianceSharesTable, VARIANCE_TABLE_RATE_DIVIDENDS_HEADER);

		varianceSharesTable.setAlignment(Element.ALIGN_LEFT);
		portfolioAO.getTimeCourses().forEach(tc -> {
			addCellHeader(varianceSharesTable, tc.share().name());
			addCell(varianceSharesTable, tc.wkn());

			addCell(varianceSharesTable, String.format(DATE_RANGE_PATTERN, dateFormat.format(tc.start()), dateFormat.format(tc.end())));
			addCell(varianceSharesTable, tc.standardDeviation(), 1000d);
			addCell(varianceSharesTable, portfolioAO.getWeights().get(tc), 100d);
			addCell(varianceSharesTable, tc.totalRate(), 100d);
			addCell(varianceSharesTable, tc.totalRateDividends(), 100d);

		});

		addCell(varianceSharesTable, null, 0);
		addCell(varianceSharesTable, null, 0);
		addCell(varianceSharesTable, null, 0);
		addCellHeader(varianceSharesTable, portfolioAO.getMinStandardDeviation(), 1000d);
		addCell(varianceSharesTable, null, 0);
		addCellHeader(varianceSharesTable, portfolioAO.getTotalRate(), 100d);
		addCellHeader(varianceSharesTable, portfolioAO.getTotalRateDividends(), 100d);
		
		add(document, varianceSharesTable);

		add(document, new Paragraph(String.format(HEADLINE_CORRELATION_PATTERN, portfolioAO.getName()), headline));
		final Table correlationTable = newCorrelationTable(portfolioAO.getShares().size() + 1);
		correlationTable.setAlignment(Element.ALIGN_LEFT);
		correlationTable.setWidth(WIDTH_TABLE);
		addCellHeader(correlationTable, CORRELATION_TABLE_CORRELATIONS_HEADER);
		portfolioAO.getShares().forEach(share -> addCellHeader(correlationTable, share));
		portfolioAO.getCorrelations().forEach(e -> {
			addCellHeader(correlationTable, e.getKey());
			portfolioAO.getShares().forEach(share -> addCell(correlationTable, e.getValue().get(share), 100d));
		});
		add(document, correlationTable);
		
		
		add(document, new Paragraph(String.format(HEADLINE_ALGORITHM_PATTERN, portfolioAO.getSharePortfolio().algorithmType().name()), headline));
		
		PdfPTable parameterTable = new PdfPTable(2);
		parameterTable.setHorizontalAlignment(Element.ALIGN_LEFT);
		parameterTable.setWidthPercentage(WIDTH_TABLE);
		parameterTable.setSpacingBefore(35);
		
	
		
		portfolioAO.getSharePortfolio().optimisationAlgorithm().params().forEach(parameter -> {
			parameterTable.addCell(parameter.name());
		
			if( parameter.isVector() ) {
				final List<String> names = portfolioAO.getTimeCourses().stream().map(tc -> tc.name()).collect(Collectors.toList());
				addCell(parameterTable, portfolioAO.getSharePortfolio().parameterVector(parameter), names );
			} else {
				parameterTable.addCell(text(portfolioAO.getSharePortfolio().param(parameter), 1d));
			}
			
		} );
		
		add(document, parameterTable);
		
		document.close();

		return os.toByteArray();
	}

	private void assignWriter(final Document document, final ByteArrayOutputStream os) {
		translator().withStatement(() -> PdfWriter.getInstance(document, os)).translate();

	}

	Table newCorrelationTable(final int size) {

		return (Table) translator().withStatement(() -> new Table(size)).translate();
	}

	Table newVarianceTable() {
		return (Table) translator().withStatement(() -> new Table(VARIANCE_TABLE_COL_SIZE)).translate();
	}

	
	Document newDocument() {
		return new Document(PageSize.A4.rotate());
	}

	private void addCell(final Table table, final Double value, final double scale) {
		translator().withStatement(() -> table.addCell(new Phrase(text(value, scale), tableCell))).translate();

	}
	
	private void addCell(final PdfPTable table, final List<Double> values, final List<String> names ) {
		
		
		final PdfPTable valueTable = new PdfPTable(2);
		
		IntStream.range(0, Math.min(values.size(), names.size())).forEach(i -> {valueTable.addCell(new Phrase(names.get(i))); valueTable.addCell(new Phrase(text(values.get(i), 1d), tableCell));});
		
		table.addCell(valueTable);
			
		

	}

	private void addCell(final Table table, final String value) {
		translator().withStatement(() -> table.addCell(new Phrase(value, tableCell))).translate();
		
	}

	private void add(final Document document, Element element) {
		translator().withStatement(() -> document.add(element)).translate();
	}

	private String text(final Double value, final double scale) {
		if (value == null) {
			return "";
		}
		return numberFormat.format(value * scale);
	}

	private void addCellHeader(final Table table, String text) {
		translator().withStatement(() -> table.addCell(new Phrase(text, tableHeadline))).translate();
	}

	private void addCellHeader(final Table table, final Double value, final double scale) {
		translator().withStatement(() -> table.addCell(new Phrase(text(value, scale), tableHeadline))).translate();
	}

	@SuppressWarnings("unchecked")
	private <R> ExceptionTranslationBuilder<R, AutoCloseable> translator() {
		return (ExceptionTranslationBuilder<R, AutoCloseable>) exceptionTranslationBuilder().withTranslation(IllegalStateException.class, Arrays.asList(BadElementException.class, DocumentException.class,  IOException.class));
	}

	@Lookup
	abstract ExceptionTranslationBuilder<?, AutoCloseable> exceptionTranslationBuilder();

}
