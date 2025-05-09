package mx.ift.sns.dao.port.implementation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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

    @Override
    public BigDecimal getTotalNumerosCanceladosHoy() throws Exception {

        Date fechaHoy = sdf.parse(sdf.format(new Date()));

        String sQuery = "SELECT COUNT(n) FROM NumeroCancelado n WHERE n.actionDate > :hoy";

        TypedQuery<Long> tQuery = getEntityManager().createQuery(sQuery, Long.class);
        tQuery.setParameter("hoy", fechaHoy);

        Long total = tQuery.getSingleResult();

        return new BigDecimal(total);
    }
}
