package mx.ift.sns.negocio.utils.excel;

/** Contiene la Información de la cabecera para la generación de reportes Excel. */
public class ExcelCabeceraInfo {

    /** Títulos de Cabecera. */
    private String[] titulos;

    /** Estilo de la celda de los títulos de cabecera. */
    private String cellStyle;

    /** Tamaño y tipo de fuente para los títulos de cabecera. */
    private String cellFont;

    /** Indica si se han de autoajustar las columnas de la cabecera. */
    private boolean autoAjuste;

    /** Indica si se ha de inmovilizar la fila de la cabecera. */
    private boolean inmovilizarCabecera;

    /** Indica si se ha de mostrar la cabecera. */
    private boolean visible;

    // GETTERS & SETTERS

    /** Constructor por defecto. */
    public ExcelCabeceraInfo() {
        visible = true;
    }

    /**
     * Títulos de Cabecera.
     * @return String[]
     */
    public String[] getTitulos() {
        return titulos;
    }

    /**
     * Títulos de Cabecera.
     * @param titulos String[]
     */
    public void setTitulos(String[] titulos) {
        this.titulos = titulos;
    }

    /**
     * Estilo de la celda de los títulos de cabecera.
     * @return String
     */
    public String getCellStyle() {
        return cellStyle;
    }

    /**
     * Estilo de la celda de los títulos de cabecera.
     * @param cellStyle String
     */
    public void setCellStyle(String cellStyle) {
        this.cellStyle = cellStyle;
    }

    /**
     * Tamaño y tipo de fuente para los títulos de cabecera.
     * @return Font
     */
    public String getCellFont() {
        return cellFont;
    }

    /**
     * Tamaño y tipo de fuente para los títulos de cabecera.
     * @param cellFont Font
     */
    public void setCellFont(String cellFont) {
        this.cellFont = cellFont;
    }

    /**
     * Indica si se han de autoajustar las columnas de la cabecera.
     * @return boolean
     */
    public boolean isAutoAjuste() {
        return autoAjuste;
    }

    /**
     * Indica si se han de autoajustar las columnas de la cabecera.
     * @param autoAjuste boolean
     */
    public void setAutoAjuste(boolean autoAjuste) {
        this.autoAjuste = autoAjuste;
    }

    /**
     * Indica si se ha de inmovilizar la fila de la cabecera.
     * @return boolean
     */
    public boolean isInmovilizarCabecera() {
        return inmovilizarCabecera;
    }

    /**
     * Indica si se ha de inmovilizar la fila de la cabecera.
     * @param inmovilizarCabecera boolean
     */
    public void setInmovilizarCabecera(boolean inmovilizarCabecera) {
        this.inmovilizarCabecera = inmovilizarCabecera;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
