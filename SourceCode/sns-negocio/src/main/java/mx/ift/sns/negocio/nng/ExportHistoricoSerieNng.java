package mx.ift.sns.negocio.nng;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.series.HistoricoSerieNng;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información de la consulta del historico de series NNG. */
public class ExportHistoricoSerieNng implements IExportarExcel {

    /** Lista de historico. */
    private List<HistoricoSerieNng> listaHistorico;

    /** Cabecera del Excel de Consulta del historico de series NNG. */
    private String[] textosCabecera = {"Consecutivo", "Número de Oficio", "Tipo de movimiento", "Fecha de Tramite",
            "Nombre PST", "BCD", "IDA", "Clave de servicio", "Serie", "N. Inicial", "N. Final"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaHistorico reporte
     */
    public ExportHistoricoSerieNng(List<HistoricoSerieNng> pListaHistorico) {

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

        for (HistoricoSerieNng historico : listaHistorico) {
            // Nueva Fila
            int contSerie = 0;
            contenidoFila = this.nuevaFila(contenido);

            celda = new ExcelCeldaInfo(historico.getConsecutivo().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(historico.getNumeroOficio(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(historico.getTipoMovimiento(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(FechasUtils.fechaToString(historico.getFechaTramite()), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(historico.getNombrePst(), ExportarExcel.ESTILO_GENERAL_LEFT, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo( (historico.getPst().getBcd() != null) ? historico.getPst().getBcd().toString() : "", null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo( (historico.getIda() != null) ? historico.getIdaAsString() : historico.getPst().getBcdAsString() , null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(historico.getClaveServicio().toString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(historico.getSerieAsString(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(historico.getNumInicial(), null, null, true);
            contenidoFila[contSerie++] = celda;

            celda = new ExcelCeldaInfo(historico.getNumFinal(), null, null, true);
            contenidoFila[contSerie++] = celda;

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
