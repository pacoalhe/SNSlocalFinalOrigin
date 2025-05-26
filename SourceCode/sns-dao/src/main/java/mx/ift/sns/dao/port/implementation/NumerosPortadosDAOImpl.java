package mx.ift.sns.dao.port.implementation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            final String sql = "SELECT NUMBERFROM, ACTION, ACTIONDATE,   DCR, DIDA, ISMPP,"
                    + "NUMBERTO, PORTID, PORTTYPE, RCR, RIDA FROM PORT_NUM_PORTADO WHERE (NUMBERFROM = '"
                    + numero + "')";

            Query consulta = getEntityManager().createNativeQuery(sql, NumeroPortado.class);

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

        // Para productivo (usa la fecha real)
        /* TODO FJAH desbloquear para productivo
        Date fechaHoy = sdf.parse(sdf.format(new Date()));
         */

        //TODO FJAH quitar cuando se terminen las pruebas locales
        // Para pruebas locales: forzar fecha
        Date fechaHoy = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // Forzar la fecha solo para pruebas retroactivas
            fechaHoy = sdf.parse("2025-05-08");
        } catch (ParseException e) {
            e.printStackTrace();
            LOGGER.error("Error al parsear la fecha forzada para pruebas retroactivas", e);
            return BigDecimal.ZERO; // O maneja el error según tu lógica
        }
        //Termina pruebas locales

        String sQuery = "SELECT COUNT(n) FROM NumeroPortado n WHERE n.actionDate > :hoy";

        TypedQuery<Long> tQuery = getEntityManager().createQuery(sQuery, Long.class);
        tQuery.setParameter("hoy", fechaHoy);

        Long total = tQuery.getSingleResult();
        LOGGER.info("La cantidad de numeros portados es: " + total);
        return new BigDecimal(total);
    }
}
