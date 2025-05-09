package mx.ift.sns.dao.ot;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.ot.Region;

/**
 * Interfaz de definición de los métodos para base de datos para NIR.
 */
public interface IRegionDao {

    /**
     * Obtiene la region por su id.
     * @param id region
     * @return region
     */
    Region getRegionById(BigDecimal id);

    /**
     * Obtiene la lista de Regiones.
     * @return lista region
     */
    List<Region> findAllRegiones();

}
