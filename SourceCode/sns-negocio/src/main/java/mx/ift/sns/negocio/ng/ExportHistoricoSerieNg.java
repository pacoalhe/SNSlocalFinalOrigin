package mx.ift.sns.negocio.ng;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.series.HistoricoSerie;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Genera un fichero Excel con la información de la consulta del historico de series NNG. */
public class ExportHistoricoSerieNg implements IExportarExcel {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportHistoricoSerieNg.class);

    /** Lista de historico. */
    private List<HistoricoSerie> listaHistorico;

    /** Cabecera del Excel de Consulta del historico de series NNG. */
    private String[] textosCabecera = {"Consecutivo", "Tipo Trámite", "N.Oficio", "PST", "IDO", "IDA",
            "Fecha Asignación", "ABN", "NIR", "Serie", "N. Inicial", "N. Final", "Tipo de Red", "Modalidad", "Estado",
            "Municipio", "Población", "Clave censal"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaHistorico reporte
     */
    public ExportHistoricoSerieNg(List<HistoricoSerie> pListaHistorico) {

        this.listaHistorico = pListaHistorico;
        workBook = new SXSSFWorkbook(5000);
        workBook.setCompressTempFiles(true);
    }

    @Override
    public SXSSFWorkbook getLibroExcel() {
        return workBook;
    }

    @Override
    public ExcelCabeceraInfo getCabecera() {
        ExcelCabeceraInfo ehi = new ExcelCabeceraInfo();
        ehi.setTitulos(textosCabecera);
        ehi.setCellFont(ExportarExcel.ARIAL_12_BOLD);
        ehi.setCellStyle(ExportarExcel.ESTILO_GENERAL_CENTRADO);
        ehi.setInmovilizarCabecera(true);

        return ehi;
    }

    @Override
    public ArrayList<ExcelCeldaInfo[]> getContenido() {

        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>();
        ExcelCeldaInfo[] contenidoFila = null;
        ExcelCeldaInfo celda = null;

        try {
            for (HistoricoSerie historico : listaHistorico) {
                // Nueva Fila
                int contSerie = 0;
                contenidoFila = this.nuevaFila(contenido);

                celda = new ExcelCeldaInfo(historico.getConsecutivo().toString(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getIdTipoSolicitud(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getNumOficio(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getNombrePst(), ExportarExcel.ESTILO_GENERAL_LEFT, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getIdoAsString(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getIdaAsString(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(FechasUtils.fechaToString(historico.getFechaAsignacion()), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getIdAbn() != null ? historico.getIdAbn().toString() : "", null,
                        null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getCdgNir().toString(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getSnaAsString(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getInicioRango(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getFinRango(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getIdTipoRed(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getIdTipoModalidad(), null, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getEstado(), ExportarExcel.ESTILO_GENERAL_LEFT, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getMunicipio(), ExportarExcel.ESTILO_GENERAL_LEFT, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getPoblacion(), ExportarExcel.ESTILO_GENERAL_LEFT, null, true);
                contenidoFila[contSerie++] = celda;

                celda = new ExcelCeldaInfo(historico.getInegi(), null, null, true);
                contenidoFila[contSerie++] = celda;

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return contenido;
    }

    /**
     * Agrega una nueva fila al contenedor de filas.
     * @param pConteneder Contenedor de filas
     * @return nueva fila para ser editada
     */
    private ExcelCeldaInfo[] nuevaFila(ArrayList<ExcelCeldaInfo[]> pConteneder) {
        ExcelCeldaInfo[] contenidoFila = new ExcelCeldaInfo[textosCabecera.length];
        pConteneder.add(contenidoFila);
        return contenidoFila;
    }

}
