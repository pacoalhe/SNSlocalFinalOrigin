package mx.ift.sns.negocio.cpsi;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.cpsi.InfoCatCpsi;
//import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Clase encargada de crear los ficheros de exportación excel para el catálogo de códigos cpsi.
 * @author X50880SA
 */
public class ExportConsultaCatalogoCodigosCPSI implements IExportarExcel {
    /** Listado de equipos a exportar. */
    private List<InfoCatCpsi> listadoCodigosCPSI;

    /** Texto de la hoja de la exportación de datos generales. */
    public static final String TXT_CODIGOS_CPSI = "Códigos CPS Internacionales";

    /** Cabecera del Excel. */
    private String[] textosCabecera = {"Binario", "Decimal Total", "Formato Decimal", "Estatus", "Nombre PST",
            "Fecha Asignación UIT", "Referencia UIT", "Fecha Fin Cuarentena"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param listadoCodigosCPSI listado a exportar
     */
    public ExportConsultaCatalogoCodigosCPSI(List<InfoCatCpsi> listadoCodigosCPSI) {
        this.listadoCodigosCPSI = listadoCodigosCPSI;
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

        int contCelda = 0;

        for (InfoCatCpsi codigo : listadoCodigosCPSI) {
            contenidoFila = this.nuevaFila(contenido);

            celda = new ExcelCeldaInfo(codigo.getBinario(), null, null, false);
            contenidoFila[contCelda++] = celda;

            Integer decimalTotal = codigo.getDecimalTotal();
            celda = new ExcelCeldaInfo(decimalTotal == null ? "" : decimalTotal.toString(), null, null,
                    false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(codigo.getFormatoDecimal(),
                    null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(codigo.getEstatus().getDescripcion(),
                    null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(codigo.getProveedor() == null ? "" : codigo.getProveedor().getNombre(), null,
                    null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(FechasUtils.fechaToString(codigo.getFechaAsignacion()),
                    null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(codigo.getReferenciaUit(),
                    null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(FechasUtils.fechaToString(codigo.getFechaFinCuarentena()),
                    null, null, false);
            contenidoFila[contCelda++] = celda;

            contCelda = 0;

        }
        return contenido;
    }

    /**
     * Agrega una nueva fila al contenedor de filas.
     * @param pConteneder Contenedor de filas
     * @return nueva fila para ser editada
     */
    private ExcelCeldaInfo[] nuevaFila(ArrayList<ExcelCeldaInfo[]> pConteneder) {
        ExcelCeldaInfo[] contenidoFila = null;
        contenidoFila = new ExcelCeldaInfo[textosCabecera.length];

        pConteneder.add(contenidoFila);
        return contenidoFila;
    }

}
