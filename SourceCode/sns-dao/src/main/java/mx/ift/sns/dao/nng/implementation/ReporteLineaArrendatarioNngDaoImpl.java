package mx.ift.sns.dao.nng.implementation;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IReporteLineaArrendatarioNngDao;
import mx.ift.sns.modelo.lineas.ReporteLineaArrendatarioNng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO de Linea Arrendatario.
 * @author X36155QU
 */
@Named
public class ReporteLineaArrendatarioNngDaoImpl extends BaseDAO<ReporteLineaArrendatarioNng> implements
        IReporteLineaArrendatarioNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteLineaArrendatarioNngDaoImpl.class);

    @Override
    public ReporteLineaArrendatarioNng saveLineaArrendatario(ReporteLineaArrendatarioNng lineaArrendatario) {

        ReporteLineaArrendatarioNng dato = getEntityManager().merge(lineaArrendatario);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Reporte Linea Arrendatario almacenado, Referencia: " + dato.getReferencia());
        }

        return dato;
    }
}
