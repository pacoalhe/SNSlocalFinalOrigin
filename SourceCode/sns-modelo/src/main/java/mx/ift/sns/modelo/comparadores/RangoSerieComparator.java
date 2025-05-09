package mx.ift.sns.modelo.comparadores;

import java.util.Comparator;

import mx.ift.sns.modelo.series.IRangoSerie;

/**
 * Comparador de clases RangoSerie en función de su Número Inicial.
 */
public class RangoSerieComparator implements Comparator<IRangoSerie> {

    @Override
    public int compare(IRangoSerie pRangoA, IRangoSerie pRangoB) {
        try {
            return (pRangoB.getNumInicioAsInt() - pRangoA.getNumInicioAsInt());
        } catch (Exception ex) {
            return -1;
        }
    }
}
