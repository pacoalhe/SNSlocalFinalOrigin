package mx.ift.sns.dao.ng;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.central.CentralesRelacion;

/**
 * Interface del DAO de la tabla que relaciona centrales de origen y destino.
 * @author X36155QU
 */
public interface ICentralesRelacionDao extends IBaseDAO<CentralesRelacion> {

    /**
     * Persite la relacion entre centrales de origen y destino.
     * @param centralesRelacion relacion
     * @return CentralesRelacion relacion
     */
    CentralesRelacion saveCentralesRelacion(CentralesRelacion centralesRelacion);

}
