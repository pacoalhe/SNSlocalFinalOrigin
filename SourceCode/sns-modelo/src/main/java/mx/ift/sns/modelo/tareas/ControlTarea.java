package mx.ift.sns.modelo.tareas;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * The persistent class for the CONTROL_TAREA database table.
 */
@Entity
@Table(name = "CONTROL_TAREA")
@NamedQuery(name = "ControlTarea.findAll", query = "SELECT c FROM ControlTarea c")
public class ControlTarea implements Serializable {

    /**
     * FJAH 08052025 Refactorización para evitar dejar con harcode de tareas a no ejecutarse.
     * 0 = desbloqueado
     * 1 = bloqueo automático (cron)
     * 2 = bloqueo manual protegido
     */
    public static final int BLOQUEADO_NO = 0;
    public static final int BLOQUEADO_SI = 1;
    public static final int BLOQUEADO_MANUAL = 2;


    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Primer intento de sincronizacion ABD. */
    public static final String PRIMER_ABD = "Sincronizacion ABD 1";

    /** Segundo intento de sincronizacion ABD. */
    public static final String SEGUNDO_ABD = "Sincronizacion ABD 2";

    /** Tercer intento de sincronizacion ABD. */
    public static final String TERCER_ABD = "Sincronizacion ABD 3";

    /** Solicitudes programadas. */
    public static final String SOLICITUDES_PROGRAMADAS = "Solicitudes programadas";

    /** Cuarentena. */
    public static final String CUARENTENA = "Cuarentena";

    /** Consolidaciones. */
    public static final String CONSOLIDACIONES = "Consolidaciones";

    /** Plan ABD. */
    public static final String PLAN_ABD = "Plan ABD";

    /** Reporte ABD. */
    public static final String REPORTE_ABD = "Reporte ABD";

    /** Plan NG. */
    public static final String PLAN_NG = "Plan NG";

    /** Plan NNG. */
    public static final String PLAN_NNG = "Plan NNG";

    /** Plan NNG Especifica. */
    public static final String PLAN_NNG_ESPECIFICA = "Plan NNG Especifica";

    /** Plan NNG Especifica PST. */
    public static final String PLAN_NNG_ESPECIFICA_PST = "Plan NNG Especifica PST";

    /** Plan NNG Especifica IFT. */
    public static final String PLAN_NNG_ESPECIFICA_IFT = "Plan NNG Especifica IFT";

    /** Identificadores de operadores. */
    public static final String IDENTIFICADORES = "Identificadores de operadores";

    /** Plan IFT. */
    public static final String PLAN_IFT = "Plan IFT";

    /** Borrado planes. */
    public static final String BORRADO_PLANES = "Borrado planes";

    public static final String PORTACION_MANUAL = "PORTACION_MANUAL";

    /** ID. */
    @Id
    private String tarea;

    /**
     * FJAH 08052025 Refactorización para evitar dejar con harcode de tareas a no ejecutarse.
     * 0 = desbloqueado
     * 1 = bloqueo automático (cron)
     * 2 = bloqueo manual protegido
     */
    /** Tarea bloqueada. */
    private Integer bloqueado;
    //private boolean bloqueado;

    /** Fecha tarea. */
    @Temporal(TemporalType.DATE)
    private Date fecha;

    /** Version JPA. */
    @Version
    private long version;

    /** Constructor, vacio por defecto. */
    public ControlTarea() {
    }

    /**
     * @return the tarea
     */
    public String getTarea() {
        return tarea;
    }

    /**
     * @param tarea the tarea to set
     */
    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    /**
     * FJAH 08052025 Refactorización para evitar dejar con harcode de tareas a no ejecutarse.
     * 0 = desbloqueado
     * 1 = bloqueo automático (cron)
     * 2 = bloqueo manual protegido
     */
    /**
     * @return the bloqueado
     */
    public Integer getBloqueado() {
        return bloqueado;
    }
    /*public boolean isBloqueado() {
        return bloqueado;
    }

     */
    /**
     * @param bloqueado the bloqueado to set
     */
    public void setBloqueado(Integer bloqueado) {
        this.bloqueado = bloqueado;
    }
    /*public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

     */


    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

}
