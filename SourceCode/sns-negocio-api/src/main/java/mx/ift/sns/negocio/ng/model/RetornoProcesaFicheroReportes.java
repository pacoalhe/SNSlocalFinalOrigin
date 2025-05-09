package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;
import java.util.List;

import mx.ift.sns.modelo.lineas.Reporte;

/** RetornoProcesaFichero. */
public class RetornoProcesaFicheroReportes extends RetornoProcesaFichero implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Constructor. */
    public RetornoProcesaFicheroReportes() {

    }

    /** Reporte Linea Activa. */
    private Reporte reporte;

    /** Detalle Reporte Linea Activa. */
    private List<String> listaDatos;

    /** Proveedor registrado. */
    private boolean proveedorRegistrado;

    /** Nombre proveedor. */
    private String nombreProveedor;

    /**
     * @return the reporte
     */
    public Reporte getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

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
