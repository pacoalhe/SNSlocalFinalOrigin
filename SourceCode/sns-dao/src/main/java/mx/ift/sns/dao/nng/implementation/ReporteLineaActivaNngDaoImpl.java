package mx.ift.sns.dao.nng.implementation;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IReporteLineaActivaNngDao;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaNng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion del DAO de lineas activas.
 * @author X36155Q
 */
@Named
public class ReporteLineaActivaNngDaoImpl extends BaseDAO<ReporteLineaActivaNng> implements IReporteLineaActivaNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteLineaActivaNngDaoImpl.class);

    @Override
    public ReporteLineaActivaNng saveLineaActiva(ReporteLineaActivaNng lineaActiva) {

        ReporteLineaActivaNng dato = getEntityManager().merge(lineaActiva);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Reporte Linea Activa almacenado, Referencia: " + dato.getReferencia());
        }

        return dato;
    }
}
