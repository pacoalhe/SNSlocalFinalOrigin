package mx.ift.sns.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;

import mx.ift.sns.negocio.exceptions.RegistroModificadoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase base para los DAOs.
 * @param <T> entidad sobre la que trabaja.
 */
public class BaseDAO<T> implements IBaseDAO<T> {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAO.class);

    /** Entity manager. */
    private static EntityManager em = null;

    /**
     * Utility interface between the application and the persistence provider managing the persistence unit.
     */
    private static PersistenceUnitUtil util = null;

    /**
     * Devuelve el entity manager.
     * @return entity manager
     */
    public EntityManager getEntityManager() {

        if (em == null) {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("sns");
            EntityManager em = factory.createEntityManager();
            util = factory.getPersistenceUnitUtil();

            LOGGER.debug("creado util1 {}", util);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("creado em {}", em);
            }
        }

        return em;
    }

    /**
     * Set del entity manager para pruebas.
     * @param e entitiy manager
     */
    public static void setEntityManager(EntityManager e) {
        em = e;

        util = e.getEntityManagerFactory().getPersistenceUnitUtil();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("em {}", em);
            LOGGER.debug("util {}", util);
        }
    }

    @Override
    public T create(T entity) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", entity);
        }

        getEntityManager().persist(entity);
        getEntityManager().flush();
        return entity;
    }

    @Override
    public T update(T entity) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", entity);
        }

        try {
            entity = getEntityManager().merge(entity);
            getEntityManager().flush();
        } catch (OptimisticLockException e) {
            throw new RegistroModificadoException();
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete(T entity) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", entity);
        }

        Object entityPk = util.getIdentifier(entity);
        T pAttachedEntity = (T) getEntityManager().find(entity.getClass(), entityPk);

        getEntityManager().remove(pAttachedEntity);
    }

    @Override
    public T saveOrUpdate(T entity) {

        T r = null;
        Object id = util.getIdentifier(entity);
        if (id == null) {
            r = create(entity);
        } else {
            r = update(entity);
        }

        return r;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T reload(T entity) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", entity);
        }

        return (T) getEntityManager().find(entity.getClass(), util.getIdentifier(entity));
    }

    @Override
    public void refresh(T entity) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{}", entity);
        }

        getEntityManager().refresh(entity);
    }
}
