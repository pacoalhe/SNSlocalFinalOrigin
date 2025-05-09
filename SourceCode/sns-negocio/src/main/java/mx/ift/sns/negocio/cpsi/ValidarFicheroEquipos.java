package mx.ift.sns.negocio.cpsi;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import mx.ift.sns.dao.cpsi.ICodigoCpsiDao;
import mx.ift.sns.dao.ng.IAbnDao;
import mx.ift.sns.dao.ot.IPoblacionDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.cpsn.EquipoCpsnExcel;
import mx.ift.sns.utils.LocalizacionUtil;
import mx.ift.sns.utils.number.NumerosUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Clase encargada de validar el fichero de equipos de señalización.
 * @author X23016PE
 */
public final class ValidarFicheroEquipos {

    /** Cabecera del archivo excel. */
    private static String[][] textosCabecera = {{"No.", "ASL", "LOCALIDAD", "CLAVE DE LA LOCALIDAD",
            "MUNICIPIO", "CLAVE DEL MUNICIPIO", "ENTIDAD", "CLAVE DE LA ENTIDAD", "COORDENADAS GEOGRAFICAS",
            "", "NOMBRE DEL EQUIPO", "CLAVE DEL EQUIPO", "TIPO DE EQUIPO", "CPS:", ""},
            {"", "", "", "", "", "", "", "", "LONGITUD", "LATITUD", "", "", "", "0=N; 1=I", "Código."}};

    /** CPS minimo. */
    private static final int CPS_MIN = 0;
    /** CPS máximo. */
    private static final int CPS_MAX = 16383;
    /** Tamaño de filas de la cabecera. */
    private static final int NUM_FILAS_CABECERA = 2;

    /** Constructor. */
    private ValidarFicheroEquipos() {
        super();
    }

    /**
     * Método que extrae los datos de un formato excel.
     * @param fichero fichero.
     * @return CargaEquiposExcel lista de equipos.
     * @throws Exception exception.
     */
    public static CargaEquiposExcel extraerExcel(File fichero) throws Exception {
        CargaEquiposExcel sheetData = new CargaEquiposExcel();
        FileInputStream fis = null;
        XSSFWorkbook xWorkbook = null;
        HSSFWorkbook hWorkbook = null;
        XSSFSheet xSheet = null;
        HSSFSheet hSheet = null;
        Iterator<Row> rows = null;
        boolean registroVacio = true;

        try {
            fis = new FileInputStream(fichero);
            String nombreFichero = fichero.getPath().substring(fichero.getPath().lastIndexOf("\\") + 1);
            sheetData.setNombreFichero(nombreFichero);

            if ("xls".equals(fichero.getPath().substring(fichero.getPath().lastIndexOf(".") + 1))) {
                hWorkbook = new HSSFWorkbook(fis);
                hSheet = hWorkbook.getSheetAt(0);
                rows = hSheet.rowIterator();
            } else if ("xlsx".equals(fichero.getPath().substring(fichero.getPath().lastIndexOf(".") + 1))) {
                xWorkbook = new XSSFWorkbook(fis);
                xSheet = xWorkbook.getSheetAt(0);
                rows = xSheet.rowIterator();
            }

            int fila = 0;
            while (rows.hasNext()) {
                Row row = (Row) rows.next();
                registroVacio = true;

                Iterator<Cell> cells = row.cellIterator();
                EquipoCpsiExcel data = new EquipoCpsiExcel();
                while (cells.hasNext()) {
                    Cell cell = (Cell) cells.next();
                    String valor = "";
                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        valor = String.valueOf((int) cell.getNumericCellValue());
                        if (valor == null || "".equals(valor.trim())) {
                            valor = "";
                        } else {
                            registroVacio = false;
                        }
                        data.addColumna(valor.trim());
                    } else {
                        valor = cell.getStringCellValue();
                        if (valor == null || "".equals(valor.trim())) {
                            valor = "";
                        } else {
                            registroVacio = false;
                        }
                        data.addColumna(valor.trim());
                    }
                }
                if (!registroVacio) {
                    fila++;
                    if (fila <= NUM_FILAS_CABECERA) {
                        sheetData.addCabecera(data);
                    } else {
                        sheetData.addRegistro(data);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (xWorkbook != null) {
                xWorkbook.close();
            } else if (hWorkbook != null) {
                hWorkbook.close();
            }

            if (fis != null) {
                fis.close();
            }
        }
        sheetData.setNumEquiposLeidos(sheetData.getEquiposExcel() != null ? sheetData.getEquiposExcel().size() : 0);
        return sheetData;
    }

    /**
     * Obtiene el numero de filas con datos real.
     * @param sheetData mapa de datos
     * @return filas reales
     */
    public static int obtenerNumRegistros(CargaEquiposExcel sheetData) {
        return sheetData.getEquiposExcel() != null ? sheetData.getEquiposExcel().size() : 0;
    }

    /**
     * Obtiene el numero de registros válidos.
     * @param sheetData mapa de datos
     * @return filas válidas
     */
    public static int obtenerNumRegistrosValidos(CargaEquiposExcel sheetData) {
        return sheetData.getEquiposExcelValidos() != null ? sheetData.getEquiposExcelValidos().size() : 0;
    }

    /**
     * Método que valida que los campos de la cabecera son correctos.
     * @param sheetData listado de datos del excel.
     * @return true si son correctas las cabeceras
     */
    public static boolean procesarCabecera(CargaEquiposExcel sheetData) {

        if (sheetData.getCabecera() == null || sheetData.getCabecera().size() < NUM_FILAS_CABECERA) {
            return false;
        }

        for (int i = 0; i < NUM_FILAS_CABECERA; i++) {

            for (int j = 0; j < textosCabecera[i].length; j++) {
                String dato = sheetData.getCabecera().get(i).getColumnas().get(j) == null ? ""
                        : sheetData.getCabecera().get(i).getColumnas().get(j);
                if (!textosCabecera[i][j].equalsIgnoreCase(dato)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Método encargado de validar todos los registros del excel.
     * @param sheetData registros a procesar
     * @param pst proveedor
     * @param abnDao dao de abns
     * @param poblacionDao dao de poblaciones
     * @param codigoCpsiDao dao de codigos internacionales
     * @return listado de errores
     */
    public static CargaEquiposExcel validarEquipos(CargaEquiposExcel sheetData, Proveedor pst,
            IAbnDao abnDao, IPoblacionDao poblacionDao, ICodigoCpsiDao codigoCpsiDao) {

        for (EquipoCpsiExcel equipo : sheetData.getEquiposExcel()) {
            if (validarDuplicados(sheetData.getEquiposExcelValidos(), equipo)) {
                procesaRegistro(equipo, pst, abnDao, poblacionDao, codigoCpsiDao);
            } else {
                equipo.addErrorValidacion(
                        new ErrorRegistro(equipo.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                                ErrorRegistro.TIPO_AVISO_ERROR, 0, equipo.getColumnas()
                                        .get(EquipoCpsiExcel.POS_CPS), "Equipo duplicado."));
                equipo.setProcesarRegistro(false);
            }

            if (equipo.isProcesarRegistro()) {
                sheetData.addRegistroValido(equipo);
            } else {
                sheetData.addRegistroErroneo(equipo);
            }
        }

        sheetData.getEquiposExcel().clear();

        return sheetData;
    }

    /**
     * Método encargado de validar los campos del registro.
     * @param equipo a validar
     * @param pst proveedor a actualizar
     * @param abnDao dao de abns
     * @param poblacionDao dao de poblaciones
     * @param codigoCpsiDao dao de codigos internacionales
     * @return listado de errores/warnings
     */
    private static EquipoCpsiExcel procesaRegistro(EquipoCpsiExcel equipo, Proveedor pst,
            IAbnDao abnDao, IPoblacionDao poblacionDao, ICodigoCpsiDao codigoCpsiDao) {
        String numEquipo = null;
        String cps = NumerosUtils.decimalABinario(equipo.getColumnas().get(EquipoCpsiExcel.POS_CPS));

        validarTipoCps(EquipoCpsnExcel.POS_TIPO_CPS, cps, equipo);
        if (equipo.getErroresValidacion() != null && !equipo.getErroresValidacion().isEmpty()) {
            return equipo;
        }

        for (int i = 0; i < textosCabecera[0].length; i++) {
            // Ignoramos el primer campo ya que no lo procesamos
            switch (i) {
            case EquipoCpsiExcel.POS_REGISTRO:
                numEquipo = equipo.getColumnas().get(i);
                if (numEquipo == null || numEquipo.isEmpty()) {
                    equipo.addErrorValidacion(new ErrorRegistro(numEquipo, ErrorRegistro.TIPO_AVISO_ERROR, i, cps,
                            "Número del equipo vacío."));
                }
                break;
            // Validación del abn
            case EquipoCpsiExcel.POS_ABN:
                validarAbn(i, cps, equipo, abnDao);
                break;
            // Validación del nombre de la localidad. Ignoramos el campo.
            case EquipoCpsiExcel.POS_NOM_LOCALIDAD:
                break;
            // Validación de la localidad (población)
            case EquipoCpsiExcel.POS_COD_LOCALIDAD:
                validarPoblacion(i, cps, equipo);
                break;
            // Validación del nombre de municipio. Ignoramos el campo.
            case EquipoCpsiExcel.POS_NOM_MUNICIPIO:
                break;
            // Validación del municipio
            case EquipoCpsiExcel.POS_COD_MUNICIPIO:
                validarMunicipio(i, cps, equipo);
                break;
            // Validación del nombre de estado. Ignoramos el campo.
            case EquipoCpsiExcel.POS_NOM_ESTADO:
                break;
            // Validación del estado.
            case EquipoCpsiExcel.POS_COD_ESTADO:
                validarEstado(i, cps, equipo);
                break;
            // Validación de la longitud.
            case EquipoCpsiExcel.POS_LONGITUD:
                validarLongitud(i, cps, equipo);
                break;
            // Validación de la latitud.
            case EquipoCpsiExcel.POS_LATITUD:
                validarLatitud(i, cps, equipo);
                break;
            // Validación del nombre de equipo.
            case EquipoCpsiExcel.POS_NOMBRE:
                validarNombre(i, cps, equipo);
                break;
            // Validación de la clave del equipo.
            case EquipoCpsiExcel.POS_CLAVE:
                validarClave(i, cps, equipo);
                break;
            // Validación del tipo del equipo.
            case EquipoCpsiExcel.POS_TIPO:
                validarTipo(i, cps, equipo);
                break;
            // Validación del tipo de CPS (0=Nacional, 1=Internacional)
            case EquipoCpsiExcel.POS_TIPO_CPS:
                validarTipoCps(i, cps, equipo);
                break;
            // Validación del código CPS
            case EquipoCpsiExcel.POS_CPS:
                validarCps(i, cps, equipo, pst, codigoCpsiDao);
                break;
            }
        }

        // Una vez procesado cada campo se valida la coherencia de los valores.
        validarOrganizacionTerritorial(cps, equipo, poblacionDao);

        return equipo;
    }

    /**
     * Método encargado de validar que el abn recibido es correcto.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     * @param abnDao dao de abns
     */
    private static void validarAbn(int columna, String cps, EquipoCpsiExcel registro, IAbnDao abnDao) {
        String abn = registro.getColumnas().get(columna);
        // Se valida que el abn es obligatorio.
        if (abn == null || abn.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps, "Abn vacío o nulo."));
        }

        // Se valida que sea numérico.
        if (!NumberUtils.isNumber(abn.trim())) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "El Abn debe ser numérico."));
        } else {
            // Se valida que esté entre 1 y 999
            if (Integer.parseInt(abn.trim()) < Abn.MIN_ABN || Integer.parseInt(abn.trim()) > Abn.MAX_ABN) {
                registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                        ErrorRegistro.TIPO_AVISO_ERROR, columna, cps,
                        "El Abn debe ser un número entre " + Abn.MIN_ABN + " y " + Abn.MAX_ABN + "."));
            } else {
                // Se valida si existe el abn.
                Abn asl = abnDao.getAbnById(BigDecimal.valueOf(Long.parseLong(abn)));
                if (asl == null) {
                    registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(
                            EquipoCpsiExcel.POS_REGISTRO),
                            ErrorRegistro.TIPO_AVISO_ERROR, columna, cps,
                            "El Abn no existe."));
                } else {
                    // Si el abn está inactivo avisamos mediante un warning.
                    if (EstadoAbn.INACTIVO.equals(asl.getEstadoAbn().getCodigo())) {
                        registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(
                                EquipoCpsiExcel.POS_REGISTRO),
                                ErrorRegistro.TIPO_AVISO_WARN, columna, cps,
                                "El Abn está inactivo. Se continúa el proceso."));
                    }
                }
            }
        }
    }

    /**
     * Método encargado de validar que la población recibida sea correcta.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     */
    private static void validarPoblacion(int columna, String cps, EquipoCpsiExcel registro) {
        String poblacion = registro.getColumnas().get(columna);

        // Se valida que la población sea obligatoria.
        if (poblacion == null || poblacion.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "Población vacío o nulo."));
        }

        // Se valida que sea numérico.
        if (!NumberUtils.isNumber(poblacion.trim())) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "El código de la población debe ser numérico."));
        } else {
            if (poblacion.trim().length() > 4) {
                registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                        ErrorRegistro.TIPO_AVISO_ERROR, columna, cps,
                        "El código de la población no puede tener más de 4 dígitos."));
            }
        }
    }

    /**
     * Método encargado de validar que el municipio recibido sea correcto.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     */
    private static void validarMunicipio(int columna, String cps, EquipoCpsiExcel registro) {
        String municipio = registro.getColumnas().get(columna);

        // Se valida que la municipio sea obligatorio.
        if (municipio == null || municipio.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "Municipio vacío o nulo."));
        }

        // Se valida que sea numérico.
        if (!NumberUtils.isNumber(municipio.trim())) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "El código del municipio debe ser numérico."));
        } else {
            if (municipio.trim().length() > 3) {
                registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                        ErrorRegistro.TIPO_AVISO_ERROR, columna, cps,
                        "El código del municipio no puede tener más de 3 dígitos."));
            }
        }
    }

    /**
     * Método encargado de validar que el estado recibido sea correcto.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     */
    private static void validarEstado(int columna, String cps, EquipoCpsiExcel registro) {
        String estado = registro.getColumnas().get(columna);

        // Se valida que la estado sea obligatorio.
        if (estado == null || estado.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "Estado vacío o nulo."));
        }

        // Se valida que sea numérico.
        if (!NumberUtils.isNumber(estado.trim())) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "El código del estado debe ser numérico."));
        } else {
            if (estado.trim().length() > 3) {
                registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                        ErrorRegistro.TIPO_AVISO_ERROR, columna, cps,
                        "El código del estado no puede tener más de 2 dígitos."));
            }
        }
    }

    /**
     * Método encargado de validar que la longitud recibida sea correcta.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     */
    private static void validarLongitud(int columna, String cps, EquipoCpsiExcel registro) {
        String longitud = registro.getColumnas().get(columna);

        // Se valida que la longitud sea obligatorio.
        if (longitud == null || longitud.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "Longitud vacía o nula. Se procesa como 0° 0' 0\""));
        }

        if (!LocalizacionUtil.validarLongitud(longitud)) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_WARN,
                    columna, cps,
                    "Longitud: " + longitud + " incorrecta o fuera del área geográfica mexicana."
                            + " Se procesa con el valor 0° 0' 0\"."));

            registro.getColumnas().set(columna, "0° 0' 0\"");
        }
    }

    /**
     * Método encargado de validar que la latitud recibida sea correcta.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     */
    private static void validarLatitud(int columna, String cps, EquipoCpsiExcel registro) {
        String latitud = registro.getColumnas().get(columna);

        // Se valida que la latitud sea obligatorio.
        if (latitud == null || latitud.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "Latitud vacía o nula."));
        }

        if (!LocalizacionUtil.validarLatitud(latitud)) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_WARN,
                    columna, cps,
                    "Latitud: " + latitud + " incorrecta o fuera del área geográfica mexicana."
                            + " Se procesa con el valor 0° 0' 0\"."));
            registro.getColumnas().set(columna, "0° 0' 0\"");
        }
    }

    /**
     * Método encargado de validar que los datos de poblacion, municipio, estado y abn son correctos.
     * @param registro recibido
     * @param cps en binario
     * @param poblacionDao dao de poblaciones
     */
    private static void validarOrganizacionTerritorial(String cps, EquipoCpsiExcel registro,
            IPoblacionDao poblacionDao) {
        String abn = registro.getColumnas().get(EquipoCpsiExcel.POS_ABN);
        String poblacion = registro.getColumnas().get(EquipoCpsiExcel.POS_COD_LOCALIDAD);
        String municipio = registro.getColumnas().get(EquipoCpsiExcel.POS_COD_MUNICIPIO);
        String estado = registro.getColumnas().get(EquipoCpsiExcel.POS_COD_ESTADO);

        if (registro.isProcesarRegistro()) {
            // Se comprueba si existe una poblacion con inegi resultado de concatener los valores recibidos.
            String inegi = StringUtils.leftPad(estado, 2, '0') + StringUtils.leftPad(municipio, 3, '0')
                    + StringUtils.leftPad(poblacion, 4, '0');

            Poblacion pob = poblacionDao.getPoblacionByInegi(inegi);
            if (pob == null) {
                registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                        ErrorRegistro.TIPO_AVISO_ERROR, 0, cps,
                        "Los datos del inegi no corresponden con ninguna población registrada."));
            } else {
                if (Estatus.INACTIVO.equals(pob.getEstatus().getCdg())) {
                    registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(
                            EquipoCpsiExcel.POS_REGISTRO),
                            ErrorRegistro.TIPO_AVISO_ERROR, 0, cps,
                            "Los datos del inegi corresponden con una población en estado inactivo."));
                }

                // Se valida que la población esté asociada al abn
                if (!pob.getAbn().getCodigoAbn().toString().equals(abn)) {
                    registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(
                            EquipoCpsiExcel.POS_REGISTRO),
                            ErrorRegistro.TIPO_AVISO_ERROR, 0, cps,
                            "La población no pertenece al Abn."));
                }
            }
        }

    }

    /**
     * Método encargado de validar que el nombre del equipo no sea nulo.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     */
    private static void validarNombre(int columna, String cps, EquipoCpsiExcel registro) {
        String nombre = registro.getColumnas().get(columna);

        // Se valida que el nombre sea obligatorio.
        if (nombre == null || nombre.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "Nombre del equipo vacía o nulo."));
        } else {
            // Se valida que el nombre no exceda los 100 caracteres.
            if (nombre.length() > 100) {
                registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                        ErrorRegistro.TIPO_AVISO_ERROR,
                        columna, cps,
                        "Nombre del equipo demasiado largo. Longitud máxima: 100. Longitud Real: " + nombre.length()));
            }
        }

    }

    /**
     * Método encargado de validar que la clave del equipo no sea nula.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     */
    private static void validarClave(int columna, String cps, EquipoCpsiExcel registro) {
        String clave = registro.getColumnas().get(columna);

        // Se valida que el nombre sea obligatorio.
        if (clave == null || clave.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "Clave del equipo vacía o nula."));
        } else {
            // Se valida que la clave no exceda los 100 caracteres.
            if (clave.length() > 100) {
                registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                        ErrorRegistro.TIPO_AVISO_ERROR,
                        columna, cps,
                        "Clave del equipo demasiado larga. Longitud máxima: 100. Longitud Real: " + clave.length()));
            }
        }
    }

    /**
     * Método encargado de validar que el tipo del equipo no sea nulo.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     */
    private static void validarTipo(int columna, String cps, EquipoCpsiExcel registro) {
        String tipo = registro.getColumnas().get(columna);

        // Se valida que el tipo sea obligatorio.
        if (tipo == null || tipo.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "Tipo del equipo vacía o nulo."));
        } else {
            // Se valida que el tipo no exceda los 100 caracteres.
            if (tipo.length() > 100) {
                registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                        ErrorRegistro.TIPO_AVISO_ERROR,
                        columna, cps,
                        "Tipo del equipo demasiado largo. Longitud máxima: 100. Longitud Real: " + tipo.length()));
            }
        }
    }

    /**
     * Método encargado de validar el tipo de cps del equipo de señalización.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     */
    private static void validarTipoCps(int columna, String cps, EquipoCpsiExcel registro) {
        String tipo = registro.getColumnas().get(columna);

        // Se valida que el tipo sea obligatorio.
        if (tipo == null || tipo.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "Tipo de CPS del equipo vacía o nulo."));
        } else {
            if (!"1".equals(tipo) && !"I".equals(tipo)) {
                registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                        ErrorRegistro.TIPO_AVISO_ERROR,
                        columna, cps,
                        "Tipo de CPS del equipo inválido. Sólo se importarán los equipos internacionales (I)."));
            }
        }
    }

    /**
     * Método encargado de validar el cps del equipo de señalización.
     * @param columna a validar
     * @param cps en binario
     * @param registro recibido
     * @param pst proveedor a validar
     * @param codigoCpsiDao dao de codigos internacionales
     */
    private static void validarCps(int columna, String cps, EquipoCpsiExcel registro, Proveedor pst,
            ICodigoCpsiDao codigoCpsiDao) {
        String cpsi = registro.getColumnas().get(columna);

        // Se valida que el tipo sea obligatorio.
        if (cpsi == null || cpsi.isEmpty()) {
            registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                    ErrorRegistro.TIPO_AVISO_ERROR,
                    columna, cps,
                    "CPS del equipo vacía o nulo."));
        } else {
            // Se valida que sea numérico.
            if (!NumberUtils.isNumber(cpsi.trim())) {
                registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(EquipoCpsiExcel.POS_REGISTRO),
                        ErrorRegistro.TIPO_AVISO_ERROR,
                        columna, cps,
                        "El Cps debe ser numérico."));
            } else {
                // Se valida que su valor esté en el rango permitido 0 - 16383
                if (CPS_MIN > Integer.parseInt(cpsi) || Integer.parseInt(cpsi) > CPS_MAX) {
                    registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(
                            EquipoCpsiExcel.POS_REGISTRO),
                            ErrorRegistro.TIPO_AVISO_ERROR,
                            columna, cps,
                            "El Cps debe estar entre los valores 0 y 16383."));
                } else {
                    // Se valida que el cps pertenezca al proveedor
                    if (!cpsAsignadoAPst(Integer.parseInt(cpsi), pst, codigoCpsiDao)) {
                        registro.addErrorValidacion(new ErrorRegistro(registro.getColumnas().get(
                                EquipoCpsiExcel.POS_REGISTRO),
                                ErrorRegistro.TIPO_AVISO_ERROR,
                                columna, cps,
                                "El Cps " + cpsi + " no está asignado al proveedor '" + pst.getNombre() + "'."));
                    }
                }
            }
        }
    }

    /**
     * Función encargada de comprobar si un código cps esá asignado a un proveedor.
     * @param cps código
     * @param pst proveedor
     * @param codigoCpsiDao dao de códigos
     * @return si está asignado
     */
    private static boolean cpsAsignadoAPst(Integer cps, Proveedor pst, ICodigoCpsiDao codigoCpsiDao) {
        CodigoCPSI codigo = null;
        boolean asignado = false;

        // Se comprueba si el código existe como cps de dicho bloque
        codigo = codigoCpsiDao.getCodigoCpsi(new BigDecimal(cps), pst);
        if (codigo != null) {
            if (codigo.getProveedor() != null && pst.getId().equals(codigo.getProveedor().getId())) {
                asignado = true;
            } else {
                asignado = false;
            }
        }

        return asignado;
    }

    /**
     * Método encargado de validar la UK de la tabla de equipos: Nombre, Longitud, Latitud, pst y cps.
     * @param equiposValidos listado de equipos validos
     * @param equipo a validar
     * @return si está duplicado
     */
    private static boolean validarDuplicados(List<EquipoCpsiExcel> equiposValidos, EquipoCpsiExcel equipo) {

        if (equiposValidos != null) {

            String tipoEquipo = equipo.getColumnas().get(EquipoCpsnExcel.POS_TIPO_CPS);
            tipoEquipo = (tipoEquipo.equals("1") || tipoEquipo.equals("I")) ? "I" : "N";

            for (EquipoCpsiExcel equip : equiposValidos) {
                if (equip.getColumnas().get(EquipoCpsiExcel.POS_CPS)
                        .equals(equipo.getColumnas().get(EquipoCpsiExcel.POS_CPS))
                        && equip.getColumnas().get(EquipoCpsiExcel.POS_LONGITUD)
                                .equals(equipo.getColumnas().get(EquipoCpsiExcel.POS_LONGITUD))
                        && equip.getColumnas().get(EquipoCpsiExcel.POS_LATITUD)
                                .equals(equipo.getColumnas().get(EquipoCpsiExcel.POS_LATITUD))
                        && equip.getColumnas().get(EquipoCpsiExcel.POS_NOMBRE)
                                .equals(equipo.getColumnas().get(EquipoCpsiExcel.POS_NOMBRE))) {

                    String tipo = equip.getColumnas().get(EquipoCpsnExcel.POS_TIPO_CPS);
                    tipo = (tipo.equals("1") || tipo.equals("I")) ? "I" : "N";

                    if (tipo.equals(tipoEquipo)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
