package mx.ift.sns.web.backend.common;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.series.Nir;

/**
 * Codificacion de erorres del backend sns.
 */
public enum Errores {

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
    ERROR_0009(9),

    /** Clave censal invalida. */
    ERROR_0010(10),

    /** Sin numeraciones seleccionadas. */
    ERROR_0011(11),

    /** Consecutivo invalido. */
    ERROR_0012(12),

    /** Serie no geográfica invalida. */
    ERROR_0013(13),

    /** Clave de servicio invalida. */
    ERROR_0014(14),

    /** Registro modificado. */
    ERROR_0015(15);

    /** Descripcion de los errores. */
    private static String[] descErrores = new String[]{

            /* ERROR_0000 */"",
            /* ERROR_0001 */"ABN Invalido. Debe ser un número entre " + Abn.MIN_ABN + " y " + Abn.MAX_ABN + ".",
            /* ERROR_0002 */"NIR Invalido. Debe ser un número entre " + Nir.MIN_NIR + " y " + Nir.MAX_NIR + ".",
            /* ERROR_0003 */"SNA Invalido. Debe ser un número entre " + Serie.MIN_SERIE + " y " + Serie.MAX_SERIE + ".",
            /* ERROR_0004 */"Error interno. Consulte con su administrador.",
            /* ERROR_0005 */"Seleccione al menos un campo.",
            /* ERROR_0006 */"Fecha final debe ser mayor que Fecha inicial.",
            /* ERROR_0007 */"Consecutivo ha de tener un formato máximo de 9 dígitos.",
            /* ERROR_0008 */"Es necesario guardar para ir al siguiente paso.",
            /* ERROR_0009 */"Clave censal invalida.",
            /* ERROR_0010 */"Número no válido.",
            /* ERROR_0011 */"No hay numeraciones seleccionadas.",
            /* ERROR_0012 */"Consecutivo no valido.",
            /* ERROR_0013 */"Serie invalida. Debe ser un número entre " + SerieNng.MIN_SERIE_NNG + " y "
                    + SerieNng.MAX_SERIE_NNG + ".",
            /* ERROR_00014 */"Clave de servicio invalida.",
            /* ERROR_00015 */"Registro modificado por otro usuario. Repita la operación."
    };

    /** Map de los errores. */
    private static final Map<Integer, Errores> LOOKUP = new HashMap<Integer, Errores>();

    static {
        for (Errores s : EnumSet.allOf(Errores.class)) {
            LOOKUP.put(s.getCode(), s);
        }
    }

    /** */
    private int code;

    /**
     * Constructor privado.
     * @param code codigo error
     */
    private Errores(int code) {
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
    public static Errores get(int code) {
        return LOOKUP.get(code);
    }

    /**
     * Devuelve la descripcion de un error.
     * @param code codigo
     * @return descripcion
     */
    public static String getDescripcionError(int code) {
        Errores e = LOOKUP.get(code);
        return descErrores[e.getCode()];
    }

    /**
     * Devuelve la descripcion de un error.
     * @param error a buscar
     * @return descripcion
     */
    public static String getDescripcionError(Errores error) {
        return descErrores[error.getCode()];
    }
}
