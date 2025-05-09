package mx.ift.sns.dao.port;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.port.EstatusSincronizacion;

/**
 * Interfaz de definición del DAO de Status de Portabilidad.
 */
public interface IEstatusSincronizacionDao extends IBaseDAO<EstatusSincronizacion> {

    /**
     * @return el estado
     */
    EstatusSincronizacion get();
}
