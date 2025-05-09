package mx.ift.sns.dao.ng;

import java.util.List;

import mx.ift.sns.modelo.central.Estatus;

/**
 * Interfaz de definición de los métodos para base de datos para Estatus del catálogo.
 */
public interface IEstatusDao {
    /**
     * Obtiene el listado de Estatus que puede tener un objeto de catálogo.
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
