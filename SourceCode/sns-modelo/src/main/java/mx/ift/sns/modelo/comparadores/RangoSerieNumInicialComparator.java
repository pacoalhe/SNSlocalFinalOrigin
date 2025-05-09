package mx.ift.sns.modelo.comparadores;

import java.util.Comparator;

import mx.ift.sns.modelo.ng.RangoSerie;

/**
 * Comparador de clases RangoSerie en función de su Número Inicial.
 */
public class RangoSerieNumInicialComparator implements Comparator<RangoSerie> {

    @Override
    public int compare(RangoSerie pRangoA, RangoSerie pRangoB) {
        if (pRangoA.getNumInicio() != null
                && !pRangoA.getNumInicio().equals("")
                && pRangoB.getNumInicio() != null
                && !pRangoB.getNumInicio().equals("")) {
            return (Integer.valueOf(pRangoB.getNumInicio()) - Integer.valueOf(pRangoA.getNumInicio()));
        } else {
            return -1;
        }
    }
}
