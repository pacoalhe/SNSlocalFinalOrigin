package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.INumeracionRedistribuidaDAO;
import mx.ift.sns.modelo.ng.NumeracionRedistribuida;

/**
 * Implementación DAO para NumeracionRedistribuida.
 * @author X53490DE
 */
@Named
public class NumeracionRedistribuidaDaoImpl extends BaseDAO<NumeracionRedistribuida> implements
        INumeracionRedistribuidaDAO {

    @Override
    public List<NumeracionRedistribuida> getNumeracionRedistribuidaById(BigDecimal pIdRedistSol) {

        String jpql = "SELECT nr FROM NumeracionRedistribuida nr WHERE nr.solicitudRedistribucion.id = :idRedistSol";
        TypedQuery<NumeracionRedistribuida> tQuery = this.getEntityManager().createQuery(jpql,
                NumeracionRedistribuida.class);
        tQuery.setParameter("idRedistSol", pIdRedistSol);
        return tQuery.getResultList();
    }

}
