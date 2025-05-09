package mx.ift.sns.negocio.ng.arrendamientos;

import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.reporteabd.SerieArrendada;
import mx.ift.sns.negocio.ng.ISeriesService;
import mx.ift.sns.negocio.ng.model.ComparacionRangoError;
import mx.ift.sns.negocio.ng.model.RangoNoAsignadoError;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.num.INumeracionService;
import mx.ift.sns.negocio.num.model.Numero;
import mx.ift.sns.negocio.utils.csv.BuscadorArchivoCSV;
import mx.ift.sns.negocio.utils.csv.ValidadorArchivoCSV;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Valida el fichero de arrendador contra el arrendatario.
 */
public class ValidadorArchivoArrendadorVsArrendatario extends ValidadorArchivoCSV {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorArchivoArrendador.class);

    /** Fichero del arrendatario contra el que validamos. */
    private String fileArrendatario;

    /** Servicio series. */
    private ISeriesService seriesService;

    /** Servicio series. */
    private INumeracionService numeracionService;

    /** cadena numero inicial. */
    private String numeroInicialS;

    /** cadena numero final. */
    private String numeroFinalS;

    /** ido arrendador. */
    private String idoArrendador;

    /** ido arrendatario. */
    private String idoArrendatario;

    /** numero inicial. */
    private Numero numeroInicial = null;

    /** numero final. */
    private Numero numeroFinal = null;

    /**
     * Comrpueba si los valores de las filas coinciden.
     * @param valores1 valores fila fichero arrendatador
     * @param valores2 valroes fila fichero arrendatario
     * @return true si coinciden
     */
    private boolean comprobarFilas(String[] valores1, String[] valores2) {

        boolean res = false;

        String numeroInicial1 = StringUtils.trim(valores1[ValidadorArchivoArrendador.CAMPO_NUMBER_FROM]);
        String numeroFinal1 = StringUtils.trim(valores1[ValidadorArchivoArrendador.CAMPO_NUMBER_TO]);
        String idoArrendador1 = StringUtils.trim(valores1[ValidadorArchivoArrendador.CAMPO_IDO_ARRENDADOR]);
        String idoArrendatario1 = StringUtils.trim(valores1[ValidadorArchivoArrendador.CAMPO_IDO_ARRENDATARIO]);

        String numeroInicial2 = StringUtils.trim(valores2[ValidadorArchivoArrendatario.CAMPO_NUMBER_FROM]);
        String numeroFinal2 = StringUtils.trim(valores2[ValidadorArchivoArrendatario.CAMPO_NUMBER_TO]);
        String idoArrendatario2 = StringUtils.trim(valores2[ValidadorArchivoArrendatario.CAMPO_IDO_ARRENDATARIO]);
        String idoArrendador2 = StringUtils.trim(valores2[ValidadorArchivoArrendatario.CAMPO_IDO_ARRENDADOR]);

        if (StringUtils.equalsIgnoreCase(numeroInicial1, numeroInicial2)
                && StringUtils.equalsIgnoreCase(numeroFinal1, numeroFinal2)
                && StringUtils.equalsIgnoreCase(idoArrendatario1, idoArrendatario2)
                && (StringUtils.equalsIgnoreCase(idoArrendador1, idoArrendador2))) {
            res = true;
        }

        return res;
    }

    /**
     * Añade la serie a la tabla.
     */
    private void addSerieArrendada() {

        SerieArrendada serie = new SerieArrendada();

        // serie.setConcesionario(numeroInicial.getRango().getAsignatario());
        // serie.setArrendatario(numeroInicial.getRango().getArrendatario());
        //
        // serie.getId().setIdPstConcesionario(numeroInicial.getRango().getAsignatario().getId());
        // serie.getId().setIdPstArrendatario(numeroInicial.getRango().getArrendatario().getId());
        serie.getId().setNumberFrom(numeroInicial.getNumero());
        serie.getId().setNumberTo(numeroFinal.getNumero());

        LOGGER.debug("add serie {}", serie);

        seriesService.create(serie);
    }

    /**
     * Parsea el numero sin dar excepcion.
     * @param num numero
     * @return numero parseado
     */
    private Numero parseNumeracion(String num) {
        Numero n = null;
        try {
            n = numeracionService.parseNumeracion(num);
        } catch (Exception e) {
            n = null;
        }

        return n;
    }

    /**
     * Añade un error de rango.
     * @param rangoPnn rango del plan
     * @param descripcion del error
     */
    private void addErrorRango(RangoSerie rangoPnn, String descripcion) {

        LOGGER.debug("");

        RangoNoAsignadoError error = new RangoNoAsignadoError();

        error.setNumeroInicial1(numeroInicialS);
        error.setNumeroFinal1(numeroFinalS);
        error.setIdo1(idoArrendador);

        if (numeroInicial != null) {
            if (numeroInicial.getCodigoNir() != null) {
                error.setNir1("" + numeroInicial.getCodigoNir());
            }

            if (numeroInicial.getSna() != null) {
                error.setSna1("" + numeroInicial.getSna());
            }

            if (numeroInicial.getRango() != null) {
                error.setPst1(numeroInicial.getRango().getAsignatario().getNombre());

            }

            if (numeroInicial.existe()) {
                error.setNumeroInicial1(numeroInicial.getNumeroInterno());
            }
        }

        if (numeroFinal != null) {
            if (numeroFinal.existe()) {
                error.setNumeroFinal1(numeroFinal.getNumeroInterno());
            }
        }

        if (rangoPnn != null) {
            if (rangoPnn.getIdoPnn() != null) {
                error.setIdo0(rangoPnn.getIdoPnn().toString());
            }

            if (rangoPnn.getSerie() != null) {
                error.setNir0("" + rangoPnn.getSerie().getNir().getCodigo());
            }

            error.setNumeroInicial0(rangoPnn.getNumInicio());
            error.setNumeroFinal0(rangoPnn.getNumFinal());
            error.setPst0(rangoPnn.getAsignatario().getNombre());

            if (rangoPnn.getId().getSna() != null) {
                error.setSna0(rangoPnn.getId().getSna().toString());
            }

            if (rangoPnn.getTipoRed() != null) {
                error.setTipoRed0(rangoPnn.getTipoRed().getCdg());
            }
        }

        error.setDescripcion(descripcion);

        ((ResultadoValidacionArrendamiento) getRes()).add(error);
    }

    /**
     * no se encuentra la fila en el fichero de arrendatario.
     */
    private void addErrorNoExisteEnArrendatario() {

        LOGGER.debug("no se encuentra");

        ComparacionRangoError error = new ComparacionRangoError();
        error.setIdoArrendador(idoArrendador);
        error.setIdoArrendatario(idoArrendatario);
        error.setNumeroFromArrendador(numeroInicialS);
        error.setNumeroToArrendador(numeroFinalS);

        Numero n = parseNumeracion(numeroInicialS);
        if ((n != null) && (n.getRango() != null)) {
            error.setArrendador(n.getRango().getAsignatario().getNombre());
        }

        ResultadoValidacionArrendamiento r = (ResultadoValidacionArrendamiento) getRes();
        r.add(error);
    }

    /**
     * Se ha encontrado la fila pero no coinciden.
     * @param valores del fichero de arrendatario
     */
    private void addErrorNoCoinciden(String[] valores) {
        //
        // Se ha encontrado la fila pero no coinciden
        //
        ComparacionRangoError error = new ComparacionRangoError();

        error.setIdoArrendador(idoArrendador);
        error.setNumeroFromArrendador(numeroInicialS);
        error.setNumeroToArrendador(numeroFinalS);

        Numero n = parseNumeracion(numeroInicialS);
        if ((n != null) && (n.getRango() != null)) {
            error.setArrendador(n.getRango().getAsignatario().getNombre());
        }

        error.setIdoArrendatario(valores[ValidadorArchivoArrendatario.CAMPO_IDO_ARRENDATARIO]);
        error.setNumeroFromArrendatario(valores[ValidadorArchivoArrendatario.CAMPO_NUMBER_FROM]);
        error.setNumeroToArrendatario(valores[ValidadorArchivoArrendatario.CAMPO_NUMBER_TO]);

        Numero n2 = parseNumeracion(valores[ValidadorArchivoArrendatario.CAMPO_NUMBER_FROM]);
        if ((n2 != null) && (n2.getRango() != null)) {
            error.setArrendatario(n.getRango().getAsignatario().getNombre());
        }

        ResultadoValidacionArrendamiento r = (ResultadoValidacionArrendamiento) getRes();
        r.add(error);
    }

    /**
     * Valida una fila del archivo.
     * @param valores d
     */
    public void checkFila(String[] valores) {

        if (valores.length >= 1) {

            numeroInicialS = StringUtils.trim(valores[ValidadorArchivoArrendador.CAMPO_NUMBER_FROM]);
            numeroFinalS = StringUtils.trim(valores[ValidadorArchivoArrendador.CAMPO_NUMBER_TO]);
            idoArrendador = StringUtils.trim(valores[ValidadorArchivoArrendador.CAMPO_IDO_ARRENDADOR]);
            idoArrendatario = StringUtils.trim(valores[ValidadorArchivoArrendador.CAMPO_IDO_ARRENDATARIO]);

            BuscadorArchivoCSV buscador = new BuscadorArchivoCSV();
            buscador.setSearch(numeroInicialS);
            try {
                buscador.validar(fileArrendatario);

                if (buscador.isEncontrado()) {
                    if (comprobarFilas(valores, buscador.getFila())) {

                        numeroInicial = parseNumeracion(numeroInicialS);
                        numeroFinal = parseNumeracion(numeroFinalS);

                        if ((numeroInicial == null || (numeroFinal == null)
                                || (numeroInicial.getRango() == null) || (numeroFinal.getRango() == null))) {
                            /* numeracion no valida. */
                            LOGGER.debug("numeracion no valida ");
                            addErrorRango(null, RangoNoAsignadoError.ERROR_1);
                        } else if (!seriesService.existeNumeracion(numeroInicial)
                                || !seriesService.existeNumeracion(numeroFinal)) {
                            LOGGER.debug("numeracion no valida 2");
                            /* numeracion no valida. */
                            addErrorRango(numeroInicial.getRango(), RangoNoAsignadoError.ERROR_1);
                        } else if (numeroInicial.getRango().getAsignatario().getIdo().equals(idoArrendador)) {
                            addErrorRango(numeroInicial.getRango(), RangoNoAsignadoError.ERROR_2);
                        } else if (numeroInicial.getRango().getArrendatario() == null) {
                            addErrorRango(numeroInicial.getRango(), RangoNoAsignadoError.ERROR_4);
                        } else {
                            addSerieArrendada();
                        }
                    } else {
                        //
                        // Se ha encontrado la fila pero no coinciden
                        //
                        addErrorNoCoinciden(buscador.getFila());
                    }
                } else {
                    //
                    // no se encuentra en el fichero de arrendatario
                    // discrepancia
                    addErrorNoExisteEnArrendatario();
                }
            } catch (Exception e) {
                LOGGER.error("error", e);
            }
        }
    }

    /**
     * @param fileArrendatario the fileArrendatario to set
     */
    public void setFileArrendatario(String fileArrendatario) {
        this.fileArrendatario = fileArrendatario;
    }

    /**
     * @param seriesService the seriesService to set
     */
    public void setSeriesService(ISeriesService seriesService) {
        this.seriesService = seriesService;
    }

    /**
     * @param numeracionService the numeracionService to set
     */
    public void setNumeracionService(INumeracionService numeracionService) {
        this.numeracionService = numeracionService;
    }
}
