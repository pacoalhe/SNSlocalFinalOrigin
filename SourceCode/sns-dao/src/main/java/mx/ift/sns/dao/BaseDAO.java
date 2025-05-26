package mx.ift.sns.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceUnitUtil;

import mx.ift.sns.negocio.exceptions.RegistroModificadoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BaseDAO<T> implements IBaseDAO<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAO.class);

    @PersistenceContext(unitName = "sns") // ← Esto le dice a WebLogic que gestione el EntityManager
    private EntityManager em;

    private PersistenceUnitUtil util;

    public EntityManager getEntityManager() {
        if (util == null && em != null) {
            util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            LOGGER.debug("Inicializado util: {}", util);
        }
        return em;
    }

    @Override
    public T create(T entity) {
        LOGGER.debug("{}", entity);
        getEntityManager().persist(entity);
        getEntityManager().flush();
        return entity;
    }

    @Override
    public T update(T entity) {
        LOGGER.debug("{}", entity);
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
        LOGGER.debug("{}", entity);
        Object entityPk = util.getIdentifier(entity);
        T pAttachedEntity = (T) getEntityManager().find(entity.getClass(), entityPk);
        getEntityManager().remove(pAttachedEntity);
    }

    @Override
    public T saveOrUpdate(T entity) {
        Object id = util.getIdentifier(entity);
        return id == null ? create(entity) : update(entity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T reload(T entity) {
        LOGGER.debug("{}", entity);
        return (T) getEntityManager().find(entity.getClass(), util.getIdentifier(entity));
    }

    @Override
    public void refresh(T entity) {
        LOGGER.debug("{}", entity);
        getEntityManager().refresh(entity);
    }

    @Override
    public List<T> saveAll(List<T> entities) {
        List<T> savedEntities = new ArrayList<>();
        for (T entity : entities) {
            savedEntities.add(saveOrUpdate(entity));
        }
        return savedEntities;
    }

    @Override
    public List<T> deleteAll(List<T> entities) {
        List<T> deletedEntities = new ArrayList<>();
        for (T entity : entities) {
            deletedEntities.add(entity);
            delete(entity);
        }
        return deletedEntities;
    }
}
