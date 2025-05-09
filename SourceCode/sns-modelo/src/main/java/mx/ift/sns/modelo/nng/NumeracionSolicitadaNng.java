package mx.ift.sns.modelo.nng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import mx.ift.sns.modelo.pst.Proveedor;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Representa la numeración solicitada de numeración no geográfica.
 */
@Entity
@Table(name = "NNG_NUM_SOLICITADA")
@SequenceGenerator(name = "SEQ_ID_NNG_NUM_SOLI", sequenceName = "SEQ_ID_NNG_NUM_SOLI", allocationSize = 1)
@NamedQuery(name = "NumeracionSolicitadaNng.findAll", query = "SELECT ns FROM NumeracionSolicitadaNng ns")
public class NumeracionSolicitadaNng implements Serializable, Cloneable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Id. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_NNG_NUM_SOLI")
    @Column(name = "ID_NNG_NUM_SOLI")
    private BigDecimal id;

    /** ABC. */
    @Column(length = 3)
    private BigDecimal abc;

    /** BCD. */
    @Column(length = 3)
    private BigDecimal bcd;

    /** Cantidad Asignada. */
    @Column(name = "CANT_ASIGNADA", length = 7)
    private BigDecimal cantidadAsignada;

    /** Cantidad Solicitada. */
    @Column(name = "CANT_SOLICITADA", nullable = false, length = 7)
    private BigDecimal cantidadSolicitada;

    /** Cliente. */
    private String cliente;

    /** Numero ininicial. */
    @Column(name = "NUM_INICIAL", length = 4)
    private String numeroInicial;

    /** Numero ininicial. */
    @Column(name = "NUM_FINAL", length = 4)
    private String numeroFinal;

    /** SNA. */
    @Column(length = 4)
    private BigDecimal sna;

    /** Concesionario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_CONCESIONARIO")
    private Proveedor concesionario;

    /** Arrendatario. */
    @ManyToOne
    @JoinColumn(name = "ID_PST_ARRENDATARIO")
    private Proveedor arrendatario;

    /** Solicitud de asigRnacion. */
    @ManyToOne
    @JoinColumn(name = "ID_SOL_SOLICITUD", nullable = false)
    private SolicitudAsignacionNng solicitudAsignacion;

    /** Referencia a la clave de Servicio asociada a la numeración. */
    @ManyToOne
    @JoinColumn(name = "ID_CLAVE_SERVICIO", nullable = false)
    private ClaveServicio claveServicio;

    /** Indicador si la numeración es valida. */
    @Column(length = 1)
    private Boolean valida;

    /** Rangos. */
    @OneToMany(mappedBy = "numeracionSolicitada")
    private List<RangoSerieNng> rangos = new ArrayList<RangoSerieNng>();

    /** Numeraciones Asignadas. */
    @OneToMany(mappedBy = "numeracionSolicitada", cascade = CascadeType.ALL)
    @PrivateOwned
    private List<NumeracionAsignadaNng> numeracionesAsignadas = new ArrayList<NumeracionAsignadaNng>();

    /** Constructor, por defecto vacío. */
    public NumeracionSolicitadaNng() {
    }

    /**
     * Método para generar copias del objeto.
     * @return ProveedorConvenio convenio
     * @throws CloneNotSupportedException ex
     */

    @Override
    public NumeracionSolicitadaNng clone() throws CloneNotSupportedException {
        NumeracionSolicitadaNng numSol = new NumeracionSolicitadaNng();
        numSol.setAbc(this.abc);
        numSol.setArrendatario(this.arrendatario);
        numSol.setBcd(this.bcd);
        numSol.setCantidadAsignada(this.cantidadAsignada);
        numSol.setCantidadSolicitada(this.cantidadSolicitada);
        numSol.setClaveServicio(this.claveServicio);
        numSol.setCliente(this.cliente);
        numSol.setConcesionario(this.concesionario);
        numSol.setNumeracionesAsignadas(new ArrayList<NumeracionAsignadaNng>());
        numSol.setNumeroFinal(this.numeroFinal);
        numSol.setNumeroInicial(this.numeroInicial);
        numSol.setSna(this.sna);
        numSol.setSolicitudAsignacion(this.solicitudAsignacion);
        numSol.setValida(this.valida);
        return numSol;
    }

    /**
     * @return the valida
     */
    public Boolean isValida() {
        return valida;
    }

    /**
     * @param valida the valida to set
     */
    public void setValida(Boolean valida) {
        this.valida = valida;
    }

    /**
     * @return the numeroInicial
     */
    public String getNumeroInicial() {
        return StringUtils.leftPad(this.numeroInicial, 4, '0');
    }

    /**
     * @param numeroInicial the numeroInicial to set
     */
    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = StringUtils.leftPad(numeroInicial, 4, '0');
    }

    /**
     * @return the numeroFinal
     */
    public String getNumeroFinal() {
        return StringUtils.leftPad(this.numeroFinal, 4, '0');
    }

    /**
     * @param numeroFinal the numeroFinal to set
     */
    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = StringUtils.leftPad(numeroFinal, 4, '0');
    }

    /**
     * @return the sna
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * devuelve el sns en formatop string con 0 a la derecha.
     * @return sna
     */
    public String getSnaAsString() {
        if (this.sna != null) {
            return String.format("%03d", this.sna.intValue());
        } else {
            return "";
        }
    }

    /**
     * @param sna the sna to set
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * @return the rangos
     */
    public List<RangoSerieNng> getRangos() {
        return rangos;
    }

    /**
     * @param rangos the rangos to set
     */
    public void setRangos(List<RangoSerieNng> rangos) {
        this.rangos = rangos;
    }

    /**
     * Añade un nuevo rango a la solicitud.
     * @param rango rango
     * @return Oficio
     */
    public RangoSerieNng addRango(RangoSerieNng rango) {
        this.getRangos().add(rango);
        rango.setNumeracionSolicitada(this);
        return rango;
    }

    /**
     * Elimina la referencia del rango a la solicitud.
     * @param rango RangoSerie
     * @return RangoSerie
     */
    public RangoSerieNng removeRango(RangoSerieNng rango) {
        this.getRangos().remove(rango);
        rango.setNumeracionSolicitada(null);
        return rango;
    }

    /**
     * Id.
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * Id.
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * ABC.
     * @return the abc
     */
    public BigDecimal getAbc() {
        return abc;
    }

    /**
     * ABC.
     * @param abc the abc to set
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
    }

    /**
     * BCD.
     * @return the bcd
     */
    public BigDecimal getBcd() {
        return bcd;
    }

    /**
     * BCD.
     * @param bcd the bcd to set
     */
    public void setBcd(BigDecimal bcd) {
        this.bcd = bcd;
    }

    /**
     * Cantidad Asignada.
     * @return the cantidadAsignada
     */
    public BigDecimal getCantidadAsignada() {
        return cantidadAsignada;
    }

    /**
     * Cantidad Asignada.
     * @param cantidadAsignada the cantidadAsignada to set
     */
    public void setCantidadAsignada(BigDecimal cantidadAsignada) {
        this.cantidadAsignada = cantidadAsignada;
    }

    /**
     * Cantidad Solicitada.
     * @return the cantidadSolicitada
     */
    public BigDecimal getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    /**
     * Cantidad Solicitada.
     * @param cantidadSolicitada the cantidadSolicitada to set
     */
    public void setCantidadSolicitada(BigDecimal cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    /**
     * Cantidad Solicitada.
     * @return the cliente
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Cantidad Solicitada.
     * @param cliente the cliente to set
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    /**
     * Concesionario.
     * @return the concesionario
     */
    public Proveedor getConcesionario() {
        return concesionario;
    }

    /**
     * Concesionario.
     * @param concesionario the concesionario to set
     */
    public void setConcesionario(Proveedor concesionario) {
        this.concesionario = concesionario;
    }

    /**
     * Arrendatario.
     * @return the arrendatario
     */
    public Proveedor getArrendatario() {
        return arrendatario;
    }

    /**
     * Arrendatario.
     * @param arrendatario the arrendatario to set
     */
    public void setArrendatario(Proveedor arrendatario) {
        this.arrendatario = arrendatario;
    }

    /**
     * Solicitud de asigRnacion.
     * @return the solicitudAsignacion
     */
    public SolicitudAsignacionNng getSolicitudAsignacion() {
        return solicitudAsignacion;
    }

    /**
     * Solicitud de asigRnacion.
     * @param solicitudAsignacion the solicitudAsignacion to set
     */
    public void setSolicitudAsignacion(SolicitudAsignacionNng solicitudAsignacion) {
        this.solicitudAsignacion = solicitudAsignacion;
    }

    /**
     * Referencia a la clave de Servicio asociada a la numeración.
     * @return the claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Referencia a la clave de Servicio asociada a la numeración.
     * @param claveServicio the claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Numeraciones Asignadas.
     * @return the numeracionesAsignadas
     */
    public List<NumeracionAsignadaNng> getNumeracionesAsignadas() {
        return numeracionesAsignadas;
    }

    /**
     * Numeraciones Asignadas.
     * @param numeracionesAsignadas the numeracionesAsignadas to set
     */
    public void setNumeracionesAsignadas(List<NumeracionAsignadaNng> numeracionesAsignadas) {
        this.numeracionesAsignadas = numeracionesAsignadas;
    }

    /**
     * Añade una numeracion asignada.
     * @param numeracionAsignada NumeracionAsignada
     * @return NumeracionAsignada
     */
    public NumeracionAsignadaNng addNumeracionAsignada(NumeracionAsignadaNng numeracionAsignada) {
        this.getNumeracionesAsignadas().add(numeracionAsignada);
        numeracionAsignada.setNumeracionSolicitada(this);
        return numeracionAsignada;
    }

    /**
     * Elimina la referencia de la numeracion asignada.
     * @param numeracionAsignada NumeracionAsignada
     * @return NumeracionAsignada
     */
    public NumeracionAsignadaNng removeNumeracionAsignada(NumeracionAsignadaNng numeracionAsignada) {
        this.getNumeracionesAsignadas().remove(numeracionAsignada);
        numeracionAsignada.setNumeracionSolicitada(null);
        return numeracionAsignada;
    }

    /**
     * @return the valida
     */
    public Boolean getValida() {
        return valida;
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
        NumeracionSolicitadaNng numSol = null;
        if (!(obj instanceof NumeracionSolicitadaNng)) {
            return false;
        } else {
            numSol = (NumeracionSolicitadaNng) obj;
        }
        String codArrendatario = "null";
        if (numSol.getArrendatario() != null) {
            codArrendatario = numSol.getArrendatario().getCdgPst().toString();
        }
        String codConcesionario = "null";
        if (numSol.getConcesionario() != null) {
            codConcesionario = numSol.getConcesionario().getCdgPst().toString();
        }

        String sna = "null";
        if (numSol.getSna() != null) {
            sna = numSol.getSna().toString();
        }

        String numIni = "null";
        if (numSol.getNumeroInicial() != null) {
            numIni = numSol.getNumeroInicial().toString();
        }

        String numFinal = "null";
        if (numSol.getNumeroFinal() != null) {
            numFinal = numSol.getNumeroFinal().toString();
        }

        String cliente = "null";
        if (numSol.getCliente() != null && !numSol.getCliente().equals("")) {
            cliente = numSol.getCliente();
        }

        String key = numSol.getClaveServicio().getCodigo().toString() + "-" + numSol.getAbc().toString() + "-"
                + codArrendatario + "-" + codConcesionario + "-" + numSol.getCantidadSolicitada() + "-" + sna + "-"
                + numIni + "-" + numFinal + "-" + cliente;

        String[] clave = key.split("-");
        if (this.getClaveServicio().getCodigo().toString().equals(clave[0])
                && this.getAbc().toString().equals(clave[1])
                && this.getCantidadSolicitada().toString().equals(clave[4])) {

            boolean encontradoArrendatario = false;
            if (this.getArrendatario() == null && clave[2].equals("null")) {
                encontradoArrendatario = true;
            } else if (this.getArrendatario() != null) {
                if (this.getArrendatario().getCdgPst().toString().equals(clave[2])) {
                    encontradoArrendatario = true;
                }
            }

            boolean encontradoConcesionario = false;
            if (this.getConcesionario() == null && clave[3].equals("null")) {
                encontradoConcesionario = true;
            } else if (this.getConcesionario() != null) {
                if (this.getConcesionario().getCdgPst().toString().equals(clave[3])) {
                    encontradoConcesionario = true;
                }
            }

            boolean encontradoSna = false;
            if (this.getSna() == null && clave[5].equals("null")) {
                encontradoSna = true;
            } else if (this.getSna() != null) {
                if (this.getSna().toString().equals(clave[5])) {
                    encontradoSna = true;
                }
            }

            boolean encontradoNumIni = false;
            if (this.getNumeroInicial() == null && clave[6].equals("null")) {
                encontradoNumIni = true;
            } else if (this.getNumeroInicial() != null) {
                if (this.getNumeroInicial().toString().equals(clave[6])) {
                    encontradoNumIni = true;
                }
            }

            boolean encontradoNumFinal = false;
            if (this.getNumeroFinal() == null && clave[7].equals("null")) {
                encontradoNumFinal = true;
            } else if (this.getNumeroFinal() != null) {
                if (this.getNumeroFinal().toString().equals(clave[7])) {
                    encontradoNumFinal = true;
                }
            }

            boolean encontradoCliente = false;
            if ((this.getCliente() == null || this.getCliente().equals(""))
                    && clave[8].equals("null")) {
                encontradoCliente = true;
            } else if (this.getCliente() != null) {
                if (this.getCliente().equals(clave[8])) {
                    encontradoCliente = true;
                }
            }

            return encontradoArrendatario && encontradoConcesionario && encontradoNumIni && encontradoNumFinal
                    && encontradoSna && encontradoCliente;

        } else {
            return false;
        }

    }

}
