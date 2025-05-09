package mx.ift.sns.dao.nng.implementation;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IReporteLineaActivaDetNngDao;
import mx.ift.sns.modelo.lineas.ReporteLineaActivaDetNng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion del DAO de lineas activas detallada.
 * @author X36155QU
 */
@Named
public class ReporteLineaActivaDetNngDaoImpl extends BaseDAO<ReporteLineaActivaDetNng> implements
        IReporteLineaActivaDetNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteLineaActivaDetNngDaoImpl.class);

    @Override
    public ReporteLineaActivaDetNng saveLineaActivaDet(ReporteLineaActivaDetNng lineaActivaDet) {

        ReporteLineaActivaDetNng dato = getEntityManager().merge(lineaActivaDet);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Reporte Linea Activa almacenado, Referencia: " + dato.getReferencia());
        }

        return dato;
    }

}
