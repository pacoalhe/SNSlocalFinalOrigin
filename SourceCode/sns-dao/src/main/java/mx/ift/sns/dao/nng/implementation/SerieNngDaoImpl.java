package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.ISerieNngDao;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.nng.SerieNngPK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Series.
 */
@Named
public class SerieNngDaoImpl extends BaseDAO<SerieNng> implements ISerieNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SerieNngDaoImpl.class);

    @Override
    public List<SerieNng> findAllSeries(FiltroBusquedaSeries pFiltros) {
        boolean ordenacion = false;
        StringBuffer sbquery = new StringBuffer("SELECT s FROM SerieNng s");

        sbquery.append(" WHERE s.estatus.cdg = :activa");
        sbquery.append(" AND (SELECT COUNT(r) FROM RangoSerieNng r ");
        sbquery.append("WHERE r.serie.id = s.id)");
        if (pFiltros.isSerieLibre()) {
            sbquery.append(" = 0 ");
        } else {
            sbquery.append(" > 0 ");
            if (pFiltros.isRangosDisponibles()) {
                sbquery.append(" AND 10000 > (SELECT ");
                sbquery.append("sum(1 + FUNC('TO_NUMBER',r.numFinal) - FUNC('TO_NUMBER',r.numInicio))");
                sbquery.append(" from RangoSerieNng r");
                sbquery.append("  where r.serie.id = s.id)");
            }
        }

        // Query Dinámica en función de los filtros
        ArrayList<Entry<String, Object>> listaFiltrosSerie = pFiltros.getFiltrosSerie();
        for (Entry<String, Object> filter : listaFiltrosSerie) {
            sbquery.append(" AND s.");
            sbquery.append(filter.getKey()).append(" = :").append(filter.getKey().replace(".", ""));
        }

        if (pFiltros.getClaveOrder() != null) {
            if (!ordenacion) {
                sbquery.append(" order by ");
                ordenacion = true;
            } else {
                sbquery.append(",");
            }
            sbquery.append("s.id.idClaveServicio " + pFiltros.getClaveOrder());
        }

        if (pFiltros.getSnaOrder() != null) {
            if (!ordenacion) {
                sbquery.append(" order by ");
                ordenacion = true;
            } else {
                sbquery.append(",");
            }
            sbquery.append("s.id.sna " + pFiltros.getSnaOrder());
        }

        // Estado activa
        TypedQuery<SerieNng> tQuery = getEntityManager().createQuery(sbquery.toString(), SerieNng.class);
        tQuery.setParameter("activa", Estatus.ACTIVO);

        // Resto de parametros
        for (Entry<String, Object> filter : listaFiltrosSerie) {
            tQuery.setParameter(filter.getKey().replace(".", ""), filter.getValue());
        }

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<SerieNng> list = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} series.", list.size());
        }

        return list;
    }

    @Override
    public Integer findAllSeriesCount(FiltroBusquedaSeries pFiltros) {

        StringBuffer sbquery = new StringBuffer("SELECT COUNT(s) FROM SerieNng s");

        sbquery.append(" WHERE s.estatus.cdg = :activa");
        sbquery.append(" AND (SELECT COUNT(r) FROM RangoSerieNng r ");
        sbquery.append("WHERE r.serie.id = s.id)");
        if (pFiltros.isSerieLibre()) {
            sbquery.append(" = 0 ");
        } else {
            sbquery.append(" > 0 ");
            if (pFiltros.isRangosDisponibles()) {
                sbquery.append(" AND 10000 > (SELECT ");
                sbquery.append("sum(1 + FUNC('TO_NUMBER',r.numFinal) - FUNC('TO_NUMBER',r.numInicio))");
                sbquery.append(" from RangoSerieNng r");
                sbquery.append("  where r.serie.id = s.id)");
            }
        }

        // Query Dinámica en función de los filtros
        ArrayList<Entry<String, Object>> listaFiltrosSerie = pFiltros.getFiltrosSerie();
        for (Entry<String, Object> filter : listaFiltrosSerie) {
            sbquery.append(" AND s.");
            sbquery.append(filter.getKey()).append(" = :").append(filter.getKey().replace(".", ""));
        }

        // Estado activa
        TypedQuery<Long> tQuery = getEntityManager().createQuery(sbquery.toString(), Long.class);
        tQuery.setParameter("activa", Estatus.ACTIVO);

        // Resto de parametros
        for (Entry<String, Object> filter : listaFiltrosSerie) {
            tQuery.setParameter(filter.getKey().replace(".", ""), filter.getValue());
        }

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        int rowCount = tQuery.getSingleResult().intValue();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han contado {} series", rowCount);
        }

        return rowCount;
    }

    @Override
    public List<SerieNng> findRandomSeriesLibreByClaveServicio(ClaveServicio clave, int n) {

        List<SerieNng> listaSeries = new ArrayList<SerieNng>();

        StringBuffer sbquery = new StringBuffer("SELECT s FROM SerieNng s");
        StringBuffer sbcount = new StringBuffer("SELECT COUNT(s) FROM SerieNng s");
        StringBuffer sbwhere = new StringBuffer(" where s.id.idClaveServicio = :clave ");

        // Construimos el where comprobando si una serie libre tiene n-1 series consecutivas libres
        for (int i = 0; i < n; i++) {
            sbwhere.append("AND (SELECT COUNT(r) FROM RangoSerieNng r ");
            sbwhere.append("WHERE r.id.sna = s.id.sna + ").append(i).append(")");
            sbwhere.append(" = 0 ");
        }

        // Obtenemos el total de registros que cumplen las condiciones para generar el random
        sbcount.append(sbwhere);
        TypedQuery<Long> tcount = getEntityManager().createQuery(sbcount.toString(), Long.class);
        tcount.setParameter("clave", clave.getCodigo());
        long total = tcount.getSingleResult();

        if (total > 0) {
            // Obtenemos una serie aleatoria
            Random rnd = new Random();
            sbquery.append(sbwhere);
            TypedQuery<SerieNng> tquery = getEntityManager().createQuery(sbquery.toString(), SerieNng.class);
            tquery.setParameter("clave", clave.getCodigo());
            SerieNng serie = tquery.setMaxResults(1).setFirstResult(rnd.nextInt((int) total)).getSingleResult();
            listaSeries.add(serie);
            // Añadimos las series consecutivas
            BigDecimal sna = serie.getId().getSna();
            for (int j = 1; j < n; j++) {
                SerieNngPK id = new SerieNngPK();
                id.setIdClaveServicio(clave.getCodigo());
                id.setSna(sna.add(new BigDecimal(j)));
                listaSeries.add(getEntityManager().find(SerieNng.class, id));
            }
        }
        return listaSeries;
    }

    @Override
    public SerieNng getRandomSerieOcupadaByClaveServicio(ClaveServicio clave, BigDecimal cantidad) {

        SerieNng serie = null;

        StringBuffer sbquery = new StringBuffer("SELECT s FROM SerieNng s");
        StringBuffer sbcount = new StringBuffer("SELECT COUNT(s) FROM SerieNng s");
        StringBuffer sbwhere = new StringBuffer(" where s.id.idClaveServicio = :clave ");

        // Comprobamos que la cantidad solicitada es menor a la disponible
        sbwhere.append("AND :cantidad >= (SELECT ");
        sbwhere.append("sum(1 + FUNC('TO_NUMBER',r.numFinal) - FUNC('TO_NUMBER',r.numInicio))");
        sbwhere.append(" from RangoSerieNng r");
        sbwhere.append("  where r.serie.id = s.id)");

        // Obtenemos el total de registros que cumplen las condiciones para generar el random
        sbcount.append(sbwhere);
        TypedQuery<Long> tcount = getEntityManager().createQuery(sbcount.toString(), Long.class);
        tcount.setParameter("clave", clave.getCodigo());
        tcount.setParameter("cantidad", 10000 - cantidad.intValue());
        long total = tcount.getSingleResult();

        if (total > 0) {
            // Obtenemos una serie aleatoria
            Random rnd = new Random();
            sbquery.append(sbwhere);
            TypedQuery<SerieNng> tquery = getEntityManager().createQuery(sbquery.toString(), SerieNng.class);
            tquery.setParameter("clave", clave.getCodigo());
            tquery.setParameter("cantidad", 10000 - cantidad.intValue());
            serie = tquery.setMaxResults(1).setFirstResult(rnd.nextInt((int) total)).getSingleResult();
        }
        return serie;
    }

    @Override
    public SerieNng getSerie(BigDecimal pIdClaveServicio, BigDecimal pSna) {
        String query = "SELECT s FROM SerieNng s WHERE s.id.idClaveServicio = :claveServicio AND s.id.sna = :sna";
        SerieNng serie = null;
        try {
            TypedQuery<SerieNng> tQuery = getEntityManager().createQuery(query, SerieNng.class);
            tQuery.setParameter("claveServicio", pIdClaveServicio);
            tQuery.setParameter("sna", pSna);
            serie = tQuery.getSingleResult();
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se ha encontrado la serie {} para idClaveServicio {}", pSna, pIdClaveServicio);
            }
            return null;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SerieNng {} recuperada para idClaveServicio {}", pSna, pIdClaveServicio);
        }
        return serie;
    }

}
