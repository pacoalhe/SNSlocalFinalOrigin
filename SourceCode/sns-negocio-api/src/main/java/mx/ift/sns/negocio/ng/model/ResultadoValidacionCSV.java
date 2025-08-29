package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Resultado de la validacion de un CSV.
 */
public class ResultadoValidacionCSV implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Fichero ok. */
    public static final int VALIDACION_OK = 0;

    /** Error accediendo al fichero. */
    public static final int ERROR_FICHERO = 1;

    /** Error en cabecera del fichero. */
    public static final int ERROR_CABECERA = 2;

    /** Error fichero vacio. */
    public static final int ERROR_FICHERO_VACIO = 3;

    /** Error fichero incorrecto. */
    public static final int ERROR_FORMATO_FICHERO = 4;

    /** Error en la validacion. */
    private int error;

    /**
     * @return the error
     */
    public int getError() {
        return error;
    }

    // Totales del procesamiento Portados
    private int totalOrigen;           // total de filas leídas del archivo
    private int totalProcesados;       // total realmente procesados en BD (insert+update)
    private int totalErrorEstructura;  // registros con error de estructura/parseo
    private int totalInsertados;       // registros insertados nuevos en BD
    private int totalActualizados;     // registros actualizados en BD
    private int totalNoPersistidos;    // registros que no quedaron en BD
    private int totalFallidosGlobal;   // ERROR_ESTRUCTURA + NO_PERSISTIDOS
    // Info adicional
    private String mensaje;           // Mensaje general del resultado (opcional)

    private int totalPendientesInsertar;
    private int totalPendientesActualizar;



    //=================================================
    // Totales CANCELADOS
    //=================================================
    private int totalOrigenCanc;
    private int totalInsertadosCanc;
    private int totalEliminadosCanc;
    private int totalFallidosCanc;

    private int totalFallidosInsercionCanc;
    private int totalFallidosEliminacionCanc;
    private int totalErrorEstructuraCanc;
    private int totalProcesadosCanc;



    private BigDecimal portCanceladas;   // total ya procesadas en cancelados
    private BigDecimal portCancelar;     // total pendientes a cancelar (del lote)
    private Timestamp canceladasTs;      // fecha/hora en que se procesaron cancelados


    /**
     * @param error the error to set
     */
    public void setError(int error) {
        this.error = error;
    }

    private String actionDateLote; // o Date/Timestamp si prefieres

    public String getActionDateLote() {
        return actionDateLote;
    }

    public void setActionDateLote(String actionDateLote) {
        this.actionDateLote = actionDateLote;
    }

    public int getTotalOrigen() {
        return totalOrigen;
    }

    public void setTotalOrigen(int totalOrigen) {
        this.totalOrigen = totalOrigen;
    }

    public int getTotalProcesados() {
        return totalProcesados;
    }

    public void setTotalProcesados(int totalProcesados) {
        this.totalProcesados = totalProcesados;
    }

    public int getTotalErrorEstructura() {
        return totalErrorEstructura;
    }

    public void setTotalErrorEstructura(int totalErrorEstructura) {
        this.totalErrorEstructura = totalErrorEstructura;
        recalcFallidosGlobal();
    }

    public int getTotalInsertados() {
        return totalInsertados;
    }

    public void setTotalInsertados(int totalInsertados) {
        this.totalInsertados = totalInsertados;
    }

    public int getTotalActualizados() {
        return totalActualizados;
    }

    public void setTotalActualizados(int totalActualizados) {
        this.totalActualizados = totalActualizados;
    }

    public int getTotalNoPersistidos() {
        return totalNoPersistidos;
    }

    public void setTotalNoPersistidos(int totalNoPersistidos) {
        this.totalNoPersistidos = totalNoPersistidos;
        recalcFallidosGlobal();
    }

    public int getTotalFallidosGlobal() {
        return totalFallidosGlobal;
    }

    private void recalcFallidosGlobal() {
        this.totalFallidosGlobal = this.totalErrorEstructura + this.totalNoPersistidos;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public BigDecimal getPortCanceladas() {
        return portCanceladas;
    }

    public void setPortCanceladas(BigDecimal portCanceladas) {
        this.portCanceladas = portCanceladas;
    }

    public BigDecimal getPortCancelar() {
        return portCancelar;
    }

    public void setPortCancelar(BigDecimal portCancelar) {
        this.portCancelar = portCancelar;
    }

    public Timestamp getCanceladasTs() {
        return canceladasTs;
    }

    public void setCanceladasTs(Timestamp canceladasTs) {
        this.canceladasTs = canceladasTs;
    }

    public int getTotalPendientesInsertar() { return totalPendientesInsertar; }
    public void setTotalPendientesInsertar(int v) { this.totalPendientesInsertar = v; }

    public int getTotalPendientesActualizar() { return totalPendientesActualizar; }
    public void setTotalPendientesActualizar(int v) { this.totalPendientesActualizar = v; }

    //=================================================
    // Getters y Setter totales CANCELADOS
    //=================================================
    public int getTotalOrigenCanc() {
        return totalOrigenCanc;
    }

    public void setTotalOrigenCanc(int totalOrigenCanc) {
        this.totalOrigenCanc = totalOrigenCanc;
    }

    public int getTotalInsertadosCanc() {
        return totalInsertadosCanc;
    }

    public void setTotalInsertadosCanc(int totalInsertadosCanc) {
        this.totalInsertadosCanc = totalInsertadosCanc;
    }

    public int getTotalEliminadosCanc() {
        return totalEliminadosCanc;
    }

    public void setTotalEliminadosCanc(int totalEliminadosCanc) {
        this.totalEliminadosCanc = totalEliminadosCanc;
    }

    public int getTotalProcesadosCanc() {
        return totalProcesadosCanc;
    }

    public void setTotalProcesadosCanc(int totalProcesadosCanc) {
        this.totalProcesadosCanc = totalProcesadosCanc;
    }

    public int getTotalFallidosCanc() {
        return totalOrigenCanc - totalProcesadosCanc;
    }

    public void setTotalFallidosCanc(int totalFallidosCanc) {
        this.totalFallidosCanc = totalFallidosCanc;
    }

    public int getTotalFallidosInsercionCanc() { return totalFallidosInsercionCanc; }
    public void setTotalFallidosInsercionCanc(int v) { this.totalFallidosInsercionCanc = v; }

    public int getTotalFallidosEliminacionCanc() { return totalFallidosEliminacionCanc; }
    public void setTotalFallidosEliminacionCanc(int v) { this.totalFallidosEliminacionCanc = v; }

    public int getTotalErrorEstructuraCanc() {
        return totalErrorEstructuraCanc;
    }

    public void setTotalErrorEstructuraCanc(int totalErrorEstructuraCanc) {
        this.totalErrorEstructuraCanc = totalErrorEstructuraCanc;
    }


    @Override
    public String toString() {
        return "ResultadoValidacionCSV{" +
                "totalOrigen=" + totalOrigen +
                ", totalProcesados=" + totalProcesados +
                ", totalErrorEstructura=" + totalErrorEstructura +
                ", actionDateLote='" + actionDateLote + '\'' +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }

    // ===== NUEVOS: CAMPOS DE EstatusSincronizacion =====
    private BigDecimal idEstatus;
    private Date ts;
    private String estatus;
    private BigDecimal reintentos;
    private BigDecimal portProcesadas;
    private BigDecimal portProcesar;
    private Timestamp procesadasTs;
    private Date actionDateLoteDate; // String actionDateLote lote

    // ===== NUEVOS =====

    public BigDecimal getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(BigDecimal idEstatus) {
        this.idEstatus = idEstatus;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public BigDecimal getReintentos() {
        return reintentos;
    }

    public void setReintentos(BigDecimal reintentos) {
        this.reintentos = reintentos;
    }

    public BigDecimal getPortProcesadas() {
        return portProcesadas;
    }

    public void setPortProcesadas(BigDecimal portProcesadas) {
        this.portProcesadas = portProcesadas;
    }

    public BigDecimal getPortProcesar() {
        return portProcesar;
    }

    public void setPortProcesar(BigDecimal portProcesar) {
        this.portProcesar = portProcesar;
    }

    public Timestamp getProcesadasTs() {
        return procesadasTs;
    }

    public void setProcesadasTs(Timestamp procesadasTs) {
        this.procesadasTs = procesadasTs;
    }

    public Date getActionDateLoteDate() {
        return actionDateLoteDate;
    }

    public void setActionDateLoteDate(Date actionDateLoteDate) {
        this.actionDateLoteDate = actionDateLoteDate;
    }



}
