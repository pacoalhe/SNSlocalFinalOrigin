package mx.ift.sns.negocio.ac;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.series.VCatalogoSerie;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información de la consulta sobre el Catálogo de Serie. */
public class ExportConsultaCatalogoSerie implements IExportarExcel {

    /** Lista de Serie. */
    private List<VCatalogoSerie> listaSerie;

    /** Cabecera del Excel de Catálogos de ABN. */
    private String[] textosCabecera = {"ABN", "NIR", "Serie", "Número Inicial",
            "Número Final", "Estatus"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaSerie Lista de series
     */
    public ExportConsultaCatalogoSerie(List<VCatalogoSerie> pListaSerie) {
        listaSerie = pListaSerie;
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

        int contSerie = 0;

        for (VCatalogoSerie vSerie : listaSerie) {
            // Nueva Fila
            contSerie = 0;
            contenidoFila = this.nuevaFila(contenido);

            // ABN
            celda = new ExcelCeldaInfo(vSerie.getIdAbn(), null, null, true);
            contenidoFila[contSerie++] = celda;

            // NIR
            celda = new ExcelCeldaInfo(vSerie.getCdgNir(), null, null, false);
            contenidoFila[contSerie++] = celda;

            // SNA
            celda = new ExcelCeldaInfo(StringUtils.leftPad(vSerie.getIdSna(), (6 - vSerie.getCdgNir().length()), '0'),
                    null, null, false);
            contenidoFila[contSerie++] = celda;

            // Numero Inicial
            celda = new ExcelCeldaInfo("0000", null, null, false);
            contenidoFila[contSerie++] = celda;

            // Numero Final
            celda = new ExcelCeldaInfo("9999", null, null, false);
            contenidoFila[contSerie++] = celda;

            // Estatus
            celda = new ExcelCeldaInfo(vSerie.getNombreEstatus(), null, null, false);
            contenidoFila[contSerie++] = celda;

            contSerie = 0;
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
