package mx.ift.sns.dao.ng.implementation;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.ICentralesRelacionDao;
import mx.ift.sns.modelo.central.CentralesRelacion;
import mx.ift.sns.modelo.central.CentralesRelacionPK;

/**
 * DAO de la tabla que relaciona centrales de origen y destino.
 * @author X36155QU
 */
public class CentralesRelacionDaoImpl extends BaseDAO<CentralesRelacion> implements ICentralesRelacionDao {

    @Override
    public CentralesRelacion saveCentralesRelacion(CentralesRelacion centralesRelacion) {

        CentralesRelacionPK id = new CentralesRelacionPK();
        id.setIdCentralDestino(centralesRelacion.getCentralDestino().getId());
        id.setIdCentralOrigen(centralesRelacion.getCentralOrigen().getId());
        centralesRelacion.setId(id);

        return this.saveOrUpdate(centralesRelacion);
    }

}
