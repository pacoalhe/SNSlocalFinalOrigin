package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IClaveServicioDao;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaClaveServicio;
import mx.ift.sns.modelo.nng.ClaveServicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de claves de servicio.
 */
@Named
public class ClaveServicioDaoImpl extends BaseDAO<ClaveServicio> implements IClaveServicioDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaveServicioDaoImpl.class);
    /** Constante que representa el punto. */
    private static final String PUNTO = ".";
    /** Constante que representa el igual. */
    private static final String IGUAL = " = ";
    /** Constante que representa los dos puntos. */
    private static final String DOS_PUNTOS = " :";
    /** Constante para el Where de las consultas. */
    private static final String WHERE = " WHERE ";
    /** Constante para el And de las consultas. */
    private static final String AND = " AND ";

    @Override
    public List<ClaveServicio> findAllClaveServicio() {
        String query = "SELECT c FROM ClaveServicio c ORDER BY c.codigo ASC";
        TypedQuery<ClaveServicio> tQuery = getEntityManager().createQuery(query, ClaveServicio.class);

        List<ClaveServicio> lista = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} claves de servicio", lista.size());
        }

        return lista;
    }

    @Override
    public ClaveServicio getClaveServicioByCodigo(BigDecimal pCodigo) {
        try {
            ClaveServicio cs = getEntityManager().find(ClaveServicio.class, pCodigo);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Clave de Servicio encontrada con código {}", pCodigo);
            }
            return cs;
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se ha encontrado Clave de Servicio con código {}", pCodigo);
            }
            return null;
        }
    }

    @Override
    public List<ClaveServicio> findAllClaveServicio(FiltroBusquedaClaveServicio pFiltros) throws Exception {
        ArrayList<FiltroBusqueda> filtros = pFiltros.getFiltrosClaveServicio();

        String query = "SELECT cs FROM ClaveServicio cs ";
        query += generarPredicado(filtros);

        TypedQuery<ClaveServicio> tQuery = getEntityManager().createQuery(query, ClaveServicio.class);
        if (!filtros.isEmpty()) {
            for (FiltroBusqueda filtro : filtros) {
                tQuery.setParameter(filtro.getCampo(), filtro.getValor());
            }
        }

        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<ClaveServicio> result = tQuery.getResultList();
        return result;
    }

    @Override
    public int findAllClaveServicioCount(FiltroBusquedaClaveServicio pFiltros) throws Exception {
        ArrayList<FiltroBusqueda> filtros = pFiltros.getFiltrosClaveServicio();

        String query = "SELECT count(1) FROM ClaveServicio cs ";
        query += generarPredicado(filtros);

        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
        if (!filtros.isEmpty()) {
            for (FiltroBusqueda filtro : filtros) {
                tQuery.setParameter(filtro.getCampo(), filtro.getValor());
            }
        }

        int rowCount = tQuery.getSingleResult().intValue();
        return rowCount;
    }

    /**
     * Método que general el string asociado al Where de la consulta de claves de servicio.
     * @param filtros ArrayList<FiltroBusqueda>
     * @return String
     */
    private String generarPredicado(ArrayList<FiltroBusqueda> filtros) {
        String parametros = "";

        if (!filtros.isEmpty()) {
            for (FiltroBusqueda filtro : filtros) {
                if (parametros.isEmpty()) {
                    parametros += filtro.getPrefijo() + PUNTO + filtro.getCampo() + IGUAL + DOS_PUNTOS
                            + filtro.getCampo();
                } else {
                    parametros += AND + filtro.getPrefijo() + PUNTO + filtro.getCampo() + IGUAL + DOS_PUNTOS
                            + filtro.getCampo();
                }

            }

            return WHERE + parametros;
        }

        return "";
    }

    @Override
    public List<ClaveServicio> findAllClaveServicioActivas() {
        String query = "SELECT c FROM ClaveServicio c WHERE c.estatus.cdg = :estatus ORDER BY c.codigo";

        TypedQuery<ClaveServicio> tQuery = getEntityManager().createQuery(query, ClaveServicio.class);
        tQuery.setParameter("estatus", Estatus.ACTIVO);

        List<ClaveServicio> lista = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} claves de servicio activas", lista.size());
        }

        return lista;
    }

    @Override
    public boolean exists(String clave) {
        String squery = "SELECT COUNT(n) FROM ClaveServicio n WHERE n.codigo = :clave";
        TypedQuery<Long> query = getEntityManager().createQuery(squery, Long.class);
        query.setParameter("clave", Integer.parseInt(clave));
        Long resultado = query.getSingleResult();

        return (resultado != null && resultado > 0);
    }

    @Override
    public List<ClaveServicio> findAllClaveServicioActivasWeb() {
        String query = "SELECT c FROM ClaveServicio c WHERE c.visibleWeb = :visible ORDER BY c.codigo";

        TypedQuery<ClaveServicio> tQuery = getEntityManager().createQuery(query, ClaveServicio.class);
        tQuery.setParameter("visible", true);

        List<ClaveServicio> lista = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} claves de servicio activas en la web", lista.size());
        }

        return lista;
    }
}
