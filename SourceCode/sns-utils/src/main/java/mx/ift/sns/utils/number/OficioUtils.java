package mx.ift.sns.utils.number;

/**
 * 
 * @author jparanda
 * Esta clase implementa utilizades para los
 * numeros de oficios, como formatos especificos
 */
public final class OficioUtils {
	
	private static final int REQUERIRED_LENGTH=30;
	private static final String CHARACTER_CERO="0";
	
	/**
	 * Private constructor
	 */
	private OficioUtils(){}
	
	/**
	 * 
	 * @param oficioNumberIn
	 * @return
	 */
	public static String OficioFormat30Charecters(String oficioNumberIn){
		
		String oficioNumberOut="";
		
		if(!oficioNumberIn.equals("")){
			
			oficioNumberOut=oficioNumberIn.replace("/","").replace("-","");
			
			if(oficioNumberOut.length()<REQUERIRED_LENGTH)
			{
				
				while(oficioNumberOut.length()<REQUERIRED_LENGTH)
				{
					oficioNumberOut=CHARACTER_CERO.concat(oficioNumberOut);
				}
				
			}
			return oficioNumberOut;
		}else{
			
			for(int i=0;i<30;i++){
				oficioNumberOut=CHARACTER_CERO.concat(oficioNumberOut);
			}
				
			return oficioNumberOut;
		}
		
		
		
		
		
	}
	
	

}
