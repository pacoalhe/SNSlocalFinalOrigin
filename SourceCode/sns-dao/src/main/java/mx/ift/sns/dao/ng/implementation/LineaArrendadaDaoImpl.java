package mx.ift.sns.dao.ng.implementation;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.ILineaArrendadaDao;
import mx.ift.sns.modelo.lineas.LineaArrendada;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO de Linea Arrendada.
 * @author X36155QU
 */
@Named
public class LineaArrendadaDaoImpl extends BaseDAO<LineaArrendada> implements ILineaArrendadaDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LineaArrendadaDaoImpl.class);

    @Override
    public LineaArrendada saveLineaArrendada(LineaArrendada lineaArrendada) {

        LineaArrendada dato = getEntityManager().merge(lineaArrendada);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Reporte Linea Arrendada almacenado, Referencia: " + dato.getReferencia());
        }

        return dato;
    }
}
