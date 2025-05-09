/**
 * .
 */
package mx.ift.sns.modelo.central;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.ReadOnly;

/**
 * Representa la vista de la central.
 */
@Entity
@Table(name = "CATALOGO_CENTRAL_VM")
@Cacheable(false)
@ReadOnly
public class VCatalogoCentral implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @Column(name = "ID_CENTRAL")
    private BigDecimal id;

    /** Nombre central. */
    @Column(name = "NOMBRE")
    private String nombre;

    /** Poblacion. */
    @Column(name = "ID_POBLACION")
    private String inegi;

    /** Municipio. */
    @Column(name = "ID_MUNICIPIO")
    private String codMunicipio;

    /** Estado. */
    @Column(name = "ID_ESTADO")
    private String codEstado;

    /** PST. */
    @Column(name = "ID_PST")
    private String proveedor;

    /** ABN. */
    @Column(name = "ABNS")
    private String abns;

    /** LATITUD. */
    @Column(name = "LATITUD")
    private String latitud;

    /** LONGITUD. */
    @Column(name = "LONGITUD")
    private String longitud;

    /** CAPACIDAD_INICIAL. */
    @Column(name = "CAPACIDAD_INICIAL")
    private String capacidadInicial;

    /** CAPACIDAD_FINAL. */
    @Column(name = "CAPACIDAD_FINAL")
    private String capacidadFinal;

    /** ESTATUS. */
    @Column(name = "ID_ESTATUS")
    private String estatus;

    /** CALLE. */
    @Column(name = "CALLE")
    private String calle;

    /** NUMERO. */
    @Column(name = "NUMERO")
    private String numero;

    /** COLONIA. */
    @Column(name = "COLONIA")
    private String colonia;

    /** CENTRALES RELACION. */
    @Column(name = "RELACION")
    private String relacion;

    /** Nombre del PST. */
    @Column(name = "NOMBRE_PST")
    private String nombrePst;

    /** Descripción estado. */
    @Column(name = "ESTATUS")
    private String descEstatus;

    /**
     * Constructor.
     */
    public VCatalogoCentral() {
    }

    /**
     * Identificador.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Nombre central.
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre central.
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Poblacion.
     * @return the inegi
     */
    public String getInegi() {
        return inegi;
    }

    /**
     * Poblacion.
     * @param inegi the inegi to set
     */
    public void setInegi(String inegi) {
        this.inegi = inegi;
    }

    /**
     * Municipio.
     * @return the codMunicipio
     */
    public String getCodMunicipio() {
        return codMunicipio;
    }

    /**
     * Municipio.
     * @param codMunicipio the codMunicipio to set
     */
    public void setCodMunicipio(String codMunicipio) {
        this.codMunicipio = codMunicipio;
    }

    /**
     * Estado.
     * @return the codEstado
     */
    public String getCodEstado() {
        return codEstado;
    }

    /**
     * Estado.
     * @param codEstado the codEstado to set
     */
    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }

    /**
     * PST.
     * @return the proveedor
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * PST.
     * @param proveedor the proveedor to set
     */
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * ABN.
     * @return the abns
     */
    public String getAbns() {
        return abns;
    }

    /**
     * ABN.
     * @param abns the abns to set
     */
    public void setAbns(String abns) {
        this.abns = abns;
    }

    /**
     * LATITUD.
     * @return the latitud
     */
    public String getLatitud() {
        return latitud;
    }

    /**
     * LATITUD.
     * @param latitud the latitud to set
     */
    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    /**
     * LONGITUD.
     * @return the longitud
     */
    public String getLongitud() {
        return longitud;
    }

    /**
     * LONGITUD.
     * @param longitud the longitud to set
     */
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    /**
     * CAPACIDAD_INICIAL.
     * @return the capacidadInicial
     */
    public String getCapacidadInicial() {
        return capacidadInicial;
    }

    /**
     * CAPACIDAD_INICIAL.
     * @param capacidadInicial the capacidadInicial to set
     */
    public void setCapacidadInicial(String capacidadInicial) {
        this.capacidadInicial = capacidadInicial;
    }

    /**
     * CAPACIDAD_FINAL.
     * @return the capacidadFinal
     */
    public String getCapacidadFinal() {
        return capacidadFinal;
    }

    /**
     * CAPACIDAD_FINAL.
     * @param capacidadFinal the capacidadFinal to set
     */
    public void setCapacidadFinal(String capacidadFinal) {
        this.capacidadFinal = capacidadFinal;
    }

    /**
     * ESTATUS.
     * @return the estatus
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * ESTATUS.
     * @param estatus the estatus to set
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * CALLE.
     * @return the calle
     */
    public String getCalle() {
        return calle;
    }

    /**
     * CALLE.
     * @param calle the calle to set
     */
    public void setCalle(String calle) {
        this.calle = calle;
    }

    /**
     * NUMERO.
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * NUMERO.
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * COLONIA.
     * @return the colonia
     */
    public String getColonia() {
        return colonia;
    }

    /**
     * COLONIA.
     * @param colonia the colonia to set
     */
    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    /**
     * CENTRALES RELACION.
     * @return the relacion
     */
    public String getRelacion() {
        return relacion;
    }

    /**
     * CENTRALES RELACION.
     * @param relacion the relacion to set
     */
    public void setRelacion(String relacion) {
        this.relacion = relacion;
    }

    /**
     * Nombre del PST.
     * @return the nombrePst
     */
    public String getNombrePst() {
        return nombrePst;
    }

    /**
     * Nombre del PST.
     * @param nombrePst the nombrePst to set
     */
    public void setNombrePst(String nombrePst) {
        this.nombrePst = nombrePst;
    }

    /**
     * Descripción estado.
     * @return the descEstatus
     */
    public String getDescEstatus() {
        return descEstatus;
    }

    /**
     * Descripción estado.
     * @param descEstatus the descEstatus to set
     */
    public void setDescEstatus(String descEstatus) {
        this.descEstatus = descEstatus;
    }

}
