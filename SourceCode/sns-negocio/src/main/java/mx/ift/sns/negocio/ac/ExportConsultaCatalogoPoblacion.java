package mx.ift.sns.negocio.ac;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.ot.VCatalogoPoblacion;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información de la consulta sobre el Catálogo de Poblaciones. */
public class ExportConsultaCatalogoPoblacion implements IExportarExcel {

    /** Lista de Poblaciones. */
    private List<VCatalogoPoblacion> listaPoblaciones;

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /** Cabecera del Excel de Catálogos de Poblaciones. */
    private String[] textosCabecera = {"Nombre Estado", "Nombre Municipio", "Nombre Poblacion",
            "Clave Censal", "Numeración Asignada", "Presuscripción", "ABN",
            "Nirs", "Región Antigua", "Región Celular", "Región PCS"};

    /**
     * Constructor.
     * @param pListaVPoblaciones Lista de poblaciones
     */
    public ExportConsultaCatalogoPoblacion(List<VCatalogoPoblacion> pListaVPoblaciones) {
        listaPoblaciones = pListaVPoblaciones;
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
        boolean cambioEstado = true;
        boolean cambioMunicipio = true;

        int contCelda = 0;
        String estado = null;
        String municipio = null;

        for (VCatalogoPoblacion poblacion : listaPoblaciones) {
            if (!poblacion.getIdEstado().equals(estado)) {
                cambioEstado = true;
            } else {
                cambioEstado = false;
            }

            if (!poblacion.getIdMunicipio().equals(municipio)) {
                cambioMunicipio = true;
            } else {
                cambioMunicipio = false;
            }

            if (cambioEstado) {
                contenidoFila = this.nuevaFila(contenido);
                // Nombre de Estado
                celda = new ExcelCeldaInfo(poblacion.getNombreEstado(), ExportarExcel.ESTILO_LG,
                        ExportarExcel.ARIAL_11_BOLD, true);
                contenidoFila[contCelda++] = celda;
                estado = poblacion.getIdEstado();
            } else {
                contCelda++;
            }

            if (cambioMunicipio) {
                contenidoFila = this.nuevaFila(contenido);
                // Nombre de Municipio
                celda = new ExcelCeldaInfo(poblacion.getNombreMunicipio(), ExportarExcel.ESTILO_PBLUE,
                        ExportarExcel.ARIAL_11_BOLD, true);
                contenidoFila[contCelda++] = celda;
                municipio = poblacion.getIdMunicipio();
            } else {
                contCelda++;
            }

            // Nombre de Población
            contenidoFila = this.nuevaFila(contenido);
            celda = new ExcelCeldaInfo(poblacion.getNombrePoblacion(), ExportarExcel.ESTILO_GENERAL_WRAP,
                    null, true);
            contenidoFila[contCelda++] = celda;

            // Clave Censal
            celda = new ExcelCeldaInfo(poblacion.getId(), null, null, false);
            contenidoFila[contCelda++] = celda;

            // Numeración asignada
            celda = new ExcelCeldaInfo(poblacion.getNumeracionAsignada(), null, null, false);
            contenidoFila[contCelda++] = celda;

            // Presuscripción
            celda = new ExcelCeldaInfo(poblacion.getPresuscripcion(), null, null, false);
            contenidoFila[contCelda++] = celda;

            // ABN
            celda = new ExcelCeldaInfo(poblacion.getIdAbn().toString(), null, null, false);
            contenidoFila[contCelda++] = celda;

            // Listado de NIRs
            String nirs = (poblacion.getNirs() != null) ? poblacion.getNirs().substring(1,
                    poblacion.getNirs().length() - 1) : "";
            celda = new ExcelCeldaInfo(nirs.replaceAll(",", "\n"), null, null, false);
            contenidoFila[contCelda++] = celda;

            // Región Antigua
            celda = new ExcelCeldaInfo(poblacion.getRegionAntigua(), null, null, false);
            contenidoFila[contCelda++] = celda;

            // Región celular
            celda = new ExcelCeldaInfo(poblacion.getRegionCelular(), null, null, false);
            contenidoFila[contCelda++] = celda;

            // Región PCS
            celda = new ExcelCeldaInfo(poblacion.getRegionPcs(), null, null, false);
            contenidoFila[contCelda++] = celda;

            contCelda = 0;
        }

        return contenido;
    }

    /**
     * Agrega una nueva fila al contenedor de filas.
     * @param pContenedor Contenedor de filas
     * @return nueva fila para ser editada
     */
    private ExcelCeldaInfo[] nuevaFila(ArrayList<ExcelCeldaInfo[]> pContenedor) {
        ExcelCeldaInfo[] contenidoFila = new ExcelCeldaInfo[textosCabecera.length];
        pContenedor.add(contenidoFila);
        return contenidoFila;
    }

}
