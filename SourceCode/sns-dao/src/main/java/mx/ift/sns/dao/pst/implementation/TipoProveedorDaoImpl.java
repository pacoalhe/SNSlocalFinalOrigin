package mx.ift.sns.dao.pst.implementation;

import java.util.List;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pst.ITipoProveedorDao;
import mx.ift.sns.modelo.pst.TipoProveedor;

/**
 * Implementación de los métodos para base de datos de tipos de proveedor.
 */
@Named
public class TipoProveedorDaoImpl extends BaseDAO<TipoProveedor> implements ITipoProveedorDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<TipoProveedor> findAllTiposProveedor() {
        return getEntityManager().createQuery("SELECT tp FROM TipoProveedor tp").getResultList();
    }

    @Override
    public TipoProveedor getTipoProveedorByCdg(String pCdgTipo) {
        return getEntityManager().find(TipoProveedor.class, pCdgTipo);
    }

}
