package mx.ift.sns.web.frontend.areas;

import java.util.Comparator;
import mx.ift.sns.modelo.abn.Abn;

public class comparaAbn  implements Comparator<Abn>{
	
	@Override
	public int compare(Abn o1, Abn o2) {
		return (o1.getNombre()).compareTo(o2.getNombre());
	}
	


}
