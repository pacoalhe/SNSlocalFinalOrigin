package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IDetalleLineaActivaDao;
import mx.ift.sns.modelo.lineas.DetalleLineaActiva;
import mx.ift.sns.modelo.lineas.Reporte;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa el DAO de DetalleLineaActiva.
 * @author X36155QU
 */
public class DetalleLineaActivaDaoImpl extends BaseDAO<DetalleLineaActiva> implements IDetalleLineaActivaDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetalleLineaActivaDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public DetalleLineaActiva getLastDetalleLineaActivaByPoblacion(Proveedor pst, String poblacion) {

        List<DetalleLineaActiva> lista = new ArrayList<DetalleLineaActiva>();

        StringBuilder sbQuery = new StringBuilder("SELECT d FROM DetalleLineaActiva d ");
        sbQuery.append("WHERE d.poblacion.inegi = :inegi");
        if (pst != null) {
            sbQuery.append(" AND d.lineaActiva.solicitudLineasActivas.proveedorSolicitante = :pst");
        }

        sbQuery.append(" ORDER BY d.lineaActiva.solicitudLineasActivas.fechaSolicitud DESC");
        Query query = getEntityManager().createQuery(sbQuery.toString());

        query.setParameter("inegi", poblacion);
        if (pst != null) {
            query.setParameter("pst", pst);
        }
        LOGGER.debug(query.toString());
        lista = query.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        } else {
            DetalleLineaActiva detLinAct = new DetalleLineaActiva();
            detLinAct.setTotalLineasActivas(new BigDecimal(0));
            detLinAct.setTotalLineasActivasCpp(new BigDecimal(0));
            detLinAct.setTotalLineasActivasMpp(new BigDecimal(0));
            detLinAct.setTotalLineasActivasFijas(new BigDecimal(0));
            return detLinAct;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public DetalleLineaActiva getLastDetalleLineaActivaByAbn(Proveedor proveedor, BigDecimal abn) {
        List<DetalleLineaActiva> lista = new ArrayList<DetalleLineaActiva>();

        StringBuilder sbQuery = new StringBuilder("SELECT d FROM DetalleLineaActiva d, ");
        sbQuery.append("PoblacionAbn p ");
        sbQuery.append("WHERE d.poblacion.inegi = p.inegi.inegi ");
        sbQuery.append("AND p.abn.codigoAbn = :abn ");
        if (proveedor != null) {
            sbQuery.append(" AND d.lineaActiva.solicitudLineasActivas.proveedorSolicitante = :pst");
        }

        sbQuery.append(" ORDER BY d.lineaActiva.solicitudLineasActivas.fechaSolicitud DESC");
        Query query = getEntityManager().createQuery(sbQuery.toString());

        query.setParameter("abn", abn);

        if (proveedor != null) {
            query.setParameter("pst", proveedor);
        }
        LOGGER.debug(query.toString());
        lista = query.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        } else {
            DetalleLineaActiva detLinAct = new DetalleLineaActiva();
            detLinAct.setTotalLineasActivas(new BigDecimal(0));
            detLinAct.setTotalLineasActivasCpp(new BigDecimal(0));
            detLinAct.setTotalLineasActivasMpp(new BigDecimal(0));
            detLinAct.setTotalLineasActivasFijas(new BigDecimal(0));
            return detLinAct;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public DetalleLineaActiva getTotalDetalleLineaActivaByAbn(BigDecimal abn) {

        DetalleLineaActiva detLinActTotal = new DetalleLineaActiva();

        BigDecimal totalLinAct = new BigDecimal(0);
        BigDecimal totalLinActFijo = new BigDecimal(0);
        BigDecimal totalLinActCpp = new BigDecimal(0);
        BigDecimal totalLinActMpp = new BigDecimal(0);

        List<Proveedor> listaPst = new ArrayList<Proveedor>();

        StringBuilder sbQuery = new StringBuilder(
                "SELECT DISTINCT(d.lineaActiva.solicitudLineasActivas.proveedorSolicitante)"
                        + " FROM DetalleLineaActiva d ");
        Query query = getEntityManager().createQuery(sbQuery.toString());
        listaPst = query.getResultList();
        LOGGER.debug(query.toString());
        for (Proveedor pst : listaPst) {
            DetalleLineaActiva detLinAct = new DetalleLineaActiva();
            detLinAct = getLastDetalleLineaActivaByAbn(pst, abn);
            if (detLinAct != null) {
                totalLinAct = totalLinAct.add(detLinAct.getTotalLineasActivas());
                totalLinActFijo = totalLinActFijo.add(detLinAct.getTotalLineasActivasFijas());
                totalLinActCpp = totalLinActCpp.add(detLinAct.getTotalLineasActivasCpp());
                totalLinActMpp = totalLinActMpp.add(detLinAct.getTotalLineasActivasMpp());
            }
        }
        detLinActTotal.setTotalLineasActivas(totalLinAct);
        detLinActTotal.setTotalLineasActivasCpp(totalLinActCpp);
        detLinActTotal.setTotalLineasActivasMpp(totalLinActMpp);
        detLinActTotal.setTotalLineasActivasFijas(totalLinActFijo);

        return detLinActTotal;
    }

    @Override
    public void saveDetalleLineaActiva(List<String> listaDatos, BigDecimal idReporte) {

        String squery = "INSERT INTO REP_DET_LINEA_ACTIVA "
                + "(ID_REP_DET_LINEA_ACTIVA,ID_REPOR,ID_INEGI,TOTAL_NUMEROS_ASIGNADOS,"
                + "TOTAL_LINEAS_ACTIVAS_FIJAS,TOTAL_LINEAS_ACTIVAS_CPP,"
                + "TOTAL_LINEAS_ACTIVAS_MPP,TOTAL_LINEAS_ACTIVAS,REGISTRO)"
                + "VALUES (SEQ_ID_DET_LINEAS_ACTIVAS.NextVal,?,?,?,?,?,?,?,?)";
        try {
            for (int i = 0; i < listaDatos.size(); i++) {
                String[] datos = listaDatos.get(i).split(":");
                Query query = getEntityManager().createNativeQuery(squery);
                query.setParameter(1, idReporte);
                query.setParameter(2, datos[0]);
                query.setParameter(3, datos[1]);
                query.setParameter(4, datos[2]);
                query.setParameter(5, datos[3]);
                query.setParameter(6, datos[4]);
                query.setParameter(7, datos[5]);
                query.setParameter(8, datos[6]);
                query.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    @SuppressWarnings({"unchecked"})
    @Override
    public List<String> findAllAvisoAsignacionDetalleLineaActiva(Reporte reporte) {

        List<String> resultado = new ArrayList<String>(0);

        try {
            // Si el reporte no se ha guardado no hay avisos
            if (reporte.getId() != null) {
                StringBuffer sbquery = new StringBuffer("SELECT d.REGISTRO FROM REP_DET_LINEA_ACTIVA d ");
                sbquery.append("WHERE d.ID_REPOR = ? AND d.TOTAL_NUMEROS_ASIGNADOS <> ");
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
