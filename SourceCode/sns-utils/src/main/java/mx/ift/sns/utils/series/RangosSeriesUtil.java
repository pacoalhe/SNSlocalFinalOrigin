package mx.ift.sns.utils.series;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mx.ift.sns.modelo.comparadores.RangoSerieComparator;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.IRangoSerie;

import org.apache.commons.lang3.StringUtils;

/**
 * Métodos comunes para el tratamiento de RangosSeries tanto en front como en back-end.
 */
public final class RangosSeriesUtil {

    /** Constructor. */
    private RangosSeriesUtil() {

    }

    /**
     * Indica si la franja de un rango ya esta ocupada por otro rango en las fracciones seleccionadas.
     * @param pRango Rango cuya franja se quiere comprobar si esta ya ocupada por otro rango.
     * @param pFraccionesByRango Listado de Fracciones ordenadas por el id de rango
     * @return True si la franja del rango ya esta ocupada por otra fracción.
     * @throws Exception en caso de error.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean fraccionRangoOcupado(
            IRangoSerie pRango, HashMap pFraccionesByRango) throws Exception {

        boolean ocupado = false;
        if (pFraccionesByRango.get(pRango.getIdentificadorRango()) != null) {
            // Comprobamos si la franja ya está ocupada por otro fraccionamiento solicitado.
            for (IRangoSerie fraccion : (List<IRangoSerie>) pFraccionesByRango.get(pRango.getIdentificadorRango())) {
                if ((pRango.getNumInicioAsInt() <= fraccion.getNumFinalAsInt())
                        && (pRango.getNumInicioAsInt() >= fraccion.getNumInicioAsInt())
                        && (pRango.getNumFinalAsInt() <= fraccion.getNumFinalAsInt())
                        && (pRango.getNumFinalAsInt() >= fraccion.getNumFinalAsInt())) {

                    // La franja del Rango que se intenta fraccionar ya está ocupada por otra fracción.
                    ocupado = true;
                    break;
                }
            }
        }

        return ocupado;
    }

    /**
     * Recupera el rango inicial del que se realizó la fracción entre la lista dada.
     * @param pFraccion Fracción del Rango que se desea obtener
     * @param pListaRangos Lista de Rangos donde buscar el rango inicial de la fracción.
     * @return Rango al que pertenece la fracción
     * @throws Exception en caso de error.
     */
    public static IRangoSerie getRangoInicial(IRangoSerie pFraccion, List<IRangoSerie> pListaRangos) throws Exception {
        IRangoSerie rangoInicial = null;

        if (pFraccion instanceof RangoSerie) {
            // NG: Se tiene en cuenta el NIR
            for (IRangoSerie rango : pListaRangos) {
                if ((((RangoSerie) rango).getId().getIdNir().intValue()
                            == ((RangoSerie) pFraccion).getId().getIdNir().intValue())
                        && (rango.getSna().intValue() == pFraccion.getSna().intValue())
                        && (rango.getNumInicioAsInt() <= pFraccion.getNumInicioAsInt())
                        && (rango.getNumFinalAsInt() >= pFraccion.getNumFinalAsInt())) {
                    rangoInicial = rango;
                    break;
                }
            }
        } else {
            // NNG: No se tiene en cuenta el NIR
            for (IRangoSerie rango : pListaRangos) {
                if ((rango.getSna().intValue() == pFraccion.getSna().intValue())
                        && (rango.getNumInicioAsInt() <= pFraccion.getNumInicioAsInt())
                        && (rango.getNumFinalAsInt() >= pFraccion.getNumFinalAsInt())) {
                    rangoInicial = rango;
                    break;
                }
            }
        }

        return rangoInicial;
    }

    /**
     * Indica si una lista contiene un RangoSerie sin usar los métodos Equals de Object.
     * @param list Lista de objetos RangoSerie
     * @param pRango objeto a comparar
     * @return True si el RangoSerie esta en la lista
     * @throws Exception en caso de error
     */
    public static boolean arrayContains(List<IRangoSerie> list, IRangoSerie pRango) throws Exception {
        for (IRangoSerie rango : list) {
            if (rango.getIdentificadorRango().equals(pRango.getIdentificadorRango())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea la lista de fracciones que se correspoden con el estado final del fraccionamiento de un rango.
     * @param pRangos Rangos solicitados.
     * @param pRangoOriginal Información del rango inicial.
     * @param pEstadoRango Estatus de las fracciones creadas
     * @return Lista de fracciones que componen el rango fragmentado.
     * @throws Exception en caso de error.
     */
    public static List<IRangoSerie> getFracciones(
            List<IRangoSerie> pRangos, IRangoSerie pRangoOriginal, EstadoRango pEstadoRango) throws Exception {

        // Resultado del fraccionamiento del rango
        List<IRangoSerie> resultadoFraccionamiento = new ArrayList<IRangoSerie>();

        // Ordenamos las fracciones por su numeración
        IRangoSerie[] arrayRangos = pRangos.toArray(new IRangoSerie[pRangos.size()]);
        Arrays.sort(arrayRangos, new RangoSerieComparator());
        ArrayList<IRangoSerie> fraccionesOrdenadas = new ArrayList<IRangoSerie>(Arrays.asList(arrayRangos));
        Collections.reverse(fraccionesOrdenadas);

        // Primera fracción. Agregamos una fracción que complete el rango por el pricipio, si procede.
        IRangoSerie primeraFraccion = fraccionesOrdenadas.get(0);
        fraccionesOrdenadas.remove(0);
        if (primeraFraccion.getNumInicioAsInt() != pRangoOriginal.getNumInicioAsInt()) {
            int numFinal = primeraFraccion.getNumInicioAsInt() - 1;
            resultadoFraccionamiento.add(
                    RangosSeriesUtil.crearRangoFicticio(
                            pRangoOriginal.getNumInicioAsInt(),
                            numFinal,
                            pRangoOriginal,
                            pEstadoRango));
        }
        resultadoFraccionamiento.add(primeraFraccion);

        // Fracciones intermedias. Creamos los huecos entre fracciones.
        IRangoSerie fraccionPrevia = primeraFraccion;
        for (IRangoSerie fraccionOcupada : fraccionesOrdenadas) {
            int numFinalPrevio = fraccionPrevia.getNumFinalAsInt();
            int numInicioActual = fraccionOcupada.getNumInicioAsInt();
            if ((numFinalPrevio + 1) != numInicioActual) {
                resultadoFraccionamiento.add(
                        RangosSeriesUtil.crearRangoFicticio(
                                (numFinalPrevio + 1),
                                (numInicioActual - 1),
                                pRangoOriginal,
                                pEstadoRango));
            }
            resultadoFraccionamiento.add(fraccionOcupada);
            fraccionPrevia = fraccionOcupada;
        }

        // Última Fracción. Agregamos la última fracción hasta completar el rango, si procede.
        int numFinalRango = pRangoOriginal.getNumFinalAsInt();
        IRangoSerie ultimaFraccion = resultadoFraccionamiento.get(resultadoFraccionamiento.size() - 1);
        if (ultimaFraccion.getNumFinalAsInt() != numFinalRango) {
            int numFinalPrevio = ultimaFraccion.getNumFinalAsInt();
            resultadoFraccionamiento.add(
                    RangosSeriesUtil.crearRangoFicticio(
                            (numFinalPrevio + 1),
                            numFinalRango,
                            pRangoOriginal,
                            pEstadoRango));
        }

        return resultadoFraccionamiento;
    }

    /**
     * Crea un rango ficticio con la numeración dada en base a un rango existente.
     * @param pNumInicio Incio de Rango
     * @param pNumFinal Fin de Rango
     * @param pRango Información complementaria.
     * @param pEstadoRango Estado de Rango.
     * @return Rango con la información dada
     * @throws Exception en caso de error.
     */
    public static IRangoSerie crearRangoFicticio(int pNumInicio, int pNumFinal,
            IRangoSerie pRango, EstadoRango pEstadoRango) throws Exception {

        // Transformamos las numeraciones a String
        int numSize = 4;
        String numIni = StringUtils.leftPad(String.valueOf(pNumInicio), numSize, '0');
        String numFin = StringUtils.leftPad(String.valueOf(pNumFinal), numSize, '0');

        if (pRango instanceof RangoSerie) {

            RangoSerie rango = (RangoSerie) pRango;
            RangoSerie rangoLibre = (RangoSerie) rango.clone();

            rangoLibre.setEstadoRango(pEstadoRango);
            rangoLibre.setNumSolicitada(rango.getNumSolicitada());
            rangoLibre.setSolicitud(rango.getSolicitud());
            rangoLibre.setNumInicio(numIni);
            rangoLibre.setNumFinal(numFin);

            return rangoLibre;

        } else {
            RangoSerieNng rango = (RangoSerieNng) pRango;
            RangoSerieNng rangoLibre = (RangoSerieNng) rango.clone();

            rangoLibre.setEstatus(pEstadoRango);
            rangoLibre.setSolicitud(rango.getSolicitud());
            rangoLibre.setNumInicio(numIni);
            rangoLibre.setNumFinal(numFin);

            return rangoLibre;
        }
    }

    /**
     * Castea una Lista de IRangoSerie en una lista de RangoSerieNng.
     * @param pListaIRangos Lista de IRangoSerie
     * @return List
     * @throws Exception en caso de error al castear.
     */
    public static List<RangoSerieNng> castListaRangoSerieNNG(List<IRangoSerie> pListaIRangos) throws Exception {
        List<RangoSerieNng> listaCasteada = new ArrayList<RangoSerieNng>(pListaIRangos.size());
        for (IRangoSerie rango : pListaIRangos) {
            listaCasteada.add((RangoSerieNng) rango);
        }
        return listaCasteada;
    }

    /**
     * Castea una Lista de IRangoSerie en una lista de RangoSerie.
     * @param pListaIRangos Lista de IRangoSerie
     * @return List
     * @throws Exception en caso de error al castear.
     */
    public static List<RangoSerie> castListaRangoSerieNG(List<IRangoSerie> pListaIRangos) throws Exception {
        List<RangoSerie> listaCasteada = new ArrayList<RangoSerie>(pListaIRangos.size());
        for (IRangoSerie rango : pListaIRangos) {
            listaCasteada.add((RangoSerie) rango);
        }
        return listaCasteada;
    }
}
