package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.ILineaActivaDao;
import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.lineas.DetalleLineaActiva;
import mx.ift.sns.modelo.lineas.ReporteLineasActivas;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.ng.SolicitudLineasActivas;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion del DAO de lineas activas.
 * @author X36155Q
 */
@Named
public class LineaActivaDaoImpl extends BaseDAO<ReporteLineasActivas> implements ILineaActivaDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LineaActivaDaoImpl.class);

    /**
     * Construye la clausula where a partir del filtro.
     * @param filtro filtro
     * @return clasusula
     */
    private String getPredicateFromFiltro(FiltroBusquedaLineasActivas filtro) {
        StringBuilder b = new StringBuilder();
        boolean primero = true;
        String from;

        if ((filtro.getAbn() != null)) {
            from = "from DetalleLineaActiva d , PoblacionAbn p ";
        } else {
            from = "from DetalleLineaActiva d ";
        }

        b.append(from);
        b.append(" where ");

        if (filtro.getPst() != null) {
            if (!primero) {
                b.append(" and");
            } else {
                primero = false;
            }

            b.append(" d.lineaActiva.solicitudLineasActivas.proveedorSolicitante = :pst");

        }

        if (filtro.getAbn() != null) {
            if (!primero) {
                b.append(" and");
            } else {
                primero = false;
            }
            b.append(" d.poblacion.inegi=p.inegi.inegi and d.poblacion.abn.codigoAbn= :abn");

        }

        if (filtro.getClaveCensal() != null) {
            if (!primero) {
                b.append(" and");
            } else {
                primero = false;
            }
            b.append(" d.poblacion.inegi= : inegi");

        } else {

            if (filtro.getEstado() != null) {
                if (!primero) {
                    b.append(" and");
                } else {
                    primero = false;
                }
                b.append(" d.poblacion.municipio.estado= :estado");

            }

            if (filtro.getMunicipio() != null) {
                if (!primero) {
                    b.append(" and");
                } else {
                    primero = false;
                }
                b.append(" d.poblacion.municipio.municipio= :municipio");

            }

            if (filtro.getPoblacion() != null) {
                if (!primero) {
                    b.append(" and");
                } else {
                    primero = false;
                }
                b.append(" d.poblacion = :poblacion");

            }
        }

        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        if (filtro.getFechaFinal() != null) {
            if (!primero) {
                b.append(" and");
            } else {
                primero = false;
            }
            b.append(" d.lineaActiva.solicitudLineasActivas.fechaSolicitud<=FUNCTION('TO_DATE','");
            b.append(dt.format(filtro.getFechaFinal()));
            b.append("','dd-MM-yyyy')");
        }

        if (filtro.getFechaInicial() != null) {
            if (!primero) {
                b.append(" and");
            } else {
                primero = false;
            }
            b.append(" d.lineaActiva.solicitudLineasActivas.fechaSolicitud>=FUNCTION('TO_DATE','");
            b.append(dt.format(filtro.getFechaInicial()));
            b.append("','dd-MM-yyyy')");
        }

        if (filtro.getConsecutivo() != null) {
            if (!primero) {
                b.append(" and");
            } else {
                primero = false;
            }
            b.append(" d.lineaActiva.id= :consecutivo");

        }

        return b.toString();
    }

    /**
     * Crea la query con los parametros rellenos.
     * @param filtro filstros
     * @param sql sql
     * @return query jpa
     */
    private Query getQuery(FiltroBusquedaLineasActivas filtro, String sql) {
        Query query = getEntityManager().createQuery(sql);
        if (filtro.getPst() != null) {
            query.setParameter("pst", filtro.getPst());
        }

        if (filtro.getAbn() != null) {
            query.setParameter("abn", filtro.getAbn());
        }

        if (filtro.getClaveCensal() != null) {
            query.setParameter("inegi", filtro.getClaveCensal());
        } else {

            if (filtro.getEstado() != null) {
                query.setParameter("estado", filtro.getEstado());
            }

            if (filtro.getMunicipio() != null) {
                query.setParameter("municipio", filtro.getMunicipio());
            }

            if (filtro.getPoblacion() != null) {
                query.setParameter("poblacion", filtro.getPoblacion());
            }
        }

        if (filtro.getConsecutivo() != null) {
            query.setParameter("consecutivo", filtro.getConsecutivo());
        }

        return query;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DetalleLineaActiva> findAllSolicitudesLineasActivas(FiltroBusquedaLineasActivas filtro) {

        LOGGER.debug("");

        String sql;
        String predicado = getPredicateFromFiltro(filtro);

        BigDecimal ultimoReporteId = null;

        if ((filtro.getFechaFinal() == null) && (filtro.getFechaInicial() == null)) {
            StringBuilder q = new StringBuilder();
            q.append("select d.lineaActiva.id ");
            q.append(predicado);
            q.append(" ORDER BY d.lineaActiva.solicitudLineasActivas.fechaSolicitud DESC");

            Query q1 = getQuery(filtro, q.toString());
            List<BigDecimal> consecutivos = q1.getResultList();
            if (!consecutivos.isEmpty()) {
                ultimoReporteId = consecutivos.get(0);
            }
            LOGGER.debug("ultimo reporte  {}", ultimoReporteId);

            StringBuilder b = new StringBuilder();
            if (ultimoReporteId != null) {

                b.append(" and d.lineaActiva.id = :consecutivo");
            }

            sql = "Select d " + predicado + b.toString();
        } else {
            sql = "Select d " + predicado;
        }

        LOGGER.debug("sql lineas {}", sql);

        Query query = getQuery(filtro, sql);

        if (ultimoReporteId != null) {
            query.setParameter("consecutivo", ultimoReporteId);
        }

        if (filtro.isUsarPaginacion()) {
            query.setFirstResult(filtro.getNumeroPagina());
            query.setMaxResults(filtro.getResultadosPagina());
        }

        LOGGER.debug("query completa {}", query);

        List<DetalleLineaActiva> res = query.getResultList();

        if (LOGGER.isDebugEnabled()) {
            if (res != null) {
                LOGGER.debug("num resultados {}", res.size());
            }
        }

        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int findAllSolicitudesLineasActivasCount(FiltroBusquedaLineasActivas filtro) {

        LOGGER.debug("");

        String sql;
        String predicado = getPredicateFromFiltro(filtro);

        BigDecimal ultimoReporteId = null;

        if ((filtro.getFechaFinal() == null) && (filtro.getFechaInicial() == null)) {
            StringBuilder q = new StringBuilder();
            q.append("select d.lineaActiva.id ");
            q.append(predicado);
            q.append(" ORDER BY d.lineaActiva.solicitudLineasActivas.fechaSolicitud DESC");

            Query q1 = getQuery(filtro, q.toString());
            List<BigDecimal> consecutivos = q1.getResultList();
            if (!consecutivos.isEmpty()) {
                ultimoReporteId = consecutivos.get(0);
            }
            LOGGER.debug("ultimo reporte  {}", ultimoReporteId);

            StringBuilder b = new StringBuilder();
            if (ultimoReporteId != null) {

                b.append(" and d.lineaActiva.id = :consecutivo");
            }

            sql = "Select COUNT(d) " + predicado + b.toString();
        } else {
            sql = "Select COUNT(d) " + predicado;
        }

        LOGGER.debug("sql lineas {}", sql);

        Query query = getQuery(filtro, sql);

        if (ultimoReporteId != null) {
            query.setParameter("consecutivo", ultimoReporteId);
        }

        LOGGER.debug("query completa {}", query);

        int rowCount = ((Long) query.getSingleResult()).intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes contadas: " + rowCount);
        }

        return rowCount;
    }

    /**
     * Construye la clausula where a partir del filtro.
     * @param filtro filtro
     * @return clasusula
     */
    private String getPredicateFromFiltroGenerica(FiltroBusquedaLineasActivas filtro) {
        StringBuilder b = new StringBuilder();

        b.append("from SolicitudLineasActivas s , ReporteLineasActivas r , DetalleLineaActiva d "
                + " where r.solicitudLineasActivas.id = s.id and r.id = d.lineaActiva.id and (r.tipoReporte.codigo=");
        b.append(TipoReporte.LINEAS_ACTIVAS);
        b.append(") ");

        if (filtro.getPst() != null) {
            b.append(" and s.proveedorSolicitante.id=");
            b.append(filtro.getPst().getId());
        }

        if (filtro.getClaveCensal() != null) {
            b.append(" and d.poblacion.inegi=");
            b.append(filtro.getClaveCensal());
        }

        if (filtro.getFechaFinal() != null) {
            b.append(" and s.fechaSolicitud<= :fechaFin ");
        }

        if (filtro.getFechaInicial() != null) {
            b.append(" and s.fechaSolicitud>= :fechaInicio ");
        }

        if (filtro.getConsecutivo() != null) {
            b.append(" and s.id=");
            b.append(Long.parseLong(filtro.getConsecutivo()));
        }

        return b.toString();
    }

    @Override
    public List<SolicitudLineasActivas> findAllSolicitudesLineasActivasConsulta(FiltroBusquedaLineasActivas filtro) {
        LOGGER.debug("");

        String q = "Select distinct s " + getPredicateFromFiltroGenerica(filtro);

        LOGGER.debug("query {}", q);

        Query query = getEntityManager().createQuery(q);
        if (filtro.getFechaInicial() != null && filtro.getFechaFinal() != null) {
            query.setParameter("fechaInicio", filtro.getFechaInicial(), TemporalType.DATE);
            query.setParameter("fechaFin", filtro.getFechaFinal(), TemporalType.DATE);
        }

        if (filtro.isUsarPaginacion()) {
            query.setFirstResult(filtro.getNumeroPagina());
            query.setMaxResults(filtro.getResultadosPagina());
        }

        LOGGER.debug("query {}", query);

        @SuppressWarnings("unchecked")
        List<SolicitudLineasActivas> res = query.getResultList();

        return res;
    }

    @Override
    public int findAllSolicitudesLineasActivasConsultaCount(FiltroBusquedaLineasActivas filtro) {

        LOGGER.debug("");

        String q = "Select count(distinct s) " + getPredicateFromFiltroGenerica(filtro);

        LOGGER.debug("query {}", q);

        Query query = getEntityManager().createQuery(q);
        if (filtro.getFechaInicial() != null && filtro.getFechaFinal() != null) {
            query.setParameter("fechaInicio", filtro.getFechaInicial(), TemporalType.DATE);
            query.setParameter("fechaFin", filtro.getFechaFinal(), TemporalType.DATE);
        }

        int rowCount = ((Long) query.getSingleResult()).intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Solicitudes contadas: " + rowCount);
        }

        return rowCount;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ReporteLineasActivas getLastReporteLineasActiva(Proveedor proveedor) {
        List<ReporteLineasActivas> lista = new ArrayList<ReporteLineasActivas>();
        String sQuery = "SELECT r FROM ReporteLineasActivas r "
                + "WHERE r.solicitudLineasActivas.proveedorSolicitante = :proveedor "
                + "ORDER BY r.solicitudLineasActivas.fechaSolicitud DESC";

        Query query = getEntityManager().createQuery(sQuery);
        query.setParameter("proveedor", proveedor);
        LOGGER.debug(query.toString());
        lista = query.getResultList();

        if (!lista.isEmpty()) {
            return lista.get(0);
        } else {
            return null;
        }
    }
}
