package mx.ift.sns.modelo.cpsi;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Contiene la información de la segunda linea del Estudio de Códigos CPSI.
 * @author X50880SA
 */
public class Linea2EstudioCPSI implements Serializable {

    /**
     * Serialización.
     */
    private static final long serialVersionUID = 1L;
    /**
     * campo total del estudio.
     */
    private Long total;
    /**
     * campo libres del estudio.
     */
    private BigDecimal libres;
    /**
     * campo reservados del estudio.
     */
    private BigDecimal reservados;
    /**
     * campo asignados del estudio.
     */
    private BigDecimal asignados;
    /**
     * campo planificados del estudio.
     */
    private BigDecimal planificados;
    /**
     * campo cuarentena del estudio.
     */
    private BigDecimal cuarentena;
    /**
     * campo porcentaje del estudio.
     */
    private String porcentaje;

    /*********************************************************************/
    /**
     * Constructor parametrizado de la clase Linea2EstudioCPSI.
     * @param total codigos CPSI totales
     * @param libres codigos CPSI en estado libre
     * @param reservados codigos CPSI en estado reservados
     * @param asignados codigos CPSI en estado reservados
     * @param planificados codigos CPSI en estado planificados
     * @param cuarentena codigos CPSI en estado cuarentena
     */
    public Linea2EstudioCPSI(Long total, Object libres, Object reservados, Object asignados, Object planificados,
            Object cuarentena) {

        super();
        this.total = total;
        this.libres = (BigDecimal) libres;
        this.reservados = (BigDecimal) reservados;
        this.asignados = (BigDecimal) asignados;
        this.planificados = (BigDecimal) planificados;
        this.cuarentena = (BigDecimal) cuarentena;

        // Se hace el porcentaje
        double value = this.asignados.floatValue() * 100.0 / total;

        // Se formatea a dos decimales
        value = value * 100;
        value = Math.floor(Math.round(value));
        value = value / 100;

        // Se le pone el símbolo de porcentaje
        this.porcentaje = value + "%";
    }

    /**
     * Accede al dato total del informe.
     * @return Long
     */
    public Long getTotal() {
        return total;
    }

    /**
     * Accede al dato libres del informe.
     * @return BigDecimal
     */
    public BigDecimal getLibres() {
        return libres;
    }

    /**
     * Accede al dato reservados del informe.
     * @return BigDecimal
     */
    public BigDecimal getReservados() {
        return reservados;
    }

    /**
     * Accede al dato asignados del informe.
     * @return BigDecimal
     */
    public BigDecimal getAsignados() {
        return asignados;
    }

    /**
     * Accede al dato cuarentena del informe.
     * @return BigDecimal
     */
    public BigDecimal getCuarentena() {
        return cuarentena;
    }

    /**
     * Accede al dato porcentaje del informe.
     * @return String
     */
    public String getPorcentaje() {
        return porcentaje;
    }

    /**
     * campo planificados del estudio.
     * @return BigDecimal
     */
    public BigDecimal getPlanificados() {
        return planificados;
    }

}
