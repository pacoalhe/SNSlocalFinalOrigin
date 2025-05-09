package mx.ift.sns.negocio.cpsn;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Clase encargada de crear los dicheros de exportación excel para el catálogo de códigos cpsn.
 * @author X23016PE
 */
public class ExportConsultaCatalogoCodigosCPSN implements IExportarExcel {
    /** Listado de equipos a exportar. */
    private List<CodigoCPSN> listadoCodigosCPSN;

    // private static final Logger LOGGER = LoggerFactory.getLogger(ExportConsultaCatalogoEquipoCPSN.class);

    /** Texto de la hoja de la exportación de datos generales. */
    public static final String TXT_CODIGOS_CPSN = "Códigos CPS Nacionales";

    /** Cabecera del Excel. */
    private String[] textosCabecera = {"Tipo", "Binario", "Decimal Red", "Decimal Total",
            "Decimal Desde", "Decimal Hasta", "Estatus", "Nombre PST", "Fecha Fin Cuarentena"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param listadoCodigosCPSN List<CodigoCPSN>
     */
    public ExportConsultaCatalogoCodigosCPSN(List<CodigoCPSN> listadoCodigosCPSN) {
        this.listadoCodigosCPSN = listadoCodigosCPSN;
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

        for (CodigoCPSN codigo : listadoCodigosCPSN) {
            contenidoFila = this.nuevaFila(contenido);

            celda = new ExcelCeldaInfo(codigo.getTipoBloqueCPSN().getDescripcion(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(codigo.getBinario(), null, null, false);
            contenidoFila[contCelda++] = celda;

            Integer decimalRed = codigo.getDecimalRed();
            celda = new ExcelCeldaInfo(decimalRed == null ? "" : codigo.getDecimalRed().toString(), null,
                    null, false);
            contenidoFila[contCelda++] = celda;

            Integer decimalTotal = codigo.getDecimalTotal();
            celda = new ExcelCeldaInfo(decimalTotal == null ? "" : decimalTotal.toString(), null, null,
                    false);
            contenidoFila[contCelda++] = celda;

            Integer decimalDesde = codigo.getDecimalDesde();
            celda = new ExcelCeldaInfo(decimalDesde == null ? "" : decimalDesde.toString(), null, null,
                    false);
            contenidoFila[contCelda++] = celda;

            Integer decimalHasta = codigo.getDecimalHasta();
            celda = new ExcelCeldaInfo(decimalHasta == null ? "" : decimalHasta.toString(),
                    null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(codigo.getEstatusCPSN().getDescripcion(),
                    null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(codigo.getProveedor() == null ? "" : codigo.getProveedor().getNombre(), null,
                    null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(FechasUtils.fechaToString(codigo.getFechaCuarentena()),
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
