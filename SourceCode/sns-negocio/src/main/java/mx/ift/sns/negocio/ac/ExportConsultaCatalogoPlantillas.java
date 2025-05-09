package mx.ift.sns.negocio.ac;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.oficios.Plantilla;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información de la consulta sobre el Catálogo de Plantillas/Oficios. */
public class ExportConsultaCatalogoPlantillas implements IExportarExcel {

    /** Lista de Plantillas. */
    private List<Plantilla> listaPlantillas;

    /** Cabecera del Excel de Catálogos de ABN. */
    private String[] textosCabecera = {"Nombre plantilla de Oficio", "Trámite", "Destinatario"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor Específico.
     * @param pListaPlantillas Lista de Plantillas.
     */
    public ExportConsultaCatalogoPlantillas(List<Plantilla> pListaPlantillas) {
        listaPlantillas = pListaPlantillas;
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

        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>(listaPlantillas.size());
        ExcelCeldaInfo[] contenidoFila = null;
        ExcelCeldaInfo celda = null;

        int cellCounter = 0;

        for (Plantilla plantilla : listaPlantillas) {
            cellCounter = 0;
            contenidoFila = this.nuevaFila(contenido);

            // Descripción Plantilla
            celda = new ExcelCeldaInfo(plantilla.getDescripcion(), null, null, false);
            contenidoFila[cellCounter++] = celda;

            // Trámite
            celda = new ExcelCeldaInfo(plantilla.getTipoSolicitud().getDescripcion(), null, null, false);
            contenidoFila[cellCounter++] = celda;

            // Destinatario
            celda = new ExcelCeldaInfo(plantilla.getTipoDestinatario().getDescripcion(), null, null, false);
            contenidoFila[cellCounter++] = celda;
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
