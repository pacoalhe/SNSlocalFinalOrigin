package mx.ift.sns.dao.port;

import java.math.BigDecimal;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.port.NumeroCancelado;

/**
 * Interfaz de definición del DAO de Status de Portabilidad.
 */
public interface INumerosCanceladosDAO extends IBaseDAO<NumeroCancelado> {

    /**
     * @param numero a buscar
     * @return el estado
     */
    NumeroCancelado get(String numero);

    /**
     * Borra el numero.
     * @param numero a borrar
     */
    void delete(String numero);

    /**
     * Obtiene el total de numeros cancelados en fecha actual del sistema.
     * @return total
     * @throws Exception error
     */
    BigDecimal getTotalNumerosCanceladosHoy() throws Exception;
}
