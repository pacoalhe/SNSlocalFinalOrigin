package mx.ift.sns.negocio;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.utils.Version;
import mx.ift.sns.utils.WeblogicNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Negocio del SNS.
 */
@Singleton
@Startup
public class SNS {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SNS.class);

    /** Gestor de entidades. */
    @PersistenceContext(unitName = "sns")
    private EntityManager entityManager;

    /** Servicio de bitacora. */
    @EJB
    private IBitacoraService bitacoraService;

    /**
     * Inicializamos el negocio.
     */
    @PostConstruct
    public void init() {
        final String v = " start " + Version.getModulo() + " " + Version.getVersion();

        //BaseDAO.setEntityManager(entityManager);

        String nodo = WeblogicNode.getName();

        if (nodo != null) {
            bitacoraService.add(nodo + v);
            LOGGER.debug(nodo + v);
        } else {
            bitacoraService.add(v);
            LOGGER.debug(v);
        }
    }

    /**
     * Fin del negocio.
     */
    @PreDestroy
    public void end() {

        String nodo = WeblogicNode.getName();

        if (nodo != null) {
            bitacoraService.add(nodo + " stop");
            LOGGER.debug(nodo + " stop");
        } else {
            bitacoraService.add("stop");
            LOGGER.debug("stop");
        }
    }
}
