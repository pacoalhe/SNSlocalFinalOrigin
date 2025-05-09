package mx.ift.sns.negocio.ng.model;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.reporteabd.SerieArrendada;

/**
 * Resultado de la validacion del arrendamiento.
 */
public class ResultadoValidacionArrendamiento extends ResultadoValidacionCSV {

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** Fichero ok. */
    public static final int VALIDACION_OK = 0;

    /** Error accediendo al fichero. */
    public static final int ERROR_FICHERO = 1;

    /** Error accediendo al fichero. */
    public static final String ERROR_FICHERO_TXT = "El fichero no existe.";

    /** Error en cabecera del fichero. */
    public static final int ERROR_CABECERA = 2;

    /** Error en cabecera del fichero. */
    public static final String ERROR_CABECERA_TXT = "La cabecera es incorrecta.";

    /** Error accediendo al fichero. */
    public static final int ERROR_FICHERO_VACIO = 1;

    /** Error accediendo al fichero. */
    public static final String ERROR_FICHERO_VACIO_TXT = "El Fichero está vacío.";

    /** Error en la validacion. */
    private int errorArrendatario;

    /** Error en la validacion. */
    private int errorArrendador;

    /** Error en la validacion. */
    private String errorArrendatarioTxt;

    /** Error en la validacion. */
    private String errorArrendadorTxt;

    /** Lista de rangos no asignados al arrendatario. */
    private List<RangoNoAsignadoError> listaRangosNoAsignados;

    /** */
    private List<ComparacionRangoError> listaErroresComparacion;

    /** Lista de errores de formato. */
    private List<FormatoError> listaErroresFormato;;

    /** */
    private int escenario;

    /** */
    private List<RangoSerie> rangosEscenario;

    /** */
    private List<SerieArrendada> seriesPreparadas;

    /**
     * Constructor por defecto.
     */
    public ResultadoValidacionArrendamiento() {
        errorArrendatario = VALIDACION_OK;
        errorArrendador = VALIDACION_OK;
        listaRangosNoAsignados = new ArrayList<RangoNoAsignadoError>();
        listaErroresComparacion = new ArrayList<ComparacionRangoError>();
        seriesPreparadas = new ArrayList<SerieArrendada>();
    }

    /**
     * @return the errorArrendatario
     */
    public int getErrorArrendatario() {
        return errorArrendatario;
    }

    /**
     * @param errorArrendatario the errorArrendatario to set
     */
    public void setErrorArrendatario(int errorArrendatario) {
        this.errorArrendatario = errorArrendatario;
    }

    /**
     * @return the errorArrendador
     */
    public int getErrorArrendador() {
        return errorArrendador;
    }

    /**
     * @param errorArrendador the errorArrendador to set
     */
    public void setErrorArrendador(int errorArrendador) {
        this.errorArrendador = errorArrendador;
    }

    /**
     * @return hay error
     */
    public boolean isError() {
        return (errorArrendador != VALIDACION_OK) || (errorArrendatario != VALIDACION_OK);
    }

    /**
     * Añade un error.
     * @param error ha añadir
     */
    public void add(RangoNoAsignadoError error) {
        listaRangosNoAsignados.add(error);
    }

    /**
     * @return the listaRangosNoAsignados
     */
    public List<RangoNoAsignadoError> getListaRangosNoAsignados() {
        return listaRangosNoAsignados;
    }

    /**
     * @param listaRangosNoAsignados the listaRangosNoAsignados to set
     */
    public void setListaRangosNoAsignados(List<RangoNoAsignadoError> listaRangosNoAsignados) {
        this.listaRangosNoAsignados = listaRangosNoAsignados;
    }

    /**
     * @return the listaErroresComparacion
     */
    public List<ComparacionRangoError> getListaErroresComparacion() {
        return listaErroresComparacion;
    }

    /**
     * @param listaErroresComparacion the listaErroresComparacion to set
     */
    public void setListaErroresComparacion(List<ComparacionRangoError> listaErroresComparacion) {
        this.listaErroresComparacion = listaErroresComparacion;
    }

    /**
     * Añade un error.
     * @param error ha añadir
     */
    public void add(ComparacionRangoError error) {
        listaErroresComparacion.add(error);
    }

    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();

        b.append("error arrendatario=");
        b.append(errorArrendatario);

        b.append(" error arrendador=");
        b.append(errorArrendador);

        b.append(" #rangos nos asigandos=");
        b.append(listaRangosNoAsignados.size());

        b.append(" #errores comparacion=");
        b.append(listaErroresComparacion.size());

        return b.toString();
    }

    /**
     * @return the escenario
     */
    public int getEscenario() {
        return escenario;
    }

    /**
     * @param escenario the escenario to set
     */
    public void setEscenario(int escenario) {
        this.escenario = escenario;
    }

    /**
     * @return the rangosEscenario
     */
    public List<RangoSerie> getRangosEscenario() {
        return rangosEscenario;
    }

    /**
     * @param rangosEscenario the rangosEscenario to set
     */
    public void setRangosEscenario(List<RangoSerie> rangosEscenario) {
        this.rangosEscenario = rangosEscenario;
    }

    /**
     * @return the seriesPreparadas
     */
    public List<SerieArrendada> getSeriesPreparadas() {
        return seriesPreparadas;
    }

    /**
     * @param seriesPreparadas the seriesPreparadas to set
     */
    public void setSeriesPreparadas(List<SerieArrendada> seriesPreparadas) {
        this.seriesPreparadas = seriesPreparadas;
    }

    /**
     * @return the errorArrendatarioTxt
     */
    public String getErrorArrendatarioTxt() {
        return errorArrendatarioTxt;
    }

    /**
     * @param errorArrendatarioTxt the errorArrendatarioTxt to set
     */
    public void setErrorArrendatarioTxt(String errorArrendatarioTxt) {
        this.errorArrendatarioTxt = errorArrendatarioTxt;
    }

    /**
     * @return the errorArrendadorTxt
     */
    public String getErrorArrendadorTxt() {
        return errorArrendadorTxt;
    }

    /**
     * @param errorArrendadorTxt the errorArrendadorTxt to set
     */
    public void setErrorArrendadorTxt(String errorArrendadorTxt) {
        this.errorArrendadorTxt = errorArrendadorTxt;
    }

    /**
     * @return the listaErroresFormato
     */
    public List<FormatoError> getListaErroresFormato() {
        return listaErroresFormato;
    }

    /**
     * @param listaErroresFormato the listaErroresFormato to set
     */
    public void setListaErroresFormato(List<FormatoError> listaErroresFormato) {
        this.listaErroresFormato = listaErroresFormato;
    }

}
