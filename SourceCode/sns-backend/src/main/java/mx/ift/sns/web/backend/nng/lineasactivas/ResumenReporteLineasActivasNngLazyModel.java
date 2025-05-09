package mx.ift.sns.web.backend.nng.lineasactivas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.ift.sns.modelo.filtros.FiltroBusquedaLineasActivas;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendadorNng;
import mx.ift.sns.modelo.lineas.DetalleLineaArrendatarioNng;
import mx.ift.sns.modelo.lineas.DetalleReporteNng;
import mx.ift.sns.modelo.lineas.TipoReporte;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modelo lazy para los resumenes del resultado de la carga de lineas activas NNG.
 * @author X36155QU
 */
public class ResumenReporteLineasActivasNngLazyModel extends LazyDataModel<ResumenReporteLineasActivasNng> {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResumenReporteLineasActivasNngLazyModel.class);

    /** Colección de objetos. */
    private List<ResumenReporteLineasActivasNng> dataSource;

    /** Id del Reporte. */
    private BigDecimal idReporte;

    /** Tipo de Reporte. */
    private TipoReporte tipoReporte;

    /** Proveedor que reporta. */
    private Proveedor proveedor;

    /** Numero de Detalles en Reporte Linea Activa. */
    private int numDatos;

    /** Fachada. */
    private INumeracionNoGeograficaFacade facade;

    /** Construuctor. */
    public ResumenReporteLineasActivasNngLazyModel() {
        dataSource = new ArrayList<ResumenReporteLineasActivasNng>(1);
    }

    @Override
    public ResumenReporteLineasActivasNng getRowData(String rowKey) {

        for (ResumenReporteLineasActivasNng dato : dataSource) {
            if (dato.getId().toString().equals(rowKey)) {
                return dato;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(ResumenReporteLineasActivasNng dato) {

        return dato.getId().toString();

    }

    @Override
    public List<ResumenReporteLineasActivasNng> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {
        dataSource.clear();

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
        this.setRowCount(numDatos);
        List<DetalleLineaArrendadorNng> listaDetalle = facade.findAllDetLineaArrendadaByReporte(idReporte, first,
                pageSize);
        for (DetalleLineaArrendadorNng detalle : listaDetalle) {
            ResumenReporteLineasActivasNng dato = new ResumenReporteLineasActivasNng();
            dato.setAbc(detalle.getAbc());
            dato.setArrendatario(detalle.getArrendatario());
            dato.setClaveServicio(detalle.getClaveServicio());
            dato.setId(detalle.getId());
            dato.setNumeracionRentada(Integer.parseInt(detalle.getNumFinal())
                    - Integer.parseInt(detalle.getNumInicial()) + 1);
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
            this.setRowCount(numDatos);
            List<DetalleLineaArrendatarioNng> listaDetalle = facade.findAllDetLineaArrendatarioByReporte(idReporte,
                    first, pageSize);
            for (DetalleLineaArrendatarioNng detalle : listaDetalle) {
                ResumenReporteLineasActivasNng dato = new ResumenReporteLineasActivasNng();
                dato.setAbc(detalle.getAbc());
                dato.setArrendador(detalle.getArrendador());
                dato.setClaveServicio(detalle.getClaveServicio());
                dato.setId(detalle.getId());
                dato.setNumeracionRentada(Integer.parseInt(detalle.getNumFinal())
                        - Integer.parseInt(detalle.getNumInicial()) + 1);
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

        FiltroBusquedaLineasActivas filtro = new FiltroBusquedaLineasActivas();
        filtro.setConsecutivo(this.idReporte.toString());
        filtro.setHistorico(true);
        this.setRowCount(facade.findAllDetalleReporteCount(filtro));

        List<DetalleReporteNng> listaDetalles = facade.findAllDetalleReporte(filtro);
        for (DetalleReporteNng detalle : listaDetalles) {
            ResumenReporteLineasActivasNng dato = new ResumenReporteLineasActivasNng();
            dato.setClaveServicio(detalle.getClaveServicio());
            dato.setId(new BigDecimal(detalle.getId()));
            dato.setNumeracionAsignadaSerie(facade.getNumeracionAsignadaSerie(detalle.getClaveServicio(),
                    detalle.getProveedor()));
            dato.setNumeracionAsignadaRango(facade.getNumeracionAsignadaRango(detalle.getClaveServicio(),
                    detalle.getProveedor()));
            dato.setNumeracionAsignadaEspecifica(facade.getNumeracionAsignadaEspecifica(detalle.getClaveServicio(),
                    detalle.getProveedor()));
            dato.setNumeracionActivaSerie(facade.getNumeracionActivaDetSerie(detalle.getClaveServicio(),
                    detalle.getProveedor(), idReporte));
            dato.setNumeracionActivaRango(facade.getNumeracionActivaDetRango(detalle.getClaveServicio(),
                    detalle.getProveedor(), idReporte));
            dato.setNumeracionActivaEspecifica(facade.getNumeracionActivaDetEspecifica(detalle.getClaveServicio(),
                    detalle.getProveedor(), idReporte));
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

        FiltroBusquedaLineasActivas filtro = new FiltroBusquedaLineasActivas();
        filtro.setConsecutivo(this.idReporte.toString());
        filtro.setHistorico(true);
        this.setRowCount(facade.findAllDetalleReporteCount(filtro));

        List<DetalleReporteNng> listaDetalles = facade.findAllDetalleReporte(filtro);
        for (DetalleReporteNng detalle : listaDetalles) {
            ResumenReporteLineasActivasNng dato = new ResumenReporteLineasActivasNng();
            dato.setClaveServicio(detalle.getClaveServicio());
            dato.setId(new BigDecimal(detalle.getId()));
            dato.setNumeracionAsignadaSerie(facade.getNumeracionAsignadaSerie(detalle.getClaveServicio(),
                    detalle.getProveedor()));
            dato.setNumeracionAsignadaRango(facade.getNumeracionAsignadaRango(detalle.getClaveServicio(),
                    detalle.getProveedor()));
            dato.setNumeracionAsignadaEspecifica(facade.getNumeracionAsignadaEspecifica(detalle.getClaveServicio(),
                    detalle.getProveedor()));
            dato.setNumeracionActivaSerie(facade.getNumeracionActivaSerie(detalle.getClaveServicio(),
                    detalle.getProveedor(), idReporte));
            dato.setNumeracionActivaRango(facade.getNumeracionActivaRango(detalle.getClaveServicio(),
                    detalle.getProveedor(), idReporte));
            dato.setNumeracionActivaEspecifica(facade.getNumeracionActivaEspecifica(detalle.getClaveServicio(),
                    detalle.getProveedor(), idReporte));
            dataSource.add(dato);
        }

    }

    /**
     * Colección de objetos.
     * @return dataSource
     */
    public List<ResumenReporteLineasActivasNng> getDataSource() {
        return dataSource;
    }

    /**
     * Colección de objetos.
     * @param dataSource dataSource to set
     */
    public void setDataSource(List<ResumenReporteLineasActivasNng> dataSource) {
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
     * Fachada.
     * @return facade
     */
    public INumeracionNoGeograficaFacade getFacade() {
        return facade;
    }

    /**
     * Fachada.
     * @param facade facade to set
     */
    public void setFacade(INumeracionNoGeograficaFacade facade) {
        this.facade = facade;
    }

    /**
     * Numero de Detalles en Reporte Linea Activa.
     * @return numDatos
     */
    public int getNumDatos() {
        return numDatos;
    }

    /**
     * Numero de Detalles en Reporte Linea Activa.
     * @param numDatos numDatos to set
     */
    public void setNumDatos(int numDatos) {
        this.numDatos = numDatos;
    }

}
