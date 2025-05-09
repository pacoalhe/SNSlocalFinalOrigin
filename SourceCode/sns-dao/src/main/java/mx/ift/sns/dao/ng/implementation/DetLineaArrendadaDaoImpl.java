package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IDetLineaArrendadaDao;
import mx.ift.sns.modelo.lineas.DetLineaArrendada;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion del DAO de DetLineaArrendada.
 * @author X36155QU
 */
public class DetLineaArrendadaDaoImpl extends BaseDAO<DetLineaArrendada> implements IDetLineaArrendadaDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetLineaArrendadaDaoImpl.class);

    @Override
    public void saveDetLineaArrendada(List<String> listaDatos, BigDecimal idReporte) {
        String squery = "INSERT INTO REP_DET_LINEA_ARRENDADOR "
                + "VALUES (SEQ_ID_DET_LINEAS_ACTIVAS_DET.NextVal,?,?,?,?,?,?,?,?,?,?,?)";
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
    public List<DetLineaArrendada> findAllDetLineaArrendadaByReporte(BigDecimal idReporte, int first, int pageSize) {

        String squery = "SELECT d FROM DetLineaArrendada d WHERE d.lineaArrendada.id = :reporte";

        TypedQuery<DetLineaArrendada> tquery = getEntityManager().createQuery(squery, DetLineaArrendada.class);
        tquery.setParameter("reporte", idReporte);

        return tquery.setFirstResult(first * pageSize).setMaxResults(pageSize).getResultList();
    }

}
