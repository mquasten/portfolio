package de.mq.portfolio.shareportfolio.support;



import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.lowagie.text.Table;

import junit.framework.Assert;

public class Portfolio2PdfConverterTest {
	
	
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
	
	
	private final Table varianceTable = Mockito.mock(Table.class);
	
	private final Table correlationTable = Mockito.mock(Table.class);
	
	
	@Before
	public final void setup() throws BadElementException {
		Mockito.when(portfolio2PdfConverter.newDocument()).thenReturn(document);
		Mockito.when(portfolio2PdfConverter.newVarianceTable()).thenReturn(varianceTable);
		Mockito.when(portfolio2PdfConverter.newCorrelationTable(shares.size()+1)).thenReturn(correlationTable);
		
		Mockito.when(portfolioAO.getShares()).thenReturn(shares);
		
		
		Mockito.when(portfolioAO.getName()).thenReturn(PORTFOLIO_NAME);
		Mockito.when(portfolioAO.getCurrency()).thenReturn(CURRENCY_CODE);
		Mockito.when(currencyConverter.convert(CURRENCY_CODE)).thenReturn(CURRENCY_SYMBOL);
	}
	
	@Test
	public final void convert() throws DocumentException {
		
		dependencies.put(NumberFormat.class, NumberFormat.getInstance());
		dependencies.put(DateFormat.class,new SimpleDateFormat("dd.MM.yy"));
		dependencies.put(Font.class,FontFactory.getFont(FontFactory.TIMES, 10));
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
		
		Mockito.verify(varianceTable).setWidth(Portfolio2PdfConverter.SIZE_VARIANCE_TABLE);
	}
	
}
