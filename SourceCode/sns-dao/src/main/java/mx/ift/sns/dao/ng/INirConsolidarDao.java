package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.ng.NirConsolidar;

/**
 * Interfaz de definición de los métodos para base de datos para NIRCONSOLIDAR.
 */
public interface INirConsolidarDao {

    /**
     * Método que devuelve todas los Nirs a consolidar para un abnConsolidar.
     * @param pId id
     * @return List<NirConsolidar>
     */
    List<NirConsolidar> findNirConsolidarById(BigDecimal pId);
}
