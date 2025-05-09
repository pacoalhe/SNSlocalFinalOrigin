package mx.ift.sns.modelo.abn;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Vista sobre el catálogo de Áreas Básicas de Numeración.
 */
@Entity
@Table(name = "CATALOGO_ABN_VM")
@Cacheable(false)
@ReadOnly
public class VCatalogoAbn implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID_CATALOGO_ABN", length = 49)
    private String id;

    /** Identificador de ABN. */
    @Column(name = "ID_ABN", length = 3)
    private int idAbn;

    /** Presuscripción. */
    @Column(name = "PRESUSCRIPCION", length = 1)
    private String presuscripcion;

    /** Población Ancla. */
    @Column(name = "POBLACIONANCLA", length = 100)
    private String poblacionAncla;

    /** Fecha Consolidación. */
    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_CONSOLIDACION")
    private Date fechaConsolidacion;

    /** Lista de NIRs que pertencen al ABN. */
    @Column(name = "NIRS", length = 4000)
    private String listaNirsABN;

    /** Identificador del Estado (OT). */
    @Column(name = "ID_ESTADO", length = 2)
    private String idEstado;

    /** Nombre del Estado (OT). */
    @Column(name = "NOMBREESTADO", length = 60)
    private String nombreEstado;

    /** Identificador del Municipio (OT). */
    @Column(name = "ID_MUNICIPIO", length = 3)
    private String idMunicipio;

    /** Nombre del Municipio (OT). */
    @Column(name = "NOMBREMUNICIPIO", length = 80)
    private String nombreMunicipio;

    /** Identificador de la Población (OT). */
    @Column(name = "ID_POBLACION", length = 4)
    private String idPoblacion;

    /** Identificador de la Población (OT). */
    @Column(name = "NOMBREPOBLACION", length = 100)
    private String nombrePoblacion;

    /** Identificador de Estatus. */
    @Column(name = "ID_ESTATUS_ABN", length = 1)
    private String idEstatus;

    /** Descripción de Estatus. */
    @Column(name = "NOMBREESTATUS", length = 100)
    private String descEstatus;

    /** Constructor. */
    public VCatalogoAbn() {
    }

    /** GETTERS & SETTERS */

    /**
     * Identificador.
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Identificador de ABN.
     * @return int
     */
    public int getIdAbn() {
        return idAbn;
    }

    /**
     * Identificador de ABN.
     * @param idAbn int
     */
    public void setIdAbn(int idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * Presuscripción.
     * @return String
     */
    public String getPresuscripcion() {
        return presuscripcion;
    }

    /**
     * Presuscripción.
     * @param presuscripcion String
     */
    public void setPresuscripcion(String presuscripcion) {
        this.presuscripcion = presuscripcion;
    }

    /**
     * Población Ancla.
     * @return String
     */
    public String getPoblacionAncla() {
        return poblacionAncla;
    }

    /**
     * Población Ancla.
     * @param poblacionAncla String
     */
    public void setPoblacionAncla(String poblacionAncla) {
        this.poblacionAncla = poblacionAncla;
    }

    /**
     * Fecha Consolidación.
     * @return Date
     */
    public Date getFechaConsolidacion() {
        return fechaConsolidacion;
    }

    /**
     * Fecha Consolidación.
     * @param fechaConsolidacion Date
     */
    public void setFechaConsolidacion(Date fechaConsolidacion) {
        this.fechaConsolidacion = fechaConsolidacion;
    }

    /**
     * Lista de NIRs que pertencen al ABN.
     * @return String
     */
    public String getListaNirsABN() {
        return listaNirsABN;
    }

    /**
     * Lista de NIRs que pertencen al ABN.
     * @param listaNirsABN String
     */
    public void setListaNirsABN(String listaNirsABN) {
        this.listaNirsABN = listaNirsABN;
    }

    /**
     * Identificador del Estado (OT).
     * @return String
     */
    public String getIdEstado() {
        return idEstado;
    }

    /**
     * Identificador del Estado (OT).
     * @param idEstado String
     */
    public void setIdEstado(String idEstado) {
        this.idEstado = idEstado;
    }

    /**
     * Nombre del Estado (OT).
     * @return String
     */
    public String getNombreEstado() {
        return nombreEstado;
    }

    /**
     * Nombre del Estado (OT).
     * @param nombreEstado String
     */
    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    /**
     * Identificador del Municipio (OT).
     * @return String
     */
    public String getIdMunicipio() {
        return idMunicipio;
    }

    /**
     * Identificador del Municipio (OT).
     * @param idMunicipio String
     */
    public void setIdMunicipio(String idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    /**
     * Nombre del Municipio (OT).
     * @return String
     */
    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

    /**
     * Nombre del Municipio (OT).
     * @param nombreMunicipio String
     */
    public void setNombreMunicipio(String nombreMunicipio) {
        this.nombreMunicipio = nombreMunicipio;
    }

    /**
     * Identificador de la Población (OT).
     * @return String
     */
    public String getIdPoblacion() {
        return idPoblacion;
    }

    /**
     * Identificador de la Población (OT).
     * @param idPoblacion String
     */
    public void setIdPoblacion(String idPoblacion) {
        this.idPoblacion = idPoblacion;
    }

    /**
     * Identificador de la Población (OT).
     * @return the nombrePoblacion
     */
    public String getNombrePoblacion() {
        return nombrePoblacion;
    }

    /**
     * Identificador de la Población (OT).
     * @param nombrePoblacion the nombrePoblacion to set
     */
    public void setNombrePoblacion(String nombrePoblacion) {
        this.nombrePoblacion = nombrePoblacion;
    }

    /**
     * Identificador de Estatus.
     * @return String
     */
    public String getIdEstatus() {
        return idEstatus;
    }

    /**
     * Identificador de Estatus.
     * @param idEstatus String
     */
    public void setIdEstatus(String idEstatus) {
        this.idEstatus = idEstatus;
    }

    /**
     * Descripción de Estatus.
     * @return String
     */
    public String getDescEstatus() {
        return descEstatus;
    }

    /**
     * Descripción de Estatus.
     * @param descEstatus String
     */
    public void setDescEstatus(String descEstatus) {
        this.descEstatus = descEstatus;
    }
}
