package mx.ift.sns.dao.ng.implementation;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.ILineaArrendatarioDao;
import mx.ift.sns.modelo.lineas.LineaArrendatario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO de Linea Arrendatario.
 * @author X36155QU
 */
@Named
public class LineaArrendatarioDaoImpl extends BaseDAO<LineaArrendatario> implements ILineaArrendatarioDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LineaArrendatarioDaoImpl.class);

    @Override
    public LineaArrendatario saveLineaArrendatario(LineaArrendatario lineaArrendatario) {

        LineaArrendatario dato = getEntityManager().merge(lineaArrendatario);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Reporte Linea Arrendatario almacenado, Referencia: " + dato.getReferencia());
        }

        return dato;
    }
}
