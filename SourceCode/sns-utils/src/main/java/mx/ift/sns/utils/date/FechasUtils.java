package mx.ift.sns.utils.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utilidades para el manejo de Fechas (java.util.date).
 * @author X53490DE
 */
public final class FechasUtils {

    /** Fecha y hora. */
    public static final String FORMATO_FECHA_HORA = "dd/MM/yyyy HH:mm:ss";

    /** Localización en Castellano. */
    private static Locale localeES = new Locale("es", "ES");

    /** Parseador de fechas con formato simple. */
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", localeES);

    /** Parseador de fechas con formato personalizado. */
    private static SimpleDateFormat sdfCustom;

    /** Constructor privador. */
    private FechasUtils() {

    }

    /**
     * Convierte una fecha a String con formato simple "dd/MM/yyyy".
     * @param pFecha Objeto Fecha
     * @return String con formato simple "dd/MM/yyyy"
     */
    public static String fechaToString(Date pFecha) {
        SimpleDateFormat sdfe = new SimpleDateFormat("dd/MM/yyyy", localeES);
        if (pFecha != null) {
            return sdfe.format(pFecha);
        } else {
            return "";
        }
    }

    /**
     * Convierte una fecha a String con el formato indicado por parámetros.
     * @param pFecha Objeto Fecha
     * @param pFormat Formato de Fecha
     * @return String con el formato indicado por parámetros.
     */
    public static String fechaToString(Date pFecha, String pFormat) {
        if (pFecha != null && pFormat != null) {
            try {
                sdfCustom = new SimpleDateFormat(pFormat, localeES);
                return sdfCustom.format(pFecha);
            } catch (Exception e) {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * Convierte un String a un objeto Fecha con formato simple "dd/MM/yyyy".
     * @param pFecha String Fecha
     * @return Fecha con formato simple "dd/MM/yyyy".
     */
    public static Date stringToFecha(String pFecha) {
        if (pFecha != null) {
            try {
                return sdf.parse(pFecha);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Convierte un String a un objeto Fecha con el formato indicado por parámetros.
     * @param pFecha String Fecha
     * @param pFormat Formato de Fecha
     * @return Fecha con formato simple "dd/MM/yyyy".
     */
    public static Date stringToFecha(String pFecha, String pFormat) {
        if (pFecha != null) {
            try {
                sdfCustom = new SimpleDateFormat(pFormat, localeES);
                return sdfCustom.parse(pFecha);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Devuelve la fecha actual con formato simple "dd/MM/yyyy".
     * @return fecha actual con formato simple "dd/MM/yyyy".
     */
    public static Date getFechaHoy() {
        try {
            return sdf.parse(sdf.format(new Date()));
        } catch (ParseException pe) {
            return null;
        }
    }

    /**
     * Devuelve la fecha actual con el formato indicado por parámetros.
     * @param pFormat Formato de Fecha
     * @return fecha actual con el formato indicado por parámetros.
     */
    public static Date getFechaHoy(String pFormat) {
        try {
            sdfCustom = new SimpleDateFormat(pFormat, localeES);
            return sdfCustom.parse(sdfCustom.format(new Date()));
        } catch (Exception pe) {
            return null;
        }
    }

    /**
     * Calcula una fecha a partir del día actual y sumándole los días/meses/años indicados por parámetros.
     * @param pDias Número de días a sumar a la fecha actual.
     * @param pMeses Número de meses a sumar a la fecha actual.
     * @param pAnios Número de años a sumar a la fecha actual.
     * @return Date
     */
    public static Date calculaFecha(int pDias, int pMeses, int pAnios) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, pDias);
            calendar.add(Calendar.MONTH, pMeses);
            calendar.add(Calendar.YEAR, pAnios);
            return sdf.parse(sdf.format(calendar.getTime()));
        } catch (Exception pe) {
            return null;
        }
    }

    /**
     * Calcula una fecha a partir de una fecha indicada y sumándole los días/meses/años indicados por parámetros.
     * @param pFecha fecha de inicio.
     * @param pDias Número de días a sumar a la fecha actual.
     * @param pMeses Número de meses a sumar a la fecha actual.
     * @param pAnios Número de años a sumar a la fecha actual.
     * @return Date
     */
    public static Date calculaFecha(Date pFecha, int pDias, int pMeses, int pAnios) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pFecha);
            calendar.add(Calendar.DAY_OF_YEAR, pDias);
            calendar.add(Calendar.MONTH, pMeses);
            calendar.add(Calendar.YEAR, pAnios);
            return sdf.parse(sdf.format(calendar.getTime()));
        } catch (Exception pe) {
            return null;
        }
    }

    /**
     * Calcula una fecha a partir del día actual y sumándole los días/meses/años indicados por parámetros.
     * @param pDias Número de días a sumar a la fecha actual.
     * @param pMeses Número de meses a sumar a la fecha actual.
     * @param pAnios Número de años a sumar a la fecha actual.
     * @param pMinutos Número de minutos a sumar a la fecha actual.
     * @param pSegundos Número de segundos a sumar a la fecha actual.
     * @return Date
     */
    public static Date calculaFecha(int pDias, int pMeses, int pAnios, int pMinutos, int pSegundos) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, pDias);
            calendar.add(Calendar.MONTH, pMeses);
            calendar.add(Calendar.YEAR, pAnios);
            calendar.add(Calendar.MINUTE, pMinutos);
            calendar.add(Calendar.SECOND, pSegundos);
            return sdf.parse(sdf.format(calendar.getTime()));
        } catch (Exception pe) {
            return null;
        }
    }

    /**
     * Calcula una fecha a partir del día actual y sumándole los días/meses/años indicados por parámetros.
     * @param pFecha fecha de inicio.
     * @param pDias Número de días a sumar a la fecha actual.
     * @param pMeses Número de meses a sumar a la fecha actual.
     * @param pAnios Número de años a sumar a la fecha actual.
     * @param pMinutos Número de minutos a sumar a la fecha actual.
     * @param pSegundos Número de segundos a sumar a la fecha actual.
     * @return Date
     */
    public static Date calculaFecha(Date pFecha, int pDias, int pMeses, int pAnios, int pMinutos, int pSegundos) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pFecha);
            calendar.add(Calendar.DAY_OF_YEAR, pDias);
            calendar.add(Calendar.MONTH, pMeses);
            calendar.add(Calendar.YEAR, pAnios);
            calendar.add(Calendar.MINUTE, pMinutos);
            calendar.add(Calendar.SECOND, pSegundos);
            return sdf.parse(sdf.format(calendar.getTime()));
        } catch (Exception pe) {
            return null;
        }
    }

    /**
     * Convierte una fecha dada al formato simple "dd/MM/yyyy".
     * @param pFecha Objeto Fecha.
     * @return fecha con formato simple "dd/MM/yyyy".
     */
    public static Date parseFecha(Date pFecha) {
        try {
            return sdf.parse(sdf.format(pFecha));
        } catch (Exception pe) {
            return null;
        }
    }

    /**
     * Convierte una fecha dada al formato indicado por parámetros.
     * @param pFecha Objeto Fecha.
     * @param pFormat Formato de Fecha
     * @return fecha con elformato indicado por parámetros.
     */
    public static Date parseFecha(Date pFecha, String pFormat) {
        try {
            sdfCustom = new SimpleDateFormat(pFormat, localeES);
            return sdfCustom.parse(sdfCustom.format(pFecha));
        } catch (Exception pe) {
            return null;
        }
    }

    /**
     * Dada una fecha de implementación de un trámite (Cesión, Liberación, etc) devuelve la fecha de implementación real
     * que siempre es el día anterior a las 23:00h.
     * @param pFechaImplementacion Fecha de implementación seleccionada.
     * @return Fecha de Implementación Real
     * @throws Exception en caso de error al parsear fechas.
     */
    public static Date getFechaImplementacionReal(Date pFechaImplementacion) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pFechaImplementacion);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Día Anterior
        calendar.add(Calendar.HOUR_OF_DAY, 22); // Las 23h (Hora de 0 a 23)
        calendar.add(Calendar.MINUTE, -1); // Las 22:59h

        // La fecha de implementación real, cuando se ha de ejecutar el trámite, corresponde con
        // el día anterior a las 23h. El servidor se ejecuta a las 23:00, por lo que al comparar
        // restamos 1 minutos para que no compare con la misma hora exactamente.

        return calendar.getTime();
    }

    /**
     * Dada una fecha de implementación de un trámite programado devuelve la fecha a partir de la cual se ha de
     * notificar al usuario que el trámite está pendiente. La fecha de notificación es a partir de 2 días antes de la
     * fecha de implementación.
     * @param pFechaImplementacion Fecha de implementación del trámite programado.
     * @return 2 días antes de la fecha de implementación.
     * @throws Exception en caso de error al parsear las fechas.
     */
    public static Date getFechaNotificacion(Date pFechaImplementacion) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pFechaImplementacion);
        calendar.add(Calendar.DAY_OF_MONTH, -2); // 2 días antes
        calendar.add(Calendar.HOUR_OF_DAY, 0); // Las 00 (primer hora del día)
        return calendar.getTime();
    }

    /**
     * Recupera el número de días comprendido entre dos fechas dadas.
     * @param pFechaInicial Fecha inicial.
     * @param pFechaFinal Fecha final.
     * @return número de días entre las dos fechas
     */
    public static long getDiasEntre(Date pFechaInicial, Date pFechaFinal) {
        try {
            long diff = 0;
            long timeDiff = Math.abs(pFechaInicial.getTime() - pFechaFinal.getTime());
            diff = TimeUnit.MILLISECONDS.toDays(timeDiff);
            return diff;
        } catch (Exception ex) {
            return 0L;
        }
    }

    /**
     * Comprueba si la fecha dada es hoy.
     * @param date fecha a comprobar
     * @return true si es hoy
     */
    public static boolean esHoy(Date date) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date());

        return (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH))
                && (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
                && (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
    }

    /**
     * Retorna un calendario, seteando la fecha pasada por parámetro y, seteándole también el último momento del día
     * @param date fecha
     * @return calendario con la fecha y el último momento del día
     */
    public static Calendar dateToCalendarLastMomentOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }
    
    /**
     * Devuelve la fecha actual con formato dd/MM/yyyy HH:mm:ss.
     * @return fecha actual con formato dd/MM/yyyy HH:mm:ss
     */
    public static String getActualDate(){
		DateFormat dateFormat = new SimpleDateFormat(FORMATO_FECHA_HORA);
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

}
