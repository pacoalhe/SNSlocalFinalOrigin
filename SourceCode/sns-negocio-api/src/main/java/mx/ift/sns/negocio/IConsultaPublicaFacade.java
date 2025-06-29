package mx.ift.sns.negocio;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoArea;
import mx.ift.sns.modelo.ot.EstadoNumeracion;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.NirNumeracion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.PoblacionNumeracion;
import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.usu.Usuario;
import mx.ift.sns.negocio.num.model.Numero;
import mx.ift.sns.modelo.ot.*;

/**
 * Interfaz de la parte publica.
 */
public interface IConsultaPublicaFacade {

    /**
     * Búsqueda de un estado por su id.
     * @param id String
     * @return Estado
     * @throws Exception ex
     */
    Estado findEstadoById(String id) throws Exception;

    /**
     * Búsqueda de todos los municipios de un Estado.
     * @param estado Estado
     * @return List<Municipio> listado
     * @throws Exception ex
     */
    List<Municipio> findMunicipiosByEstado(String estado) throws Exception;

    /**
     * Búsqueda de un municipio por su id.
     * @param id MunicipioPK
     * @return Municipio municipio
     * @throws Exception ex
     */
    Municipio findMunicipioById(MunicipioPK id) throws Exception;

    /**
     * Devuelve lista de poblacion de un estado y municipio concreto.
     * @param estado Código de Estado
     * @param municipio Código de Municipio
     * @return Lista de poblaciones por estado y municipio
     */
    List<Poblacion> findAllPoblaciones(String estado, String municipio);

    /**
     * Búsqueda de una población por su id.
     * @param inegi String
     * @return Poblacion poblacion
     * @throws Exception ex
     */
    Poblacion findPoblacionById(String inegi) throws Exception;

    /**
     * Búsqueda de todos los Estados.
     * @return List<Estado> estados
     * @throws Exception ex
     */
    List<Estado> findEstados() throws Exception;

    /**
     * Búsqueda de todas las poblaciones con nombre parecido al recibido.
     * @param cadena String
     * @return List<Poblacion> poblaciones
     * @throws Exception ex
     */
    List<Poblacion> findAllPoblacionesLikeNombre(String cadena) throws Exception;

    /**
     * Búsqueda de todos los nombres de poblaciones parecidos al recibido.
     * @param cadena String
     * @return List<String> listado
     */
    List<String> findAllPoblacionesNameLikeNombre(String cadena);

    /**
     * Búsqueda de todos los proveedores concesionarios de un ABN.
     * @param abn Abn
     * @return List<Proveedor> listado
     * @throws Exception ex
     */
    List<Proveedor> findAllConcesionariosByAbn(Abn abn) throws Exception;

    /**
     * Búsqueda de todos los proveedores concesionarios de una población.
     * @param poblacion Poblacion
     * @return List<Proveedor> listado
     * @throws Exception ex
     */
    List<Proveedor> findAllConcesionariosByPoblacion(Poblacion poblacion) throws Exception;

    /**
     * Recupera la lista de poblaciones de un abn.
     * @param pAbn Información del Abn.
     * @return List<Poblacion> poblaciones.
     */
    List<Poblacion> findAllPoblacionesByAbn(Abn pAbn);

    /**
     * Recupera la lista de municipios de un abn.
     * @param pAbn Información del Abn.
     * @return List<Municipio> municipios.
     */
    List<Municipio> findAllMunicipiosByAbn(Abn pAbn);

    /**
     * Búsqueda del total de rangos asignados a un población, proveedor, red y modalidad.
     * @param tipoRed String
     * @param tipoModalidad String
     * @param proveedor Proveedor
     * @param poblacion Poblacion
     * @return bigdecimal número
     * @throws Exception ex
     */
    BigDecimal getTotalNumRangosAsignadosByPoblacion(String tipoRed, String tipoModalidad, Proveedor proveedor,
            Poblacion poblacion) throws Exception;

    /**
     * Obtiene el total de números asisgnados a un estado.
     * @param estado Estado
     * @return total números
     */
    Integer getTotalNumeracionAsignadaByEstado(Estado estado);

    /**
     * Búsqueda del total de rangos asignados a un abn, proveedor, red y modalidad.
     * @param tipoRed String
     * @param tipoModalidad String
     * @param abn Abn
     * @param proveedor Proveedor
     * @return bigdecimal numero
     */
    BigDecimal getTotalNumRangosAsignadosByAbn(String tipoRed, String tipoModalidad, Abn abn, Proveedor proveedor);

    /**
     * Búsqueda de la poblacion con el número máximo de numeraciones asignadas.
     * @param abn Abn
     * @return Poblacion poblacion
     * @throws Exception ex
     */
    Poblacion getPoblacionWithMaxNumAsignadaByAbn(Abn abn) throws Exception;

    /**
     * Búsqueda de un ABN a partir de un Nir.
     * @param nir String
     * @return Abn abn
     * @throws Exception ex
     */
    Abn getAbnByCodigoNir(String nir) throws Exception;

    List<Abn> getAbnByZona(int zona) throws Exception;

    /**
     * Parseo de un número.
     * @param numeracion String
     * @return Numero numero
     */
    Numero parseNumeracion(String numeracion);

    /**
     * Búsqueda del rango asociado a un número.
     * @param numero Numero
     * @return RangoSerie rango
     */
    RangoSerie getRangoPertenece(Numero numero);

    /**
     * Búsqueda de los nirs asociados a un número local.
     * @param numeroLocal Numero
     * @return List<Nir> listado
     */
    List<Nir> findNirsNumeroLocal(Numero numeroLocal);

    /**
     * Búsqueda de las poblaciones de numeración de un proveedor.
     * @param proveedorServ Proveedor
     * @return List<PoblacionNumeracion> listado
     */
    List<PoblacionNumeracion> getPoblacionesNumeracionByProveedor(Proveedor proveedorServ);

    /**
     * Búsqueda de los estados de numeración por proveedor.
     * @param proveedorServ Proveedor
     * @return List<EstadoNumeracion> listado
     */
    List<EstadoNumeracion> getEstadosNumeracionByProveedor(Proveedor proveedorServ);

    /**
     * Búsqueda de los Nirs de una numeración por proveedor.
     * @param proveedorServ Proveedor
     * @return List<NirNumeracion> listado
     */
    List<NirNumeracion> getNirsNumeracionByProveedor(Proveedor proveedorServ);

    /**
     * Búsqueda de las numeraciónes para un proveedor en una población.
     * @param proveedorServ Proveedor
     * @param poblacion Poblacion
     * @return bigdecimal numero
     */
    BigDecimal getNumeracionPoblacionByProveedor(Proveedor proveedorServ, Poblacion poblacion);

    /**
     * Comprueba si exite un nir.
     * @param nir String
     * @return true, false
     */
    boolean existsNir(String nir);

    boolean existsZona(String zona);

    /**
     * Devuelve el número de poblaciones de un estado.
     * @param estado Código de Estado.
     * @return String
     */
    String countAllPoblacionesByEstado(String estado);

    /**
     * Obtiene la lista de todos los proveedores por estado.
     * @param estado Estado
     * @return List<Proveedor>
     */
    List<Proveedor> findAllConcesionariosByEstado(Estado estado);

    /**
     * Obtiene la lista de todos los ABN por estado.
     * @param estado String
     * @return List<Abn>
     */
    List<Abn> findAbnInEstado(String estado);

    /**
     * Obtiene la lista de todos los Nir por Abn.
     * @param numAbn BigDecimal
     * @return List
     */
    List<Nir> findAllNirByAbn(BigDecimal numAbn);

    /**
     * Obtiene la lista de todos los Abn por municipio.
     * @param municipio String
     * @param estado String
     * @return List<Abn>
     */
    List<Abn> findAbnInMunicipio(String municipio, String estado);

    /**
     * Obiente la lista de todos los Nir de un municipio.
     * @param municipio String
     * @param estado String
     * @return List<Nir>
     */
    List<Nir> finAllNirInMunicipio(String municipio, String estado);

    /**
     * Obtiene la lista de proveedores de un municipio.
     * @param municipio Municipio
     * @return List
     */
    List<Proveedor> findAllConcesionariosByMunicipio(Municipio municipio);

    /**
     * Obtiene la numeración asignada en un municipio.
     * @param municipio Municipio
     * @return Integer
     */
    Integer getTotalNumRangosAsignadosByMunicipio(Municipio municipio);

    /**
     * Busca al usuario con el uid.
     * @param uid a buscar
     * @return usuario
     */
    Usuario findUsuario(String uid);

    /**
     * Obtiene la lista de tipos de planes por Rol.
     * @param idRol String
     * @return List<String>
     */
    List<String> findAllTipoPlanByRol(String idRol);

    /**
     * Obtiene la lista de los planes de un rol.
     * @param idRol String
     * @return List<plan>
     */
    List<Plan> findAllPlanByRol(String idRol);

    /**
     * Busca el plan por tipo.
     * @param idTipo String
     * @return Plan
     */
    Plan getPlanByTipo(String idTipo);

    /**
     * Obtiene todas la claves de servicio de la numeración no goegráfica.
     * @return List<ClavesServicio>
     * @throws Exception
     */
    List<ClaveServicio> findAllClaveServicio();

    /**
     * Obtiene el estado_rango por codigo.
     * @param codigo String
     * @return EstadoRango
     */
    EstadoRango getEstadoRangoByCodigo(String codigo);

    /**
     * Obtiene el proveedor por id.
     * @param pIdProveedor BigDecimal
     * @return Proveedor
     * @throws Exception exception
     */
    Proveedor getProveedorById(BigDecimal pIdProveedor) throws Exception;

    /**
     * Devuelve el rangoSerieNng.
     * @param pClaveServicio BigDecimal
     * @param pSna BigDecimal
     * @param pNumInicial String
     * @param pAsignatario Proveedor
     * @return RangoSerieNng
     */
    RangoSerieNng getRangoSerie(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial, Proveedor pAsignatario);

    /**
     * Devuelve el rango serie numeración no geográfica dentro del rango de numeración.
     * @param pClaveServicio BigDecimal
     * @param pSna BigDecimal
     * @param pNumInicial String
     * @param pNumFinal String
     * @param pAsignatario Proveedor
     * @return RangoSerieNng
     */
    RangoSerieNng getRangoSerieByFraccion(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial,
            String pNumFinal,
            Proveedor pAsignatario);

    /**
     * Obtiene el rango serie de una numeración en concreto que tenga estatus "Asignado".
     * @param pClaveServicio BigDecimal
     * @param pSna BigDecimal
     * @param pNumeracion String
     * @return RangoSerieNng
     */
    RangoSerieNng getRangoSeriePertenece(BigDecimal pClaveServicio, BigDecimal pSna, String pNumeracion);

    /**
     * Busca todas las claves de servicio activas.
     * @return List<ClaveServicio>
     */
    List<ClaveServicio> findAllClaveServicioActivas();

    /**
     * Comprueba si el email corresponde al usuario introducido.
     * @param idUsuario String
     * @param email String
     * @return boolean
     */
    boolean existeEmailUsuario(String idUsuario, String email);

    /**
     * Obtiene el plan por tipo y clave.
     * @param idTipo String
     * @param claveServicio BigDecimal
     * @return Plan
     */
    Plan getPlanByTipoAndClaveServicio(String idTipo, BigDecimal claveServicio);

    /**
     * Obtiene todo la numeración asignada a un proveedor.
     * @param idPst BigDecimal
     * @return Integer
     */
    Integer getTotalNumeracionAginadaProveedor(BigDecimal idPst);

    /**
     * Listado de poblaciones por estado con numeración asignada.
     * @param estado Estado
     * @return List<Poblacion>
     */
    List<Poblacion> findAllPoblacionesEstadoNumeracion(Estado estado);

    /**
     * Método que devuelve los nirs asociados a una población.
     * @param poblacion Poblacion
     * @return List<Nir>
     */
    List<Nir> findAllNirByPoblacion(Poblacion poblacion);

    /**
     * Recupera un nir por su codigo.
     * @param cdgNir codigo nir
     * @return el nir
     */
    Nir getNirByCodigo(int cdgNir);

    /**
     * Recupera una lista de nirs por su zona.
     * @param zona zona
     * @return la lista de nirs
     */
    List<Nir> getNirByZona(int zona);

    /**
     * Obtiene una lista de población numeració filtrada por proveedor y estado.
     * @param proveedorServ Proveedor
     * @param estado Estado
     * @return List<PoblacionNumeracin>
     */
    List<PoblacionNumeracion> findALLPoblacionesNumeracionByProveedorEstado(Proveedor proveedorServ, Estado estado);

    /**
     * Obtiene una lista de población numeració filtrada por proveedor y nir.
     * @param proveedorServ Proveedor
     * @param nir Nir
     * @return List<PoblacionNumeracion>
     */
    List<PoblacionNumeracion> findALLPoblacionesNumeracionByProveedorNir(Proveedor proveedorServ, Nir nir);

    /**
     * Busca todas las claves de servicio activas en la web.
     * @return List<ClaveServicio>
     */
    List<ClaveServicio> findAllClaveServicioActivasWeb();

    /**
     * Obtiene el Abn de una poblacion.
     * @param poblacion Poblacion
     * @return Abn
     */
    Abn getAbnByPoblacion(Poblacion poblacion);

    /**
     * Obtiene la lista de proveedores que prestan servicio por: nir, abn, poblacion, municipio ó estado.
     * @param nir Nir
     * @param abn Abn
     * @param poblacion Poblacion
     * @param municipio Municipio
     * @param estado Estado
     * @return List<Proveedor>
     */

    List<Proveedor> findAllPrestadoresServicioBy(Nir nir, Abn abn, Poblacion poblacion, Municipio municipio,
            Estado estado);

    /**
     * Busca las áreas de un estado.
     * @param estado Estado
     * @return List<EstadoArea>
     */
    List<EstadoArea> findAllAreaEstadoByEstado(Estado estado);

    /**
     * Recupera un nir.
     * @param id id del nir
     * @return el nir
     * @throws Exception Exceptions
     */
    Nir getNirById(BigDecimal id) throws Exception;

    /**
     * Obtiene las poblaciones con numeración asignada de un NIR.
     * @param nir Nir
     * @return List<Poblacion>
     */
    List<Poblacion> findALLPoblacionesNumeracionAsignadaByNir(Nir nir);

    /**
     * Obtiene la numeración asignada a un nir.
     * @param nir NIr
     * @return int
     */
    int findNumeracionesAsignadasNir(Nir nir);

    /**
     * Obtiene las poblaciones con numeración asignada de un municipio.
     * @param municipio Municipio
     * @return List<Poblacion>
     */
    List<Poblacion> findALLPoblacionesNumeracionAsignadaByMunicipio(Municipio municipio);

    /**
     * Recupera los municipios vinculados al nir de un abn.
     * @param nir Nir
     * @param pUseCache boolean
     * @return List<Municipio>
     */
    List<Municipio> findAllMunicipiosByNir(Nir nir, boolean pUseCache);

    /**
     * Método que devuelve los nirs asociados a un estado.
     * @param estado Estado
     * @return List<Nir>
     */
    List<Nir> findAllNirByEstado(Estado estado);

    /**
     * Busca si se encuentra portado el número indicado.
     * @param numero numero a buscar
     * @return numero portado si esta portado
     */
    NumeroPortado findNumeroPortado(String numero);

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
    
    /**
     * Retorna el action de portabilidad.
     * @param nameParameter
     * @return
     */
    String getActionPorted(String nameParameter);

    Municipio findMunicipioByNombreAndEstado(String nombreMun, String nombreEst) throws Exception;

    List<Poblacion> findPoblacionByNombreAndMunicipioAndEstado(String nombrePob, Municipio mun);

    List<Region> getRegiones();

    /**
     * FJAH 27.05.2025
     * @param idZona
     * @return total de numeración asignada por zona
     */
    Integer getTotalNumeracionAsignadaPorZona(Integer idZona);


    /**
     * FJAH 27.06.2025
     * @param idZona zona geográfica
     * @return total de municipios únicos
     */
    Long getTotalMunicipiosByZona(Integer idZona) throws Exception;

    /**
     * FJAH 27.05.2025
     * @param idZona
     * @return
     * @throws Exception
     */
    List<Municipio> findMunicipiosByZona(Integer idZona) throws Exception;

    /**
     * FJAH 29.05.2025
     * @param idRegion
     * @return
     */
    List<Proveedor> findAllPrestadoresServicioByZona(Integer idRegion);

}
