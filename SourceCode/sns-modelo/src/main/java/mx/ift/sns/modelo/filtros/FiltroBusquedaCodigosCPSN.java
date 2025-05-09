package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Clase auxiliar para búsqueda de codigos CPS Nacionales. Contiene los filtros que negocio enviará a los DAOS para que
 * construya las querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaCodigosCPSN implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Constante para el alias de la tabla de proveedores. */
    private static final String FILTRO_ALIAS_TABLA = "cpsn";
    /** Constante para el campo proveedor. */
    private static final String FILTRO_PROVEEDOR = "proveedor";
    /** Constante para el campo tipo bloque. */
    private static final String FILTRO_TIPO_BLOQUE = "tipoBloqueCPSN";
    /** Constante para el campo estatus. */
    private static final String FILTRO_ESTATUS = "estatusCPSN";
    /** Constante para el campo Fecha Cuarentena. */
    private static final String FILTRO_CUARENTENA = "fechaCuarentena";

    /** Pst. */
    private Proveedor proveedor;

    /** Tipo Bloque CPSN. */
    private TipoBloqueCPSN tipoBloqueCPSN;

    /** Estatus. */
    private EstatusCPSN estatusCPSN;

    /** Fin de Fecha de Cuarentena. */
    private Date fechaCuarentenaHasta;

    /** Constructor por defecto. */
    public FiltroBusquedaCodigosCPSN() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getFiltrosCodigosCPSN() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_PROVEEDOR, FILTRO_ALIAS_TABLA);
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (tipoBloqueCPSN != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_TIPO_BLOQUE, FILTRO_ALIAS_TABLA);
            filtro.setValor(tipoBloqueCPSN, TipoBloqueCPSN.class);
            filtros.add(filtro);
        }

        if (estatusCPSN != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_ESTATUS, FILTRO_ALIAS_TABLA);
            filtro.setValor(estatusCPSN, EstatusCPSN.class);
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
        tipoBloqueCPSN = null;
        estatusCPSN = null;
        fechaCuarentenaHasta = null;
    }

    /**
     * Pst.
     * @return the proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Pst.
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Tipo Bloque CPSN.
     * @return the tipoBloqueCPSN
     */
    public TipoBloqueCPSN getTipoBloqueCPSN() {
        return tipoBloqueCPSN;
    }

    /**
     * Tipo Bloque CPSN.
     * @param tipoBloqueCPSN the tipoBloqueCPSN to set
     */
    public void setTipoBloqueCPSN(TipoBloqueCPSN tipoBloqueCPSN) {
        this.tipoBloqueCPSN = tipoBloqueCPSN;
    }

    /**
     * Estatus.
     * @return the estatusCPSN
     */
    public EstatusCPSN getEstatusCPSN() {
        return estatusCPSN;
    }

    /**
     * Estatus.
     * @param estatusCPSN the estatusCPSN to set
     */
    public void setEstatusCPSN(EstatusCPSN estatusCPSN) {
        this.estatusCPSN = estatusCPSN;
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
