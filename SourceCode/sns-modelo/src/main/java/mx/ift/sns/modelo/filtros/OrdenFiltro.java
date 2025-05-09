package mx.ift.sns.modelo.filtros;

/**
 * Orden del filtro de busqueda.
 */
public enum OrdenFiltro {

    /** Sin ordenacion. */
    NINGUNO(-1),

    /** Ascendente. */
    ASCENDENTE(0),

    /** Descendente. */
    DESCENDENTE(1);

    /** valor del enum. */
    private int value;

    /**
     * Constructor por valor.
     * @param value valor
     */
    private OrdenFiltro(int value) {
        this.setValue(value);
    }

    /**
     * valor del enum.
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * valor del enum.
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }
}
