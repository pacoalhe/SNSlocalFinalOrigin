package mx.ift.sns.web.backend.ng.lineasactivas.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.lineas.DetLineaActivaDet;
import mx.ift.sns.modelo.lineas.DetLineaArrendada;
import mx.ift.sns.modelo.lineas.DetLineaArrendatario;
import mx.ift.sns.modelo.lineas.DetalleLineaActiva;
import mx.ift.sns.modelo.lineas.DetalleReporte;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.utils.number.NumerosUtils;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Model del resumen de la carga del reporte de lineas activas.
 * @author X36155QU
 */
public class ResumenReporteLineasActivasModel extends LazyDataModel<ResumenReporteLineasActivas> {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResumenReporteLineasActivasModel.class);

    /** Colección de objetos. */
    private List<ResumenReporteLineasActivas> dataSource;

    /** Id del Reporte. */
    private BigDecimal idReporte;

    /** Tipo de Reporte. */
    private TipoReporte tipoReporte;

    /** Proveedor que reporta. */
    private Proveedor proveedor;

    /** Detalle Reporte Linea Activa. */
    private List<String> listaDatos;

    /** Servicio. */
    private INumeracionGeograficaService service;

    /** Construuctor. */
    public ResumenReporteLineasActivasModel() {
        dataSource = new ArrayList<ResumenReporteLineasActivas>(1);
    }

    @Override
    public ResumenReporteLineasActivas getRowData(String rowKey) {
        if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ACTIVAS)) {
            for (ResumenReporteLineasActivas dato : dataSource) {
                if (dato.getDetLineaActiva().getPoblacion().getInegi().equals(rowKey)) {
                    return dato;
                }
            }
        } else if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ACTIVAS_DET)) {
            for (ResumenReporteLineasActivas dato : dataSource) {
                if (String.valueOf(dato.getDetLineaActivaDet().getId()).equals(rowKey)) {
                    return dato;
                }
            }
        } else if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ACTIVAS_ARRENDATARIO)) {
            for (ResumenReporteLineasActivas dato : dataSource) {
                if (String.valueOf(dato.getDetLineaArrendatario().getId()).equals(rowKey)) {
                    return dato;
                }
            }
        } else if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ARRENDADAS)) {
            for (ResumenReporteLineasActivas dato : dataSource) {
                if (String.valueOf(dato.getDetLineaArrendada().getId()).equals(rowKey)) {
                    return dato;
                }
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(ResumenReporteLineasActivas dato) {
        if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ACTIVAS)) {
            return dato.getDetLineaActiva().getPoblacion().getInegi();
        } else if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ACTIVAS_DET)) {
            return String.valueOf(dato.getDetLineaActivaDet().getId());
        } else if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ACTIVAS_ARRENDATARIO)) {
            return String.valueOf(dato.getDetLineaArrendatario().getId());
        } else if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ARRENDADAS)) {
            return String.valueOf(dato.getDetLineaArrendada().getId());
        }

        return null;

    }

    @Override
    public List<ResumenReporteLineasActivas> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {
        dataSource.clear();
        this.setRowCount(listaDatos.size());

        if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ACTIVAS)) {
            cargaLineasActivas(first, pageSize);
        } else if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ACTIVAS_DET)) {
            cargaLineasActivasDet(first, pageSize);
        } else if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ACTIVAS_ARRENDATARIO)) {
            cargaLineasArrendatario(first, pageSize);
        } else if (tipoReporte.getCodigo().equals(TipoReporte.LINEAS_ARRENDADAS)) {
            cargaLineasArrendada(first, pageSize);
        }

        return dataSource;

    }

    /**
     * Carga el model para lineas arrendadas.
     * @param first posicion pagina
     * @param pageSize tamaño pagina
     */
    private void cargaLineasArrendada(int first, int pageSize) {

        List<DetLineaArrendada> listaDetalle = service.findAllDetLineaArrendadaByReporte(idReporte, first, pageSize);
        for (DetLineaArrendada detalle : listaDetalle) {
            ResumenReporteLineasActivas dato = new ResumenReporteLineasActivas();
            dato.setDetLineaArrendada(detalle);
            dataSource.add(dato);
        }

    }

    /**
     * Carga el model para lineas arrendatario.
     * @param first posicion pagina
     * @param pageSize tamaño pagina
     */
    private void cargaLineasArrendatario(int first, int pageSize) {

        try {
            List<DetLineaArrendatario> listaDetalle = service.findAllDetLineaArrendatarioByReporte(idReporte, first,
                    pageSize);
            for (DetLineaArrendatario detalle : listaDetalle) {
                ResumenReporteLineasActivas dato = new ResumenReporteLineasActivas();
                dato.setDetLineaArrendatario(detalle);
                dato.setPorcentajeTotal(NumerosUtils.calcularPorcentaje(detalle.getTotalLineasActivas().intValue(),
                        detalle.getNumFinalAsInt() - detalle.getNumInicialAsInt() + 1));
                dataSource.add(dato);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Carga el model para lineas activas detalladas.
     * @param first posicion pagina
     * @param pageSize tamaño pagina
     */
    private void cargaLineasActivasDet(int first, int pageSize) {

        List<DetLineaActivaDet> listaDetalle = service.findAllDetLineaActivaDetByReporte(idReporte, first, pageSize);

        for (DetLineaActivaDet detalle : listaDetalle) {
            ResumenReporteLineasActivas dato = new ResumenReporteLineasActivas();

            dato.setDetLineaActivaDet(detalle);
            Poblacion pob = detalle.getPoblacion();

            Integer totalAsignadoFija = service.getTotalNumRangosAsignadosByPoblacion("F", "", proveedor, pob)
                    .intValue();
            dato.setTotalAsignadoFijo(totalAsignadoFija);
            Integer totalActivoFijo = detalle.getTotalNumServicioFijo()
                    .intValue()
                    + detalle.getTotalNumCuarentenaFijo().intValue()
                    + detalle.getTotalNumPortadosFijo().intValue()
                    + detalle.getTotalNumUsoInternoFijo().intValue()
                    + detalle.getTotalNumTelPubFijo().intValue();
            dato.setPorcentajeUtilizacionFijo(NumerosUtils.calcularPorcentaje(totalActivoFijo, totalAsignadoFija));
            dato.setTotalAsignadoFijo(totalAsignadoFija);

            Integer totalAsignadoCpp = service.getTotalNumRangosAsignadosByPoblacion("M", "CPP", proveedor, pob)
                    .intValue();
            dato.setTotalAsignadoCpp(totalAsignadoCpp);
            Integer totalActivoCpp = detalle.getTotalNumServicioCpp()
                    .intValue()
                    + detalle.getTotalNumCuarentenaCpp().intValue()
                    + detalle.getTotalNumPortadosCpp().intValue()
                    + detalle.getTotalNumUsoInternoCpp().intValue()
                    + detalle.getTotalNumTelPubCpp().intValue();
            dato.setPorcentajeUtilizacionCpp(NumerosUtils.calcularPorcentaje(totalActivoCpp, totalAsignadoCpp));

            Integer totalAsignadoMpp = service.getTotalNumRangosAsignadosByPoblacion("M", "MPP", proveedor, pob)
                    .intValue();
            dato.setTotalAsignadoMpp(totalAsignadoMpp);
            Integer totalActivoMpp = detalle.getTotalNumServicioMpp()
                    .intValue()
                    + detalle.getTotalNumCuarentenaMpp().intValue()
                    + detalle.getTotalNumPortadosMpp().intValue()
                    + detalle.getTotalNumUsoInternoMpp().intValue()
                    + detalle.getTotalNumTelPubMpp().intValue();
            dato.setPorcentajeUtilizacionMpp(NumerosUtils.calcularPorcentaje(totalActivoMpp, totalAsignadoMpp));

            dato.setTotalAsignadoMovil(totalAsignadoMpp + totalAsignadoCpp);
            dato.setPorcentajeUtilizacionMovil(NumerosUtils.calcularPorcentaje(totalActivoMpp + totalActivoCpp,
                    totalAsignadoMpp + totalAsignadoCpp));

            dato.setPorcentajeTotal(NumerosUtils.calcularPorcentaje(totalActivoFijo + totalActivoMpp + totalActivoCpp,
                    totalAsignadoFija + totalAsignadoMpp + totalAsignadoCpp));

            dataSource.add(dato);
        }

    }

    /** Resetea los valores de búsqueda. */
    public void clear() {
        this.idReporte = null;
        this.proveedor = null;
        this.tipoReporte = null;
    }

    /**
     * Carga el model para lineas activas.
     * @param first posicion pagina
     * @param pageSize tamaño pagina
     */
    private void cargaLineasActivas(int first, int pageSize) {

        FiltroBusquedaLineasActivas filtros = new FiltroBusquedaLineasActivas();

        filtros.setConsecutivo(idReporte.toString());
        filtros.setNumeroPagina(first);
        filtros.setResultadosPagina(pageSize);
        filtros.setUsarPaginacion(true);
        filtros.setHistorico(true);

        this.setRowCount(service.findAllDetalleReporteCount(filtros));
        List<DetalleReporte> listaDetallesReporte = service.findAllDetalleReporte(filtros);

        for (DetalleReporte detalleReporte : listaDetallesReporte) {

            ResumenReporteLineasActivas dato = new ResumenReporteLineasActivas();

            DetalleLineaActiva detalle = new DetalleLineaActiva();

            detalle.setPoblacion(detalleReporte.getPoblacion());
            detalle.setTotalNumerosAsignados(detalleReporte.getTotalAsignadas());
            detalle.setTotalLineasActivasFijas(detalleReporte.getTotalLineasActivasFijas());
            detalle.setTotalLineasActivasCpp(detalleReporte.getTotalLineasActivasCpp());
            detalle.setTotalLineasActivasMpp(detalleReporte.getTotalLineasActivasMpp());
            detalle.setTotalLineasActivas(detalleReporte.getTotalLineasActivas());
            dato.setDetLineaActiva(detalle);

            Integer totalAsignadoFija = detalleReporte.getTotalAsignadasFijas().intValue();
            dato.setTotalAsignadoFijo(totalAsignadoFija);
            dato.setPorcentajeUtilizacionFijo(NumerosUtils.calcularPorcentaje(detalle.getTotalLineasActivasFijas()
                    .intValue(),
                    totalAsignadoFija));
            Integer totalAsignadoCpp = detalleReporte.getTotalAsignadasCpp().intValue();
            dato.setTotalAsignadoCpp(totalAsignadoCpp);
            dato.setPorcentajeUtilizacionCpp(NumerosUtils.calcularPorcentaje(detalle.getTotalLineasActivasCpp()
                    .intValue(),
                    totalAsignadoCpp));
            Integer totalAsignadoMpp = detalleReporte.getTotalAsignadasMpp().intValue();
            dato.setTotalAsignadoMpp(totalAsignadoMpp);
            dato.setPorcentajeUtilizacionMpp(NumerosUtils.calcularPorcentaje(detalle.getTotalLineasActivasMpp()
                    .intValue(),
                    totalAsignadoMpp));
            dato.setTotalAsignadoMovil(totalAsignadoMpp + totalAsignadoCpp);
            dato.setPorcentajeUtilizacionMovil(NumerosUtils.calcularPorcentaje(detalle.getTotalLineasActivasCpp()
                    .intValue()
                    + detalle.getTotalLineasActivasMpp().intValue(), totalAsignadoMpp + totalAsignadoCpp));
            dataSource.add(dato);

        }

    }

    /**
     * Colección de objetos.
     * @return dataSource
     */
    public List<ResumenReporteLineasActivas> getDataSource() {
        return dataSource;
    }

    /**
     * Colección de objetos.
     * @param dataSource dataSource to set
     */
    public void setDataSource(List<ResumenReporteLineasActivas> dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Id del Reporte.
     * @return idReporte
     */
    public BigDecimal getIdReporte() {
        return idReporte;
    }

    /**
     * Id del Reporte.
     * @param idReporte idReporte to set
     */
    public void setIdReporte(BigDecimal idReporte) {
        this.idReporte = idReporte;
    }

    /**
     * Servicio.
     * @return service
     */
    public INumeracionGeograficaService getService() {
        return service;
    }

    /**
     * Servicio.
     * @param service service to set
     */
    public void setService(INumeracionGeograficaService service) {
        this.service = service;
    }

    /**
     * Tipo de Reporte.
     * @return tipoReporte
     */
    public TipoReporte getTipoReporte() {
        return tipoReporte;
    }

    /**
     * Tipo de Reporte.
     * @param tipoReporte tipoReporte to set
     */
    public void setTipoReporte(TipoReporte tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    /**
     * Proveedor que reporta.
     * @return proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor que reporta.
     * @param proveedor proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Detalle Reporte Linea Activa.
     * @return listaDatos
     */
    public List<String> getListaDatos() {
        return listaDatos;
    }

    /**
     * Detalle Reporte Linea Activa.
     * @param listaDatos listaDatos to set
     */
    public void setListaDatos(List<String> listaDatos) {
        this.listaDatos = listaDatos;
    }

}
