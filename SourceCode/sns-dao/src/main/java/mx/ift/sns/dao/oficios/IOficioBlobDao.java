package mx.ift.sns.dao.oficios;

import java.math.BigDecimal;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.oficios.OficioBlob;

/**
 * Interfaz de definición de los métodos para base de datos para la generación de Oficios Blob.
 */
public interface IOficioBlobDao extends IBaseDAO<OficioBlob> {

    /**
     * Recupera la instancia de base de datos del objeto OficioBlob solicitado.
     * @param pOficioBlobId Identificador del objeto OficioBlob
     * @return Objeto OficioBlob almacenado en bbdd.
     */
    OficioBlob getOficioBlob(BigDecimal pOficioBlobId);
}
