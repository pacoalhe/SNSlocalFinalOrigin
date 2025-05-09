package mx.ift.sns.negocio.num;

import mx.ift.sns.negocio.num.model.Numero;

/**
 * Servicio de numeracion.
 */
public interface INumeracionService {

    /**
     * Parsea una numeracion y devuelve numero al que corresponde (el nir, la serie y numeracion corta).
     * @param numeracion nir + sna + num o clave + sna + num
     * @return numero
     */
    Numero parseNumeracion(String numeracion);
}
