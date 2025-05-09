package mx.ift.sns.dao.ng;

import java.util.List;

import mx.ift.sns.modelo.reporteabd.SerieArrendada;
import mx.ift.sns.modelo.reporteabd.SerieArrendadaPadre;

/**
 * Interfaz de definición de los métodos de Series Arrendadas.
 */
public interface ISerieArrendadaDAO {

    /**
     * Crea la serie arrendada.
     * @param serie serie arrendada
     * @return serie modificada
     */
    SerieArrendada create(SerieArrendada serie);

    /**
     * Crea la serie arrendada padre.
     * @param serie serie arrendada
     */
    void create(SerieArrendadaPadre serie);

    /**
     * Borra todas las filas de la tabla.
     */
    void deleteAll();

    /**
     * Devuelve la lista de series arrendadas.
     * @return lista
     */
    List<SerieArrendada> findAll();

    /**
     * Devuelve la lista de series arrendadas empezando por first.
     * @param first primer resultado
     * @param pageSize numero maximo de resultados
     * @return lista de series
     */
    List<SerieArrendada> findAll(int first, int pageSize);

    /**
     * Devuelve el numero de series arrendadas.
     * @return count
     */
    int getSeriesArrendadasCount();

    /**
     * Devuelve la lista de series arrendadas padres empezando por first.
     * @param first primer resultado
     * @param pageSize numero maximo de resultados
     * @return lista de series
     */
    List<SerieArrendadaPadre> findAllPadres(int first, int pageSize);

    /**
     * Devuelve el número de series arrendadas padres.
     * @return número de series arrendadas padre
     */
    int findAllPadresCount();
}
