package mx.ift.sns.negocio.ac;

import java.util.List;

import mx.ift.sns.modelo.central.Estatus;

/**
 * Interfaz del servicio de Estatus.
 */
public interface IEstatusService {

    /**
     * Obtiene todos los estatus posibles del PST.
     * @return List<Estatus> listado
     */
    List<Estatus> findAllEstatus();

    /**
     * Obtiene el Estatus asociado al id.
     * @param idEstatus String
     * @return Estatus estatus
     */
    Estatus getEstatusById(String idEstatus);
}
