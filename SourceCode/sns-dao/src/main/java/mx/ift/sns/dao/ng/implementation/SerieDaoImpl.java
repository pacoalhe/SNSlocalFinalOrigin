package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.ISerieDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.ng.SeriePK;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.VCatalogoSerie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Series.
 */
@Named
public class SerieDaoImpl extends BaseDAO<Serie> implements ISerieDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SerieDaoImpl.class);

    /**
     * Constructor.
     */
    public SerieDaoImpl() {
    }

    @Override
    public List<Serie> findSeriesLibres(Abn abn) {

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT s FROM Serie s WHERE s.nir.abn = :abn ");
        sbQuery.append("AND s.rangos IS EMPTY AND s.estatus.cdg = :estado");

        TypedQuery<Serie> tQuery = getEntityManager().createQuery(sbQuery.toString(), Serie.class);
        tQuery.setParameter("abn", abn);
        tQuery.setParameter("estado", Estatus.ACTIVO);

        List<Serie> list = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} series libres activas", list.size());
        }
        return list;
    }

    @Override
    public List<Serie> findAllSeries(FiltroBusquedaSeries pFiltros) {

        boolean ordenacion = false;
        // Consulta
        ArrayList<Entry<String, Object>> listaFiltrosSerie = pFiltros.getFiltrosSerie();
        ArrayList<Entry<String, Object>> listaFiltrosRango = pFiltros.getFiltrosRango();

        StringBuffer sbquery = new StringBuffer("SELECT s FROM Serie s");
        sbquery.append(" WHERE s.estatus.cdg = :estado");

        // Query para los rangos
        StringBuffer sbqueryrangos = new StringBuffer("SELECT r.id FROM RangoSerie r ");
        sbqueryrangos.append("WHERE r.serie.id = s.id");

        if (!pFiltros.isSerieLibre() && pFiltros.isRangosDisponibles()) {
            sbquery.append(" AND 10000 > (SELECT ");
            sbquery.append("sum(1 + FUNC('TO_NUMBER',r.numFinal) - FUNC('TO_NUMBER',r.numInicio))");
            sbquery.append(" from RangoSerie r");
            sbquery.append("  where r.serie.id = s.id)");
        }

        // Query Dinámica en función de los filtros

        for (Entry<String, Object> filter : listaFiltrosSerie) {
            if (filter.getKey().equals("ocupacion")) {
                sbquery.append(" AND COALESCE((SELECT ");
                sbquery.append("sum(1 + FUNC('TO_NUMBER',r.numFinal) - FUNC('TO_NUMBER',r.numInicio))");
                sbquery.append(" from RangoSerie r");
                sbquery.append("  where r.serie.id = s.id),0)");
                sbquery.append(" = :").append(filter.getKey().replace(".", ""));
            } else if (filter.getKey().equals("disponible")) {
                sbquery.append(" AND 10000 - COALESCE((SELECT ");
                sbquery.append("sum(1 + FUNC('TO_NUMBER',r.numFinal) - FUNC('TO_NUMBER',r.numInicio))");
                sbquery.append(" from RangoSerie r");
                sbquery.append("  where r.serie.id = s.id),0)");
                sbquery.append(" = :").append(filter.getKey().replace(".", ""));
            } else {
                sbquery.append(" AND s.");
                sbquery.append(filter.getKey()).append(" = :").append(filter.getKey().replace(".", ""));
            }
        }

        for (Entry<String, Object> filter : listaFiltrosRango) {
            if (pFiltros.isOtroAsignatario() && filter.getKey().equals("asignatario")) {
                sbqueryrangos.append(" AND r.");
                sbqueryrangos.append(filter.getKey()).append(" <> :").append(filter.getKey().replace(".", ""));
            } else {
                sbqueryrangos.append(" AND r.");
                sbqueryrangos.append(filter.getKey()).append(" = :").append(filter.getKey().replace(".", ""));
            }
        }

        // Ordenamos el resultado.
        StringBuffer sborder = new StringBuffer();
        if (pFiltros.getNirOrder() != null) {

            if (!ordenacion) {
                sborder.append(" order by ");
                ordenacion = true;
            }
            sborder.append("s.nir.codigo " + pFiltros.getNirOrder());
        }

        if (pFiltros.getSnaOrder() != null) {
            if (!ordenacion) {
                sborder.append(" order by ");
                ordenacion = true;
            } else {
                sborder.append(",");
            }
            sborder.append("s.id.sna " + pFiltros.getSnaOrder());
        }

        if (pFiltros.isSerieLibre()) {
            sbquery.append(" AND NOT EXISTS (").append(sbqueryrangos).append(") ");
        } else {
            sbquery.append(" AND EXISTS (").append(sbqueryrangos).append(") ");
        }

        sbquery.append(sborder);

        // Estado activa
        TypedQuery<Serie> tQuery = getEntityManager().createQuery(sbquery.toString(), Serie.class);

        tQuery.setParameter("estado", Estatus.ACTIVO);

        // Resto de parametros
        for (Entry<String, Object> filter : listaFiltrosSerie) {
            tQuery.setParameter(filter.getKey().replace(".", ""), filter.getValue());
        }
        for (Entry<String, Object> filter : listaFiltrosRango) {
            tQuery.setParameter(filter.getKey().replace(".", ""), filter.getValue());
        }

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<Serie> list = tQuery.getResultList();

        return list;
    }

    @Override
    public int findAllSeriesCount(FiltroBusquedaSeries pFiltros) {

        // Si se busca por serie libre devuelve los resultados de la tabla de series.
        // Si se busca por serie ocupada devuelve los resultados de la tabla de rangos.

        // Consulta
        ArrayList<Entry<String, Object>> listaFiltrosSerie = pFiltros.getFiltrosSerie();
        ArrayList<Entry<String, Object>> listaFiltrosRango = pFiltros.getFiltrosRango();

        StringBuffer sbquery = new StringBuffer("SELECT COUNT(1) FROM Serie s");
        sbquery.append(" WHERE s.estatus.cdg = :estado");

        // Query para los rangos
        StringBuffer sbqueryrangos = new StringBuffer("SELECT r.id FROM RangoSerie r ");
        sbqueryrangos.append("WHERE r.serie.id = s.id");

        if (!pFiltros.isSerieLibre() && pFiltros.isRangosDisponibles()) {
            sbquery.append(" AND 10000 > (SELECT ");
            sbquery.append("sum(1 + FUNC('TO_NUMBER',r.numFinal) - FUNC('TO_NUMBER',r.numInicio))");
            sbquery.append(" from RangoSerie r");
            sbquery.append("  where r.serie.id = s.id)");
        }

        // Query Dinámica en función de los filtros

        for (Entry<String, Object> filter : listaFiltrosSerie) {
            if (filter.getKey().equals("ocupacion")) {
                sbquery.append(" AND COALESCE((SELECT ");
                sbquery.append("sum(1 + FUNC('TO_NUMBER',r.numFinal) - FUNC('TO_NUMBER',r.numInicio))");
                sbquery.append(" from RangoSerie r");
                sbquery.append("  where r.serie.id = s.id),0)");
                sbquery.append(" = :").append(filter.getKey().replace(".", ""));
            } else if (filter.getKey().equals("disponible")) {
                sbquery.append(" AND 10000 - COALESCE((SELECT ");
                sbquery.append("sum(1 + FUNC('TO_NUMBER',r.numFinal) - FUNC('TO_NUMBER',r.numInicio))");
                sbquery.append(" from RangoSerie r");
                sbquery.append("  where r.serie.id = s.id),0)");
                sbquery.append(" = :").append(filter.getKey().replace(".", ""));
            } else {
                sbquery.append(" AND s.");
                sbquery.append(filter.getKey()).append(" = :").append(filter.getKey().replace(".", ""));
            }
        }

        for (Entry<String, Object> filter : listaFiltrosRango) {
            if (pFiltros.isOtroAsignatario() && filter.getKey().equals("asignatario")) {
                sbqueryrangos.append(" AND r.");
                sbqueryrangos.append(filter.getKey()).append(" <> :").append(filter.getKey().replace(".", ""));
            } else {
                sbqueryrangos.append(" AND r.");
                sbqueryrangos.append(filter.getKey()).append(" = :").append(filter.getKey().replace(".", ""));
            }
        }

        if (pFiltros.isSerieLibre()) {
            sbquery.append(" AND NOT EXISTS (").append(sbqueryrangos).append(") ");
        } else {
            sbquery.append(" AND EXISTS (").append(sbqueryrangos).append(") ");
        }
        LOGGER.debug(sbquery.toString());
        // Estado activa
        Query query = getEntityManager().createQuery(sbquery.toString());

        query.setParameter("estado", Estatus.ACTIVO);

        // Resto de parametros
        for (Entry<String, Object> filter : listaFiltrosSerie) {
            query.setParameter(filter.getKey().replace(".", ""), filter.getValue());
        }
        for (Entry<String, Object> filter : listaFiltrosRango) {
            query.setParameter(filter.getKey().replace(".", ""), filter.getValue());
        }

        int rowCount = ((Long) query.getSingleResult()).intValue();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han contado {} series", rowCount);
        }

        return rowCount;
    }

    @Override
    public Serie getSerie(BigDecimal pIdSna, BigDecimal pIdNir) {

        String strQuery = "SELECT s FROM Serie s where s.id.sna = :sna and s.id.idNir = :nir";
        Serie serie = null;
        try {
            TypedQuery<Serie> query = getEntityManager().createQuery(strQuery, Serie.class);
            query.setParameter("sna", pIdSna);
            query.setParameter("nir", pIdNir);
            serie = query.getSingleResult();
        } catch (NoResultException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se ha encontrado la serie {} para el idNir {}", pIdSna, pIdNir);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Serie {} recuperada para el idNir {}", pIdSna, pIdNir);
        }
        return serie;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Serie> findSeriesPst(Proveedor pst, Abn abn) {

        List<Serie> list = new ArrayList<Serie>();

        String strQuery = "SELECT DISTINCT(o.serie) FROM RangoSerie o "
                + "where o.asignatario = :pst and o.serie.nir.abn = :abn "
                + "order by o.serie.nir.codigo , o.serie.id.sna";

        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("pst", pst);
        query.setParameter("abn", abn);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        list = query.getResultList();

        return list;
    }

    @Override
    public List<Serie> findSeriesOtrosPsts(Proveedor pst, Abn abn) {

        String strQuery = "SELECT DISTINCT(o.serie) FROM RangoSerie o "
                + "where o.asignatario <> :pst and o.serie.nir.abn = :abn "
                + "order by o.serie.nir.codigo, o.serie.id.sna";
        Query query = getEntityManager().createQuery(strQuery);
        query.setParameter("pst", pst);
        query.setParameter("abn", abn);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        @SuppressWarnings("unchecked")
        List<Serie> list = query.getResultList();
        return list;
    }

    @Override
    public int getMaxMinSerieFromNir(int pCdgNir, boolean pMaxValue) {

        StringBuilder sbQuery = new StringBuilder();
        if (pMaxValue) {
            sbQuery.append("SELECT MAX(s.id.sna) FROM Serie s ");
        } else {
            sbQuery.append("SELECT MIN(s.id.sna) FROM Serie s ");
        }
        sbQuery.append("WHERE s.nir.codigo = :codigoNir");

        TypedQuery<Integer> tQuery = getEntityManager().createQuery(sbQuery.toString(), Integer.class);
        tQuery.setParameter("codigoNir", pCdgNir);

        Integer value = tQuery.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            if (pMaxValue) {
                LOGGER.debug("Serie más alta para el cdgNir {}: {}", pCdgNir, value);
            } else {
                LOGGER.debug("Serie más baja para el cdgNir {}: {}", pCdgNir, value);
            }
        }

        return value.intValue();
    }

    @Override
    public int desactivarSeriesByNir(BigDecimal pIdNir) {

        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.INACTIVO);

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("UPDATE Serie s SET s.estatus = :statusInactivo ");
        sbQuery.append("WHERE s.id.idNir = :idNir");

        Query query = getEntityManager().createQuery(sbQuery.toString());
        query.setParameter("statusInactivo", estatus);
        query.setParameter("idNir", pIdNir);

        int result = query.executeUpdate();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han desactivado {} series del idNir {}", result);
        }

        return result;
    }

    @Override
    public int activarSeriesByNir(BigDecimal pIdNir, int pInicioSerie, int pFinalSerie) {

        Estatus estatus = new Estatus();
        estatus.setCdg(Estatus.ACTIVO);

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("UPDATE Serie s SET s.estatus = :statusActivo ");
        sbQuery.append("WHERE s.id.idNir = :idNir ");
        sbQuery.append("AND s.id.sna >= :inicioSerie AND s.id.sna <= :finalSerie");

        Query query = getEntityManager().createQuery(sbQuery.toString());
        query.setParameter("statusActivo", estatus);
        query.setParameter("idNir", pIdNir);
        query.setParameter("inicioSerie", pInicioSerie);
        query.setParameter("finalSerie", pFinalSerie);

        int result = query.executeUpdate();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han activado {} series del idNir {}", pIdNir);
        }

        return result;
    }

    @Override
    public boolean existSerieWithNir(String sna, String nir) {

        String squery = "SELECT COUNT(s) FROM Serie s WHERE s.id.sna = :sna AND s.nir.codigo = :nir";
        TypedQuery<Long> query = getEntityManager().createQuery(squery, Long.class);
        query.setParameter("nir", Integer.parseInt(nir));
        query.setParameter("sna", new BigDecimal(sna));
        Long resultado = query.getSingleResult();

        return (resultado != null && resultado > 0);
    }

    @Override
    public List<VCatalogoSerie> findAllCatalogoSeries(FiltroBusquedaSeries pFiltros) {

        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltrosCatalogoSerie();

        StringBuilder sbQuery = new StringBuilder(
                "SELECT vcs FROM VCatalogoSerie vcs ");
        if (!pFiltros.getListaFiltrosCatalogoSerie().isEmpty()) {
            sbQuery.append("WHERE ");

            for (int i = 0; i < filtros.size(); i++) {
                if (i > 0) {
                    sbQuery.append(" AND ");
                }

                FiltroBusqueda filter = filtros.get(i);

                sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                        .append(filter.getCampo());
            }
        }
        sbQuery.append(" ORDER BY vcs.idAbn,vcs.idSna ASC");
        LOGGER.debug(sbQuery.toString());

        TypedQuery<VCatalogoSerie> tQuery = getEntityManager().createQuery(sbQuery.toString(), VCatalogoSerie.class);

        for (FiltroBusqueda filter : filtros) {
            if (filter.getCampo().equals("idAbn") || filter.getCampo().equals("cdgNir")) {
                tQuery.setParameter(filter.getCampo(), filter.getValor().toString());
            } else {
                tQuery.setParameter(filter.getCampo(), filter.getValor());
            }
        }
        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<VCatalogoSerie> list = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} series.", list.size());
            LOGGER.debug("Query: " + tQuery.toString());
        }

        return list;

    }

    @Override
    public int findAllCatalogoSeriesCount(FiltroBusquedaSeries pFiltros) {
        // Filtros de Búsqueda
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltrosCatalogoSerie();

        StringBuilder sbQuery = new StringBuilder(
                "SELECT COUNT(1) FROM VCatalogoSerie vcs ");
        if (!pFiltros.getListaFiltrosCatalogoSerie().isEmpty()) {
            sbQuery.append("WHERE ");

            for (int i = 0; i < filtros.size(); i++) {
                if (i > 0) {
                    sbQuery.append(" AND ");
                }

                FiltroBusqueda filter = filtros.get(i);

                sbQuery.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                        .append(filter.getCampo());
            }
        }
        LOGGER.debug(sbQuery.toString());

        Query query = getEntityManager().createQuery(sbQuery.toString());

        for (FiltroBusqueda filter : filtros) {
            if (filter.getCampo().equals("idAbn") || filter.getCampo().equals("cdgNir")) {
                query.setParameter(filter.getCampo(), filter.getValor().toString());
            } else {
                query.setParameter(filter.getCampo(), filter.getValor());
            }
        }

        Long numResultados = (Long) query.getSingleResult();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Series contadas: " + numResultados);
        }

        return numResultados.intValue();
    }

    @Override
    public boolean existSerieWithNirAbn(String abn, String nir) {

        String query;

        StringBuilder sbQuery = new StringBuilder();
        sbQuery.append("SELECT COUNT(s) FROM Serie s");
        sbQuery.append(" INNER JOIN Nir n ON (s.nir.id = n.id)");
        sbQuery.append(" WHERE n.abn.codigoAbn = :abn AND s.nir.id = :nir");
        // sbQuery.append(" AND s.estatus.cdg = :estatusActivo ");
        sbQuery.append(" AND s.id.sna = '").append("0' ");

        query = sbQuery.toString();

        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);

        // tQuery.setParameter("estatusActivo", Estatus.ACTIVO);
        tQuery.setParameter("nir", Integer.parseInt(nir));
        tQuery.setParameter("abn", new BigDecimal(abn));
        Long resultado = tQuery.getSingleResult();

        return (resultado != null && resultado > 0);
    }

    @Override
    public List<Object[]> findAllSeriesOcupadas(FiltroBusquedaSeries pFiltros) {

        List<Object[]> resultado = new ArrayList<Object[]>();
        boolean ordenacion = false;
        // Consulta
        ArrayList<Entry<String, Object>> listaFiltrosSerie = pFiltros.getFiltrosSerie();
        ArrayList<Entry<String, Object>> listaFiltrosRango = pFiltros.getFiltrosRango();

        StringBuffer sbSelect = new StringBuffer("SELECT nd.sna, nd.nir, nd.ocupacion ");
        StringBuffer sbFrom = new StringBuffer("FROM ");
        StringBuffer sbSubQuery = new StringBuffer("SELECT r.ID_SERIE as sna, r.ID_NIR as nir, ");
        sbSubQuery.append("sum(1 + TO_NUMBER(r.N_FINAL) - TO_NUMBER(r.N_INICIO)) as ocupacion ");
        sbSubQuery.append("FROM RANGO_SERIE r, CAT_SERIE s ")
                .append("WHERE r.ID_SERIE = s.ID_SERIE and s.ID_NIR = r.ID_NIR AND s.ID_ESTATUS =:estado");
        StringBuffer sbGroup = new StringBuffer(" GROUP BY r.ID_SERIE,r.ID_NIR");
        StringBuffer sbWhere = new StringBuffer("WHERE nd.ocupacion < 10000");
        StringBuffer sbOrder = new StringBuffer();

        // Query Dinámica en función de los filtros

        for (Entry<String, Object> filter : listaFiltrosSerie) {
            sbWhere.append(" AND nd.serie.");
            sbWhere.append(filter.getKey()).append(" = :").append(filter.getKey().replace(".", ""));
        }

        for (Entry<String, Object> filter : listaFiltrosRango) {
            if (pFiltros.isOtroAsignatario() && filter.getKey().equals("asignatario")) {
                sbSubQuery.append(" AND r.");
                sbSubQuery.append(filter.getKey()).append(" <> :").append(filter.getKey().replace(".", ""));
            } else {
                sbSubQuery.append(" AND r.");
                sbSubQuery.append(filter.getKey()).append(" = :").append(filter.getKey().replace(".", ""));
            }
        }

        // Ordenamos el resultado.
        if (pFiltros.getNirOrder() != null) {

            if (!ordenacion) {
                sbOrder.append(" order by ");
                ordenacion = true;
            }
            sbOrder.append("nd.serie.nir.codigo " + pFiltros.getNirOrder());
        }

        if (pFiltros.getSnaOrder() != null) {
            if (!ordenacion) {
                sbOrder.append(" order by ");
                ordenacion = true;
            } else {
                sbOrder.append(",");
            }
            sbOrder.append("nd.serie.id.sna " + pFiltros.getSnaOrder());
        }

        // Juntamos todos los stringbuffer
        sbSubQuery.append(sbGroup);
        sbFrom.append("(").append(sbSubQuery).append(") nd ");
        sbSelect.append(sbFrom).append(sbWhere).append(sbOrder);

        // Estado activa
        TypedQuery<Object[]> tQuery = getEntityManager().createQuery(sbSelect.toString(), Object[].class);
        // Query tQuery = getEntityManager().createQuery(sbquery.toString());
        tQuery.setParameter("estado", Estatus.ACTIVO);

        // Resto de parametros
        for (Entry<String, Object> filter : listaFiltrosSerie) {
            tQuery.setParameter(filter.getKey().replace(".", ""), filter.getValue());
        }
        for (Entry<String, Object> filter : listaFiltrosRango) {
            tQuery.setParameter(filter.getKey().replace(".", ""), filter.getValue());
        }

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<Object[]> list = tQuery.getResultList();
        for (Object[] dato : list) {
            Object[] parse = {null, null};
            SeriePK id = new SeriePK();
            id.setSna((BigDecimal) dato[0]);
            id.setIdNir((BigDecimal) dato[0]);
            parse[0] = getEntityManager().find(Serie.class, id);
            parse[1] = dato[1];
            resultado.add(parse);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} series.", list.size());
            // LOGGER.debug("Query: " + tQuery.toString());
        }

        return list;
    }

    @Override
    public String actualizaSerie(BigDecimal serieOriginal, BigDecimal nirOriginal, BigDecimal serieNueva,
            BigDecimal usuario) {
        Query query = getEntityManager().createNamedQuery("actualizarSerie_pa");
        query.setParameter("serie_original", serieOriginal);
        query.setParameter("nir_original", nirOriginal);
        query.setParameter("serie_nueva", serieNueva);
        query.setParameter("usuario", usuario);
        String result = (String) query.getSingleResult();
        return result;
    }
}
