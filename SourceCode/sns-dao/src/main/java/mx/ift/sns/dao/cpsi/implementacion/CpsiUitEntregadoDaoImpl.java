package mx.ift.sns.dao.cpsi.implementacion;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.cpsi.ICpsiUitEntregadoDao;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.CpsiUitEntregado;

/**
 * Implementación de los métodos para base de datos de codigod CPS internacionales.
 * @author X50880SA
 */
@Named
public class CpsiUitEntregadoDaoImpl extends BaseDAO<CodigoCPSI> implements ICpsiUitEntregadoDao {

    @Override
    public CpsiUitEntregado getByIdCpsi(Integer idCpsi) throws Exception {
        String query = "SELECT cpsiUitEntregado "
                + "FROM CpsiUitEntregado cpsiUitEntregado"
                + " WHERE cpsiUitEntregado.idCpsi = :idCpsi";

        TypedQuery<CpsiUitEntregado> consulta = getEntityManager().createQuery(query, CpsiUitEntregado.class);
        consulta.setParameter("idCpsi", idCpsi);

        CpsiUitEntregado cpsiUitEntregado = null;
        try {
            cpsiUitEntregado = consulta.getSingleResult();
        } catch (NoResultException nre) {

        }

        return cpsiUitEntregado;
    }

}
