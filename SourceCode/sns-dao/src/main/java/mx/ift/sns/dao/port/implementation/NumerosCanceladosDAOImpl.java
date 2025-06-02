package mx.ift.sns.dao.port.implementation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.port.INumerosCanceladosDAO;
import mx.ift.sns.modelo.port.NumeroCancelado;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del DAO de Numeros portados.
 */
@Named
public class NumerosCanceladosDAOImpl extends BaseDAO<NumeroCancelado> implements INumerosCanceladosDAO {

    //FECHA INICIAL
    public static final Date FECHA_INICIAL;
    static {
        try {
            //FECHA_INICIAL = new SimpleDateFormat("dd.MM.yyyy").parse("30.05.2025");

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            FECHA_INICIAL = sdf.parse(sdf.format(new Date()));

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    //termina fecha inicial

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumerosCanceladosDAOImpl.class);

    /** Localización en Castellano. */
    private static Locale localeES = new Locale("es", "ES");

    /** Parseador de fechas con formato simple. */
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", localeES);

    @Override
    public NumeroCancelado get(String numero) {

        final String sql = "SELECT NUMBERFROM, ACTION, ACTIONDATE, ASSIGNEECR, ASSIGNEEIDA, DCR, DIDA, ISMPP,"
                + "NUMBERTO, PORTID, PORTTYPE, RCR, RIDA FROM PORT_NUM_CANCELADO WHERE (NUMBERFROM = '"
                + numero + "')";

        Query consulta = getEntityManager().createNativeQuery(sql, NumeroCancelado.class);

        NumeroCancelado resultado = (NumeroCancelado) consulta.getSingleResult();

        return resultado;
    }

    @Override
    public void delete(String numero) {
        final String sql = "DELETE FROM NumeroCancelado n where n.numberFrom= :num";
        Query updateQuery = getEntityManager().createQuery(sql);
        updateQuery.setParameter("num", numero);

        updateQuery.executeUpdate();
    }

    //FJAH 26052025 Refatorización de la consulta procesados
    @Override
    public BigDecimal getTotalNumerosCanceladosHoy() throws Exception {
        // TODO FJAH 26052025 desbloquear para productivo
        //Date fechaHoy = sdf.parse(sdf.format(new Date()));
        Date fechaHoy = FECHA_INICIAL; // Debe ser a las 00:00:00.000

        //String sQuery = "SELECT COUNT(n) FROM NumeroCancelado n WHERE n.actionDate > :hoy";
        String sQuery = "SELECT COUNT(n) FROM NumeroCancelado n WHERE FUNCTION('TRUNC', n.actionDate) = :fecha";
        TypedQuery<Long> tQuery = getEntityManager().createQuery(sQuery, Long.class);

        tQuery.setParameter("fecha", fechaHoy);

        Long total = tQuery.getSingleResult();
        LOGGER.info("La cantidad de numeros cancelados ES DEL DIA {}: {}", fechaHoy, total);
        return new BigDecimal(total);
    }

    //FJAH 27.05.2025 Refactorizacion, Obtener los totales en base actiondate del xml procesado
    @Override
    public BigDecimal getTotalNumerosCanceladosPorFecha(Date actionDate) throws Exception {
        Date fecha = truncarHora(actionDate); // Asegurar la hora en 00:00:00 - la fecha obtenida del archivo CSV
        String sQuery = "SELECT COUNT(n) FROM NumeroCancelado n WHERE FUNCTION('TRUNC', n.actionDate) = :fecha";
        TypedQuery<Long> tQuery = getEntityManager().createQuery(sQuery, Long.class);
        tQuery.setParameter("fecha", fecha);
        Long total = tQuery.getSingleResult();
        LOGGER.info("Cantidad de números cancelados para el actionDate {}: {}", fecha, total);
        return new BigDecimal(total);
    }

    public static Date truncarHora(Date fechaOriginal) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaOriginal);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


}
