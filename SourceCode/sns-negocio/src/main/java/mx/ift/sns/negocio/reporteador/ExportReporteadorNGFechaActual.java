package mx.ift.sns.negocio.reporteador;

import java.util.Arrays;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.reporteador.ElementoAgrupador;
import mx.ift.sns.modelo.reporteador.NGReporteador;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xslf.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Genera un fichero Excel con la información del reporte sobre el Numeraciones Asignadas/Activas a la fecha actual. */
public class ExportReporteadorNGFechaActual {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportReporteadorNGFechaActual.class);

    /** Cabeceras del Excel de Numeraciones Asignadas/Activas a la fecha actual. */
    /** Cabecera de campos por los que se realiza el filtrado/agrupación. */
    private String[] textosCabeceraFiltrado = new String[10];
    /** Cabecera de campos con los datos de asignación y activos. */
    private String[] textosCabeceraDatos = new String[20];

    /** Lista de Series del Histórico. */
    private List<NGReporteador> listaHistoricoSerie;

    /** Filtro de campos. */
    private FiltroReporteadorNG filtroReporteadorNG;

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /** Variables con los % de activas sobre asignadas. */
    /** Variable % de activas sobre el total de asignadas. */
    private Float porcentajeActivasTotal;
    /** Variable % de activas fijas sobre asignadas fijas. */
    private Float porcentajeActivasFijas;
    /** Variable % de activas móviles sobre asignadas móviles. */
    private Float porcentajeActivasMoviles;
    /** Variable % de activas móviles CPP sobre asignadas móviles CPP. */
    private Float porcentajeActivasMovilesCPP;
    /** Variable % de activas móviles MPP sobre asignadas móviles MPP. */
    private Float porcentajeActivasMovilesMPP;

    /**
     * Constructor.
     * @param pHistoricoSeries Lista de Series del histórico.
     * @param pFiltro valores por los que se filtra
     */
    public ExportReporteadorNGFechaActual(List<NGReporteador> pHistoricoSeries, FiltroReporteadorNG pFiltro) {
        listaHistoricoSerie = pHistoricoSeries;
        filtroReporteadorNG = pFiltro;
        workBook = new SXSSFWorkbook(5000);
        workBook.setCompressTempFiles(true);
    }

    /**
     * Objeto Hoja de cálculo.
     * @return workBook Libro de hojas de cálculo
     */
    public SXSSFWorkbook getLibroExcel() {
        return workBook;
    }

    /**
     * Método que efectúa la carga de información en el objeto de la hoja de cálculo.
     * @param hoja objeto a cargar con la información filtrada
     * @return hoja objeto con la información cargada
     */
    public Sheet generarContenidoAsignadosActivosActual(Sheet hoja) {
        Cell celda = null;

        hoja.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
        hoja.addMergedRegion(new CellRangeAddress(2, 3, 1, 1));
        hoja.addMergedRegion(new CellRangeAddress(2, 3, 2, 2));
        hoja.addMergedRegion(new CellRangeAddress(2, 3, 3, 3));
        hoja.addMergedRegion(new CellRangeAddress(2, 3, 4, 4));
        hoja.addMergedRegion(new CellRangeAddress(2, 2, 5, 9));
        hoja.addMergedRegion(new CellRangeAddress(2, 2, 10, 19));

        CellStyle estiloBlue = crearEstilo(HSSFColor.PALE_BLUE.index, true, this.workBook);
        CellStyle estiloGrey = crearEstilo(HSSFColor.GREY_25_PERCENT.index, true, this.workBook);

        int contFila = 0;
        int cont = 0;
        // Título e información del filtrado
        Row linea = hoja.createRow(contFila++);
        celda = linea.createCell(cont);
        String s = "Numeraciones Geográficas Asignadas/Activas a la fecha actual "
                + FechasUtils.fechaToString(FechasUtils.getFechaHoy(), "dd-MM-yyyy");
        if (filtroReporteadorNG.getPrimeraAgrupacion() != null) {
            s += " y agrupadas por " + filtroReporteadorNG.getPrimeraAgrupacion().getDescripcion();
            if (filtroReporteadorNG.getSegundaAgrupacion() != null) {
                s += " y " + filtroReporteadorNG.getSegundaAgrupacion().getDescripcion();
            }
        }
        celda.setCellValue(s);

        linea = hoja.createRow(contFila++);
        celda = linea.createCell(cont);
        celda.setCellValue(getTextFiltros(filtroReporteadorNG));

        // Cabeceras de las columnas
        cont = 0;
        linea = hoja.createRow(contFila++);
        textosCabeceraFiltrado = getTextosCabeceraFiltrado(filtroReporteadorNG);
        for (int i = 0; i < textosCabeceraFiltrado.length; i++) {
            if (null != textosCabeceraFiltrado[i]) {
                celda = linea.createCell(cont++);
                celda.setCellValue(textosCabeceraFiltrado[i]);
                celda.setCellStyle(estiloGrey);
            }
        }

        celda = linea.createCell(cont++);
        celda.setCellValue("Asignados");
        celda.setCellStyle(estiloBlue);

        for (int i = 0; i < 4; i++) {
            celda = linea.createCell(cont++);
            celda.setCellValue("");
            celda.setCellStyle(estiloBlue);
        }

        celda = linea.createCell(cont++);
        celda.setCellValue("Activos");
        celda.setCellStyle(estiloBlue);
        for (int i = 0; i < 9; i++) {
            celda = linea.createCell(cont++);
            celda.setCellValue("");
            celda.setCellStyle(estiloBlue);
        }

        cont = 0;
        linea = hoja.createRow(contFila++);
        textosCabeceraDatos = getTextosCabeceraDatos();
        for (int i = 0; i < textosCabeceraDatos.length; i++) {
            if (null != textosCabeceraDatos[i]) {
                celda = linea.createCell(cont++);
                celda.setCellValue(textosCabeceraDatos[i]);
                celda.setCellStyle(estiloGrey);
            }
        }

        for (NGReporteador elemento : listaHistoricoSerie) {
            // Nueva Fila
            cont = 0;
            linea = hoja.createRow(contFila++);

            if (filtroReporteadorNG.getPrimeraAgrupacion() != null) {
                celda = linea.createCell(cont++);
                celda.setCellValue((String) getValorElementoAgrupacion(
                        filtroReporteadorNG.getPrimeraAgrupacion().getCodigo(), elemento).toString());
                if (filtroReporteadorNG.getSegundaAgrupacion() != null) {
                    celda = linea.createCell(cont++);
                    celda.setCellValue((String) getValorElementoAgrupacion(
                            filtroReporteadorNG.getSegundaAgrupacion().getCodigo(), elemento).toString());

                    if (!ElementoAgrupador.COD_PST.equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion()
                            .getCodigo())
                            && !ElementoAgrupador.COD_PST.equalsIgnoreCase(filtroReporteadorNG.getSegundaAgrupacion()
                                    .getCodigo())) {
                        celda = linea.createCell(cont++);
                        if (elemento.getPst() != null) {
                            celda.setCellValue((String) elemento.getPst().toString());
                        } else {
                            celda.setCellValue("");
                        }
                    }
                    if (!ElementoAgrupador.COD_ESTADO.equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion()
                            .getCodigo())
                            && !ElementoAgrupador.COD_ESTADO.equalsIgnoreCase(filtroReporteadorNG.getSegundaAgrupacion()
                                    .getCodigo())) {
                        celda = linea.createCell(cont++);
                        if (elemento.getEstado() != null) {
                            celda.setCellValue((String) elemento.getEstado().toString());
                        } else {
                            celda.setCellValue("");
                        }
                    }
                    if (!ElementoAgrupador.COD_MUNICIPIO
                            .equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion().getCodigo())
                            && !ElementoAgrupador.COD_MUNICIPIO.equalsIgnoreCase(filtroReporteadorNG
                                    .getSegundaAgrupacion()
                                    .getCodigo())) {
                        celda = linea.createCell(cont++);
                        if (elemento.getMunicipio() != null) {
                            celda.setCellValue((String) elemento.getMunicipio().toString());
                        } else {
                            celda.setCellValue("");
                        }
                    }
                    if (!ElementoAgrupador.COD_POBLACION
                            .equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion().getCodigo())
                            && !ElementoAgrupador.COD_POBLACION.equalsIgnoreCase(filtroReporteadorNG
                                    .getSegundaAgrupacion()
                                    .getCodigo())) {
                        celda = linea.createCell(cont++);
                        if (elemento.getDescPoblacion() != null) {
                            celda.setCellValue((String) elemento.getDescPoblacion().toString());
                        } else {
                            celda.setCellValue("");
                        }
                    }
                    if (!ElementoAgrupador.COD_ABN.equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion()
                            .getCodigo())
                            && !ElementoAgrupador.COD_ABN.equalsIgnoreCase(filtroReporteadorNG.getSegundaAgrupacion()
                                    .getCodigo())) {
                        celda = linea.createCell(cont++);
                        if (elemento.getAbn() != null) {
                            celda.setCellValue((String) elemento.getAbn().toString());
                        } else {
                            celda.setCellValue("");
                        }
                    }
                } else {
                    if (!ElementoAgrupador.COD_PST.equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion()
                            .getCodigo())) {
                        celda = linea.createCell(cont++);
                        if (elemento.getPst() != null) {
                            celda.setCellValue((String) elemento.getPst().toString());
                        } else {
                            celda.setCellValue("");
                        }
                    }
                    if (!ElementoAgrupador.COD_ESTADO.equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion()
                            .getCodigo())) {
                        celda = linea.createCell(cont++);
                        if (elemento.getEstado() != null) {
                            celda.setCellValue((String) elemento.getEstado().toString());
                        } else {
                            celda.setCellValue("");
                        }
                    }
                    if (!ElementoAgrupador.COD_MUNICIPIO.equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion()
                            .getCodigo())) {
                        celda = linea.createCell(cont++);
                        if (elemento.getMunicipio() != null) {
                            celda.setCellValue((String) elemento.getMunicipio().toString());
                        } else {
                            celda.setCellValue("");
                        }
                    }
                    if (!ElementoAgrupador.COD_POBLACION.equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion()
                            .getCodigo())) {
                        celda = linea.createCell(cont++);
                        if (elemento.getDescPoblacion() != null) {
                            celda.setCellValue((String) elemento.getDescPoblacion().toString());
                        } else {
                            celda.setCellValue("");
                        }
                    }
                    if (!ElementoAgrupador.COD_ABN.equalsIgnoreCase(filtroReporteadorNG.getPrimeraAgrupacion()
                            .getCodigo())) {
                        celda = linea.createCell(cont++);
                        if (elemento.getAbn() != null) {
                            celda.setCellValue((String) elemento.getAbn().toString());
                        } else {
                            celda.setCellValue("");
                        }

                    }
                }
            } else {
                for (int i = 0; i < textosCabeceraFiltrado.length; i++) {
                    if (null != textosCabeceraFiltrado[i]) {
                        celda = linea.createCell(cont++);
                        if (textosCabeceraFiltrado[i].equals(ElementoAgrupador.DESC_PST)) {
                            celda.setCellValue(elemento.getPst());
                        }
                        if (textosCabeceraFiltrado[i].equals(ElementoAgrupador.DESC_ESTADO)) {
                            celda.setCellValue(elemento.getEstado());
                        }
                        if (textosCabeceraFiltrado[i].equals(ElementoAgrupador.DESC_MUNICIPIO)) {
                            celda.setCellValue(elemento.getMunicipio());
                        }
                        if (textosCabeceraFiltrado[i].equals(ElementoAgrupador.DESC_POBLACION)) {
                            celda.setCellValue(elemento.getDescPoblacion());
                        }
                        if (textosCabeceraFiltrado[i].equals(ElementoAgrupador.DESC_ABN)) {
                            celda.setCellValue(elemento.getAbn());
                        }
                    }
                }
            }
            // Grupo de Numeraciones Asignadas
            // Total Numeraciones Asignadas
            if (null != elemento.getTotalAsignadas()) {
                celda = linea.createCell(cont++);
                celda.setCellValue((String) elemento.getTotalAsignadas().toString());

            } else {
                cont++;
            }
            // Numeraciones Asignadas Fijas
            if (null != elemento.getAsignadasFijas()) {
                celda = linea.createCell(cont++);
                celda.setCellValue((String) elemento.getAsignadasFijas().toString());
            } else {
                cont++;
            }
            // Numeraciones Asignadas Móviles
            if (null != elemento.getAsignadasMovilesCPP() && null != elemento.getAsignadasMovilesMPP()) {
                elemento.setAsignadasMoviles(elemento.getAsignadasMovilesCPP() + elemento.getAsignadasMovilesMPP());
                celda = linea.createCell(cont++);
                celda.setCellValue((String) elemento.getAsignadasMoviles().toString());
            } else {
                cont++;
            }
            // Numeraciones Asignadas Móviles CPP
            if (null != elemento.getAsignadasMovilesCPP()) {
                celda = linea.createCell(cont++);
                celda.setCellValue((String) elemento.getAsignadasMovilesCPP().toString());
            } else {
                cont++;
            }
            // Numeraciones Asignadas Móviles MPP
            if (null != elemento.getAsignadasMovilesMPP()) {
                celda = linea.createCell(cont++);
                celda.setCellValue((String) elemento.getAsignadasMovilesMPP().toString());
            } else {
                cont++;
            }
            // Total Numeraciones Activas
            if (null != elemento.getTotalActivas()) {
                celda = linea.createCell(cont++);
                celda.setCellValue((String) elemento.getTotalActivas().toString());
            } else {
                cont++;
            }
            // % Total Numeraciones Activas sobre las Asignadas
            porcentajeActivasTotal = new Float(0);
            if (null != elemento.getTotalActivas() && null != elemento.getTotalAsignadas()
                    && elemento.getTotalAsignadas() > 0) {
                porcentajeActivasTotal = (elemento.getTotalActivas().floatValue() / elemento.getTotalAsignadas()
                        .floatValue()) * 100;
                celda = linea.createCell(cont++);
                celda.setCellValue((String) String.format("%.2f", porcentajeActivasTotal));
            } else {
                cont++;
            }
            // Numeraciones Activas Fijas
            if (null != elemento.getActivasFijas()) {
                celda = linea.createCell(cont++);
                celda.setCellValue((String) elemento.getActivasFijas().toString());
            } else {
                cont++;
            }
            // % Numeraciones Activas Fijas sobre las Asignadas Fijas
            porcentajeActivasFijas = new Float(0);
            if (null != elemento.getTotalActivas() && null != elemento.getAsignadasFijas()
                    && elemento.getAsignadasFijas() > 0) {
                porcentajeActivasFijas = (elemento.getActivasFijas().floatValue() / elemento.getAsignadasFijas()
                        .floatValue()) * 100;
                celda = linea.createCell(cont++);
                celda.setCellValue((String) String.format("%.2f", porcentajeActivasFijas));
            } else {
                cont++;
            }
            // Numeraciones Activas Móviles
            if (null != elemento.getActivasMovilesCPP() && null != elemento.getActivasMovilesMPP()) {
                elemento.setActivasMoviles(elemento.getActivasMovilesCPP() + elemento.getActivasMovilesMPP());
                celda = linea.createCell(cont++);
                celda.setCellValue((String) elemento.getActivasMoviles().toString());
            } else {
                cont++;
            }
            // % Numeraciones Activas Móviles sobre las Asignadas Móviles
            porcentajeActivasMoviles = new Float(0);
            if (null != elemento.getActivasMoviles() && null != elemento.getAsignadasMoviles()
                    && elemento.getAsignadasMoviles() > 0) {
                porcentajeActivasMoviles = (elemento.getActivasMoviles().floatValue() / elemento.getAsignadasMoviles()
                        .floatValue()) * 100;
                celda = linea.createCell(cont++);
                celda.setCellValue((String) String.format("%.2f", porcentajeActivasMoviles));
            } else {
                cont++;
            }
            // Numeraciones Activas Móviles CPP
            if (null != elemento.getActivasMovilesCPP()) {
                celda = linea.createCell(cont++);
                celda.setCellValue((String) elemento.getActivasMovilesCPP().toString());
            } else {
                cont++;
            }
            // % Numeraciones Activas Móviles CPP sobre las Asignadas Móviles CPP
            porcentajeActivasMovilesCPP = new Float(0);
            if (null != elemento.getActivasMovilesCPP() && null != elemento.getAsignadasMovilesCPP()
                    && elemento.getAsignadasMovilesCPP() > 0) {
                porcentajeActivasMovilesCPP = (elemento.getActivasMovilesCPP().floatValue() / elemento
                        .getAsignadasMovilesCPP()
                        .floatValue()) * 100;
                celda = linea.createCell(cont++);
                celda.setCellValue((String) String.format("%.2f", porcentajeActivasMovilesCPP));
            } else {
                cont++;
            }
            // Numeraciones Activas Móviles MPP
            if (null != elemento.getActivasMovilesMPP()) {
                celda = linea.createCell(cont++);
                celda.setCellValue((String) elemento.getActivasMovilesMPP().toString());
            } else {
                cont++;
            }
            // % Numeraciones Activas Móviles MPP sobre las Asignadas Móviles MPP
            porcentajeActivasMovilesMPP = new Float(0);
            if (null != elemento.getActivasMovilesMPP() && null != elemento.getAsignadasMovilesMPP()
                    && elemento.getAsignadasMovilesMPP() > 0) {
                porcentajeActivasMovilesMPP = (elemento.getActivasMovilesMPP().floatValue() / elemento
                        .getAsignadasMovilesMPP()
                        .floatValue()) * 100;
                celda = linea.createCell(cont++);
                celda.setCellValue((String) String.format("%.2f", porcentajeActivasMovilesMPP));
            } else {
                cont++;
            }
        }
        return hoja;
    }

    /**
     * Devuelve el valor de elemento de agrupación seleccionado.
     * @param elementoAgrupacion de busqueda del informe del reporteador
     * @param elemento tupla de valores de la vista V_HISTORICO_SERIE cargados en el NGReporteador
     * @return valor del campo seleccionado
     */
    private String getValorElementoAgrupacion(String elementoAgrupacion, NGReporteador elemento) {
        String valor = "";
        if (elementoAgrupacion.equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
            valor = elemento.getPst();
        } else if (elementoAgrupacion.equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
            valor = elemento.getEstado();
        } else if (elementoAgrupacion.equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
            valor = elemento.getMunicipio();
        } else if (elementoAgrupacion.equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
            valor = elemento.getDescPoblacion();
        } else if (elementoAgrupacion.equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
            valor = elemento.getAbn();
        }
        return valor;
    }

    /**
     * Construye la cabecera en función de los elementos de agrupación/filtrado seleccionados.
     * @param filtro de busqueda del informe del reporteador
     * @return cadena con las cadenas de caracteres de las cabeceras
     */
    private String[] getTextosCabeceraFiltrado(FiltroReporteadorNG filtro) {

        int i = 0;
        if (filtro.getPrimeraAgrupacion() != null) {
            textosCabeceraFiltrado[i] = filtro.getPrimeraAgrupacion().getDescripcion();
            if (filtro.getSegundaAgrupacion() != null) {
                i++;
                textosCabeceraFiltrado[i] = filtro.getSegundaAgrupacion().getDescripcion();
                if (!ElementoAgrupador.COD_PST.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())
                        && !ElementoAgrupador.COD_PST.equalsIgnoreCase(filtro.getSegundaAgrupacion().getCodigo())) {
                    i++;
                    textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_PST;
                }
                if (!ElementoAgrupador.COD_ESTADO.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())
                        && !ElementoAgrupador.COD_ESTADO.equalsIgnoreCase(filtro.getSegundaAgrupacion().getCodigo())) {
                    i++;
                    textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_ESTADO;
                }
                if (!ElementoAgrupador.COD_MUNICIPIO.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())
                        && !ElementoAgrupador.COD_MUNICIPIO.equalsIgnoreCase(
                                filtro.getSegundaAgrupacion().getCodigo())) {
                    i++;
                    textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_MUNICIPIO;
                }
                if (!ElementoAgrupador.COD_POBLACION.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())
                        && !ElementoAgrupador.COD_POBLACION.equalsIgnoreCase(
                                filtro.getSegundaAgrupacion().getCodigo())) {
                    i++;
                    textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_POBLACION;
                }
                if (!ElementoAgrupador.COD_ABN.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())
                        && !ElementoAgrupador.COD_ABN.equalsIgnoreCase(filtro.getSegundaAgrupacion().getCodigo())) {
                    i++;
                    textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_ABN;
                }
            } else {
                if (!ElementoAgrupador.COD_PST.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())) {
                    i++;
                    textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_PST;
                }
                if (!ElementoAgrupador.COD_ESTADO.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())) {
                    i++;
                    textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_ESTADO;
                }
                if (!ElementoAgrupador.COD_MUNICIPIO.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())) {
                    i++;
                    textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_MUNICIPIO;
                }
                if (!ElementoAgrupador.COD_POBLACION.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())) {
                    i++;
                    textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_POBLACION;
                }
                if (!ElementoAgrupador.COD_ABN.equalsIgnoreCase(filtro.getPrimeraAgrupacion().getCodigo())) {
                    i++;
                    textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_ABN;
                }
            }
        } else {
            if (filtro.getPstSeleccionada() != null) {
                textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_PST;
                i++;
            }
            if (filtro.getEstadoSeleccionado() != null) {
                textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_ESTADO;
                i++;
            }
            if (filtro.getMunicipioSeleccionado() != null) {
                textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_MUNICIPIO;
                i++;
            }
            if (filtro.getPoblacionSeleccionada() != null) {
                textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_POBLACION;
                i++;
            }
            if (filtro.getAbnSeleccionado() != null) {
                textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_ABN;
                i++;
            }
            if (!Arrays.asList(textosCabeceraFiltrado).contains(ElementoAgrupador.DESC_PST)) {
                textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_PST;
                i++;
            }
            if (!Arrays.asList(textosCabeceraFiltrado).contains(ElementoAgrupador.DESC_ESTADO)) {
                textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_ESTADO;
                i++;
            }
            if (!Arrays.asList(textosCabeceraFiltrado).contains(ElementoAgrupador.DESC_MUNICIPIO)) {
                textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_MUNICIPIO;
                i++;
            }
            if (!Arrays.asList(textosCabeceraFiltrado).contains(ElementoAgrupador.DESC_POBLACION)) {
                textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_POBLACION;
                i++;
            }
            if (!Arrays.asList(textosCabeceraFiltrado).contains(ElementoAgrupador.DESC_ABN)) {
                textosCabeceraFiltrado[i] = ElementoAgrupador.DESC_ABN;
            }
        }
        return textosCabeceraFiltrado;
    }

    /**
     * Construye la cabecera de los datos a mostrar.
     * @return cadena con las cadenas de caracteres de las cabeceras
     */
    private String[] getTextosCabeceraDatos() {

        int i = 0;

        textosCabeceraDatos[i] = "";
        i++;
        textosCabeceraDatos[i] = "";
        i++;
        textosCabeceraDatos[i] = "";
        i++;
        textosCabeceraDatos[i] = "";
        i++;
        textosCabeceraDatos[i] = "";
        i++;
        textosCabeceraDatos[i] = "Total";
        i++;
        textosCabeceraDatos[i] = "Fijo";
        i++;
        textosCabeceraDatos[i] = "Móvil";
        i++;
        textosCabeceraDatos[i] = "CPP";
        i++;
        textosCabeceraDatos[i] = "MPP";
        i++;
        textosCabeceraDatos[i] = "Total";
        i++;
        textosCabeceraDatos[i] = "% Total";
        i++;
        textosCabeceraDatos[i] = "Fijos";
        i++;
        textosCabeceraDatos[i] = "% Fijos";
        i++;
        textosCabeceraDatos[i] = "Móvil";
        i++;
        textosCabeceraDatos[i] = "% Móvil";
        i++;
        textosCabeceraDatos[i] = "CPP";
        i++;
        textosCabeceraDatos[i] = "% CPP";
        i++;
        textosCabeceraDatos[i] = "MPP";
        i++;
        textosCabeceraDatos[i] = "% MPP";

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("agrupador '{}'", textosCabeceraDatos[0]);
        }
        return textosCabeceraDatos;
    }

    /**
     * Genera un stilo del excel.
     * @param color numero de Color
     * @param marcarBordes bordes en negrita
     * @param workbook hoja
     * @return CellStyle estilo
     */
    private CellStyle crearEstilo(short color, boolean marcarBordes, SXSSFWorkbook workbook) {

        CellStyle estilo = workBook.createCellStyle();
        estilo.setAlignment(CellStyle.ALIGN_CENTER);
        estilo.setVerticalAlignment((short) VerticalAlignment.MIDDLE.ordinal());
        estilo.setFillForegroundColor(color);
        if (marcarBordes) {
            estilo.setFillPattern(CellStyle.SOLID_FOREGROUND);
            estilo.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
            estilo.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
            estilo.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
            estilo.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
        }
        return estilo;
    }

    /**
     * Evalúa la existencia de algun filtro.
     * @param filtro filtro
     * @return String texto
     */
    private String getTextFiltros(FiltroReporteadorNG filtro) {
        String textoCelda = "";
        if (null != filtro.getPstSeleccionada()) {
            textoCelda = textoCelda + (" PST: " + filtro.getPstSeleccionada().getNombre());
        }
        if (null != filtro.getEstadoSeleccionado()) {
            textoCelda = textoCelda + (" Estado: " + filtro.getEstadoSeleccionado().getNombre());
        }
        if (null != filtro.getMunicipioSeleccionado()) {
            textoCelda = textoCelda + (" Municipio: " + filtro.getMunicipioSeleccionado().getNombre());
        }
        if (null != filtro.getPoblacionSeleccionada()) {
            textoCelda = textoCelda + (" Poblacion: " + filtro.getPoblacionSeleccionada().getNombre());
        }
        if (null != filtro.getAbnSeleccionado()) {
            textoCelda = textoCelda + (" ABN: " + filtro.getAbnSeleccionado());
        }

        if (textoCelda.length() > 0) {
            textoCelda = "Para" + textoCelda;
        }
        return textoCelda;
    }
}
