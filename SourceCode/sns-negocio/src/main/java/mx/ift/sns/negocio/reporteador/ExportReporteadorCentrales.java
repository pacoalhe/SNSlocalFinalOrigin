package mx.ift.sns.negocio.reporteador;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.central.ReporteCentralVm;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información del reporte de Centrales. */
public class ExportReporteadorCentrales implements IExportarExcel {

    /** Lista de Central. */
    private List<ReporteCentralVm> listaCentral;

    /** Cabecera del Excel de Catálogos de ABN. */
    private String[] textosCabecera = {"PST", "Central", "Abn",
            "Estado", "Municipio", "Población"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaCentral Lista de Central
     */
    public ExportReporteadorCentrales(List<ReporteCentralVm> pListaCentral) {
        listaCentral = pListaCentral;
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

        int contCentral = 0;

        for (ReporteCentralVm central : listaCentral) {
            // Nueva Fila
            contCentral = 0;
            contenidoFila = this.nuevaFila(contenido);

            // PST
            celda = new ExcelCeldaInfo(central.getPstNombre(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // Nombre Central
            celda = new ExcelCeldaInfo(central.getNombreCentral(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // ABN
            celda = new ExcelCeldaInfo("" + central.getIdAbn(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // Estado
            celda = new ExcelCeldaInfo(central.getEstadoNombre(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // Municipio
            celda = new ExcelCeldaInfo(central.getMunicipioNombre(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // Población
            celda = new ExcelCeldaInfo(central.getPoblacionNombre(), null, null, false);
            contenidoFila[contCentral++] = celda;

            contCentral = 0;
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
