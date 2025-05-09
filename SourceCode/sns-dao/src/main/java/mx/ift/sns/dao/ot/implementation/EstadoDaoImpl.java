package mx.ift.sns.dao.ot.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ot.IEstadoDao;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Estados (Organización Territorial).
 */
@Named
public class EstadoDaoImpl extends BaseDAO<Estado> implements IEstadoDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EstadoDaoImpl.class);

    @Override
    public Boolean existsEstadoByCodigo(String estado) {
        // Query
        String query = "SELECT e FROM Estado e WHERE e.codEstado=:cdgEstado";
        boolean existe = false;

        Estado std = null;
        try {
            TypedQuery<Estado> tQuery = getEntityManager().createQuery(query, Estado.class);
            tQuery.setParameter("cdgEstado", estado);
            std = tQuery.getSingleResult();
            if (std != null) {
                existe = true;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Estado encontrado: {}", std.getNombre());
                }
            }
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Estado {} no encontrado.", estado);
            }
            return null;
        }

        return existe;
    }

    @Override
    public Estado getEstadoByCodigo(String estado) {
        // Query
        String query = "SELECT e FROM Estado e WHERE e.codEstado=:cdgEstado";
        Estado std = null;

        try {
            TypedQuery<Estado> nativeQuery = getEntityManager().createQuery(query, Estado.class);
            nativeQuery.setParameter("cdgEstado", estado);
            std = (Estado) nativeQuery.getSingleResult();
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Estado {} no encontrado.", estado);
            }
            return null;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Estado encontrado: {}", std.getNombre());
        }
        return std;
    }

    @Override
    public List<Estado> findEstados() {
        String query = "SELECT e FROM Estado e ORDER BY e.nombre";
        TypedQuery<Estado> tQuery = getEntityManager().createQuery(query, Estado.class);

        // Cacheamos el resultado de la query para optimización de tiempos.
        tQuery.setHint("eclipselink.query-results-cache", "true");

        List<Estado> list = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Estados econtrados: {}", list.size());
        }

        return list;
    }

    @Override
    public Estado getEstadoByName(String name) {
        // Query
        String query = "SELECT e FROM Estado e WHERE e.nombre= :name";
        Estado std = null;

        try {
            TypedQuery<Estado> tQuery = getEntityManager().createQuery(query, Estado.class);
            tQuery.setParameter("name", name);
            std = tQuery.getSingleResult();
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Estado {} no encontrado.", name);
            }
            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Estado {} encontrado. Código: ", std.getCodEstado());
        }
        return std;
    }

    @Override
    public List<Estado> findAllEstados(int first, int pagesize) throws Exception {
        // Consulta
        String query = ("SELECT e FROM Estado e ORDER BY e.nombre ");
        TypedQuery<Estado> tQuery = getEntityManager().createQuery(query, Estado.class);
        tQuery.setFirstResult(first);
        tQuery.setMaxResults(pagesize);

        List<Estado> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Estados econtrados: {}", result.size());
        }

        return result;
    }

    @Override
    public int findAllEstadosCount() {
        String query = "SELECT COUNT(e.codEstado) FROM Estado e";
        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);

        int rowCount = tQuery.getSingleResult().intValue();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Estados contados: {}", rowCount);
        }
        return rowCount;
    }

    @Override
    public List<EstadoArea> findAllAreaEstadoByEstado(Estado estado) {
        String query = "SELECT a FROM EstadoArea a WHERE a.id.idEstado = :idEstado";
        TypedQuery<EstadoArea> tQuery = getEntityManager().createQuery(query, EstadoArea.class);
        tQuery.setParameter("idEstado", estado.getCodEstado());

        List<EstadoArea> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Áreas encontradas: {}", result.size());
        }
        return result;
    }
}
