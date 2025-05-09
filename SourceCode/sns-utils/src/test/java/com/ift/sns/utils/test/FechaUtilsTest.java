package com.ift.sns.utils.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import mx.ift.sns.utils.date.FechasUtils;

public class FechaUtilsTest {
	
	 private static Locale localeES = new Locale("es", "ES");
	
	@Test
	public void fechaToStringTest(){
		SimpleDateFormat sdfCustom = new SimpleDateFormat("YYYYMMdd", localeES);
		String fechaEsperada=sdfCustom.format(new Date());
		String resultFecha=FechasUtils.fechaToString(new Date(),"YYYYMMdd");
		Assert.assertTrue(fechaEsperada.equals(resultFecha));
		
		
	}

}
