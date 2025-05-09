package mx.ift.sns.negocio.psts;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaProveedores;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.DetalleProveedor;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;

/**
 * Servicio de Proveedores.
 */
public interface IProveedoresService {

    /**
     * Recupera la lista de Proveedores que pueden ser cesionarios del proveedor cedente facilitado.
     * @param pCedente Proveedor Cedente (Concesionario, Comercializador o Ambos)
     * @return lista de Proveedores que pueden ser cesionarios del proveedor cedente
     */
    List<Proveedor> findAllCesionarios(Proveedor pCedente);

    /**
     * Recupera la lista de proveedores activos completa sin filtros.
     * @return List<Proveedor>
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedoresActivos() throws Exception;

    /**
     * Recupera la lista de detalle de todos proveedores activos.
     * @return List<DetalleProveedor>
     * @throws Exception en caso de error.
     */
    List<DetalleProveedor> findAllProveedoresActivosD() throws Exception;

    /**
     * Recupera la lista de proveedores completa sin filtros.
     * @return List<Proveedor>
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedores() throws Exception;

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
     * Recupera un proveedor por su nombre.
     * @param pNombre Nombre de Proveedor.
     * @return Proveedor
     * @throws Exception en caso de error.
     */
    Proveedor getProveedorByNombre(String pNombre) throws Exception;

    /**
     * Actualiza la información de un proveedor existente en base de datos.
     * @param pProveedor Información actualizada del proveedor.
     * @throws Exception en caso de error.
     */
    void updateProveedor(Proveedor pProveedor) throws Exception;

    /**
     * Recupera la lista de Representantes legales del Proveedor indicado.
     * @param pTipoContacto Tipo de Representante Legal
     * @param pIdProveedor Identificador del Proveedor.
     * @return List<Contacto>
     */
    List<Contacto> getRepresentantesLegales(String pTipoContacto, BigDecimal pIdProveedor);

    /**
     * Recupera un representante legal por su identificador.
     * @param pIdContacto Identificador del representante legal.
     * @return Contacto
     * @throws Exception en caso de error.
     */
    Contacto getRepresentanteLegal(BigDecimal pIdContacto) throws Exception;

    /**
     * Recupera un Proveedor por su identificador.
     * @param pIdProveedor Identificador del Proveedor.
     * @return Proveedor
     * @throws Exception en caso de error.
     */
    Proveedor getProveedorById(BigDecimal pIdProveedor) throws Exception;

    /**
     * Devuelve la lista de convenios por proveedor.
     * @param pCodPstComercializador BigDecimal
     * @return List
     * @throws Exception en caso de error.
     */
    List<ProveedorConvenio> findAllConveniosByProveedor(BigDecimal pCodPstComercializador) throws Exception;

    /**
     * Recupera la lista de convenios del Proveedor Comercializador con el tipo de red indicado o con ambos tipos de
     * red.
     * @param pComercializador Proveedor Comercializador
     * @param pTipoRedConvenio Tipo de Red del Convenio
     * @return Listado de Convenios que concuerdan con los parámetros
     */
    List<ProveedorConvenio> findAllConveniosByProveedor(Proveedor pComercializador, TipoRed pTipoRedConvenio);

    /**
     * Devuelve la lista de concecisionarios con que un comercializador tiene convenio.
     * @param tipoRed Tipo de Red del Proveedor.
     * @param comercializador Proveedor
     * @return List
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllConcesionariosByComercializador(TipoRed tipoRed, Proveedor comercializador) throws Exception;

    /**
     * Recupera un convenio por su identificador.
     * @param pId Identificador de Convenio
     * @return ProveedorConvenio
     */
    ProveedorConvenio getConvenioById(BigDecimal pId);

    /**
     * Recupera la lista de Proveedores Concesionarios con los que el Proveedor Comercializador tiene convenio.
     * @param pComercializador Proveedor Comercializador
     * @param pTipoRed Tipo de Red del Convenio
     * @return Lista de Concesionarios con los que el Proveedor Comercializador tiene convenio.
     */
    List<Proveedor> findAllConcesionariosFromConvenios(Proveedor pComercializador, TipoRed pTipoRed);

    /**
     * Recupera el catálogo de tipos de proveedor.
     * @return List
     * @throws Exception en caso de error.
     */
    List<TipoProveedor> findAllTiposProveedor() throws Exception;

    /**
     * Recupera un tipo de Proveedor por su código.
     * @param pCdgTipo Código de Tipo de Proveedor.
     * @return TipoProveedor
     */
    TipoProveedor getTipoProveedorByCdg(String pCdgTipo);

    /**
     * Recupera la lista de Proveedores Concesionarios de un Abn.
     * @param abn Abn.
     * @return Lista de proveedores de un abn.
     */
    List<Proveedor> findAllConcesionariosByAbn(Abn abn);

    /**
     * Recupera la lista de Proveedores Concesionarios de una Poblacion.
     * @param poblacion Poblacion.
     * @return Lista de proveedores de una poblacion.
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
     * Guarda o actualiza el proveedor en la bbdd.
     * @param proveedor Proveedor
     * @param encriptarPass boolean
     * @return Proveedor
     */
    Proveedor guardarProveedor(Proveedor proveedor, boolean encriptarPass);

    /**
     * Exporta los datos generales del proveedor o proveedores.
     * @param filtro FiltroBusquedaProveedores
     * @return byte[]
     * @throws Exception exception
     */
    byte[] exportarDatosGenerales(FiltroBusquedaProveedores filtro) throws Exception;

    /**
     * Exporta los datos generales del proveedor o proveedores.
     * @param filtro FiltroBusquedaProveedores
     * @return byte[]
     * @throws Exception exception
     */
    byte[] exportarDatosContactos(FiltroBusquedaProveedores filtro) throws Exception;

    /**
     * Exporta los datos generales del proveedor o proveedores.
     * @param filtro FiltroBusquedaProveedores
     * @return byte[]
     * @throws Exception exception
     */
    byte[] exportarDatosConvenios(FiltroBusquedaProveedores filtro) throws Exception;

    /**
     * Comprueba si existe otro proveedor con el mismo IDO.
     * @param proveedor Proveedor
     * @return <ul>
     *         <li>0 - No Existe</li>
     *         <li>1 - Existe Activo</li>
     *         <li>2- Existe Inactivo</li>
     *         </ul>
     */
    int existeIdo(Proveedor proveedor);

    /**
     * Comprueba si existe otro proveedor con el mismo IDA.
     * @param proveedor Proveedor
     * @return <ul>
     *         <li>0 - No Existe</li>
     *         <li>1 - Existe Activo</li>
     *         <li>2- Existe Inactivo</li>
     *         </ul>
     */
    int existeIda(Proveedor proveedor);

    /**
     * Comprueba si existe otro proveedor con el mismo ABC.
     * @param proveedor Proveedor
     * @return <ul>
     *         <li>0 - No Existe</li>
     *         <li>1 - Existe Activo</li>
     *         <li>2- Existe Inactivo</li>
     *         </ul>
     */
    int existeAbc(Proveedor proveedor);

    /**
     * Comprueba si existe otro proveedor con el mismo BCD.
     * @param proveedor Proveedor
     * @return <ul>
     *         <li>0 - No Existe</li>
     *         <li>1 - Existe Activo</li>
     *         <li>2- Existe Inactivo</li>
     *         </ul>
     */
    int existeBcd(Proveedor proveedor);

    /**
     * Valida si existe un representante legal para el proveedor.
     * @param proveedor Proveedor
     * @return boolean boolean
     */
    boolean faltaRepresentanteLegal(Proveedor proveedor);

    /**
     * Comprueba si existe en BBDD algún proveedor con el mismo nombre.
     * @param proveedor Proveedor
     * @return boolean existe
     */
    boolean existeNombreProveedor(Proveedor proveedor);

    /**
     * Valida si los datos del proveedor son correctos.
     * @param proveedor Proveedor
     * @param tipoPstInicial String
     * @param tipoRedInicial String
     * @param validaUsuario boolean
     * @return Proveedor proveedor
     */
    Proveedor validaProveedor(Proveedor proveedor, String tipoPstInicial, String tipoRedInicial, boolean validaUsuario);

    /**
     * Pone a inactivo el proveedor recibido.
     * @param proveedor Proveedor
     * @return Proveedor
     */
    Proveedor bajaProveedor(Proveedor proveedor);

    /**
     * Pone a activo el proveedor recibido.
     * @param proveedor Proveedor
     * @return Proveedor
     */
    Proveedor activarProveedor(Proveedor proveedor);

    /**
     * Comprueba si existe en BBDD algún proveedor con el mismo nombre corto.
     * @param proveedor Proveedor
     * @return boolean existe
     */
    boolean existeNombreCortoProveedor(Proveedor proveedor);

    /**
     * Recupera la lista de Proveedores que soportarn el tipo de red del proveedor.
     * @param tipoRed TipoRed
     * @return List
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedoresByTRed(TipoRed tipoRed) throws Exception;

    /**
     * Método que devuelve los tipos de red.
     * @return List
     */
    List<TipoRed> findAllTiposRed();

    /**
     * Método que devuelve el tipo red.
     * @param idTipoRed String
     * @return TipoRed
     */
    TipoRed getTipoRedById(String idTipoRed);

    /**
     * Método que devuelve los tipos de red válidos (desconocido no se muestra).
     * @return List
     */
    List<TipoRed> findAllTiposRedValidos();

    /**
     * Método que devuelve los tipos de red válidos que soportaan el tipo de red especificado.
     * @param tipoRed TipoRed
     * @return List
     */
    List<TipoRed> findAllTiposRedValidos(TipoRed tipoRed);

    /**
     * Método que devuelve los tipos modalidad.
     * @return List
     */
    List<TipoModalidad> findAllTiposModalidad();

    /**
     * Método que devuelve el tipo modalidad.
     * @param idtipoModalidad String
     * @return TipoModalidad
     */
    TipoModalidad getTipoModalidadById(String idtipoModalidad);

    /**
     * FUnción que valida que los datos del contacto sean correctos.
     * @param contacto Contacto
     * @return List<String> errores en el contacto
     */
    List<String> validarContacto(Contacto contacto);

    /**
     * Función que comprueba si el contacto está siendo referenciado en algún trámite.
     * @param contacto Contacto
     * @return boolean usado
     */
    boolean contactoNoUsado(Contacto contacto);

    /**
     * Comprueba para un proveedor si existe algun convenio con unconcesionario que tenga ABC.
     * @param proveedor proveedor
     * @return boolean
     */
    boolean existeConcesionarioConvenioConBcd(Proveedor proveedor);

    /**
     * Obtiene todos los proveedores a los que se puede arrendar numeraciones no geograficas.
     * @return lista proveedores
     */
    List<Proveedor> findAllArrendatariosNng();

    /**
     * Obtiene la lista de todos los proveedores por estado.
     * @param estado Estado
     * @return List<Proveedor>
     */
    List<Proveedor> findAllConcesionariosByEstado(Estado estado);

    /**
     * Obtiene la lista de concesionarios por estado.
     * @param municipio Municipio
     * @return List<Proveedor>
     */
    List<Proveedor> findAllConcesionariosByMunicipio(Municipio municipio);

    /**
     * Obtiene todos los concesionarios que un proveedor tiene convenio para numeración no geográfica(el concesionario
     * tiene ABC).
     * @param proveedor convenio
     * @return lista concesionario
     */
    List<Proveedor> findAllConcesonarioConvenioNng(Proveedor proveedor);

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
     * Devuelve el proveedor con el IDA buscado.
     * @param idaProveedor BigDecimal
     * @return Proveedor
     */
    List<Proveedor> getProveedorByIDA(BigDecimal idaProveedor);

    /**
     * Devuelve el proveedor con el IDO buscado.
     * @param idoProveedor BigDecimal
     * @return Proveedor
     */
    List<Proveedor> getProveedorByIDO(BigDecimal idoProveedor);

    /**
     * Devuelve el proveedor cuyo IDO corresponda al ABC.
     * @param abcProveedor BigDecimal
     * @return List<Proveedor>
     */
    List<Proveedor> getProveedorByABC(BigDecimal abcProveedor);

}
