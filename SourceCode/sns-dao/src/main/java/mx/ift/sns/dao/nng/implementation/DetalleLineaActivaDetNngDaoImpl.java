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
import mx.ift.sns.dao.nng.IDetalleLineaActivaDetNngDao;
import mx.ift.sns.modelo.lineas.DetalleLineaActivaDetNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.EstadoRango;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clas que implementa el DAO de DetLineaActivaDet.
 * @author X36155QU
 */
public class DetalleLineaActivaDetNngDaoImpl extends BaseDAO<DetalleLineaActivaDetNng> implements
        IDetalleLineaActivaDetNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetalleLineaActivaDetNngDaoImpl.class);

    @Override
    public void saveDetLineaActivaDet(List<String> listaDatos, BigDecimal idReporte) {
        String squery = "INSERT INTO NNG_REP_DET_LIN_ACT_DET VALUES (SEQ_ID_NNG_RDET_LACT_DET.NextVal,?,?,?,?,?,?,?,?)";
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Integer getNumeracionActivaDetSerie(String claveServicio, Proveedor proveedor, BigDecimal idReporte) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            Root detalle = query.from(DetalleLineaActivaDetNng.class);
            query.select(
                    cb.sum(cb.sum(detalle.get("numServicio"),
                            detalle.get("numCuarentena")), detalle.get("numPortada")));
            query.where(cb.equal(detalle.get("claveServicio").get("codigo"), new BigDecimal(claveServicio)),
                    cb.equal(detalle.get("reporteLineasActivasDet").get("id"), idReporte),
                    cb.equal(
                            cb.sum(cb.diff(detalle.<Number> get("numFinal"), detalle.<Number> get("numInicial")), 1),
                            10000));

            sum = getEntityManager().createQuery(query).getSingleResult();

            LOGGER.debug("Total numeración activa por serie para la clave de servicio {} para {}: {}", claveServicio,
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
    public Integer getNumeracionActivaDetRango(String claveServicio, Proveedor proveedor, BigDecimal idReporte) {

        BigDecimal sum = new BigDecimal(0);

        try {
            StringBuffer sbquery = new StringBuffer("select sum(d.numServicio + d.numCuarentena + d.numPortada) ");
            sbquery.append("from DetalleLineaActivaDetNng d ");
            sbquery.append("where d.reporteLineasActivasDet.id = :idReporte and d.claveServicio.codigo = :clave ");
            sbquery.append("and FUNCTION('TO_NUMBER',d.numFinal)-FUNCTION('TO_NUMBER',d.numInicial) +1 < 10000 ");
            sbquery.append("and not exists (select r from RangoSerieNng r ");
            sbquery.append("where r.asignatario = :pst and r.claveServicio = d.claveServicio ");
            sbquery.append("and r.id.sna = d.sna and r.numInicio = d.numInicial and r.numFinal = d.numFinal ");
            sbquery.append("and r.cliente is not null and r.estatus.codigo <> :pendiente)");

            TypedQuery<BigDecimal> tquery = getEntityManager().createQuery(sbquery.toString(), BigDecimal.class);
            tquery.setParameter("idReporte", idReporte);
            tquery.setParameter("clave", new BigDecimal(claveServicio));
            tquery.setParameter("pst", proveedor);
            tquery.setParameter("pendiente", EstadoRango.PENDIENTE);

            sum = tquery.getSingleResult();

        } catch (NoResultException e) {

            return 0;
        }

        return (sum != null) ? sum.intValue() : 0;
    }

    @Override
    public Integer getNumeracionActivaDetEspecifica(String claveServicio, Proveedor proveedor,
            BigDecimal idReporte) {

        BigDecimal sum = new BigDecimal(0);

        try {
            StringBuffer sbquery = new StringBuffer("select sum(d.numServicio + d.numCuarentena + d.numPortada) ");
            sbquery.append("from DetalleLineaActivaDetNng d ");
            sbquery.append("where d.reporteLineasActivasDet.id = :idReporte and d.claveServicio.codigo = :clave ");
            sbquery.append("and FUNCTION('TO_NUMBER',d.numFinal)-FUNCTION('TO_NUMBER',d.numInicial)+1 < 10000 ");
            sbquery.append("and exists (select r from RangoSerieNng r ");
            sbquery.append("where r.asignatario = :pst and r.claveServicio = d.claveServicio ");
            sbquery.append("and r.id.sna = d.sna and r.numInicio = d.numInicial and r.numFinal = d.numFinal ");
            sbquery.append("and r.cliente is not null and r.estatus.codigo <> :pendiente)");

            TypedQuery<BigDecimal> tquery = getEntityManager().createQuery(sbquery.toString(), BigDecimal.class);
            tquery.setParameter("idReporte", idReporte);
            tquery.setParameter("clave", new BigDecimal(claveServicio));
            tquery.setParameter("pst", proveedor);
            tquery.setParameter("pendiente", EstadoRango.PENDIENTE);

            sum = tquery.getSingleResult();

        } catch (NoResultException e) {

            return 0;
        }

        return (sum != null) ? sum.intValue() : 0;
    }

}
