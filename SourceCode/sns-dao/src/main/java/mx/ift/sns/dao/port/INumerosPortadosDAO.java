package mx.ift.sns.dao.port;

import java.math.BigDecimal;
import java.util.Date;

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

    /** /**
     * FJAH 27.05.2025 Refactorización.
     * Obtiene el total de números portados para una fecha de actionDate específica.
     * @param fechaActionDate Fecha a consultar (generalmente, la extraída del archivo CSV/XML)
     * @return total
     * @throws Exception error
     */
    BigDecimal getTotalNumerosPortadosPorFecha(Date fechaActionDate) throws Exception;



}
