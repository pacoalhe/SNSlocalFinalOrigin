package mx.ift.sns.dao.ot.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ot.IMunicipioDao;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Modelos.
 */
@Named
public class MunicipioDaoImpl extends BaseDAO<Municipio> implements IMunicipioDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MunicipioDaoImpl.class);

    @Override
    public Boolean getMunicipioByEstado(String estado, String municipio) {
        String query = "SELECT m FROM Municipio m WHERE m.id.codEstado=:estado AND m.id.codMunicipio=:municipio";
        boolean existe = false;
        Municipio municip = null;
        try {
            TypedQuery<Municipio> tQuery = getEntityManager().createQuery(query, Municipio.class);
            tQuery.setParameter("estado", String.valueOf(Integer.parseInt(estado)));
            tQuery.setParameter("municipio", String.valueOf(Integer.parseInt(municipio)));
            municip = tQuery.getSingleResult();
            existe = (municip != null);
        } catch (NoResultException nre) {
            return false;
        }
        return existe;
    }

    @Override
    public Municipio getMunicipioById(MunicipioPK id) {
        String query = "SELECT m FROM Municipio m WHERE m.id.codEstado=:estado AND m.id.codMunicipio=:municipio";
        Municipio municip = null;
        try {
            Query nativeQuery = getEntityManager().createQuery(query);
            nativeQuery.setParameter("estado", id.getCodEstado());
            nativeQuery.setParameter("municipio", id.getCodMunicipio());
            municip = (Municipio) nativeQuery.getSingleResult();
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Municipio no encontrado para el id {}", id);
            }
            return null;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Municipio {} encontrado para el id {}", municip.getNombre(), id);
        }
        return municip;
    }

    @Override
    public List<Municipio> findMunicipio(String estado) {
        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT m FROM Municipio m ");
        sbQuery.append("WHERE m.id.codEstado=:estado ");
        sbQuery.append("AND m.estatus.cdg = :activo ");
        sbQuery.append("ORDER BY m.nombre");

        TypedQuery<Municipio> tQuery = getEntityManager().createQuery(sbQuery.toString(), Municipio.class);
        tQuery.setParameter("estado", estado);
        tQuery.setParameter("activo", Estatus.ACTIVO);

        // Cacheamos el resultado de la query para optimización de tiempos.
        tQuery.setHint("eclipselink.query-results-cache", "true");

        List<Municipio> list = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrados {} Municipios Activos para el Estado {}", list.size(), estado);
        }

        return list;
    }

    @Override
    public List<Municipio> findAllMunicipiosByCode(String codigo) {
        String query = "SELECT m FROM Municipio m where m.id.codMunicipio = :codigo";
        TypedQuery<Municipio> tQuery = getEntityManager().createQuery(query, Municipio.class);
        tQuery.setParameter("codigo", codigo);

        // Cacheamos el resultado de la query para optimización de tiempos.
        tQuery.setHint("eclipselink.query-results-cache", "true");

        List<Municipio> list = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrados {} Municipios con código {}", list.size(), codigo);
        }
        return list;
    }

    @Override
    public List<Municipio> findAllMunicipios(FiltroBusquedaMunicipios filtro) {

        StringBuffer sbQuery = new StringBuffer("SELECT m FROM Municipio m");

        // Query Dinámica en función de los filtros
        List<FiltroBusqueda> listaFiltros = filtro.getListaFiltros();

        if (!listaFiltros.isEmpty()) {
            sbQuery.append(" WHERE ");
        }
        for (int i = 0; i < listaFiltros.size(); i++) {
            if (i > 0) {
                sbQuery.append(" AND ");
            }

            FiltroBusqueda filter = listaFiltros.get(i);
            sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                    .append(filter.getCampo());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(sbQuery.toString());
        }

        TypedQuery<Municipio> query = getEntityManager().createQuery(sbQuery.toString(), Municipio.class);

        if (filtro.isUsarPaginacion()) {
            query.setFirstResult(filtro.getNumeroPagina()).setMaxResults(filtro.getResultadosPagina());
        }

        for (FiltroBusqueda filter : listaFiltros) {
            query.setParameter(filter.getCampo(), filter.getValor());
        }

        List<Municipio> lista = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrados {} Municipios", lista.size());
        }

        return lista;
    }

    @Override
    public Integer findAllMunicipiosCount(FiltroBusquedaMunicipios filtro) {

        StringBuffer sbQuery = new StringBuffer("SELECT COUNT(m) FROM Municipio m");

        // Query Dinámica en función de los filtros
        List<FiltroBusqueda> listaFiltros = filtro.getListaFiltros();

        if (!listaFiltros.isEmpty()) {
            sbQuery.append(" WHERE ");
        }
        for (int i = 0; i < listaFiltros.size(); i++) {
            if (i > 0) {
                sbQuery.append(" AND ");
            }

            FiltroBusqueda filter = listaFiltros.get(i);

            sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                    .append(filter.getCampo());
        }

        Query query = getEntityManager().createQuery(sbQuery.toString());

        for (FiltroBusqueda filter : listaFiltros) {
            query.setParameter(filter.getCampo(), filter.getValor());
        }

        Long total = (Long) query.getSingleResult();
        LOGGER.debug("Municipios encontrados: " + total);
        return total.intValue();
    }

    public Municipio findMunicipioByNombreAndEstado(String nombreMun, String nombreEst) throws Exception{
        String query = "SELECT m FROM Municipio m where m.nombre = :nombreMun AND m.estado.nombre = :nombreEst";
        TypedQuery<Municipio> tQuery = getEntityManager().createQuery(query, Municipio.class);
        tQuery.setParameter("nombreMun", nombreMun);
        tQuery.setParameter("nombreEst", nombreEst);

        // Cacheamos el resultado de la query para optimización de tiempos.
        tQuery.setHint("eclipselink.query-results-cache", "true");

        Municipio result = tQuery.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrados {} Municipios con nombres  {}", 1, nombreMun +" - " + nombreEst);
        }
        return result;
    }

    /**
     * FJAH 27.06.2025
     * @param idZona
     * @return
     */
    @Override
    public List<Object[]> findMunicipiosByZona(Integer idZona) {
        String sql = "SELECT cm.ID_MUNICIPIO, cm.NOMBRE, cm.ID_ESTADO, ce.NOMBRE AS ESTADO_NOMBRE " +
                "FROM CAT_MUNICIPIO cm " +
                "JOIN CAT_ESTADO ce ON cm.ID_ESTADO = ce.ID_ESTADO " +
                "WHERE cm.REGION_CELULAR = ? " +
                "ORDER BY cm.NOMBRE";

        return getEntityManager()
                .createNativeQuery(sql)
                .setParameter(1, idZona)
                .getResultList();
    }

    @Override
    public Long countMunicipiosByZona(Integer idZona) {
        String sql =
                "SELECT SUM(TOTAL_MUNICIPIOS) AS TOTAL_MUNICIPIOS_ZONA " +
                        "FROM ( " +
                        "   SELECT COUNT(DISTINCT cm.ID_MUNICIPIO) AS TOTAL_MUNICIPIOS " +
                        "   FROM RANGO_SERIE rs " +
                        "   JOIN CAT_NIR cn ON rs.ID_NIR = cn.ID_NIR " +
                        "   JOIN CAT_POBLACION cp ON rs.ID_POBLACION = cp.ID_INEGI " +
                        "   JOIN CAT_MUNICIPIO cm ON cp.ID_MUNICIPIO = cm.ID_MUNICIPIO " +
                        "   JOIN CAT_ESTADO ce ON cp.ID_ESTADO = ce.ID_ESTADO " +
                        "   WHERE cn.ZONA = ? " +
                        "   GROUP BY ce.NOMBRE " +
                        ")";

        Object result = getEntityManager()
                .createNativeQuery(sql)
                .setParameter(1, idZona)
                .getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }


}
