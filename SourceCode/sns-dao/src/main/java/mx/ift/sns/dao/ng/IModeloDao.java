package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMarcaModelo;

/**
 * Interfaz de definición de los métodos para base de datos para Modelos de Centrales.
 */
public interface IModeloDao extends IBaseDAO<Modelo> {

    /**
     * Comprueba si exite un modelo a partir de una marca.
     * @param id identificador de la marca
     * @param tipoModelo tipoModelo
     * @return Modelo
     */
    Modelo getModeloByMarca(BigDecimal id, String tipoModelo);

    /**
     * Recupera el listado de modelos de un marca.
     * @param id de la Marca
     * @return Lista de Modelos
     */
    List<Modelo> getModelosByMarca(BigDecimal id);

    /**
     * Recupera los modelos según los criterios de búsqueda.
     * @param pFiltros criterios busqueda
     * @return lista de modelos
     */
    List<Modelo> findAllModelos(FiltroBusquedaMarcaModelo pFiltros);

    /**
     * numero total de modelos según los criterios de búsqueda.
     * @param pFiltros criterios busqueda
     * @return numero de elementos encontrados
     */
    int findAllModelosCount(FiltroBusquedaMarcaModelo pFiltros);

    /**
     * Recupera un modelo.
     * @param id identificador del modelo
     * @return Modelo
     */
    Modelo getModeloById(BigDecimal id);
}
