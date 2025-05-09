package mx.ift.sns.negocio.ac;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.central.VCatalogoCentral;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información de la consulta sobre el Catálogo de Central. */
public class ExportConsultaCatalogoCentral implements IExportarExcel {

    /** Lista de Central. */
    private List<VCatalogoCentral> listaCentral;

    /** Cabecera del Excel de Catálogos de ABN. */
    private String[] textosCabecera = {"Nombre del PST", "Nombre de la Central", "Latitud", "Longitud",
            "Calle", "Número", "Colonia", "Listado de Abn's", "Listado de Centrales de Entrega"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaCentral Lista de Central
     */
    public ExportConsultaCatalogoCentral(List<VCatalogoCentral> pListaCentral) {
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
        String codigo = "";
        String nombre = "";

        for (VCatalogoCentral central : listaCentral) {
            // Nueva Fila
            contCentral = 0;
            contenidoFila = this.nuevaFila(contenido);

            // PST
            celda = new ExcelCeldaInfo(central.getNombrePst(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // Nombre Central
            celda = new ExcelCeldaInfo(central.getNombre(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // Latitud
            celda = new ExcelCeldaInfo(central.getLatitud(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // Longitud
            celda = new ExcelCeldaInfo(central.getLongitud(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // Calle
            celda = new ExcelCeldaInfo(central.getCalle(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // Numero
            celda = new ExcelCeldaInfo((central.getNumero() == null) ? "" : central.getNumero().toString(),
                    null, null, false);
            contenidoFila[contCentral++] = celda;

            // Colonia
            celda = new ExcelCeldaInfo(central.getColonia(), null, null, false);
            contenidoFila[contCentral++] = celda;

            // Abn Centrales
            if (central.getAbns() != null && !central.getAbns().isEmpty()) {
                codigo = central.getAbns().replaceFirst(",", "").replaceAll(",", "\n");
            }

            celda = new ExcelCeldaInfo(codigo, ExportarExcel.ESTILO_GENERAL_WRAP, null, true);
            contenidoFila[contCentral++] = celda;

            // Centrales Destino
            if (central.getRelacion() != null && !central.getRelacion().isEmpty()) {
                nombre = central.getRelacion().replaceAll(",", "\n");
            }

            celda = new ExcelCeldaInfo(nombre, null, null, false);
            contenidoFila[contCentral++] = celda;

            contCentral = 0;
            codigo = "";
            nombre = "";
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
