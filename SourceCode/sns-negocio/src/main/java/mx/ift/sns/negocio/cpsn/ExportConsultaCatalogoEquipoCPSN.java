package mx.ift.sns.negocio.cpsn;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.cpsn.EquipoSenalCPSN;
import mx.ift.sns.modelo.cpsn.EquipoSenalCPSNWarn;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Clase encargada de crear los dicheros de exportación excel para el catálogo de equipos cpsn.
 * @author X23016PE
 */
public class ExportConsultaCatalogoEquipoCPSN implements IExportarExcel {
    /** Listado de equipos a exportar. */
    private List<EquipoSenalCPSN> listadoEquiposCPSN;

    // private static final Logger LOGGER = LoggerFactory.getLogger(ExportConsultaCatalogoEquipoCPSN.class);

    /** Texto de la hoja de la exportación de datos generales. */
    public static final String TXT_EQUIPOS_CPSN = "Equipos de Señalizacion Nacional";

    /** Cabecera del Excel. */
    private String[] textosCabecera = {"Nº", "PST", "ABN", "Localidad", "Clave de la Localidad",
            "Municipio", "Clave del Municipio", "Entidad", "Clave de la Entidad", "Longitud",
            "Latitud", "Nombre del Equipo", "Clave del Equipo", "Tipo del Equipo", "0=N\n1=I",
            "Código", "Usuario Creación", "Fecha Creación", "Usuario Modificación", "Fecha Modificación",
            "Nombre Fichero", "Warnings"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param listadoEquiposCPSN List<EquipoSenalCPSN>
     */
    public ExportConsultaCatalogoEquipoCPSN(List<EquipoSenalCPSN> listadoEquiposCPSN) {
        this.listadoEquiposCPSN = listadoEquiposCPSN;
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

        for (EquipoSenalCPSN equipo : listadoEquiposCPSN) {
            contenidoFila = this.nuevaFila(contenido);

            celda = new ExcelCeldaInfo(equipo.getId().toString(), ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getProveedor().getNombre(),
                    ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getAbn().getCodigoAbn().toString(), ExportarExcel.ESTILO_GENERAL_WRAP,
                    null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getPoblacion().getNombre(),
                    ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getPoblacion().getCdgPoblacion(), ExportarExcel.ESTILO_GENERAL_WRAP, null,
                    false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getPoblacion().getMunicipio().getNombre(),
                    ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getPoblacion().getMunicipio().getId().toString(),
                    ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getPoblacion().getMunicipio().getEstado().
                    getNombre(), ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getPoblacion().getMunicipio().getEstado().
                    getCodEstado(), ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getLongitud(), ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getLatitud(), ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getNombre(), ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getClave(), ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getTipo(), ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo("N", ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getCps().toString(), ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getUsuarioCreacion() != null ? equipo.getUsuarioCreacion().getUserid()
                    : "", ExportarExcel.ESTILO_GENERAL_WRAP, null,
                    false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getFechaCreacion() != null ? FechasUtils.fechaToString(equipo
                    .getFechaCreacion()) : "",
                    ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo((equipo.getUsuarioModificacion() != null) ? equipo.getUsuarioModificacion()
                    .getUserid() : "", ExportarExcel.ESTILO_GENERAL_WRAP,
                    null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo((equipo.getFechaModificacion() != null) ? FechasUtils.fechaToString(equipo
                    .getFechaModificacion()) : "",
                    ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(equipo.getNombreFichero(), ExportarExcel.ESTILO_GENERAL_WRAP, null, false);
            contenidoFila[contCelda++] = celda;

            String warnings = "";
            for (EquipoSenalCPSNWarn warn : equipo.getWarnings()) {
                warnings += (warnings.isEmpty()) ? warn.getWarning() : "\n" + warn.getWarning();
            }

            celda = new ExcelCeldaInfo(warnings, ExportarExcel.ESTILO_GENERAL_WRAP_LEFT, null, false);
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
