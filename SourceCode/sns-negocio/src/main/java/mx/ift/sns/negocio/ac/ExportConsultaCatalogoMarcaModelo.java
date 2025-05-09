package mx.ift.sns.negocio.ac;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Genera un fichero Excel con la información de la consulta sobre el Catálogo de Marcas y Modelos. */
public class ExportConsultaCatalogoMarcaModelo implements IExportarExcel {

    /** Lista de Marcas. */
    private List<Marca> listaMarca;

    /** Cabecera del Excel de Catálogos de Marcas y Modelos. */
    private String[] textosCabecera = {"Código Marca", "Marca", "Estado", "Modelo",
            "Descripción", "Estado Modelo"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param pListaMarca Lista de Marca
     */
    public ExportConsultaCatalogoMarcaModelo(List<Marca> pListaMarca) {
        listaMarca = pListaMarca;
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

        int contMarca = 0;
        int contModelo = 0;

        for (Marca marca : listaMarca) {
            // Nueva Fila
            contMarca = 0;
            contenidoFila = this.nuevaFila(contenido);

            celda = new ExcelCeldaInfo(marca.getId().toString(), ExportarExcel.ESTILO_LG,
                    ExportarExcel.ARIAL_11_BOLD, true);
            contenidoFila[contMarca++] = celda;

            // Nombre Marca
            celda = new ExcelCeldaInfo(marca.getNombre(), null, null, false);
            contenidoFila[contMarca++] = celda;

            // Estado de la Marca
            celda = new ExcelCeldaInfo(marca.getEstatus().getDescripcion(), null, null, false);
            contenidoFila[contMarca++] = celda;

            for (Modelo modelo : marca.getModelos()) {
                // Modelos de la Marca -> Nueva Fila
                contModelo = contMarca;
                contenidoFila = this.nuevaFila(contenido);

                // Nombre de Modelo
                celda = new ExcelCeldaInfo(modelo.getTipoModelo(), null, null, false);
                contenidoFila[contModelo++] = celda;

                // Descricion de Modelo
                celda = new ExcelCeldaInfo(modelo.getDescripcion(), null, null, false);
                contenidoFila[contModelo++] = celda;

                // Estado de Modelo
                celda = new ExcelCeldaInfo(modelo.getEstatus().getDescripcion(), null, null, false);
                contenidoFila[contModelo++] = celda;
            }

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
