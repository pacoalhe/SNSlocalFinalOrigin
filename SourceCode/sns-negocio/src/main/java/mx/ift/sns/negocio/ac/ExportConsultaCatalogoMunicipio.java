package mx.ift.sns.negocio.ac;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.ot.ExportMunicipio;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Genera un fichero Excel con la información de la consulta sobre el Catálogo de Central. */
public class ExportConsultaCatalogoMunicipio implements IExportarExcel {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportConsultaCatalogoMunicipio.class);

    /** Lista de Central. */
    private List<ExportMunicipio> listaDatos;

    /** Cabecera del Excel de Catálogos de ABN. */
    private String[] textosCabecera = {"Nombre Estado", "Nombre Municipio", "Numeración Asignada", "Región Celular",
            "Región PCS", "Clave Censal", "Nombre Población", "ABN", "Presuscripción"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaDatos Lista de Central
     */
    public ExportConsultaCatalogoMunicipio(List<ExportMunicipio> pListaDatos) {
        listaDatos = pListaDatos;
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
            String idMunicipio = "";

            for (ExportMunicipio dato : listaDatos) {

                contenidoFila = this.nuevaFila(contenido);

                // Cargamos los datos del municipio
                if (!idMunicipio.equals(dato.getIdEstado() + dato.getIdMunicipio())) {
                    int contCelda = 0;
                    idMunicipio = dato.getIdEstado() + dato.getIdMunicipio();

                    celda = new ExcelCeldaInfo(dato.getNombreEstado(), ExportarExcel.ESTILO_AQUA,
                            ExportarExcel.ARIAL_11_BOLD, true);
                    contenidoFila[contCelda++] = celda;

                    celda = new ExcelCeldaInfo(dato.getNombreMunicipio(), null, null, false);
                    contenidoFila[contCelda++] = celda;

                    celda = new ExcelCeldaInfo(dato.getNumeracionAsignada(), null, null, false);
                    contenidoFila[contCelda++] = celda;

                    celda = new ExcelCeldaInfo((dato.getRegionCelular() != null) ? dato.getRegionCelular().toString()
                            : "", null, null, false);
                    contenidoFila[contCelda++] = celda;

                    celda = new ExcelCeldaInfo((dato.getRegionPcs() != null) ? dato.getRegionPcs().toString() : "",
                            null, null, false);
                    contenidoFila[contCelda++] = celda;

                    contenidoFila = this.nuevaFila(contenido);

                }

                if (dato.getClaveCensal() != null) {
                    // Cargamos los datos de cada poblacion
                    int contCeldaPob = 5;

                    celda = new ExcelCeldaInfo(dato.getClaveCensal(), null, null, false);
                    contenidoFila[contCeldaPob++] = celda;

                    celda = new ExcelCeldaInfo(dato.getNombrePoblacion(), ExportarExcel.ESTILO_GENERAL_LEFT,
                            null, false);
                    contenidoFila[contCeldaPob++] = celda;

                    celda = new ExcelCeldaInfo((dato.getIdAbn() != null) ? dato.getIdAbn().toString() : "", null, null,
                            false);
                    contenidoFila[contCeldaPob++] = celda;

                    celda = new ExcelCeldaInfo(dato.getPresuscripcion(), null, null, false);
                    contenidoFila[contCeldaPob++] = celda;
                }

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

    /**
     * @return the listaDatos
     */
    public List<ExportMunicipio> getListaDatos() {
        return listaDatos;
    }

    /**
     * @param listaDatos the listaDatos to set
     */
    public void setListaDatos(List<ExportMunicipio> listaDatos) {
        this.listaDatos = listaDatos;
    }
}
