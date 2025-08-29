package mx.ift.sns.dao.port.implementation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.port.INumerosPortadosDAO;
import mx.ift.sns.modelo.port.NumeroPortado;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del DAO de Numeros portados.
 */
@Named
public class NumerosPortadosDAOImpl extends BaseDAO<NumeroPortado> implements INumerosPortadosDAO {

    /*
    //FECHA_PROCESO
    private Date fechaproceso() {
        try {
            //date FECHA_PROCESO = new SimpleDateFormat("dd.MM.yyyy").parse("02.06.2025");
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            return sdf.parse(sdf.format(new Date())); // Siempre regresa la fecha actual con hora 00:00:00.000
        } catch (ParseException e) {
            LOGGER.error("Error al obtener la fecha de proceso", e);
            return null;
        }
    }
    //termina FECHA_PROCESO

     */

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumerosPortadosDAOImpl.class);

    /** Localización en Castellano. */
    private static Locale localeES = new Locale("es", "ES");

    /** Parseador de fechas con formato simple. */
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", localeES);

    @Override
    public NumeroPortado get(String numero) {

        NumeroPortado res = null;

        try {
            final String sql = "SELECT NUMBERFROM, ACTION, ACTIONDATE, DCR, DIDA, ISMPP, " +
                    "NUMBERTO, PORTID, PORTTYPE, RCR, RIDA " +
                    "FROM PORT_NUM_PORTADO WHERE NUMBERFROM = ?";

            Query consulta = getEntityManager().createNativeQuery(sql, NumeroPortado.class);
            consulta.setParameter(1, numero);

            @SuppressWarnings("unchecked")
            List<NumeroPortado> list = consulta.getResultList();

            if ((list != null) && (list.size() > 0)) {
                res = list.get(0);
            }
        } catch (NoResultException e) {
            return null;
        }

        return res;
    }

    @Override
    public void delete(String numero) {

        try {
            final String sql = "DELETE FROM NumeroPortado n where n.numberFrom= :num";
            Query updateQuery = getEntityManager().createQuery(sql);
            updateQuery.setParameter("num", numero);

            updateQuery.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("error dao " + numero, e);
        }
    }

    @Override
    public BigDecimal getTotalNumerosPortadosHoy() throws Exception {
        // TODO FJAH 26052025 desbloquear para productivo
        Date fechaHoy = sdf.parse(sdf.format(new Date()));
        //Date fechaHoy = fechaproceso(); // Debe ser a las 00:00:00.000

        //String sQuery = "SELECT COUNT(n) FROM NumeroPortado n WHERE n.actionDate > :hoy";
        String sQuery = "SELECT COUNT(n) FROM NumeroPortado n WHERE FUNCTION('TRUNC', n.actionDate) = :fecha";
        TypedQuery<Long> tQuery = getEntityManager().createQuery(sQuery, Long.class);

        tQuery.setParameter("fecha", fechaHoy);

        Long total = tQuery.getSingleResult();
        LOGGER.info("La cantidad de numeros portados ES DEL DIA {}: {}", fechaHoy, total);
        return new BigDecimal(total);
    }

    //FJAH 27.05.2025 Refactorizacion, Obtener los totales en base actiondate del xml procesado
    @Override
    public BigDecimal getTotalNumerosPortadosPorFecha(Date actionDate) throws Exception {
        Date fecha = truncarHora(actionDate); // Asegurar la hora en 00:00:00 - la fecha obtenida del archivo CSV
        String sQuery = "SELECT COUNT(n) FROM NumeroPortado n WHERE FUNCTION('TRUNC', n.actionDate) = :fecha";
        TypedQuery<Long> tQuery = getEntityManager().createQuery(sQuery, Long.class);
        tQuery.setParameter("fecha", fecha);
        Long total = tQuery.getSingleResult();
        LOGGER.info("Cantidad de números portados para el actionDate {}: {}", fecha, total);
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

    public List<Object[]> getDiferenciasTmpVsFinal() {
        String sql = "SELECT t.PORTID, t.NUMBERFROM, t.NUMBERTO, t.ACTIONDATE " +
                "FROM TMP_NUM_PORTADO t " +
                "LEFT JOIN PORT_NUM_PORTADO p " +
                "  ON t.PORTID = p.PORTID " +
                " AND t.NUMBERFROM = p.NUMBERFROM " +
                "WHERE p.PORTID IS NULL " +
                "   OR NVL(t.NUMBERTO, 'X') <> NVL(p.NUMBERTO, 'X') " +
                "   OR NVL(t.ACTIONDATE, TO_DATE('1900-01-01','YYYY-MM-DD')) <> NVL(p.ACTIONDATE, TO_DATE('1900-01-01','YYYY-MM-DD'))";
        Query query = getEntityManager().createNativeQuery(sql);
        return query.getResultList();
    }

}
