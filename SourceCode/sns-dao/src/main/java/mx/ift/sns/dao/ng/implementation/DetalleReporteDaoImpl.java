package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IDetalleReporteDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion del DAO de la vista de DetalleReporte.
 * @author X36155QU
 */
public class DetalleReporteDaoImpl extends BaseDAO<DetalleReporte> implements IDetalleReporteDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetalleReporteDaoImpl.class);

    @Override
    public List<DetalleReporte> findAllDetalleReporte(FiltroBusquedaLineasActivas filtros) {

        StringBuffer sbQuery = new StringBuffer("SELECT d");
        StringBuffer sbFrom = new StringBuffer(" FROM DetalleReporte d");
        StringBuffer sbWhere = new StringBuffer();

        // Cargamos los filtros
        List<FiltroBusqueda> listaFiltros = filtros.getListaFiltrosNg();

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
                    sbWhere.append(" WHERE d.consecutivo = :consecutivo");
                } else {
                    sbWhere.append(" and d.consecutivo = :consecutivo");
                }
            }

        }

        // Juntamos todos los bufferString de la query
        sbQuery.append(sbFrom).append(sbWhere);

        TypedQuery<DetalleReporte> query = getEntityManager().createQuery(sbQuery.toString(), DetalleReporte.class);

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

        List<DetalleReporte> lista = query.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrados {} Reportes", lista.size());
        }

        return lista;
    }

    @Override
    public Integer findAllDetalleReporteCount(FiltroBusquedaLineasActivas filtros) {

        StringBuffer sbQuery = new StringBuffer("SELECT COUNT(d)");
        StringBuffer sbFrom = new StringBuffer(" FROM DetalleReporte d");
        StringBuffer sbWhere = new StringBuffer();

        // Cargamos los filtros
        List<FiltroBusqueda> listaFiltros = filtros.getListaFiltrosNg();

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
                    sbWhere.append(" WHERE d.consecutivo = :consecutivo");
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
    public DetalleReporte getLastDetalleReporteByPoblacion(Proveedor proveedor, String poblacion) {

        DetalleReporte resultado = new DetalleReporte();

        try {

            StringBuffer sbquery = new StringBuffer("select A.* ");
            sbquery.append("from DETALLE_REPORTE_VM A , ");
            sbquery.append("(select max(consecutivo) consecutivo, pst, poblacion ");
            sbquery.append("from DETALLE_REPORTE_VM group by poblacion, pst) b ");
            sbquery.append("where b.consecutivo = a.consecutivo and a.pst = b.pst and a.poblacion = b.poblacion");
            sbquery.append(" and a.poblacion = ? and a.pst = ? and ROWNUM <=1");

            Query query = getEntityManager().createNativeQuery(sbquery.toString(), DetalleReporte.class);

            query.setParameter(1, poblacion);
            query.setParameter(2, proveedor.getId());

            resultado = (DetalleReporte) query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("FIN");
            resultado.setTotalAsignadas(new BigDecimal(0));
            resultado.setTotalAsignadasCpp(new BigDecimal(0));
            resultado.setTotalAsignadasMpp(new BigDecimal(0));
            resultado.setTotalAsignadasFijas(new BigDecimal(0));
            resultado.setTotalLineasActivas(new BigDecimal(0));
            resultado.setTotalLineasActivasCpp(new BigDecimal(0));
            resultado.setTotalLineasActivasMpp(new BigDecimal(0));
            resultado.setTotalLineasActivasFijas(new BigDecimal(0));

        }

        return resultado;
    }

    @Override
    public Object[] getTotalDetalleReporteByAbn(Proveedor proveedor, BigDecimal abn) {
        // if (proveedor != null) {
        // LOGGER.debug(proveedor.getId().toString() + "-" + abn.toString());
        // } else {
        // LOGGER.debug("null-" + abn.toString());
        // }
        Object[] resultado = {new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
                new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0)};

        try {
            StringBuffer sbquery = new StringBuffer("select /*+ parallel(2) */ ");

            sbquery.append("SUM(d1.TOTAL_LINEAS_ACTIVAS), ");
            sbquery.append("SUM(d1.TOTAL_LINEAS_ACTIVAS_FIJAS), ");
            sbquery.append("SUM(d1.TOTAL_LINEAS_ACTIVAS_CPP), ");
            sbquery.append("SUM(d1.TOTAL_LINEAS_ACTIVAS_MPP), ");
            sbquery.append("SUM(d1.TOTAL_ASIGNADAS), ");
            sbquery.append("SUM(d1.TOTAL_ASIGNADAS_FIJA), ");
            sbquery.append("SUM(d1.TOTAL_ASIGNADAS_CPP), ");
            sbquery.append("SUM(d1.TOTAL_ASIGNADAS_MPP) ");
            sbquery.append("from DETALLE_REPORTE_VM d1 ,");
            sbquery.append("(select /*+ parallel(2) */ max(consecutivo) consecutivo, pst, abn ");
            sbquery.append("from DETALLE_REPORTE_VM group by abn, pst) d2 ");
            sbquery.append(" where d2.consecutivo = d1.consecutivo and d2.pst = d1.pst and d2.abn = d1.abn ");
            sbquery.append(" and d1.abn = ? and d1.pst = ?");

            // sbquery.append(" WHERE ");
            // if (proveedor != null) {
            // sbquery.append(" d2.proveedor = :proveedor");
            // } else {
            // sbquery.append(" d2.proveedor = d1.proveedor");
            // }
            //
            // sbquery.append(") AND d1.abn = :abn");

            // TypedQuery<Object[]> tquery = getEntityManager()
            // .createQuery(sbquery.toString(), Object[].class);
            // tquery.setParameter("abn", abn);
            // if (proveedor != null) {
            // tquery.setParameter("proveedor", proveedor);
            // }

            Query query = getEntityManager().createNativeQuery(sbquery.toString());
            query.setParameter(1, abn);
            query.setParameter(2, proveedor.getId());

            resultado = (Object[]) query.getResultList().get(0);
        } catch (NoResultException e) {
            LOGGER.debug("");
            return resultado;
        }
        if (resultado[0] == null) {
            for (int i = 0; i < 8; i++) {
                resultado[i] = new BigDecimal(0);
            }
        }

        return resultado;
    }

    @Override
    public DetalleReporte findTotalesActivosDetalleReporte(FiltroReporteadorNG filtros) {

        StringBuffer sbQuery = new StringBuffer("");
        StringBuffer sbQueryInicio = new StringBuffer(
                "select /*+ parallel(2) */ -1 as ID_DETALLE_REPORTE , "
                + "sum(a.total_lineas_activas) as total_lineas_activas ,"
                        + " sum(total_lineas_activas_fijas) as total_lineas_activas_fijas, "
                        + "sum(total_lineas_activas_cpp) as total_lineas_activas_cpp,"
                        + " sum(total_lineas_activas_mpp) as total_lineas_activas_mpp "
                        + "from DETALLE_REPORTE_VM A ,"
                        + "(select /*+ parallel(2) */ max(consecutivo) consecutivo, "
                        + "pst, poblacion from DETALLE_REPORTE_VM "
                        + "where FECHA_REPORTE < ?  group by poblacion, pst) b ");

        StringBuffer sbQueryFinal = new StringBuffer(
                " where b.consecutivo = a.consecutivo and a.pst = b.pst and a.poblacion = b.poblacion ");

        if (null != filtros.getPstSeleccionada()) {
            sbQueryFinal.append(" AND A.PST = ? ");
        }

        if (null != filtros.getPoblacionSeleccionada()
                && !"".equals(filtros.getPoblacionSeleccionada().getInegi())) {
            sbQueryFinal.append(" AND A.POBLACION = ? ");
        } else if (null != filtros.getMunicipioSeleccionado()
                && !"".equals(filtros.getMunicipioSeleccionado().getId().getCodMunicipio())) {
            sbQueryInicio.append(" ,CAT_POBLACION CP ");
            sbQueryFinal.append(" AND CP.ID_INEGI = a.POBLACION  AND CP.ID_MUNICIPIO = ?  AND CP.ID_ESTADO = ? ");
        } else if (null != filtros.getEstadoSeleccionado()) {
            sbQueryInicio.append(" ,CAT_POBLACION CP ");
            sbQueryFinal.append(" AND CP.ID_INEGI = a.POBLACION  AND CP.ID_ESTADO = ? ");
        }

        if (null != filtros.getAbnSeleccionado()) {
            sbQueryInicio.append(" ,POBLACION_ABN pa  ");
            sbQueryFinal.append(" and a.POBLACION = pa.ID_INEGI and pa.id_abn = ? ");
        }

        // Juntamos todos los bufferString de la query
        sbQuery.append(sbQueryInicio).append(sbQueryFinal);

        int numeroParametro = 1;
        Query query = getEntityManager().createNativeQuery(sbQuery.toString(), DetalleReporte.class);
        query.setParameter(numeroParametro, filtros.getFechaFin());
        numeroParametro++;

        if (null != filtros.getPstSeleccionada()) {
            query.setParameter(numeroParametro, filtros.getPstSeleccionada().getId());
            numeroParametro++;
        }

        if (null != filtros.getPoblacionSeleccionada()
                && !"".equals(filtros.getPoblacionSeleccionada().getInegi())) {
            query.setParameter(numeroParametro, filtros.getPoblacionSeleccionada().getInegi());
            numeroParametro++;
        } else if (null != filtros.getMunicipioSeleccionado()
                && !"".equals(filtros.getMunicipioSeleccionado().getId().getCodMunicipio())) {
            query.setParameter(numeroParametro, filtros.getMunicipioSeleccionado().getId().getCodMunicipio());
            numeroParametro++;
            query.setParameter(numeroParametro, filtros.getMunicipioSeleccionado().getId().getCodEstado());
            numeroParametro++;
        } else if (null != filtros.getEstadoSeleccionado()) {
            query.setParameter(numeroParametro, filtros.getEstadoSeleccionado().getCodEstado());
            numeroParametro++;
        }

        if (null != filtros.getAbnSeleccionado()) {
            query.setParameter(numeroParametro, filtros.getAbnSeleccionado());

        }

        LOGGER.debug("Encontrados {} Query", query.toString());

        DetalleReporte totales = (DetalleReporte) query.getSingleResult();

        return totales;
    }
}
