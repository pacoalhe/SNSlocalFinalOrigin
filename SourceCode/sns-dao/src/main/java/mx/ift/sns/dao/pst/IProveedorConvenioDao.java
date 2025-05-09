package mx.ift.sns.dao.pst;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoRed;

/**
 * Interfaz de definición de los métodos para base de datos de Convenios de Proveedor.
 */
public interface IProveedorConvenioDao {

    /**
     * Recupera un convenio por su identificador.
     * @param pId Identificador de Convenio
     * @return ProveedorConvenio
     */
    ProveedorConvenio getConvenioById(BigDecimal pId);

    /**
     * Devuelve la lista de convenios por proveedor.
     * @param pCodPstComercializador BigDecimal
     * @return List
     */
    List<ProveedorConvenio> findAllConveniosByProveedor(BigDecimal pCodPstComercializador);

    /**
     * Devuelve la lista de concesionarios con que un comercializador tiene convenio.
     * @param tipoRed del proveedor
     * @param comercializador proveedor a asociar
     * @return listado de concesionarios con convenio con el comercializador.
     */
    List<Proveedor> findAllConcesionariosByComercializador(TipoRed tipoRed, Proveedor comercializador);

    /**
     * Recupera la lista de convenios del Proveedor Comercializador con el tipo de red indicado o con ambos tipos de
     * red.
     * @param pComercializador Proveedor Comercializador
     * @param pTipoRedConvenio Tipo de Red del Convenio
     * @return Listado de Convenios que concuerdan con los parámetros
     */
    List<ProveedorConvenio> findAllConveniosByProveedor(Proveedor pComercializador, TipoRed pTipoRedConvenio);

    /**
     * Comprueba si existe convenio entre dos proveedores.
     * @param concesionario proveedor
     * @param comercializador proveedor
     * @return true/false
     */
    boolean existConvenio(Proveedor concesionario, Proveedor comercializador);

    /**
     * Comprueba para un proveedor si existe algun convenio con unconcesionario que tenga ABC.
     * @param proveedor proveedor
     * @return boolean
     */
    boolean existeConcesionarioConvenioConBcd(Proveedor proveedor);

    /**
     * Obtiene todos los concesionarios que un proveedor tiene convenio para numeración no geográfica(el concesionario
     * tiene ABC).
     * @param proveedor convenio
     * @return lista concesionario
     */
    List<Proveedor> findAllConcesonarioConvenioNng(Proveedor proveedor);

}
