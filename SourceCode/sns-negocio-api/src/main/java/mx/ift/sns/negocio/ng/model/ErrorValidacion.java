package mx.ift.sns.negocio.ng.model;

import java.io.Serializable;

/**
 * Clase que almacenará los errores.
 */
public class ErrorValidacion implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * constructor.
     */
    public ErrorValidacion() {
    }

    /**
     * Numero de registro.
     */
    private String numRegistro;

    /**
     * Código.
     */
    private String codigo;

    /**
     * Descripción.
     */
    private String descripcion;

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
     * @return the numRegistro
     */
    public String getNumRegistro() {
        return numRegistro;
    }

    /**
     * @param numRegistro the numRegistro to set
     */
    public void setNumRegistro(String numRegistro) {
        this.numRegistro = numRegistro;
    }

}
