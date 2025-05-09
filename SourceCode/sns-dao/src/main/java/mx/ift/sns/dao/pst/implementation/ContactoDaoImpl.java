package mx.ift.sns.dao.pst.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pst.IContactoDao;
import mx.ift.sns.modelo.pst.Contacto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de contactos de proveedor.
 */
@Named
public class ContactoDaoImpl extends BaseDAO<Contacto> implements IContactoDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactoDaoImpl.class);

    @Override
    public List<Contacto> getRepresentantesLegales(String pTipoContacto, BigDecimal pIdProveedor) {
        String strquery = "SELECT p FROM Contacto p "
                + "WHERE p.tipoContacto.cdg=:codigoTipo AND p.proveedor.id=:idProveedor";

        TypedQuery<Contacto> query = getEntityManager().createQuery(strquery, Contacto.class);
        query.setParameter("codigoTipo", pTipoContacto);
        query.setParameter("idProveedor", pIdProveedor);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando contactos. idProveedor: {}, Tipo Contacto: {}",
                    pIdProveedor.toString(),
                    pTipoContacto);
        }

        List<Contacto> resultado = query.getResultList();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Contactos encontrados: " + resultado.size());
        }

        return resultado;
    }

    @Override
    public Contacto getRepresentanteLegalById(BigDecimal pIdContacto) {
        return getEntityManager().find(Contacto.class, pIdContacto);
    }

    @Override
    public boolean contactoNoUsado(Contacto contacto) {
        String query = "";

        query = "SELECT COUNT(1) FROM VContactoTramite ct WHERE ct.idRepLegal = :idContactoLeg "
                + "OR ct.idRepSup = :idContactoSup";
        TypedQuery<Long> tQuery = getEntityManager().createQuery(query, Long.class);
        tQuery.setParameter("idContactoLeg", contacto.getId());
        tQuery.setParameter("idContactoSup", contacto.getId());
        Long resultado = tQuery.getSingleResult();
        return resultado == 0;
    }

    @Override
    public Contacto getContactoById(BigDecimal id) {
        String query = "SELECT co FROM Contacto co WHERE co.id=:id";

        Contacto contacto = null;
        try {
            TypedQuery<Contacto> tQuery = getEntityManager().createQuery(query, Contacto.class);
            tQuery.setParameter("id", id);
            contacto = tQuery.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }

        return contacto;
    }

}
