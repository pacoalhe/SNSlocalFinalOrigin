package mx.ift.sns.negocio.nng;

import java.io.Serializable;
import java.util.List;

import mx.ift.sns.modelo.lineas.ReporteNng;
import mx.ift.sns.negocio.ng.model.RetornoProcesaFichero;

/** RetornoProcesaFicheroNNg. */
public class RetornoProcesaFicheroReportesNng extends RetornoProcesaFichero implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Constructor. */
    public RetornoProcesaFicheroReportesNng() {

    }

    /** Reporte Linea Activa. */
    private ReporteNng reporte;

    /** Detalle Reporte Linea Activa. */
    private List<String> listaDatos;

    /** Proveedor registrado. */
    private boolean proveedorRegistrado;

    /** Nombre proveedor. */
    private String nombreProveedor;

    /**
     * @return the proveedorRegistrado
     */
    public boolean isProveedorRegistrado() {
        return proveedorRegistrado;
    }

    /**
     * @param proveedorRegistrado the proveedorRegistrado to set
     */
    public void setProveedorRegistrado(boolean proveedorRegistrado) {
        this.proveedorRegistrado = proveedorRegistrado;
    }

    /**
     * @return the nombreProveedor
     */
    public String getNombreProveedor() {
        return nombreProveedor;
    }

    /**
     * @param nombreProveedor the nombreProveedor to set
     */
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    /**
     * @return the reporte
     */
    public ReporteNng getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(ReporteNng reporte) {
        this.reporte = reporte;
    }

    /**
     * @return the listaDatos
     */
    public List<String> getListaDatos() {
        return listaDatos;
    }

    /**
     * @param listaDatos the listaDatos to set
     */
    public void setListaDatos(List<String> listaDatos) {
        this.listaDatos = listaDatos;
    }

}
