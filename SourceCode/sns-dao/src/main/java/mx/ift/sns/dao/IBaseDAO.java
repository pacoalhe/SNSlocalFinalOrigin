package mx.ift.sns.dao;

/**
 * Interfaz que implementan los DAOs.
 * @param <T> parametro generico
 */
public interface IBaseDAO<T> {

    /**
     * Insert a new entity. create- and lastmodified-date is set with current time.
     * @param entity - detached entity object
     * @return entidad
     */
    T create(T entity);

    /**
     * Crea o actualiza el entity según sea el parámetro id nulo o no.
     * @param entity - entity object to be inserted or updated
     * @return entidad
     */
    T saveOrUpdate(T entity);

    /**
     * Deletes entity from persistence store.
     * @param entity - entity object
     */
    void delete(T entity);

    /**
     * Reloads an entity from persistance.
     * @param entity - entity object
     * @return entidad refrescada
     */
    T reload(T entity);

    /**
     * Copy the state of the given object onto the persistent object with the same identifier. If there is no persistent
     * instance currently associated with the session, it will be loaded.
     * @param entity - entity object
     * @return entity modificada
     */
    T update(T entity);

    /**
     * Refresca la entidad.
     * @param entity - entity object
     */
    void refresh(T entity);
}
