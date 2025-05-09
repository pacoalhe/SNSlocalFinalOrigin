package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IDetLineaArrendatarioDao;
import mx.ift.sns.modelo.lineas.DetLineaArrendatario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementa el DAO de DetLineaArrendatario.
 * @author X36155QU
 */
public class DetLineaArrendatarioDaoImpl extends BaseDAO<DetLineaArrendatario> implements IDetLineaArrendatarioDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetLineaArrendatarioDaoImpl.class);

    @Override
    public void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal idReporte) {

        String squery = "INSERT INTO REP_DET_LINEA_ARRENDATARIO"
                + " VALUES (SEQ_ID_DET_REP_LIN_ARREND.NextVal,?,?,?,?,?,?,?,?,?,?,?,?)";
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
    public List<DetLineaArrendatario> findAllDetLineaArrendatarioByReporte(BigDecimal idReporte,
            int first, int pageSize) {

        String squery = "SELECT d FROM DetLineaArrendatario d WHERE d.lineaArrendatario.id = :reporte";

        TypedQuery<DetLineaArrendatario> tquery = getEntityManager().createQuery(squery, DetLineaArrendatario.class);
        tquery.setParameter("reporte", idReporte);

        return tquery.setFirstResult(first * pageSize).setMaxResults(pageSize).getResultList();
    }

}
