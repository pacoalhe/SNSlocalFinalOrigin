package mx.ift.sns.dao.ng;

import mx.ift.sns.modelo.lineas.LineaArrendatario;

/**
 * Interfaz del DAO de linea arrendatario.
 * @author X36155QU
 */
public interface ILineaArrendatarioDao {

    /**
     * Guarda una linea arrendatario.
     * @param lineaArrendatario lineaArrendatario
     * @return LineaArrendatario
     */
    LineaArrendatario saveLineaArrendatario(LineaArrendatario lineaArrendatario);

}
