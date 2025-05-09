package mx.ift.sns.utils.number;

/**
 * 
 * @author jparanda
 *
 */
public final class IdentificadoresPST {
	
	private static final int REQUERIRED_LENGTH=3;
	private static final String CHARACTER_CERO="0";
	
	/**
	 * Private constructor
	 */
	private IdentificadoresPST() {
		// TODO Auto-generated constructor stub
	}
	
	public static String ABCIDOIDAto30Charecters(String abcidoIn){
		
		String abcidoOut="";
		
		if(!abcidoIn.equals("")){
			
			abcidoOut=abcidoIn;
			if(abcidoOut.length()<REQUERIRED_LENGTH)
			{
				
				while(abcidoOut.length()<REQUERIRED_LENGTH)
				{
					abcidoOut=CHARACTER_CERO.concat(abcidoOut);
				}
				
			}
			return abcidoOut;
		}else{
			
			for(int i=0;i<30;i++){
				abcidoOut=CHARACTER_CERO.concat(abcidoOut);
			}
				
			return abcidoOut;
		}

}
	public static String BCDIDOIDAto30Charecters(String bcdidoIn){

		String bcdidoOut="";

		if(!bcdidoIn.equals("")){

			bcdidoOut=bcdidoIn;
			if(bcdidoOut.length()<REQUERIRED_LENGTH)
			{

				while(bcdidoOut.length()<REQUERIRED_LENGTH)
				{
					bcdidoOut=CHARACTER_CERO.concat(bcdidoOut);
				}

			}
			return bcdidoOut;
		}else{

			for(int i=0;i<30;i++){
				bcdidoOut=CHARACTER_CERO.concat(bcdidoOut);
			}

			return bcdidoOut;
		}

	}
}
