package mx.ift.sns.dao.pst.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pst.ITipoContactoDao;
import mx.ift.sns.modelo.pst.TipoContacto;

/**
 * Implementación de los métodos para base de datos de tipos de contacto.
 */
@Named
public class TipoContactoDaoImpl extends BaseDAO<TipoContacto> implements ITipoContactoDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<TipoContacto> findAllTiposContacto() {
        return getEntityManager().createQuery("SELECT t FROM TipoContacto t").getResultList();
    }

    @Override
    public TipoContacto getTipoContactoByCdg(String pCdgTipo) {
        Query query = getEntityManager().createQuery("SELECT t FROM TipoContacto t WHERE t.cdg = :cdg");
        query.setParameter("cdg", pCdgTipo);

        return (TipoContacto) query.getSingleResult();
    }
}
