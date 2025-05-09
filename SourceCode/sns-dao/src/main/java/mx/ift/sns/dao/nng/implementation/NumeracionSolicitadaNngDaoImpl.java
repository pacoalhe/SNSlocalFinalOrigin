package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.INumeracionSolicitadaNngDao;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.Solicitud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa el DAO de NumeracionSolicitadaNng.
 * @author X36155QU
 */
public class NumeracionSolicitadaNngDaoImpl extends BaseDAO<NumeracionSolicitadaNng> implements
        INumeracionSolicitadaNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeracionSolicitadaNngDaoImpl.class);

    @Override
    public List<String> findAllClientesBySolicitud(Solicitud sol) {

        List<String> listaClientes = new ArrayList<String>();

        try {
            String squery = "SELECT DISTINCT(ns.cliente) FROM NumeracionSolicitadaNng ns "
                    + "WHERE  ns.solicitudAsignacion =  :solicitud";

            TypedQuery<String> tquery = getEntityManager().createQuery(squery, String.class);
            tquery.setParameter("solicitud", sol);

            listaClientes = tquery.getResultList();

        } catch (NoResultException e) {
            LOGGER.debug("No se han encrontado clientes para la solicitud {}", sol.getId().toString());
            return new ArrayList<String>();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} clientes para la solicitud {}", listaClientes.size(), sol.getId()
                    .toString());
        }
        return listaClientes;
    }

    @Override
    public boolean isNumeracionSolicitadaWithRangos(NumeracionSolicitadaNng numeracionSolicitada) {

        String squery = "SELECT COUNT(r) FROM RangoSerieNng r WHERE r.numeracionSolicitada = :numsol";
        TypedQuery<Long> query = getEntityManager().createQuery(squery, Long.class);
        query.setParameter("numsol", numeracionSolicitada);

        Long resultado = query.getSingleResult();

        return (resultado != null && resultado > 0);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Integer getTotalNumeracionesSolicitadasByClave(String clave, Proveedor proveedor) {

        BigDecimal sum = new BigDecimal(0);

        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
            Root ns = query.from(NumeracionSolicitadaNng.class);
            query.select(cb.sum(ns.get("cantidadSolicitada")));
            query.where(cb.equal(ns.get("claveServicio").get("codigo"), new BigDecimal(clave)),
                    cb.equal(ns.get("solicitudAsignacion").get("proveedorSolicitante"), proveedor));

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

}
