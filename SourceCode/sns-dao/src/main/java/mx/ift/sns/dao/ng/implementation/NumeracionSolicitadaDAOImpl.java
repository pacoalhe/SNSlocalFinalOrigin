package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.INumeracionSolicitadaDAO;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Numeración Solicitada.
 */
@Named
public class NumeracionSolicitadaDAOImpl extends BaseDAO<NumeracionSolicitada> implements INumeracionSolicitadaDAO {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeracionSolicitadaDAOImpl.class);

    @Override
    public List<NumeracionSolicitada> getNumSolicitada(BigDecimal codSol) {

        String strQuery = "SELECT n FROM NumeracionSolicitada n where n.solicitudAsignacion.id = :codSol";

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + strQuery);
        }

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("codSol", codSol);

        @SuppressWarnings("unchecked")
        List<NumeracionSolicitada> lista = query.getResultList();

        return lista;
    }

    @Override
    public BigDecimal getTotalNumSolicitadasByPoblacion(String tipoRed, String tipoModalidad, String poblacion,
            Proveedor proveedor) {

        BigDecimal sum = new BigDecimal(0);

        String strQuery = "SELECT SUM(n.cantSolicitada) FROM NumeracionSolicitada n "
                + "WHERE  n.poblacion.inegi = :poblacion"
                + " AND n.solicitudAsignacion.proveedorSolicitante = :proveedor";

        if (!tipoRed.equals("")) {
            strQuery = strQuery + " AND n.tipoRed.cdg = :tipoRed";
        }

        if (!tipoModalidad.equals("")) {
            strQuery = strQuery + " AND n.tipoModalidad.cdg = :tipoModalidad";
        }

        try {
            Query query = getEntityManager().createQuery(strQuery);

            query.setParameter("poblacion", poblacion);
            query.setParameter("proveedor", proveedor);

            if (!tipoRed.equals("")) {
                query.setParameter("tipoRed", tipoRed);
            }

            if (!tipoModalidad.equals("")) {
                query.setParameter("tipoModalidad", tipoModalidad);
            }

            sum = (BigDecimal) query.getSingleResult();
        } catch (NoResultException e) {

            return new BigDecimal(0);
        }

        if (sum == null) {
            return new BigDecimal(0);
        } else {
            return sum;
        }
    }

    @Override
    public BigDecimal getTotalNumSolicitadasByAbn(String tipoRed, String tipoModalidad, BigDecimal abn,
            Proveedor proveedor) {

        BigDecimal sum = new BigDecimal(0);

        String strQuery = "SELECT SUM(n.cantSolicitada) FROM NumeracionSolicitada n, PoblacionAbn pabn WHERE "
                + "n.poblacion.inegi = pabn.inegi.inegi AND pabn.abn.codigoAbn = :abn"
                + " AND n.solicitudAsignacion.proveedorSolicitante = :proveedor";

        if (!tipoRed.equals("")) {
            strQuery = strQuery + " AND n.tipoRed.cdg = :tipoRed";
        }

        if (!tipoModalidad.equals("")) {
            strQuery = strQuery + " AND n.tipoModalidad.cdg = :tipoModalidad";
        }

        try {
            Query query = getEntityManager().createQuery(strQuery);

            query.setParameter("abn", abn);
            query.setParameter("proveedor", proveedor);

            if (!tipoRed.equals("")) {
                query.setParameter("tipoRed", tipoRed);
            }

            if (!tipoModalidad.equals("")) {
                query.setParameter("tipoModalidad", tipoModalidad);
            }

            sum = (BigDecimal) query.getSingleResult();
        } catch (NoResultException e) {

            return new BigDecimal(0);
        }

        if (sum == null) {
            return new BigDecimal(0);
        } else {
            return sum;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAllNumeracionAsignadaByPoblacionGroupByAnio(String poblacion, Proveedor proveedor) {

        List<Object[]> lista = new ArrayList<Object[]>();

        StringBuffer sbquery = new StringBuffer(
                "SELECT EXTRACT(YEAR from s.FECHA_ASIGNACION) , SUM(ns.CANT_ASIGNADA), ");
        sbquery.append("sum(case ns.id_tipo_red when 'F' then ns.CANT_ASIGNADA else 0 end), ");
        sbquery.append("sum(case ns.ID_TIPO_MODALIDAD when 'CPP' then ns.CANT_ASIGNADA else 0 end), ");
        sbquery.append("sum(case ns.ID_TIPO_MODALIDAD when 'MPP' then ns.CANT_ASIGNADA else 0 end) ");
        sbquery.append("FROM NG_NUM_SOLICITADA ns, SOL_SOLICITUD s ");
        sbquery.append("WHERE ns.ID_POBLACION  = ? AND ns.ID_SOL = s.ID_SOL_SOLICITUD ");
        sbquery.append("AND s.ID_PST_SOLICITANTE = ? AND s.FECHA_ASIGNACION IS NOT NULL ");
        sbquery.append("AND ns.CANT_ASIGNADA > 0 ");
        sbquery.append("GROUP BY EXTRACT(YEAR from s.FECHA_ASIGNACION) ");
        sbquery.append("ORDER BY EXTRACT(YEAR from s.FECHA_ASIGNACION)");

        Query tquery = getEntityManager().createNativeQuery(sbquery.toString());
        tquery.setParameter(2, proveedor.getId());
        tquery.setParameter(1, poblacion);

        lista = tquery.getResultList();

        return lista;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAllNumeracionAsignadaByAbnGroupByAnio(BigDecimal abn, Proveedor proveedor) {

        List<Object[]> lista = new ArrayList<Object[]>();

        StringBuffer sbquery = new StringBuffer(
                "SELECT EXTRACT(YEAR from s.FECHA_ASIGNACION) , SUM(ns.CANT_ASIGNADA), ");
        sbquery.append("sum(case ns.id_tipo_red when 'F' then ns.CANT_ASIGNADA else 0 end), ");
        sbquery.append("sum(case ns.ID_TIPO_MODALIDAD when 'CPP' then ns.CANT_ASIGNADA else 0 end), ");
        sbquery.append("sum(case ns.ID_TIPO_MODALIDAD when 'MPP' then ns.CANT_ASIGNADA else 0 end) ");
        sbquery.append("FROM NG_NUM_SOLICITADA ns, SOL_SOLICITUD s, POBLACION_ABN p ");
        sbquery.append("WHERE p.ID_ABN = ? AND ns.ID_SOL = s.ID_SOL_SOLICITUD ");
        sbquery.append("AND s.ID_PST_SOLICITANTE = ? AND s.FECHA_ASIGNACION IS NOT NULL ");
        sbquery.append("AND ns.CANT_ASIGNADA > 0 AND p.ID_INEGI = ns.ID_POBLACION ");
        sbquery.append("GROUP BY EXTRACT(YEAR from s.FECHA_ASIGNACION) ");
        sbquery.append("ORDER BY EXTRACT(YEAR from s.FECHA_ASIGNACION)");

        Query tquery = getEntityManager().createNativeQuery(sbquery.toString());
        tquery.setParameter(2, proveedor.getId());
        tquery.setParameter(1, abn);

        lista = tquery.getResultList();

        return lista;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAllNumSolicitadasBySolicitudGroupByPoblacion(SolicitudAsignacion solicitud) {

        List<Object[]> lista = new ArrayList<Object[]>();

        try {

            StringBuffer sbquery = new StringBuffer(
                    "SELECT ns.ID_POBLACION, s.FECHA_ASIGNACION, p.NOMBRE, SUM(ns.CANT_ASIGNADA), ");
            sbquery.append("sum(case ns.id_tipo_red when 'F' then ns.CANT_ASIGNADA else 0 end), ");
            sbquery.append("sum(case ns.ID_TIPO_MODALIDAD when 'CPP' then ns.CANT_ASIGNADA else 0 end),");
            sbquery.append("sum(case ns.ID_TIPO_MODALIDAD when 'MPP' then ns.CANT_ASIGNADA else 0 end) ");
            sbquery.append("FROM NG_NUM_SOLICITADA ns ");
            sbquery.append("inner join SOL_SOLICITUD s on ns.ID_SOL = s.ID_SOL_SOLICITUD ");
            sbquery.append("inner join CAT_POBLACION p on p.ID_INEGI = ns.ID_POBLACION ");
            sbquery.append("where s.ID_PST_SOLICITANTE = ? AND s.FECHA_ASIGNACION IS NOT NULL ");
            sbquery.append("AND ns.ID_POBLACION in (select distinct(ns1.ID_POBLACION) ");
            sbquery.append("from NG_NUM_SOLICITADA ns1 where ns1.ID_SOL = ?) ");
            sbquery.append("AND ns.CANT_ASIGNADA > 0 ");
            sbquery.append("GROUP BY ns.ID_POBLACION, s.FECHA_ASIGNACION, p.NOMBRE ");
            sbquery.append("ORDER BY ns.ID_POBLACION, s.FECHA_ASIGNACION");

            Query tquery = getEntityManager().createNativeQuery(sbquery.toString());
            tquery.setParameter(1, solicitud.getProveedorSolicitante().getId());
            tquery.setParameter(2, solicitud.getId());

            lista = tquery.getResultList();

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return lista;

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAllNumSolicitadasBySolicitudGroupByAbn(SolicitudAsignacion solicitud) {

        List<Object[]> lista = new ArrayList<Object[]>();

        try {

            StringBuffer sbquery = new StringBuffer(
                    "SELECT p.ID_ABN, s.FECHA_ASIGNACION, SUM(ns.CANT_ASIGNADA), ");
            sbquery.append("sum(case ns.id_tipo_red when 'F' then ns.CANT_ASIGNADA else 0 end), ");
            sbquery.append("sum(case ns.ID_TIPO_MODALIDAD when 'CPP' then ns.CANT_ASIGNADA else 0 end),");
            sbquery.append("sum(case ns.ID_TIPO_MODALIDAD when 'MPP' then ns.CANT_ASIGNADA else 0 end) ");
            sbquery.append("FROM NG_NUM_SOLICITADA ns ");
            sbquery.append("inner join SOL_SOLICITUD s on ns.ID_SOL = s.ID_SOL_SOLICITUD ");
            sbquery.append("inner join POBLACION_ABN p on p.ID_INEGI = ns.ID_POBLACION ");
            sbquery.append("where s.ID_PST_SOLICITANTE = ? AND s.FECHA_ASIGNACION IS NOT NULL ");
            sbquery.append("AND p.ID_ABN in (select distinct(p1.ID_ABN) from NG_NUM_SOLICITADA ns1 ");
            sbquery.append("inner join  POBLACION_ABN p1 on p1.ID_INEGI = ns1.ID_POBLACION ");
            sbquery.append("where ns1.ID_SOL = ?) ");
            sbquery.append("AND ns.CANT_ASIGNADA > 0 ");
            sbquery.append("GROUP BY p.ID_ABN, s.FECHA_ASIGNACION ");
            sbquery.append("ORDER BY p.ID_ABN, s.FECHA_ASIGNACION");

            Query tquery = getEntityManager().createNativeQuery(sbquery.toString());
            tquery.setParameter(1, solicitud.getProveedorSolicitante().getId());
            tquery.setParameter(2, solicitud.getId());

            lista = tquery.getResultList();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return lista;
    }
}
