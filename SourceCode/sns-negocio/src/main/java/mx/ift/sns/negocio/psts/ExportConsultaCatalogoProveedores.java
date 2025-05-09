package mx.ift.sns.negocio.psts;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.negocio.utils.excel.ExcelCabeceraInfo;
import mx.ift.sns.negocio.utils.excel.ExcelCeldaInfo;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.negocio.utils.excel.IExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * Clase encargada de crear los dicheros de exportación excel para el catálogo de proveedores.
 * @author X23016PE
 */
public class ExportConsultaCatalogoProveedores implements IExportarExcel {
    /** Listado de Proveedores a exportar. */
    private List<Proveedor> listadoProveedores;

    // private static final Logger LOGGER = LoggerFactory.getLogger(ExportConsultaCatalogoProveedores.class);

    /** Identificador del excel a generar. */
    private int tipoExportacion;

    /** ID de exportación de datos generales. */
    public static final int ID_DATOS_GENERALES_PST = 1;
    /** ID de exportación de datos contactos. */
    public static final int ID_DATOS_CONTACTOS_PST = 2;
    /** ID de exportación de datos generales. */
    public static final int ID_DATOS_CONVENIOS_PST = 3;

    /** Texto de la hoja de la exportación de datos generales. */
    public static final String TXT_DATOS_GENERALES = "Datos Generales Pst";

    /** Texto de la hoja de la exportación de datos de convenios. */
    public static final String TXT_DATOS_CONVENIOS = "Datos Convenios Pst";

    /** Texto de la hoja de la exportación de datos de contactos. */
    public static final String TXT_DATOS_CONTACTOS = "Datos Contactos Pst";

    /** Cabecera del Excel de Datos Generales. */
    private String[] textosCabeceraDG = {"Nombre", "Nombre Corto", "Tipo de Servicio", "Tipo de Pst",
    		"Tipo de Red", "Tipo de Red Original", "IDO", "IDA", "ABC", "IDO (ANTES BCD)", "Estado", "Calle",
            "Número Exterior", "Número Interior", "Colonia", "Ciudad", "Código Postal"};

    /** Cabecera del Excel de Convenios. */
    private String[] textosCabeceraConv = {"Nombre PST", "Nombre Pst Convenio", "Número de Contrato",
            "Fecha Inicio Convenio"};

    /** Cabecera del Excel de Contactos. */
    private String[] textosCabeceraCont = {"Nombre PST", "Nombre Contacto", "Correo Electrónico", "Tipo de Contacto",
            "Teléfono 1", "Teléfono 2", "Teléfono 3"};

    /** Libro Excel para el reporte. */
    private SXSSFWorkbook workBook = null;

    /**
     * Constructor.
     * @param listadoProveedores List<Proveedor>
     * @param tipoExportacion int
     */
    public ExportConsultaCatalogoProveedores(List<Proveedor> listadoProveedores, int tipoExportacion) {
        this.listadoProveedores = listadoProveedores;
        this.tipoExportacion = tipoExportacion;
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
        if (tipoExportacion == ID_DATOS_GENERALES_PST) {
            ehi.setTitulos(textosCabeceraDG);
        } else if (tipoExportacion == ID_DATOS_CONVENIOS_PST) {
            ehi.setTitulos(textosCabeceraConv);
        } else if (tipoExportacion == ID_DATOS_CONTACTOS_PST) {
            ehi.setTitulos(textosCabeceraCont);
        }

        ehi.setCellFont(ExportarExcel.ARIAL_12_BOLD);
        ehi.setCellStyle(ExportarExcel.ESTILO_GENERAL_CENTRADO);
        ehi.setInmovilizarCabecera(true);

        return ehi;
    }

    @Override
    public ArrayList<ExcelCeldaInfo[]> getContenido() {
        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>();

        if (tipoExportacion == ID_DATOS_GENERALES_PST) {
            contenido = getContenidoDG();
        } else if (tipoExportacion == ID_DATOS_CONVENIOS_PST) {
            contenido = getContenidoConv();
        } else if (tipoExportacion == ID_DATOS_CONTACTOS_PST) {
            contenido = getContenidoCont();
        }

        return contenido;
    }

    /**
     * Función que crea los valores de exportación de los datos generales.
     * @return ArrayList<String[]>
     */
    private ArrayList<ExcelCeldaInfo[]> getContenidoDG() {
        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>();
        ExcelCeldaInfo[] contenidoFila = null;
        ExcelCeldaInfo celda = null;

        int contCelda = 0;

        for (Proveedor proveedor : listadoProveedores) {
            contenidoFila = this.nuevaFila(contenido);

            celda = new ExcelCeldaInfo(proveedor.getNombre(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getNombreCorto(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getTipoServicio().getDescripcion(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getTipoProveedor().getDescripcion(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getTipoRed().getDescripcion(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getTipoRedOriginal() == null ? "" : proveedor
                    .getTipoRedOriginal()
                    .getDescripcion(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getIdo() == null ? "" : proveedor.getIdo().toString(), null, null,
                    false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getIda() == null ? "" : proveedor.getIda().toString(), null, null,
                    false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getAbc() == null ? "" : proveedor.getAbc().toString(), null, null,
                    false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getBcd() == null ? "" : proveedor.getBcd().toString(), null, null,
                    false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getEstado().getNombre(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getCalle(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getNumExt(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getNumInt(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getColonia(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getCiudad(), null, null, false);
            contenidoFila[contCelda++] = celda;

            celda = new ExcelCeldaInfo(proveedor.getCp(), null, null, false);
            contenidoFila[contCelda++] = celda;

            contCelda = 0;

        }
        return contenido;
    }

    /**
     * Función que crea los valores de exportación de los convenios.
     * @return ArrayList<Strin[]>
     */
    private ArrayList<ExcelCeldaInfo[]> getContenidoConv() {

        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>();
        ExcelCeldaInfo[] contenidoFila = null;
        ExcelCeldaInfo celda = null;

        int contCelda = 0;

        for (Proveedor proveedor : listadoProveedores) {
            for (ProveedorConvenio convenio : proveedor.getConveniosComercializador()) {
                contenidoFila = this.nuevaFila(contenido);

                celda = new ExcelCeldaInfo(proveedor.getNombre(), null, null, false);
                contenidoFila[contCelda++] = celda;

                celda = new ExcelCeldaInfo(convenio.getProveedorConcesionario().getNombre(), null, null, false);
                contenidoFila[contCelda++] = celda;

                celda = new ExcelCeldaInfo(convenio.getContrato(), null, null, false);
                contenidoFila[contCelda++] = celda;

                celda = new ExcelCeldaInfo(FechasUtils.fechaToString(convenio.getFechaInicio()), null, null, false);
                contenidoFila[contCelda++] = celda;

                contCelda = 0;
            }
        }

        return contenido;
    }

    /**
     * Función que crea los valores de exportación de los contactos.
     * @return ArrayList<String[]>
     */
    private ArrayList<ExcelCeldaInfo[]> getContenidoCont() {
        ArrayList<ExcelCeldaInfo[]> contenido = new ArrayList<ExcelCeldaInfo[]>();
        ExcelCeldaInfo[] contenidoFila = null;
        ExcelCeldaInfo celda = null;

        int contCelda = 0;

        for (Proveedor proveedor : listadoProveedores) {
            for (Contacto contacto : proveedor.getContactos()) {
                contenidoFila = this.nuevaFila(contenido);

                celda = new ExcelCeldaInfo(proveedor.getNombre(), null, null, false);
                contenidoFila[contCelda++] = celda;

                celda = new ExcelCeldaInfo(contacto.getNombre(), null, null, false);
                contenidoFila[contCelda++] = celda;

                celda = new ExcelCeldaInfo(contacto.getEmail(), null, null, false);
                contenidoFila[contCelda++] = celda;

                celda = new ExcelCeldaInfo(contacto.getTipoContacto().getDescripcion(), null, null, false);
                contenidoFila[contCelda++] = celda;

                celda = new ExcelCeldaInfo(contacto.getTelefono1(), null, null, false);
                contenidoFila[contCelda++] = celda;

                celda = new ExcelCeldaInfo(contacto.getTelefono2(), null, null, false);
                contenidoFila[contCelda++] = celda;

                celda = new ExcelCeldaInfo(contacto.getTelefono3(), null, null, false);
                contenidoFila[contCelda++] = celda;

                contCelda = 0;
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
        ExcelCeldaInfo[] contenidoFila = null;

        if (tipoExportacion == ID_DATOS_GENERALES_PST) {
            contenidoFila = new ExcelCeldaInfo[textosCabeceraDG.length];
        } else if (tipoExportacion == ID_DATOS_CONVENIOS_PST) {
            contenidoFila = new ExcelCeldaInfo[textosCabeceraConv.length];
        } else if (tipoExportacion == ID_DATOS_CONTACTOS_PST) {
            contenidoFila = new ExcelCeldaInfo[textosCabeceraCont.length];
        }

        pConteneder.add(contenidoFila);
        return contenidoFila;
    }

}
