package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IDetalleLineaArrendatarioNngDao;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendatarioNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.EstadoRango;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementa el DAO de DetLineaArrendatario.
 * @author X36155QU
 */
public class DetalleLineaArrendatarioNngDaoImpl extends BaseDAO<DetalleLineaArrendatarioNng> implements
        IDetalleLineaArrendatarioNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetalleLineaArrendatarioNngDaoImpl.class);

    @Override
    public void saveDetLineaArrendatario(List<String> listaDatos, BigDecimal idReporte) {

        String squery =
                "INSERT INTO NNG_REP_DET_LIN_ARRTARIO VALUES (SEQ_ID_NNG_RDLIN_ARRTARIO.NextVal,?,?,?,?,?,?,?,?,?)";
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
    public List<DetalleLineaArrendatarioNng> findAllDetLineaArrendatarioByReporte(BigDecimal idReporte, int first,
            int pageSize) {

        String squery = "SELECT d FROM DetalleLineaArrendatarioNng d WHERE d.reporteLineaArrendatario.id = :reporte";

        TypedQuery<DetalleLineaArrendatarioNng> tquery = getEntityManager().createQuery(squery,
                DetalleLineaArrendatarioNng.class);
        tquery.setParameter("reporte", idReporte);

        return tquery.setFirstResult(first * pageSize).setMaxResults(pageSize).getResultList();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Integer getNumeracionActivaArrendatarioSerie(String clave, Proveedor proveedor, BigDecimal idReporte) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            Root detalle = query.from(DetalleLineaArrendatarioNng.class);
            query.select(cb.sum(detalle.get("numActiva")));
            query.where(cb.equal(detalle.get("claveServicio").get("codigo"), new BigDecimal(clave)),
                    cb.equal(detalle.get("reporteLineaActiva").get("id"), idReporte),
                    cb.equal(
                            cb.sum(cb.diff(detalle.<Number> get("numFinal"), detalle.<Number> get("numInicio")), 1),
                            10000));

            sum = getEntityManager().createQuery(query).getSingleResult();

            LOGGER.debug("Total numeración asignada por serie para la clave de servicio {} para {}: {}", clave,
                    proveedor.getNombre(), sum);
        } catch (NoResultException e) {

            return 0;
        }

        if (sum == null) {
            return 0;
        } else {
            return sum.intValue();
        }

    }

    @Override
    public Integer getNumeracionActivaArrendatarioRango(String clave, Proveedor proveedor, BigDecimal idReporte) {

        BigDecimal sum = new BigDecimal(0);

        try {
            StringBuffer sbquery = new StringBuffer("select sum(d.numActiva) from DetalleLineaArrendatarioNng d ");
            sbquery.append("where d.reporteLineaActiva.id = :idReporte and d.claveServicio.codigo = :clave ");
            sbquery.append("and FUNCTION('TO_NUMBER',d.numFinal)-FUNCTION('TO_NUMBER',d.numInicio)+1)<10000 ");
            sbquery.append("and not exists (select r from RangoSerieNng r ");
            sbquery.append("where r.asignatario = :pst and r.claveServicio = d.claveServicio ");
            sbquery.append("and r.id.sna = d.sna and r.numInicio = d.numInicio and r.numFinal = d.numFinal ");
            sbquery.append("and r.cliente is not null and r.estatus.codigo <> :pendiente)");

            TypedQuery<BigDecimal> tquery = getEntityManager().createQuery(sbquery.toString(), BigDecimal.class);
            tquery.setParameter("idReporte", idReporte);
            tquery.setParameter("clave", new BigDecimal(clave));
            tquery.setParameter("pst", proveedor);
            tquery.setParameter("pendiente", EstadoRango.PENDIENTE);

            sum = tquery.getSingleResult();

        } catch (NoResultException e) {

            return 0;
        }

        return (sum != null) ? sum.intValue() : 0;
    }

    @Override
    public Integer getNumeracionActivaArrendatarioEspecifica(String clave, Proveedor proveedor, BigDecimal idReporte) {

        BigDecimal sum = new BigDecimal(0);

        try {
            StringBuffer sbquery = new StringBuffer("select sum(d.numActiva) from DetalleLineaArrendatarioNng d ");
            sbquery.append("where d.reporteLineaActiva.id = :idReporte and d.claveServicio.codigo = :clave ");
            sbquery.append("and FUNCTION('TO_NUMBER',d.numFinal)-FUNCTION('TO_NUMBER',d.numInicio)+1 < 10000 ");
            sbquery.append("and exists (select r from RangoSerieNng r ");
            sbquery.append("where r.asignatario = :pst and r.claveServicio = d.claveServicio ");
            sbquery.append("and r.id.sna = d.sna and r.numInicio = d.numInicio and r.numFinal = d.numFinal ");
            sbquery.append("and r.cliente is not null and r.estatus.codigo <> :pendiente)");

            TypedQuery<BigDecimal> tquery = getEntityManager().createQuery(sbquery.toString(), BigDecimal.class);
            tquery.setParameter("idReporte", idReporte);
            tquery.setParameter("clave", new BigDecimal(clave));
            tquery.setParameter("pst", proveedor);
            tquery.setParameter("pendiente", EstadoRango.PENDIENTE);

            sum = tquery.getSingleResult();

        } catch (NoResultException e) {

            return 0;
        }

        return (sum != null) ? sum.intValue() : 0;
    }
}
