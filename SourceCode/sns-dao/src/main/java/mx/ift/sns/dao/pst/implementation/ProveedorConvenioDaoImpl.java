package mx.ift.sns.dao.pst.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pst.IProveedorConvenioDao;
import mx.ift.sns.modelo.pst.EstadoConvenio;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoRed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de convenios de proveedor.
 */
@Named
public class ProveedorConvenioDaoImpl extends BaseDAO<ProveedorConvenio> implements IProveedorConvenioDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProveedorConvenioDaoImpl.class);

    @Override
    public ProveedorConvenio getConvenioById(BigDecimal pId) {
        return getEntityManager().find(ProveedorConvenio.class, pId);
    }

    @Override
    public List<ProveedorConvenio> findAllConveniosByProveedor(BigDecimal pCodPstComercializador) {

        String query = "SELECT p FROM ProveedorConvenio p WHERE p.proveedorConvenio.id =:codPst";

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        Query consulta = getEntityManager().createQuery(query);
        consulta.setParameter("codPst", pCodPstComercializador);

        @SuppressWarnings("unchecked")
        List<ProveedorConvenio> resultado = consulta.getResultList();
        return resultado;
    }

    @Override
    public List<ProveedorConvenio> findAllConveniosByProveedor(Proveedor pComercializador, TipoRed pTipoRedConvenio) {

        // CriteriaQuery
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProveedorConvenio> cQuery = cb.createQuery(ProveedorConvenio.class);
        Root<ProveedorConvenio> pc = cQuery.from(ProveedorConvenio.class);

        // Parámetros comúnes
        ParameterExpression<Proveedor> pstComer = cb.parameter(Proveedor.class);
        ParameterExpression<EstadoConvenio> statusActivo = cb.parameter(EstadoConvenio.class);

        // Estatus Convenio Activo
        EstadoConvenio ecActivo = new EstadoConvenio();
        ecActivo.setCdg(EstadoConvenio.VIGENTE);

        TypedQuery<ProveedorConvenio> tQuery = null;
        if (pTipoRedConvenio.getCdg().equals(TipoRed.AMBAS)) {
            cQuery.where(
                    cb.equal(pc.get("proveedorConvenio"), pstComer),
                    cb.and(cb.equal(pc.get("estatus"), statusActivo)));

            tQuery = getEntityManager().createQuery(cQuery.select(pc));
            tQuery.setParameter(pstComer, pComercializador);
            tQuery.setParameter(statusActivo, ecActivo);

        } else {
            // Parámetros
            ParameterExpression<TipoRed> tipoRedPst = cb.parameter(TipoRed.class);
            ParameterExpression<TipoRed> tipoRedAmbas = cb.parameter(TipoRed.class);

            cQuery.where(
                    cb.equal(pc.get("proveedorConvenio"), pstComer),
                    cb.and(cb.equal(pc.get("estatus"), statusActivo)),
                    cb.and(cb.or(
                            cb.equal(pc.get("tipoRed"), tipoRedPst),
                            cb.equal(pc.get("tipoRed"), tipoRedAmbas))));

            // Tipo de Red Ambas
            TipoRed trAmbas = new TipoRed();
            trAmbas.setCdg(TipoRed.AMBAS);

            tQuery = getEntityManager().createQuery(cQuery.select(pc));
            tQuery.setParameter(tipoRedPst, pTipoRedConvenio);
            tQuery.setParameter(tipoRedAmbas, trAmbas);
            tQuery.setParameter(pstComer, pComercializador);
            tQuery.setParameter(statusActivo, ecActivo);
        }

        List<ProveedorConvenio> list = tQuery.getResultList();

        // if (LOGGER.isDebugEnabled()) {
        // // Específico para EclipseLink. Hay que habilitar la dependencia con la libería en el POM
        // String sqlString = tQuery.unwrap(org.eclipse.persistence.jpa.JpaQuery.class).getDatabaseQuery()
        // .getSQLString();
        // LOGGER.debug("Criteria SqlString: " + sqlString);
        // }

        return list;
    }

    @Override
    public List<Proveedor> findAllConcesionariosByComercializador(TipoRed tipoRed, Proveedor comercializador) {

        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT DISTINCT(p.proveedorConcesionario) FROM ProveedorConvenio p ");
        sbQuery.append("WHERE p.proveedorConvenio = :comercializador AND p.proveedorConcesionario.ido IS NOT NULL ");
        sbQuery.append("AND p.proveedorConcesionario <> p.proveedorConvenio ");

        // Control TipoRed 'Ambas'
        if (!tipoRed.getCdg().equals(TipoRed.AMBAS)) {
            sbQuery.append("AND (p.tipoRed = :tipoRed OR p.tipoRed.cdg = :ambas) ");
        } else {
            sbQuery.append("AND p.tipoRed = :tipoRed");
        }

        sbQuery.append("ORDER BY p.proveedorConcesionario.nombre");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando Concesionarios para el Comercializador {} con para tipo de red {}",
                    comercializador.getNombreCorto(), tipoRed.getDescripcion());
        }

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(sbQuery.toString(), Proveedor.class);
        tQuery.setParameter("comercializador", comercializador);
        tQuery.setParameter("tipoRed", tipoRed);
        if (!tipoRed.getCdg().equals(TipoRed.AMBAS)) {
            tQuery.setParameter("ambas", TipoRed.AMBAS);
        }

        List<Proveedor> resultado = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Concesionarios encontrados: {}", resultado.size());
        }

        return resultado;
    }

    @Override
    public boolean existConvenio(Proveedor concesionario, Proveedor comercializador) {

        String query = "SELECT p.proveedorConcesionario FROM ProveedorConvenio p "
                + "WHERE p.proveedorConvenio = :comercializador AND p.proveedorConcesionario = :concesionario";

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        Query consulta = getEntityManager().createQuery(query);
        consulta.setParameter("comercializador", comercializador);
        consulta.setParameter("concesionario", concesionario);

        @SuppressWarnings("unchecked")
        List<Proveedor> resultado = consulta.getResultList();

        if (resultado.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean existeConcesionarioConvenioConBcd(Proveedor proveedor) {

        StringBuffer sbquery = new StringBuffer("SELECT count(1) FROM ProveedorConvenio c ");
        sbquery.append("WHERE c.proveedorConcesionario.bcd IS NOT NULL AND c.proveedorConvenio = :proveedor");

        TypedQuery<Long> tQuery = getEntityManager().createQuery(sbquery.toString(), Long.class);
        tQuery.setParameter("proveedor", proveedor);

        Long resultado = tQuery.getSingleResult();

        return (resultado != null && resultado > 0);
    }

    @Override
    public List<Proveedor> findAllConcesonarioConvenioNng(Proveedor proveedor) {
        StringBuffer sbquery = new StringBuffer("SELECT DISTINCT(c.proveedorConcesionario) FROM ProveedorConvenio c ");
        sbquery.append("WHERE c.proveedorConcesionario.bcd IS NOT NULL AND c.proveedorConvenio = :proveedor ");
        sbquery.append("AND c.proveedorConcesionario <> :proveedor");

        TypedQuery<Proveedor> tQuery = getEntityManager().createQuery(sbquery.toString(), Proveedor.class);
        tQuery.setParameter("proveedor", proveedor);

        return tQuery.getResultList();

    }
}
