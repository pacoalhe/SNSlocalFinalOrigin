package mx.ift.sns.negocio.utils.excel;

import java.util.ArrayList;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/** Interfaz de métodos comunes a las clases de generación de listados Excel. */
public interface IExportarExcel {

    /**
     * Genera el Documento Excel que se utilizará para el Reporte.
     * @return XSSFWorkbook
     */
    SXSSFWorkbook getLibroExcel();

    /**
     * Devuelve la información de la cabecera.
     * @return ExcellHeaderInfo con los títulos y estilo de cabecera.
     */
    ExcelCabeceraInfo getCabecera();

    /**
     * Devuelve el contenido de las celdas organizado por filas y columnas.
     * @return Matriz con el contenido de las celdas por filas y columnas.
     */
    ArrayList<ExcelCeldaInfo[]> getContenido();

}
