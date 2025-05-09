package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IPoblacionAbnDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.series.Nir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de PoblacionAbn.
 */
@Named
public class PoblacionAbnDaoImpl extends BaseDAO<PoblacionAbn> implements IPoblacionAbnDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PoblacionAbnDaoImpl.class);

    /** Inserts en Batch Writting. */
    private static final int BATCH_SIZE = 50;

    /**
     * Recupera la ruta de un campo específico de PoblacionAbn. Al usar criterias, no se puede acceder directamente al
     * atributo de un objeto compuesto (objeto.id.sna x ej). Para ello es necesario encadenar llamadas a cada atributo
     * usando un get que nos devuelva el Path correspondiente.
     * @param pFiled Atributo que se quiere obtener de un objeto JPA.
     * @param pRoot Contenedor
     * @return Path del atributo dentro de la clase JPA.
     */
    private Path<Object> getPobAbnPath(String pFiled, Root<PoblacionAbn> pRoot) {
        // Cada campo de cada objeto viene separado por el "."
        String[] fields = pFiled.split("\\.");
        Path<Object> path = pRoot.get(fields[0]);

        // Recuperamos el path anidando las llamadas.
        for (int i = 1; i < fields.length; i++) {
            path = path.get(fields[i]);
        }

        return path;
    }

    @Override
    public void updatePoblacionesAbn(BigDecimal pCodAbn, List<PoblacionAbn> pPoblacionesAbn) {

        // Eliminamos las poblaciones ABN anteriores. Delete Masivo.
        String strDelete = "DELETE FROM PoblacionAbn p WHERE p.abn.codigoAbn = :codAbn";
        Query queryDelete = getEntityManager().createQuery(strDelete);
        queryDelete.setParameter("codAbn", pCodAbn);
        int deletedRows = queryDelete.executeUpdate();

        // Creamos las nuevas relaciones de población - abn. Usamos Batch Writting para optimizar.
        int counter = 0;
        for (PoblacionAbn poblacionAbn : pPoblacionesAbn) {
            getEntityManager().persist(poblacionAbn);
            if (counter % BATCH_SIZE == 0) {
                // Flush a batch of inserts and release memory.
                getEntityManager().flush();
                getEntityManager().clear();
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Eliminadas {} relaciones de población con el ABN {}", deletedRows, pCodAbn);
            LOGGER.debug("Agregadas {} relaciones de población con el ABN {}", pPoblacionesAbn.size(), pCodAbn);
        }
    }

    @Override
    public List<Poblacion> findAllPoblacionesByAbn(Abn pAbn) {
        return this.findAllPoblacionesByAbn(pAbn, true);
    }

    @Override
    public List<Municipio> findAllMunicipiosByAbn(Abn pAbn) {
        return this.findAllMunicipiosByAbn(pAbn, true);
    }

    @Override
    public List<Poblacion> findAllPoblacionesByAbn(Abn pAbn, boolean pUseCache) {
        String query = "SELECT p.inegi FROM PoblacionAbn p where p.abn.codigoAbn = :codigoAbn";
        TypedQuery<Poblacion> tQuery = getEntityManager().createQuery(query, Poblacion.class);
        tQuery.setParameter("codigoAbn", pAbn.getCodigoAbn());

        // Cacheamos el resultado de la query para optimización de tiempos.
        tQuery.setHint("eclipselink.query-results-cache", String.valueOf(pUseCache));

        List<Poblacion> poblacionesAbn = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} poblaciones para el ABN: {}",
                    poblacionesAbn.size(), pAbn.getCodigoAbn());
        }

        return poblacionesAbn;
    }

    @Override
    public List<Municipio> findAllMunicipiosByAbn(Abn pAbn, boolean pUseCache) {
        String query = "SELECT DISTINCT(p.inegi.municipio) FROM PoblacionAbn p where p.abn.codigoAbn = :codigoAbn";
        TypedQuery<Municipio> tQuery = getEntityManager().createQuery(query, Municipio.class);
        tQuery.setParameter("codigoAbn", pAbn.getCodigoAbn());

        // Cacheamos el resultado de la query para optimización de tiempos.
        tQuery.setHint("eclipselink.query-results-cache", String.valueOf(pUseCache));

        List<Municipio> municipiosAbn = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} municipios para el ABN: {}",
                    municipiosAbn.size(), pAbn.getCodigoAbn());
        }

        return municipiosAbn;
    }

    @Override
    public List<PoblacionAbn> findAllPoblacionesAbn(FiltroBusquedaABNs pFiltros) {
        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PoblacionAbn> cQuery = cBuilder.createQuery(PoblacionAbn.class);
        Root<PoblacionAbn> pobAbn = cQuery.from(PoblacionAbn.class);

        // WHERE
        Predicate wherePredicate = cBuilder.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = cBuilder.equal(this.getPobAbnPath(filtro.getCampo(), pobAbn), filtro.getValor());
            wherePredicate = cBuilder.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate).distinct(true);
        cQuery.select(pobAbn);
        cQuery.orderBy(cBuilder.asc(pobAbn.get("inegi").get("inegi")));

        TypedQuery<PoblacionAbn> tQuery = getEntityManager().createQuery(cQuery);

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<PoblacionAbn> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sbTraza = new StringBuffer();
            sbTraza.append("PoblacionesAbn encontrados: ").append(result.size());
            sbTraza.append(", paginación: ").append(pFiltros.isUsarPaginacion());
            if (pFiltros.isUsarPaginacion()) {
                sbTraza.append(" (Página: ").append(pFiltros.getNumeroPagina());
                sbTraza.append(", MaxResult: ").append(pFiltros.getResultadosPagina()).append(")");
            }
            LOGGER.debug(sbTraza.toString());
            // LOGGER.debug(tQuery.toString());
        }

        return result;
    }

    @Override
    public int findAllPoblacionesAbnCount(FiltroBusquedaABNs pFiltros) {
        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        // SELECT a FROM Abn
        CriteriaBuilder cBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cBuilder.createQuery(Long.class);
        Root<PoblacionAbn> pobAbn = cQuery.from(PoblacionAbn.class);

        // WHERE
        Predicate wherePredicate = cBuilder.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = cBuilder.equal(this.getPobAbnPath(filtro.getCampo(), pobAbn), filtro.getValor());
            wherePredicate = cBuilder.and(wherePredicate, pred);
        }
        cQuery.select(cBuilder.countDistinct(pobAbn));
        cQuery.where(wherePredicate);

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery);
        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("PoblacionesAbn contadas: " + rowCount);
        }

        return rowCount;
    }

    @Override
    public List<Municipio> findAllMunicipiosByNir(Nir nir, boolean pUseCache) {
        String query = "SELECT DISTINCT(p.inegi.municipio) "
                + "FROM PoblacionAbn p, Nir n "
                + "WHERE p.abn.codigoAbn = n.abn.codigoAbn"
                + " AND n.codigo = :codigoNir";

        TypedQuery<Municipio> tQuery = getEntityManager().createQuery(query, Municipio.class);
        tQuery.setParameter("codigoNir", nir.getCodigo());

        // Cacheamos el resultado de la query para optimización de tiempos.
        tQuery.setHint("eclipselink.query-results-cache", String.valueOf(pUseCache));

        List<Municipio> municipiosNir = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} municipios para el nir: {}",
                    municipiosNir.size(), nir.getCodigo());
        }

        return municipiosNir;
    }

    @Override
    public int findAllMunicipiosByAbnAndEstadoCount(FiltroBusquedaABNs pFiltros) {
        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT COUNT(COUNT(p.inegi.municipio)) FROM PoblacionAbn p ");
        sbQuery.append("WHERE p.abn.codigoAbn = :codigoAbn ");
        sbQuery.append("AND p.inegi.municipio.estado = :estado ");
        sbQuery.append("GROUP BY p.inegi.municipio");

        TypedQuery<Long> tQuery = getEntityManager().createQuery(sbQuery.toString(), Long.class);

        for (FiltroBusqueda filtr : filtros) {
            if (filtr.getCampo().equals("abn.codigoAbn")) {
                tQuery.setParameter("codigoAbn", filtr.getValor());
            } else if (filtr.getCampo().equals("inegi.municipio.estado")) {
                tQuery.setParameter("estado", filtr.getValor());
            }
        }

        int rowCount = tQuery.getSingleResult().intValue();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Municipios contados: {}", rowCount);
        }

        return rowCount;
    }

    @Override
    public List<Municipio> findAllMunicipiosByAbnAndEstado(FiltroBusquedaABNs pFiltros) {
        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT DISTINCT(p.inegi.municipio) FROM PoblacionAbn p ");
        sbQuery.append("WHERE p.abn.codigoAbn = :codigoAbn ");
        sbQuery.append("AND p.inegi.municipio.estado = :estado");

        TypedQuery<Municipio> tQuery = getEntityManager().createQuery(sbQuery.toString(), Municipio.class);

        for (FiltroBusqueda filtr : filtros) {
            if (filtr.getCampo().equals("abn.codigoAbn")) {
                tQuery.setParameter("codigoAbn", filtr.getValor());
            } else if (filtr.getCampo().equals("inegi.municipio.estado")) {
                tQuery.setParameter("estado", filtr.getValor());
            }
        }

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<Municipio> municipiosAbn = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} municipios", municipiosAbn.size());
        }

        return municipiosAbn;
    }

    @Override
    public List<PoblacionAbn> findAllPoblacionAbnByAbn(Abn pAbn, boolean pUseCache) {
        String query = "SELECT p FROM PoblacionAbn p where p.abn.codigoAbn = :codigoAbn";
        TypedQuery<PoblacionAbn> tQuery = getEntityManager().createQuery(query, PoblacionAbn.class);
        tQuery.setParameter("codigoAbn", pAbn.getCodigoAbn());

        // Cacheamos el resultado de la query para optimización de tiempos.
        tQuery.setHint("eclipselink.query-results-cache", String.valueOf(pUseCache));

        List<PoblacionAbn> poblacionesAbn = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} poblaciones para el ABN: {}",
                    poblacionesAbn.size(), pAbn.getCodigoAbn());
        }

        return poblacionesAbn;
    }
}
