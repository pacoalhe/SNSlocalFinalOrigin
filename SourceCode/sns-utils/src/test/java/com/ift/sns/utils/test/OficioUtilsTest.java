package com.ift.sns.utils.test;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import mx.ift.sns.utils.number.OficioUtils;

public class OficioUtilsTest {
	
	@Ignore
	@Test
	public void format30CharectersTest(){
		
		String oficioEsperado="000000000IFT223UCSDGAUSE592815";
		String responde=OficioUtils.OficioFormat30Charecters("IFT/223/UCS/DG-AUSE/5928/15");
		Assert.assertTrue(oficioEsperado.equals(responde));
		
	}
	
	@Test
	public void format30CharectersVacioTest(){
		
		String oficioEsperado="000000000000000000000000000000";
		String responde=OficioUtils.OficioFormat30Charecters("");
		Assert.assertTrue(oficioEsperado.equals(responde));
		
	}

}
