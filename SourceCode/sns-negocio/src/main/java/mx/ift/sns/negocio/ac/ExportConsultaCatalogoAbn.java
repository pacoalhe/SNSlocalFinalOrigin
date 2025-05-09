package mx.ift.sns.negocio.ac;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.abn.VCatalogoAbn;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información de la consulta sobre el Catálogo de ABN. */
public class ExportConsultaCatalogoAbn implements IExportarExcel {

    /** Lista de Regigistros devueltos por la vista V_CATALOGO_ABN. */
    private List<VCatalogoAbn> listaAbnsVista;

    /** Cabecera para Nuevo ABN. */
    private String[] headerAbn = {"Código ABN", "Presuscripción", "Población Ancla", "Fecha Consolidación", "NIRS", ""};

    /** Cabecera para Organización Territorial. */
    private String[] headerOt = {"Código Estado", "Nombre Estado", "Código Municipio", "Nombre Municipio",
            "Código Población", "Nombre Población"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaAbn Lista de ABN
     * @param pOtService Servicio de Organización territorial
     */
    public ExportConsultaCatalogoAbn(List<VCatalogoAbn> pListaAbn, IOrganizacionTerritorialService pOtService) {
        listaAbnsVista = pListaAbn;
        // otService = pOtService;
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
        ehi.setTitulos(headerAbn);
        ehi.setCellFont(ExportarExcel.ARIAL_11_BOLD);
        ehi.setCellStyle(ExportarExcel.ESTILO_ABN);
        ehi.setAutoAjuste(true);
        ehi.setInmovilizarCabecera(false);
        ehi.setVisible(false);
        return ehi;
    }

    @Override
    public ArrayList<ExcelCeldaInfo[]> getContenido() {

        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>();
        ExcelCeldaInfo[] contenidoFila = null;
        ExcelCeldaInfo celda = null;

        int contCelda = 0;
        int lastAbn = -1;

        for (VCatalogoAbn registro : listaAbnsVista) {
            // Nuevo ABN a mostrar
            if ((lastAbn < 0) || (lastAbn != registro.getIdAbn())) {
                // Cabecera ABN
                contenidoFila = this.nuevaFila(contenido);
                for (int i = 0; i < headerAbn.length; i++) {
                    celda = new ExcelCeldaInfo(
                            headerAbn[i],
                            ExportarExcel.ESTILO_ABN,
                            ExportarExcel.ARIAL_11_BOLD,
                            true);
                    contenidoFila[i] = celda;
                }

                // Nueva Fila
                contCelda = 0;
                contenidoFila = this.nuevaFila(contenido);

                // Codigo ABN
                celda = new ExcelCeldaInfo(String.valueOf(registro.getIdAbn()), null, null, false);
                contenidoFila[contCelda++] = celda;

                // Presuscripción
                celda = new ExcelCeldaInfo(registro.getPresuscripcion(), null, null, false);
                contenidoFila[contCelda++] = celda;

                // Población Ancla
                celda = new ExcelCeldaInfo(registro.getPoblacionAncla(), null, null, false);
                contenidoFila[contCelda++] = celda;

                // Fecha Consolidación
                celda = new ExcelCeldaInfo(FechasUtils.fechaToString(registro.getFechaConsolidacion()),
                        null, null, false);
                contenidoFila[contCelda++] = celda;

                // Lista de Nirs
                celda = new ExcelCeldaInfo(registro.getListaNirsABN(), null, null, false);
                contenidoFila[contCelda++] = celda;

                contenidoFila = this.nuevaFila(contenido);
                for (int i = 0; i < headerOt.length; i++) {
                    celda = new ExcelCeldaInfo(
                            headerOt[i],
                            ExportarExcel.ESTILO_MUNICIPIO,
                            ExportarExcel.ARIAL_11_BOLD,
                            true);
                    contenidoFila[i] = celda;
                }

                // Actualizamos el id de ABN para las cabeceras.
                lastAbn = registro.getIdAbn();
            }

            contCelda = 0;
            contenidoFila = this.nuevaFila(contenido);

            // Código de Estado
            celda = new ExcelCeldaInfo(registro.getIdEstado(), null, null, false);
            contenidoFila[contCelda++] = celda;

            // Nombre de Estado
            celda = new ExcelCeldaInfo(registro.getNombreEstado(), ExportarExcel.ESTILO_GENERAL_LEFT, null, false);
            contenidoFila[contCelda++] = celda;

            // Código de Municipio
            celda = new ExcelCeldaInfo(registro.getIdMunicipio(), null, null, false);
            contenidoFila[contCelda++] = celda;

            // Nombre de Municipio
            celda = new ExcelCeldaInfo(
                    registro.getNombreMunicipio(), ExportarExcel.ESTILO_GENERAL_LEFT, null, false);
            contenidoFila[contCelda++] = celda;

            // Código de Población
            celda = new ExcelCeldaInfo(registro.getIdPoblacion(), null, null, false);
            contenidoFila[contCelda++] = celda;

            // Nombre de Población
            celda = new ExcelCeldaInfo(
                    registro.getNombrePoblacion(), ExportarExcel.ESTILO_GENERAL_LEFT, null, false);
            contenidoFila[contCelda++] = celda;
        }

        return contenido;
    }

    /**
     * Agrega una nueva fila al contenedor de filas.
     * @param pConteneder Contenedor de filas
     * @return nueva fila para ser editada
     */
    private ExcelCeldaInfo[] nuevaFila(ArrayList<ExcelCeldaInfo[]> pConteneder) {
        ExcelCeldaInfo[] contenidoFila = new ExcelCeldaInfo[headerAbn.length];
        pConteneder.add(contenidoFila);
        return contenidoFila;
    }

    /**
     * @return the headerAbn
     */
    public String[] getHeaderAbn() {
        return headerAbn;
    }

    /**
     * @return the headerOt
     */
    public String[] getHeaderOt() {
        return headerOt;
    }
}
