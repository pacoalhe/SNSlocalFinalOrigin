package mx.ift.sns.dao.nng.implementation;

import java.math.BigDecimal;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.IReporteNngDao;
import mx.ift.sns.modelo.lineas.ReporteNng;

/**
 * Implemntacion del DAO de reportes NNG.
 * @author X36155QU
 */
@Named
public class ReporteNngDaoImpl extends BaseDAO<ReporteNng> implements IReporteNngDao {

    @Override
    public ReporteNng getReporteById(BigDecimal idReporte) {

        return getEntityManager().find(ReporteNng.class, idReporte);
    }

}
