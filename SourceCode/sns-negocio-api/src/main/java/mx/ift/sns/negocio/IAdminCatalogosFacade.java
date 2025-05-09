package mx.ift.sns.negocio;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.ComboCentral;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.central.VCatalogoCentral;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.EquipoSenalCpsi;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.EstudioEquipoCpsi;
import mx.ift.sns.modelo.cpsi.InfoCatCpsi;
import mx.ift.sns.modelo.cpsi.VEstudioCPSI;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EquipoSenalCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.EstudioEquipoCPSN;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.cpsn.VEstudioCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCentrales;
import mx.ift.sns.modelo.filtros.FiltroBusquedaClaveServicio;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaEquipoSenal;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMarcaModelo;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMunicipios;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPlantillas;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPoblaciones;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.oficios.Plantilla;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.ExportMunicipio;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.Region;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.series.VCatalogoSerie;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.cpsi.DetalleImportacionEquiposCpsi;
import mx.ift.sns.negocio.cpsn.DetalleImportacionEquiposCpsn;

/**
 * Interfaz de Adminstración de Catalogos.
 */
public interface IAdminCatalogosFacade extends IConfiguracionFacade {

    /**
     * Devuelve el valor de un parametro por su nombre.
     * @param name nombre
     * @return valor
     */
    String getParamByName(String name);

    /**
     * Devuelve la marca a partir del nombre.
     * @param nombre nombre
     * @return Marca
     * @throws Exception exception
     */
    Marca getMarcaByNombre(String nombre) throws Exception;

    /**
     * Método que obtiene la marca a partir del id.
     * @param idMarca idMarca
     * @return Marca
     * @throws Exception Exception
     */
    Marca getMarcaById(BigDecimal idMarca) throws Exception;

    /**
     * Devuelve el listado de marcas.
     * @return List<Marca>
     * @throws Exception exception
     */
    List<Marca> findAllMarcas() throws Exception;

    /**
     * Lista de estado.
     * @return lista de estados
     * @throws Exception exepcion
     */
    List<Estado> findAllEstados() throws Exception;

    /**
     * Lista de estado.
     * @param first first
     * @param pagesize pagesize
     * @return lista de estados
     * @throws Exception exepcion
     */
    List<Estado> findEstados(int first, int pagesize) throws Exception;

    /**
     * Devuelve un estado por su id.
     * @param id estado
     * @return estado
     * @throws Exception excepcion
     */
    Estado findEstadoById(String id) throws Exception;

    /**
     * Lista de municipios por estado.
     * @param estado estado
     * @return lista de municipios
     * @throws Exception exepcion
     */
    List<Municipio> findMunicipiosByEstado(String estado) throws Exception;

    /**
     * Numero Total de estados.
     * @return numero estados
     * @throws Exception exepcion
     */
    int findAllEstadosCount();

    /**
     * Método para guardar el estado.
     * @param estado estado
     * @return Estado
     */
    Estado saveEstado(Estado estado);

    /**
     * Excel con los Estados y sus municpios.
     * @param listaEstados listaEstados
     * @return byte[]
     * @throws Exception exception
     */
    byte[] generarListadoEstados(List<Estado> listaEstados) throws Exception;

    /**
     * Devuelve lista de poblacion de un estado y municipio concreto.
     * @param estado Código de Estado
     * @param municipio Código de Municipio
     * @return Lista de poblaciones por estado y municipio
     */
    List<Poblacion> findAllPoblaciones(String estado, String municipio);

    /**
     * Devuelve lista de poblacion de un estado y municipio concreto.
     * @param estado Código de Estado
     * @param municipio Código de Municipio
     * @param pUseCache Indica si se han de cachear los resultados para agilizar.
     * @return Lista de poblaciones por estado y municipio
     */
    List<Poblacion> findAllPoblaciones(String estado, String municipio, boolean pUseCache);

    /**
     * Método para guardar la poblacion.
     * @param poblacion poblacion
     * @return Poblacion
     */
    Poblacion savePoblacion(Poblacion poblacion);

    /**
     * Método para guardar la relación poblacion-abn.
     * @param poblacionAbn poblacionAbn
     * @return PoblacionAbn
     */
    PoblacionAbn savePoblacionAbn(PoblacionAbn poblacionAbn);

    /**
     * Genera un Documento Excel con la información de las poblaciones pasadas como parámetro.
     * @param pFiltros FiltroBusquedaPoblaciones
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] getExportConsultaCatalogoPoblaciones(FiltroBusquedaPoblaciones pFiltros) throws Exception;

    /**
     * Recupera la lista de proveedores activos completa sin filtros.
     * @return List<Proveedor>
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedoresActivos() throws Exception;

    /**
     * Recupera la lista de Proveedores completa, sin filtros.
     * @return List
     * @throws Exception en caso de error.
     */
    List<Proveedor> findAllProveedores() throws Exception;

    /**
     * Obtiene el combo del catalogo de centrales.
     * @return combo centrales
     */
    List<ComboCentral> getComboCentrales();

    /**
     * Devuelve una central por su id.
     * @param idCentral id central
     * @return central por id
     * @throws Exception exception
     */
    Central findCentralById(BigDecimal idCentral) throws Exception;

    /**
     * Lista de centrales.
     * @param pFiltrosCentral pFiltrosCentral
     * @return lista de centrales
     * @throws Exception exepcion
     */
    List<VCatalogoCentral> findAllCentrales(FiltroBusquedaCentrales pFiltrosCentral);

    /**
     * Numero Total de centrales.
     * @param pFiltrosCentral pFiltrosCentral
     * @return numero estados
     * @throws Exception exepcion
     */
    int findAllCentralesCount(FiltroBusquedaCentrales pFiltrosCentral);

    /**
     * Método que hace que la central pase de Activo a Inactivo.
     * @param central central
     * @throws Exception Exception
     */
    void bajaCentral(Central central) throws Exception;

    /**
     * Modelos asociados a una marca.
     * @param idMarca idMarca
     * @return List<Modelo>
     */
    List<Modelo> getModelosByMarca(BigDecimal idMarca);

    /**
     * Modelos asociados a unas filtros.
     * @param filtros filtros
     * @return List<Modelo>
     */
    List<Modelo> findAllModelos(FiltroBusquedaMarcaModelo filtros);

    /**
     * Cuanta los modelos a partir de los filtros introducidos.
     * @param filtros filtros
     * @return int
     */
    int findAllModelosCount(FiltroBusquedaMarcaModelo filtros);

    /**
     * Devuelve las centrales activas por su Modelo.
     * @param modelo Información del Modelo.
     * @return lista centrales
     * @throws Exception exception
     */
    List<Central> getCentralesActivasByModelo(Modelo modelo);

    /**
     * Guarda una Marca.
     * @param marca Marca a guardar
     * @return Marca guardada
     */
    Marca saveMarca(Marca marca);

    /**
     * Obtiene la lista de estatus de una marca/modelo.
     * @return List
     */
    List<Estatus> findAllEstatus();

    /**
     * Obtiene la lista de Regiones.
     * @return lista region
     */
    List<Region> findAllRegiones();

    /**
     * Busca los municipios por filtro.
     * @param filtros filtros
     * @return lista municipios
     */
    List<Municipio> findAllMunicipios(FiltroBusquedaMunicipios filtros);

    /**
     * Lista de poblaciones.
     * @param pFiltrosPoblacion pFiltrosPoblacion
     * @return lista de poblaciones
     * @throws Exception exepcion
     */
    List<Poblacion> findAllPoblaciones(FiltroBusquedaPoblaciones pFiltrosPoblacion);

    /**
     * Numero Total de Poblaciones.
     * @param pFiltrosPoblacion pFiltrosPoblacion
     * @return numero estados
     * @throws Exception exepcion
     */
    int findAllPoblacionesCount(FiltroBusquedaPoblaciones pFiltrosPoblacion);

    /**
     * Guarda una lista de poblaciones nuevas.
     * @param listaPoblaciones poblaciones
     * @throws Exception error
     */
    void guardaPoblaciones(List<Poblacion> listaPoblaciones) throws Exception;

    /**
     * Método que salva una central.
     * @param central central
     * @return central central
     * @throws Exception exception
     */
    Central saveCentral(Central central) throws Exception;

    /**
     * Devuelve una poblacion por su id.
     * @param inegi inegi
     * @return poblacion poblacion
     * @throws Exception excepcion
     */
    Poblacion findPoblacionById(String inegi) throws Exception;

    /**
     * Marcas asociados a unas filtros.
     * @param filtros filtros
     * @return List<Marca>
     */
    List<Marca> findAllMarcasEager(FiltroBusquedaMarcaModelo filtros);

    /**
     * Cuanta las marcas a partir de los filtros introducidos.
     * @param filtros filtros
     * @return int
     */
    int findAllMarcasCount(FiltroBusquedaMarcaModelo filtros);

    /**
     * Devuelve un municipio por su id.
     * @param id estado y municipio
     * @return municipio
     * @throws Exception excepcion
     */
    Municipio findMunicipioById(MunicipioPK id) throws Exception;

    /**
     * Obtiene el total de numeracion asignada en un municipio.
     * @param municipio municipio
     * @return total asignados
     */
    Integer getTotalNumRangosAsignadosByMunicipio(Municipio municipio);

    /**
     * Obtiene el número de municipios por filtro.
     * @param filtros filtro
     * @return total
     */
    Integer findAllMunicipiosCount(FiltroBusquedaMunicipios filtros);

    /**
     * Recupera un municipio a partir del estado y del municipio.
     * @param codEstado estado
     * @param codMunicipio municipio
     * @return boolean existe
     */
    boolean getMunicipioByEstado(String codEstado, String codMunicipio);

    /**
     * Guarda una lista de muncipios nuevos.
     * @param listaMunicipios Municipios
     * @throws Exception error
     */
    void guardaMunicipios(List<Municipio> listaMunicipios) throws Exception;

    /**
     * Persiste un Municipio.
     * @param municipio municipio
     * @return municipio municipio
     */
    Municipio saveMunicipio(Municipio municipio);

    /**
     * Exporta los datos de las centrales.
     * @param pfiltro FiltroBusquedaCentrales
     * @return byte[]
     * @throws Exception ex
     */
    byte[] getExportConsultaCatalogoCentrales(FiltroBusquedaCentrales pfiltro) throws Exception;

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
     * Busca las centrales por nombre.
     * @param name name
     * @return List<Central>
     * @throws Exception exception
     */
    List<Central> findAllCentralesByName(String name);

    /**
     * Genera un Documento Excel con la información de los ABNs pasados como parámetro.
     * @param pFiltros Filtros de búsqueda seleccionados en el interfaz del catálogo de ABNs.
     * @return Documento Excel Serializado
     * @throws Exception En caso de error.
     */
    byte[] getExportConsultaCatalogoABNs(FiltroBusquedaABNs pFiltros) throws Exception;

    /**
     * Elimina un municipio.
     * @param municipio Municipio
     */
    void removeMunicipio(Municipio municipio);

    /**
     * Genera un excel de municipio con los filtros de busqueda.
     * @param listaDatos busqueda
     * @return fichero
     * @throws Exception error
     */
    InputStream generarListadoMunicipios(List<ExportMunicipio> listaDatos) throws Exception;

    /**
     * Genera la lista exportable de municipios con sus poblaciones.
     * @param filtros FiltroBusquedaMunicipios
     * @return lista datos
     */
    List<ExportMunicipio> findAllExportMunicipio(FiltroBusquedaMunicipios filtros);

    /**
     * Obtiene el total exportable de municipios con sus poblaciones.
     * @param filtros FiltroBusquedaMunicipios
     * @return total datos
     */
    Integer findAllExportMunicipioCount(FiltroBusquedaMunicipios filtros);

    /**
     * Obtiene todas las claves de servicio estean activas o inactivas.
     * @return List<ClaveServicio>
     */
    List<ClaveServicio> findAllClaveServicio();

    /**
     * Recupera una Clave de Servicio por su código.
     * @param pCodigo Código de Clave de Servicio.
     * @return ClaveServicio
     */
    ClaveServicio getClaveServicioByCodigo(BigDecimal pCodigo);

    /**
     * Recupera el número rangos solicitados en función de los parámetros facilitados.
     * @param pFiltros Filtros de Búsqueda
     * @return int Número de registros
     * @throws Exception en caso de error.
     */
    int findAllRangosCount(FiltroBusquedaRangos pFiltros) throws Exception;

    /**
     * Recupera un nir por su código.
     * @param cdgNir codigo
     * @return el nir
     */
    Nir getNirByCodigo(int cdgNir);

    /**
     * Recupera la lista de NIRs cuyos primeros dígitos coinciden con el código dado.
     * @param pCodigoNir Parte del Código NIR
     * @return Lista de Nirs
     */
    List<Nir> findAllNirByParteCodigo(int pCodigoNir);

    /**
     * Recupera la lista de NIRs asociados a un ABN.
     * @param numAbn Información del ABN.
     * @return List<Nir>
     * @throws Exception Exception
     */
    List<Nir> findAllNirByAbn(BigDecimal numAbn) throws Exception;

    /**
     * Marca un Nir y todas sus Series como Desactivadas.
     * @param pNir Información del Nir
     * @throws Exception en caso de error.
     */
    void desactivarNir(Nir pNir) throws Exception;

    /**
     * Crea un nuevo Nir para un ABN existente con los SNA indicados.
     * @param pAbn Información del ABN
     * @param pCdgNir Código del Nuevo NIR.
     * @param pListaSnas Identificador de los SNA (nuevos o existentes)
     * @return Nuevo NIR.
     * @throws Exception En caso de error.
     */
    Nir createNir(Abn pAbn, int pCdgNir, List<String> pListaSnas) throws Exception;

    /**
     * Guarda un Nir en la BD.
     * @param nir nir
     * @return Nir
     */
    Nir saveNir(Nir nir);

    /**
     * Recupera una serie por SNA y NIR.
     * @param pIdSna BigDecimal
     * @param pIdNir BigDecimal
     * @return Serie
     * @throws Exception en caso de que haya un error en la búsqueda
     */
    Serie getSerie(BigDecimal pIdSna, BigDecimal pIdNir) throws Exception;

    /**
     * Crea una nueva serie para el Nir indicado.
     * @param pNir Información del Nir.
     * @param pSna Identificador de Serie.
     * @return Serie
     */
    Serie createSerie(Nir pNir, BigDecimal pSna);

    /**
     * Actualiza un abn.
     * @param abn abn
     * @return Abn
     * @throws Exception exception
     */
    Abn saveAbn(Abn abn) throws Exception;

    /**
     * Recupera un ABN por código.
     * @param pCodigo BigDecimal
     * @return Abn
     * @throws Exception en caso de error
     */
    Abn getAbnById(BigDecimal pCodigo) throws Exception;

    /**
     * Cambia el Código de un ABN actualizando todas las tablas que hacen referencia a ése ABN.
     * @param pAbn Información del ABN a modificar.
     * @param pNewCode Nuevo código de ABN.
     * @return Abn modificado.
     * @throws Exception en caso de error.
     */
    Abn changeAbnCode(Abn pAbn, BigDecimal pNewCode) throws Exception;

    /**
     * Actualiza la relación de poblaciones de un ABN.
     * @param pCodAbn Identificador del ABN.
     * @param pPoblacionesAbn Lista de poblaciones asociadas al ABN.
     */
    void updatePoblacionesAbn(BigDecimal pCodAbn, List<PoblacionAbn> pPoblacionesAbn);

    /**
     * Recupera la lista de poblaciones de un abn.
     * @param pAbn Información del Abn.
     * @return List<Poblacion> poblaciones.
     */
    List<Poblacion> findAllPoblacionesByAbn(Abn pAbn);

    /**
     * Recupera la lista de poblaciones de un abn.
     * @param pAbn Información del Abn.
     * @param pUseCache Indica si se ha de almacenar la información en caché o no.
     * @return List<Poblacion> poblaciones.
     */
    List<Poblacion> findAllPoblacionesByAbn(Abn pAbn, boolean pUseCache);

    /**
     * Recupera la lista de municipios de un abn.
     * @param pAbn Información del Abn.
     * @return List<Municipio> municipios.
     */
    List<Municipio> findAllMunicipiosByAbn(Abn pAbn);

    /**
     * Recupera la lista de municipios de un abn.
     * @param pAbn Información del Abn.
     * @param pUseCache Indica si se ha de almacenar la información en caché o no.
     * @return List<Municipio> municipios.
     */
    List<Municipio> findAllMunicipiosByAbn(Abn pAbn, boolean pUseCache);

    /**
     * Recupera todos las plantillas almacenadas.
     * @return List
     */
    List<Plantilla> findAllPlantillas();

    /**
     * Recupera todos las plantillas almacenadas según los criterios dados.
     * @param pFiltros Filtros de Búsqueda y paginación.
     * @return List
     */
    List<Plantilla> findAllPlantillas(FiltroBusquedaPlantillas pFiltros);

    /**
     * Recupera el número de plantillas almacenadas según los criterios dados.
     * @param pFiltros Filtros de Búsqueda y paginación.
     * @return número de plantillas almacenadas según los criterios dados.
     */
    int findAllPlantillasCount(FiltroBusquedaPlantillas pFiltros);

    /**
     * Recupera el catálogo de tipos de solicitud.
     * @return List
     */
    List<TipoSolicitud> findAllTiposSolicitud();

    /**
     * Método que devuelve el tipo solicitud.
     * @param idTipoSolicitud idTipoSolicitud
     * @return TipoSolicitud
     */
    TipoSolicitud getTipoSolicitudById(Integer idTipoSolicitud);

    /**
     * Recupera el catálogo de tipos de destinatario.
     * @return List
     */
    List<TipoDestinatario> findAllTiposDestinatario();

    /**
     * Recupera un Tipo Destinatario por su código.
     * @param pCdgDestinatario Código de Tipo de Destinatario
     * @return TipoDestinatario
     */
    TipoDestinatario getTipoDestinatarioByCdg(String pCdgDestinatario);

    /**
     * Almacena una nueva plantilla en el catálogo de plantillas.
     * @param pPlantilla Plantilla
     * @return Plantilla
     */
    Plantilla savePlantilla(Plantilla pPlantilla);

    /**
     * Recupera una plantilla del catálogo de plantillas.
     * @param pTipoSolicitud Tipo de solicitud.
     * @param pTipoDestinatario Tipo de destinatario del oficio.
     * @return Objeto plantilla con el documento serializado.
     */
    Plantilla getPlantilla(TipoSolicitud pTipoSolicitud, TipoDestinatario pTipoDestinatario);

    /**
     * Genera el fichero excel serializado con la exportación de la consulta del catálogo de plantillas.
     * @param pListaPlantillas Lista de Plantillas
     * @return Fichero Excel Serializado.
     * @throws Exception en caso de error.
     */
    byte[] getExportConsultaCatalogoPlantillas(List<Plantilla> pListaPlantillas) throws Exception;

    /**
     * Persiste una Serie y sus cambios.
     * @param pSerie Información de la Serie.
     * @return Serie persistida.
     */
    Serie saveSerie(Serie pSerie);

    /**
     * Genera un Documento Excel con la información de las Marcas y Modelos pasados como parámetro.
     * @param pListaMarca Lista de Marcas y Modelos a mostrar en el documento.
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] getExportConsultaCatalogoMarcas(List<Marca> pListaMarca) throws Exception;

    /**
     * Recupera el listado de Claves de Servicio que cumplen con el filtro de búsqueda.
     * @param pFiltros FiltroBusquedaClaveServicio
     * @return List<ClaveServicio>
     * @throws Exception ex
     */
    List<ClaveServicio> findAllClaveServicio(FiltroBusquedaClaveServicio pFiltros) throws Exception;

    /**
     * Obtiene el número de claves de servicio que cumplen los filtros de búsqueda.
     * @param pFiltros FiltroBusquedaClaveServicio
     * @return int
     * @throws Exception ex
     */
    int findAllClaveServicioCount(FiltroBusquedaClaveServicio pFiltros) throws Exception;

    /**
     * Función que valida que los datos a guardar de la clave de servicio.
     * @param claveServicio ClaveServicio
     * @param modoEdicion boolean
     * @return claveServicio ClaveServicio
     */
    ClaveServicio validaClaveServicio(ClaveServicio claveServicio, boolean modoEdicion);

    /**
     * Función que guarda la clave de servicio.
     * @param claveServicio ClaveServicio
     * @param modoEdicion boolean
     * @return claveServicio ClaveServicio
     */
    ClaveServicio guardarClaveServicio(ClaveServicio claveServicio, boolean modoEdicion);

    /**
     * Recupera todas las series.
     * @param pFiltros pFiltros
     * @return List<VCatalogoSerie>
     * @throws Exception Exception
     */
    List<VCatalogoSerie> findAllCatalogoSeries(FiltroBusquedaSeries pFiltros) throws Exception;

    /**
     * Recupera el número de resultados de búsqueda de series.
     * @param pFiltros pFiltros
     * @return int
     * @throws Exception Exception
     */
    int findAllCatalogoSeriesCount(FiltroBusquedaSeries pFiltros) throws Exception;

    /**
     * Función que obtiene el listado de Nirs Activos.
     * @return List<Nir>
     * @throws Exception exception
     */
    List<Nir> findAllNirs() throws Exception;

    /**
     * Exporta los datos de las series.
     * @param pfiltro pfiltro
     * @return byte[]
     * @throws Exception ex
     */
    byte[] getExportConsultaCatalogoSeries(FiltroBusquedaSeries pfiltro) throws Exception;

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return List<EquipoSenal>
     * @throws Exception ex
     */
    List<EquipoSenalCPSN> findAllEquiposSenal(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception;

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda para la exportación.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return byte[] excel
     * @throws Exception ex
     */
    byte[] exportarEquiposCPSN(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception;

    /**
     * Función que busca los códigos de señalización internacional que cumplen con los criterios de búsqueda para la
     * exportación.
     * @param estudio Información del informe.
     * @return byte[]
     * @throws Exception Lanza una Excepcion
     */
    byte[] exportarEstudioCPSI(VEstudioCPSI estudio) throws Exception;

    /**
     * Método que devuelve todos los Nirs de tres cifras que cuyas dos primeras cifras coinciden con el código pasado
     * por parámetros.
     * @param cdgNir cdgNir
     * @return List<Nir>
     */
    List<Nir> findNirsByDigitos(int cdgNir);

    /**
     * Comprueba que una serie exite con un nir dado.
     * @param abn abn
     * @param nir nir
     * @return boolean
     */
    boolean existSerieWithNirAbn(String abn, String nir);

    /**
     * Devuelve un tipo de bloque CPSN por su id.
     * @param id tipo de bloque
     * @return Tipo de bloque CPSN
     * @throws Exception excepcion
     */
    TipoBloqueCPSN getTipoBloqueCPSNById(String id) throws Exception;

    /**
     * Devuelve un estatus CPSN por su id.
     * @param id estatus CPSN
     * @return estatus CPSN
     * @throws Exception excepcion
     */
    EstatusCPSN getEstatusCPSNById(String id) throws Exception;

    /**
     * Consulta de todos los tipos de bloque CPSN.
     * @return listado de los bloques.
     */
    List<TipoBloqueCPSN> findAllTiposBloqueCPSN();

    /**
     * Consulta de todos los estatus de CPSN.
     * @return listado de estatusCPSN
     */
    List<EstatusCPSN> findAllEstatusCPSN();

    /**
     * Consulta de los códigos CPSN que cumplen el filtro.
     * @param pFiltro filtro de la búsqueda
     * @return listado de codigos.
     */
    List<CodigoCPSN> findCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltro);

    /**
     * Genera un Documento Excel con la información de los códigos CPS nacioonales.
     * @param pFiltros FiltroBusquedaCodigosCPSN
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] exportarCodigosCPSN(FiltroBusquedaCodigosCPSN pFiltros) throws Exception;

    /**
     * Método encargado de validar y liberar los códigos seleccionados.
     * @param codigosCPSNSeleccionados listado de códigos a liberar
     * @return error en caso de producirse
     */
    String liberarCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados);

    /**
     * Método encargado de validar y reservar los códigos seleccionados.
     * @param codigosCPSNSeleccionados listado de códigos a liberar
     * @return error en caso de producirse
     */
    String reservarCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados);

    /**
     * Método encargado de validar si el código se puede desagrupar.
     * @param codigosCPSNSeleccionados código a desagrupar
     * @return error en caso de producirse
     */
    String validarDesagrupacionCodigosCPSN(List<CodigoCPSN> codigosCPSNSeleccionados);

    /**
     * Método encargado de desagrupar el código.
     * @param codigosCPSNSeleccionados código a desagrupar
     * @return error si no pasa alguna validación
     */
    String desagruparCodigoCPSN(List<CodigoCPSN> codigosCPSNSeleccionados);

    /**
     * Método encargado de agrupar el código.
     * @param codigosCPSNSeleccionados código a agrupar
     * @return error si no pasa alguna validación
     */
    String agruparCodigoCPSN(List<CodigoCPSN> codigosCPSNSeleccionados);

    /**
     * Método encargado de obtener los datos del estudio de códigos CPS Nacionales.
     * @return List<VEstudioCPSN> estudio
     */
    List<VEstudioCPSN> estudioCPSN();

    /**
     * Función que elimina el equipo de señalización cpsn.
     * @param equipo cpsn
     */
    void eliminarEquipo(EquipoSenalCPSN equipo);

    /**
     * Método encargado de obtener los datos del estudio de equipos CPS Nacionales.
     * @param pst proveedor
     * @return List<EstudioEquipoCPSN> estudio
     */
    List<EstudioEquipoCPSN> estudioEquipoCPSN(Proveedor pst);

    /**
     * Método encargado de obtener el equipo de señalización cargando los warnings.
     * @param equipo a cargar
     * @return equipo cargado
     */
    EquipoSenalCPSN getEquipoSenalCPSNEagerLoad(EquipoSenalCPSN equipo);

    /**
     * Recupera la lista de PoblacionesAbn según los filtros dados.
     * @param pFiltros Condiciones de búsqueda.
     * @return List<PoblacionAbn> Lista de PoblacionesAbn.
     */
    List<PoblacionAbn> findAllPoblacionesAbn(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera el número de PoblacionesAbn según los filtros dados.
     * @param pFiltros Condiciones de búsqueda.
     * @return List<PoblacionAbn> Lista de PoblacionesAbn.
     */
    int findAllPoblacionesAbnCount(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera el catálogo de estados de ABN.
     * @return List
     */
    List<EstadoAbn> findAllEstadosAbn();

    /**
     * Recupera el catálogo de áreas de numeración.
     * @return List
     * @throws Exception en caso de error
     */
    List<Abn> findAllAbns() throws Exception;

    /**
     * Recupera los ABNs que concuerdan con los fltros seleccionados.
     * @param pFiltros Filtros de Búsqueda.
     * @return List
     */
    List<Abn> findAllAbns(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera el número de ABNs que concuerdan con los fltros seleccionados.
     * @param pFiltros Filtros de Búsqueda.
     * @return Número de ABNs que concuerdan con los fltros seleccionados.
     */
    int findAllAbnsCount(FiltroBusquedaABNs pFiltros);

    /**********************************************************/

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return List<EquipoSenal>
     * @throws Exception ex
     */
    /*
     * List<EquipoSenalCPSI> findAllEquiposSenalizacionCPSI(FiltroBusquedaEquipoSenal pFiltros) throws Exception;
     */

    /**
     * Devuelve un estatus CPSI por su id.
     * @param id estatus CPSI
     * @return estatus CPSI
     * @throws Exception excepcion
     */
    EstatusCPSI getEstatusCPSIById(String id) throws Exception;

    /**
     * Consulta de todos los estatus de CPSI.
     * @return listado de estatusCPSI
     */
    List<EstatusCPSI> findAllEstatusCPSI();

    /**
     * Consulta de los códigos CPSI que cumplen el filtro.
     * @param pFiltro filtro de la búsqueda
     * @return listado de codigos.
     */
    List<CodigoCPSI> findCodigosCPSI(FiltroBusquedaCodigosCPSI pFiltro);

    /**
     * Genera un Documento Excel con la información de los códigos CPS nacioonales.
     * @param pFiltros FiltroBusquedaCodigosCPSI
     * @return Documento Excel Serielizado
     * @throws Exception En caso de error.
     */
    byte[] exportarCodigosCPSI(FiltroBusquedaCodigosCPSI pFiltros) throws Exception;

    /**
     * Método encargado de validar y liberar los códigos seleccionados.
     * @param codigosCPSISeleccionados listado de códigos a liberar
     * @return error en caso de producirse
     */
    String liberarCodigosCPSI(List<InfoCatCpsi> codigosCPSISeleccionados);

    /**
     * Método encargado de validar y reservar los códigos seleccionados.
     * @param codigosCPSISeleccionados listado de códigos a liberar
     * @return error en caso de producirse
     */
    String reservarCodigosCPSI(List<InfoCatCpsi> codigosCPSISeleccionados);

    /**
     * Método encargado de obtener los datos del estudio de códigos CPS Internacionales.
     * @param proveedor pst para filtrar en el estudio que muestra por pantalla
     * @return List<VEstudioCPSI> estudio
     */
    VEstudioCPSI estudioCPSI(Proveedor proveedor);

    /**
     * Método encargado de obtener los datos del estudio de equipos CPS Internacionales.
     * @param pst proveedor
     * @return List<EstudioEquipoCPSN> estudio
     */
    List<EstudioEquipoCpsi> estudioEquipoCPSI(Proveedor pst);

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return List<EquipoSenalCpsi>
     * @throws Exception ex
     */
    List<EquipoSenalCpsi> findAllEquiposSenalCpsi(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception;

    /**
     * Método encargado de obtener el equipo de señalización cargando los warnings.
     * @param equipo a cargar
     * @return equipo cargado
     */
    EquipoSenalCpsi getEquipoSenalCpsiEagerLoad(EquipoSenalCpsi equipo);

    /**
     * Función que elimina el equipo de señalización cpsn.
     * @param equipo cpsn
     */
    void eliminarEquipoCpsi(EquipoSenalCpsi equipo);

    /**
     * Método encargado de procesar el archivo de equipos de señalización nacional.
     * @param archivo excel con los equipos a procesar.
     * @param pst proveedor
     * @return detalle de la importación
     * @throws Exception al procesar
     */
    DetalleImportacionEquiposCpsn procesarArchivoEquiposCpsn(File archivo, Proveedor pst)
            throws Exception;

    /**
     * Método encargado de procesar el archivo de equipos de señalización internacional.
     * @param archivo excel con los equipos a procesar.
     * @param pst proveedor
     * @return detalle de la importación
     * @throws Exception al procesar
     */
    DetalleImportacionEquiposCpsi procesarArchivoEquiposCpsi(File archivo, Proveedor pst)
            throws Exception;

    /**
     * Función que busca los equipos de señalización que cumplen con los criterios de búsqueda para la exportación.
     * @param pFiltros FiltroBusquedaEquipoSenal
     * @return byte[] excel
     * @throws Exception ex
     */
    byte[] exportarEquiposCpsi(FiltroBusquedaEquipoSenal pFiltros)
            throws Exception;

    /**
     * Consulta de los códigos CPSI que cumplen el filtro.
     * @param filtros filtro de la búsqueda
     * @return listado de codigos.
     */
    List<InfoCatCpsi> findAllInfoCatCPSI(FiltroBusquedaCodigosCPSI filtros);

    /**
     * Consulta si una poblacion tiene numeracion asignada.
     * @param poblacion Poblacion a consultar
     * @return si existe numeracion asignada
     */
    boolean isNumeracionAsignada(Poblacion poblacion);

    /**
     * Método que realiza la llamada a un procedimiento de BD para actualizar las series.
     * @param serieOriginal serieOriginal
     * @param nirOriginal nirOriginal
     * @param serieNueva serieNueva
     * @param usuario usuario
     * @return String
     */
    String actualizaSerie(BigDecimal serieOriginal, BigDecimal nirOriginal, BigDecimal serieNueva,
            BigDecimal usuario);

    /**
     * Compueba si existen solicitudes en trámite para el nir dado.
     * @param idNir Nir
     * @return boolean
     */
    boolean isSolicitudPendieteByNir(BigDecimal idNir);

    /**
     * true si hay rangos en estado pendiente para un nir seleccionado.
     * @param nir Nir
     * @return boolean
     */
    boolean isRangosPentientesByNir(Nir nir);
}
