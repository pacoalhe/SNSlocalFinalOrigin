package mx.ift.sns.modelo.port;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.*;

/**
 * Estado de la sincronización con el ABD.
 */
@Entity
@Table(name = "PORT_ESTATUS")
public class EstatusSincronizacion implements Serializable {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Estado ok. */
    public static final String ESTATUS_PORT_OK = "0";

    /** Error de comunicaciones. */
    public static final String ESTATUS_PORT_ERR_COMM = "-1";

    /** Id de la tabla. */
    @Id
    @Column(name = "ID_PORT_ESTATUS")
    private BigDecimal id;

    /** Timestamp del estatus. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date ts;

    /** Numero de reintento. */
    @Column(name = "REINTENTOS")
    private BigDecimal reintentos;

    /** Timestamp del fichero de Portabilidades canceladas. */
    @Column(name = "CANCELADAS_TS")
    private Timestamp canceladasTs;

    /** Portabilidades canceladas. */
    @Column(name = "PORT_CANCELADAS")
    private BigDecimal portCanceladas;

    /** Portabilidades canceladas realmente. */
    @Column(name = "PORT_CANCELAR")
    private BigDecimal portCancelar;

    /** Portabilidades. */
    @Column(name = "PORT_PROCESADAS")
    private BigDecimal portProcesadas;

    /** Portabilidades reales. */
    @Column(name = "PORT_PROCESAR")
    private BigDecimal portProcesar;

    /** Timestamp del fichero de Portabilidades. */
    @Column(name = "PROCESADAS_TS")
    private Timestamp procesadasTs;

    /** Estatus de la sinc. */
    private String estatus;

    /**
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * @return the reintento
     */
    public BigDecimal getReintentos() {
        return reintentos;
    }

    /**
     * @param reintentos the reintentos to set
     */
    public void setReintentos(BigDecimal reintentos) {
        this.reintentos = reintentos;
    }

    /**
     * @return the ts
     */
    public Date getTs() {
        return ts;
    }

    /**
     * @param ts the ts to set
     */
    public void setTs(Date ts) {
        this.ts = ts;
    }

    /**
     * @return the canceladasTs
     */
    public Timestamp getCanceladasTs() {
        return canceladasTs;
    }

    /**
     * @param canceladasTs the canceladasTs to set
     */
    public void setCanceladasTs(Timestamp canceladasTs) {
        this.canceladasTs = canceladasTs;
    }

    /**
     * @return the portCanceladas
     */
    public BigDecimal getPortCanceladas() {
        return portCanceladas;
    }

    /**
     * @param portCanceladas the portCanceladas to set
     */
    public void setPortCanceladas(BigDecimal portCanceladas) {
        this.portCanceladas = portCanceladas;
    }

    /**
     * @return the portCancelar
     */
    public BigDecimal getPortCancelar() {
        return portCancelar;
    }

    /**
     * @param portCancelar the portCancelar to set
     */
    public void setPortCancelar(BigDecimal portCancelar) {
        this.portCancelar = portCancelar;
    }

    /**
     * @return the portProcesadas
     */
    public BigDecimal getPortProcesadas() {
        return portProcesadas;
    }

    /**
     * @param portProcesadas the portProcesadas to set
     */
    public void setPortProcesadas(BigDecimal portProcesadas) {
        this.portProcesadas = portProcesadas;
    }

    /**
     * @return the portProcesar
     */
    public BigDecimal getPortProcesar() {
        return portProcesar;
    }

    /**
     * @param portProcesar the portProcesar to set
     */
    public void setPortProcesar(BigDecimal portProcesar) {
        this.portProcesar = portProcesar;
    }

    /**
     * @return the procesadasTs
     */
    public Timestamp getProcesadasTs() {
        return procesadasTs;
    }

    /**
     * @param procesadasTs the procesadasTs to set
     */
    public void setProcesadasTs(Timestamp procesadasTs) {
        this.procesadasTs = procesadasTs;
    }

    /**
     * @return the status
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * @param estatus the status to set
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    // FJAH 27.05.2025 Refactorización para obtener el valor real del actiondate
    @Transient
    private Date actionDateLote;

    public Date getActionDateLote() { return actionDateLote; }
    public void setActionDateLote(Date actionDateLote) { this.actionDateLote = actionDateLote; }



    /**
     * Limpia el objeto.
     */
    public void clear() {

        estatus = ESTATUS_PORT_OK;
        reintentos = BigDecimal.ZERO;

        canceladasTs = null;
        portCanceladas = BigDecimal.ZERO;
        portCancelar = BigDecimal.ZERO;

        portProcesadas = BigDecimal.ZERO;
        portProcesar = BigDecimal.ZERO;
        procesadasTs = null;
    }

    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();

        b.append("ts=").append(ts);

        b.append(" estatus=").append(estatus);
        b.append(" r=").append(reintentos);

        b.append(" canceladas_ts=").append(canceladasTs);
        b.append(" portCanceladas=").append(portCanceladas);
        b.append(" portCancelar=").append(portCancelar);

        b.append(" portProcesadas=").append(portProcesadas);
        b.append(" portProcesar=").append(portProcesar);
        b.append(" procesadasTs=").append(procesadasTs);

        b.append(" actionDateLote=").append(actionDateLote);

        return b.toString();
    }
}
