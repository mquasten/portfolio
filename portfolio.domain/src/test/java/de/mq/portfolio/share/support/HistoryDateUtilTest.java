package de.mq.portfolio.share.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.support.DataAccessUtils;


public class HistoryDateUtilTest {
	
	private final HistoryDateUtil historyDateUtil = new HistoryDateUtil();
	
	
	public  Date dateDaysBack(final int daysBack) {
		final Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DAY_OF_YEAR, -daysBack);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	} 
	
	
	@Test
	public final void oneYearBack() {
		Assert.assertEquals(dateDaysBack(HistoryDateUtil.OFFSET_DAYS_ONE_YEAR_BACK), historyDateUtil.getOneYearBack());
	}

	@Test
	public final void getOneDayBack() {
		Assert.assertEquals(dateDaysBack(HistoryDateUtil.OFFSET_DAYS_ONE_DAY_BACK), historyDateUtil.getOneDayBack());
	}
	
	@Test
	public final void dateDaysBack() {
		final int tenDaysBack = 10;
		Assert.assertEquals(dateDaysBack(tenDaysBack), historyDateUtil.dateDaysBack(tenDaysBack));
	}
	
	@Test
	public final void oneYearBackString() {
		final DateFormat dateFormat = new SimpleDateFormat(HistoryDateUtil.GERMAN_YEAR_TO_DAY_DATE_FORMAT);
		Assert.assertEquals(dateFormat.format(dateDaysBack(HistoryDateUtil.OFFSET_DAYS_ONE_YEAR_BACK)), historyDateUtil.oneYearBack(dateFormat));
	}

	
	@Test
	public final void oneDayBackString() {
		final DateFormat dateFormat = new SimpleDateFormat(HistoryDateUtil.GOOGLE_DATE_FORMAT,Locale.US);
		Assert.assertEquals(dateFormat.format(dateDaysBack(HistoryDateUtil.OFFSET_DAYS_ONE_DAY_BACK)), historyDateUtil.oneDayBack(dateFormat));
	}
	
	@Test
	public final void googleDateFormat() {
		Assert.assertEquals(new SimpleDateFormat(HistoryDateUtil.GOOGLE_DATE_FORMAT, Locale.US), historyDateUtil.getGoogleDateFormat());
	}
	
	@Test
	public final void germanYearToDayDateFormat() {
		Assert.assertEquals(new SimpleDateFormat(HistoryDateUtil.GERMAN_YEAR_TO_DAY_DATE_FORMAT, Locale.GERMANY), historyDateUtil.getGermanYearToDayDateFormat());
	}
	
	@Test
	public final void basename() {
		Assert.assertEquals("SAP", HistoryDateUtil.basename("SAP.DE"));
		Assert.assertTrue(HistoryDateUtil.basename(null).isEmpty());
	} 
	
	@Test
	public final void basenameMethod() {
		final Method method = HistoryDateUtil.basenameMethod();
		Assert.assertEquals(HistoryDateUtil.BASENAME_METHOD_NAME, method.getName());
		Assert.assertEquals(1, method.getParameterTypes().length);
		Assert.assertEquals(String.class, method.getParameterTypes()[0]);
	}
	
	@Test
	public final void basenameMethodSucks() throws SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Method m = DataAccessUtils.requiredSingleResult(Arrays.asList(HistoryDateUtil.class.getDeclaredMethods()).stream().filter(method  -> Modifier.isStatic(method.getModifiers()) && method.getReturnType().equals(Method.class) && method.getParameterTypes().length==1 && method.getParameterTypes()[0].equals(String.class)).collect(Collectors.toList()));
		m.setAccessible(true);
		try{
		m.invoke(null, "dontLetMeGetMe");
		Assert.fail(InvocationTargetException.class.getName() + " should be raised.");
		} catch (InvocationTargetException ex ){

			Assert.assertTrue(ex.getTargetException() instanceof IllegalStateException);
	
		}
		
	}


	

	
}
