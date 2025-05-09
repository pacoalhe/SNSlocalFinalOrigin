package mx.ift.sns.dao.nng.implementation;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IReporteLineaArrendadorNngDao;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendadorNng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO de Linea Arrendada.
 * @author X36155QU
 */
@Named
public class ReporteLineaArrendadorNngDaoImpl extends BaseDAO<ReporteLineaArrendadorNng> implements
        IReporteLineaArrendadorNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteLineaArrendadorNngDaoImpl.class);

    @Override
    public ReporteLineaArrendadorNng saveLineaArrendada(ReporteLineaArrendadorNng lineaArrendada) {

        ReporteLineaArrendadorNng dato = getEntityManager().merge(lineaArrendada);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Reporte Linea Arrendada almacenado, Referencia: " + dato.getReferencia());
        }

        return dato;
    }
}
