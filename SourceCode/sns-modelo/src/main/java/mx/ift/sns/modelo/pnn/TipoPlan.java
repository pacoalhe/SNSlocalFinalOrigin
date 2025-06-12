package mx.ift.sns.modelo.pnn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Tipo de plan de numeracion.
 */
@Entity
@Table(name = "CAT_TIPO_PLAN")
@NamedQuery(name = "TipoPlan.findAll", query = "SELECT t FROM TipoPlan t")
public class TipoPlan implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Plan de numeracion ABD Presuscrupción. */
    public static final String TIPO_PLAN_ABD_PRESUSCRIPCION = "A";

    /** Plan de numeración no geográfica Específica. */
    public static final String TIPO_PLAN_NNG_ESPECIFICA = "B";

    /** Reporte ABD portabilidad. */
    public static final String TIPO_PLAN_ABD_PORTABILIDAD = "C";

    /** Plan de numeración geográfica para los PSTs. */
    public static final String TIPO_PLAN_NG_PST = "D";

    /** Plan de numeración geográfica público. */
    public static final String TIPO_PLAN_NG_PUBLICO = "E";

    /** Plan de numeración no geográfica para los PSTs. */
    public static final String TIPO_PLAN_NNG_PST = "F";

    /** Plan de numeración no geográfica públicos. */
    public static final String TIPO_PLAN_NNG_PUBLICO = "G";

    /** Plan de numeración no geográfica IFT. */
    public static final String TIPO_PLAN_NNG_IFT = "H";

    /** Identificadores de operadores. */
    public static final String TIPO_PLAN_ID_OPERADORES = "I";

    /** Plan de numeracion IFT. */
    public static final String TIPO_PLAN_IFT = "J";

    /** Plan de numeracion no geográfica Específica IFT. */
    public static final String TIPO_PLAN_NNG_ESPECIFICA_IFT = "K";

    /** BBDD NNGE. */
    public static final String TIPO_PLAN_BBDD_NNGE = "L";

    /** Plan de numeracion no geográfica Específica PST. */
    public static final String TIPO_PLAN_NNG_ESPECIFICA_PST = "M";

    /** Matriz de enrutamiento 911 y 089. */
    public static final String TIPO_MATRIZ_ENRUTAMIENTO_911_889 = "0";

    /** Matriz de enrutamiento CSE 07x. */
    public static final String TIPO_MATRIZ_ENRUTAMIENTO_CSE_07X = "P";

    /** Matriz de enrutamiento 911 y 089 NUEVO. */
    public static final String TIPO_MATRIZ_ENRUTAMIENTO_911_889_NUEVO = "Q";

    /** Matriz de enrutamiento CSE 07x NUEVO. */
    public static final String TIPO_MATRIZ_ENRUTAMIENTO_CSE_07X_NUEVO = "R";

    /** Plan de numeración geográfica para los PSTs Nuevo. */
    public static final String TIPO_PLAN_NG_PST_NUEVO = "S";

    /** Plan de numeración geográfica público Nuevo. */
    public static final String TIPO_PLAN_NG_PUBLICO_NUEVO = "T";

    /** Plan de numeración no geográfica IFT Nuevo. */
    public static final String TIPO_PLAN_NNG_IFT_NUEVO = "U";

    /** Reporte ABD portabilidad. */
    public static final String TIPO_PLAN_ABD_PORTABILIDAD_NUEVO = "V";

    /** Id del plan. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_TIPO_PLAN", unique = true, nullable = false, length = 1)
    private String id;

    /** Descripcion del tipo. */
    @Column(length = 100)
    private String descripcion;

    /** Formato del nombre. */
    @Column(name = "FORMATO_NOMBRE", length = 50)
    private String formatoNombre;

    /** Periodo de retencion en dias. */
    private BigDecimal retencion;

    /** Planes con los que esta asociado este tipo. */
    @OneToMany(mappedBy = "tipoPlan")
    private List<Plan> planes;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the formatoNombre
     */
    public String getFormatoNombre() {
        return formatoNombre;
    }

    /**
     * @param formatoNombre the formatoNombre to set
     */
    public void setFormatoNombre(String formatoNombre) {
        this.formatoNombre = formatoNombre;
    }

    /**
     * @return the retencion
     */
    public BigDecimal getRetencion() {
        return retencion;
    }

    /**
     * @param retencion the retencion to set
     */
    public void setRetencion(BigDecimal retencion) {
        this.retencion = retencion;
    }

    /**
     * @return the planes
     */
    public List<Plan> getPlanes() {
        return planes;
    }

    /**
     * @param planes the planes to set
     */
    public void setPlanes(List<Plan> planes) {
        this.planes = planes;
    }

    /**
     * Añade un plan.
     * @param plan a añadir
     * @return plan añadido
     */
    public Plan addPlan(Plan plan) {
        getPlanes().add(plan);
        plan.setTipoPlan(this);

        return plan;
    }

    /**
     * Quita un plan.
     * @param plan a añadir
     * @return plan borrado
     */
    public Plan removePlan(Plan plan) {
        getPlanes().remove(plan);
        plan.setTipoPlan(null);

        return plan;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("id=");
        b.append(id);
        b.append(" desc=");
        b.append(descripcion);

        return b.toString();
    }
}
