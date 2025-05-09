package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import mx.ift.sns.modelo.cpsi.EstatusCPSI;
//import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Clase auxiliar para búsqueda de codigos CPS Internacionales. Contiene los filtros que negocio enviará a los DAOS para
 * que construya las querys. Cada variable asignada se considerará un filtro a satisfacer.
 * @author X50880SA
 */
public class FiltroBusquedaCodigosCPSI implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Constante para el alias de la tabla de proveedores. */
    private static final String FILTRO_ALIAS_TABLA = "cpsi";

    /** Constante para el campo proveedor. */
    private static final String FILTRO_PROVEEDOR = "proveedor";

    /** Constante para el campo estatus. */
    private static final String FILTRO_ESTATUS = "estatus";

    /** Constante para el campo Fecha Cuarentena. */
    private static final String FILTRO_CUARENTENA = "fechaFinCuarentena";

    /** Pst. */
    private Proveedor proveedor;

    /** Estatus. */
    private EstatusCPSI estatusCPSI;

    /** Fin de Fecha de Cuarentena. */
    private Date fechaCuarentenaHasta;

    /** Constructor por defecto. */
    public FiltroBusquedaCodigosCPSI() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getFiltrosCodigosCPSI() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_PROVEEDOR, FILTRO_ALIAS_TABLA);
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (estatusCPSI != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_ESTATUS, FILTRO_ALIAS_TABLA);
            filtro.setValor(estatusCPSI, EstatusCPSI.class);
            filtros.add(filtro);
        }

        if (fechaCuarentenaHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_CUARENTENA, FILTRO_ALIAS_TABLA);
            filtro.setFechaHasta(true);
            filtro.setValor(fechaCuarentenaHasta, Date.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        proveedor = null;
        estatusCPSI = null;
        fechaCuarentenaHasta = null;
    }

    /**
     * Pst.
     * @return Proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Pst.
     * @param proveedor Proveedor
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Estatus.
     * @return EstatusCPSI
     */
    public EstatusCPSI getEstatusCPSI() {
        return estatusCPSI;
    }

    /**
     * Estatus.
     * @param estatusCPSI EstatusCPSI
     */
    public void setEstatusCPSI(EstatusCPSI estatusCPSI) {
        this.estatusCPSI = estatusCPSI;
    }

    /**
     * Fin de Fecha de Cuarentena.
     * @return the fechaCuarentenaHasta
     */
    public Date getFechaCuarentenaHasta() {
        return fechaCuarentenaHasta;
    }

    /**
     * Fin de Fecha de Cuarentena.
     * @param fechaCuarentenaHasta the fechaCuarentenaHasta to set
     */
    public void setFechaCuarentenaHasta(Date fechaCuarentenaHasta) {
        this.fechaCuarentenaHasta = fechaCuarentenaHasta;
    }

}
