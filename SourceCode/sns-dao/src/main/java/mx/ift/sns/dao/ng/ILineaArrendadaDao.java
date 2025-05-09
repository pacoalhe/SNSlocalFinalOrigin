package mx.ift.sns.dao.ng;

import mx.ift.sns.modelo.lineas.LineaArrendada;

/**
 * Interfaz del DAO de linea arrendada.
 * @author X36155QU
 */
public interface ILineaArrendadaDao {

    /**
     * Guarda una linea arrendatario.
     * @param lineaArrendada lineaArrendada
     * @return lineaArrendada
     */
    LineaArrendada saveLineaArrendada(LineaArrendada lineaArrendada);

}
