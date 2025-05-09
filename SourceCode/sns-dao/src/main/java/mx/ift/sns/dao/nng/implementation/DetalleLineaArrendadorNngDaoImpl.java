package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IDetalleLineaArrendadorNngDao;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendadorNng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion del DAO de DetLineaArrendada.
 * @author X36155QU
 */
public class DetalleLineaArrendadorNngDaoImpl extends BaseDAO<DetalleLineaArrendadorNng> implements
        IDetalleLineaArrendadorNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetalleLineaArrendadorNngDaoImpl.class);

    @Override
    public void saveDetLineaArrendada(List<String> listaDatos, BigDecimal idReporte) {
        String squery = "INSERT INTO NNG_REP_LIN_ARRENDADOR_DET VALUES (SEQ_ID_NNG_RDLIN_ARR.NextVal,?,?,?,?,?,?,?)";
        try {
            for (int i = 0; i < listaDatos.size(); i++) {
                String[] datos = listaDatos.get(i).split(":");
                Query query = getEntityManager().createNativeQuery(squery);
                query.setParameter(1, idReporte);
                for (int j = 0; j < datos.length; j++) {
                    query.setParameter(j + 2, datos[j]);
                }
                query.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    @Override
    public List<DetalleLineaArrendadorNng> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first,
            int pageSize) {

        String squery = "SELECT d FROM DetalleLineaArrendadorNng d WHERE d.reporteLineaArrendador.id = :reporte";

        TypedQuery<DetalleLineaArrendadorNng> tquery = getEntityManager().createQuery(squery,
                DetalleLineaArrendadorNng.class);
        tquery.setParameter("reporte", idReporte);

        return tquery.setFirstResult(first * pageSize).setMaxResults(pageSize).getResultList();

    }

}
