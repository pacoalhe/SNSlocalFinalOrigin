package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IAbnConsolidarDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ng.AbnConsolidar;
import mx.ift.sns.modelo.ot.Poblacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de centrales de áreas de numeración.
 */
@Named
public class AbnConsolidarDaoImpl extends BaseDAO<AbnConsolidar> implements IAbnConsolidarDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbnConsolidarDaoImpl.class);

    @Override
    public AbnConsolidar getAbnConsolidarByIdSol(BigDecimal pIdSol) {
        String query = "SELECT a FROM AbnConsolidar a where a.solicitudConsolidacion.id = :idSol";
        TypedQuery<AbnConsolidar> tQuery = getEntityManager().createQuery(query, AbnConsolidar.class);
        tQuery.setParameter("idSol", pIdSol);

        AbnConsolidar abnConso = tQuery.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado un abnConsolidar con id: {}",
                    abnConso.getId());
        }
        return abnConso;
    }

    @Override
    public String getFechaConsolidacionByRangoAbn(Abn rangoAbn) {
        String fechaConsolidacion = null;
        try {
            String query = "SELECT MAX(ac.fechaConsolidacion) FROM AbnConsolidar ac "
                    + "where ac.solicitudConsolidacion.abnRecibe = :abn";
            TypedQuery<Date> tQuery = getEntityManager().createQuery(query, Date.class);
            tQuery.setParameter("abn", rangoAbn);
            Date fecha = tQuery.getSingleResult();
            if (null != fecha) {
                Locale localeES = new Locale("es", "ES");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", localeES);
                fechaConsolidacion = sdf.format(fecha);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Se han encontrado la fecha de consolidacion ", fechaConsolidacion);
            }

        } catch (NoResultException e) {
            return null;
        }
        return fechaConsolidacion;
    }

    @Override
    public Date getFechaConsolidacionPoblacion(Poblacion poblacion) {

        Date fecha = null;

        try {
            StringBuffer sbQuery = new StringBuffer("SELECT MAX(ac.fechaConsolidacion) FROM AbnConsolidar ac ");
            sbQuery.append("where ac.solicitudConsolidacion.abnRecibe = :abn");

            TypedQuery<Date> tQuery = getEntityManager().createQuery(sbQuery.toString(), Date.class);
            tQuery.setParameter("abn", poblacion.getAbn());

            fecha = tQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return fecha;
    }
}
