package de.mq.portfolio.shareportfolio.support;



import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;
import org.springframework.test.util.ReflectionTestUtils;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilder;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;
import junit.framework.Assert;

public class Portfolio2PdfConverterTest {
	
	
	private static final double CORRELATION = 0.2345;

	private static final double FULL_CORRELATION = 1.0d;

	private static final double TOTAL_RATE = 7.0e-2;
	
	private static final double TOTAL_RATE_DIVIDENDS = 4.2e-2;

	private static final double MIN_STDEV = 9.0e-3;

	private static final double RATE_DIVIDENDS_01 = 2.5e-3;

	private static final double RATE_01 = 5.0e-2;

	private static final double WEIGHT_01 = 0.4d;

	private static final double STD_01 = 1.0e-3;
	
	
	
	
	
	private static final double RATE_DIVIDENDS_02 = 3.5e-3;

	private static final double RATE_02 = 8.0e-2;

	private static final double WEIGHT_02 = 0.6d;

	private static final double STD_02 = 1.5e-3;
	
	private static final String WKN_02 = "wkn02";
	
	

	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("dd.MM.yy");

	private static final String WKN_01 = "wkn01";

	private static final Font FONT = FontFactory.getFont(FontFactory.TIMES, 10);

	private  final List<String> shares = Arrays.asList("share1","share2");

	private static final String CURRENCY_SYMBOL = "US$";

	private static final String CURRENCY_CODE = "USD";

	private static final String PORTFOLIO_NAME = "min-risk";

	@SuppressWarnings("unchecked")
	private final Converter<String, String> currencyConverter = (Converter<String, String>) Mockito.mock((Class<?>) Converter.class);
	
	private final Portfolio2PdfConverter portfolio2PdfConverter = Mockito.mock(Portfolio2PdfConverter.class, Mockito.CALLS_REAL_METHODS);
	
	private final  Document document = Mockito.mock(Document.class);
	
	private final PortfolioAO portfolioAO = Mockito.mock(PortfolioAO.class);
	
	private final Map<Class<?>,Object> dependencies = new HashMap<>();
	
	private final ArgumentCaptor<Element> elementCaptor = ArgumentCaptor.forClass(Element.class);
	
	private final ArgumentCaptor<Phrase> phraseVarianceTableCaptor = ArgumentCaptor.forClass(Phrase.class);
	
	private final ArgumentCaptor<Phrase> phraseCorrelationTableCaptor = ArgumentCaptor.forClass(Phrase.class);
	
	
	private final Table varianceTable = Mockito.mock(Table.class);
	
	private final Table correlationTable = Mockito.mock(Table.class);
	
	
	private final TimeCourse timeCourse01 = Mockito.mock(TimeCourse.class);
	
	private final Share share01 = Mockito.mock(Share.class);
	
	private final TimeCourse timeCourse02 = Mockito.mock(TimeCourse.class);
	
	private final Share share02 = Mockito.mock(Share.class);
	
	
	private final Date startDate = asDate(LocalDateTime.now().minusDays(1));
	
	private final Date endDate = asDate(LocalDateTime.now());
	
	private final NumberFormat numberFormat = NumberFormat.getInstance();
	
	private Map<TimeCourse,Double> weights = new HashMap<>();
	
	private final List<Entry<String, Map<String, Double>>> correlations = new ArrayList<>();
	
	//private final ExceptionTranslationBuilder<byte[], AutoCloseable> exceptionTranslationBuilder = new ExceptionTranslationBuilderImpl<>();
	
	Date asDate(LocalDateTime localDateTime) {
	    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	  }

	
	@Before
	public final void setup() throws BadElementException {
		
		
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumFractionDigits(2);
		
		Mockito.doAnswer(in -> new ExceptionTranslationBuilderImpl<Table,AutoCloseable>()).when(portfolio2PdfConverter).exceptionTranslationBuilder();	
		Mockito.when(portfolio2PdfConverter.newDocument()).thenReturn(document);
		Mockito.when(portfolio2PdfConverter.newVarianceTable()).thenReturn(varianceTable);
		Mockito.when(portfolio2PdfConverter.newCorrelationTable(shares.size()+1)).thenReturn(correlationTable);
		
	
		
		Mockito.when(portfolioAO.getShares()).thenReturn(shares);
		
		
		Mockito.when(portfolioAO.getName()).thenReturn(PORTFOLIO_NAME);
		Mockito.when(portfolioAO.getCurrency()).thenReturn(CURRENCY_CODE);
		Mockito.when(currencyConverter.convert(CURRENCY_CODE)).thenReturn(CURRENCY_SYMBOL);
		
		Mockito.when(timeCourse01.end()).thenReturn(endDate);
		Mockito.when(timeCourse01.start()).thenReturn(startDate);
		Mockito.when(share01.name()).thenReturn(shares.get(0));
		Mockito.when(timeCourse01.wkn()).thenReturn(WKN_01);
		Mockito.when(timeCourse01.share()).thenReturn(share01);
		Mockito.when(timeCourse01.standardDeviation()).thenReturn(STD_01);
		Mockito.when(timeCourse01.totalRate()).thenReturn(RATE_01);
		Mockito.when(timeCourse01.totalRateDividends()).thenReturn(RATE_DIVIDENDS_01);
		
		Mockito.when(timeCourse02.end()).thenReturn(endDate);
		
		Mockito.when(timeCourse02.start()).thenReturn(startDate);
		
		Mockito.when(share02.name()).thenReturn(shares.get(1));
		Mockito.when(timeCourse02.wkn()).thenReturn(WKN_02);
		Mockito.when(timeCourse02.share()).thenReturn(share02);
		Mockito.when(timeCourse02.standardDeviation()).thenReturn(STD_02);
		Mockito.when(timeCourse02.totalRate()).thenReturn(RATE_02);
		Mockito.when(timeCourse02.totalRateDividends()).thenReturn(RATE_DIVIDENDS_02);
		
		
		Mockito.when(timeCourse02.share()).thenReturn(share02);
		
		
		Mockito.when(portfolioAO.getTimeCourses()).thenReturn(Arrays.asList(timeCourse01, timeCourse02));
		
		weights.put(timeCourse01, WEIGHT_01);
		weights.put(timeCourse02, WEIGHT_02);
		
		Mockito.when(portfolioAO.getWeights()).thenReturn(weights);
		
		Mockito.when(portfolioAO.getMinStandardDeviation()).thenReturn(MIN_STDEV);
		Mockito.when(portfolioAO.getTotalRate()).thenReturn(TOTAL_RATE);
		
		Mockito.when(portfolioAO.getTotalRateDividends()).thenReturn(TOTAL_RATE_DIVIDENDS);
		
		final Map<String, Double> correlationsMap01 = new HashMap<>();
		correlationsMap01.put(shares.get(0), FULL_CORRELATION);
		correlationsMap01.put(shares.get(1), CORRELATION);
		
		final Map<String, Double> correlationsMap02 = new HashMap<>();
		correlationsMap02.put(shares.get(0), CORRELATION);
		correlationsMap02.put(shares.get(1), FULL_CORRELATION);
		correlations.add(new AbstractMap.SimpleImmutableEntry<>(shares.get(0), correlationsMap01));
		correlations.add(new AbstractMap.SimpleImmutableEntry<>(shares.get(1), correlationsMap02));
		
		Mockito.when(portfolioAO.getCorrelations()).thenReturn(correlations);
	}
	
	@Test
	public final void convert() throws DocumentException {
		
		
		dependencies.put(NumberFormat.class, numberFormat);
		dependencies.put(DateFormat.class,DATEFORMAT);
		dependencies.put(Font.class,FONT);
		dependencies.put(Converter.class,currencyConverter);
		
		Arrays.asList(Portfolio2PdfConverter.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(portfolio2PdfConverter, field.getName(), dependencies.get(field.getType())));
		
	
		portfolio2PdfConverter.convert(portfolioAO);
		
		
		
		Mockito.verify(document).open();
		Mockito.verify(document).addTitle(PORTFOLIO_NAME);
		
		Mockito.verify(document, Mockito.atLeastOnce()).add(elementCaptor.capture());
		
		final List<String> headlines = elementCaptor.getAllValues().stream().filter(e -> e.getClass().equals(Paragraph.class) ).map(p -> ((Paragraph)p).getContent()).collect(Collectors.toList());
		
		Assert.assertEquals(2, headlines.size());
		Assert.assertEquals(String.format(Portfolio2PdfConverter.HEADLINE_SHARE_PATTERN, PORTFOLIO_NAME, CURRENCY_SYMBOL),headlines.get(0));
		Assert.assertEquals(String.format(Portfolio2PdfConverter.HEADLINE_CORRELATION_PATTERN, PORTFOLIO_NAME), headlines.get(1));
		
		final List<Element> tables = elementCaptor.getAllValues().stream().filter(e -> Table.class.isInstance(e) ).collect(Collectors.toList());
		Assert.assertEquals(2, tables.size());
		Assert.assertEquals(varianceTable, tables.get(0));
		Assert.assertEquals(correlationTable, tables.get(1));
		
		Mockito.verify(varianceTable).setWidth(Portfolio2PdfConverter.WIDTH_TABLE);
		
		Mockito.verify(varianceTable, Mockito.atLeastOnce()).addCell(phraseVarianceTableCaptor.capture());
		
		Mockito.verify(varianceTable).setAlignment(Element.ALIGN_LEFT);
		Assert.assertEquals(4*Portfolio2PdfConverter.VARIANCE_TABLE_COL_SIZE, phraseVarianceTableCaptor.getAllValues().size());
		
		phraseVarianceTableCaptor.getAllValues().stream().map(p -> p.getFont()).forEach(f ->Assert.assertEquals(FONT, f));
		
		final List<String> varianceTableCells = phraseVarianceTableCaptor.getAllValues().stream().map(p -> p.getContent()).collect(Collectors.toList());
		
		Assert.assertEquals(Portfolio2PdfConverter.VARIANCE_TABLE_SHARE_HEADER, varianceTableCells.get(0));
		Assert.assertEquals(Portfolio2PdfConverter.VARIANCE_TABLE_WKN_HEADER, varianceTableCells.get(1));
		Assert.assertEquals(Portfolio2PdfConverter.VARIANCE_TABLE_TIME_FRAME_HEADER, varianceTableCells.get(2));
		Assert.assertEquals(Portfolio2PdfConverter.VARIANCE_TABLE_STANDARD_DEVIATION_HEADER, varianceTableCells.get(3));
		Assert.assertEquals(Portfolio2PdfConverter.VARIANCE_TABLE_RATIO_HEADER,  varianceTableCells.get(4));
		Assert.assertEquals(Portfolio2PdfConverter.VARIANCE_TABLE_RATE_HEADER , varianceTableCells.get(5));
		Assert.assertEquals(Portfolio2PdfConverter.VARIANCE_TABLE_RATE_DIVIDENDS_HEADER , varianceTableCells.get(6));
		
		Assert.assertEquals(shares.get(0) , varianceTableCells.get(7));
		Assert.assertEquals(WKN_01 , varianceTableCells.get(8));
		Assert.assertEquals( String.format(Portfolio2PdfConverter.DATE_RANGE_PATTERN, DATEFORMAT.format(startDate) , DATEFORMAT.format(endDate)) ,  varianceTableCells.get(9) ) ; ;
		Assert.assertEquals(numberFormat.format(STD_01 * 1000), varianceTableCells.get(10));
		Assert.assertEquals(numberFormat.format(WEIGHT_01 * 100), varianceTableCells.get(11));
		Assert.assertEquals(numberFormat.format(RATE_01 * 100),varianceTableCells.get(12));
		Assert.assertEquals(numberFormat.format(RATE_DIVIDENDS_01 * 100),varianceTableCells.get(13));
		
		
		Assert.assertEquals(shares.get(1) , varianceTableCells.get(14));
		Assert.assertEquals(WKN_02 , varianceTableCells.get(15));
		Assert.assertEquals( String.format(Portfolio2PdfConverter.DATE_RANGE_PATTERN, DATEFORMAT.format(startDate) , DATEFORMAT.format(endDate)) ,  varianceTableCells.get(16) ) ; ;
		Assert.assertEquals(numberFormat.format(STD_02 * 1000), varianceTableCells.get(17));
		Assert.assertEquals(numberFormat.format(WEIGHT_02 * 100), varianceTableCells.get(18));
		Assert.assertEquals(numberFormat.format(RATE_02 * 100),varianceTableCells.get(19));
		Assert.assertEquals(numberFormat.format(RATE_DIVIDENDS_02 * 100),varianceTableCells.get(20));
		
		Assert.assertTrue(varianceTableCells.get(21).isEmpty());
		Assert.assertTrue(varianceTableCells.get(22).isEmpty());
		Assert.assertTrue(varianceTableCells.get(23).isEmpty());
		Assert.assertEquals(numberFormat.format(MIN_STDEV * 1000),varianceTableCells.get(24));
		Assert.assertTrue(varianceTableCells.get(25).isEmpty());
		Assert.assertEquals(numberFormat.format(TOTAL_RATE * 100),varianceTableCells.get(26));
		
		Assert.assertEquals(numberFormat.format(TOTAL_RATE_DIVIDENDS * 100), varianceTableCells.get(27));
		
		
		Mockito.verify(correlationTable).setAlignment(Element.ALIGN_LEFT);
		Mockito.verify(correlationTable).setWidth(Portfolio2PdfConverter.WIDTH_TABLE);
		
		Mockito.verify(correlationTable, Mockito.atLeastOnce()).addCell(phraseCorrelationTableCaptor.capture());
		final List<String> correlationTableCells = phraseCorrelationTableCaptor.getAllValues().stream().map(p -> p.getContent()).collect(Collectors.toList());
		Assert.assertEquals(Math.pow(1+shares.size(), 2), (double) correlationTableCells.size());
		
		Assert.assertEquals(Portfolio2PdfConverter.CORRELATION_TABLE_CORRELATIONS_HEADER, correlationTableCells.get(0));
		Assert.assertEquals(shares.get(0), correlationTableCells.get(1));
		Assert.assertEquals(shares.get(1), correlationTableCells.get(2));
		
		Assert.assertEquals(shares.get(0), correlationTableCells.get(3));
		Assert.assertEquals(numberFormat.format(100* FULL_CORRELATION), correlationTableCells.get(4));
		Assert.assertEquals(numberFormat.format(CORRELATION*100), correlationTableCells.get(5));
		
		Assert.assertEquals(shares.get(1), correlationTableCells.get(6));
		Assert.assertEquals(numberFormat.format(CORRELATION*100), correlationTableCells.get(7));
		Assert.assertEquals(numberFormat.format(100 * FULL_CORRELATION), correlationTableCells.get(8));
		
		Mockito.verify(document).close();
		
	}
	
	@Test
	public final void dependencies() {
		final Portfolio2PdfConverter portfolio2PdfConverter = new Portfolio2PdfConverter(currencyConverter) {

			@Override
			final ExceptionTranslationBuilder<?, AutoCloseable> exceptionTranslationBuilder() {
				return null;
			}};
			
			final Map<Class<?>, Collection<Object>> dependencies = new HashMap<>();
			Arrays.asList(Portfolio2PdfConverter.class.getDeclaredFields()).stream().filter(field -> ! Modifier.isStatic(field.getModifiers())).forEach(field -> {
				if( ! dependencies.containsKey(field.getType())) {
					dependencies.put(field.getType(), new HashSet<>());
				}
				
				dependencies.get(field.getType()).add(ReflectionTestUtils.getField(portfolio2PdfConverter, field.getName()));
			});
			
			Assert.assertEquals(4, dependencies.size());
			Assert.assertTrue(dependencies.containsKey(Font.class));
			Assert.assertEquals(3, dependencies.get(Font.class).size());
			
			Assert.assertTrue(dependencies.containsKey(NumberFormat.class));
			Assert.assertEquals(1, dependencies.get(NumberFormat.class).size());
			
			Assert.assertTrue(dependencies.containsKey(DateFormat.class));
			Assert.assertEquals(1, dependencies.get(DateFormat.class).size());
			
			Assert.assertTrue(dependencies.containsKey(Converter.class));
			Assert.assertEquals(1, dependencies.get(Converter.class).size());
			Assert.assertEquals(currencyConverter, dependencies.get(Converter.class).stream().findAny().get());
			
	}
	
}
