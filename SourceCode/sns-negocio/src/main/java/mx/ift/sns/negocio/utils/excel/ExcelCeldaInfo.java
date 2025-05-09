package mx.ift.sns.negocio.utils.excel;

/** Información de celda excel para generación de reportes. */
public class ExcelCeldaInfo {

    /** Texto a mostrar en la celda. */
    private String texto;

    /** Estilo de la celda de los títulos de cabecera. */
    private String cellStyle;

    /** Tamaño y tipo de fuente para los títulos de cabecera. */
    private String cellFont;

    /** Indica si se ha de colorear la fila entera con el color de fondo del estilo de ésta celda. */
    private boolean autoColorearFila;

    /** Constructor por defecto. */
    public ExcelCeldaInfo() {

    }

    /**
     * Constructor.
     * @param pEstilo Estilo de celda.
     */
    public ExcelCeldaInfo(String pEstilo) {
        cellStyle = pEstilo;
    }

    /**
     * Constructor.
     * @param pTexto Texto de celda.
     * @param pEstilo Estilo de celda.
     */
    public ExcelCeldaInfo(String pTexto, String pEstilo) {
        cellStyle = pEstilo;
        texto = pTexto;
    }

    /**
     * Constructor.
     * @param pTexto Texto de celda.
     * @param pEstilo Estilo de celda.
     * @param pFuente Fuente
     * @param pAutocolorear boolean
     */
    public ExcelCeldaInfo(String pTexto, String pEstilo, String pFuente, boolean pAutocolorear) {
        cellStyle = pEstilo;
        texto = pTexto;
        cellFont = pFuente;
        autoColorearFila = pAutocolorear;
    }

    // GETTERS & SETTERS

    /**
     * Estilo de la celda de los títulos de cabecera.
     * @return CellStyle
     */
    public String getCellStyle() {
        return cellStyle;
    }

    /**
     * Estilo de la celda de los títulos de cabecera.
     * @param cellStyle CellStyle
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
     * Texto a mostrar en la celda.
     * @return String
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Texto a mostrar en la celda.
     * @param texto String
     */
    public void setTexto(String texto) {
        if (texto != null) {
            this.texto = texto;
        } else {
            this.texto = "";
        }

    }

    /**
     * Indica si se ha de colorear la fila entera con el color de fondo del estilo de ésta celda.
     * @return the autoColorearFila
     */
    public boolean isAutoColorearFila() {
        return autoColorearFila;
    }

    /**
     * Indica si se ha de colorear la fila entera con el color de fondo del estilo de ésta celda.
     * @param autoColorearFila the autoColorearFila to set
     */
    public void setAutoColorearFila(boolean autoColorearFila) {
        this.autoColorearFila = autoColorearFila;
    }
}
