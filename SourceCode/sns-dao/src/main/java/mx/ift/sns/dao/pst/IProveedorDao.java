package mx.ift.sns.dao.pst;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaProveedores;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.DetalleProveedor;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;

/**
 * Interfaz de definición de los métodos para base de datos de Proveedores.
 */
public interface IProveedorDao extends IBaseDAO<Proveedor> {

    /**
     * Recupera la lista de Proveedores activos completa, sin filtros.
     * @return List de proveedores
     */
    List<Proveedor> findAllProveedoresActivos();

    /**
     * Recupera la lista de detalle de todos proveedores activos.
     * @return List<DetalleProveedor>
     * @throws Exception en caso de error.
     */
    List<DetalleProveedor> findAllProveedoresActivosD();

    /**
     * Recupera la lista de Proveedores completa, sin filtros.
     * @return List de proveedores
     */
    List<Proveedor> findAllProveedores();

    /**
     * Recupera todos los proveedores de un mismo tipo de servicio.
     * @param pTipoProveedor TipoProveedor
     * @param pTipoRed TipoRed
     * @param pIdSolicitante Identificador del Proveedor que solicita la Lista. Puede se nulo. Si existe, se elimina el
     *        proveedor de los resultados.
     * @return List
     */
    List<Proveedor> findAllProveedoresByServicio(TipoProveedor pTipoProveedor,
            TipoRed pTipoRed, BigDecimal pIdSolicitante);

    /**
     * Se encarga de obtener los proveedores por tipo de servicio que cumplan.
     * <ul>
     * <li>Si tipo de proveedor no es Comercializador se buscan todos los tipos de proveedores por el tipo de red</li>
     * <li>Si es Comercializador se buscan los proveedores de tipo comercializador y comercializador/concesionario</li>
     * </ul>
     * @param pTipoProveedor TipoProveedor
     * @param pTipoRed TipoRed
     * @param idPst BigDecimal
     * @return List<Proveedor>
     */
    List<Proveedor> findAllProveedoresByServicioArrendar(TipoProveedor pTipoProveedor,
            TipoRed pTipoRed, BigDecimal idPst);

    /**
     * Recupera un Proveedor por su nobre.
     * @param pNombre String
     * @return Proveedor
     */
    Proveedor getProveedorByNombre(String pNombre);

    /**
     * Recupera un Proveedor por su identificador.
     * @param pIdProveedor BigDecimal
     * @return Proveedor
     */
    Proveedor getProveedorById(BigDecimal pIdProveedor);

    /**
     * Recupera la lista de Proveedores que pueden ser cesionarios del proveedor cedente facilitado.
     * @param pCedente Proveedor Cedente (Concesionario, Comercializador o Ambos)
     * @return lista de Proveedores que pueden ser cesionarios del proveedor cedente
     */
    List<Proveedor> findAllCesionarios(Proveedor pCedente);

    /**
     * Recupera una lista de todos los proveedor de tipo concesionario.
     * @return lista proveedores
     */
    List<Proveedor> findAllConcesionarios();

    /**
     * Recupera la lista de Proveedores Concesionarios con los que el Proveedor Comercializador tiene convenio.
     * @param pComercializador Proveedor Comercializador
     * @param pTipoRed Tipo de Red del Convenio
     * @return Lista de Concesionarios con los que el Proveedor Comercializador tiene convenio.
     */
    List<Proveedor> findAllConcesionariosFromConvenios(Proveedor pComercializador, TipoRed pTipoRed);

    /**
     * Recupera la lista de Proveedores Concesionarios de un Abn.
     * @param abn Abn.
     * @return Lista de Concesionarios de un abn.
     */
    List<Proveedor> findAllConcesionariosByAbn(Abn abn);

    /**
     * Recupera la lista de Proveedores Concesionarios de una Poblacion.
     * @param poblacion Poblacion.
     * @return Lista de Concesionarios de una poblacion.
     */
    List<Proveedor> findAllConcesionariosByPoblacion(Poblacion poblacion);

    /**
     * Recupera el listado de Proveedores que cumplen con el filtro de búsqueda.
     * @param pFiltros FiltroBusquedaProveedores
     * @return List<Proveedor>
     * @throws Exception ex
     */
    List<Proveedor> findAllProveedores(FiltroBusquedaProveedores pFiltros) throws Exception;

    /**
     * Obtiene el número de proveedores que cumplen los filtros de búsqueda.
     * @param pFiltros FiltroBusquedaProveedores
     * @return int
     * @throws Exception ex
     */
    int findAllProveedoresCount(FiltroBusquedaProveedores pFiltros) throws Exception;

    /**
     * Comprueba si existe otro proveedor con el mismo IDO.
     * @param proveedor Proveedor
     * @return String Estado del Proveedor que comparte IDO
     */
    String existeIdo(Proveedor proveedor);

    /**
     * Comprueba si existe otro proveedor con el mismo IDA.
     * @param proveedor Proveedor
     * @return String Estado del Proveedor que comparte IDA
     */
    String existeIda(Proveedor proveedor);

    /**
     * Comprueba si existe otro proveedor con el mismo ABC.
     * @param proveedor Proveedor
     * @return String Estado del Proveedor que comparte ABC
     */
    String existeAbc(Proveedor proveedor);

    /**
     * Comprueba si existe otro proveedor con el mismo BCD.
     * @param proveedor Proveedor
     * @return String Estado del Proveedor que comparte BCD
     */
    String existeBcd(Proveedor proveedor);

    /**
     * Comprueba si existe en BBDD algún proveedor con el mismo nombre.
     * @param proveedor Proveedor
     * @return boolean existe
     */
    boolean existeNombreProveedor(Proveedor proveedor);

    /**
     * Comprueba si existe en BBDD algún proveedor con el mismo nombre corto.
     * @param proveedor Proveedor
     * @return boolean existe
     */
    boolean existeNombreCortoProveedor(Proveedor proveedor);

    /**
     * Comprueba si existe en BBDD algún proveedor con dicho usuario.
     * @param proveedor Proveedor
     * @return boolean existe
     */
    boolean usuarioUtilizado(Proveedor proveedor);
    

    /**
     * Comprueba el proveedor actual teiene asignado el mismo string
     * del usuario es decir el userid.
     * @param proveedor Proveedor
     * @return boolean existe
     */
    boolean userIdUsedBySamePST(Proveedor proveedor);

    /**
     * Recupera la lista de Proveedores que soportarn el tipo de red del proveedor.
     * @param tipoRed TipoRed
     * @return List
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedoresByTRed(TipoRed tipoRed) throws Exception;

    /**
     * Obtiene todos los proveedores a los que se puede arrendar numeraciones no geograficas.
     * @return lista proveedores
     */
    List<Proveedor> findAllArrendatariosNng();

    /**
     * Obtiene la lista de proveedores que prestan servicio en un estado.
     * @param estado Estado
     * @return List<Proveedor>
     */
    List<Proveedor> findAllConcesionariosByEstado(Estado estado);

    /**
     * Obtiene la lista de proveedores que prestan servicio en un municipio.
     * @param municipio Municipio seleccionado
     * @return List
     */
    List<Proveedor> findAllConcesionariosByMunicipio(Municipio municipio);

    /**
     * Recupera la lista de Proveedores que pueden ser arrendatarios del proveedor indicado por parámetros. Los
     * arrendatarios pueden ser:<br>
     * - Todos los Concesionarios o Comercializadoras que tengan ABC definio<br>
     * - Comercializadores sin ABC, pero con Convenio con algún Concesionario que sí tenga ABC
     * @param proveedor Proveedor solicitante de la lista de arrendatarios
     * @return lista de Proveedores que pueden ser arrendatarios del proveedor indicado
     */
    List<Proveedor> findAllArrendatariosByAbc(Proveedor proveedor);

    /**
     * Recupera la lista de Proveedores con los que el solicitante tiene convenios y que, además, tengan ABC.
     * @param pComercializador Proveedor Solicitante.
     * @return lista de Proveedores con los que el solicitante tiene convenios y ABC definido.
     */
    List<Proveedor> findAllConcesionariosFromConveniosByAbc(Proveedor pComercializador);

    /**
     * Comprueba si existe una IDA o ABC.
     * @param dato IDA/ABC
     * @return true/false
     */
    boolean existeIdaAbc(String dato);

    /**
     * Devuelve el proveedor con el IDA buscado.
     * @param idaProveedor BigDecimal
     * @return List<Proveedor>
     */
    List<Proveedor> getProveedorByIDA(BigDecimal idaProveedor);

    /**
     * Devuelve el proveedor con el IDO buscado.
     * @param idoProveedor BigDecimal
     * @return List<Proveedor>
     */
    List<Proveedor> getProveedorByIDO(BigDecimal idoProveedor);

    /**
     * Devuelve el proveedor cuyo IDO corresponda al ABC.
     * @param abcProveedor BigDecimal
     * @return List<Proveedor>
     */
    List<Proveedor> getProveedorByABC(BigDecimal abcProveedor);
}
