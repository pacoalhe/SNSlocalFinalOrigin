package mx.ift.sns.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceUnitUtil;

import mx.ift.sns.modelo.oficios.OficioBlob;
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

    /**
     * FJAH 06.09.2025 Refactorizacion y Logger metodos saveorUpdate, create & Update
     * @param entity - entity object to be inserted or updated
     * @return
     */
    @Override
    public T saveOrUpdate(T entity) {
        LOGGER.info("Entrando a saveOrUpdate. entity={}", entity);
        if (entity == null) {
            LOGGER.error("Intentando guardar entidad null en saveOrUpdateSafe!");
            throw new IllegalArgumentException("Entidad nula");
        }
        Object id = getSafeIdentifier(entity);
        if (id == null) {
            LOGGER.info("ID null en saveOrUpdateSafe. Se creará la entidad.");
            return create(entity);
        } else {
            LOGGER.info("ID presente en saveOrUpdateSafe ({}). Se actualizará la entidad.", id);
            return update(entity);
        }
    }
    @Override
    public T create(T entity) {
        LOGGER.info("[CREATE] Entidad recibida: {}", entity);

        // Validación de entityManager y entidad
        if (getEntityManager() == null) {
            LOGGER.error("[CREATE] entityManager ES NULL");
            throw new IllegalStateException("EntityManager null en create()");
        }
        if (entity == null) {
            LOGGER.error("[CREATE] Entidad null recibida");
            throw new IllegalArgumentException("Entidad null en create()");
        }

        // Si es OficioBlob, log especial de sus campos
        if (entity instanceof OficioBlob) {
            OficioBlob ob = (OficioBlob) entity;
            LOGGER.info("[CREATE] OficioBlob: id={}, documento={} bytes",
                    ob.getId(),
                    ob.getDocumento() == null ? "null" : ob.getDocumento().length
            );
        }

        try {
            getEntityManager().persist(entity);
            LOGGER.info("[CREATE] Persistencia exitosa");
            getEntityManager().flush();
            LOGGER.info("[CREATE] Flush exitoso");

            if (entity instanceof OficioBlob) {
                OficioBlob ob = (OficioBlob) entity;
                LOGGER.info("[CREATE] OficioBlob guardado: id_sol_oficio_doc={}, documento={} bytes",
                        ob.getId(),
                        ob.getDocumento() == null ? "null" : ob.getDocumento().length
                );
            } else {
                LOGGER.info("[CREATE] Entidad guardada: {}", entity);
            }

        } catch (Exception e) {
            LOGGER.error("[CREATE] Error al persistir entidad: {}", e.getMessage(), e);
            throw e;
        }
        return entity;
    }

    @Override
    public T update(T entity) {
        LOGGER.info("[UPDATE] Entidad recibida: {}", entity);

        if (getEntityManager() == null) {
            LOGGER.error("[UPDATE] entityManager ES NULL");
            throw new IllegalStateException("EntityManager null en update()");
        }
        if (entity == null) {
            LOGGER.error("[UPDATE] Entidad null recibida");
            throw new IllegalArgumentException("Entidad null en update()");
        }

        if (entity instanceof OficioBlob) {
            OficioBlob ob = (OficioBlob) entity;
            LOGGER.info("[UPDATE] OficioBlob: id={}, documento={} bytes",
                    ob.getId(),
                    ob.getDocumento() == null ? "null" : ob.getDocumento().length
            );
        }

        try {
            entity = getEntityManager().merge(entity);
            LOGGER.info("[UPDATE] Merge exitoso");
            getEntityManager().flush();
            LOGGER.info("[UPDATE] Flush exitoso");

            if (entity instanceof OficioBlob) {
                OficioBlob ob = (OficioBlob) entity;
                LOGGER.info("[UPDATE] OficioBlob actualizado: id_sol_oficio_doc={}, documento={} bytes",
                        ob.getId(),
                        ob.getDocumento() == null ? "null" : ob.getDocumento().length
                );
            } else {
                LOGGER.info("[UPDATE] Entidad actualizada: {}", entity);
            }

        } catch (OptimisticLockException e) {
            LOGGER.error("[UPDATE] RegistroModificadoException por lock optimista: {}", e.getMessage());
            throw new RegistroModificadoException();
        } catch (Exception e) {
            LOGGER.error("[UPDATE] Error al actualizar entidad: {}", e.getMessage(), e);
            throw e;
        }
        return entity;
    }


    /*
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

     */

    /*
    @Override
    public T saveOrUpdate(T entity) {
        LOGGER.info("Entrando a saveOrUpdate. entity={}", entity);
        if (entity == null) {
            LOGGER.error("Intentando guardar entidad null en BaseDAO!");
            throw new IllegalArgumentException("Entidad nula");
        }
        Object id = util.getIdentifier(entity);
        return id == null ? create(entity) : update(entity);
    }

     */

    @SuppressWarnings("unchecked")
    @Override
    public void delete(T entity) {
        LOGGER.debug("{}", entity);
        Object entityPk = util.getIdentifier(entity);
        T pAttachedEntity = (T) getEntityManager().find(entity.getClass(), entityPk);
        getEntityManager().remove(pAttachedEntity);
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

    /**
     * FJAH 09.06.2025 Refactorización modo seguro oficioblob creacion metodo getSafeIdentifier
     * @param entity - entity object to be inserted or updated
     * @return
     */
    protected Object getSafeIdentifier(T entity) {
        try {
            if (util == null) {
                getEntityManager(); // Forzar inicialización del util
            }
            return util.getIdentifier(entity);
        } catch (Exception e) {
            LOGGER.error("[getSafeIdentifier] Error al obtener el identificador de la entidad {}: {}",
                    entity != null ? entity.getClass().getName() : "null", e.getMessage(), e);
            return null; // El flujo sigue como "entidad nueva"
        }
    }

}
