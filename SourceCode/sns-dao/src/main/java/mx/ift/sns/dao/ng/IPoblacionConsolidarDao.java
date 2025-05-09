package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.ng.PoblacionConsolidar;

/**
 * Interfaz de definición de los métodos para base de datos para NIRCONSOLIDAR.
 */
public interface IPoblacionConsolidarDao {

    /**
     * Método que devuelve todas las poblaciones a consolidar para un abnConsolidar.
     * @param pId id
     * @return List<PoblacionConsolidar>
     */
    List<PoblacionConsolidar> findPoblacionConsolidarById(BigDecimal pId);
}
