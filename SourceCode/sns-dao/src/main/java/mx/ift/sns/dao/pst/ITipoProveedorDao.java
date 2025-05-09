package mx.ift.sns.dao.pst;

import java.util.List;

import mx.ift.sns.modelo.pst.TipoProveedor;

/**
 * Interfaz de definición de los métodos para base de datos de Tipos de Proveedor.
 */
public interface ITipoProveedorDao {

    /**
     * Recupera el catálogo de tipos de proveedor.
     * @return List
     */
    List<TipoProveedor> findAllTiposProveedor();

    /**
     * Recupera un tipo de Proveedor por su código.
     * @param pCdgTipo Código de Tipo de Proveedor.
     * @return TipoProveedor
     */
    TipoProveedor getTipoProveedorByCdg(String pCdgTipo);

}
