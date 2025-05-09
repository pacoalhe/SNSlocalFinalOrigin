package mx.ift.sns.web.backend.ng.asignacion.manual.analisis;

import java.math.BigDecimal;

/**
 * Clase para formar la tabla de sugerencias en el analisis de la asignacion.
 */
public class SugerenciaNumeracion {

    /**
     * Numero inicial.
     */
    private BigDecimal numInicial;

    /**
     * Numero final.
     */
    private BigDecimal numFinal;

    /**
     * Numero inicial.
     * @return numInicial
     */
    public BigDecimal getNumInicial() {
        return numInicial;
    }

    /**
     * Numero inicial.
     * @param numInicial BigDecimal
     */
    public void setNumInicial(BigDecimal numInicial) {
        this.numInicial = numInicial;
    }

    /**
     * Numero final.
     * @return numFinal
     */
    public BigDecimal getNumFinal() {
        return numFinal;
    }

    /**
     * Numero final.
     * @param numFinal BigDecimal
     */
    public void setNumFinal(BigDecimal numFinal) {
        this.numFinal = numFinal;
    }

}
