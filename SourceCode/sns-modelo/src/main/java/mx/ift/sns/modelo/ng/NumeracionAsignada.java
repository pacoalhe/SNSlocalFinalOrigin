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

import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.Nir;

import org.apache.commons.lang3.StringUtils;

/**
 * Representa la numeración asignada de nuemeración geográfica.
 */
@Entity
@Table(name = "NG_NUM_ASIGNADA")
@SequenceGenerator(name = "SEQ_ID_NUMS_ASIG", sequenceName = "SEQ_ID_NUMS_ASIG", allocationSize = 1)
@NamedQuery(name = "NumeracionAsignada.findAll", query = "SELECT n FROM NumeracionAsignada n")
public class NumeracionAsignada implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NUMS_ASIG")
    @Column(name = "ID_NG_NUM_ASIGNADA", unique = true, nullable = false)
    private BigDecimal id;

    /** Fin rango. */
    @Column(name = "FIN_RANGO", nullable = false, length = 5)
    private String finRango;

    /** Inicio rango. */
    @Column(name = "INICIO_RANGO", nullable = false, length = 5)
    private String inicioRango;

    /** SNA. */
    @Column(name = "ID_SERIE", nullable = false)
    private BigDecimal sna;

    /** NIR. */
    @ManyToOne
    @JoinColumn(name = "ID_NIR", nullable = false)
    private Nir nir;

    /** Numeracion solicitada. */
    @ManyToOne
    @JoinColumn(name = "ID_NUM_SOLICITADA", nullable = false)
    private NumeracionSolicitada numeracionSolicitada;

    /** Solicitud. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL", nullable = false)
    private SolicitudAsignacion solicitudAsignacion;

    /** Relación: Muchos rangos pueden tener el mismo estatus. */
    @ManyToOne
    @JoinColumn(name = "ID_ESTATUS_RANGO", nullable = false)
    private EstadoRango estatus;

    /** Fin rango. */
    @Column(name = "CDG_NIR_INICIAL", nullable = true, length = 3)
    private int cdgNirInicial;

    /** Inicio rango. */
    @Column(name = "ID_SERIE_INICIAL", nullable = true, precision = 5)
    private BigDecimal idSerieInicial;

    /** Constructor vacio por defecto. */
    public NumeracionAsignada() {
    }

    /**
     * Constructor utilizando un rango.
     * @param rango rango
     * @throws Exception error
     */
    public NumeracionAsignada(RangoSerie rango) throws Exception {
        this.nir = rango.getSerie().getNir();
        this.sna = rango.getSna();
        this.inicioRango = rango.getNumInicio();
        this.finRango = rango.getNumFinal();
        this.numeracionSolicitada = rango.getNumSolicitada();
        this.estatus = rango.getEstadoRango();
        this.cdgNirInicial = rango.getSerie().getNir().getCodigo();
        this.idSerieInicial = rango.getSerie().getId().getSna();
    }

    /**
     * Método que añade al SNA inicial tantos 0 a la izquierda dependiendo del tamaño del NIR inicial.
     * @return String
     * @throws Exception Exception
     */
    public String getIdSerieInicialAsString() throws Exception {
        return StringUtils.leftPad(this.idSerieInicial.toString(), 6 - String.valueOf(this.cdgNirInicial).length(), '0');
    }
        

    /**
     * @return the cdgNirInicial
     */
    public int getCdgNirInicial() {
        return cdgNirInicial;
    }

    /**
     * @param cdgNirInicial the cdgNirInicial to set
     */
    public void setCdgNirInicial(int cdgNirInicial) {
        this.cdgNirInicial = cdgNirInicial;
    }

    /**
     * @return the idSerieInicial
     */
    public BigDecimal getIdSerieInicial() {
        return idSerieInicial;
    }

    /**
     * @param idSerieInicial the idSerieInicial to set
     */
    public void setIdSerieInicial(BigDecimal idSerieInicial) {
        this.idSerieInicial = idSerieInicial;
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
     * Fin rango.
     * @return the finRango
     */
    public String getFinRango() {
        return finRango;
    }

    /**
     * Fin rango.
     * @param finRango the finRango to set
     */
    public void setFinRango(String finRango) {
        this.finRango = finRango;
    }

    /**
     * Inicio rango.
     * @return the inicioRango
     */
    public String getInicioRango() {
        return inicioRango;
    }

    /**
     * Inicio rango.
     * @param inicioRango the inicioRango to set
     */
    public void setInicioRango(String inicioRango) {
        this.inicioRango = inicioRango;
    }

    /**
     * SNA.
     * @return the sna
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * SNA.
     * @param sna the sna to set
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * NIR.
     * @return the nir
     */
    public Nir getNir() {
        return nir;
    }

    /**
     * NIR.
     * @param nir the nir to set
     */
    public void setNir(Nir nir) {
        this.nir = nir;
    }

    /**
     * Numeracion solicitada.
     * @return the numeracionSolicitada
     */
    public NumeracionSolicitada getNumeracionSolicitada() {
        return numeracionSolicitada;
    }

    /**
     * Numeracion solicitada.
     * @param numeracionSolicitada the numeracionSolicitada to set
     */
    public void setNumeracionSolicitada(NumeracionSolicitada numeracionSolicitada) {
        this.numeracionSolicitada = numeracionSolicitada;
    }

    /**
     * Solicitud.
     * @return the solicitudAsignacion
     */
    public SolicitudAsignacion getSolicitudAsignacion() {
        return solicitudAsignacion;
    }

    /**
     * Solicitud.
     * @param solicitudAsignacion solicitudAsignacion to set
     */
    public void setSolicitudAsignacion(SolicitudAsignacion solicitudAsignacion) {
        this.solicitudAsignacion = solicitudAsignacion;
    }

    /**
     * Estatus.
     * @return estatus
     */
    public EstadoRango getEstatus() {
        return estatus;
    }

    /**
     * Estatus.
     * @param estatus estatus to set
     */
    public void setEstatus(EstadoRango estatus) {
        this.estatus = estatus;
    }

    /**
     * Método que añade al SNA tantos 0 a la izquierda dependiendo del tamaño del NIR.
     * @return String
     * @throws Exception Exception
     */
    public String getSnaAsString() throws Exception {
        if (String.valueOf(this.nir.getCodigo()).length() == 3) {
            return StringUtils.leftPad(this.sna.toString(), 3, '0');
        } else if (String.valueOf(this.nir.getCodigo()).length() == 2) {
            return StringUtils.leftPad(this.sna.toString(), 4, '0');
        } else {
            return StringUtils.leftPad(this.sna.toString(), 5, '0');
        }
    }

    /**
     * Inicio rango como int.
     * @return finRango
     */
    public int getInicioRangoAsInt() {
        return Integer.parseInt(inicioRango);
    }

    /**
     * Fin rango como int.
     * @return finRango
     */
    public int getFinRangoAsInt() {
        return Integer.parseInt(finRango);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NumeracionAsignada)) {
            return false;
        }
        return this.id.compareTo(((NumeracionAsignada) obj).getId()) == 0;
    }
}
