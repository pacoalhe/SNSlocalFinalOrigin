package mx.ift.sns.negocio.centrales;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.CentralesRelacion;
import mx.ift.sns.modelo.central.ComboCentral;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.central.ReporteCentralVm;
import mx.ift.sns.modelo.central.VCatalogoCentral;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCentrales;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMarcaModelo;
import mx.ift.sns.modelo.filtros.FiltroReporteadorCentrales;

/**
 * Interfaz servicio de Centrales.
 */
public interface ICentralesService {

    /**
     * Devuelve todas las marcas.
     * @return List<Marca>
     * @throws Exception Exception
     */
    List<Marca> findAllMarcas() throws Exception;

    /**
     * Obtiene la marca por el nombre.
     * @param nombre nombre
     * @return Marca
     * @throws Exception exception
     */
    Marca getMarcaByNombre(String nombre) throws Exception;

    /**
     * Busqueda de una marca por su id.
     * @param idMarca id de la marca a encontrar
     * @return Marca
     * @throws Exception ex
     */
    Marca getMarcaById(BigDecimal idMarca) throws Exception;

    /**
     * Persite la relacion entre centrales de origen y destino.
     * @param centralesRelacion relacion
     * @return CentralesRelacion relacion
     */
    CentralesRelacion saveCentralesRelacion(CentralesRelacion centralesRelacion);

    /**
     * Salva una Marca.
     * @param marca Marca a guardar
     * @return marca guardada
     */
    Marca saveMarca(Marca marca);

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
     * Devuelve las centrales por su modelo.
     * @param modelo Modelo
     * @return lista centrales
     * @throws Exception exception
     */
    List<Central> getCentralesActivasByModelo(Modelo modelo);

    /**
     * Método que busca las centrales.
     * @param pFiltrosCentral pFiltrosCentral
     * @return List<VCatalogoCentral>
     * @throws Exception Exception
     */
    List<VCatalogoCentral> findAllCentrales(FiltroBusquedaCentrales pFiltrosCentral);

    /**
     * Devuelve el número de centrales.
     * @param pFiltrosCentral pFiltrosCentral
     * @return int
     */
    int findAllCentralesCount(FiltroBusquedaCentrales pFiltrosCentral);

    /**
     * Método que salva una central.
     * @param central central
     * @return central central
     * @throws Exception exception
     */
    Central saveCentral(Central central) throws Exception;

    /**
     * Método que salva una central desde
     * el Tramite de Asignacion automatica NG
     * @param central central
     * @return central central
     * @throws Exception exception
     */
    Central saveCentralFromAsignacion(Central central)throws Exception;
    
    /**
     * Método que hace que la central pase de Activo a Inactivo.
     * @param central central
     * @throws Exception Exception
     */
    void bajaCentral(Central central) throws Exception;

    /**
     * Exporta los datos de la central.
     * @param pfiltro pfiltro
     * @return byte[]
     * @throws Exception exception
     */
    byte[] getExportConsultaCatalogoCentrales(FiltroBusquedaCentrales pfiltro) throws Exception;

    /**
     * Busca las centrales por nombre.
     * @param name name
     * @return List<Central>
     * @throws Exception exception
     */
    List<Central> findAllCentralesByName(String name);

    /**
     * Método que busca las marcas.
     * @param filtros filtros
     * @return List<Marca>
     */
    List<Marca> findAllMarcasEager(FiltroBusquedaMarcaModelo filtros);

    /**
     * Devuelve el número de marcas.
     * @param filtros filtros
     * @return int
     */
    int findAllMarcasCount(FiltroBusquedaMarcaModelo filtros);

    /**
     * Genera listado de Modelos de una Marca.
     * @param idMarca identificador de la marca
     * @return List<Modelo> lista de modelos
     */
    List<Modelo> getModelosByMarca(BigDecimal idMarca);

    /**
     * Lista modelos con los criterios de búsqueda.
     * @param filtros criterios de búsqueda
     * @return lista
     */
    List<Modelo> findAll(FiltroBusquedaMarcaModelo filtros);

    /**
     * Número de elementos según los criterios de búsqueda.
     * @param filtros criterios de búsqueda
     * @return número de elementos
     */
    int findAllCount(FiltroBusquedaMarcaModelo filtros);

    /**
     * Lista de estatus de una marca (edición, alta).
     * @return estatus
     */
    List<Estatus> findAllEstatus();

    /**
     * Exporta los datos de las marcas.
     * @param pListaMarca pListaMarca
     * @return byte[]
     * @throws Exception exception
     */
    byte[] getExportConsultaMarcas(List<Marca> pListaMarca) throws Exception;

    /**
     * Busca las centrales por localidad a las que da servicio.
     * @param filtro FiltroReporteadorCentrales
     * @return ReporteCentralVm
     */
    List<ReporteCentralVm> findAllCentralesPorLocalidad(FiltroReporteadorCentrales filtro);

    /**
     * Count de centrales con localidades a las que da servicio.
     * @param pFiltrosCentral filtro
     * @return count
     */
    int findAllCentralesPorLocalidadCount(FiltroReporteadorCentrales pFiltrosCentral);

    /**
     * Comprueba que una central extiste por su nombre, proveedor y coordenadas. Retorna una lista de centrales por ABN
     * asignado.
     * @param central central
     * @return centrales
     */
    Central comprobarCentral(Central central);

}
