package mx.ift.sns.dao.ng;

import java.util.List;

import mx.ift.sns.modelo.abn.EstadoAbn;

/**
 * Interfaz de definición de los métodos para base de datos de estatus de área de numeración.
 */
public interface IEstadoAbnDao {

    /**
     * Recupera el catálogo de estados de ABN.
     * @return List
     */
    List<EstadoAbn> findAllEstadosAbn();

    /**
     * Recupera un EstadoABN por su código.
     * @param pCodigo Código de EstadoABN
     * @return EstadoAbn
     */
    EstadoAbn getEstadoAbnByCodigo(String pCodigo);

}
