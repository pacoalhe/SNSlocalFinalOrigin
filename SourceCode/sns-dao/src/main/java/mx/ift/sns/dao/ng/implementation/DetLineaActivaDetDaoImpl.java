package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IDetLineaActivaDetDao;
import mx.ift.sns.modelo.lineas.DetLineaActivaDet;
import mx.ift.sns.modelo.lineas.Reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clas que implementa el DAO de DetLineaActivaDet.
 * @author X36155QU
 */
public class DetLineaActivaDetDaoImpl extends BaseDAO<DetLineaActivaDet> implements IDetLineaActivaDetDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetLineaActivaDetDaoImpl.class);

    @Override
    public List<DetLineaActivaDet> findAllDetLineaActivaDetByReporte(BigDecimal idReporte, int first, int pageSize) {

        String squery = "SELECT d FROM DetLineaActivaDet d WHERE d.lineasActivasDet.id = :reporte";

        TypedQuery<DetLineaActivaDet> tquery = getEntityManager().createQuery(squery, DetLineaActivaDet.class);
        tquery.setParameter("reporte", idReporte);

        return tquery.setFirstResult(first * pageSize).setMaxResults(pageSize).getResultList();
    }

    @Override
    public void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal idReporte) {
        String squery = "INSERT INTO REP_DET_LINEA_ACTIVA_DET "
                + "VALUES (SEQ_ID_DET_LINEAS_ACTIVAS_DET.NextVal,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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

    @SuppressWarnings("unchecked")
    @Override
    public List<String> findAllAvisoAsignacionDetalleLineaActivaDet(Reporte reporte) {

        List<String> resultado = new ArrayList<String>(0);

        try {
            // Si el reporte no se ha guardado no hay avisos
            if (reporte.getId() != null) {
                StringBuffer sbquery = new StringBuffer("SELECT d.REGISTRO FROM REP_DET_LINEA_ACTIVA_DET d ");
                sbquery.append("WHERE d.ID_REPOR = ? AND d.TOTAL_NUM_ASIGANDOS <> ");
                sbquery.append("(SELECT COALESCE (SUM(TO_NUMBER(r.N_FINAL) - TO_NUMBER(r.N_INICIO) + 1), 0) ");
                sbquery.append("FROM  RANGO_SERIE r WHERE r.ID_PST_ASIGNATARIO = ? AND r.ID_POBLACION = d.ID_INEGI)");
                Query tquery = getEntityManager().createNativeQuery(sbquery.toString());
                tquery.setParameter(1, reporte.getId());
                tquery.setParameter(2, reporte.getSolicitudLineasActivas().getProveedorSolicitante().getId());
                resultado = tquery.getResultList();
            }
        } catch (NoResultException e) {
            LOGGER.debug("No se han encontrado avisos");
        }
        LOGGER.debug("Se han encontrado {} avisos", resultado.size());
        return resultado;
    }
}
