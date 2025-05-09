package mx.ift.sns.dao.port;

import java.math.BigDecimal;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.port.NumeroPortado;

/**
 * Interfaz de definición del DAO de Status de Portabilidad.
 */
public interface INumerosPortadosDAO extends IBaseDAO<NumeroPortado> {

    /**
     * @param numero a buscar
     * @return el estado
     */
    NumeroPortado get(String numero);

    /**
     * Borra el numero.
     * @param numero a borrar
     */
    void delete(String numero);

    /**
     * Obtiene el total de numeros portados en fecha actual del sistema.
     * @return total
     * @throws Exception error
     */
    BigDecimal getTotalNumerosPortadosHoy() throws Exception;
}
