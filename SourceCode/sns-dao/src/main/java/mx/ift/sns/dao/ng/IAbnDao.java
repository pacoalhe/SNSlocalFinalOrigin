package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.VCatalogoAbn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.ot.Poblacion;

/**
 * Interfaz de definición de los métodos para base de datos para áreas de numeración.
 */
public interface IAbnDao extends IBaseDAO<Abn> {

    /**
     * Recupera el catálogo de áreas de numeración.
     * @return List
     */
    List<Abn> findAllAbns();

    /**
     * Recupera los ABNs que concuerdan con los fltros seleccionados.
     * @param pFiltros Filtros de Búsqueda.
     * @return List
     */
    List<Abn> findAllAbns(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera los ABNs que concuerdan con los fltros seleccionados.
     * @param pFiltros Filtros de Búsqueda.
     * @return List VCatalogoAbn
     */
    List<VCatalogoAbn> findAllAbnsForCatalog(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera el número de ABNs que concuerdan con los fltros seleccionados.
     * @param pFiltros Filtros de Búsqueda.
     * @return Número de ABNs que concuerdan con los fltros seleccionados.
     */
    int findAllAbnsCount(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera un ABN por código.
     * @param pCodigo BigDecimal
     * @return Abn
     */
    Abn getAbnById(BigDecimal pCodigo);

    /**
     * Recupera la poblacion ancla de un ABN.
     * @param codigo abn
     * @return poblacion ancla
     */
    Poblacion getPoblacionAnclaByCodigoAbn(BigDecimal codigo);

    /**
     * @param codigoNir codigoNir.
     * @return abn Abn.
     */
    Abn getAbnByCodigoNir(String codigoNir);

    List<Abn> getAbnByZona(int zona);

    /**
     * Cambia el Código de un ABN actualizando todas las tablas que hacen referencia a ése ABN.
     * @param pViejoAbn Información del ABN a modificar.
     * @param pNuevoAbn Información del nuevo ABN
     * @return Abn modificado.
     * @throws Exception en caso de error.
     */
    Abn changeAbnCode(Abn pViejoAbn, Abn pNuevoAbn) throws Exception;

    /**
     * Comprueba que un codigo de ABN existe en el SNS.
     * @param abn abn
     * @return boolean
     */
    boolean existAbn(String abn);

    /**
     * Busca todos los ABN de un estado.
     * @param estado String
     * @return List<Abn>
     */
    List<Abn> findAbnInEstado(String estado);

    /**
     * Obtiene la lista de Abn de un municipio.
     * @param municipio String
     * @param estado String
     * @return List<Abn>
     */
    List<Abn> findAbnInMunicipio(String municipio, String estado);

    /**
     * Obtiene el Abn de una poblacion.
     * @param poblacion Poblacion
     * @return Abn
     */
    Abn getAbnByPoblacion(Poblacion poblacion);
}
