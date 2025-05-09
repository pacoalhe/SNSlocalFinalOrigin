package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaABNs;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.series.Nir;

/** Interfaz de métodos DAO para PoblacionAbn. */
public interface IPoblacionAbnDao extends IBaseDAO<PoblacionAbn> {

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
     * Recupera los municipios vinculados al nir de una poblacion y un abn.
     * @param nir Nir
     * @param pUseCache boolean
     * @return List<Municipio>
     */
    List<Municipio> findAllMunicipiosByNir(Nir nir, boolean pUseCache);

    /**
     * Recupera el número de Municipios según los filtros dados.
     * @param pFiltros filtros
     * @return int
     */
    int findAllMunicipiosByAbnAndEstadoCount(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera los municipios vinculados al abn y al estado.
     * @param pFiltros filtros
     * @return List<Municipio>
     */
    List<Municipio> findAllMunicipiosByAbnAndEstado(FiltroBusquedaABNs pFiltros);

    /**
     * Recupera la lista de poblaciones de un abn.
     * @param pAbn Información del Abn.
     * @param pUseCache Indica si se ha de almacenar la información en caché o no.
     * @return List<PoblacionAbn> poblaciones.
     */
    List<PoblacionAbn> findAllPoblacionAbnByAbn(Abn pAbn, boolean pUseCache);
}
