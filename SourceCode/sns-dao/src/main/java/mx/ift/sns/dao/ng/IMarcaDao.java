package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.filtros.FiltroBusquedaMarcaModelo;

/**
 * Interfaz de definición de los métodos para base de datos para Marcas de Centrales.
 */
public interface IMarcaDao extends IBaseDAO<Marca> {

    /**
     * Comprueba si exite una marca a partir del nombre.
     * @param nombre @param
     * @return Marca
     */
    Marca getMarcaByNombre(String nombre);

    /**
     * Recupera una Marca a través de su id.
     * @param idMarca marca a recuperar
     * @return Marca
     */
    Marca getMarcaById(BigDecimal idMarca);

    /**
     * Devuelve lista de marcas.
     * @return marcas
     */
    List<Marca> findAllMarcas();

    /**
     * Listados de marcas con los criterios de los filtros.
     * @param pFiltros pFiltros
     * @return marcas
     */
    List<Marca> findAllMarcasEager(FiltroBusquedaMarcaModelo pFiltros);

    /**
     * Devuelve el número de marcas.
     * @param filtros filtros
     * @return int
     */
    int findAllMarcasCount(FiltroBusquedaMarcaModelo filtros);

}
