package mx.ift.sns.modelo.ng;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;

/**
 * Representa una redistribución de numeración ejecutada. Contiene la información del trámite que la generó.
 */
@Entity
@Table(name = "NG_NUM_REDISTRIBUIDA")
@SequenceGenerator(name = "SEQ_ID_NUMS_REDIS", sequenceName = "SEQ_ID_NUMS_REDIS", allocationSize = 1)
@NamedQuery(name = "NumeracionRedistribuida.findAll", query = "SELECT n FROM NumeracionRedistribuida n")
public class NumeracionRedistribuida implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NUMS_REDIS")
    @Column(name = "ID_NUM_REDISTRIBUIDA", unique = true, nullable = false)
    private BigDecimal id;

    /** Relación: Muchas Numeraciones redistribuidas pueden pertenecer a la misma Redistribución solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_REDIST", nullable = false)
    private RedistribucionSolicitadaNg redistribucionSolicitada;

    /** Relación: Muchas Numeraciones redistribuidas pueden pertenecer a la misma Solicitud de Redistribución. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private SolicitudRedistribucionNg solicitudRedistribucion;

    /** Final del rango de numeración. */
    @Column(name = "FIN_RANGO", nullable = false, length = 4)
    private String finRango;

    /** Inicio del rango de numeración. */
    @Column(name = "INICIO_RANGO", nullable = false, length = 4)
    private String inicioRango;

    /** Relación: Muchas Numeraciones Redistribuidas pueden ser de un mismo tipo de central. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_ORIGEN", nullable = false)
    private Central centralOrigen;

    /** Relación: Muchas Numeraciones Redistribuidas pueden ser de un mismo tipo de central. */
    @ManyToOne
    @JoinColumn(name = "ID_CENTRAL_DESTINO", nullable = false)
    private Central centralDestino;

    /** Relación: Muchas Numeraciones Redistribuidas pueden tener el mismo proveedor concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONCESIONARIO", nullable = true)
    private Proveedor concesionario;

    /** Relación: Muchas Numeraciones Redistribuidas pueden tener el mismo proveedor concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO", nullable = true)
    private Proveedor arrendatario;

    /** Relación: Muchas Numeraciones Redistribuidas pueden tener la misma población. */
    @ManyToOne
    @JoinColumn(name = "ID_INEGI", nullable = false)
    private Poblacion poblacion;

    /** Relación: Muchas Numeraciones Redistribuidas pueden tener el mismo tipo de modalidad de red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_MODALIDAD", nullable = true)
    private TipoModalidad tipoModalidad;

    /** Relación: Muchas Numeraciones Redistribuidas pueden tener el mismo tipo de red. */
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_RED", nullable = false)
    private TipoRed tipoRed;

    /** Constructor. **/
    public NumeracionRedistribuida() {
    }

    /**
     * Final del rango de numeración.
     * @return String
     */
    public String getFinRango() {
        return this.finRango;
    }

    /**
     * Final del rango de numeración.
     * @param finRango String
     */
    public void setFinRango(String finRango) {
        this.finRango = finRango;
    }

    /**
     * Inicio del rango de numeración.
     * @return String
     */
    public String getInicioRango() {
        return this.inicioRango;
    }

    /**
     * Inicio del rango de numeración.
     * @param inicioRango String
     */
    public void setInicioRango(String inicioRango) {
        this.inicioRango = inicioRango;
    }

    /**
     * Devuelve el inicio de rango como un valor int.
     * @return int Inicio de Rango
     * @throws Exception si no se puede parsear el número.
     */
    public int getNumInicioAsInt() throws Exception {
        return (Integer.valueOf(inicioRango).intValue());
    }

    /**
     * Devuelve el final de rango como un valor int.
     * @return int Final de Rango
     * @throws Exception si no se puede parsear el número.
     */
    public int getNumFinalAsInt() throws Exception {
        return (Integer.valueOf(finRango).intValue());
    }

    /**
     * Identificador.
     * @return BigDecimal
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Identificador.
     * @param id BigDecimal
     **/
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Asociación de la redistribución con la solicitud que la originó.
     * @return RedistribucionSolicitadaNg RedistribucionSolicitadaNg
     */
    public RedistribucionSolicitadaNg getRedistribucionSolicitada() {
        return redistribucionSolicitada;
    }

    /**
     * Asociación de la redistribución con la solicitud que la originó.
     * @param redistribucionSolicitada RedistribucionSolicitadaNg
     */
    public void setRedistribucionSolicitada(RedistribucionSolicitadaNg redistribucionSolicitada) {
        this.redistribucionSolicitada = redistribucionSolicitada;
    }

    /**
     * Solicitud de redistribución asociada a la Numeración Redistribuida.
     * @return SolicitudRedistribucionNg
     */
    public SolicitudRedistribucionNg getSolicitudRedistribucion() {
        return solicitudRedistribucion;
    }

    /**
     * Solicitud de redistribución asociada a la Numeración Redistribuida.
     * @param solicitudRedistribucion SolicitudRedistribucionNg
     */
    public void setSolicitudRedistribucion(SolicitudRedistribucionNg solicitudRedistribucion) {
        this.solicitudRedistribucion = solicitudRedistribucion;
    }

    /**
     * Central destino de la población asociada a la numeración redistribuida.
     * @return Central
     */
    public Central getCentralDestino() {
        return centralDestino;
    }

    /**
     * Central destino de la población asociada a la numeración redistribuida.
     * @param centralDestino Central
     */
    public void setCentralDestino(Central centralDestino) {
        this.centralDestino = centralDestino;
    }

    /**
     * Central origen de la población asociada a la numeración redistribuida.
     * @return Central
     */
    public Central getCentralOrigen() {
        return centralOrigen;
    }

    /**
     * Central origen de la población asociada a la numeración redistribuida.
     * @param centralOrigen Central
     */
    public void setCentralOrigen(Central centralOrigen) {
        this.centralOrigen = centralOrigen;
    }

    /**
     * Población asociada a la numeración redistribuida.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Población asociada a la numeración redistribuida.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Concesionario de la numeración en el caso de que exista arrendamiento sobre la serie.
     * @return Proveedor Concesionario del arrendamiento.
     */
    public Proveedor getConcesionario() {
        return concesionario;
    }

    /**
     * Concesionario de la numeración en el caso de que exista arrendamiento sobre la serie.
     * @param concesionario Proveedor Concesionario del arrendamiento.
     */
    public void setConcesionario(Proveedor concesionario) {
        this.concesionario = concesionario;
    }

    /**
     * Comercializador de la numeración en el caso de que exista arrendamiento sobre la serie.
     * @return Proveedor Comercializador del arrendamiento.
     */
    public Proveedor getArrendatario() {
        return arrendatario;
    }

    /**
     * Comercializador de la numeración en el caso de que exista arrendamiento sobre la serie.
     * @param arrendatario Proveedor Comercializador del arrendamiento.
     */
    public void setArrendatario(Proveedor arrendatario) {
        this.arrendatario = arrendatario;
    }

    /**
     * Tipo de la modalidad de red.
     * @return TipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * Tipo de la modalidad de red.
     * @param tipoModalidad TipoModalidad
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    /**
     * Tipo de red.
     * @return TipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo de red.
     * @param tipoRed TipoRed
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }
}
