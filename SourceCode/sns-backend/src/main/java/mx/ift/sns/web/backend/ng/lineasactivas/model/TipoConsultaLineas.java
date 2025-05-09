package mx.ift.sns.web.backend.ng.lineasactivas.model;

import java.io.Serializable;

/**
 * Tipo para el combo de datos a cargar.
 */
public class TipoConsultaLineas implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Estatus Vigente. */
    public static final String HISTORICO = "0";

    /** Estatus No Vigente. */
    public static final String ULTIMO_REPORTE = "1";

    /** Constante para historico. */
    public static final TipoConsultaLineas TIPO_HISTORICO = new TipoConsultaLineas(HISTORICO, "Histórico");

    /** Constante para ultimo reporte. */
    public static final TipoConsultaLineas TIPO_ULTIMO_REPORTE =
            new TipoConsultaLineas(ULTIMO_REPORTE, "Último Reporte");

    /** Codigo de tipo consulta. */
    private String cdg;

    /** Descipcion de tipo consulta. */
    private String descripcion;

    /**
     * Constructor, por defecto vacío.
     */
    public TipoConsultaLineas() {
    }

    /**
     * Constructor.
     * @param cdg codigo
     * @param desc descripcion
     */
    public TipoConsultaLineas(String cdg, String desc) {
        this.cdg = cdg;
        this.descripcion = desc;
    }

    /**
     * Código de tipo.
     * @return String
     */
    public String getCdg() {
        return this.cdg;
    }

    /**
     * Código de tipo.
     * @param cdg String
     */
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }

    /**
     * Descripción de tipo.
     * @return String
     */
    public String getDescripcion() {
        return this.descripcion;
    }

    /**
     * Descripción de tipo.
     * @param descripcion String
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
