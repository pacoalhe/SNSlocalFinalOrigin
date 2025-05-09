package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.INumeracionAsignadaNngDao;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.NumeracionAsignadaNng;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa el DAO de numeracio asignada.
 * @author X36155QU
 */
@Named
public class NumeracionAsignadaNngDaoImpl extends BaseDAO<NumeracionAsignadaNng> implements INumeracionAsignadaNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeracionAsignadaNngDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAllNumeracionAsignadaByPstGroupByClave(Proveedor proveedor,
            List<ClaveServicio> listaClaves) {

        List<Object[]> lista = new ArrayList<Object[]>();

        try {

            StringBuffer sbquery = new StringBuffer("SELECT r.ID_CLAVE_SERVICIO, s.FECHA_ASIGNACION, ");
            sbquery.append("sum(case TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1  ");
            sbquery.append("when 10000 then TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 else 0 end), ");
            sbquery.append("sum(case when ns.CLIENTE is null ");
            sbquery.append("and TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 < 10000 ");
            sbquery.append("then TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 else 0 end), ");
            sbquery.append("sum(case when ns.CLIENTE is not null ");
            sbquery.append("and TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 < 10000 ");
            sbquery.append("then TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 else 0 end) ");
            sbquery.append("from NNG_NUM_ASIGNADA r, SOL_SOLICITUD s, NNG_NUM_SOLICITADA ns ");
            sbquery.append("WHERE s.ID_SOL_SOLICITUD = ns.ID_SOL_SOLICITUD ");
            sbquery.append("and r.ID_NNG_NUM_SOLI = ns.ID_NNG_NUM_SOLI ");
            sbquery.append("AND s.ID_PST_SOLICITANTE = ? AND s.FECHA_ASIGNACION IS NOT NULL ");
            sbquery.append("AND r.ID_CLAVE_SERVICIO IN (?");
            for (int i = 1; i < listaClaves.size(); i++) {
                sbquery.append(",?");
            }
            sbquery.append(")");
            sbquery.append("GROUP BY r.ID_CLAVE_SERVICIO, s.FECHA_ASIGNACION ");
            sbquery.append("ORDER BY r.ID_CLAVE_SERVICIO, s.FECHA_ASIGNACION");

            Query tquery = getEntityManager().createNativeQuery(sbquery.toString());
            tquery.setParameter(1, proveedor.getId());
            for (int i = 0; i < listaClaves.size(); i++) {
                tquery.setParameter(i + 2, listaClaves.get(i).getCodigo());
            }

            lista = tquery.getResultList();

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return lista;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAllNumeracionAsignadaByClaveGroupByAnio(String clave, Proveedor proveedor) {

        List<Object[]> lista = new ArrayList<Object[]>();

        try {

            StringBuffer sbquery = new StringBuffer("SELECT EXTRACT(YEAR from s.FECHA_ASIGNACION), ");
            sbquery.append("sum(case TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 ");
            sbquery.append("when 10000 then TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 else 0 end), ");
            sbquery.append("sum(case when ns.CLIENTE is null ");
            sbquery.append("and TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 < 10000 ");
            sbquery.append("then TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 else 0 end), ");
            sbquery.append("sum(case when ns.CLIENTE is not null ");
            sbquery.append("and TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 < 10000 ");
            sbquery.append("then TO_NUMBER(r.FIN_RANGO)-TO_NUMBER(r.INICIO_RANGO)+1 else 0 end) ");
            sbquery.append("from NNG_NUM_ASIGNADA r, SOL_SOLICITUD s, NNG_NUM_SOLICITADA ns ");
            sbquery.append("WHERE s.ID_SOL_SOLICITUD = ns.ID_SOL_SOLICITUD ");
            sbquery.append("and r.ID_NNG_NUM_SOLI = ns.ID_NNG_NUM_SOLI ");
            sbquery.append("AND s.ID_PST_SOLICITANTE = ? AND s.FECHA_ASIGNACION IS NOT NULL ");
            sbquery.append("AND r.ID_CLAVE_SERVICIO = ? ");
            sbquery.append("GROUP BY EXTRACT(YEAR from s.FECHA_ASIGNACION) ");
            sbquery.append("ORDER BY EXTRACT(YEAR from s.FECHA_ASIGNACION)");

            Query tquery = getEntityManager().createNativeQuery(sbquery.toString());
            tquery.setParameter(1, proveedor.getId());
            tquery.setParameter(2, new BigDecimal(clave));

            lista = tquery.getResultList();

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return lista;
    }

    @Override
    public List<NumeracionAsignadaNng> findAllNumeracionAsignadaBySolicitud(SolicitudAsignacionNng solicitud) {
        TypedQuery<NumeracionAsignadaNng> tQuery;
        try {
            StringBuffer sbQuery = new StringBuffer("SELECT na FROM NumeracionAsignadaNng na ");
            sbQuery.append("WHERE na.numeracionSolicitada.solicitudAsignacion = :solicitud");
            tQuery = getEntityManager().createQuery(sbQuery.toString(),
                    NumeracionAsignadaNng.class);
            tQuery.setParameter("solicitud", solicitud);
        } catch (NoResultException e) {
            return new ArrayList<NumeracionAsignadaNng>();
        }
        return tQuery.getResultList();
    }

    @Override
    public List<NumeracionAsignadaNng> findAllNumeracionAsignadaBySolicitada(NumeracionSolicitadaNng numSol) {
        TypedQuery<NumeracionAsignadaNng> tQuery;
        try {
            StringBuffer sbQuery = new StringBuffer("SELECT na FROM NumeracionAsignadaNng na ");
            sbQuery.append("WHERE na.numeracionSolicitada = :numSol");
            tQuery = getEntityManager().createQuery(sbQuery.toString(),
                    NumeracionAsignadaNng.class);
            tQuery.setParameter("numSol", numSol);
        } catch (NoResultException e) {
            return new ArrayList<NumeracionAsignadaNng>();
        }
        return tQuery.getResultList();
    }
}
