package mx.ift.sns.modelo.config;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Parámetro de configuración del Sistema.
 */
@Entity
@Table(name = "CAT_PARAMETRO")
@SequenceGenerator(name = "SEQ_ID_CONF_PARAM", sequenceName = "SEQ_ID_CONF_PARAM", allocationSize = 1)
@NamedQuery(name = "Parametro.findAll", query = "SELECT c FROM Parametro c")
public class Parametro implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Nombre parametro prefijo oficio. */
    public static final String PREFIJO = "prefijo";

    /** Nombre del tamaño máximo del fichero de líneas activas. */
    public static final String FICH_LINEAS_ACTIVAS_SIZE = "maxTamFicheroLineasActivas";

    /** Nombre del tamaño máximo del fichero de Asignación Automática. */
    public static final String FICH_ASIG_AUTO_SIZE = "maxTamFicheroAsigAutomatica";

    /** Nombre del tamaño máximo del fichero de Oficios. */
    public static final String FICH_OFICIO_SIZE = "maxTamFicheroOficio";

    /** Parámetro que indica el número de registros por página a mostrar en el BackEnd. */
    public static final String REGISTROS_POR_PAGINA_BACK = "numRegistrosPorPagina";

    /** Parámetro que indica el número de registros por página a mostrar en el FrontEnd. */
    public static final String REGISTROS_POR_PAGINA_FRONT = "numRegistrosPorPaginaFrontEnd";

    /** Parámetro que indica el número de registros por página a mostrar en el BackEnd dentro de los Wizard. */
    public static final String REGISTROS_POR_PAGINA_BACK_WIZ = "numRegistrosPorPaginaWizard";

    /** ID del parametro. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID_CONF_PARAM")
    @Column(name = "ID_PARAMETRO")
    private long id;

    /** Nombre del parametro. */
    @Column(unique = true, nullable = false)
    private String nombre;

    /** Descripcion del parametro. */
    private String descripcion;

    /** Valor del parametro. */
    @Column(nullable = false)
    private String valor;

    /**
     * ID del parametro.
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * ID del parametro.
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Nombre del parametro.
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre del parametro.
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Descripcion del parametro.
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Descripcion del parametro.
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Valor del parametro.
     * @return the valor
     */
    public String getValor() {
        return valor;
    }

    /**
     * Valor del parametro.
     * @param valor the valor to set
     */
    public void setValor(String valor) {
        this.valor = valor;
    }
}
