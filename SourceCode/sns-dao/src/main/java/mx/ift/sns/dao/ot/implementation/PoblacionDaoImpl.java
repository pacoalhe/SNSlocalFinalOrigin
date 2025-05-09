package mx.ift.sns.dao.ot.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ot.IPoblacionDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPoblaciones;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.VCatalogoPoblacion;
import mx.ift.sns.modelo.series.EstadoRango;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Población.
 */
@Named
public class PoblacionDaoImpl extends BaseDAO<Poblacion> implements IPoblacionDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PoblacionDaoImpl.class);

    @Override
    public Poblacion getPoblacionByInegi(String inegi) {
        String query = "SELECT p FROM Poblacion p WHERE p.inegi =:inegi";
        TypedQuery<Poblacion> tQuery = getEntityManager().createQuery(query, Poblacion.class);
        tQuery.setParameter("inegi", inegi);
        Poblacion poblacion = null;
        try {
            poblacion = tQuery.getSingleResult();
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado población para INEGI: " + inegi);
            }
            return null;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Población encontrada para INEGI " + inegi + ": " + poblacion.toString());
        }
        return poblacion;
    }

    @Override
    public List<Poblacion> findAllPoblaciones(String estado, String municipio, boolean pUseCache) {
        String query = "SELECT p FROM Poblacion p where p.municipio.id.codEstado =:estado "
                + "AND p.municipio.id.codMunicipio =:municipio ORDER BY p.nombre";
        TypedQuery<Poblacion> tQuery = getEntityManager().createQuery(query, Poblacion.class);
        tQuery.setParameter("estado", estado);
        tQuery.setParameter("municipio", municipio);

        // Cacheamos el resultado de la query para optimización de tiempos.
        tQuery.setHint("eclipselink.query-results-cache", String.valueOf(pUseCache));

        List<Poblacion> list = tQuery.getResultList();
        if (list.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sbTrazas = new StringBuffer();
                sbTrazas.append("No se han encontrado poblaciones para ");
                sbTrazas.append("Estado: ").append(estado);
                sbTrazas.append(", Municipio: ").append(municipio);
                LOGGER.debug(sbTrazas.toString());
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sbTrazas = new StringBuffer();
                sbTrazas.append("Se han encontrado ").append(list.size()).append(" poblaciones para ");
                sbTrazas.append("Estado: ").append(estado);
                sbTrazas.append(", Municipio: ").append(municipio);
                LOGGER.debug(sbTrazas.toString());
            }
        }
        return list;
    }

    @Override
    public List<Poblacion> findAllPoblaciones(String estado, String municipio) {
        // Resultados cacheados por defecto.
        return findAllPoblaciones(estado, municipio, true);
    }

    @Override
    public List<Poblacion> findAllPoblacionByAbnUbicacion(String estado, String municipio, BigDecimal abn) {
        String query = "SELECT pa.inegi FROM PoblacionAbn pa where pa.inegi.municipio.id.codEstado = :estado "
                + "AND pa.inegi.municipio.id.codMunicipio = :municipio AND pa.abn.codigoAbn = :abn";
        TypedQuery<Poblacion> nativeQuery = getEntityManager().createQuery(query, Poblacion.class);
        nativeQuery.setParameter("estado", estado);
        nativeQuery.setParameter("municipio", municipio);
        nativeQuery.setParameter("abn", abn);
        List<Poblacion> list = nativeQuery.getResultList();
        if (list.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sbTrazas = new StringBuffer();
                sbTrazas.append("No se han encontrado poblaciones para ");
                sbTrazas.append("Estado: ").append(estado);
                sbTrazas.append(", Municipio: ").append(municipio);
                sbTrazas.append(", Abn: ").append(abn);
                LOGGER.debug(sbTrazas.toString());
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sbTrazas = new StringBuffer();
                sbTrazas.append("Se han encontrado ").append(list.size()).append(" poblaciones para ");
                sbTrazas.append("Estado: ").append(estado);
                sbTrazas.append(", Municipio: ").append(municipio);
                sbTrazas.append(", Abn: ").append(abn);
                LOGGER.debug(sbTrazas.toString());
            }
        }
        return list;
    }

    @Override
    public Poblacion getPoblacionByName(String name) {
        String query = "SELECT p FROM Poblacion p WHERE p.nombre =:name";
        TypedQuery<Poblacion> nativeQuery = getEntityManager().createQuery(query, Poblacion.class);
        nativeQuery.setParameter("name", name);
        Poblacion poblacion = null;
        try {
            poblacion = nativeQuery.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Población encontrada con nombre " + name + ": " + poblacion.toString());
            }
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado población con nombre : " + name);
            }
        }

        return poblacion;
    }

    @Override
    public Poblacion getPoblacionByAbnInegi(BigDecimal codigoAbn, String inegi) {
        String query = "SELECT pa.inegi FROM PoblacionAbn pa where pa.abn.codigoAbn=:codigoAbn "
                + "AND pa.inegi.inegi=:inegi";
        TypedQuery<Poblacion> nativeQuery = getEntityManager().createQuery(query, Poblacion.class);
        nativeQuery.setParameter("codigoAbn", codigoAbn);
        nativeQuery.setParameter("inegi", inegi);
        Poblacion poblacion = null;
        try {
            poblacion = nativeQuery.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sbTrazas = new StringBuffer();
                sbTrazas.append("Se ha encontrado la población: ");
                sbTrazas.append(poblacion.getNombre()).append(" para ");
                sbTrazas.append("Código ABN: ").append(codigoAbn);
                sbTrazas.append(", INEGI: ").append(inegi);
                LOGGER.debug(sbTrazas.toString());
            }
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sbTrazas = new StringBuffer();
                sbTrazas.append("No se han encontrado población para ");
                sbTrazas.append("Código ABN: ").append(codigoAbn);
                sbTrazas.append(", INEGI: ").append(inegi);
                LOGGER.debug(sbTrazas.toString());
            }
        }

        return poblacion;
    }

    @Override
    public PoblacionAbn getPoblacionAbnByAbnInegi(BigDecimal codigoAbn, String inegi) {
        String query = "SELECT pa FROM PoblacionAbn pa where pa.abn.codigoAbn=:codigoAbn AND pa.inegi.inegi=:inegi";
        TypedQuery<PoblacionAbn> nativeQuery = getEntityManager().createQuery(query, PoblacionAbn.class);
        nativeQuery.setParameter("codigoAbn", codigoAbn);
        nativeQuery.setParameter("inegi", inegi);
        PoblacionAbn poblacionAbn = null;
        try {
            poblacionAbn = nativeQuery.getSingleResult();
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                StringBuffer sbTrazas = new StringBuffer();
                sbTrazas.append("No se han encontrado poblaciónAbn para ");
                sbTrazas.append("Código ABN: ").append(codigoAbn);
                sbTrazas.append(", INEGI: ").append(inegi);
                LOGGER.debug(sbTrazas.toString());
            }
        }
        return poblacionAbn;
    }

    @Override
    public int findAllPoblacionesCount(BigDecimal codigo) {
        String query = "SELECT COUNT(pa.inegi) FROM PoblacionAbn pa WHERE pa.abn.codigoAbn=:codigo";
        TypedQuery<Long> nativeQuery = getEntityManager().createQuery(query, Long.class);
        nativeQuery.setParameter("codigo", codigo);
        int rowCount = (nativeQuery.getSingleResult()).intValue();
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sbTrazas = new StringBuffer();
            sbTrazas.append("Se han contado ").append(rowCount).append(" poblaciones ");
            sbTrazas.append("con código ABN: ").append(codigo);
            LOGGER.debug(sbTrazas.toString());
        }
        return rowCount;
    }

    @Override
    public int findAllMunicipiosCount(BigDecimal codigo) {
        String query = "SELECT COUNT(DISTINCT pa.inegi.municipio.id.codMunicipio) "
                + "FROM PoblacionAbn pa WHERE pa.abn.codigoAbn=:codigo";
        TypedQuery<Long> nativeQuery = getEntityManager().createQuery(query, Long.class);
        nativeQuery.setParameter("codigo", codigo);
        int rowCount = nativeQuery.getSingleResult().intValue();
        if (LOGGER.isDebugEnabled()) {
            StringBuffer sbTrazas = new StringBuffer();
            sbTrazas.append("Se han contado ").append(rowCount).append(" municipios ");
            sbTrazas.append("con código ABN: ").append(codigo);
            LOGGER.debug(sbTrazas.toString());
        }
        return rowCount;
    }

    @Override
    public boolean existPoblacion(String nombre) {

        String query = "SELECT p FROM Poblacion p WHERE p.nombre =:name";
        TypedQuery<Poblacion> nativeQuery = getEntityManager().createQuery(query, Poblacion.class);
        nativeQuery.setParameter("name", nombre);

        try {
            nativeQuery.getResultList();
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado población con nombre : " + nombre);
            }
            return false;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Población encontrada con nombre " + nombre);
        }
        return true;
    }

    @Override
    public List<Poblacion> findAllPoblacionesLikeNombre(String cadena) {

        String query = "SELECT p.inegi FROM PoblacionAbn p JOIN Abn a ON p.abn.codigoAbn = a.codigoAbn "
                + "JOIN FETCH a.nirs WHERE p.inegi.nombre like :name ";
        TypedQuery<Poblacion> nativeQuery = getEntityManager().createQuery(query, Poblacion.class);
        nativeQuery.setParameter("name", cadena.toUpperCase() + "%");

        List<Poblacion> poblaciones = nativeQuery.getResultList();

        // Es necesario hacer una llamada explicita a la lista indirecta para que se creen las instancias en la
        // sesion de jsf
        final int size = poblaciones.size();

        if (poblaciones != null) {
            for (int i = 0; i < size; i++) {
                poblaciones.get(i).getAbn().getNirs().size();
            }
        }

        return poblaciones;
    }

    @Override
    public List<String> findAllPoblacionesNameLikeNombre(String cadena) {

        //String query = "SELECT p.nombre FROM Poblacion p  WHERE p.nombre like :name ORDER BY p.nombre DESC";
        String query = "SELECT CONCAT(CONCAT(p.nombre,'; ',p.municipio.nombre),'; ',p.municipio.estado.nombre) FROM Poblacion p WHERE p.nombre like :name ORDER BY p.nombre DESC";

        TypedQuery<String> nativeQuery = getEntityManager().createQuery(query, String.class);

        nativeQuery.setParameter("name", "%" + cadena.toUpperCase() + "%");

        List<String> nombrePoblaciones = nativeQuery.getResultList();

        return nombrePoblaciones;
    }

    /**
     * Comprueba si el abn está asociado a la poblacion.
     * @param poblacion Poblacion
     * @param abn Abn
     * @return boolean
     */
    @Override
    public boolean existePoblacionEnAbn(Poblacion poblacion, Abn abn) {
        String query = "SELECT count(p) FROM PoblacionAbn p WHERE p.inegi.inegi = :inegi AND p.abn.codigoAbn = :abn";
        TypedQuery<Long> nativeQuery = getEntityManager().createQuery(query, Long.class);
        nativeQuery.setParameter("inegi", poblacion.getInegi());
        nativeQuery.setParameter("abn", abn.getCodigoAbn());

        Long num = nativeQuery.getSingleResult();

        return num > 0;
    }

    @Override
    public List<Poblacion> findAllPoblaciones(FiltroBusquedaPoblaciones pFiltros) {

        StringBuffer sbQuery = new StringBuffer();
        String sQuery = "";
        ArrayList<FiltroBusqueda> listaFiltros = pFiltros.getFiltrosPoblacion();
        sbQuery.append("SELECT p FROM Poblacion p, PoblacionAbn pabn ");
        sbQuery.append("WHERE p.inegi = pabn.inegi.inegi ");
        for (FiltroBusqueda filtro : listaFiltros) {
            sbQuery.append(" AND ");
            sbQuery.append(filtro.getPrefijo()).append(".");
            sbQuery.append(filtro.getCampo()).append(" = :").append(filtro.getCampo().replace(".", ""));
        }
        sQuery = sbQuery.toString();
        sQuery = sQuery + " Order by p.nombre";

        TypedQuery<Poblacion> query = getEntityManager().createQuery(sQuery, Poblacion.class);

        // parametros
        for (FiltroBusqueda filtro : listaFiltros) {
            query.setParameter(filtro.getCampo().replace(".", ""), filtro.getValor());
        }

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            query.setFirstResult(pFiltros.getNumeroPagina());
            query.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<Poblacion> listPoblaciones = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} poblaciones", listPoblaciones.size());
        }

        return listPoblaciones;
    }

    @Override
    public int findAllPoblacionesCount(FiltroBusquedaPoblaciones pFiltros) {

        StringBuffer sbQuery = new StringBuffer();
        String sQuery = "";
        ArrayList<FiltroBusqueda> listaFiltros = pFiltros.getFiltrosPoblacion();
        sbQuery.append("SELECT COUNT(p) FROM Poblacion p, PoblacionAbn pabn ");
        sbQuery.append("WHERE p.inegi = pabn.inegi.inegi ");

        for (FiltroBusqueda filtro : listaFiltros) {
            sbQuery.append(" AND ");
            sbQuery.append(filtro.getPrefijo()).append(".");
            sbQuery.append(filtro.getCampo()).append(" = :").append(filtro.getCampo().replace(".", ""));
        }
        sQuery = sbQuery.toString();
        sQuery = sQuery + " Order by p.nombre";
        Query query = getEntityManager().createQuery(sQuery);
        // parametros
        for (FiltroBusqueda filtro : listaFiltros) {
            query.setParameter(filtro.getCampo().replace(".", ""), filtro.getValor());
        }

        int rowCount = ((Long) query.getSingleResult()).intValue();

        return rowCount;
    }

    @Override
    public List<Poblacion> findAllPoblacionesByEstadoAbn(String estado, BigDecimal codAbn) {
        String query = "SELECT p.inegi FROM PoblacionAbn p where p.inegi.municipio.id.codEstado =:estado "
                + "AND p.abn.codigoAbn = :codAbn ORDER BY p.inegi.nombre";

        TypedQuery<Poblacion> nativeQuery = getEntityManager().createQuery(query, Poblacion.class);
        nativeQuery.setParameter("estado", estado);
        nativeQuery.setParameter("codAbn", codAbn);

        List<Poblacion> list = nativeQuery.getResultList();
        if (list.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado poblaciones para el estado {} y abn {}", estado, codAbn);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Se han encontrado {} poblaciones para el estado {} y abn {}",
                        list.size(), estado, codAbn);
            }
        }
        return list;
    }

    @Override
    public String countAllPoblacionesByEstado(String estado) {
        String sQuery = "SELECT COUNT(p) FROM Poblacion p where p.municipio.estado.codEstado = :estado";

        Query query = getEntityManager().createQuery(sQuery);
        query.setParameter("estado", estado);

        String numeroPob = query.getSingleResult().toString();
        if (numeroPob.compareTo("0") == 0) {
            numeroPob = "No existe poblaciones que presten servicio de telefonía local en este estado";
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado poblaciones para el estado {}", estado);
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Se han encontrado {} poblaciones para el estado {}",
                        numeroPob, estado);
            }
        }
        return numeroPob;
    }

    @Override
    public List<Poblacion> findAllPoblacionesFilterMunicipio(FiltroBusquedaMunicipios filtro) {

        StringBuffer sbQuery = new StringBuffer();

        sbQuery.append("SELECT p FROM Poblacion p");

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
            sbQuery.append("p.municipio.").append(filter.getCampo()).append(" = :")
                    .append(filter.getCampo());
        }

        sbQuery.append(" ORDER BY p.inegi");
        Query query = getEntityManager().createQuery(sbQuery.toString());

        // parametros
        for (FiltroBusqueda filter : listaFiltros) {
            query.setParameter(filter.getCampo(), filter.getValor());
        }

        @SuppressWarnings("unchecked")
        List<Poblacion> listPoblaciones = query.getResultList();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontradas {} Poblaciones", listPoblaciones.size());
        }

        return listPoblaciones;

    }

    @Override
    public boolean existePoblacion(String inegi) {

        String query = "SELECT count(1) FROM Poblacion p WHERE p.inegi = :inegi";

        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
        tQuery.setParameter("inegi", inegi);

        Long resultado = tQuery.getSingleResult();

        return (resultado != null && resultado > 0);

    }

    @Override
    public boolean existPoblacionWithAbn(String inegi, String abn) {
        String squery = "SELECT COUNT(p) FROM Poblacion p, PoblacionAbn a WHERE a.abn.codigoAbn = :abn"
                + " and p.inegi = :inegi and p.inegi = a.inegi.inegi";
        TypedQuery<Long> query = getEntityManager().createQuery(squery, Long.class);
        query.setParameter("abn", new BigDecimal(abn));
        query.setParameter("inegi", inegi);
        Long resultado = query.getSingleResult();

        return (resultado != null && resultado > 0);
    }

    @Override
    public List<VCatalogoPoblacion> findAllCatalogoPoblacion(FiltroBusquedaPoblaciones pFiltros) {
        String query = "SELECT vcp FROM VCatalogoPoblacion vcp ";
        String where = "";

        if (pFiltros.getEstado() != null) {
            where += "WHERE vcp.idEstado = :estado ";
        }

        if (pFiltros.getMunicipio() != null) {
            where += (where.isEmpty()) ? "WHERE vcp.idMunicipio = :municipio " : " AND vcp.idMunicipio = :municipio ";
        }

        if (pFiltros.getPoblacion() != null) {
            where += (where.isEmpty()) ? "WHERE vcp.id = :poblacion " : " AND vcp.id = :poblacion ";
        }

        if (pFiltros.getIdAbn() != null) {
            where += (where.isEmpty()) ? "WHERE vcp.idAbn = :abn " : " AND vcp.idAbn = :abn ";
        }

        query += where + "ORDER BY vcp.nombreEstado, vcp.nombreMunicipio, vcp.nombrePoblacion";

        TypedQuery<VCatalogoPoblacion> tQuery = getEntityManager().createQuery(query, VCatalogoPoblacion.class);

        if (pFiltros.getEstado() != null) {
            tQuery.setParameter("estado", pFiltros.getEstado().getCodEstado());
        }

        if (pFiltros.getMunicipio() != null) {
            tQuery.setParameter("municipio", pFiltros.getMunicipio().getId().getCodMunicipio());
        }

        if (pFiltros.getPoblacion() != null) {
            tQuery.setParameter("poblacion", pFiltros.getPoblacion().getInegi());
        }

        if (pFiltros.getIdAbn() != null) {
            tQuery.setParameter("abn", pFiltros.getIdAbn().toString());
        }

        List<VCatalogoPoblacion> list = tQuery.getResultList();

        return list;
    }

    @Override
    public List<Poblacion> findAllPoblacionesEstadoNumeracion(Estado estado) {

        // Select p from POBLACION
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Poblacion> cQuery = criteriaBuilder.createQuery(Poblacion.class);
        Root<RangoSerie> rango = cQuery.from(RangoSerie.class);

        // Joins
        // Join<Poblacion, PoblacionAbn> joinPobAbn = poblacion.join("poblacionAbn", JoinType.INNER);
        // Join<PoblacionAbn, Abn> joinAbn = joinPobAbn.join("abn", JoinType.INNER);
        // Join<Abn, Nir> joinir = joinAbn.join("nirs", JoinType.INNER);
        //
        // Join<Poblacion, RangoSerie> joinRango = poblacion.join("rangosSeries", JoinType.INNER);

        // Where
        ParameterExpression<Estado> estadoOt = criteriaBuilder.parameter(Estado.class);
        ParameterExpression<String> cdgEstatusRango = criteriaBuilder.parameter(String.class);

        // cQuery.where(criteriaBuilder.equal(poblacion.get("municipio").get("estado"), estadoOt));

        cQuery.where(
                criteriaBuilder.equal(rango.get("poblacion").get("municipio").get("estado"), estadoOt),
                criteriaBuilder.notEqual(rango.get("estadoRango").get("codigo"), cdgEstatusRango));

        cQuery.select(rango.<Poblacion> get("poblacion"));
        cQuery.orderBy(criteriaBuilder.asc(rango.get("poblacion").get("nombre")));
        cQuery.distinct(true);
        TypedQuery<Poblacion> typedQuery = getEntityManager().createQuery(cQuery);
        typedQuery.setParameter(estadoOt, estado);
        typedQuery.setParameter(cdgEstatusRango, EstadoRango.PENDIENTE);

        // typedQuery.setHint("eclipselink.join-fetch", "joinAbn.nirs");
        // typedQuery.setHint("eclipselink.join-fetch", "poblacion.rangosSeries");

        List<Poblacion> resultado = typedQuery.getResultList();
        LOGGER.debug(typedQuery.toString());
        return resultado;
    }

    @Override
    public VCatalogoPoblacion findPoblacion(String inegi) {
        String query = "SELECT vcp FROM VCatalogoPoblacion vcp WHERE vcp.id =:inegi";
        TypedQuery<VCatalogoPoblacion> tQuery = getEntityManager().createQuery(query, VCatalogoPoblacion.class);
        tQuery.setParameter("inegi", inegi);
        VCatalogoPoblacion poblacion = null;
        try {
            poblacion = tQuery.getSingleResult();
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se han encontrado población para INEGI: " + inegi);
            }
            return null;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Población encontrada para INEGI " + inegi + ": " + poblacion.toString());
        }
        return poblacion;
    }

    public List<Poblacion> findPoblacionByNombreAndMunicipioAndEstado(String nombrePob, String codMun) {

        String query = "SELECT p.inegi FROM PoblacionAbn p JOIN Abn a ON p.abn.codigoAbn = a.codigoAbn "
                + "JOIN FETCH a.nirs WHERE p.inegi.nombre = :poblacion " +
                "AND p.inegi.municipio.id.codMunicipio = :municipio";
        TypedQuery<Poblacion> nativeQuery = getEntityManager().createQuery(query, Poblacion.class);
        nativeQuery.setParameter("poblacion", nombrePob.toUpperCase());
        nativeQuery.setParameter("municipio", codMun.toUpperCase());

        List<Poblacion> poblaciones = nativeQuery.getResultList();

        // Es necesario hacer una llamada explicita a la lista indirecta para que se creen las instancias en la
        // sesion de jsf
        final int size = poblaciones.size();

        if (poblaciones != null) {
            for (int i = 0; i < size; i++) {
                poblaciones.get(i).getAbn().getNirs().size();
            }
        }

        return poblaciones;

    }

}
