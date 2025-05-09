package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IHistoricoSerieDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.reporteador.ElementoAgrupador;
import mx.ift.sns.modelo.reporteador.NGReporteador;
import mx.ift.sns.modelo.series.HistoricoSerie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de HsitoricoSerie.
 */
@Named
public class HistoricoSerieDAOImpl extends BaseDAO<HistoricoSerie> implements IHistoricoSerieDAO {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoricoSerieDAOImpl.class);
    /** Formateador de fechas. */
    private SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
    /** Cte del alias para la consulta resultantes de obtener las solicitudes asignadas para reportes. */
    private static final String ASIGNADAS = "Asignadas";
    /** Cte del alias para la consulta resultante de obtener las solicitudes de numeraciones cedidas para reportes. */
    private static final String CEDIDAS = "Cedidas";
    /** Cte del PST Cesionario. */
    private static final String PST_CESIONARIO = "ID_PST_CESIONARIO";
    /** Cte del PST Cedente. */
    private static final String PST_CEDENTE = "ID_PST";

    /**
     * Construye las condiciones a partir del filtro.
     * @param filtro de busqueda
     * @return cadena jpa
     */
    private String contruirFiltro(FiltroBusquedaHistoricoSeries filtro) {
        StringBuilder b = new StringBuilder("");

        if (filtro.getIdAbn() != null) {
            if (b.length() == 0) {
                b.append(" h.idAbn= :idAbn");
            } else {
                b.append(" and h.idAbn= :idAbn");
            }
        }

        if (filtro.getCdgNir() != null) {
            if (b.length() == 0) {
                b.append(" h.cdgNir= :cdg_nir");
            } else {
                b.append(" and h.cdgNir= :cdg_nir");
            }
        }

        if (filtro.getIdSna() != null) {
            if (b.length() == 0) {
                b.append(" h.idSerie= :sna");
            } else {
                b.append(" and h.idSerie= :sna");
            }
        }

        if (filtro.getNumIni() != null) {
            if (b.length() == 0) {
                b.append(" h.inicioRango = :num_inicio");
            } else {
                b.append(" and h.inicioRango = :num_inicio");
            }
        }

        if (filtro.getNumFin() != null) {
            if (b.length() == 0) {
                b.append(" h.finRango = :num_final");
            } else {
                b.append(" and h.finRango = :num_final");
            }
        }

        if (filtro.getEstado() != null) {
            if (b.length() == 0) {
                b.append(" h.idEstado= :cod_estado");
            } else {
                b.append(" and h.idEstado= :cod_estado");
            }
        }

        if (filtro.getMunicipio() != null) {
            if (b.length() == 0) {
                b.append(" h.idMunicipio= :cod_municipio");
            } else {
                b.append(" and h.idMunicipio= :cod_municipio");
            }
        }

        if (filtro.getPoblacion() != null) {
            if (b.length() == 0) {
                b.append(" h.inegi= :cod_poblacion");
            } else {
                b.append(" and h.inegi= :cod_poblacion");
            }
        }

        if (filtro.getTipoRed() != null) {
            if (b.length() == 0) {
                b.append(" h.idTipoRed= :cod_tipo_red");
            } else {
                b.append(" and h.idTipoRed= :cod_tipo_red");
            }

        }

        if (filtro.getTipoModalidad() != null) {
            if (b.length() == 0) {
                b.append(" h.idTipoModalidad= :cod_tipo_modalidad");
            } else {
                b.append(" and h.idTipoModalidad= :cod_tipo_modalidad");
            }
        }

        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");

        if (filtro.getFechaInicio() != null) {
            if (b.length() == 0) {
                b.append(" h.fechaAsignacion>= FUNCTION('TO_DATE','");
            } else {
                b.append(" and h.fechaAsignacion>= FUNCTION('TO_DATE','");
            }

            b.append(dt.format(filtro.getFechaInicio()));
            b.append("','dd-MM-yyyy')");
        }

        if (filtro.getFechaFin() != null) {
            if (b.length() == 0) {
                b.append(" h.fechaAsignacion<= FUNCTION('TO_DATE','");
            } else {
                b.append(" and h.fechaAsignacion<= FUNCTION('TO_DATE','");
            }

            b.append(dt.format(filtro.getFechaFin()));
            b.append("','dd-MM-yyyy')");
        }

        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }

        return r;
    }

    /**
     * Convierte string a bigdecimal.
     * @param s string
     * @return numero dcimal
     */
    private BigDecimal toBigDecimal(String s) {

        int i = 0;

        i = Integer.parseInt(s);
        BigDecimal b = new BigDecimal(i);

        return b;
    }

    /**
     * Crea la query con los parametros rellenos.
     * @param filtro filstros
     * @param sql sql
     * @return query jpa
     */
    private Query getQuery(FiltroBusquedaHistoricoSeries filtro, String sql) {
        Query query = getEntityManager().createQuery(sql);

        if (filtro.getIdAbn() != null) {
            query.setParameter("idAbn", toBigDecimal(filtro.getIdAbn()));
        }

        if (filtro.getCdgNir() != null) {
            query.setParameter("cdg_nir", toBigDecimal(filtro.getCdgNir()));
        }

        if (filtro.getIdSna() != null) {
            query.setParameter("sna", toBigDecimal(filtro.getIdSna()));
        }

        if (filtro.getNumIni() != null) {
            query.setParameter("num_inicio", filtro.getNumIni());
        }

        if (filtro.getNumFin() != null) {
            query.setParameter("num_final", filtro.getNumFin());
        }

        if (filtro.getEstado() != null) {
            query.setParameter("cod_estado", filtro.getEstado().getCodEstado());
        }

        if (filtro.getMunicipio() != null) {
            query.setParameter("cod_municipio", filtro.getMunicipio().getId().getCodMunicipio());
        }

        if (filtro.getPoblacion() != null) {
            query.setParameter("cod_poblacion", filtro.getPoblacion().getInegi());
        }

        if (filtro.getTipoRed() != null) {
            query.setParameter("cod_tipo_red", filtro.getTipoRed().getCdg());
        }

        if (filtro.getTipoModalidad() != null) {
            query.setParameter("cod_tipo_modalidad", filtro.getTipoModalidad().getCdg());
        }

        return query;
    }

    @Override
    public List<HistoricoSerie> findHistoricoSeries(FiltroBusquedaHistoricoSeries filtro, int first, int pagesize) {

        LOGGER.debug("firt {} pagesize {}", first, pagesize);

        StringBuilder b = new StringBuilder();

        b.append("select h from HistoricoSerie h ");

        String f = contruirFiltro(filtro);
        if (!f.isEmpty()) {
            b.append(" where ");
            b.append(f);
        }

        b.append(" order by h.consecutivo");

        String q = b.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: {}", q);
        }

        Query query = getQuery(filtro, q);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        query.setFirstResult(first);
        query.setMaxResults(pagesize);

        @SuppressWarnings("unchecked")
        List<HistoricoSerie> list = query.getResultList();

        if (LOGGER.isDebugEnabled()) {
            if (list != null) {
                LOGGER.debug("#n {}", list.size());
            }
        }
        return list;
    }

    @Override
    public int findHistoricoSeriesCount(FiltroBusquedaHistoricoSeries filtro) {

        StringBuilder b = new StringBuilder();
        // b.append("select count(distinct rango) from RangoSerie rango , Solicitud  sol,  Oficio so ,PoblacionAbn pa  "
        // + "where rango.id.id=sol.id and so.id=sol.id");
        b.append("select count(h) from HistoricoSerie h ");

        String f = contruirFiltro(filtro);
        if (!f.isEmpty()) {
            b.append(" where ");
            b.append(f);
        }

        String q = b.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: {}", q);
        }

        Query query = getQuery(filtro, q);

        int l = ((Long) query.getSingleResult()).intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("#={} Query: {}", l, query);
        }

        return l;
    }

    /**
     * Obtención de los datos de Asignadas de la vista necesarios para los informes del reporteador.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<NGReporteador> findHistoricoSeriesTotalesAsignadas(FiltroReporteadorNG filtro) {

        StringBuffer b = new StringBuffer();
        List<NGReporteador> lista = new ArrayList<NGReporteador>();

        String stringIdPST = "";
        String stringIdPSTCesionario = "";
        if (filtro.getPstSeleccionada() != null) {
            stringIdPST = " AND ID_PST= " + filtro.getPstSeleccionada().getId();
            stringIdPSTCesionario = "AND ID_PST_CESIONARIO=" + filtro.getPstSeleccionada().getId();
        }

        try {

            b.append("SELECT ROWNUM as lineaConsulta, Asignadas.* from (SELECT /*+ parallel(2) */ ");

            b.append(" SUM(case  when  id_tipo_solicitud = 1 " + stringIdPST + " then fin_rango - inicio_rango + 1 ");
            b.append(" when id_tipo_solicitud = 3 " + stringIdPST + " then inicio_rango -fin_rango -1 ");
            if (filtro.getPstSeleccionada() != null) {
                b.append(" when id_tipo_solicitud = 2 " + stringIdPSTCesionario
                        + " then  fin_rango - inicio_rango + 1 ");
                b.append(" when id_tipo_solicitud = 2 " + stringIdPST + " then inicio_rango -fin_rango -1 ");
            }
            b.append(" else 0 end)   AS totalAsignadas, ");

            b.append(" SUM(case  when  id_tipo_red = 'F' and id_tipo_solicitud = 1 " + stringIdPST
                    + " then fin_rango - inicio_rango + 1 ");
            b.append(" when id_tipo_red = 'F' and id_tipo_solicitud = 3 " + stringIdPST
                    + " then inicio_rango -fin_rango -1 ");
            if (filtro.getPstSeleccionada() != null) {
                b.append(" when id_tipo_red = 'F' and id_tipo_solicitud = 2 " + stringIdPSTCesionario
                        + " then  fin_rango - inicio_rango + 1 ");
                b.append(" when id_tipo_red = 'F' and id_tipo_solicitud = 2 " + stringIdPST
                        + " then inicio_rango -fin_rango -1 ");
            }
            b.append(" else 0 end) AS asignadasFijas, ");

            b.append(" SUM(case  when  ID_TIPO_MODALIDAD = 'CPP' and id_tipo_solicitud = 1 " + stringIdPST
                    + " then fin_rango - inicio_rango + 1 ");
            b.append(" when ID_TIPO_MODALIDAD = 'CPP' and id_tipo_solicitud = 3 " + stringIdPST
                    + " then inicio_rango -fin_rango -1 ");
            if (filtro.getPstSeleccionada() != null) {
                b.append(" when ID_TIPO_MODALIDAD = 'CPP' and id_tipo_solicitud = 2 " + stringIdPSTCesionario
                        + " then  fin_rango - inicio_rango + 1 ");
                b.append(" when ID_TIPO_MODALIDAD = 'CPP' and id_tipo_solicitud = 2 " + stringIdPST
                        + " then inicio_rango -fin_rango -1 ");
            }
            b.append(" else 0 end) AS asignadasMovilesCPP, ");

            b.append(" SUM(case  when  ID_TIPO_MODALIDAD = 'MPP' and id_tipo_solicitud = 1 " + stringIdPST
                    + " then fin_rango - inicio_rango + 1 ");
            b.append(" when ID_TIPO_MODALIDAD = 'MPP' and id_tipo_solicitud = 3 " + stringIdPST
                    + " then inicio_rango -fin_rango -1 ");
            if (filtro.getPstSeleccionada() != null) {
                b.append(" when ID_TIPO_MODALIDAD = 'MPP' and id_tipo_solicitud = 2 " + stringIdPSTCesionario
                        + " then  fin_rango - inicio_rango + 1 ");
                b.append(" when ID_TIPO_MODALIDAD = 'MPP' and id_tipo_solicitud = 2 " + stringIdPST
                        + " then inicio_rango -fin_rango -1 ");
            }
            b.append(" else 0 end)  AS asignadasMovilesMPP ");

            b.append(" FROM HISTORICO_SERIE_VM ");

            String f = contruirFiltroTotalesAsignadas(filtro);
            if (!f.isEmpty()) {
                b.append(" WHERE ");
                b.append(f);
            }

            b.append(" ) Asignadas ");

            String q = b.toString();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("QueryTexto: {}", q);
            }

            Query query = getEntityManager().createNativeQuery(q, NGReporteador.class);

            lista = query.getResultList();

            if (LOGGER.isDebugEnabled()) {
                if (lista != null) {
                    LOGGER.debug("#n {}", lista.size());
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return lista;
    }

    /**
     * Obtención de los datos de la vista necesarios para los informes del reporteador.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<NGReporteador> findHistoricoSeries(FiltroReporteadorNG filtro) {

        StringBuffer sbQuery = new StringBuffer("SELECT ROWNUM as lineaConsulta, Asignadas.*, Activas.* ");
        StringBuffer sbFrom = new StringBuffer();
        StringBuffer sbSubQuery = new StringBuffer();
        StringBuffer sbWhere = new StringBuffer();
        StringBuffer sbGroupBy = new StringBuffer();
        StringBuffer sbOrderBy = new StringBuffer();
        List<NGReporteador> lista = new ArrayList<NGReporteador>();

        try {
            // Comenzamos con la parte de la query relativa a las numeraciones solicitadas

            if (!isFiltroVacio(filtro)) {
                String sComunes = contruirColumnasComunes(filtro);
                if (!sComunes.isEmpty()) {
                    sbQuery.append(", ");
                    sbQuery.append(sComunes);
                }
            }
            sbQuery.append(" from (SELECT ");
            String sAsignadas = contruirColumnasAsignadas(filtro);
            if (!sAsignadas.isEmpty()) {
                sbQuery.append(sAsignadas);
            }
            String sSumatoriosPorTipoSolicitud = construirSumatoriosPorTipoSolicitud();
            if (!sSumatoriosPorTipoSolicitud.isEmpty()) {
                sbQuery.append(sSumatoriosPorTipoSolicitud);
            }
            sbFrom.append(" FROM HISTORICO_SERIE_VM ");

            String fAsignadas = contruirFiltroAsignadas(filtro, PST_CEDENTE);
            if (!fAsignadas.isEmpty()) {
                sbWhere.append(" WHERE ");
                sbWhere.append(fAsignadas);
            }

            String gAsignadas = contruirAgrupacionAsignadas(filtro, PST_CEDENTE);
            if (!gAsignadas.isEmpty()) {
                sbGroupBy.append(" Group by ");
                sbGroupBy.append(gAsignadas);
            }
            sbGroupBy.append(" ) Asignadas ");

            // Juntamos todos los bufferString de la query
            sbQuery.append(sbFrom).append(sbWhere).append(sbGroupBy);

            // Reseteamos las variables para conformar el siguiente tramo de la query relativa a las Activas
            sbFrom.setLength(0);
            sbWhere.setLength(0);
            sbGroupBy.setLength(0);

            if (!isFiltroVacio(filtro)) {
                sbQuery.append(" FULL OUTER JOIN (SELECT ");
            } else {
                sbQuery.append(" , (SELECT ");
            }

            String sActivas = contruirColumnasActivas(filtro);
            if (!sActivas.isEmpty()) {
                sbQuery.append(sActivas);
            }
            sbQuery.append(" SUM(total_lineas_activas) as totalActivas, "
                    + "SUM(total_lineas_activas_fijas) as activasFijas, "
                    + "SUM(total_lineas_activas_cpp + total_lineas_activas_mpp) as activasMoviles, "
                    + "SUM(total_lineas_activas_cpp) as activasMovilesCPP, "
                    + "SUM(total_lineas_activas_mpp) as activasMovilesMPP ");
            sbFrom.append(" FROM DETALLE_REPORTE_VM a ");
            String sJoinTablas = construirInnerJoinTablasCatalago(filtro);
            if (!sJoinTablas.isEmpty()) {
                sbFrom.append(sJoinTablas);
            }
            sbFrom.append(" , ");
            sbSubQuery.append("(select max(consecutivo) as consecutivo, pst, poblacion "
                    + "from DETALLE_REPORTE_VM a "
                    + "where FECHA_REPORTE < CURRENT_DATE + 1 "
                    + "group by poblacion, pst) b ");

            String fActivas = contruirFiltroActivas(filtro);
            if (!fActivas.isEmpty()) {
                sbWhere.append(" WHERE ");
                sbWhere.append(fActivas);
            }

            String gActivas = contruirAgrupacionActivas(filtro);
            if (!gActivas.isEmpty()) {
                sbGroupBy.append(" Group by ");
                sbGroupBy.append(gActivas);
            }
            sbGroupBy.append(" ) Activas ");

            // Juntamos todos los bufferString de la query
            sbQuery.append(sbFrom).append(sbSubQuery).append(sbWhere).append(sbGroupBy);

            // Y relacionamos las dos tramos de query si fuera preciso
            sbWhere.setLength(0);
            String wQuery = construirRelacionAsignadasActivas(filtro);
            if (!wQuery.isEmpty()) {
                sbWhere.append(" ON ");
                sbWhere.append(wQuery);
            }

            // Por último, ordenamos la sentencia por los elementos de agrupación
            sbOrderBy.setLength(0);
            String oQuery = construirOrdenacion(filtro);
            if (!oQuery.isEmpty()) {
                sbOrderBy.append(" ORDER BY ");
                sbOrderBy.append(oQuery);
            }

            String q = sbQuery.append(sbWhere).append(sbOrderBy).toString();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Query: {}", q);
            }

            Query query = getEntityManager().createNativeQuery(q, NGReporteador.class);

            lista = query.getResultList();

            if (LOGGER.isDebugEnabled()) {
                if (lista != null) {
                    LOGGER.debug("#n {}", lista.size());
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return lista;
    }

    /**
     * Construye los elementos que se devolverán como resultado de la petición.
     * @param filtro de busqueda del informe del reporteador
     * @return cadena jpa
     */
    private String contruirColumnasComunes(FiltroReporteadorNG filtro) {
        StringBuilder b = new StringBuilder("");

        if (filtro.getPrimeraAgrupacion() != null) {
            if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                b.append(" CASE WHEN ASIGNADAS.ID_PST IS NOT NULL THEN ASIGNADAS.ID_PST ");
                b.append(" ELSE ACTIVAS.ID_PST END AS idPst, ");
                b.append(" CASE WHEN ASIGNADAS.D_PST IS NOT NULL THEN ASIGNADAS.D_PST ");
                b.append(" ELSE ACTIVAS.D_PST END AS pst,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                b.append(" CASE WHEN ASIGNADAS.D_ESTADO IS NOT NULL THEN ASIGNADAS.D_ESTADO ");
                b.append(" ELSE ACTIVAS.D_ESTADO END AS estado,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                b.append(" CASE WHEN ASIGNADAS.D_MUNICIPIO IS NOT NULL THEN ASIGNADAS.D_MUNICIPIO ");
                b.append(" ELSE ACTIVAS.D_MUNICIPIO END AS municipio,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                b.append(" CASE WHEN ASIGNADAS.D_POBLACION IS NOT NULL THEN ASIGNADAS.D_POBLACION ");
                b.append(" ELSE ACTIVAS.D_POBLACION END AS descPoblacion,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                b.append(" CASE WHEN ASIGNADAS.ID_ABN IS NOT NULL THEN ASIGNADAS.ID_ABN ");
                b.append(" ELSE ACTIVAS.ID_ABN END AS idAbn,");
                b.append(" CASE WHEN ASIGNADAS.D_ABN IS NOT NULL THEN ASIGNADAS.D_ABN ");
                b.append(" ELSE ACTIVAS.D_ABN END AS abn,");
            }
            if (filtro.getSegundaAgrupacion() != null) {
                if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    b.append(" CASE WHEN ASIGNADAS.ID_PST IS NOT NULL THEN ASIGNADAS.ID_PST ");
                    b.append(" ELSE ACTIVAS.ID_PST END AS idPst, ");
                    b.append(" CASE WHEN ASIGNADAS.D_PST IS NOT NULL THEN ASIGNADAS.D_PST ");
                    b.append(" ELSE ACTIVAS.D_PST END AS pst ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    b.append(" CASE WHEN ASIGNADAS.D_ESTADO IS NOT NULL THEN ASIGNADAS.D_ESTADO ");
                    b.append(" ELSE ACTIVAS.D_ESTADO END AS estado ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    b.append(" CASE WHEN ASIGNADAS.D_MUNICIPIO IS NOT NULL THEN ASIGNADAS.D_MUNICIPIO ");
                    b.append(" ELSE ACTIVAS.D_MUNICIPIO END AS municipio ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    b.append(" CASE WHEN ASIGNADAS.D_POBLACION IS NOT NULL THEN ASIGNADAS.D_POBLACION ");
                    b.append(" ELSE ACTIVAS.D_POBLACION END AS descPoblacion ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    b.append(" CASE WHEN ASIGNADAS.ID_ABN IS NOT NULL THEN ASIGNADAS.ID_ABN ");
                    b.append(" ELSE ACTIVAS.ID_ABN END AS idAbn, ");
                    b.append(" CASE WHEN ASIGNADAS.D_ABN IS NOT NULL THEN ASIGNADAS.D_ABN ");
                    b.append(" ELSE ACTIVAS.D_ABN END AS abn ");
                }
            } else {
                // No existe un segundo nivel de agrupación por lo que eliminamos la última coma del String formado
                b.setLength(b.length() - 1);
            }
        } else {
            b.append(" CASE WHEN ASIGNADAS.ID_PST IS NOT NULL THEN ASIGNADAS.ID_PST ");
            b.append(" ELSE ACTIVAS.ID_PST END AS idPst, ");
            b.append(" CASE WHEN ASIGNADAS.D_PST IS NOT NULL THEN ASIGNADAS.D_PST ");
            b.append(" ELSE ACTIVAS.D_PST END AS pst, ");
            b.append(" CASE WHEN ASIGNADAS.D_ESTADO IS NOT NULL THEN ASIGNADAS.D_ESTADO ");
            b.append(" ELSE ACTIVAS.D_ESTADO END AS estado, ");
            b.append(" CASE WHEN ASIGNADAS.D_MUNICIPIO IS NOT NULL THEN ASIGNADAS.D_MUNICIPIO ");
            b.append(" ELSE ACTIVAS.D_MUNICIPIO END AS municipio, ");
            b.append(" CASE WHEN ASIGNADAS.D_POBLACION IS NOT NULL THEN ASIGNADAS.D_POBLACION ");
            b.append(" ELSE ACTIVAS.D_POBLACION END AS descPoblacion, ");
            b.append(" CASE WHEN ASIGNADAS.ID_ABN IS NOT NULL THEN ASIGNADAS.ID_ABN ");
            b.append(" ELSE ACTIVAS.ID_ABN END AS idAbn, ");
            b.append(" CASE WHEN ASIGNADAS.D_ABN IS NOT NULL THEN ASIGNADAS.D_ABN ");
            b.append(" ELSE ACTIVAS.D_ABN END AS abn ");
        }
        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }
        return r;
    }

    /**
     * Construye los elementos que constituyen el resultado a obtener (Asignadas).
     * @param filtro de busqueda del informe del reporteador
     * @return cadena jpa
     */
    private String contruirColumnasAsignadas(FiltroReporteadorNG filtro) {
        StringBuilder b = new StringBuilder("");

        if (filtro.getPrimeraAgrupacion() != null) {
            if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                b.append(" NOMBRE_PST as D_PST, ID_PST, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                b.append(" ESTADO as D_ESTADO, ID_ESTADO as ID_ESTADO_ASIGNADAS, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                b.append(" MUNICIPIO as D_MUNICIPIO, ID_MUNICIPIO, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                b.append(" POBLACION as D_POBLACION, INEGI, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                b.append(" ABN as D_ABN, ID_ABN, ");
            }
            if (filtro.getSegundaAgrupacion() != null) {
                if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    b.append(" NOMBRE_PST as D_PST, ID_PST, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    b.append(" ESTADO as D_ESTADO, ID_ESTADO as ID_ESTADO_ASIGNADAS, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    b.append(" MUNICIPIO as D_MUNICIPIO, ID_MUNICIPIO, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    b.append(" POBLACION as D_POBLACION, INEGI, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    b.append(" ABN as D_ABN, ID_ABN, ");
                }
            }
        } else {
            if (null != filtro.getPstSeleccionada() || null != filtro.getEstadoSeleccionado()
                    || null != filtro.getMunicipioSeleccionado() || null != filtro.getPoblacionSeleccionada()
                    || null != filtro.getAbnSeleccionado()) {
                b.append(" NOMBRE_PST as D_PST, ID_PST, ");
                b.append(" ESTADO as D_ESTADO, ID_ESTADO as ID_ESTADO_ASIGNADAS, ");
                b.append(" MUNICIPIO as D_MUNICIPIO, ID_MUNICIPIO, ");
                b.append(" POBLACION as D_POBLACION, INEGI, ");
                b.append(" ABN as D_ABN, ID_ABN, ");
            }
        }
        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }
        return r;
    }

    /**
     * Construye los sumatorios de rangos de solicitudes por tipo de solicitud.
     * @return cadena jpa
     */
    private String construirSumatoriosPorTipoSolicitud() {
        StringBuilder b = new StringBuilder("");

        b.append(" SUM("
                + " CASE WHEN id_tipo_solicitud = 1 THEN fin_rango - inicio_rango + 1 "
                + "      WHEN id_tipo_solicitud = 2 THEN inicio_rango - fin_rango - 1 "
                + "      WHEN id_tipo_solicitud = 3 THEN inicio_rango - fin_rango - 1 "
                + " ELSE 0 END "
                + " ) AS totalAsignadas, ");
        b.append(" SUM("
                + " CASE WHEN id_tipo_red = 'F' AND id_tipo_solicitud = 1 THEN fin_rango - inicio_rango + 1 "
                + "      WHEN id_tipo_red = 'F' AND id_tipo_solicitud = 2 THEN inicio_rango - fin_rango - 1 "
                + "      WHEN id_tipo_red = 'F' AND id_tipo_solicitud = 3 THEN inicio_rango - fin_rango - 1 "
                + " ELSE 0 END) ");
        b.append(" AS asignadasFijas, ");
        b.append(" SUM("
                + " CASE WHEN id_tipo_modalidad = 'CPP' AND id_tipo_solicitud = 1 "
                + "      THEN fin_rango - inicio_rango + 1 "
                + "      WHEN id_tipo_modalidad = 'CPP' AND id_tipo_solicitud = 2 "
                + "      THEN inicio_rango - fin_rango - 1 "
                + "      WHEN id_tipo_modalidad = 'CPP' AND id_tipo_solicitud = 3 "
                + "      THEN inicio_rango - fin_rango - 1 "
                + " ELSE 0 END) ");
        b.append(" AS asignadasMovilesCPP, ");
        b.append(" SUM("
                + " CASE WHEN id_tipo_modalidad = 'MPP' AND id_tipo_solicitud = 1 "
                + "      THEN fin_rango - inicio_rango + 1 "
                + "      WHEN id_tipo_modalidad = 'MPP' AND id_tipo_solicitud = 2 "
                + "      THEN inicio_rango - fin_rango - 1 "
                + "      WHEN id_tipo_modalidad = 'MPP' AND id_tipo_solicitud = 3 "
                + "      THEN inicio_rango - fin_rango - 1 "
                + " ELSE 0 END) ");
        b.append(" as asignadasMovilesMPP ");

        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }
        return r;
    }

    /**
     * Construye las condiciones a partir del filtro (Asignadas).
     * @param filtro de busqueda del informe del reporteador
     * @param tipoPST según sea cedente o cesionario
     * @return cadena jpa
     * @throws ParseException excepción al parsear los datos
     */
    private String contruirFiltroAsignadas(FiltroReporteadorNG filtro, String tipoPST) throws ParseException {
        StringBuilder b = new StringBuilder("");

        if (filtro.getPstSeleccionada() != null) {
            if (b.length() == 0) {
                if (tipoPST.equals(PST_CEDENTE)) {
                    b.append(" ID_PST = " + filtro.getPstSeleccionada().getId());
                } else if (tipoPST.equals(PST_CESIONARIO)) {
                    b.append(" ID_PST_CESIONARIO = " + filtro.getPstSeleccionada().getId());
                }
            } else {
                if (tipoPST.equals(PST_CEDENTE)) {
                    b.append(" AND ID_PST= " + filtro.getPstSeleccionada().getId());
                } else if (tipoPST.equals(PST_CESIONARIO)) {
                    b.append(" AND ID_PST_CESIONARIO = " + filtro.getPstSeleccionada().getId());
                }
            }
        }

        if (filtro.getEstadoSeleccionado() != null) {
            if (b.length() == 0) {
                b.append(" ID_ESTADO = " + filtro.getEstadoSeleccionado().getCodEstado());
            } else {
                b.append(" AND ID_ESTADO = " + filtro.getEstadoSeleccionado().getCodEstado());
            }
        }

        if (filtro.getMunicipioSeleccionado() != null) {
            if (b.length() == 0) {
                b.append(" ID_MUNICIPIO = " + filtro.getMunicipioSeleccionado().getId().getCodMunicipio());
            } else {
                b.append(" AND ID_MUNICIPIO = " + filtro.getMunicipioSeleccionado().getId().getCodMunicipio());
            }
        }

        if (filtro.getPoblacionSeleccionada() != null) {
            if (b.length() == 0) {
                b.append(" INEGI = " + filtro.getPoblacionSeleccionada().getInegi());
            } else {
                b.append(" AND INEGI = " + filtro.getPoblacionSeleccionada().getInegi());
            }
        }

        if (filtro.getAbnSeleccionado() != null) {
            if (b.length() == 0) {
                b.append(" ID_ABN = " + filtro.getAbnSeleccionado());
            } else {
                b.append(" AND ID_ABN = " + filtro.getAbnSeleccionado());
            }
        }

        if (filtro.getFechaFin() != null) {
            String fechaAsignacion = formateador.format(filtro.getFechaFin());
            if (b.length() == 0) {
                b.append(" FECHA_ASIGNACION < TO_DATE('" + fechaAsignacion + "', 'dd/mm/yyyy')");
            } else {
                b.append(" AND FECHA_ASIGNACION < TO_DATE('" + fechaAsignacion + "', 'dd/mm/yyyy')");
            }
        }
        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }
        return r;
    }

    /**
     * Construye las condiciones a partir del filtro (Asignadas).
     * @param filtro de busqueda del informe del reporteador
     * @return cadena jpa
     * @throws ParseException excepción al parsear los datos
     */
    private String contruirFiltroTotalesAsignadas(FiltroReporteadorNG filtro) throws ParseException {
        StringBuilder b = new StringBuilder("");

        if (filtro.getEstadoSeleccionado() != null) {
            if (b.length() == 0) {
                b.append(" ID_ESTADO = " + filtro.getEstadoSeleccionado().getCodEstado());
            } else {
                b.append(" AND ID_ESTADO = " + filtro.getEstadoSeleccionado().getCodEstado());
            }
        }

        if (filtro.getMunicipioSeleccionado() != null) {
            if (b.length() == 0) {
                b.append(" ID_MUNICIPIO = " + filtro.getMunicipioSeleccionado().getId().getCodMunicipio());
            } else {
                b.append(" AND ID_MUNICIPIO = " + filtro.getMunicipioSeleccionado().getId().getCodMunicipio());
            }
        }

        if (filtro.getPoblacionSeleccionada() != null) {
            if (b.length() == 0) {
                b.append(" INEGI = " + filtro.getPoblacionSeleccionada().getInegi());
            } else {
                b.append(" AND INEGI = " + filtro.getPoblacionSeleccionada().getInegi());
            }
        }

        if (filtro.getAbnSeleccionado() != null) {
            if (b.length() == 0) {
                b.append(" ID_ABN = " + filtro.getAbnSeleccionado());
            } else {
                b.append(" AND ID_ABN = " + filtro.getAbnSeleccionado());
            }
        }

        if (filtro.getFechaFin() != null) {
            String fechaAsignacion = formateador.format(filtro.getFechaFin());
            if (b.length() == 0) {
                b.append(" FECHA_ASIGNACION < TO_DATE('" + fechaAsignacion + "', 'dd/mm/yyyy')");
            } else {
                b.append(" AND FECHA_ASIGNACION < TO_DATE('" + fechaAsignacion + "', 'dd/mm/yyyy')");
            }
        }

        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }

        return r;
    }

    /**
     * Construye las condiciones de agrupación de la parte de Asignadas.
     * @param filtro de busqueda del informe del reporteador
     * @param tipoPST según sea cedente o cesionario
     * @return cadena jpa
     */
    private String contruirAgrupacionAsignadas(FiltroReporteadorNG filtro, String tipoPST) {

        StringBuilder b = new StringBuilder("");

        if (filtro.getPrimeraAgrupacion() != null
                && !filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MES)
                && !filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ANYO)) {
            if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                if (tipoPST.equals(PST_CEDENTE)) {
                    b.append(" ID_PST, NOMBRE_PST,");
                } else if (tipoPST.equals(PST_CESIONARIO)) {
                    b.append(" ID_PST_CESIONARIO, NOMBRE_PST,");
                }
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                b.append(" ID_ESTADO, ESTADO,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                b.append(" ID_MUNICIPIO, MUNICIPIO,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                b.append(" INEGI, POBLACION,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                b.append(" ID_ABN, ABN,");
            }
            if (filtro.getSegundaAgrupacion() != null) {
                if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    if (tipoPST.equals(PST_CEDENTE)) {
                        b.append(" ID_PST, NOMBRE_PST ");
                    } else if (tipoPST.equals(PST_CESIONARIO)) {
                        b.append(" ID_PST_CESIONARIO, NOMBRE_PST ");
                    }
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    b.append(" ID_ESTADO, ESTADO ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    b.append(" ID_MUNICIPIO, MUNICIPIO ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    b.append(" INEGI, POBLACION ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    b.append(" ID_ABN, ABN ");
                }
            } else {
                // No existe un segundo nivel de agrupación por lo que eliminamos la última coma del String formado
                b.setLength(b.length() - 1);
            }
        } else {
            // Si la llamada es desde Comparativa por fechas, debemos de obviar la agrupación
            if (filtro.getFechaInicio() == null) {
                if (null != filtro.getPstSeleccionada() || null != filtro.getEstadoSeleccionado()
                        || null != filtro.getMunicipioSeleccionado() || null != filtro.getPoblacionSeleccionada()
                        || null != filtro.getAbnSeleccionado()) {
                    if (tipoPST.equals(PST_CEDENTE)) {
                        b.append(" ID_PST,");
                    } else if (tipoPST.equals(PST_CESIONARIO)) {
                        b.append(" ID_PST_CESIONARIO,");
                    }
                    b.append(" ID_ESTADO, ID_MUNICIPIO, INEGI, ID_ABN,");
                    b.append(" NOMBRE_PST, ABN, ESTADO, MUNICIPIO, POBLACION");
                }
            }
        }
        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("agrupador '{}'", r);
        }
        return r;
    }

    /**
     * Construye los elementos que constituyen el resultado a obtener (Activas).
     * @param filtro de busqueda del informe del reporteador
     * @return cadena jpa
     */
    private String contruirColumnasActivas(FiltroReporteadorNG filtro) {
        StringBuilder b = new StringBuilder("");

        if (filtro.getPrimeraAgrupacion() != null) {
            if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                b.append(" a.PST as ID_PST, PST.NOMBRE as D_PST, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                b.append(" SUBSTR(a.POBLACION, 1, 2) as ID_ESTADO_ACTIVAS, es.NOMBRE as D_ESTADO, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                b.append(" SUBSTR(a.POBLACION, 3, 3) as ID_MUNICIPIO_ACTIVAS, mun.NOMBRE as D_MUNICIPIO, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                b.append(" a.POBLACION, pob.NOMBRE as D_POBLACION, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                b.append(" a.ABN as ID_ABN, abn.NOMBRE as D_ABN, ");
            }
            if (filtro.getSegundaAgrupacion() != null) {
                if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    b.append(" a.PST as ID_PST, PST.NOMBRE AS D_PST, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    b.append(" SUBSTR(a.POBLACION, 1, 2) as ID_ESTADO_ACTIVAS, es.NOMBRE AS D_ESTADO, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    b.append(" SUBSTR(a.POBLACION, 3, 3) as ID_MUNICIPIO_ACTIVAS, mun.NOMBRE as D_MUNICIPIO, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    b.append(" a.POBLACION, pob.NOMBRE as D_POBLACION, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    b.append(" a.ABN as ID_ABN, abn.NOMBRE as D_ABN, ");
                }
            }
        } else {
            if (null != filtro.getPstSeleccionada() || null != filtro.getEstadoSeleccionado()
                    || null != filtro.getMunicipioSeleccionado() || null != filtro.getPoblacionSeleccionada()
                    || null != filtro.getAbnSeleccionado()) {
                b.append(" a.PST as ID_PST, PST.NOMBRE as D_PST, ");
                b.append(" SUBSTR(a.POBLACION, 1, 2) as ID_ESTADO_ACTIVAS, es.NOMBRE as D_ESTADO, ");
                b.append(" SUBSTR(a.POBLACION, 3, 3) as ID_MUNICIPIO_ACTIVAS, mun.NOMBRE as D_MUNICIPIO, ");
                b.append(" a.POBLACION, pob.NOMBRE as D_POBLACION, ");
                b.append(" a.ABN as ID_ABN, abn.NOMBRE as D_ABN, ");
            }
        }

        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }

        return r;
    }

    /**
     * Construye las condiciones a partir del filtro (Activas).
     * @param filtro de busqueda del informe del reporteador
     * @return cadena jpa
     */
    private String contruirFiltroActivas(FiltroReporteadorNG filtro) {
        StringBuilder b = new StringBuilder("");

        b.append(" b.CONSECUTIVO = a.CONSECUTIVO and a.PST = b.PST and a.POBLACION = b.POBLACION ");

        if (filtro.getPstSeleccionada() != null) {
            b.append(" AND a.PST = " + filtro.getPstSeleccionada().getId());
        }

        if (filtro.getEstadoSeleccionado() != null) {
            b.append(" AND SUBSTR(a.POBLACION, 1, 2) = " + filtro.getEstadoSeleccionado().getCodEstado());
        }

        if (filtro.getMunicipioSeleccionado() != null) {
            b.append(" AND SUBSTR(a.POBLACION, 3, 3) = " + filtro.getMunicipioSeleccionado().getId().getCodMunicipio());
        }

        if (filtro.getPoblacionSeleccionada() != null) {
            b.append(" AND a.POBLACION = " + filtro.getPoblacionSeleccionada().getInegi());
        }

        if (filtro.getAbnSeleccionado() != null) {
            b.append(" AND a.ABN = " + filtro.getAbnSeleccionado());
        }

        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }

        return r;
    }

    /**
     * Construye las condiciones de agrupación de la parte de Activas.
     * @param filtro de busqueda del informe del reporteador
     * @return cadena jpa
     */
    private String contruirAgrupacionActivas(FiltroReporteadorNG filtro) {

        StringBuilder b = new StringBuilder("");

        if (filtro.getPrimeraAgrupacion() != null) {
            if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                b.append(" a.PST, pst.NOMBRE,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                b.append(" SUBSTR(a.POBLACION, 1, 2), es.NOMBRE,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                b.append(" SUBSTR(a.POBLACION, 3, 3), mun.NOMBRE,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                b.append(" a.POBLACION, pob.NOMBRE,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                b.append(" a.ABN, abn.NOMBRE,");
            }
            if (filtro.getSegundaAgrupacion() != null) {
                if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    b.append(" a.PST, pst.NOMBRE ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    b.append(" SUBSTR(a.POBLACION, 1, 2), es.NOMBRE ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    b.append(" SUBSTR(a.POBLACION, 3, 3), mun.NOMBRE ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    b.append(" a.POBLACION, pob.NOMBRE ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    b.append(" a.ABN, abn.NOMBRE ");
                }
            } else {
                // No existe un segundo nivel de agrupación por lo que eliminamos la última coma del String formado
                b.setLength(b.length() - 1);
            }
        } else {
            if (null != filtro.getPstSeleccionada() || null != filtro.getEstadoSeleccionado()
                    || null != filtro.getMunicipioSeleccionado() || null != filtro.getPoblacionSeleccionada()
                    || null != filtro.getAbnSeleccionado()) {
                b.append(" a.PST, pst.NOMBRE, SUBSTR(a.POBLACION, 1, 2), es.NOMBRE, SUBSTR(a.POBLACION, 3, 3), ");
                b.append(" mun.NOMBRE, a.POBLACION, pob.NOMBRE, a.ABN, abn.NOMBRE ");
            }
        }
        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("agrupador '{}'", r);
        }
        return r;
    }

    /**
     * Construye los elementos que constituyen el resultado a obtener (Activas).
     * @param filtro de busqueda del informe del reporteador
     * @return cadena jpa
     */
    private String construirInnerJoinTablasCatalago(FiltroReporteadorNG filtro) {
        StringBuilder b = new StringBuilder("");

        if (filtro.getPrimeraAgrupacion() != null) {
            if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                b.append(" INNER JOIN CAT_ESTADO es ");
                b.append(" ON SUBSTR(a.POBLACION, 1, 2) = es.ID_ESTADO ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                b.append(" LEFT JOIN CAT_PST pst ");
                b.append(" ON a.PST = pst.ID_PST ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                b.append(" INNER JOIN CAT_MUNICIPIO mun ");
                b.append(" ON SUBSTR(a.POBLACION, 3, 3) = mun.ID_MUNICIPIO ");
                b.append(" AND SUBSTR(a.POBLACION, 1, 2) = mun.ID_ESTADO ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                b.append(" INNER JOIN CAT_POBLACION pob ");
                b.append(" ON a.POBLACION = pob.ID_INEGI ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                b.append(" INNER JOIN CAT_ABN abn ");
                b.append(" ON a.ABN = abn.ID_ABN ");
            }
            if (filtro.getSegundaAgrupacion() != null) {
                if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    b.append(" INNER JOIN CAT_ESTADO es ");
                    b.append(" ON SUBSTR(a.POBLACION, 1, 2) = es.ID_ESTADO ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    b.append(" LEFT JOIN CAT_PST pst ");
                    b.append(" ON a.PST = pst.ID_PST ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    b.append(" INNER JOIN CAT_MUNICIPIO mun ");
                    b.append(" ON SUBSTR(a.POBLACION, 3, 3) = mun.ID_MUNICIPIO ");
                    b.append(" AND SUBSTR(a.POBLACION, 1, 2) = mun.ID_ESTADO ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    b.append(" INNER JOIN CAT_POBLACION pob ");
                    b.append(" ON a.POBLACION = pob.ID_INEGI ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    b.append(" INNER JOIN CAT_ABN abn ");
                    b.append(" ON a.ABN = abn.ID_ABN ");
                }
            }
        } else {
            if (null != filtro.getPstSeleccionada() || null != filtro.getEstadoSeleccionado()
                    || null != filtro.getMunicipioSeleccionado() || null != filtro.getPoblacionSeleccionada()
                    || null != filtro.getAbnSeleccionado()) {
                b.append(" INNER JOIN CAT_ESTADO es ");
                b.append(" ON SUBSTR(a.POBLACION, 1, 2) = es.ID_ESTADO ");
                b.append(" LEFT JOIN CAT_PST pst ");
                b.append(" ON a.PST = pst.ID_PST ");
                b.append(" INNER JOIN CAT_MUNICIPIO mun ");
                b.append(" ON SUBSTR(a.POBLACION, 3, 3) = mun.ID_MUNICIPIO ");
                b.append(" AND SUBSTR(a.POBLACION, 1, 2) = mun.ID_ESTADO ");
                b.append(" INNER JOIN CAT_POBLACION pob ");
                b.append(" ON a.POBLACION = pob.ID_INEGI ");
                b.append(" INNER JOIN CAT_ABN abn ");
                b.append(" ON a.ABN = abn.ID_ABN ");
            }
        }

        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }
        return r;
    }

    /**
     * Construye las condiciones de agrupación de las vistas generadas.
     * @param filtro de busqueda del informe del reporteador
     * @return cadena jpa
     */
    private String construirRelacionAsignadasActivas(FiltroReporteadorNG filtro) {

        StringBuilder b = new StringBuilder("");

        if (filtro.getPrimeraAgrupacion() != null) {
            if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                b.append(" Asignadas.ID_PST = Activas.ID_PST ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                if (b.length() == 0) {
                    b.append(" LPAD(Asignadas.ID_ESTADO_ASIGNADAS, 2, '0') = Activas.ID_ESTADO_ACTIVAS ");
                } else {
                    b.append(" AND LPAD(Asignadas.ID_ESTADO_ASIGNADAS, 2, '0') = Activas.ID_ESTADO_ACTIVAS ");
                }
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                if (b.length() == 0) {
                    b.append(" LPAD(Asignadas.ID_MUNICIPIO, 3, '0') = Activas.ID_MUNICIPIO_ACTIVAS ");
                } else {
                    b.append(" AND LPAD(Asignadas.ID_MUNICIPIO, 3, '0') = Activas.ID_MUNICIPIO_ACTIVAS ");
                }
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                if (b.length() == 0) {
                    b.append(" Asignadas.INEGI = Activas.POBLACION ");
                } else {
                    b.append(" AND Asignadas.INEGI = Activas.POBLACION ");
                }
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                if (b.length() == 0) {
                    b.append(" Asignadas.ID_ABN = Activas.ID_ABN ");
                } else {
                    b.append(" AND Asignadas.ID_ABN = Activas.ID_ABN ");
                }
            }
            if (filtro.getSegundaAgrupacion() != null) {
                if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    b.append(" AND Asignadas.ID_PST = Activas.ID_PST ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    b.append(" AND LPAD(Asignadas.ID_ESTADO_ASIGNADAS, 2, '0') = Activas.ID_ESTADO_ACTIVAS ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    b.append(" AND LPAD(Asignadas.ID_MUNICIPIO, 3, '0') = Activas.ID_MUNICIPIO_ACTIVAS ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    b.append(" AND Asignadas.INEGI = Activas.POBLACION ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    b.append(" AND Asignadas.ID_ABN = Activas.ID_ABN ");
                }
            }
        } else {
            if (null != filtro.getPstSeleccionada() || null != filtro.getEstadoSeleccionado()
                    || null != filtro.getMunicipioSeleccionado() || null != filtro.getPoblacionSeleccionada()
                    || null != filtro.getAbnSeleccionado()) {
                b.append(" Asignadas.ID_PST = Activas.ID_PST ");
                b.append(" AND LPAD(Asignadas.ID_ESTADO_ASIGNADAS, 2, '0') = Activas.ID_ESTADO_ACTIVAS ");
                b.append(" AND LPAD(Asignadas.ID_MUNICIPIO, 3, '0') = Activas.ID_MUNICIPIO_ACTIVAS ");
                b.append(" AND Asignadas.INEGI = Activas.POBLACION ");
                b.append(" AND Asignadas.ID_ABN = Activas.ID_ABN ");
            }
        }

        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("relacionQueries '{}'", r);
        }
        return r;

    }

    /**
     * Construye las condiciones de ordenación de las vistas generadas.
     * @param filtro de busqueda del informe del reporteador
     * @return cadena jpa
     */
    private String construirOrdenacion(FiltroReporteadorNG filtro) {

        StringBuilder b = new StringBuilder("");

        if (filtro.getPrimeraAgrupacion() != null) {
            if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                b.append("pst,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                b.append("estado,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                b.append("municipio,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                b.append("descPoblacion,");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                b.append("abn,");
            }
            if (filtro.getSegundaAgrupacion() != null) {
                if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    b.append(" pst ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    b.append(" estado ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    b.append(" municipio ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    b.append(" descPoblacion ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    b.append(" abn ");
                }
            } else {
                // No existe un segundo nivel de agrupación por lo que eliminamos la última coma del String formado
                b.setLength(b.length() - 1);
            }
        } else {
            if (null != filtro.getPstSeleccionada() || null != filtro.getEstadoSeleccionado()
                    || null != filtro.getMunicipioSeleccionado() || null != filtro.getPoblacionSeleccionada()
                    || null != filtro.getAbnSeleccionado()) {
                b.append("pst, estado, municipio, descPoblacion, abn");
            }
        }
        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ordenador '{}'", r);
        }
        return r;
    }

    /**
     * Obtención de los datos de la vista con las series de numeraciones cedidas.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<NGReporteador> findHistoricoSeriesCedidas(FiltroReporteadorNG filtro) {

        StringBuffer sbQuery = new StringBuffer("SELECT ROWNUM as lineaConsulta, Cedidas.* from (SELECT ");
        StringBuffer sbFrom = new StringBuffer();
        StringBuffer sbWhere = new StringBuffer();
        StringBuffer sbGroupBy = new StringBuffer();
        StringBuffer sbOrderBy = new StringBuffer();

        List<NGReporteador> lista = new ArrayList<NGReporteador>();

        try {
            // Recuperamos los rangos de solicitudes de cesión
            String sCedidas = contruirColumnasCedidas(filtro);
            if (!sCedidas.isEmpty()) {
                sbQuery.append(sCedidas);
            }
            sbQuery.append(" SUM("
                    + " CASE WHEN id_tipo_solicitud = 2 THEN fin_rango - inicio_rango + 1 "
                    + " ELSE 0 END "
                    + " ) AS totalAsignadas, ");
            sbQuery.append(" SUM("
                    + " CASE WHEN id_tipo_red = 'F' AND id_tipo_solicitud = 2 THEN fin_rango - inicio_rango + 1 "
                    + " ELSE 0 END) ");
            sbQuery.append(" AS asignadasFijas, ");
            sbQuery.append(" SUM("
                    + " CASE WHEN id_tipo_modalidad = 'CPP' AND id_tipo_solicitud = 2 "
                    + "      THEN fin_rango - inicio_rango + 1 "
                    + " ELSE 0 END) ");
            sbQuery.append(" AS asignadasMovilesCPP, ");
            sbQuery.append(" SUM("
                    + " CASE WHEN id_tipo_modalidad = 'MPP' AND id_tipo_solicitud = 2 "
                    + "      THEN fin_rango - inicio_rango + 1 "
                    + " ELSE 0 END) ");
            sbQuery.append(" as asignadasMovilesMPP ");
            sbFrom.append(" FROM HISTORICO_SERIE_VM ");

            String fCedidas = contruirFiltroAsignadas(filtro, PST_CESIONARIO);
            if (!fCedidas.isEmpty()) {
                sbWhere.append(" WHERE ");
                sbWhere.append(fCedidas);
            }

            String gCedidas = contruirAgrupacionAsignadas(filtro, PST_CESIONARIO);
            if (!gCedidas.isEmpty()) {
                sbGroupBy.append(" Group by ");
                sbGroupBy.append(gCedidas);
            }
            sbGroupBy.append(" ) Cedidas ");

            // Juntamos todos los bufferString de la query
            sbQuery.append(sbFrom).append(sbWhere).append(sbGroupBy);

            // Por último, ordenamos la sentencia por los elementos de agrupación
            sbOrderBy.setLength(0);
            String oQuery = construirOrdenacion(filtro);
            if (!oQuery.isEmpty()) {
                sbOrderBy.append(" ORDER BY ");
                sbOrderBy.append(oQuery);
            }

            String q = sbQuery.append(sbOrderBy).toString();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Query: {}", q);
            }

            Query query = getEntityManager().createNativeQuery(q, NGReporteador.class);

            lista = query.getResultList();

            if (LOGGER.isDebugEnabled()) {
                if (lista != null) {
                    LOGGER.debug("#n {}", lista.size());
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return lista;
    }

    /**
     * Construye los elementos que constituyen el resultado a obtener (Asignadas).
     * @param filtro de busqueda del informe del reporteador
     * @return cadena jpa
     */
    private String contruirColumnasCedidas(FiltroReporteadorNG filtro) {
        StringBuilder b = new StringBuilder("");

        if (filtro.getPrimeraAgrupacion() != null) {
            if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                b.append(" NOMBRE_PST as PST, ID_PST_CESIONARIO as idPst, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                b.append(" ESTADO, ID_ESTADO, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                b.append(" MUNICIPIO, ID_MUNICIPIO, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                b.append(" POBLACION as descPoblacion, INEGI, ");
            } else if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                b.append(" ABN, ID_ABN as idAbn, ");
            }
            if (filtro.getSegundaAgrupacion() != null) {
                if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                    b.append(" NOMBRE_PST as PST, ID_PST_CESIONARIO as idPst, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                    b.append(" ESTADO, ID_ESTADO, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                    b.append(" MUNICIPIO, ID_MUNICIPIO, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().
                        equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                    b.append(" POBLACION  as descPoblacion, INEGI, ");
                } else if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                    b.append(" ABN, ID_ABN as idAbn, ");
                }
            }
        } else {
            if (null != filtro.getPstSeleccionada() || null != filtro.getEstadoSeleccionado()
                    || null != filtro.getMunicipioSeleccionado() || null != filtro.getPoblacionSeleccionada()
                    || null != filtro.getAbnSeleccionado()) {
                b.append(" NOMBRE_PST as PST, ID_PST_CESIONARIO as idPst, ");
                b.append(" ESTADO, ID_ESTADO, ");
                b.append(" MUNICIPIO, ID_MUNICIPIO, ");
                b.append(" POBLACION  as descPoblacion, INEGI, ");
                b.append(" ABN, ID_ABN as idAbn, ");
            }
        }

        String r = b.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("filtro '{}'", r);
        }

        return r;
    }

    /**
     * Comprueba que el filtro está vacío de cualquier criterio.
     * @param filtro de busqueda del informe del reporteador
     * @return boolean
     */
    private boolean isFiltroVacio(FiltroReporteadorNG filtro) {
        boolean isVacio = false;
        if (null == filtro.getPrimeraAgrupacion()
                && null == filtro.getSegundaAgrupacion()
                && null == filtro.getPstSeleccionada()
                && null == filtro.getEstadoSeleccionado()
                && null == filtro.getMunicipioSeleccionado()
                && null == filtro.getPoblacionSeleccionada()
                && null == filtro.getAbnSeleccionado()) {
            isVacio = true;
        }
        return isVacio;
    }

    /**
     * Recupera el listado con los tipo de tramites y volumen de datos filtrados por fechas.
     * @param filtro de busqueda del informe del reporteador
     * @return lista de objetos
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> getTotalesTramitesPorFechas(FiltroReporteadorNG filtro) {

        List<Object[]> resultado = new ArrayList<Object[]>();
        String formatoTrunc = "'mm'";
        if (null != filtro.getPrimeraAgrupacion()
                && ElementoAgrupador.COD_ANYO.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())) {
            formatoTrunc = "'yy'";
        }
        try {
            StringBuffer sbquery = new StringBuffer("select ");
            if (null != filtro.getPrimeraAgrupacion() && !"".equals(filtro.getPrimeraAgrupacion().getCodigo())) {
                sbquery.append("TRUNC(FECHA_ASIGNACION, ").append(formatoTrunc).append(") ");
            } else {
                sbquery.append("SYSDATE ");
            }

            sbquery.append(",TIPO_SOLICITUD ");
            sbquery.append(",count(distinct consecutivo), ");
            sbquery.append("  COUNT(*)");
            sbquery.append(",SUM(FIN_RANGO-INICIO_RANGO + 1) ");
            sbquery.append("from historico_serie_VM  ");
            sbquery.append("where FECHA_ASIGNACION >= ?  and FECHA_ASIGNACION < ? ");
            if (filtro.getPstSeleccionada() != null) {
                sbquery.append(" and ID_PST = ?");
            }
            sbquery.append(" GROUP BY");
            if (null != filtro.getPrimeraAgrupacion() && !"".equals(filtro.getPrimeraAgrupacion().getCodigo())) {
                sbquery.append(" TRUNC(FECHA_ASIGNACION, ");
                sbquery.append(formatoTrunc).append(") ,");
            }
            sbquery.append(" TIPO_SOLICITUD ORDER BY ");

            if (null != filtro.getPrimeraAgrupacion() && !"".equals(filtro.getPrimeraAgrupacion().getCodigo())) {
                sbquery.append("TRUNC(FECHA_ASIGNACION, ");
                sbquery.append(formatoTrunc).append(") DESC, ");
            }
            sbquery.append("TIPO_SOLICITUD ASC");
            Query query = getEntityManager().createNativeQuery(sbquery.toString());

            query.setParameter(1, filtro.getFechaInicio());
            query.setParameter(2, filtro.getFechaFin());
            if (filtro.getPstSeleccionada() != null) {
                query.setParameter(3, filtro.getPstSeleccionada().getId());
            }
            resultado = query.getResultList();
        } catch (NoResultException e) {
            LOGGER.debug("");
            return null;
        }
        LOGGER.debug("FIN");
        return resultado;
    }
}
