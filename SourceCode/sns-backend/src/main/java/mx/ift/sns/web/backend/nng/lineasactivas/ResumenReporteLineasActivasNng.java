package mx.ift.sns.web.backend.nng.lineasactivas;

import java.math.BigDecimal;

import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.utils.number.NumerosUtils;

/**
 * Clase que muestra el resumen del reporte de lineas activas.
 * @author X36155QU
 */
public class ResumenReporteLineasActivasNng {

    /** ID. */
    private BigDecimal id;

    /** Clave de servicio. */
    private ClaveServicio claveServicio;

    /** Numeracion activa por Serie. */
    private Integer numeracionActivaSerie;

    /** Numeracion activa por rango. */
    private Integer numeracionActivaRango;

    /** Numeracion activa por especifica. */
    private Integer numeracionActivaEspecifica;

    /** Numeracion asignada por Serie. */
    private Integer numeracionAsignadaSerie;

    /** Numeracion asignada por rango. */
    private Integer numeracionAsignadaRango;

    /** Numeracion asignada por especifica. */
    private Integer numeracionAsignadaEspecifica;

    /** Numeracion rentada. */
    private Integer numeracionRentada;

    /** Arrendador. */
    private Proveedor arrendador;

    /** Arrendatario. */
    private Proveedor arrendatario;

    /** ABC. */
    private BigDecimal abc;

    /**
     * Devuelve el porcentaje de utilización por serie.
     * @return porcentaje
     */
    public String getPorcentajeUsoSerie() {
        return NumerosUtils.calcularPorcentajeAsString(this.numeracionActivaSerie, this.numeracionAsignadaSerie);
    }

    /**
     * Devuelve el porcentaje de utilización por rango.
     * @return porcentaje
     */
    public String getPorcentajeUsoRango() {
        return NumerosUtils.calcularPorcentajeAsString(this.numeracionActivaRango, this.numeracionAsignadaRango);
    }

    /**
     * Devuelve el porcentaje de utilización por especifica.
     * @return porcentaje
     */
    public String getPorcentajeUsoEspecifica() {
        return NumerosUtils.calcularPorcentajeAsString(this.numeracionActivaEspecifica,
                this.numeracionAsignadaEspecifica);
    }

    /**
     * Devuelve el porcentaje de utilización total.
     * @return porcentaje
     */
    public String getPorcentajeUsoTotal() {
        Integer totalActivas = (this.numeracionActivaSerie != null
                && this.numeracionActivaRango != null
                && this.numeracionActivaEspecifica != null) ? this.numeracionActivaSerie
                + this.numeracionActivaRango + this.numeracionActivaEspecifica
                : null;
        Integer totalAsignadas = (this.numeracionAsignadaSerie != null && this.numeracionAsignadaRango != null
                && this.numeracionAsignadaEspecifica != null) ? this.numeracionAsignadaSerie
                + this.numeracionAsignadaRango + this.numeracionAsignadaEspecifica
                : null;
        return NumerosUtils.calcularPorcentajeAsString(totalActivas, totalAsignadas);
    }

    /**
     * Clave de servicio.
     * @return claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Clave de servicio.
     * @param claveServicio claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Numeracion rentada.
     * @return numeracionRentada
     */
    public Integer getNumeracionRentada() {
        return numeracionRentada;
    }

    /**
     * Numeracion rentada.
     * @param numeracionRentada numeracionRentada to set
     */
    public void setNumeracionRentada(Integer numeracionRentada) {
        this.numeracionRentada = numeracionRentada;
    }

    /**
     * Arrendador.
     * @return arrendador
     */
    public Proveedor getArrendador() {
        return arrendador;
    }

    /**
     * Arrendador.
     * @param arrendador arrendador to set
     */
    public void setArrendador(Proveedor arrendador) {
        this.arrendador = arrendador;
    }

    /**
     * ABC.
     * @return abc
     */
    public BigDecimal getAbc() {
        return abc;
    }

    /**
     * ABC.
     * @param abc abc to set
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
    }

    /**
     * ID.
     * @return id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * ID.
     * @param id id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * Arrendatario.
     * @return arrendatario
     */
    public Proveedor getArrendatario() {
        return arrendatario;
    }

    /**
     * Arrendatario.
     * @param arrendatario arrendatario to set
     */
    public void setArrendatario(Proveedor arrendatario) {
        this.arrendatario = arrendatario;
    }

    /**
     * Numeracion activa por Serie.
     * @return numeracionActivaSerie
     */
    public Integer getNumeracionActivaSerie() {
        return numeracionActivaSerie;
    }

    /**
     * Numeracion activa por Serie.
     * @param numeracionActivaSerie numeracionActivaSerie to set
     */
    public void setNumeracionActivaSerie(Integer numeracionActivaSerie) {
        this.numeracionActivaSerie = numeracionActivaSerie;
    }

    /**
     * Numeracion activa por rango.
     * @return numeracionActivaRango
     */
    public Integer getNumeracionActivaRango() {
        return numeracionActivaRango;
    }

    /**
     * Numeracion activa por rango.
     * @param numeracionActivaRango numeracionActivaRango to set
     */
    public void setNumeracionActivaRango(Integer numeracionActivaRango) {
        this.numeracionActivaRango = numeracionActivaRango;
    }

    /**
     * Numeracion activa por especifica.
     * @return numeracionActivaEspecifica
     */
    public Integer getNumeracionActivaEspecifica() {
        return numeracionActivaEspecifica;
    }

    /**
     * Numeracion activa por especifica.
     * @param numeracionActivaEspecifica numeracionActivaEspecifica to set
     */
    public void setNumeracionActivaEspecifica(Integer numeracionActivaEspecifica) {
        this.numeracionActivaEspecifica = numeracionActivaEspecifica;
    }

    /**
     * Numeracion asignada por Serie.
     * @return numeracionAsignadaSerie
     */
    public Integer getNumeracionAsignadaSerie() {
        return numeracionAsignadaSerie;
    }

    /**
     * Numeracion asignada por Serie.
     * @param numeracionAsignadaSerie numeracionAsignadaSerie to set
     */
    public void setNumeracionAsignadaSerie(Integer numeracionAsignadaSerie) {
        this.numeracionAsignadaSerie = numeracionAsignadaSerie;
    }

    /**
     * Numeracion asignada por rango.
     * @return numeracionAsignadaRango
     */
    public Integer getNumeracionAsignadaRango() {
        return numeracionAsignadaRango;
    }

    /**
     * Numeracion asignada por rango.
     * @param numeracionAsignadaRango numeracionAsignadaRango to set
     */
    public void setNumeracionAsignadaRango(Integer numeracionAsignadaRango) {
        this.numeracionAsignadaRango = numeracionAsignadaRango;
    }

    /**
     * Numeracion asignada por especifica.
     * @return numeracionAsignadaEspecifica
     */
    public Integer getNumeracionAsignadaEspecifica() {
        return numeracionAsignadaEspecifica;
    }

    /**
     * Numeracion asignada por especifica.
     * @param numeracionAsignadaEspecifica numeracionAsignadaEspecifica to set
     */
    public void setNumeracionAsignadaEspecifica(Integer numeracionAsignadaEspecifica) {
        this.numeracionAsignadaEspecifica = numeracionAsignadaEspecifica;
    }

}
