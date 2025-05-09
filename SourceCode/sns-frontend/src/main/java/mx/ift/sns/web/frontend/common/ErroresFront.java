package mx.ift.sns.web.frontend.common;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.series.Nir;

/**
 * Codificacion de erorres del frontend sns.
 */
public enum ErroresFront {

    /** */
    ERROR_0000(0),

    /** ABN invalido. */
    ERROR_0001(1),

    /** NIR invalido. */
    ERROR_0002(2),

    /** SNA invalido. */
    ERROR_0003(3),

    /** Error en BDDD. */
    ERROR_0004(4),

    /** Filtro vacio. */
    ERROR_0005(5),

    /** Fechas. */
    ERROR_0006(6),

    /** Consecutivo. */
    ERROR_0007(7),

    /** Debe grabar antes de poder ir al paso siguiente. */
    ERROR_0008(8),

    /** Clave censal invalida. */
    ERROR_0009(9);

    /** Descripcion de los errores. */
    private static String[] descErrores = new String[]{

            /* ERROR_0000 */"",
            /* ERROR_0001 */"ABN Invalido. Debe ser un número entre " + Abn.MIN_ABN + " y " + Abn.MAX_ABN + ".",
            /* ERROR_0002 */"NIR Invalido. Debe ser un número entre " + Nir.MIN_NIR + " y " + Nir.MAX_NIR + ".",
            /* ERROR_0003 */"SNA Invalido. Debe ser un número entre" + Serie.MIN_SERIE + " y " + Serie.MAX_SERIE + ".",
            /* ERROR_0004 */"Error interno. Consulte con su administrador.",
            /* ERROR_0005 */"Seleccione al menos un campo.",
            /* ERROR_0006 */"Fecha final debe ser mayor que Fecha inicial.",
            /* ERROR_0007 */"Consecutivo ha de tener un formato máximo de 9 dígitos.",
            /* ERROR_0008 */"Es necesario guardar para ir al siguiente paso.",
            /* ERROR_0009 */"Clave censal invalida."
    };

    /** Map de los errores. */
    private static final Map<Integer, ErroresFront> LOOKUP = new HashMap<Integer, ErroresFront>();

    static {
        for (ErroresFront s : EnumSet.allOf(ErroresFront.class)) {
            LOOKUP.put(s.getCode(), s);
        }
    }

    /** */
    private int code;

    /**
     * Constructor privado.
     * @param code codigo error
     */
    private ErroresFront(int code) {
        this.code = code;
    }

    /**
     * Devuelve el id.
     * @return id
     */
    public int getCode() {
        return code;
    }

    /**
     * Devuelve el error asociado al numero.
     * @param code codigo
     * @return error
     */
    public static ErroresFront get(int code) {
        return LOOKUP.get(code);
    }

    /**
     * Devuelve la descripcion de un error.
     * @param code codigo
     * @return descripcion
     */
    public static String getDescripcionError(int code) {
        ErroresFront e = LOOKUP.get(code);
        return descErrores[e.getCode()];
    }

    /**
     * Devuelve la descripcion de un error.
     * @param error a buscar
     * @return descripcion
     */
    public static String getDescripcionError(ErroresFront error) {
        return descErrores[error.getCode()];
    }
}
