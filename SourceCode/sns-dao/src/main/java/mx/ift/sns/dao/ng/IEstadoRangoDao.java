package mx.ift.sns.dao.ng;

import java.util.List;

import mx.ift.sns.modelo.series.EstadoRango;

/**
 * Interfaz de definición de los métodos para base de datos de estados de rango.
 */
public interface IEstadoRangoDao {

    /**
     * Recupera el catálogo estados de rangos.
     * @return List
     */
    List<EstadoRango> findAllEstadosRango();

    /**
     * Devuelve un objeto EstadoRango por su codigo.
     * @param codigo codigo
     * @return EstadoRango
     */
    EstadoRango getEstadoRangoByCodigo(String codigo);
}
