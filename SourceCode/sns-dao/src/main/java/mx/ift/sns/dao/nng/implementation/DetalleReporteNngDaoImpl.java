package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IDetalleReporteNngDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.lineas.DetalleReporteNng;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion del DAO de la vista de DetalleReporteNng.
 * @author X36155QU
 */
@Named
public class DetalleReporteNngDaoImpl extends BaseDAO<DetalleReporteNng> implements IDetalleReporteNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetalleReporteNngDaoImpl.class);

    @Override
    public List<DetalleReporteNng> findAllDetalleReporte(FiltroBusquedaLineasActivas filtros) {

        StringBuffer sbQuery = new StringBuffer("SELECT d");
        StringBuffer sbFrom = new StringBuffer(" FROM DetalleReporteNng d");
        StringBuffer sbWhere = new StringBuffer();

        // Cargamos los filtros
        List<FiltroBusqueda> listaFiltros = filtros.getListaFiltrosNng();

        if (!listaFiltros.isEmpty()) {
            sbWhere.append(" WHERE ");
        }

        for (int i = 0; i < listaFiltros.size(); i++) {
            if (i > 0) {
                sbWhere.append(" AND ");
            }

            FiltroBusqueda filter = listaFiltros.get(i);
            if (filter.isFechaDesde()) {
                sbWhere.append(filter.getPrefijo()).append(".").append("fechaReporte").append(" >= :")
                        .append(filter.getCampo());
            } else if (filter.isFechaHasta()) {
                sbWhere.append(filter.getPrefijo()).append(".").append("fechaReporte").append(" <= :")
                        .append(filter.getCampo());
            } else {
                sbWhere.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                        .append(filter.getCampo());
            }
        }

        // Buscamos el cosecutivo del ultimop reporte si se requeire
        BigDecimal consecutivo = null;

        if (!filtros.isHistorico()) {
            StringBuilder q = new StringBuilder();
            q.append("select DISTINCT(d.consecutivo) ");
            q.append(sbFrom);
            if (!filtros.isEmpty()) {
                q.append(sbWhere);
            }
            q.append(" ORDER BY d.fechaReporte DESC, d.consecutivo DESC");

            TypedQuery<BigDecimal> q1 = getEntityManager().createQuery(q.toString(), BigDecimal.class);
            for (FiltroBusqueda filter : listaFiltros) {
                q1.setParameter(filter.getCampo(), filter.getValor());
            }

            List<BigDecimal> consecutivos = q1.getResultList();

            if (!consecutivos.isEmpty()) {
                consecutivo = consecutivos.get(0);
            }
            LOGGER.debug("ultimo reporte  {}", consecutivo);

            if (consecutivo != null) {
                if (sbWhere.length() == 0) {
                    sbWhere.append(" WHERE d.consecutivo = :consecutivo");
                } else {
                    sbWhere.append(" and d.consecutivo = :consecutivo");
                }
            }

        }

        // Juntamos todos los bufferString de la query
        sbQuery.append(sbFrom).append(sbWhere);

        TypedQuery<DetalleReporteNng> query = getEntityManager().createQuery(sbQuery.toString(),
                DetalleReporteNng.class);

        if (filtros.isUsarPaginacion()) {
            query.setFirstResult(filtros.getNumeroPagina()).setMaxResults(filtros.getResultadosPagina());
        }

        // Cargamos todos los parametros de los filtros
        for (FiltroBusqueda filter : listaFiltros) {
            query.setParameter(filter.getCampo(), filter.getValor());
        }

        if (consecutivo != null) {
            query.setParameter("consecutivo", consecutivo);
        }

        List<DetalleReporteNng> lista = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrados {} Reportes", lista.size());
        }

        return lista;
    }

    @Override
    public Integer findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtros) {

        StringBuffer sbQuery = new StringBuffer("SELECT COUNT(d)");
        StringBuffer sbFrom = new StringBuffer(" FROM DetalleReporteNng d");
        StringBuffer sbWhere = new StringBuffer();

        // Cargamos los filtros
        List<FiltroBusqueda> listaFiltros = filtros.getListaFiltrosNng();

        if (!listaFiltros.isEmpty()) {
            sbWhere.append(" WHERE ");
        }
        for (int i = 0; i < listaFiltros.size(); i++) {
            if (i > 0) {
                sbWhere.append(" AND ");
            }

            FiltroBusqueda filter = listaFiltros.get(i);
            if (filter.getCampo().equals("codigoAbn")) {
                sbFrom.append(", PoblacionAbn pabn");
                sbWhere.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                        .append(filter.getCampo());
                sbWhere.append(" AND d.poblacion = pabn.inegi ");
            } else if (filter.isFechaDesde()) {
                sbWhere.append(filter.getPrefijo()).append(".").append("fechaReporte").append(" >= :")
                        .append(filter.getCampo());
            } else if (filter.isFechaHasta()) {
                sbWhere.append(filter.getPrefijo()).append(".").append("fechaReporte").append(" <= :")
                        .append(filter.getCampo());
            } else {
                sbWhere.append(filter.getPrefijo()).append(".").append(filter.getCampo()).append(" = :")
                        .append(filter.getCampo());
            }
        }

        // Buscamos el cosecutivo del ultimop reporte si se requeire
        BigDecimal consecutivo = null;

        if (!filtros.isHistorico()) {
            StringBuilder q = new StringBuilder();
            q.append("select DISTINCT(d.consecutivo) ");
            q.append(sbFrom);
            if (!filtros.isEmpty()) {
                q.append(sbWhere);
            }
            q.append(" ORDER BY d.fechaReporte DESC, d.consecutivo DESC");

            TypedQuery<BigDecimal> q1 = getEntityManager().createQuery(q.toString(), BigDecimal.class);

            for (FiltroBusqueda filter : listaFiltros) {
                q1.setParameter(filter.getCampo(), filter.getValor());
            }

            List<BigDecimal> consecutivos = q1.getResultList();
            if (!consecutivos.isEmpty()) {
                consecutivo = consecutivos.get(0);
            }
            LOGGER.debug("ultimo reporte  {}", consecutivo);

            if (consecutivo != null) {
                if (filtros.isEmpty()) {
                    sbWhere.append(" where d.consecutivo = :consecutivo");
                } else {
                    sbWhere.append(" and d.consecutivo = :consecutivo");
                }
            }

        }

        // Juntamos todos los bufferString de la query
        sbQuery.append(sbFrom).append(sbWhere);

        TypedQuery<Long> query = getEntityManager().createQuery(sbQuery.toString(), Long.class);

        for (FiltroBusqueda filter : listaFiltros) {
            query.setParameter(filter.getCampo(), filter.getValor());
        }

        if (consecutivo != null) {
            query.setParameter("consecutivo", consecutivo);
        }

        Long resultado = query.getSingleResult();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrados {} Reportes", resultado);
        }

        return resultado.intValue();

    }

    @Override
    public BigDecimal getLastConsecutivoReporte(String clave, Proveedor proveedor) {

        StringBuffer sbquery = new StringBuffer("SELECT MAX(d.consecutivo) ");
        sbquery.append("FROM DetalleReporteNng d ");
        sbquery.append("WHERE d.claveServicio.codigo = :clave AND d.proveedor = :proveedor");
        sbquery.append(" and d.totalLineasActivas is not null");

        TypedQuery<BigDecimal> tquery = getEntityManager().createQuery(sbquery.toString(), BigDecimal.class);
        tquery.setParameter("clave", new BigDecimal(clave));
        tquery.setParameter("proveedor", proveedor);

        return tquery.getSingleResult();
    }
}
